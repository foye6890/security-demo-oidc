package com.security.demo.security.user;

public record AppUserInfo(
        String userId,
        String loginId,
        String displayName,
        String email
) {
}
