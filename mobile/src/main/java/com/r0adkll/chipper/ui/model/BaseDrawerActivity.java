package com.r0adkll.chipper.ui.model;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.r0adkll.chipper.ChipperApp;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.account.GoogleAccountManager;
import com.r0adkll.chipper.data.events.OfflineModeChangeEvent;
import com.r0adkll.chipper.push.PushManager;
import com.r0adkll.chipper.ui.screens.dashboard.DashboardActivity;
import com.r0adkll.chipper.ui.screens.featured.FeaturedActivity;
import com.r0adkll.chipper.ui.screens.player.MusicPlayer;
import com.r0adkll.chipper.utils.prefs.BooleanPreference;
import com.r0adkll.chipper.qualifiers.OfflineSwitchPreference;
import com.r0adkll.chipper.ui.screens.all.ChiptunesActivity;
import com.r0adkll.chipper.ui.screens.playlists.PlaylistActivity;
import com.r0adkll.chipper.ui.screens.popular.PopularActivity;
import com.r0adkll.chipper.ui.settings.SettingsActivity;
import com.r0adkll.chipper.ui.widget.ScrimInsetsScrollView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import dagger.ObjectGraph;

/**
 * This is a base UI activity that assists in creating a scoped
 * object graph on the activity for DI
 *
 * Project: Chipper
 * Package: com.r0adkll.chipper.ui
 * Created by drew.heavner on 11/12/14.
 */
public abstract class BaseDrawerActivity extends ActionBarActivity implements GoogleAccountManager.OnAccountLoadedListener {

    /***********************************************************************************************
     *
     *  Constants
     *
     */

    protected static final int NAVDRAWER_ITEM_DASHBOARD = 0;
    protected static final int NAVDRAWER_ITEM_CHIPTUNES = 1;
    protected static final int NAVDRAWER_ITEM_POPULAR = 2;
    protected static final int NAVDRAWER_ITEM_FEATURED = 3;
    protected static final int NAVDRAWER_ITEM_PLAYLISTS = 4;
    protected static final int NAVDRAWER_ITEM_PARTIES = 5;
    protected static final int NAVDRAWER_ITEM_OFFLINE_MODE = 6;
    protected static final int NAVDRAWER_ITEM_SETTINGS = 7;
    protected static final int NAVDRAWER_ITEM_FEEDBACK = 8;
    protected static final int NAVDRAWER_ITEM_INVALID = -1;

    // delay to launch nav drawer item, to allow close animation to play
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;

    // fade in and fade out durations for the main content when switching between
    // different Activities of the app through the Nav Drawer
    private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
    private static final int MAIN_CONTENT_FADEIN_DURATION = 250;

    /***********************************************************************************************
     *
     *  Variables
     *
     */

    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @InjectView(R.id.sliding_layout)
    SlidingUpPanelLayout mSlidingLayout;

    @Optional
    @InjectView(R.id.navdrawer_items_list)
    ViewGroup mDrawerItemsListContainer;

    @Inject @OfflineSwitchPreference
    BooleanPreference mOfflineSwitchPreference;

    @Inject
    GoogleAccountManager mAccountManager;

    @Inject
    PushManager mPushManager;

    @Inject
    Bus mBus;

    private MusicPlayer mPlayer;

    private final Handler mHandler = new Handler();
    private ObjectGraph activityGraph;

    /* The ActionBar Toolbar used for the drawer */
    private Toolbar mActionBarToolbar;
    private ActionBarDrawerToggle mDrawerToggle;

    // views that correspond to each navdrawer item, null if not yet created
    private Map<Integer, View> mNavDrawerItemViews = new HashMap<>();

    /* The List of drawer items used in the nav drawer */
    private List<DrawerItem> mDrawerItems = new ArrayList<>();


    /***********************************************************************************************
     *
     *  Lifecycle Methods
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGraph = ChipperApp.get(this).createScopedGraph(getModules());
        activityGraph.inject(this);

        // Setup Account Manager
        mAccountManager.setOnAccountLoadedListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPushManager.handleActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityGraph = null;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
        ButterKnife.inject(this);
        mPlayer = (MusicPlayer) getFragmentManager().findFragmentById(R.id.music_player);
        mPlayer.setSlidingLayout(mSlidingLayout);
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();
        mSlidingLayout.hidePanel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAccountManager.onStart();
        mPushManager.checkRegistration(this);
        setupAccountBox();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAccountManager.onStop();
    }

    /**
     * Called when the plus account info is loaded
     */
    @Override
    public void onLoaded() {
        setupAccountBox();
    }

    /***********************************************************************************************
     *
     *  Helper Methods
     *
     */


    /**
     * Get the toolbar actionbar
     *
     * @return      get teh action bar
     */
    public Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = ButterKnife.findById(this, R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
            }
        }
        return mActionBarToolbar;
    }

    /**
     * Get the reference to the music player
     * @return
     */
    protected MusicPlayer getPlayer(){
        return mPlayer;
    }

    /**
     * Get the sliding layout reference
     * @return
     */
    protected SlidingUpPanelLayout getSlidingLayout(){
        return mSlidingLayout;
    }

    /**
     * Setup the account box
     */
    private void setupAccountBox(){

        // Get Views
        View chosenAccountView = findViewById(R.id.chosen_account_view);
        if(mAccountManager.getProfileId() == null){
            chosenAccountView.setVisibility(View.GONE);
        }else{
            chosenAccountView.setVisibility(View.VISIBLE);
        }

        ImageView coverImageView = ButterKnife.findById(chosenAccountView, R.id.profile_cover_image);
        ImageView profileImageView = ButterKnife.findById(chosenAccountView, R.id.profile_image);
        TextView nameTextView = ButterKnife.findById(chosenAccountView, R.id.profile_name_text);
        TextView email = ButterKnife.findById(chosenAccountView, R.id.profile_email_text);

        // Now attempt to load the information from leh profile
        String imageUrl = mAccountManager.getImageUrl();
        if(imageUrl != null){
            Picasso.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.person_image_empty)
                    .into(profileImageView);
        }

        String coverImageUrl = mAccountManager.getCoverUrl();
        if(coverImageUrl != null){
            Picasso.with(this)
                    .load(coverImageUrl)
                    .placeholder(R.drawable.default_cover)
                    .into(coverImageView);
        }

        String displayName = mAccountManager.getDisplayName();
        if(displayName != null){
            nameTextView.setVisibility(View.VISIBLE);
            nameTextView.setText(displayName);
        }else{
            nameTextView.setVisibility(View.GONE);
        }

        email.setText(mAccountManager.getAccountName());

    }

    /**
     * Setup the navigation drawer
     */
    private void setupNavDrawer(){
        // What nav drawer item should be selected?
        int selfItem = getSelfNavDrawerItem();

        // Safety check for layout
        if (mDrawerLayout == null) {
            return;
        }

        // Set the drawer layout statusbar color
        mDrawerLayout.setStatusBarBackgroundColor(
                getResources().getColor(R.color.primaryDark));

        // Find the ScrimInsetScrollView
        ScrimInsetsScrollView navDrawer = ButterKnife.findById(mDrawerLayout, R.id.navdrawer);

        // Check for invalid self item
        if(selfItem == NAVDRAWER_ITEM_INVALID){
            // do not show a nav drawer
            if (navDrawer != null) {
                ((ViewGroup) navDrawer.getParent()).removeView(navDrawer);
            }
            mDrawerLayout = null;
            return;
        }

        // Otherwise if we have the nav drawer, continue setting it up
        if(navDrawer != null){
            final View chosenAccountContentView = findViewById(R.id.chosen_account_content_view);
            final View chosenAccountView = findViewById(R.id.chosen_account_view);
            final int navDrawerChosenAccountHeight = getResources().getDimensionPixelSize(
                    R.dimen.navdrawer_chosen_account_height);

            navDrawer.setOnInsetsCallback(new ScrimInsetsScrollView.OnInsetsCallback() {
                @Override
                public void onInsetsChanged(Rect insets) {
                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)
                            chosenAccountContentView.getLayoutParams();
                    lp.topMargin = insets.top;
                    chosenAccountContentView.setLayoutParams(lp);

                    ViewGroup.LayoutParams lp2 = chosenAccountView.getLayoutParams();
                    lp2.height = navDrawerChosenAccountHeight + insets.top;
                    chosenAccountView.setLayoutParams(lp2);
                }
            });

        }

        // Setup the drawer toggle
        if(mActionBarToolbar != null){
            mDrawerToggle = new ActionBarDrawerToggle(this,
                    mDrawerLayout,
                    mActionBarToolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close){

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);


                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);

                    // Update 'learned drawer' preference here.

                }

                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    super.onDrawerSlide(drawerView, slideOffset);
                    onNavDrawerSlide(slideOffset);
                }
            };

            // Defer code dependent on restoration of previous instance state.
            mDrawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    mDrawerToggle.syncState();
                }
            });

            mDrawerLayout.setDrawerListener(mDrawerToggle);
        }

        // Setup the drawer shadow
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        // Populate the nav drawer
        populateNavDrawer();

    }

    /**
     * Populate the navigation drawer
     *
     */
    private void populateNavDrawer(){
        // Clear out any existing items
        mDrawerItems.clear();

        // Generate all the drawer items that will be used in the drawer
        mDrawerItems.add(new IconDrawerItem(NAVDRAWER_ITEM_DASHBOARD, R.string.navdrawer_item_dashboard, R.drawable.ic_dashboard));
        mDrawerItems.add(new IconDrawerItem(NAVDRAWER_ITEM_CHIPTUNES, R.string.navdrawer_item_chiptunes, R.drawable.ic_music));
        mDrawerItems.add(new IconDrawerItem(NAVDRAWER_ITEM_POPULAR, R.string.navdrawer_item_popular, R.drawable.ic_popular));
        mDrawerItems.add(new IconDrawerItem(NAVDRAWER_ITEM_FEATURED, R.string.navdrawer_item_featured, R.drawable.ic_featured));
        mDrawerItems.add(new IconDrawerItem(NAVDRAWER_ITEM_PLAYLISTS, R.string.navdrawer_item_playlists, R.drawable.ic_playlists));
        mDrawerItems.add(new IconDrawerItem(NAVDRAWER_ITEM_PARTIES, R.string.navdrawer_item_parties, R.drawable.ic_party));

        mDrawerItems.add(new SeperatorDrawerItem());

        mDrawerItems.add(new SwitchDrawerItem(NAVDRAWER_ITEM_OFFLINE_MODE, R.string.navdrawer_item_offline, mOfflineSwitchPreference, new SwitchDrawerItem.OnSwitchToggleListener() {
            @Override
            public void onToggled(boolean checked) {
                // Post event to let UI know that it should alter it's content
                mBus.post(new OfflineModeChangeEvent(checked));
            }
        }));
        mDrawerItems.add(new IconDrawerItem(NAVDRAWER_ITEM_SETTINGS, R.string.navdrawer_item_settings, R.drawable.ic_settings));
        mDrawerItems.add(new IconDrawerItem(NAVDRAWER_ITEM_FEEDBACK, R.string.navdrawer_item_feedback, R.drawable.ic_forum));

        // Now generate the items into the view
        createNavDrawerItems();

    }

    /**
     * Populate the nav drawer items into the view
     */
    private void createNavDrawerItems(){
        if (mDrawerItemsListContainer == null) {
            return;
        }

        mNavDrawerItemViews.clear();
        mDrawerItemsListContainer.removeAllViews();
        for (DrawerItem item: mDrawerItems) {
            item.setSelected(item.getId() == getSelfNavDrawerItem());
            View view = item.onCreateView(getLayoutInflater(), mDrawerItemsListContainer);
            if(!(item instanceof SeperatorDrawerItem)){
                view.setId(item.getId());
                mNavDrawerItemViews.put(item.getId(), view);

                // Set the view's click listener
                if(!(item instanceof SwitchDrawerItem)) {
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onNavDrawerItemClicked(v.getId());
                        }
                    });
                }

            }

            mDrawerItemsListContainer.addView(view);
        }
    }

    /**
     * Return whether or not hte drawer is open
     * @return
     */
    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.START);
    }

    /**
     * Close the navdrawer if possible
     */
    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(Gravity.START);
        }
    }

    /**
     * Return whether or not a drawer item is special
     * @param itemId
     * @return
     */
    private boolean isSpecialItem(int itemId) {
        return itemId == NAVDRAWER_ITEM_SETTINGS;
    }

    /**
     * Call when a nav drawer item is clicked
     *
     * @param itemId        the id of the item clicked
     */
    private void onNavDrawerItemClicked(final int itemId) {
        if (itemId == getSelfNavDrawerItem()) {
            mDrawerLayout.closeDrawer(Gravity.START);
            return;
        }

        if (isSpecialItem(itemId)) {
            goToNavDrawerItem(itemId);
        } else {
            // launch the target Activity after a short delay, to allow the close animation to play
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToNavDrawerItem(itemId);
                }
            }, NAVDRAWER_LAUNCH_DELAY);

            // change the active item on the list so the user can see the item changed
            setSelectedNavDrawerItem(itemId);

            // fade out the main content
//            View mainContent = findViewById(R.id.main_content);
//            if (mainContent != null) {
//                mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
//            }
        }

        mDrawerLayout.closeDrawer(Gravity.START);
    }

    /**
     * Travel to a specific drawer item destination
     * @param item
     */
    private void goToNavDrawerItem(int item) {
        Intent intent;
        switch (item) {
            case NAVDRAWER_ITEM_DASHBOARD:
                intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);
                finish();
                break;
            case NAVDRAWER_ITEM_CHIPTUNES:
                intent = new Intent(this, ChiptunesActivity.class);
                startActivity(intent);
                finish();
                break;
            case NAVDRAWER_ITEM_POPULAR:
                intent = new Intent(this, PopularActivity.class);
                startActivity(intent);
                finish();
                break;
            case NAVDRAWER_ITEM_FEATURED:
                intent = new Intent(this, FeaturedActivity.class);
                startActivity(intent);
                finish();
                break;
            case NAVDRAWER_ITEM_PLAYLISTS:
                intent = new Intent(this, PlaylistActivity.class);
                startActivity(intent);
                finish();
                break;
            case NAVDRAWER_ITEM_PARTIES:
//                intent = new Intent(this, PeopleIveMetActivity.class);
//                startActivity(intent);
//                finish();
                break;
            case NAVDRAWER_ITEM_SETTINGS:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case NAVDRAWER_ITEM_FEEDBACK:
//                intent = new Intent(this, VideoLibraryActivity.class);
//                startActivity(intent);
//                finish();
                break;
        }
    }

    /**
     * Sets up the given navdrawer item's appearance to the selected state. Note: this could
     * also be accomplished (perhaps more cleanly) with state-based layouts.
     */
    private void setSelectedNavDrawerItem(int itemId) {
        for(DrawerItem item: mDrawerItems){
            formatNavDrawerItem(item, itemId == item.getId());
        }
    }

    /**
     * Format a nav drawer item based on current selected states
     * @param item
     * @param selected
     */
    private void formatNavDrawerItem(DrawerItem item, boolean selected) {
        if (item instanceof SeperatorDrawerItem || item instanceof SwitchDrawerItem) {
            // not applicable
            return;
        }

        // Get the associated view
        View view = mNavDrawerItemViews.get(item.getId());

        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView titleView = (TextView) view.findViewById(R.id.title);

        // configure its appearance according to whether or not it's selected
        titleView.setTextColor(selected ?
                getResources().getColor(R.color.navdrawer_text_color_selected) :
                getResources().getColor(R.color.navdrawer_text_color));
        iconView.setColorFilter(selected ?
                getResources().getColor(R.color.navdrawer_icon_tint_selected) :
                getResources().getColor(R.color.navdrawer_icon_tint));
    }

    /***********************************************************************************************
     *
     *  Abstract Methods
     *
     */

    /**
     * Returns the navigation drawer item that corresponds to this Activity. Subclasses
     * of BaseActivity override this to indicate what nav drawer item corresponds to them
     * Return NAVDRAWER_ITEM_INVALID to mean that this Activity should not have a Nav Drawer.
     */
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_INVALID;
    }

    /**
     * This is called when the nav drawer is sliding
     * @param offset        the offset of hte drawer between [0-1]
     */
    protected abstract void onNavDrawerSlide(float offset);

    /**
     * Return the list of Dagger Modules used to
     * construct this activity
     *
     * @return      the list of accompanying Dagger modules
     */
    protected abstract Object[] getModules();
}
