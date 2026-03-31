package com.security.demo.security.permission;

import java.util.List;

public record AppExecutionPermissionSet(List<AppExecutionPermission> permissions) {
}
