package com.github.exabrial.cdi.nanoscopes.messagescoped;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.exabrial.cdi.microspike.AnnotationInstanceProvider;

import jakarta.inject.Qualifier;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Qualifier
public @interface MessageBound {
	MessageBound INSTANCE = AnnotationInstanceProvider.of(MessageBound.class);
}
