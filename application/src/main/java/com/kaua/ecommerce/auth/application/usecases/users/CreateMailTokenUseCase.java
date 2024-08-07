package com.kaua.ecommerce.auth.application.usecases.users;

import com.kaua.ecommerce.auth.application.UseCase;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.CreateMailTokenInput;
import com.kaua.ecommerce.auth.application.usecases.users.outputs.CreateMailTokenOutput;

public abstract class CreateMailTokenUseCase extends
        UseCase<CreateMailTokenInput, CreateMailTokenOutput> {
}
