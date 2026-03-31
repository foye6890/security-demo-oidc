package com.security.demo.security.oidc;

import java.util.Collection;
import com.security.demo.security.permission.AppExecutionPermissionSet;
import com.security.demo.security.scope.AppReferenceScopeSet;
import com.security.demo.security.user.AppUserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

public class AppOidcUser extends DefaultOidcUser {

    private final String userId;
    private final AppUserInfo userInfo;
    private final AppExecutionPermissionSet executionPermissionSet;
    private final AppReferenceScopeSet referenceScopeSet;

    public AppOidcUser(
            Collection<? extends GrantedAuthority> authorities,
            OidcIdToken idToken,
            OidcUserInfo userInfo,
            String nameAttributeKey,
            String userId,
            AppUserInfo appUserInfo,
            AppExecutionPermissionSet executionPermissionSet,
            AppReferenceScopeSet referenceScopeSet
    ) {
        super(authorities, idToken, userInfo, nameAttributeKey);
        this.userId = userId;
        this.userInfo = appUserInfo;
        this.executionPermissionSet = executionPermissionSet;
        this.referenceScopeSet = referenceScopeSet;
    }

    public String getUserId() {
        return userId;
    }

    public AppUserInfo getUserInfo() {
        return userInfo;
    }

    public AppExecutionPermissionSet getExecutionPermissionSet() {
        return executionPermissionSet;
    }

    public AppReferenceScopeSet getReferenceScopeSet() {
        return referenceScopeSet;
    }
}
