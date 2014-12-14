package com.r0adkll.chipper;

import android.app.Application;
import android.app.Fragment;
import android.app.Service;
import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.crashlytics.android.Crashlytics;
import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.ChiptuneReference;
import com.r0adkll.chipper.api.model.Device;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.api.model.Vote;
import com.r0adkll.chipper.data.Historian;
import com.r0adkll.chipper.utils.CrashlyticsTree;
import com.r0adkll.chipper.utils.FileTree;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.model.Design;
import com.r0adkll.postoffice.model.Stamp;

import io.fabric.sdk.android.Fabric;
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
        Fabric.with(this, new Crashlytics());

        // Initialize ActiveAndroid
        Configuration.Builder builder = new Configuration.Builder(this)
                .addModelClasses(
                        User.class,
                        Device.class,
                        Chiptune.class,
                        Playlist.class,
                        ChiptuneReference.class,
                        Vote.class,
                        Historian.Chronicle.class
                );

        // Initialize the Database ORM
        ActiveAndroid.initialize(builder.create());

        // Plant Timber Trees
        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }else{
            Timber.plant(new CrashlyticsTree());
            Timber.plant(new FileTree(this, getLogFilename()));
        }

        // Setup PostOffice stamp
        PostOffice.lick(new Stamp.Builder(this)
                .setDesign(Design.MATERIAL_LIGHT)
                .setThemeColorResource(R.color.primary)
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .build());

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

    /**
     * Get a reference to this application with a service
     * object
     *
     * @param ctx
     * @return
     */
    public static ChipperApp get(Service ctx){
        return (ChipperApp) ctx.getApplication();
    }

    /**
     * Get a reference to this application with a service
     * object
     *
     * @param ctx
     * @return
     */
    public static ChipperApp get(Fragment ctx){
        return get(ctx.getActivity());
    }

    /**
     * Get a reference to this application with a service
     * object
     *
     * @param ctx
     * @return
     */
    public static ChipperApp get(android.support.v4.app.Fragment ctx){
        return get(ctx.getActivity());
    }





}