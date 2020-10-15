/*
 * Copyright (C) 2018-2020 The Dirty Unicorns Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nusantara.wings.fragments.hardware;

import android.content.Context;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.Handler;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.nad.NadUtils;

import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.search.SearchIndexable;

import com.nusantara.support.preferences.SystemSettingSwitchPreference;
import com.nusantara.support.preferences.SecureSettingSwitchPreference;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class NavigationOptions extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String KEY_NAVIGATION_BAR_ENABLED = "force_show_navbar";
    private static final String KEY_LAYOUT_SETTINGS = "layout_settings";
    private static final String KEY_NAVIGATION_BAR_ARROWS = "navigation_bar_menu_arrow_keys";
    private static final String KEY_SWAP_KEYS = "swap_navigation_keys";

    private static final String KEY_BACK_LONG_PRESS_ACTION = "back_key_long_press";
    private static final String KEY_BACK_LONG_PRESS_CUSTOM_APP = "back_key_long_press_custom_app";
    private static final String KEY_BACK_DOUBLE_TAP_ACTION = "back_key_double_tap";
    private static final String KEY_BACK_DOUBLE_TAP_CUSTOM_APP = "back_key_double_tap_custom_app";
    private static final String KEY_HOME_LONG_PRESS_ACTION = "home_key_long_press";
    private static final String KEY_HOME_LONG_PRESS_CUSTOM_APP = "home_key_long_press_custom_app";
    private static final String KEY_HOME_DOUBLE_TAP_ACTION = "home_key_double_tap";
    private static final String KEY_HOME_DOUBLE_TAP_CUSTOM_APP = "home_key_double_tap_custom_app";
    private static final String KEY_APP_SWITCH_LONG_PRESS = "app_switch_key_long_press";
    private static final String KEY_APP_SWITCH_LONG_PRESS_CUSTOM_APP = "app_switch_key_long_press_custom_app";
    private static final String KEY_APP_SWITCH_DOUBLE_TAP = "app_switch_key_double_tap";
    private static final String KEY_APP_SWITCH_DOUBLE_TAP_CUSTOM_APP = "app_switch_key_double_tap_custom_app";
    private static final String KEY_MENU_LONG_PRESS_ACTION = "menu_key_long_press";
    private static final String KEY_MENU_DOUBLE_TAP_ACTION = "menu_key_double_tap";
    private static final String KEY_CAMERA_LONG_PRESS_ACTION = "camera_key_long_press";
    private static final String KEY_CAMERA_DOUBLE_TAP_ACTION = "camera_key_double_tap";
    private static final String KEY_ASSIST_LONG_PRESS_ACTION = "assist_key_long_press";
    private static final String KEY_ASSIST_DOUBLE_TAP_ACTION = "assist_key_double_tap";

    private static final String KEY_CATEGORY_HOME          = "home_key";
    private static final String KEY_CATEGORY_BACK          = "back_key";
    private static final String KEY_CATEGORY_MENU          = "menu_key";
    private static final String KEY_CATEGORY_ASSIST        = "assist_key";
    private static final String KEY_CATEGORY_APP_SWITCH    = "app_switch_key";
    private static final String KEY_CATEGORY_CAMERA        = "camera_key";
    private static final String KEY_CATEGORY_HW_KEYS       = "hw_keys";
    private static final String KEY_CATEGORY_BACK_GESTURE  = "back_gesture";

    private static final int KEY_MASK_HOME = 0x01;
    private static final int KEY_MASK_BACK = 0x02;
    private static final int KEY_MASK_MENU = 0x04;
    private static final int KEY_MASK_ASSIST = 0x08;
    private static final int KEY_MASK_APP_SWITCH = 0x10;
    private static final int KEY_MASK_CAMERA = 0x20;

    private ListPreference mBackLongPress;
    private ListPreference mBackDoubleTap;
    private ListPreference mHomeLongPress;
    private ListPreference mHomeDoubleTap;
    private ListPreference mAppSwitchLongPress;
    private ListPreference mAppSwitchDoubleTap;
    private ListPreference mMenuLongPress;
    private ListPreference mMenuDoubleTap;
    private ListPreference mCameraLongPress;
    private ListPreference mCameraDoubleTap;
    private ListPreference mAssistLongPress;
    private ListPreference mAssistDoubleTap;

    private Preference mAppSwitchLongPressCustomApp;
    private Preference mAppSwitchDoubleTapCustomApp;
    private Preference mBackLongPressCustomApp;
    private Preference mBackDoubleTapCustomApp;
    private Preference mHomeLongPressCustomApp;
    private Preference mHomeDoubleTapCustomApp;
    private Preference mLayoutSettings;

    private SwitchPreference mNavigationBar;
    private SystemSettingSwitchPreference mNavigationArrowKeys;
    private SystemSettingSwitchPreference mSwapKeys;

    private PreferenceCategory homeCategory;
    private PreferenceCategory backCategory;
    private PreferenceCategory menuCategory;
    private PreferenceCategory assistCategory;
    private PreferenceCategory appSwitchCategory;
    private PreferenceCategory cameraCategory;
    private PreferenceCategory hwKeysCategory;

    private int deviceKeys;

    private boolean mIsNavSwitchingMode = false;

    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.nad_navigation_options);

        final PreferenceScreen prefSet = getPreferenceScreen();
        final ContentResolver resolver = getActivity().getContentResolver();

        deviceKeys = getResources().getInteger(
                com.android.internal.R.integer.config_deviceHardwareKeys);

        int backKeyLongPress = getResources().getInteger(
                com.android.internal.R.integer.config_longPressOnBackKeyBehavior);
        int backKeyDoubleTap = getResources().getInteger(
                com.android.internal.R.integer.config_doubleTapOnBackKeyBehavior);
        int homeKeyLongPress = getResources().getInteger(
                com.android.internal.R.integer.config_longPressOnHomeKeyBehavior);
        int homeKeyDoubleTap = getResources().getInteger(
                com.android.internal.R.integer.config_doubleTapOnHomeKeyBehavior);
        int AppSwitchKeyLongPress = getResources().getInteger(
                com.android.internal.R.integer.config_longPressOnAppSwitchKeyBehavior);
        int AppSwitchKeyDoubleTap = getResources().getInteger(
                com.android.internal.R.integer.config_doubleTapOnAppSwitchKeyBehavior);
        int MenuKeyLongPress = getResources().getInteger(
                com.android.internal.R.integer.config_longPressOnMenuKeyBehavior);
        int MenuKeyDoubleTap = getResources().getInteger(
                com.android.internal.R.integer.config_doubleTapOnMenuKeyBehavior);
        int CameraKeyLongPress = getResources().getInteger(
                com.android.internal.R.integer.config_longPressOnCameraKeyBehavior);
        int CameraKeyDoubleTap = getResources().getInteger(
                com.android.internal.R.integer.config_doubleTapOnCameraKeyBehavior);
        int AssistKeyLongPress = getResources().getInteger(
                com.android.internal.R.integer.config_longPressOnAssistKeyBehavior);
        int AssistKeyDoubleTap = getResources().getInteger(
                com.android.internal.R.integer.config_doubleTapOnAssistKeyBehavior);

        boolean hasHome = (deviceKeys & KEY_MASK_HOME) != 0;
        boolean hasBack = (deviceKeys & KEY_MASK_BACK) != 0;
        boolean hasMenu = (deviceKeys & KEY_MASK_MENU) != 0;
        boolean hasAssist = (deviceKeys & KEY_MASK_ASSIST) != 0;
        boolean hasAppSwitch = (deviceKeys & KEY_MASK_APP_SWITCH) != 0;
        boolean hasCamera = (deviceKeys & KEY_MASK_CAMERA) != 0;

        homeCategory = (PreferenceCategory) findPreference(KEY_CATEGORY_HOME);
        backCategory = (PreferenceCategory) findPreference(KEY_CATEGORY_BACK);
        menuCategory = (PreferenceCategory) findPreference(KEY_CATEGORY_MENU);
        assistCategory = (PreferenceCategory) findPreference(KEY_CATEGORY_ASSIST);
        appSwitchCategory = (PreferenceCategory) findPreference(KEY_CATEGORY_APP_SWITCH);
        cameraCategory = (PreferenceCategory) findPreference(KEY_CATEGORY_CAMERA);
        hwKeysCategory = (PreferenceCategory) findPreference(KEY_CATEGORY_HW_KEYS);

        mBackLongPressCustomApp = (Preference) findPreference(KEY_BACK_LONG_PRESS_CUSTOM_APP);
        mBackDoubleTapCustomApp = (Preference) findPreference(KEY_BACK_DOUBLE_TAP_CUSTOM_APP);
        mHomeLongPressCustomApp = (Preference) findPreference(KEY_HOME_LONG_PRESS_CUSTOM_APP);
        mHomeDoubleTapCustomApp = (Preference) findPreference(KEY_HOME_DOUBLE_TAP_CUSTOM_APP);
        mAppSwitchLongPressCustomApp = (Preference) findPreference(KEY_APP_SWITCH_LONG_PRESS_CUSTOM_APP);
        mAppSwitchDoubleTapCustomApp = (Preference) findPreference(KEY_APP_SWITCH_DOUBLE_TAP_CUSTOM_APP);

        mLayoutSettings = (Preference) findPreference(KEY_LAYOUT_SETTINGS);
        if (NadUtils.isGestureNavbar()) {
            prefSet.removePreference(mLayoutSettings);
        }

        mSwapKeys = (SystemSettingSwitchPreference) findPreference(KEY_SWAP_KEYS);
        mNavigationArrowKeys = (SystemSettingSwitchPreference) findPreference(KEY_NAVIGATION_BAR_ARROWS);

        mNavigationBar = (SwitchPreference) findPreference(KEY_NAVIGATION_BAR_ENABLED);
        mNavigationBar.setChecked(isNavbarVisible());
        mNavigationBar.setOnPreferenceChangeListener(this);

        mBackLongPress = (ListPreference) findPreference(KEY_BACK_LONG_PRESS_ACTION);
        int backlongpress = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.KEY_BACK_LONG_PRESS_ACTION, backKeyLongPress, UserHandle.USER_CURRENT);
        mBackLongPress.setValue(String.valueOf(backlongpress));
        mBackLongPress.setSummary(mBackLongPress.getEntry());
        mBackLongPress.setOnPreferenceChangeListener(this);

        mBackDoubleTap = (ListPreference) findPreference(KEY_BACK_DOUBLE_TAP_ACTION);
        int backdoubletap = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.KEY_BACK_DOUBLE_TAP_ACTION, backKeyDoubleTap, UserHandle.USER_CURRENT);
        mBackDoubleTap.setValue(String.valueOf(backdoubletap));
        mBackDoubleTap.setSummary(mBackDoubleTap.getEntry());
        mBackDoubleTap.setOnPreferenceChangeListener(this);

        mHomeLongPress = (ListPreference) findPreference(KEY_HOME_LONG_PRESS_ACTION);
        int homelongpress = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.KEY_HOME_LONG_PRESS_ACTION, homeKeyLongPress, UserHandle.USER_CURRENT);
        mHomeLongPress.setValue(String.valueOf(homelongpress));
        mHomeLongPress.setSummary(mHomeLongPress.getEntry());
        mHomeLongPress.setOnPreferenceChangeListener(this);

        mHomeDoubleTap = (ListPreference) findPreference(KEY_HOME_DOUBLE_TAP_ACTION);
        int homedoubletap = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.KEY_HOME_DOUBLE_TAP_ACTION, homeKeyDoubleTap, UserHandle.USER_CURRENT);
        mHomeDoubleTap.setValue(String.valueOf(homedoubletap));
        mHomeDoubleTap.setSummary(mHomeDoubleTap.getEntry());
        mHomeDoubleTap.setOnPreferenceChangeListener(this);

        mAppSwitchLongPress = (ListPreference) findPreference(KEY_APP_SWITCH_LONG_PRESS);
        int appswitchlongpress = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.KEY_APP_SWITCH_LONG_PRESS_ACTION, AppSwitchKeyLongPress, UserHandle.USER_CURRENT);
        mAppSwitchLongPress.setValue(String.valueOf(appswitchlongpress));
        mAppSwitchLongPress.setSummary(mAppSwitchLongPress.getEntry());
        mAppSwitchLongPress.setOnPreferenceChangeListener(this);

        mAppSwitchDoubleTap = (ListPreference) findPreference(KEY_APP_SWITCH_DOUBLE_TAP);
        int appswitchdoubletap = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.KEY_APP_SWITCH_DOUBLE_TAP_ACTION, AppSwitchKeyDoubleTap, UserHandle.USER_CURRENT);
        mAppSwitchDoubleTap.setValue(String.valueOf(appswitchdoubletap));
        mAppSwitchDoubleTap.setSummary(mAppSwitchDoubleTap.getEntry());
        mAppSwitchDoubleTap.setOnPreferenceChangeListener(this);

        mMenuLongPress = (ListPreference) findPreference(KEY_MENU_LONG_PRESS_ACTION);
        int menulongpress = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.KEY_MENU_LONG_PRESS_ACTION, MenuKeyLongPress, UserHandle.USER_CURRENT);
        mMenuLongPress.setValue(String.valueOf(menulongpress));
        mMenuLongPress.setSummary(mMenuLongPress.getEntry());
        mMenuLongPress.setOnPreferenceChangeListener(this);

        mMenuDoubleTap = (ListPreference) findPreference(KEY_MENU_DOUBLE_TAP_ACTION);
        int menudoubletap = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.KEY_MENU_DOUBLE_TAP_ACTION, MenuKeyDoubleTap, UserHandle.USER_CURRENT);
        mMenuDoubleTap.setValue(String.valueOf(menudoubletap));
        mMenuDoubleTap.setSummary(mMenuDoubleTap.getEntry());
        mMenuDoubleTap.setOnPreferenceChangeListener(this);

        mCameraLongPress = (ListPreference) findPreference(KEY_CAMERA_LONG_PRESS_ACTION);
        int cameralongpress = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.KEY_CAMERA_LONG_PRESS_ACTION, CameraKeyLongPress, UserHandle.USER_CURRENT);
        mCameraLongPress.setValue(String.valueOf(cameralongpress));
        mCameraLongPress.setSummary(mCameraLongPress.getEntry());
        mCameraLongPress.setOnPreferenceChangeListener(this);

        mCameraDoubleTap = (ListPreference) findPreference(KEY_CAMERA_DOUBLE_TAP_ACTION);
        int cameradoubletap = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.KEY_CAMERA_DOUBLE_TAP_ACTION, CameraKeyDoubleTap, UserHandle.USER_CURRENT);
        mCameraDoubleTap.setValue(String.valueOf(cameradoubletap));
        mCameraDoubleTap.setSummary(mCameraDoubleTap.getEntry());
        mCameraDoubleTap.setOnPreferenceChangeListener(this);

        mAssistLongPress = (ListPreference) findPreference(KEY_ASSIST_LONG_PRESS_ACTION);
        int assistlongpress = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.KEY_ASSIST_LONG_PRESS_ACTION, AssistKeyLongPress, UserHandle.USER_CURRENT);
        mAssistLongPress.setValue(String.valueOf(assistlongpress));
        mAssistLongPress.setSummary(mAssistLongPress.getEntry());
        mAssistLongPress.setOnPreferenceChangeListener(this);

        mAssistDoubleTap = (ListPreference) findPreference(KEY_ASSIST_DOUBLE_TAP_ACTION);
        int assistdoubletap = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.KEY_ASSIST_DOUBLE_TAP_ACTION, AssistKeyDoubleTap, UserHandle.USER_CURRENT);
        mAssistDoubleTap.setValue(String.valueOf(assistdoubletap));
        mAssistDoubleTap.setSummary(mAssistDoubleTap.getEntry());
        mAssistDoubleTap.setOnPreferenceChangeListener(this);

        if (!hasMenu && menuCategory != null) {
            prefSet.removePreference(menuCategory);
        }

        if (!hasAssist && assistCategory != null) {
            prefSet.removePreference(assistCategory);
        }

        if (!hasCamera && cameraCategory != null) {
            prefSet.removePreference(cameraCategory);
        }

        if (deviceKeys == 0) {
            prefSet.removePreference(hwKeysCategory);
            prefSet.removePreference(menuCategory);
            prefSet.removePreference(assistCategory);
            prefSet.removePreference(cameraCategory);
        }

        mHandler = new Handler();
        updateHwKeys();
        navbarCheck();
        customAppCheck();

        boolean keySwapSupported = hasBack && (hasMenu || hasAppSwitch);

        if (!keySwapSupported) {
            mSwapKeys.setVisible(false);
        }

        mBackLongPressCustomApp.setVisible(mBackLongPress.getEntryValues()
                [backlongpress].equals("16"));
        mBackDoubleTapCustomApp.setVisible(mBackDoubleTap.getEntryValues()
                [backdoubletap].equals("16"));
        mHomeLongPressCustomApp.setVisible(mHomeLongPress.getEntryValues()
                [homelongpress].equals("16"));
        mHomeDoubleTapCustomApp.setVisible(mHomeDoubleTap.getEntryValues()
                [homedoubletap].equals("16"));
        mAppSwitchLongPressCustomApp.setVisible(mAppSwitchLongPress.getEntryValues()
                [appswitchlongpress].equals("16"));
        mAppSwitchDoubleTapCustomApp.setVisible(mAppSwitchDoubleTap.getEntryValues()
                [appswitchdoubletap].equals("16"));
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mNavigationBar) {
            boolean value = (Boolean) objValue;
            if (mIsNavSwitchingMode) {
                return false;
            }
            mIsNavSwitchingMode = true;
            Settings.System.putInt(resolver,
                    Settings.System.FORCE_SHOW_NAVBAR, value ? 1 : 0);
            navbarCheck();
            updateHwKeys();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIsNavSwitchingMode = false;
                }
            }, 1500);
            return true;
        } else if (preference == mBackLongPress) {
            int value = Integer.parseInt((String) objValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.KEY_BACK_LONG_PRESS_ACTION, value,
                    UserHandle.USER_CURRENT);
            int index = mBackLongPress.findIndexOfValue((String) objValue);
            mBackLongPress.setSummary(
                    mBackLongPress.getEntries()[index]);
            customAppCheck();
            mBackLongPressCustomApp.setVisible(mBackLongPress.getEntryValues()
                    [index].equals("16"));
            return true;
        } else if (preference == mBackDoubleTap) {
            int value = Integer.parseInt((String) objValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.KEY_BACK_DOUBLE_TAP_ACTION, value,
                    UserHandle.USER_CURRENT);
            int index = mBackDoubleTap.findIndexOfValue((String) objValue);
            mBackDoubleTap.setSummary(
                    mBackDoubleTap.getEntries()[index]);
            mBackDoubleTapCustomApp.setVisible(mBackDoubleTap.getEntryValues()
                    [index].equals("16"));
            return true;
        } else if (preference == mHomeLongPress) {
            int value = Integer.parseInt((String) objValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.KEY_HOME_LONG_PRESS_ACTION, value,
                    UserHandle.USER_CURRENT);
            int index = mHomeLongPress.findIndexOfValue((String) objValue);
            mHomeLongPress.setSummary(
                    mHomeLongPress.getEntries()[index]);
            mHomeLongPressCustomApp.setVisible(mHomeLongPress.getEntryValues()
                    [index].equals("16"));
            return true;
        } else if (preference == mHomeDoubleTap) {
            int value = Integer.parseInt((String) objValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.KEY_HOME_DOUBLE_TAP_ACTION, value,
                    UserHandle.USER_CURRENT);
            int index = mHomeDoubleTap.findIndexOfValue((String) objValue);
            mHomeDoubleTap.setSummary(
                    mHomeDoubleTap.getEntries()[index]);
            mHomeDoubleTapCustomApp.setVisible(mHomeDoubleTap.getEntryValues()
                    [index].equals("16"));
            return true;
        } else if (preference == mAppSwitchLongPress) {
            int value = Integer.parseInt((String) objValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.KEY_APP_SWITCH_LONG_PRESS_ACTION, value,
                    UserHandle.USER_CURRENT);
            int index = mAppSwitchLongPress.findIndexOfValue((String) objValue);
            mAppSwitchLongPress.setSummary(
                    mAppSwitchLongPress.getEntries()[index]);
            mAppSwitchLongPressCustomApp.setVisible(mAppSwitchLongPress.getEntryValues()
                    [index].equals("16"));
            return true;
        } else if (preference == mAppSwitchDoubleTap) {
            int value = Integer.parseInt((String) objValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.KEY_APP_SWITCH_DOUBLE_TAP_ACTION, value,
                    UserHandle.USER_CURRENT);
            int index = mAppSwitchDoubleTap.findIndexOfValue((String) objValue);
            mAppSwitchDoubleTap.setSummary(
                    mAppSwitchDoubleTap.getEntries()[index]);
            mAppSwitchDoubleTapCustomApp.setVisible(mAppSwitchDoubleTap.getEntryValues()
                    [index].equals("16"));
            return true;
        } else if (preference == mMenuLongPress) {
            int value = Integer.parseInt((String) objValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.KEY_MENU_LONG_PRESS_ACTION, value,
                    UserHandle.USER_CURRENT);
            int index = mMenuLongPress.findIndexOfValue((String) objValue);
            mMenuLongPress.setSummary(
                    mMenuLongPress.getEntries()[index]);
            return true;
        } else if (preference == mMenuDoubleTap) {
            int value = Integer.parseInt((String) objValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.KEY_MENU_DOUBLE_TAP_ACTION, value,
                    UserHandle.USER_CURRENT);
            int index = mMenuDoubleTap.findIndexOfValue((String) objValue);
            mMenuDoubleTap.setSummary(
                    mMenuDoubleTap.getEntries()[index]);
            return true;
        } else if (preference == mCameraLongPress) {
            int value = Integer.parseInt((String) objValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.KEY_CAMERA_LONG_PRESS_ACTION, value,
                    UserHandle.USER_CURRENT);
            int index = mCameraLongPress.findIndexOfValue((String) objValue);
            mCameraLongPress.setSummary(
                    mCameraLongPress.getEntries()[index]);
            return true;
        } else if (preference == mCameraDoubleTap) {
            int value = Integer.parseInt((String) objValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.KEY_CAMERA_DOUBLE_TAP_ACTION, value,
                    UserHandle.USER_CURRENT);
            int index = mCameraDoubleTap.findIndexOfValue((String) objValue);
            mCameraDoubleTap.setSummary(
                    mCameraDoubleTap.getEntries()[index]);
            return true;
        } else if (preference == mAssistLongPress) {
            int value = Integer.parseInt((String) objValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.KEY_ASSIST_LONG_PRESS_ACTION, value,
                    UserHandle.USER_CURRENT);
            int index = mAssistLongPress.findIndexOfValue((String) objValue);
            mAssistLongPress.setSummary(
                    mAssistLongPress.getEntries()[index]);
            return true;
        } else if (preference == mAssistDoubleTap) {
            int value = Integer.parseInt((String) objValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.KEY_ASSIST_DOUBLE_TAP_ACTION, value,
                    UserHandle.USER_CURRENT);
            int index = mAssistDoubleTap.findIndexOfValue((String) objValue);
            mAssistDoubleTap.setSummary(
                    mAssistDoubleTap.getEntries()[index]);
            return true;
        }
        return false;
    }

    private void updateHwKeys() {
        if (isNavbarVisible()) {
            hwKeysCategory.setEnabled(false);
        } else {
            hwKeysCategory.setEnabled(true);
        }
    }

    private boolean isNavbarVisible() {
        boolean defaultToNavigationBar = NadUtils.deviceSupportNavigationBar(getActivity());
        return Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.FORCE_SHOW_NAVBAR, defaultToNavigationBar ? 1 : 0) == 1;
    }

    private void navbarCheck() {
        deviceKeys = getResources().getInteger(
                com.android.internal.R.integer.config_deviceHardwareKeys);

        if (deviceKeys == 0) {
                homeCategory.setEnabled(true);
                backCategory.setEnabled(true);
                menuCategory.setEnabled(true);
                assistCategory.setEnabled(true);
                appSwitchCategory.setEnabled(true);
                cameraCategory.setEnabled(true);
                mNavigationArrowKeys.setEnabled(true);
        } else {
            if (isNavbarVisible()) {
                homeCategory.setEnabled(true);
                backCategory.setEnabled(true);
                menuCategory.setEnabled(true);
                assistCategory.setEnabled(true);
                appSwitchCategory.setEnabled(true);
                cameraCategory.setEnabled(true);
                mNavigationArrowKeys.setEnabled(true);
            } else {
                homeCategory.setEnabled(true);
                backCategory.setEnabled(true);
                menuCategory.setEnabled(true);
                assistCategory.setEnabled(true);
                appSwitchCategory.setEnabled(true);
                cameraCategory.setEnabled(true);
                mNavigationArrowKeys.setEnabled(false);
            }
        }

        if (NadUtils.isGestureNavbar() && isNavbarVisible()) {
            homeCategory.setVisible(false);
            backCategory.setVisible(false);
            menuCategory.setVisible(false);
            assistCategory.setVisible(false);
            appSwitchCategory.setVisible(false);
            cameraCategory.setVisible(false);
        } else {
            homeCategory.setVisible(true);
            backCategory.setVisible(true);
            menuCategory.setVisible(true);
            assistCategory.setVisible(true);
            appSwitchCategory.setVisible(true);
            cameraCategory.setVisible(true);
        }

        if (NadUtils.isThemeEnabled("com.android.internal.systemui.navbar.twobutton") && isNavbarVisible()) {
            homeCategory.setEnabled(true);
            backCategory.setEnabled(true);
            menuCategory.setVisible(false);
            assistCategory.setVisible(false);
            appSwitchCategory.setVisible(false);
            cameraCategory.setVisible(false);
        }
    }

    private void customAppCheck() {
        mBackLongPressCustomApp.setSummary(Settings.System.getString(getActivity().getContentResolver(),
                String.valueOf(Settings.System.KEY_BACK_LONG_PRESS_CUSTOM_APP_FR_NAME)));
        mBackDoubleTapCustomApp.setSummary(Settings.System.getString(getActivity().getContentResolver(),
                String.valueOf(Settings.System.KEY_BACK_DOUBLE_TAP_CUSTOM_APP_FR_NAME)));
        mHomeLongPressCustomApp.setSummary(Settings.System.getString(getActivity().getContentResolver(),
                String.valueOf(Settings.System.KEY_HOME_LONG_PRESS_CUSTOM_APP_FR_NAME)));
        mHomeDoubleTapCustomApp.setSummary(Settings.System.getString(getActivity().getContentResolver(),
                String.valueOf(Settings.System.KEY_HOME_DOUBLE_TAP_CUSTOM_APP_FR_NAME)));
        mAppSwitchLongPressCustomApp.setSummary(Settings.System.getString(getActivity().getContentResolver(),
                String.valueOf(Settings.System.KEY_APP_SWITCH_LONG_PRESS_CUSTOM_APP_FR_NAME)));
        mAppSwitchDoubleTapCustomApp.setSummary(Settings.System.getString(getActivity().getContentResolver(),
                String.valueOf(Settings.System.KEY_APP_SWITCH_DOUBLE_TAP_CUSTOM_APP_FR_NAME)));
    }

    @Override
    public void onResume() {
        super.onResume();
        navbarCheck();
        updateHwKeys();
        customAppCheck();
    }

    @Override
    public void onPause() {
        super.onPause();
        navbarCheck();
        updateHwKeys();
        customAppCheck();
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.NUSANTARA_PRJ;
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.nad_navigation_options;
                    result.add(sir);
                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    return keys;
        }
    };
}
