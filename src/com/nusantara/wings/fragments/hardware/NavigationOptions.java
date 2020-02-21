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
import com.nusantara.support.preferences.SystemSettingListPreference;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class NavigationOptions extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String KEY_NAVIGATION_BAR_ENABLED = "force_show_navbar";
    private static final String KEY_SWAP_NAVIGATION_KEYS = "swap_navigation_keys";

    private SwitchPreference mNavigationBar;
    private SystemSettingSwitchPreference mSwapHardwareKeys;

    private int deviceKeys;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.nad_navigation_options);

        final PreferenceScreen prefSet = getPreferenceScreen();
        final ContentResolver resolver = getActivity().getContentResolver();
        final boolean defaultToNavigationBar = getResources().getBoolean(
                com.android.internal.R.bool.config_showNavigationBar);
        final boolean navigationBarEnabled = Settings.System.getIntForUser(
                resolver, Settings.System.FORCE_SHOW_NAVBAR,
                defaultToNavigationBar ? 1 : 0, UserHandle.USER_CURRENT) != 0;

        deviceKeys = getResources().getInteger(
                com.android.internal.R.integer.config_deviceHardwareKeys);

        mNavigationBar = (SwitchPreference) findPreference(KEY_NAVIGATION_BAR_ENABLED);
        mNavigationBar.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.FORCE_SHOW_NAVBAR,
                defaultToNavigationBar ? 1 : 0) == 1));
        mNavigationBar.setOnPreferenceChangeListener(this);

        mSwapHardwareKeys = (SystemSettingSwitchPreference) findPreference(KEY_SWAP_NAVIGATION_KEYS);

        if (deviceKeys == 0) {
            prefSet.removePreference(mSwapHardwareKeys);
        }
        updateOptions();
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mNavigationBar) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(resolver,
                    Settings.System.FORCE_SHOW_NAVBAR, value ? 1 : 0);
            updateOptions();
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateOptions();
    }

    @Override
    public void onPause() {
        super.onPause();
        updateOptions();
    }

    private void updateOptions() {
        final ContentResolver resolver = getActivity().getContentResolver();
        boolean defaultToNavigationBar = getResources().getBoolean(
                com.android.internal.R.bool.config_showNavigationBar);
        boolean navigationBarEnabled = Settings.System.getIntForUser(
                resolver, Settings.System.FORCE_SHOW_NAVBAR,
                defaultToNavigationBar ? 1 : 0, UserHandle.USER_CURRENT) != 0;

        if (navigationBarEnabled) {
            mSwapHardwareKeys.setEnabled(false);
        } else {
            mSwapHardwareKeys.setEnabled(true);
        }
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
