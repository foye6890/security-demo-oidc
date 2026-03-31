package com.security.demo.security.oidc;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.security.demo.security.permission.AppExecutionPermission;
import com.security.demo.security.permission.AppExecutionPermissionSet;
import com.security.demo.security.scope.AppReferenceScopeSet;
import com.security.demo.security.user.AppUserLookupService;
import com.security.demo.security.user.AppUserInfo;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class AppOidcUserService extends OidcUserService {

    private final ObjectProvider<AppUserLookupService> appUserLookupServiceProvider;
    private final String userIdClaim;

    public AppOidcUserService(
            ObjectProvider<AppUserLookupService> appUserLookupServiceProvider,
            @Value("${app.security.oauth2.user-id-claim:preferred_username}") String userIdClaim
    ) {
        this.appUserLookupServiceProvider = appUserLookupServiceProvider;
        this.userIdClaim = userIdClaim;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);
        String userId = resolveUserId(userRequest, oidcUser);
        AppUserLookupService appUserLookupService = appUserLookupServiceProvider.getIfAvailable();
        AppUserInfo appUserInfo = findUserInfo(appUserLookupService, userId).orElse(null);
        AppExecutionPermissionSet executionPermissionSet = findExecutionPermissions(appUserLookupService, userId)
                .orElseGet(() -> new AppExecutionPermissionSet(List.of()));
        AppReferenceScopeSet referenceScopeSet = findReferenceScopes(appUserLookupService, userId)
                .orElseGet(() -> new AppReferenceScopeSet(List.of()));

        Set<GrantedAuthority> authorities = new LinkedHashSet<>(oidcUser.getAuthorities());
        executionPermissionSet.permissions().stream()
                    .map(AppExecutionPermission::code)
                    .map(SimpleGrantedAuthority::new)
                    .forEach(authorities::add);

        return new AppOidcUser(
                authorities,
                oidcUser.getIdToken(),
                oidcUser.getUserInfo(),
                resolveNameAttributeKey(userRequest),
                userId,
                appUserInfo,
                executionPermissionSet,
                referenceScopeSet
        );
    }

    private String resolveUserId(OidcUserRequest userRequest, OidcUser oidcUser) {
        String configuredClaim = Optional.ofNullable(oidcUser.getClaimAsString(userIdClaim)).orElse(null);
        if (configuredClaim != null && !configuredClaim.isBlank()) {
            return configuredClaim;
        }

        String configuredUserNameAttribute = resolveNameAttributeKey(userRequest);
        if (configuredUserNameAttribute != null) {
            String claimValue = oidcUser.getClaimAsString(configuredUserNameAttribute);
            if (claimValue != null && !claimValue.isBlank()) {
                return claimValue;
            }
        }

        return Optional.ofNullable(oidcUser.getClaimAsString(userIdClaim))
                .or(() -> Optional.ofNullable(oidcUser.getPreferredUsername()))
                .or(() -> Optional.ofNullable(oidcUser.getEmail()))
                .or(() -> Optional.ofNullable(oidcUser.getSubject()))
                .orElseThrow(() -> new IllegalStateException(
                        "OIDC user identifier could not be resolved from the OIDC request and claims"));
    }

    private Optional<AppUserInfo> findUserInfo(AppUserLookupService appUserLookupService, String userId) {
        if (appUserLookupService == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(appUserLookupService.findUserInfoByUserId(userId));
    }

    private Optional<AppExecutionPermissionSet> findExecutionPermissions(
            AppUserLookupService appUserLookupService,
            String userId
    ) {
        if (appUserLookupService == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(appUserLookupService.findExecutionPermissionsByUserId(userId));
    }

    private Optional<AppReferenceScopeSet> findReferenceScopes(
            AppUserLookupService appUserLookupService,
            String userId
    ) {
        if (appUserLookupService == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(appUserLookupService.findReferenceScopesByUserId(userId));
    }

    private String resolveNameAttributeKey(OidcUserRequest userRequest) {
        return userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
    }
}
