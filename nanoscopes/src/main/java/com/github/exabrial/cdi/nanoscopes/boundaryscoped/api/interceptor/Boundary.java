package com.github.exabrial.cdi.nanoscopes.boundaryscoped.api.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.interceptor.InterceptorBinding;

import com.github.exabrial.cdi.microspike.AnnotationInstanceProvider;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@InterceptorBinding
@Inherited
public @interface Boundary {
	Boundary INSTANCE = AnnotationInstanceProvider.of(Boundary.class);
}
