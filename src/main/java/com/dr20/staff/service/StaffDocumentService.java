package com.dr20.staff.service;

import com.dr20.common.exception.ResourceNotFoundException;
import com.dr20.shared.model.StaffDocument;
import com.dr20.shared.repository.StaffDocumentRepository;
import com.dr20.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffDocumentService {

    private final StaffDocumentRepository documentRepository;
    private final UserRepository userRepository;

    public List<StaffDocument> getDocuments(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return documentRepository.findByUserId(userId);
    }
}
