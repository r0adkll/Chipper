package com.r0adkll.chipper.ui.settings;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.r0adkll.chipper.ChipperApp;
import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.data.CashMachine;
import com.r0adkll.chipper.qualifiers.CurrentUser;
import com.r0adkll.chipper.ui.model.BaseActivity;
import com.r0adkll.deadskunk.utils.FileUtils;
import com.r0adkll.deadskunk.utils.Utils;

import java.io.File;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.ui.settings
 * Created by drew.heavner on 12/9/14.
 */
public class SettingsActivity extends ActionBarActivity {

    @InjectView(R.id.setting_layout)
    LinearLayout mSettingLayout;

    @InjectView(R.id.setting_account)
    TextView mSettingAccount;

    @Inject @CurrentUser
    User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChipperApp.get(this).inject(this);

        setContentView(R.layout.activity_settings);
        ButterKnife.inject(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background));

        // Apply user information to the setting item
        mSettingAccount.setText(mCurrentUser.email);
    }

    @OnClick({
        R.id.setting_general,
        R.id.setting_account,
        R.id.setting_downloading,
        R.id.setting_about
    })
    void onSettingItemClicked(View v){
        switch (v.getId()){
            case R.id.setting_general:
                showSetting(GeneralSettings.createInstance());
                break;
            case R.id.setting_account:
                showSetting(AccountSettings.createInstance());
                break;
            case R.id.setting_downloading:
                showSetting(DownloadingSettings.createInstance());
                break;
            case R.id.setting_about:
                showSetting(AboutSettings.createInstance());
                break;
        }
    }

    private void showSetting(PreferenceFragment setting){
        mSettingLayout.setVisibility(View.GONE);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, setting)
                .addToBackStack(null)
                .commit();
    }

    private void reset(){
        mSettingLayout.setVisibility(View.VISIBLE);
        getSupportActionBar().setTitle(R.string.settings);
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
            reset();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){

            if(getFragmentManager().getBackStackEntryCount() > 0){
                getFragmentManager().popBackStack();
                reset();
            }else{
                finish();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /***********************************************************************************************
     *
     *
     * Preference Fragments
     *
     *
     */

    /**
     * General Settings Fragment
     */
    public static class GeneralSettings extends PreferenceFragment{

        public static GeneralSettings createInstance(){
            return new GeneralSettings();
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            ChipperApp.get(getActivity()).inject(this);
            addPreferencesFromResource(R.xml.settings_general);
            ActionBar ab =((SettingsActivity) getActivity()).getSupportActionBar();
            ab.setTitle(R.string.settings_general);

            // Set the theme preference
            final String[] themes = getResources().getStringArray(R.array.theme_choices);
            ListPreference themePickerPreference = (ListPreference) findPreference("pref_theme_picker");
            int value = Integer.parseInt(themePickerPreference.getValue());
            themePickerPreference.setSummary(themes[value]);
            themePickerPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(themes[Integer.parseInt((String) newValue)]);
                    return true;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if(item.getItemId() == android.R.id.home){
                getFragmentManager().popBackStack();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            switch (preference.getKey()){

            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }

    /**
     * Downloading settings fragment
     */
    public static class DownloadingSettings extends PreferenceFragment{

        public static DownloadingSettings createInstance(){
            return new DownloadingSettings();
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            ChipperApp.get(getActivity()).inject(this);
            addPreferencesFromResource(R.xml.settings_downloading);
            ActionBar ab =((SettingsActivity) getActivity()).getSupportActionBar();
            ab.setTitle(R.string.settings_downloading);

            computeCacheSizes();

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if(item.getItemId() == android.R.id.home){
                getFragmentManager().popBackStack();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            switch (preference.getKey()){
                case "pref_clear_offline_cache":
                    if(FileUtils.deleteDirectory(new File(getActivity().getFilesDir(), CashMachine.CACHE_DIRECTORY_NAME))){
                        computeCacheSizes();
                        return true;
                    }
                    break;
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }


        private void computeCacheSizes(){

            new AsyncTask<Void, Void, Long>(){
                @Override
                protected Long doInBackground(Void... params) {

                    // Compute the cache sizes
                    File dir = new File(getActivity().getFilesDir(), CashMachine.CACHE_DIRECTORY_NAME);
                    long size = 0;

                    for(File file: dir.listFiles()){
                        size += file.length();
                    }

                    return size;
                }

                @Override
                protected void onPostExecute(Long result) {

                    // Get the menu item
                    Preference offlineSize = getPreferenceManager().findPreference("pref_offline_size");

                    // Condensed
                    offlineSize.setSummary(Utils.condenseFileSize(result, Utils.TWO_DIGIT));


                }
            }.execute();

        }

    }

    /**
     * Accounts Setting Fragment
     */
    public static class AccountSettings extends PreferenceFragment{

        public static AccountSettings createInstance(){
            return new AccountSettings();
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            ChipperApp.get(getActivity()).inject(this);
            addPreferencesFromResource(R.xml.settings_account);
            ActionBar ab =((SettingsActivity) getActivity()).getSupportActionBar();
            ab.setTitle(R.string.settings_account);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if(item.getItemId() == android.R.id.home){
                getFragmentManager().popBackStack();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            switch (preference.getKey()){

            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }

    /**
     * About Setting Fragment
     */
    public static class AboutSettings extends PreferenceFragment{

        public static AboutSettings createInstance(){
            return new AboutSettings();
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            ChipperApp.get(getActivity()).inject(this);
            addPreferencesFromResource(R.xml.settings_account);
            ActionBar ab =((SettingsActivity) getActivity()).getSupportActionBar();
            ab.setTitle(R.string.settings_about);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if(item.getItemId() == android.R.id.home){
                getFragmentManager().popBackStack();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            switch (preference.getKey()){

            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }



}
