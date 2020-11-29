/*
 * Copyright (C) 2019-2020 Nusantara Project
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

package com.nusantara.wings;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;

import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.util.nad.NadUtils;

public class NusantaraCat extends SettingsPreferenceFragment {

    private static final String BUTTONS_CATEGORY = "buttons_category";
    private static final String NAVIGATION_CATEGORY = "navigation_category";
    private static final String POWERMENU_CATEGORY = "powermenu_category";
    private static final String FINGERPRINT_PREFS_CATEGORY = "fingerprint_prefs_category";
    private static final String LOCKSCREEN_ITEMS_CATEGORY = "lockscreen_items_category";
    private static final String BATTERY_CATEGORY = "battery_options_category";
    private static final String CARRIER_LABEL_CATEGORY = "carrier_label_category";
    private static final String CLOCK_CATEGORY = "clock_options_category";
    private static final String ICON_MANAGER_CATEGORY = "icon_manager_title";
    private static final String QUICK_SETTINGS_CATEGORY = "quick_settings_category";
    private static final String TRAFFIC_CATEGORY = "traffic_category";
    private static final String NUSANTARA_PARTS_CATEGORY = "nusantara_parts_category";
    private static final String NOTIFICATIONS_CATEGORY = "notifications_category";
    private static final String MISC_CATEGORY = "miscellaneous_category";
    private static final String THEMES_CATEGORY = "themes_category";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.tab_cat);

        Preference Buttons = findPreference(BUTTONS_CATEGORY);
        if (!getResources().getBoolean(R.bool.has_buttons)) {
            getPreferenceScreen().removePreference(Buttons);
        }

        Preference Navigation = findPreference(NAVIGATION_CATEGORY);
        if (!getResources().getBoolean(R.bool.has_navigation)) {
            getPreferenceScreen().removePreference(Navigation);
        }

        Preference PowerMenu = findPreference(POWERMENU_CATEGORY);
        if (!getResources().getBoolean(R.bool.has_powermenu)) {
            getPreferenceScreen().removePreference(PowerMenu);
        }

        Preference LockscreenItems = findPreference(LOCKSCREEN_ITEMS_CATEGORY);
        if (!getResources().getBoolean(R.bool.has_lockscreen_items)) {
            getPreferenceScreen().removePreference(LockscreenItems);
        }

        Preference FingerprintPrefs = findPreference(FINGERPRINT_PREFS_CATEGORY);
        if (!getResources().getBoolean(R.bool.has_fingerprint_prefs)) {
            getPreferenceScreen().removePreference(FingerprintPrefs);
        } else {
            if (!NadUtils.hasFingerprintSupport(getContext())) {
                getPreferenceScreen().removePreference(FingerprintPrefs);
            }
        }

        Preference BatteryOptions = findPreference(BATTERY_CATEGORY);
        if (!getResources().getBoolean(R.bool.has_battery_options)) {
            getPreferenceScreen().removePreference(BatteryOptions);
        }

        Preference CarrierLabel = findPreference(CARRIER_LABEL_CATEGORY);
        if (!getResources().getBoolean(R.bool.has_carrier_label)) {
            getPreferenceScreen().removePreference(CarrierLabel);
        }

        Preference ClockOptions = findPreference(CLOCK_CATEGORY);
        if (!getResources().getBoolean(R.bool.has_clock_options)) {
            getPreferenceScreen().removePreference(ClockOptions);
        }

        Preference IconManager = findPreference(ICON_MANAGER_CATEGORY);
        if (!getResources().getBoolean(R.bool.has_icon_manager)) {
            getPreferenceScreen().removePreference(IconManager);
        }

        Preference QuickSettings = findPreference(QUICK_SETTINGS_CATEGORY);
        if (!getResources().getBoolean(R.bool.has_quick_settings)) {
            getPreferenceScreen().removePreference(QuickSettings);
        }

        Preference Traffic = findPreference(TRAFFIC_CATEGORY);
        if (!getResources().getBoolean(R.bool.has_traffic)) {
            getPreferenceScreen().removePreference(Traffic);
        }

        Preference NusantaraParts = findPreference(NUSANTARA_PARTS_CATEGORY);
        if (!getResources().getBoolean(R.bool.has_nusantara_parts_available)) {
            getPreferenceScreen().removePreference(NusantaraParts);
        }

        Preference Notifications = findPreference(NOTIFICATIONS_CATEGORY);
        if (!getResources().getBoolean(R.bool.has_notifications)) {
            getPreferenceScreen().removePreference(Notifications);
        }

        Preference MiscOptions = findPreference(MISC_CATEGORY);
        if (!getResources().getBoolean(R.bool.has_misc_options)) {
            getPreferenceScreen().removePreference(MiscOptions);
        }

        Preference Themes = findPreference(THEMES_CATEGORY);
        if (!getResources().getBoolean(R.bool.has_themes)) {
            getPreferenceScreen().removePreference(Themes);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.NUSANTARA_PRJ;
    }
}
