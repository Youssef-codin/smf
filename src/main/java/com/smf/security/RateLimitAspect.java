package com.smf.security;

import com.smf.util.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class RateLimitAspect {

  private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

  @Around("@annotation(rateLimit)")
  public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
    String key = resolveKey(rateLimit.keyType(), joinPoint);
    Bucket bucket = buckets.computeIfAbsent(key, k -> createBucket(rateLimit));

    if (!bucket.tryConsume(1)) {
      throw new RateLimitExceededException("Rate limit exceeded. Try again later.");
    }

    return joinPoint.proceed();
  }

  private String resolveKey(RateLimitKeyType keyType, ProceedingJoinPoint joinPoint) {
    String prefix = joinPoint.getSignature().toShortString();
    String suffix =
        switch (keyType) {
          case IP -> getClientIp();
          case USER -> getUserIdentifier();
          case DEVICE -> getDeviceIdentifier();
          case GLOBAL -> "global";
        };
    return prefix + ":" + suffix;
  }

  private String getClientIp() {
    ServletRequestAttributes attrs =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attrs == null) {
      return "unknown";
    }
    HttpServletRequest request = attrs.getRequest();
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader != null && !xfHeader.isBlank()) {
      return xfHeader.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }

  private String getUserIdentifier() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
      return auth.getName();
    }
    return "anonymous";
  }

  private String getDeviceIdentifier() {
    ServletRequestAttributes attrs =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attrs == null) {
      return "unknown-device";
    }
    String mac = attrs.getRequest().getHeader("X-Device-Mac");
    if (mac != null && !mac.isBlank()) {
      return mac;
    }
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() != null) {
      return auth.getPrincipal().toString();
    }
    return "unknown-device";
  }

  private Bucket createBucket(RateLimit rateLimit) {
    Bandwidth bandwidth =
        Bandwidth.classic(
            rateLimit.limit(),
            Refill.intervally(rateLimit.limit(), Duration.ofSeconds(rateLimit.duration())));
    return Bucket.builder().addLimit(bandwidth).build();
  }
}

