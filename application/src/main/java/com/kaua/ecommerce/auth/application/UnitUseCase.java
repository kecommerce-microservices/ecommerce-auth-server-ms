package com.kaua.ecommerce.auth.application;

public abstract class UnitUseCase<I> {

    public abstract void execute(I input);
}
