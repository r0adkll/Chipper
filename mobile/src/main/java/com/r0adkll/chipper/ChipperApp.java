package com.r0adkll.chipper;

import android.app.Application;
import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
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
public class ChipperApp extends Application{
    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize ActiveAndroid
        Configuration.Builder builder = new Configuration.Builder(this)
                .addModelClasses(User.class, Device.class, Chiptune.class, Playlist.class);
        ActiveAndroid.initialize(builder.create());

        // Plant Timber Trees
        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }else{
            Timber.plant(new CrashlyticsTree());
            Timber.plant(new FileTree(this, getLogFilename()));
        }

        // Setup PostOffice stamp
        Stamp stamp = new Stamp.Builder(this)
                .setDesign(Design.MATERIAL_LIGHT)
                .setThemeColorResource(R.color.primary)
                .build();

        // Lick the stamp and apply it
        PostOffice.lick(stamp);

        // Setup the object graph
        buildObjectGraphAndInject();
    }

    @DebugLog
    public void buildObjectGraphAndInject(){
        objectGraph = ObjectGraph.create(Modules.list(this));
        objectGraph.inject(this);
    }

    /**
     * Create a scoped object graph
     *
     * @param modules       the list of modules to add to the scope
     * @return              the scoped graph
     */
    public ObjectGraph createScopedGraph(Object... modules){
        return objectGraph.plus(modules);
    }

    /**
     * Get the file log name
     * @return
     */
    private String getLogFilename(){
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
        return String.format("chipper_%s_%s.log", sdf.format(new Date()), BuildConfig.VERSION_NAME);
    }

    /**
     * Inject an object with the object graph
     */
    public void inject(Object o){
        objectGraph.inject(o);
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