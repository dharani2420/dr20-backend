package com.dr20.patient.service;

import com.dr20.common.exception.ResourceNotFoundException;
import com.dr20.shared.model.FamilyMember;
import com.dr20.shared.repository.FamilyMemberRepository;
import com.dr20.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FamilyService {

    private final FamilyMemberRepository familyMemberRepository;
    private final UserRepository userRepository;

    public List<FamilyMember> getByUser(String userId) {
        validateUser(userId);
        return familyMemberRepository.findByUserId(userId);
    }

    public FamilyMember add(String userId, FamilyMember member) {
        validateUser(userId);
        member.setUserId(userId);
        return familyMemberRepository.save(member);
    }

    public FamilyMember update(String memberId, FamilyMember updated) {
        FamilyMember member = familyMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Family member not found"));
        if (updated.getName() != null) member.setName(updated.getName());
        if (updated.getRelation() != null) member.setRelation(updated.getRelation());
        if (updated.getAge() != null) member.setAge(updated.getAge());
        if (updated.getGender() != null) member.setGender(updated.getGender());
        if (updated.getBloodGroup() != null) member.setBloodGroup(updated.getBloodGroup());
        return familyMemberRepository.save(member);
    }

    public void remove(String memberId) {
        if (!familyMemberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Family member not found");
        }
        familyMemberRepository.deleteById(memberId);
    }

    private void validateUser(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
