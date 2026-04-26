package com.smf.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
  int limit();

  long duration() default 60;

  RateLimitKeyType keyType() default RateLimitKeyType.IP;
}

