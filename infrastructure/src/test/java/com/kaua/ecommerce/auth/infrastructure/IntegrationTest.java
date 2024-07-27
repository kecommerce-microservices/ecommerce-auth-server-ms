package com.kaua.ecommerce.auth.infrastructure;

import com.kaua.ecommerce.auth.config.JpaCleanUpExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ActiveProfiles("test-integration")
@SpringBootTest(classes = {Main.class})
@ExtendWith(JpaCleanUpExtension.class)
public @interface IntegrationTest {
}
