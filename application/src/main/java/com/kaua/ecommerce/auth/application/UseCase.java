package com.kaua.ecommerce.auth.application;

public abstract class UseCase<I, O> {

    public abstract O execute(I input);
}
