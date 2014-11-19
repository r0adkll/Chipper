package com.r0adkll.chipper.qualifiers;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.core.qualifiers
 * Created by drew.heavner on 11/18/14.
 */
@Qualifier
@Retention(RUNTIME)
public @interface PushToken {}
