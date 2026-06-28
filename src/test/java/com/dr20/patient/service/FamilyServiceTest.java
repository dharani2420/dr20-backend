package com.dr20.patient.service;

import com.dr20.shared.model.FamilyMember;
import com.dr20.shared.model.User;
import com.dr20.shared.repository.FamilyMemberRepository;
import com.dr20.shared.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FamilyServiceTest {

    @Mock private FamilyMemberRepository familyMemberRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private FamilyService familyService;

    @Test
    void addFamilyMember_linksToUser() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(new User()));
        when(familyMemberRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        FamilyMember member = new FamilyMember();
        member.setName("Jane");
        FamilyMember saved = familyService.add("u1", member);

        assertEquals("u1", saved.getUserId());
    }
}
