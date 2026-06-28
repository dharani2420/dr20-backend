package com.dr20.common.security;

import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthHelper {

    private AuthHelper() {}

    public static String currentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
