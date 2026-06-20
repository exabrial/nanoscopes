package com.github.exabrial.cdi.nanoscoped.boundaryscoped;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.exabrial.cdi.microspike.AnnotationInstanceProvider;

import jakarta.interceptor.InterceptorBinding;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@InterceptorBinding
@Inherited
public @interface Boundary {
	Boundary INSTANCE = AnnotationInstanceProvider.of(Boundary.class);
}
