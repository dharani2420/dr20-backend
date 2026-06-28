package com.dr20.common.security;

import com.dr20.common.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        jwtUtil = new JwtUtil("dr20-test-secret-key-minimum-256-bits-long-enough!!", 24);
    }

    @Test
    void generateAndParseToken() {
        String token = jwtUtil.generateToken("u1", "9876543210", UserRole.PATIENT, null);
        assertEquals("u1", jwtUtil.getUserId(token));
        assertEquals(UserRole.PATIENT, jwtUtil.getRole(token));
    }

    @Test
    void staffTokenIncludesLinkedProfile() {
        String token = jwtUtil.generateToken("s1", "9123456781", UserRole.DOCTOR, "d1");
        assertEquals("d1", jwtUtil.getLinkedProfileId(token));
    }
}
