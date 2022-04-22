/*
 * Copyright (C) 2022 The Nusantara Project
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

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import com.nusantara.wings.UtilsNad;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.logging.nano.MetricsProto;

import com.nusantara.support.preferences.SystemSettingSeekBarPreference;
import com.nusantara.support.preferences.SystemSettingSwitchPreference;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import java.util.ArrayList;
import java.util.List;
import com.android.settings.R;
import androidx.preference.SeekBarPreference;

public class BlurredSettings extends SettingsPreferenceFragment
implements Preference.OnPreferenceChangeListener {

    private static final String KEY_BLUR_STYLE = "blur_style";
    private static final String KEY_BLUR_RADIUS = "blur_radius";
    private static final String KEY_BLUR_SCALE_PREFERENCE = "blur_scale";
    private static final String KEY_BLUR_COMBINED = "combined_blur";

    private SystemSettingSwitchPreference mBlurStyle;
    private SystemSettingSeekBarPreference mBlurRadius;
    private SystemSettingSeekBarPreference mBlurScale;
    private SystemSettingSwitchPreference mCombinedBlur;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getContext();
        String name = ctx.getPackageName();

        addPreferencesFromResource(getResources().getIdentifier("blur_settings", "xml", name));

        PreferenceScreen prefSet = getPreferenceScreen();

        ContentResolver resolver = ctx.getContentResolver();

        SystemSettingSwitchPreference blurStyle = mBlurStyle = prefSet.findPreference(KEY_BLUR_STYLE);
        blurStyle.setOnPreferenceChangeListener(this);
        boolean blurStyleEnable  =  Settings.System.getIntForUser(resolver, KEY_BLUR_STYLE, 0, UserHandle.USER_CURRENT) != 0;
        blurStyle.setChecked(blurStyleEnable);

        SystemSettingSeekBarPreference blurRadius = mBlurRadius = prefSet.findPreference(KEY_BLUR_RADIUS);
        blurRadius.setOnPreferenceChangeListener(this);
        int blurRadiusValue  =  Settings.System.getInt(resolver, KEY_BLUR_RADIUS, 5);
        blurRadius.setValue(blurRadiusValue);
        blurRadius.setEnabled(blurStyleEnable);

        SystemSettingSeekBarPreference blurScale = mBlurScale = prefSet.findPreference(KEY_BLUR_SCALE_PREFERENCE);
        blurScale.setOnPreferenceChangeListener(this);
        int blurScaleValue  =  Settings.System.getInt(resolver, KEY_BLUR_SCALE_PREFERENCE, 10);
        blurScale.setValue(blurScaleValue);
        blurScale.setEnabled(blurStyleEnable);

        SystemSettingSwitchPreference combinedBlur = mCombinedBlur = prefSet.findPreference(KEY_BLUR_COMBINED);
        combinedBlur.setOnPreferenceChangeListener(this);
        boolean combinedBlurEnable  =  Settings.System.getIntForUser(resolver, KEY_BLUR_COMBINED, 0, UserHandle.USER_CURRENT) != 0;
        combinedBlur.setChecked(combinedBlurEnable);
        combinedBlur.setEnabled(blurStyleEnable);

        updatePrefAll();
    }

    protected void updatePrefAll() {
        Context ctx = getContext();
        ContentResolver resolver = ctx.getContentResolver();

        boolean blurEnable  =  Settings.System.getIntForUser(resolver, KEY_BLUR_STYLE, 0, UserHandle.USER_CURRENT) != 0;
        
        mBlurRadius.setEnabled(blurEnable);
        mBlurScale.setEnabled(blurEnable);
        mCombinedBlur.setEnabled(blurEnable);
    }
    
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getContext().getContentResolver();
        if (preference == mBlurStyle) {
            boolean value = (Boolean) newValue;
            Settings.System.putIntForUser(resolver,
                KEY_BLUR_STYLE, value ? 1 : 0, UserHandle.USER_CURRENT);
            UtilsNad.showSettingsRestartDialog(getContext());
            updatePrefAll();
            return true;
        } else if (preference == mBlurRadius) {
            int value = (Integer) newValue;
            Settings.System.putInt(resolver,
                KEY_BLUR_RADIUS, value);
            updatePrefAll();
            return true;
        } else if (preference == mBlurScale) {
            int value = (Integer) newValue;
            Settings.System.putInt(resolver,
                KEY_BLUR_SCALE_PREFERENCE, value);
            updatePrefAll();
            return true;
        } else if (preference == mCombinedBlur) {
            boolean value = (Boolean) newValue;
            Settings.System.putIntForUser(resolver,
                KEY_BLUR_COMBINED, value ? 1 : 0, UserHandle.USER_CURRENT);
            UtilsNad.showSettingsRestartDialog(getContext());
            updatePrefAll();
            return true;
        }
        return false;
    }

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
                    sir.xmlResId = R.xml.blur_settings;
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

