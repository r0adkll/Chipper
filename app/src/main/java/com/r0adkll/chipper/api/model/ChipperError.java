package com.r0adkll.chipper.api.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.api.model
 * Created by drew.heavner on 11/12/14.
 */
@JsonObject
public class ChipperError {

    @JsonField
    public String technical;

    @JsonField
    public String readable;
    
}
