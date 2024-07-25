package com.kaua.ecommerce.auth.infrastructure;

import org.junit.jupiter.api.Tag;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Tag("unitTest")
public @interface UnitTest {
}
