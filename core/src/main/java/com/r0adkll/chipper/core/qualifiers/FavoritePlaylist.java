package com.r0adkll.chipper.core.qualifiers;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by r0adkll on 11/12/14.
 */
@Qualifier
@Retention(RUNTIME)
public @interface FavoritePlaylist {}
