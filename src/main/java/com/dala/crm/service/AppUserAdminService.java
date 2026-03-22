package com.dala.crm.service;

import com.dala.crm.dto.AppUserCreateRequest;
import com.dala.crm.dto.AppUserPasswordResetRequest;
import com.dala.crm.dto.AppUserResponse;
import com.dala.crm.dto.AppUserUpdateRequest;
import java.util.List;

/**
 * Tenant-scoped user administration contract.
 */
public interface AppUserAdminService {

    List<AppUserResponse> list();

    AppUserResponse create(AppUserCreateRequest request);

    AppUserResponse update(Long id, AppUserUpdateRequest request);

    AppUserResponse resetPassword(Long id, AppUserPasswordResetRequest request);
}
