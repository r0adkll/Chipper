package com.r0adkll.chipper.qualifiers;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Default Preference qualifier
 */
@Qualifier
@Retention(RUNTIME)
public @interface DefaultPrefs {}