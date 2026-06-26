package com.github.exabrial.cdi.nanoscopes.testscoped.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.enterprise.context.NormalScope;

import com.github.exabrial.cdi.microspike.AnnotationInstanceProvider;

@NormalScope(passivating = false)
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE })
public @interface TestUnitScoped {
	TestUnitScoped LITERAL = AnnotationInstanceProvider.of(TestUnitScoped.class);
}
