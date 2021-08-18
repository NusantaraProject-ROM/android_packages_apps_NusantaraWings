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

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;

import androidx.preference.SwitchPreference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.util.ArrayList;

import com.nusantara.wings.preferences.PackageListPreference;

import com.nusantara.support.preferences.SystemSettingSwitchPreference;

public class GamingModeSettings extends SettingsPreferenceFragment {

    private static final String GAMING_MODE_DISABLE_HW_KEYS = "gaming_mode_disable_hw_keys";

    private static final int KEY_MASK_HOME = 0x01;
    private static final int KEY_MASK_BACK = 0x02;
    private static final int KEY_MASK_MENU = 0x04;
    private static final int KEY_MASK_APP_SWITCH = 0x10;

    private PackageListPreference mGamingPrefList;

    private SystemSettingSwitchPreference mHardwareKeysDisable;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.nad_settings_gaming);

        final PreferenceScreen prefScreen = getPreferenceScreen();

        mGamingPrefList = (PackageListPreference) findPreference("gaming_mode_app_list");
        mGamingPrefList.setRemovedListKey(Settings.System.GAMING_MODE_REMOVED_APP_LIST);

        mHardwareKeysDisable = (SystemSettingSwitchPreference) findPreference(GAMING_MODE_DISABLE_HW_KEYS);

        if (!hasHWkeys()) {
            prefScreen.removePreference(mHardwareKeysDisable);
        }
    }

    private boolean hasHWkeys() {
        final int deviceKeys = getContext().getResources().getInteger(
                com.android.internal.R.integer.config_deviceHardwareKeys);

        // read bits for present hardware keys
        final boolean hasHomeKey = (deviceKeys & KEY_MASK_HOME) != 0;
        final boolean hasBackKey = (deviceKeys & KEY_MASK_BACK) != 0;
        final boolean hasMenuKey = (deviceKeys & KEY_MASK_MENU) != 0;
        final boolean hasAppSwitchKey = (deviceKeys & KEY_MASK_APP_SWITCH) != 0;

        return (hasHomeKey || hasBackKey || hasMenuKey || hasAppSwitchKey);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.NUSANTARA_PRJ;
    }
}
