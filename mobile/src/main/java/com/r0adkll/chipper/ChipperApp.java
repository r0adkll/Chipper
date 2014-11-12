package com.r0adkll.chipper;

import android.app.Application;
import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.r0adkll.chipper.core.utils.CrashlyticsTree;
import com.r0adkll.chipper.core.utils.FileTree;

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
        ActiveAndroid.initialize(this);

        // Plant Timber Trees
        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }else{
            Timber.plant(new CrashlyticsTree());
            Timber.plant(new FileTree(this, getLogFilename()));
        }

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
