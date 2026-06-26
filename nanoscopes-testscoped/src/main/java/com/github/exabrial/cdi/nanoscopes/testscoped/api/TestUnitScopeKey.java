package com.github.exabrial.cdi.nanoscopes.testscoped.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.inject.Qualifier;

import com.github.exabrial.cdi.microspike.AnnotationInstanceProvider;

@Qualifier
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE })
public @interface TestUnitScopeKey {
	TestUnitScopeKey LITERAL = AnnotationInstanceProvider.of(TestUnitScopeKey.class);
}
