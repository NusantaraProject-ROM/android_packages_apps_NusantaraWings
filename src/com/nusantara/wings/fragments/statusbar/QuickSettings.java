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

package com.nusantara.wings.fragments.statusbar;

import android.content.Context;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.search.SearchIndexable;

import com.nusantara.support.preferences.SystemSettingEditTextPreference;
import com.nusantara.support.preferences.SystemSettingMasterSwitchPreference;
import com.nusantara.support.preferences.SystemSettingListPreference;
import com.nusantara.support.preferences.SystemSettingSwitchPreference;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class QuickSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String FOOTER_TEXT_STRING = "footer_text_string";
    private static final String STATUS_BAR_CUSTOM_HEADER = "status_bar_custom_header";

    private SystemSettingEditTextPreference mFooterString;
    private SystemSettingMasterSwitchPreference mCustomHeader;
    private SystemSettingListPreference mDataUsage;
    private SystemSettingSwitchPreference mDataUsageLoc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.nad_quick_settings);

        final ContentResolver resolver = getActivity().getContentResolver();

        mFooterString = (SystemSettingEditTextPreference) findPreference(FOOTER_TEXT_STRING);
        mFooterString.setOnPreferenceChangeListener(this);
        String footerString = Settings.System.getString(getContentResolver(),
                FOOTER_TEXT_STRING);
        if (footerString != null && footerString != "")
            mFooterString.setText(footerString);
        else {
            mFooterString.setText("#Nusantara Project");
            Settings.System.putString(getActivity().getContentResolver(),
                    Settings.System.FOOTER_TEXT_STRING, "#Nusantara Project");
        }

        mCustomHeader = (SystemSettingMasterSwitchPreference) findPreference(STATUS_BAR_CUSTOM_HEADER);
        int qsHeader = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_CUSTOM_HEADER, 0);
        mCustomHeader.setChecked(qsHeader != 0);
        mCustomHeader.setOnPreferenceChangeListener(this);

        mDataUsage = (SystemSettingListPreference) findPreference("qs_datausage");
        int usage = Settings.System.getIntForUser(resolver,
                Settings.System.QS_DATAUSAGE, 0, UserHandle.USER_CURRENT);
        mDataUsage.setValue(String.valueOf(usage));
        mDataUsage.setOnPreferenceChangeListener(this);

        mDataUsageLoc = (SystemSettingSwitchPreference) findPreference("qs_datausage_location");
        mDataUsageLoc.setChecked((Settings.System.getInt(resolver,
                Settings.System.QS_DATAUSAGE_LOCATION, 0) == 1));
        mDataUsageLoc.setOnPreferenceChangeListener(this);

        updateDataUsage(usage);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mFooterString) {
            String value = (String) newValue;
            if (value != "" && value != null)
                Settings.System.putString(resolver,
                        Settings.System.FOOTER_TEXT_STRING, value);
            else {
                mFooterString.setText("#Nusantara Project");
                Settings.System.putString(resolver,
                        Settings.System.FOOTER_TEXT_STRING, "#Nusantara Project");
            }
            return true;
        } else if (preference == mCustomHeader) {
            boolean header = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_CUSTOM_HEADER, header ? 1 : 0);
            return true;
        } else if (preference == mDataUsage) {
            int datausage = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.QS_DATAUSAGE, datausage, UserHandle.USER_CURRENT);
            updateDataUsage(datausage);
            return true;
        } else if (preference == mDataUsageLoc) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.QS_DATAUSAGE_LOCATION, value ? 0 : 1);
            return true;
        }
        return false;
    }

    public void updateDataUsage(int usage) {
        switch(usage){
            case 0:
                mDataUsageLoc.setEnabled(false);
                break;
            case 1:
            case 2:
                mDataUsageLoc.setEnabled(true);
                break;
            default:
                break;
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
                    sir.xmlResId = R.xml.nad_quick_settings;
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
