package com.r0adkll.chipper.core.qualifiers;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Generic preference qualifiers
 */
@Qualifier
@Retention(RUNTIME)
public @interface GenericPrefs {}
