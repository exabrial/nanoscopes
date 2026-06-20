package com.github.exabrial.cdi.nanoscopes.messagescoped;

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
public @interface MessageBoundary {
	MessageBoundary INSTANCE = AnnotationInstanceProvider.of(MessageBoundary.class);
}
