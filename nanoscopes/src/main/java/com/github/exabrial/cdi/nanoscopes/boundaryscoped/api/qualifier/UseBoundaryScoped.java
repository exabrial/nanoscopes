package com.github.exabrial.cdi.nanoscopes.boundaryscoped.api.qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.inject.Qualifier;

import com.github.exabrial.cdi.microspike.AnnotationInstanceProvider;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Qualifier
public @interface UseBoundaryScoped {
	UseBoundaryScoped INSTANCE = AnnotationInstanceProvider.of(UseBoundaryScoped.class);
}
