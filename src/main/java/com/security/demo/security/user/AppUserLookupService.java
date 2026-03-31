package com.security.demo.security.user;

import com.security.demo.security.permission.AppExecutionPermissionSet;
import com.security.demo.security.scope.AppReferenceScopeSet;

public interface AppUserLookupService {

    AppUserInfo findUserInfoByUserId(String userId);

    AppExecutionPermissionSet findExecutionPermissionsByUserId(String userId);

    AppReferenceScopeSet findReferenceScopesByUserId(String userId);
}
