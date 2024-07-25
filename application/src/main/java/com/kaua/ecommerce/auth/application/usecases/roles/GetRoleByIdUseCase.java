package com.kaua.ecommerce.auth.application.usecases.roles;

import com.kaua.ecommerce.auth.application.UseCase;
import com.kaua.ecommerce.auth.application.usecases.roles.outputs.GetRoleByIdOutput;
import com.kaua.ecommerce.auth.domain.roles.RoleId;

public abstract class GetRoleByIdUseCase extends UseCase<RoleId, GetRoleByIdOutput> {
}
