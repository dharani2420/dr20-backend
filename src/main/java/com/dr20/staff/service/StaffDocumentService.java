package com.dr20.staff.service;

import com.dr20.common.exception.BadRequestException;
import com.dr20.common.exception.ResourceNotFoundException;
import com.dr20.shared.model.StaffBankDetails;
import com.dr20.shared.model.StaffDocument;
import com.dr20.shared.model.User;
import com.dr20.shared.repository.StaffBankDetailsRepository;
import com.dr20.shared.repository.StaffDocumentRepository;
import com.dr20.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffDocumentService {

    private static final DateTimeFormatter UPLOAD_DATE_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    private static final String PAYOUT_MESSAGE =
            "Your earnings are automatically transferred to your registered bank account every Tuesday.";

    private final StaffDocumentRepository documentRepository;
    private final StaffBankDetailsRepository bankDetailsRepository;
    private final UserRepository userRepository;

    public Map<String, Object> getDocumentsScreen(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<StaffDocument> docs = documentRepository.findByUserId(userId).stream()
                .filter(d -> !"BANK".equalsIgnoreCase(d.getType()))
                .collect(Collectors.toList());

        boolean allVerified = docs.stream().allMatch(d -> "VERIFIED".equalsIgnoreCase(d.getStatus()));
        String overallStatus = allVerified && "APPROVED".equalsIgnoreCase(user.getVerificationStatus())
                ? "VERIFIED" : "PENDING";

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("overallStatus", overallStatus);
        res.put("overallMessage", "VERIFIED".equals(overallStatus)
                ? "All your documents are verified"
                : "Document verification in progress");
        res.put("documents", docs.stream().map(this::toDocumentCard).collect(Collectors.toList()));
        res.put("bankDetails", bankDetailsRepository.findByUserId(userId).map(this::toBankCard).orElse(null));
        res.put("automaticPayout", Map.of(
                "title", "Automatic Payout",
                "message", PAYOUT_MESSAGE
        ));
        return res;
    }

    public List<StaffDocument> getDocuments(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return documentRepository.findByUserId(userId);
    }

    public StaffDocument uploadDocument(String userId, Map<String, String> body) {
        String type = body.get("type");
        String fileUrl = body.get("fileUrl");
        if (type == null || fileUrl == null || fileUrl.isBlank()) {
            throw new BadRequestException("type and fileUrl are required");
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        StaffDocument doc = new StaffDocument();
        doc.setUserId(userId);
        doc.setType(type.toUpperCase());
        doc.setTitle(body.getOrDefault("title", defaultTitle(type)));
        doc.setFileUrl(fileUrl);
        doc.setStatus("PENDING");
        return documentRepository.save(doc);
    }

    public StaffBankDetails saveBankDetails(String userId, Map<String, String> body) {
        String bankName = body.get("bankName");
        String accountNumber = body.get("accountNumber");
        if (bankName == null || accountNumber == null) {
            throw new BadRequestException("bankName and accountNumber are required");
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        StaffBankDetails bank = bankDetailsRepository.findByUserId(userId).orElse(new StaffBankDetails());
        bank.setUserId(userId);
        bank.setBankName(bankName);
        bank.setAccountNumber(accountNumber);
        bank.setMaskedAccountNumber(maskAccountNumber(accountNumber));
        bank.setIfscCode(body.get("ifscCode"));
        bank.setDocumentUrl(body.get("documentUrl"));
        bank.setStatus("PENDING");
        return bankDetailsRepository.save(bank);
    }

    private Map<String, Object> toDocumentCard(StaffDocument doc) {
        Map<String, Object> card = new LinkedHashMap<>();
        card.put("id", doc.getId());
        card.put("label", documentLabel(doc.getType()));
        card.put("documentType", doc.getTitle());
        card.put("type", doc.getType());
        card.put("uploadedOn", doc.getUploadedAt() != null
                ? doc.getUploadedAt().format(UPLOAD_DATE_FMT) : null);
        card.put("status", doc.getStatus());
        card.put("fileUrl", doc.getFileUrl());
        card.put("imageUrl", doc.getFileUrl());
        return card;
    }

    private Map<String, Object> toBankCard(StaffBankDetails bank) {
        Map<String, Object> card = new LinkedHashMap<>();
        card.put("bankName", bank.getBankName());
        card.put("maskedAccountNumber", bank.getMaskedAccountNumber());
        card.put("status", bank.getStatus());
        card.put("fileUrl", bank.getDocumentUrl());
        card.put("imageUrl", bank.getDocumentUrl());
        return card;
    }

    private String documentLabel(String type) {
        return switch (type != null ? type.toUpperCase() : "") {
            case "IDENTITY" -> "Identity Proof";
            case "PROFESSIONAL_CERT" -> "Professional Certificate";
            case "PROFILE_PHOTO" -> "Profile Photo";
            case "BANK" -> "Bank Details";
            default -> "Document";
        };
    }

    private String defaultTitle(String type) {
        return switch (type.toUpperCase()) {
            case "IDENTITY" -> "Identity Proof";
            case "PROFESSIONAL_CERT" -> "Professional Certificate";
            case "PROFILE_PHOTO" -> "Profile Photo";
            case "BANK" -> "Bank Document";
            default -> "Document";
        };
    }

    static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) return accountNumber;
        String last4 = accountNumber.substring(accountNumber.length() - 3);
        return "XXXX XXXX " + last4;
    }
}
