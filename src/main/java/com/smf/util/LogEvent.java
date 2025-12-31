package com.smf.util;

import com.smf.model.enums.EventTypes;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogEvent {
  EventTypes eventType();
}
