package com.r0adkll.chipper;

import android.app.Application;
import android.app.Fragment;
import android.app.Service;
import android.content.Context;

import com.ftinc.kit.mvp.BaseApplication;
import com.r0adkll.chipper.AppComponent.Initializer;
import com.r0adkll.chipper.utils.CrashlyticsTree;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.model.Design;
import com.r0adkll.postoffice.model.Stamp;

import hugo.weaving.DebugLog;
import timber.log.Timber;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper
 * Created by drew.heavner on 11/12/14.
 */
public class ChipperApp extends BaseApplication{

    /***********************************************************************************************
     *
     * Variables
     *
     */

    private AppGraph mComponent;

    /***********************************************************************************************
     *
     * Application Methods
     *
     */


    @Override
    public void onCreate() {
        super.onCreate();

        // Build Dagger2 graph
        setupGraph();

        // Setup PostOffice stamp
        PostOffice.lick(new Stamp.Builder(this)
                .setDesign(Design.MATERIAL_LIGHT)
                .setThemeColorResource(R.color.primary)
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .build());

    }

    /***********************************************************************************************
     *
     * Dagger Methods
     *
     */

    /**
     * Setup the Dagger object graph component
     */
    @DebugLog
    private void setupGraph(){
        mComponent = Initializer.init(this);
        mComponent.inject(this);
    }

    /**
     * Get the Application object graph component
     *
     * @return      the application graph
     */
    public AppGraph component(){
        return mComponent;
    }

    /***********************************************************************************************
     *
     * Base Methods
     *
     */

    @Override
    public Timber.Tree[] getDebugTrees() {
        return new Timber.Tree[]{
            new Timber.DebugTree()
        };
    }

    @Override
    public Timber.Tree[] getReleaseTrees() {
        return new Timber.Tree[]{
            new CrashlyticsTree(this)
        };
    }

    @Override
    public Boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    /***********************************************************************************************
     *
     * Static Accessors
     *
     */


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