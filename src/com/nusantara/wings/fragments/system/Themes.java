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
import android.content.FontInfo;
import android.content.IFontService;
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
import com.android.internal.util.nad.NadUtils;
import com.android.internal.util.nad.ThemesUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.display.FontDialogPreference;
import com.android.settingslib.search.SearchIndexable;
import com.nusantara.support.colorpicker.ColorPickerPreference;
import com.nusantara.support.preferences.SystemSettingListPreference;
import com.nusantara.wings.UtilsNad;

import java.util.ArrayList;
import java.util.List;

import static com.nusantara.wings.UtilsThemes.handleBackgrounds;
import static com.nusantara.wings.UtilsThemes.handleOverlays;
import static com.nusantara.wings.UtilsThemes.threeButtonNavbarEnabled;


@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class Themes extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String PREF_THEME_SWITCH = "theme_switch";
    private static final String PREF_ADAPTIVE_ICON_SHAPE = "adapative_icon_shape";
    private static final String PREF_STATUSBAR_ICONS = "statusbar_icons";
    private static final String PREF_NAVBAR_STYLE = "theme_navbar_style";
    private static final String PREF_QS_HEADER_STYLE = "qs_header_style";
    private static final String PREF_ROUNDED_CORNER = "rounded_ui";
    private static final String PREF_SB_HEIGHT = "statusbar_height";
    private static final String BRIGHTNESS_SLIDER_STYLE = "brightness_slider_style";
    private static final String PREF_PANEL_BG = "panel_bg";
    private static final String PREF_QS_SHAPE = "qs_shape";
    private static final String PREF_SWITCH_STYLE = "switch_style";
    private static final String PREF_SETTINGS_THEMES = "themes_settings";
    private static final String PREF_RGB_ACCENT_PICKER = "rgb_accent_picker";
    private static final String PREF_SIGNAL_ICON = "signal_icon";
    private static final String PREF_WIFI_ICON = "wifi_icon";
    private static final String KEY_FONT_PICKER_FRAGMENT_PREF = "custom_font";
    private static final String PREF_SETTINGS_ICONS = "theming_settings_dashboard_icons";

    private Context mContext;
    private IOverlayManager mOverlayManager;
    private UiModeManager mUiModeManager;

    private ColorPickerPreference rgbAccentPicker;
    private ListPreference mThemeSwitch;
    private ListPreference mAccentPicker;
    private ListPreference mAdaptiveIconShape;
    private ListPreference mStatusbarIcons;
    private ListPreference mNavbarPicker;
    private ListPreference mQsHeaderStyle;
    private ListPreference mRoundedUi;
    private ListPreference mSbHeight;
    private ListPreference mBrightnessSliderStyle;
    private ListPreference mPanelBg;
    private ListPreference mQsShape;
    private ListPreference mSwitchStyle;
    private ListPreference mSignalIcon;
    private ListPreference mWifiIcon;
    private Preference mThemesSettings;
    private FontDialogPreference mFontPreference;
    private SystemSettingListPreference mDashboardIcons;

    IFontService mFontService = IFontService.Stub.asInterface(ServiceManager.getService("dufont"));

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
        } else if (NadUtils.isThemeEnabled("com.android.theme.color.ocean.system")) {
            mThemeSwitch.setValue("6");
        } else if (NadUtils.isThemeEnabled("com.android.theme.color.nadclear.system")) {
            mThemeSwitch.setValue("5");
        } else if (NadUtils.isThemeEnabled("com.android.theme.color.solarized.system")) {
            mThemeSwitch.setValue("4");
        } else if (NadUtils.isThemeEnabled("com.android.theme.color.pitchblack.system")) {
            mThemeSwitch.setValue("3");
        } else { // Google dark theme
            mThemeSwitch.setValue("2");
        }

        mThemeSwitch.setSummary(mThemeSwitch.getEntry());
        mThemeSwitch.setOnPreferenceChangeListener(this);

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

        mQsHeaderStyle = (ListPreference) findPreference(PREF_QS_HEADER_STYLE);
        int qsStyleValue = getOverlayPosition(ThemesUtils.QS_HEADER_THEMES);
        if (qsStyleValue != -1) {
            mQsHeaderStyle.setValue(String.valueOf(qsStyleValue + 2));
        } else {
            mQsHeaderStyle.setValue("1");
        }
        mQsHeaderStyle.setSummary(mQsHeaderStyle.getEntry());
        mQsHeaderStyle.setOnPreferenceChangeListener(this);

        mRoundedUi = (ListPreference) findPreference(PREF_ROUNDED_CORNER);
        int roundedValue = getOverlayPosition(ThemesUtils.UI_RADIUS);
        if (roundedValue != -1) {
            mRoundedUi.setValue(String.valueOf(roundedValue + 2));
        } else {
            mRoundedUi.setValue("1");
        }
        mRoundedUi.setSummary(mRoundedUi.getEntry());
        mRoundedUi.setOnPreferenceChangeListener(this);

        mSbHeight = (ListPreference) findPreference(PREF_SB_HEIGHT);
        int sbHeightValue = getOverlayPosition(ThemesUtils.STATUSBAR_HEIGHT);
        if (sbHeightValue != -1) {
            mSbHeight.setValue(String.valueOf(sbHeightValue + 2));
        } else {
            mSbHeight.setValue("1");
        }
        mSbHeight.setSummary(mSbHeight.getEntry());
        mSbHeight.setOnPreferenceChangeListener(this);

        mBrightnessSliderStyle = (ListPreference) findPreference(BRIGHTNESS_SLIDER_STYLE);
        int brightnessSliderValue = getOverlayPosition(ThemesUtils.BRIGHTNESS_SLIDER_THEMES);
        if (brightnessSliderValue != -1) {
            mBrightnessSliderStyle.setValue(String.valueOf(brightnessSliderValue + 2));
        } else {
            mBrightnessSliderStyle.setValue("1");
        }
        mBrightnessSliderStyle.setSummary(mBrightnessSliderStyle.getEntry());
        mBrightnessSliderStyle.setOnPreferenceChangeListener(this);


        mPanelBg = (ListPreference) findPreference(PREF_PANEL_BG);
        int mPanelValue = getOverlayPosition(ThemesUtils.PANEL_BG_STYLE);
        if (mPanelValue != -1) {
            mPanelBg.setValue(String.valueOf(mPanelValue + 2));
        } else {
            mPanelBg.setValue("1");
        }
        mPanelBg.setSummary(mPanelBg.getEntry());
        mPanelBg.setOnPreferenceChangeListener(this);

        mSignalIcon = (ListPreference) findPreference(PREF_SIGNAL_ICON);
        int mSignalIconValue = getOverlayPosition(ThemesUtils.SIGNAL_BAR);
        if (mSignalIconValue != -1) {
            mSignalIcon.setValue(String.valueOf(mSignalIconValue + 2));
        } else {
            mSignalIcon.setValue("1");
        }
        mSignalIcon.setSummary(mSignalIcon.getEntry());
        mSignalIcon.setOnPreferenceChangeListener(this);

        mWifiIcon = (ListPreference) findPreference(PREF_WIFI_ICON);
        int mWifiIconValue = getOverlayPosition(ThemesUtils.WIFI_BAR);
        if (mWifiIconValue != -1) {
            mWifiIcon.setValue(String.valueOf(mWifiIconValue + 2));
        } else {
            mWifiIcon.setValue("1");
        }
        mWifiIcon.setSummary(mWifiIcon.getEntry());
        mWifiIcon.setOnPreferenceChangeListener(this);

        // Qs shape
        mQsShape = (ListPreference) findPreference(PREF_QS_SHAPE);
        int qsShapeValue = getOverlayPosition(ThemesUtils.QS_SHAPE);
        if (qsShapeValue != -1) {
            mQsShape.setValue(String.valueOf(qsShapeValue + 2));
        } else {
            mQsShape.setValue("1");
        }
        mQsShape.setSummary(mQsShape.getEntry());
        mQsShape.setOnPreferenceChangeListener(this);

        // Switch style
        mSwitchStyle = (ListPreference) findPreference(PREF_SWITCH_STYLE);
        int switchStyleValue = getOverlayPosition(ThemesUtils.SWITCH_STYLE);
        if (switchStyleValue != -1) {
            mSwitchStyle.setValue(String.valueOf(switchStyleValue + 2));
        } else {
            mSwitchStyle.setValue("1");
        }
        mSwitchStyle.setSummary(mSwitchStyle.getEntry());
        mSwitchStyle.setOnPreferenceChangeListener(this);

        rgbAccentPicker = (ColorPickerPreference) findPreference(PREF_RGB_ACCENT_PICKER);
        String colorVal = Settings.Secure.getStringForUser(mContext.getContentResolver(),
                Settings.Secure.ACCENT_COLOR, UserHandle.USER_CURRENT);
        int color = (colorVal == null)
                ? Color.WHITE
                : Color.parseColor("#" + colorVal);
        rgbAccentPicker.setNewPreviewColor(color);
        rgbAccentPicker.setOnPreferenceChangeListener(this);

        mDashboardIcons = (SystemSettingListPreference)
                findPreference(PREF_SETTINGS_ICONS);
        mDashboardIcons.setOnPreferenceChangeListener(this);

        // Settings themes
        mThemesSettings = (Preference) findPreference(PREF_SETTINGS_THEMES);
        mThemesSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference pref) {
                Dialog dialog = new Dialog(getActivity());
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_settings, null);
                dialog.setContentView(view);
                RadioButton a = (RadioButton) view.findViewById(R.id.themes_a);
                RadioButton b = (RadioButton) view.findViewById(R.id.themes_b);
                RadioButton def = (RadioButton) view.findViewById(R.id.themes_default);
                Button ok = view.findViewById(R.id.button_oke);

                SharedPreferences sharedPreferences = mContext.getSharedPreferences("PrefsFile", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Boolean val = sharedPreferences.getBoolean("tema_satu", true);
                Boolean val1 = sharedPreferences.getBoolean("tema_dua", true);
                Boolean val2 = sharedPreferences.getBoolean("tema_def", true);

                
                if (a.isChecked()) {
                     def.setChecked(false);
                     b.setChecked(false);
                 } else if (def.isChecked()) {
                     a.setChecked(false);
                     b.setChecked(false);
                 } else if (b.isChecked()) {
                     a.setChecked(false);
                     def.setChecked(false);
                 }

                if (val){
                     b.setChecked(false);
                     def.setChecked(false);
                } else if (val2) {
                     a.setChecked(false);
                     b.setChecked(false);
                } else if (val1) {
                     a.setChecked(false);
                     def.setChecked(false);
                }
                dialog.show();
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (a.isChecked()) {
                            editor.putBoolean("tema_satu", true);
                            editor.putBoolean("tema_def", false);
                            editor.putBoolean("tema_dua", false);
                            editor.putString("tema", "TEMA_SATU");
                            editor.commit();
                            editor.apply();
                        } else if (def.isChecked()) {
                            editor.putBoolean("tema_def", true);
                            editor.putBoolean("tema_satu", false);
                            editor.putBoolean("tema_dua", false);
                            editor.putString("tema", "TEMA_DEFAULT");
                            editor.commit();
                            editor.apply();
                        } else if (b.isChecked()) {
                            editor.putBoolean("tema_dua", true);
                            editor.putBoolean("tema_def", false);
                            editor.putBoolean("tema_satu", false);
                            editor.putString("tema", "TEMA_DUA");
                            editor.commit();
                            editor.apply();
                        }
                        dialog.dismiss();
                    }
                });
                return false;
            }
        });

        mFontPreference = (FontDialogPreference) findPreference(KEY_FONT_PICKER_FRAGMENT_PREF);
        mFontPreference.setSummary(getCurrentFontInfo().fontName.replace("_", " "));
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
        } else if (preference == mQsHeaderStyle) {
            String qsHeaderStyle = (String) newValue;
            int qsHeaderStyleValue = Integer.parseInt(qsHeaderStyle);
            mQsHeaderStyle.setValue(String.valueOf(qsHeaderStyleValue));
            String overlayName = getOverlayName(ThemesUtils.QS_HEADER_THEMES);
            if (overlayName != null) {
                handleOverlays(overlayName, false, mOverlayManager);
            }
            if (qsHeaderStyleValue > 1) {
                handleOverlays(ThemesUtils.QS_HEADER_THEMES[qsHeaderStyleValue - 2],
                        true, mOverlayManager);
            }
            mQsHeaderStyle.setSummary(mQsHeaderStyle.getEntry());
            return true;
        } else if (preference == mRoundedUi) {
            String rounded = (String) newValue;
            int roundedValue = Integer.parseInt(rounded);
            mRoundedUi.setValue(String.valueOf(roundedValue));
            String overlayName = getOverlayName(ThemesUtils.UI_RADIUS);
            if (overlayName != null) {
                handleOverlays(overlayName, false, mOverlayManager);
            }
            if (roundedValue > 1) {
                handleOverlays(ThemesUtils.UI_RADIUS[roundedValue - 2],
                        true, mOverlayManager);
            }
            mRoundedUi.setSummary(mRoundedUi.getEntry());
            return true;
        } else if (preference == mSbHeight) {
            String sbheight = (String) newValue;
            int sbheightValue = Integer.parseInt(sbheight);
            mSbHeight.setValue(String.valueOf(sbheightValue));
            String overlayName = getOverlayName(ThemesUtils.STATUSBAR_HEIGHT);
            if (overlayName != null) {
                handleOverlays(overlayName, false, mOverlayManager);
            }
            if (sbheightValue > 1) {
                handleOverlays(ThemesUtils.STATUSBAR_HEIGHT[sbheightValue - 2],
                        true, mOverlayManager);
            }
            mSbHeight.setSummary(mSbHeight.getEntry());
            return true;
        } else if (preference == mBrightnessSliderStyle) {
            String sliderStyle = (String) newValue;
            int sliderValue = Integer.parseInt(sliderStyle);
            mBrightnessSliderStyle.setValue(String.valueOf(sliderValue));
            String overlayName = getOverlayName(ThemesUtils.BRIGHTNESS_SLIDER_THEMES);
            if (overlayName != null) {
                handleOverlays(overlayName, false, mOverlayManager);
            }
            if (sliderValue > 1) {
                handleOverlays(ThemesUtils.BRIGHTNESS_SLIDER_THEMES[sliderValue - 2],
                        true, mOverlayManager);
            }
            mBrightnessSliderStyle.setSummary(mBrightnessSliderStyle.getEntry());
            return true;
        } else if (preference == mPanelBg) {
            String panelbg = (String) newValue;
            int panelBgValue = Integer.parseInt(panelbg);
            mPanelBg.setValue(String.valueOf(panelBgValue));
            String overlayName = getOverlayName(ThemesUtils.PANEL_BG_STYLE);
            if (overlayName != null) {
                handleOverlays(overlayName, false, mOverlayManager);
            }
            if (panelBgValue > 1) {
                UtilsNad.showSystemUiRestartDialog(getContext());
                handleOverlays(ThemesUtils.PANEL_BG_STYLE[panelBgValue - 2],
                        true, mOverlayManager);

            }
            mPanelBg.setSummary(mPanelBg.getEntry());
            return true;
        } else if (preference == mQsShape) {
            String qqsShape = (String) newValue;
            String overlayName = getOverlayName(ThemesUtils.QS_SHAPE);
            int qqsShapeValue = Integer.parseInt(qqsShape);
            mQsShape.setValue(String.valueOf(qqsShapeValue));
            if (overlayName != null) {
                handleOverlays(overlayName, false, mOverlayManager);
            }
            if (qqsShapeValue > 1) {
                handleOverlays(ThemesUtils.QS_SHAPE[qqsShapeValue - 2],
                        true, mOverlayManager);
            }
            mQsShape.setSummary(mQsShape.getEntry());
            return true;
        } else if (preference == mDashboardIcons) {
            int value = Integer.parseInt((String) newValue);
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.THEMING_SETTINGS_DASHBOARD_ICONS, value);
            return true;
        } else if (preference == mSwitchStyle) {
            String switchStyle = (String) newValue;
            String overlayName = getOverlayName(ThemesUtils.SWITCH_STYLE);
            int switchStyleValue = Integer.parseInt(switchStyle);
            mQsShape.setValue(String.valueOf(switchStyleValue));
            if (overlayName != null) {
                handleOverlays(overlayName, false, mOverlayManager);
            }
            if (switchStyleValue > 1) {
                handleOverlays(ThemesUtils.SWITCH_STYLE[switchStyleValue - 2],
                        true, mOverlayManager);
            }
            mSwitchStyle.setSummary(mSwitchStyle.getEntry());
            return true;
        } else if (preference == mSignalIcon) {
            String signalIcon = (String) newValue;
            String overlayName = getOverlayName(ThemesUtils.SIGNAL_BAR);
            int signalIconValue = Integer.parseInt(signalIcon);
            mSignalIcon.setValue(String.valueOf(signalIconValue));
            if (overlayName != null) {
                handleOverlays(overlayName, false, mOverlayManager);
            }
            if (signalIconValue > 1) {
                handleOverlays(ThemesUtils.SIGNAL_BAR[signalIconValue - 2],
                        true, mOverlayManager);
            }
            mSignalIcon.setSummary(mSignalIcon.getEntry());
            return true;
        } else if (preference == mWifiIcon) {
            String wifiIcon = (String) newValue;
            String overlayName = getOverlayName(ThemesUtils.WIFI_BAR);
            int wifiIconValue = Integer.parseInt(wifiIcon);
            mWifiIcon.setValue(String.valueOf(wifiIconValue));
            if (overlayName != null) {
                handleOverlays(overlayName, false, mOverlayManager);
            }
            if (wifiIconValue > 1) {
                handleOverlays(ThemesUtils.WIFI_BAR[wifiIconValue - 2],
                        true, mOverlayManager);
            }
            mWifiIcon.setSummary(mWifiIcon.getEntry());
            return true;
        } else if (preference == rgbAccentPicker) {
            int color = (Integer) newValue;
            String hexColor = String.format("%08X", (0xFFFFFFFF & color));
            Settings.Secure.putStringForUser(mContext.getContentResolver(),
                        Settings.Secure.ACCENT_COLOR,
                        hexColor, UserHandle.USER_CURRENT);
            try {
                 mOverlayManager.reloadAssets("com.android.settings", UserHandle.USER_CURRENT);
                 mOverlayManager.reloadAssets("com.android.systemui", UserHandle.USER_CURRENT);
             } catch (RemoteException ignored) {
             }
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

    private FontInfo getCurrentFontInfo() {
        try {
            return mFontService.getFontInfo();
        } catch (RemoteException e) {
            return FontInfo.getDefaultFontInfo();
        }
    }

    public void stopProgress() {
    	mFontPreference.stopProgress();
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
