package com.r0adkll.chipper;

import android.app.Application;
import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.r0adkll.chipper.core.CoreApplication;
import com.r0adkll.chipper.core.api.model.Chiptune;
import com.r0adkll.chipper.core.api.model.Device;
import com.r0adkll.chipper.core.api.model.Playlist;
import com.r0adkll.chipper.core.api.model.User;
import com.r0adkll.chipper.core.utils.CrashlyticsTree;
import com.r0adkll.chipper.core.utils.FileTree;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.model.Design;
import com.r0adkll.postoffice.model.Stamp;

import java.text.SimpleDateFormat;
import java.util.Date;

import dagger.ObjectGraph;
import hugo.weaving.DebugLog;
import timber.log.Timber;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper
 * Created by drew.heavner on 11/12/14.
 */
public class ChipperApp extends CoreApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // Setup PostOffice stamp
        Stamp stamp = new Stamp.Builder(this)
                .setDesign(Design.MATERIAL_LIGHT)
                .setThemeColorResource(R.color.primary)
                .build();

        // Lick the stamp and apply it
        PostOffice.lick(stamp);
    }

    @Override
    public Object[] getModules(Application application) {
        return Modules.list();
    }

    /**
     * Get the file log name
     * @return
     */
    @Override
    public String getLogFilename(){
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
        return String.format("chipper_%s_%s.log", sdf.format(new Date()), BuildConfig.VERSION_NAME);
    }

    /**
     * Get a reference to the Application
     *
     * @param ctx       the context
     * @return          the ChipperApp reference
     */
    public static ChipperApp get(Context ctx){
        return (ChipperApp) ctx.getApplicationContext();
    }

}
