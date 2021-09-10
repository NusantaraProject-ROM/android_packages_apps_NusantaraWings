/*
 * Copyright (C) 2020 The exTHmUI Open Source Project
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

package com.nusantara.wings.fragments.system;

import com.android.internal.logging.nano.MetricsProto;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;

import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.util.ArrayList;

import com.nusantara.wings.preferences.PackageListPreference;

import com.nusantara.support.preferences.SystemSettingSwitchPreference;

import com.android.internal.util.nad.NadUtils;

import com.nusantara.support.preferences.SystemSettingSeekBarPreference;

public class GamingModeSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String GAMING_MODE_DISABLE_HW_KEYS = "gaming_mode_disable_hw_keys";
    private static final String GAMING_MODE_DISABLE_NAVBAR = "gaming_mode_disable_navbar";

    private PackageListPreference mGamingPrefList;
    private SwitchPreference mUseMenuSwitch;
    private Preference mDanmaku;
    private Preference mQapps;
    private SystemSettingSeekBarPreference mOpacity;

    private SystemSettingSwitchPreference mHardwareKeysDisable;
    private SystemSettingSwitchPreference mNavbarDisable;
    private boolean performance_supported;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.nad_settings_gaming);

        final PreferenceScreen prefScreen = getPreferenceScreen();

        final PreferenceCategory perfCat = (PreferenceCategory) prefScreen
                .findPreference("performance_category");

        performance_supported = getResources().getBoolean(
                    com.android.internal.R.bool.config_gamingmode_performance);

        if (!performance_supported) {
            prefScreen.removePreference(perfCat);
        }

        mUseMenuSwitch = (SwitchPreference) findPreference("gaming_mode_use_overlay_menu");
        mDanmaku = (Preference) findPreference("gaming_mode_notification_danmaku");
        mQapps = (Preference) findPreference("gaming_mode_quick_start_apps");
        mOpacity = (SystemSettingSeekBarPreference) findPreference("gaming_mode_menu_opacity");

        boolean menuEnabled = Settings.System.getInt(getContentResolver(),
                            Settings.System.GAMING_MODE_USE_OVERLAY_MENU, 1) == 1;
        mUseMenuSwitch.setChecked(menuEnabled);
        mUseMenuSwitch.setOnPreferenceChangeListener(this);

        mDanmaku.setEnabled(menuEnabled);
        mQapps.setEnabled(menuEnabled);
        mOpacity.setEnabled(menuEnabled);

        mGamingPrefList = (PackageListPreference) findPreference("gaming_mode_app_list");
        mGamingPrefList.setRemovedListKey(Settings.System.GAMING_MODE_REMOVED_APP_LIST);

        mHardwareKeysDisable = (SystemSettingSwitchPreference) findPreference(GAMING_MODE_DISABLE_HW_KEYS);
        mNavbarDisable = (SystemSettingSwitchPreference) findPreference(GAMING_MODE_DISABLE_NAVBAR);

        if (isNavbarVisible()) {
            prefScreen.removePreference(mHardwareKeysDisable);
        } else {
            prefScreen.removePreference(mNavbarDisable);
        }
    }

    private boolean isNavbarVisible() {
        boolean defaultToNavigationBar = NadUtils.deviceSupportNavigationBar(getActivity());
        return Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.FORCE_SHOW_NAVBAR, defaultToNavigationBar ? 1 : 0) == 1;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mUseMenuSwitch) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver, Settings.System.GAMING_MODE_USE_OVERLAY_MENU,
                    value ? 1 : 0);
            mDanmaku.setEnabled(value);
            mQapps.setEnabled(value);
            mOpacity.setEnabled(value);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.NUSANTARA_PRJ;
    }
}
