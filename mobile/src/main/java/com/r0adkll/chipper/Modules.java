package com.r0adkll.chipper;

import com.r0adkll.chipper.core.CoreModule;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper
 * Created by drew.heavner on 11/12/14.
 */
public class Modules {
    static Object[] list(ChipperApp app){
        return new Object[]{
            new ChipperModule(app)
        };
    }

    private Modules(){}
}
