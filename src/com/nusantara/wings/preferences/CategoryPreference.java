/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.nusantara.wings.preferences;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import android.widget.ImageView;

import com.android.settingslib.Utils;
import com.android.settings.R;

import java.util.Random;

public class CategoryPreference extends Preference {

    private final View.OnClickListener mClickListener = this::performClick;

    private boolean mAllowDividerAbove;
    private boolean mAllowDividerBelow;
    private int mColorRandom;
    private ImageView mBG;
    private int mCatStyle;

    public CategoryPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        mCatStyle = Settings.System.getIntForUser(context.getContentResolver(),
                Settings.System.CATEGORY_NUSANTARA, 0, UserHandle.USER_CURRENT);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Preference);

        mAllowDividerAbove = TypedArrayUtils.getBoolean(a, R.styleable.Preference_allowDividerAbove,
                R.styleable.Preference_allowDividerAbove, false);
        mAllowDividerBelow = TypedArrayUtils.getBoolean(a, R.styleable.Preference_allowDividerBelow,
                R.styleable.Preference_allowDividerBelow, false);
        a.recycle();

        if (mCatStyle == 0) {
            setLayoutResource(R.layout.category_preference);
        } else if (mCatStyle == 1) {
            setLayoutResource(R.layout.category_preference);
        } else {
            setLayoutResource(R.layout.tab_preference);
        }
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setOnClickListener(mClickListener);
        final Context context = getContext();

        final boolean selectable = isSelectable();
        holder.itemView.setFocusable(selectable);
        holder.itemView.setClickable(selectable);
        holder.setDividerAllowedAbove(mAllowDividerAbove);
        holder.setDividerAllowedBelow(mAllowDividerBelow);
        mColorRandom = randomColor();
        mBG = (ImageView) holder.findViewById(R.id.card);
        setStyleColor(context);
    }

    private void setStyleColor(Context context) {
        int nadStyle = Settings.System.getIntForUser(context.getContentResolver(),
                    Settings.System.NUSANTARA_WINGS_STYLE, 0, UserHandle.USER_CURRENT);

        if (nadStyle == 0) {
            mBG.setImageResource(R.drawable.ios_card_bg);
        } else if (nadStyle == 1) {
            mBG.setColorFilter(Color.TRANSPARENT);
        } else if (nadStyle == 2) {
            mBG.setColorFilter(mColorRandom);
            mBG.setImageTintList(ColorStateList.valueOf(Color.parseColor("#80ffffff")));
            mBG.setImageResource(R.drawable.card_bg);
        } else {
            mBG.setColorFilter(Utils.getColorAttrDefaultColor(context, android.R.attr.colorAccent));
            mBG.setImageResource(R.drawable.card_bg);
            mBG.setImageTintList(ColorStateList.valueOf(Color.parseColor("#80ffffff")));
        }
    }

    public int randomColor() {
        Random r = new Random();
        float hsl[] = new float[3];
        hsl[0] = r.nextInt(360);
        hsl[1] = r.nextFloat() * 0.5f;
        hsl[2] = r.nextFloat() * 0.6f;
        return com.android.internal.graphics.ColorUtils.HSLToColor(hsl);
    }
}
