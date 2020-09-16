/*
 * Copyright (C) 2017-2019 The Dirty Unicorns Project
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

import static com.nusantara.wings.UtilsThemes.handleBackgrounds;
import static com.nusantara.wings.UtilsThemes.handleOverlays;

import android.app.UiModeManager;
import android.content.Context;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.om.IOverlayManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.ServiceManager;
import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import com.android.internal.logging.nano.MetricsProto;

import com.android.internal.util.nad.ThemesUtils;
import com.android.internal.util.nad.NadUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.search.SearchIndexable;
import com.android.settings.search.BaseSearchIndexProvider;

@SearchIndexable
public class Themes extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String PREF_THEME_SWITCH = "theme_switch";
    private static final String PREF_THEME_ACCENT_PICKER = "theme_accent_picker";

    private Context mContext;
    private IOverlayManager mOverlayManager;
    private UiModeManager mUiModeManager;

    private ListPreference mThemeSwitch;
    private ListPreference mAccentPicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.nad_themes);
        final PreferenceScreen prefScreen = getPreferenceScreen();
        mContext = getActivity();

        // Theme services
        mUiModeManager = getContext().getSystemService(UiModeManager.class);
        mOverlayManager = IOverlayManager.Stub.asInterface(
                ServiceManager.getService(Context.OVERLAY_SERVICE));

        // Themes
        mThemeSwitch = (ListPreference) findPreference(PREF_THEME_SWITCH);
        // First of all we have to evaluate whether the light or dark mode is active
        if (mUiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_NO) {
            mThemeSwitch.setValue("1");
        } else if (NadUtils.isThemeEnabled("com.android.theme.solarizeddark.system")) {
            mThemeSwitch.setValue("4");
        } else if (NadUtils.isThemeEnabled("com.android.theme.pitchblack.system")) {
            mThemeSwitch.setValue("3");
        } else { // Google dark theme
            mThemeSwitch.setValue("2");
        }

        mThemeSwitch.setSummary(mThemeSwitch.getEntry());
        mThemeSwitch.setOnPreferenceChangeListener(this);

        mAccentPicker = (ListPreference) findPreference(PREF_THEME_ACCENT_PICKER);
        int accentPickerValues = getOverlayPosition(ThemesUtils.ACCENTS);
        if (accentPickerValues != -1) {
            mAccentPicker.setValue(String.valueOf(accentPickerValues + 2));
        } else {
            mAccentPicker.setValue("1");
        }
        mAccentPicker.setSummary(mAccentPicker.getEntry());
        mAccentPicker.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mThemeSwitch) {
            String themeSwitch = (String) newValue;
                switch (themeSwitch) {
                    case "1":
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_NO,
                                ThemesUtils.PITCH_BLACK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_NO,
                                ThemesUtils.SOLARIZED_DARK, mOverlayManager);
                        break;
                    case "2":
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.PITCH_BLACK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.SOLARIZED_DARK, mOverlayManager);
                        break;
                    case "3":
                        handleBackgrounds(true, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.PITCH_BLACK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.SOLARIZED_DARK, mOverlayManager);
                        break;
                    case "4":
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.PITCH_BLACK, mOverlayManager);
                        handleBackgrounds(true, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.SOLARIZED_DARK, mOverlayManager);
                        break;
            }
            mThemeSwitch.setSummary(mThemeSwitch.getEntry());
            return true;
        } else if (preference == mAccentPicker) {
            String accentStyle = (String) newValue;
            int accentPickerValue = Integer.parseInt(accentStyle);
            mAccentPicker.setValue(String.valueOf(accentPickerValue));
            String overlayName = getOverlayName(ThemesUtils.ACCENTS);
                if (overlayName != null) {
                    handleOverlays(overlayName, false, mOverlayManager);
                }
                if (accentPickerValue > 1) {
                    handleOverlays(ThemesUtils.ACCENTS[accentPickerValue - 2],
                            true, mOverlayManager);
            }
            mAccentPicker.setSummary(mAccentPicker.getEntry());
            return true;
        }
        return false;
    }

    private int getOverlayPosition(String[] overlays) {
        int position = -1;
        for (int i = 0; i < overlays.length; i++) {
            String overlay = overlays[i];
            if (NadUtils.isThemeEnabled(overlay)) {
                position = i;
            }
        }
        return position;
    }

    private String getOverlayName(String[] overlays) {
        String overlayName = null;
        for (int i = 0; i < overlays.length; i++) {
            String overlay = overlays[i];
            if (NadUtils.isThemeEnabled(overlay)) {
                overlayName = overlay;
            }
        }
        return overlayName;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.NUSANTARA_PRJ;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.nad_themes);
}
