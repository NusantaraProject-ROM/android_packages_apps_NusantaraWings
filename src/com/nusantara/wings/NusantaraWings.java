/*
 * Copyright (C) 2014-2016 The Dirty Unicorns Project
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

package com.nusantara.wings;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.ViewPager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.logging.nano.MetricsProto;

import com.nusantara.wings.fragments.team.TeamActivity;
import com.nusantara.wings.tabs.Lockscreen;
import com.nusantara.wings.tabs.Hardware;
import com.nusantara.wings.tabs.Statusbar;
import com.nusantara.wings.tabs.System;
import com.nusantara.wings.bottomnav.BubbleNavigationConstraintView;
import com.nusantara.wings.bottomnav.BubbleNavigationChangeListener;

public class NusantaraWings extends SettingsPreferenceFragment {

    protected Context mContext;
    private View view;
    private int mCatStyle;
    ViewGroup mContainer;
    LayoutInflater mInflater;
    ViewPager mViewPager;
    PagerAdapter mPagerAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContainer = container;
        mInflater = inflater;
        mContext = getActivity();
        createNavLayout();
        setHasOptionsMenu(true);
        return view;
    }

    public void createNavLayout() {

        int mCatStyle = Settings.System.getIntForUser(getActivity().getContentResolver(),
                Settings.System.CATEGORY_NUSANTARA, 0, UserHandle.USER_CURRENT);

        view = mInflater.inflate(R.layout.nusantarawings, mContainer, false);

        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.nusantarawings_title);
        }

        BubbleNavigationConstraintView bubbleNavigationConstraintView =  (BubbleNavigationConstraintView) view.findViewById(R.id.bottom_navigation_view_constraint);
        mViewPager = view.findViewById(R.id.viewpager);
        mPagerAdapter = new PagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        if (mCatStyle == 0) {
            bubbleNavigationConstraintView.setVisibility(View.VISIBLE);
        } else if (mCatStyle == 1) {
            bubbleNavigationConstraintView.setVisibility(View.GONE);
        } else {
            bubbleNavigationConstraintView.setVisibility(View.VISIBLE);
        }

        bubbleNavigationConstraintView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
            int id = view.getId();
               if (id == R.id.system) {
                   mViewPager.setCurrentItem(0);

               } else if (id == R.id.lockscreen) {
                   mViewPager.setCurrentItem(1);

               } else if (id == R.id.statusbar) {
                   mViewPager.setCurrentItem(2);

               } else if (id == R.id.hardware) {
                   mViewPager.setCurrentItem(3);
               }
           }
       });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                bubbleNavigationConstraintView.setCurrentActiveItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        setHasOptionsMenu(true);
    }

    class PagerAdapter extends FragmentPagerAdapter {

        int mCatStyle = Settings.System.getIntForUser(getActivity().getContentResolver(),
                Settings.System.CATEGORY_NUSANTARA, 0, UserHandle.USER_CURRENT);

        String titles[] = getTitles();
        private Fragment frags[] = new Fragment[titles.length];

        PagerAdapter(FragmentManager fm) {
            super(fm);

        	if (mCatStyle == 0) {
                frags[0] = new System();
                frags[1] = new Lockscreen();
                frags[2] = new Statusbar();
                frags[3] = new Hardware();
        	  } else if (mCatStyle == 1)  {
                frags[0] = new NusantaraCat();
        	  } else {
                frags[0] = new System();
                frags[1] = new Lockscreen();
                frags[2] = new Statusbar();
                frags[3] = new Hardware();
            }
        }

        @Override
        public Fragment getItem(int position) {
            return frags[position];
        }

        @Override
        public int getCount() {
            return frags.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    private String[] getTitles() {
        int mCatStyle = Settings.System.getIntForUser(getActivity().getContentResolver(),
                Settings.System.CATEGORY_NUSANTARA, 0, UserHandle.USER_CURRENT);
        String titleString[];
        if (mCatStyle == 0) {
        titleString = new String[]{
                getString(R.string.bottom_nav_system_title),
                getString(R.string.bottom_nav_lockscreen_title),
                getString(R.string.bottom_nav_statusbar_title),
                getString(R.string.bottom_nav_hardware_title)};
        	} else if (mCatStyle == 1) {
                titleString = new String[]{
                getString(R.string.nusantarawings_title)};
        	} else {
                titleString = new String[]{
                getString(R.string.bottom_nav_system_title),
                getString(R.string.bottom_nav_lockscreen_title),
                getString(R.string.bottom_nav_statusbar_title),
                getString(R.string.bottom_nav_hardware_title)};
          }
        return titleString;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle saveState) {
        super.onSaveInstanceState(saveState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.NUSANTARA_PRJ;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, 0, 0, R.string.dialog_team_title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            Intent intent = new Intent(mContext, TeamActivity.class);
            mContext.startActivity(intent);
            return true;
        }
        return false;
    }
}
