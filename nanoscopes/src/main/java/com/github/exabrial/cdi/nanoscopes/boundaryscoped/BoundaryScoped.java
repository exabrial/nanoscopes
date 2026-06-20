package com.github.exabrial.cdi.nanoscopes.boundaryscoped;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.exabrial.cdi.microspike.AnnotationInstanceProvider;

import jakarta.enterprise.context.NormalScope;

@Documented
@NormalScope(passivating = false)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
public @interface BoundaryScoped {
	BoundaryScoped INSTANCE = AnnotationInstanceProvider.of(BoundaryScoped.class);
}
