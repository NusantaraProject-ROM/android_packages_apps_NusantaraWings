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
import static com.nusantara.wings.UtilsThemes.threeButtonNavbarEnabled;

import android.app.UiModeManager;
import android.content.Context;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.om.IOverlayManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.ServiceManager;
import android.provider.Settings;
import android.provider.SearchIndexableResource;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.internal.util.nad.ThemesUtils;
import com.android.internal.util.nad.NadUtils;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.search.SearchIndexable;
import com.android.settings.search.BaseSearchIndexProvider;

import java.util.ArrayList;
import java.util.List;

import com.nusantara.support.colorpicker.ColorPickerPreference;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class Themes extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String PREF_THEME_SWITCH = "theme_switch";
    private static final String PREF_THEME_ACCENT_PICKER = "theme_accent_picker";
    private static final String PREF_ADAPTIVE_ICON_SHAPE = "adapative_icon_shape";
    private static final String PREF_STATUSBAR_ICONS = "statusbar_icons";
    private static final String PREF_FONT_PICKER = "font_picker";
    private static final String PREF_NAVBAR_STYLE = "theme_navbar_style";
    private static final String ACCENT_COLOR = "accent_color";
    private static final String PREF_QS_HEADER_STYLE = "qs_header_style";
    static final int DEFAULT_ACCENT_COLOR = 0xff1a73e8;

    private Context mContext;
    private IOverlayManager mOverlayManager;
    private UiModeManager mUiModeManager;

    private ListPreference mThemeSwitch;
    private ListPreference mAccentPicker;
    private ListPreference mAdaptiveIconShape;
    private ListPreference mStatusbarIcons;
    private ListPreference mFontPicker;
    private ListPreference mNavbarPicker;
    private ListPreference mQsHeaderStyle;
    private ColorPickerPreference mAccentColor;

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
        } else if (NadUtils.isThemeEnabled("com.android.theme.materialocean.system")) {
            mThemeSwitch.setValue("6");
        } else if (NadUtils.isThemeEnabled("com.android.theme.nadclear.system")) {
            mThemeSwitch.setValue("5");
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

        // Statusbar icons
        mStatusbarIcons = (ListPreference) findPreference(PREF_STATUSBAR_ICONS);
        int sbIconsValue = getOverlayPosition(ThemesUtils.STATUSBAR_ICONS);

        if (sbIconsValue != -1) {
            mStatusbarIcons.setValue(String.valueOf(sbIconsValue + 2));
        } else {
            mStatusbarIcons.setValue("1");
        }
        mStatusbarIcons.setSummary(mStatusbarIcons.getEntry());
        mStatusbarIcons.setOnPreferenceChangeListener(this);

        mAdaptiveIconShape = (ListPreference) findPreference(PREF_ADAPTIVE_ICON_SHAPE);
        int iconShapeValue = getOverlayPosition(ThemesUtils.ADAPTIVE_ICON_SHAPE);
        if (iconShapeValue != -1) {
            mAdaptiveIconShape.setValue(String.valueOf(iconShapeValue + 2));
        } else {
            mAdaptiveIconShape.setValue("1");
        }
        mAdaptiveIconShape.setSummary(mAdaptiveIconShape.getEntry());
        mAdaptiveIconShape.setOnPreferenceChangeListener(this);

        // Font picker
        mFontPicker = (ListPreference) findPreference(PREF_FONT_PICKER);
        int fontPickerValue = getOverlayPosition(ThemesUtils.FONTS);
        if (fontPickerValue != -1) {
            mFontPicker.setValue(String.valueOf(fontPickerValue + 2));
        } else {
            mFontPicker.setValue("1");
        }
        mFontPicker.setSummary(mFontPicker.getEntry());
        mFontPicker.setOnPreferenceChangeListener(this);

        mNavbarPicker = (ListPreference) findPreference(PREF_NAVBAR_STYLE);
        if (threeButtonNavbarEnabled(mContext)) {
            int navbarStyleValues = getOverlayPosition(ThemesUtils.NAVBAR_STYLES);
            if (navbarStyleValues != -1) {
                mNavbarPicker.setValue(String.valueOf(navbarStyleValues + 2));
            } else {
                mNavbarPicker.setValue("1");
            }
            mNavbarPicker.setSummary(mNavbarPicker.getEntry());
            mNavbarPicker.setOnPreferenceChangeListener(this);
        } else {
            prefScreen.removePreference(mNavbarPicker);
        }

        mAccentColor = (ColorPickerPreference) findPreference(ACCENT_COLOR);
        int intColor = Settings.System.getIntForUser(mContext.getContentResolver(),
                Settings.System.ACCENT_COLOR, DEFAULT_ACCENT_COLOR, UserHandle.USER_CURRENT);
        String hexColor = String.format("#%08x", (0xff1a73e8 & intColor));
        if (hexColor.equals("#ff1a73e8")) {
            mAccentColor.setSummary(R.string.accent_color_default);
            mAccentPicker.setEnabled(true);
        } else {
            mAccentColor.setSummary(hexColor);
            mAccentPicker.setEnabled(false);
        }
        mAccentColor.setNewPreviewColor(intColor);
        mAccentColor.setOnPreferenceChangeListener(this);
        if (hexColor.equals("#ff1a73e8"))
            mAccentColor.setCustomColorPreview(getContext().getResources()
                    .getColor(R.color.nusantara_wings_category_icon_tint));

        mQsHeaderStyle = (ListPreference) findPreference(PREF_QS_HEADER_STYLE);
        int qsStyleValue = getOverlayPosition(ThemesUtils.QS_HEADER_THEMES);
        if (qsStyleValue != -1) {
            mQsHeaderStyle.setValue(String.valueOf(qsStyleValue + 2));
        } else {
            mQsHeaderStyle.setValue("1");
        }
        mQsHeaderStyle.setSummary(mQsHeaderStyle.getEntry());
        mQsHeaderStyle.setOnPreferenceChangeListener(this);
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
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_NO,
                                ThemesUtils.NAD_CLEAR, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_NO,
                                ThemesUtils.MATERIAL_OCEAN, mOverlayManager);
                        break;
                    case "2":
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.PITCH_BLACK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.SOLARIZED_DARK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.NAD_CLEAR, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.MATERIAL_OCEAN, mOverlayManager);
                        break;
                    case "3":
                        handleBackgrounds(true, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.PITCH_BLACK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.SOLARIZED_DARK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.NAD_CLEAR, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.MATERIAL_OCEAN, mOverlayManager);
                        break;
                    case "4":
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.PITCH_BLACK, mOverlayManager);
                        handleBackgrounds(true, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.SOLARIZED_DARK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.NAD_CLEAR, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.MATERIAL_OCEAN, mOverlayManager);
                        break;
                    case "5":
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.PITCH_BLACK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.SOLARIZED_DARK, mOverlayManager);
                        handleBackgrounds(true, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.NAD_CLEAR, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.MATERIAL_OCEAN, mOverlayManager);
                        break;
                    case "6":
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.PITCH_BLACK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.SOLARIZED_DARK, mOverlayManager);
                        handleBackgrounds(false, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.NAD_CLEAR, mOverlayManager);
                        handleBackgrounds(true, mContext, UiModeManager.MODE_NIGHT_YES,
                                ThemesUtils.MATERIAL_OCEAN, mOverlayManager);
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
        } else if (preference == mStatusbarIcons) {
            String statusbarIcons = (String) newValue;
            String overlayName = getOverlayName(ThemesUtils.STATUSBAR_ICONS);
            int statusbarIconsValue = Integer.parseInt(statusbarIcons);
            mStatusbarIcons.setValue(String.valueOf(statusbarIconsValue));
                if (overlayName != null) {
                    handleOverlays(overlayName, false, mOverlayManager);
                }
                if (statusbarIconsValue > 1) {
                    handleOverlays(ThemesUtils.STATUSBAR_ICONS[statusbarIconsValue - 2],
                            true, mOverlayManager);
            }
            mStatusbarIcons.setSummary(mStatusbarIcons.getEntry());
            return true;
        } else if (preference == mAdaptiveIconShape) {
            String adapativeIconShape = (String) newValue;
            String overlayName = getOverlayName(ThemesUtils.ADAPTIVE_ICON_SHAPE);
            int adapativeIconShapeValue = Integer.parseInt(adapativeIconShape);
            mAdaptiveIconShape.setValue(String.valueOf(adapativeIconShapeValue));
                if (overlayName != null) {
                    handleOverlays(overlayName, false, mOverlayManager);
                }
                if (adapativeIconShapeValue > 1) {
                    handleOverlays(ThemesUtils.ADAPTIVE_ICON_SHAPE[adapativeIconShapeValue - 2],
                            true, mOverlayManager);
            }
            mAdaptiveIconShape.setSummary(mAdaptiveIconShape.getEntry());
            return true;
        } else if (preference == mFontPicker) {
            String font = (String) newValue;
            int fontTypeValue = Integer.parseInt(font);
            mFontPicker.setValue(String.valueOf(fontTypeValue));
            String overlayName = getOverlayName(ThemesUtils.FONTS);
            if (overlayName != null) {
                handleOverlays(overlayName, false, mOverlayManager);
            }
            if (fontTypeValue > 1) {
                handleOverlays(ThemesUtils.FONTS[fontTypeValue - 2],
                        true, mOverlayManager);
            }
            mFontPicker.setSummary(mFontPicker.getEntry());
            return true;
        } else if (preference == mNavbarPicker) {
            String navbarStyle = (String) newValue;
            int navbarStyleValue = Integer.parseInt(navbarStyle);
            mNavbarPicker.setValue(String.valueOf(navbarStyleValue));
            String overlayName = getOverlayName(ThemesUtils.NAVBAR_STYLES);
                if (overlayName != null) {
                    handleOverlays(overlayName, false, mOverlayManager);
                }
                if (navbarStyleValue > 1) {
                    handleOverlays(ThemesUtils.NAVBAR_STYLES[navbarStyleValue - 2],
                            true, mOverlayManager);
            }
            mNavbarPicker.setSummary(mNavbarPicker.getEntry());
            return true;
        } else if (preference == mAccentColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#ff1a73e8")) {
                mAccentColor.setSummary(R.string.accent_color_default);
                mAccentPicker.setEnabled(true);
            } else {
                mAccentColor.setSummary(hex);
                mAccentPicker.setEnabled(false);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putIntForUser(mContext.getContentResolver(),
                    Settings.System.ACCENT_COLOR, intHex, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mQsHeaderStyle) {
            String qsHeaderStyle = (String) newValue;
            int qsHeaderStyleValue = Integer.parseInt(qsHeaderStyle);
            mQsHeaderStyle.setValue(String.valueOf(qsHeaderStyleValue));
            String overlayName = getOverlayName(ThemesUtils.QS_HEADER_THEMES);
                if (overlayName != null) {
                    handleOverlays(overlayName, false, mOverlayManager);
                }
                if (qsHeaderStyleValue > 1) {
                    handleOverlays(ThemesUtils.QS_HEADER_THEMES[qsHeaderStyleValue -2],
                            true, mOverlayManager);
            }
            mQsHeaderStyle.setSummary(mQsHeaderStyle.getEntry());
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
