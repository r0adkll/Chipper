package com.r0adkll.chipper.core;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
import hugo.weaving.DebugLog;
import timber.log.Timber;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.core
 * Created by drew.heavner on 11/18/14.
 */
public abstract class CoreApplication extends Application {
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

        // Build and inject the graph
        buildObjectGraphAndInject();
    }

    /**
     * Get the Dagger Modules used in the object graph
     * @param application       the application reference
     * @return
     */
    public abstract Object[] getModules(Application application);

    /**
     * Get the filename used for the log file on disk
     * @return
     */
    public abstract String getLogFilename();

    @DebugLog
    public void buildObjectGraphAndInject(){
        Object[] ext_modules = getModules(this);

        List<Object> modules = Arrays.asList(ext_modules);
        modules.add(new CoreModule(this));
        Object[] all_modules = modules.toArray();

        objectGraph = ObjectGraph.create(all_modules);
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
    public static CoreApplication get(Context ctx){
        return (CoreApplication) ctx.getApplicationContext();
    }


}
