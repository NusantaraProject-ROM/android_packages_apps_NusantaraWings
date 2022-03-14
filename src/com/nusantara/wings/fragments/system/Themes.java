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

import android.app.Dialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.om.IOverlayManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import com.nusantara.support.colorpicker.ColorPickerPreference;
import com.nusantara.support.preferences.SystemSettingListPreference;
import com.nusantara.support.preferences.SystemSettingSwitchPreference;
import com.nusantara.wings.UtilsNad;

import java.util.ArrayList;
import java.util.List;

import static com.nusantara.wings.UtilsThemes.handleBackgrounds;
import static com.nusantara.wings.UtilsThemes.handleOverlays;


@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class Themes extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private String MONET_ENGINE_COLOR_OVERRIDE = "monet_engine_color_override";

    private ColorPickerPreference mMonetColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.nad_themes);
        final PreferenceScreen prefScreen = getPreferenceScreen();
        final ContentResolver resolver = getActivity().getContentResolver();

        mMonetColor = (ColorPickerPreference) prefScreen.findPreference(MONET_ENGINE_COLOR_OVERRIDE);
        int intColor = Settings.Secure.getInt(resolver, MONET_ENGINE_COLOR_OVERRIDE, Color.WHITE);
        String hexColor = String.format("#%08x", (0xffffff & intColor));
        mMonetColor.setNewPreviewColor(intColor);
        mMonetColor.setSummary(hexColor);
        mMonetColor.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mMonetColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                .parseInt(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.Secure.putInt(resolver,
                MONET_ENGINE_COLOR_OVERRIDE, intHex);
            return true;
        }
        return false;
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
                    sir.xmlResId = R.xml.nad_themes;
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
