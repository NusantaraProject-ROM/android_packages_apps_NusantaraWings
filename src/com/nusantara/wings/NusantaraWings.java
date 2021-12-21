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

    Context mContext;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nusantarawings, container, false);
        ActionBar actionBar = getActivity().getActionBar();
        mContext = getActivity();
        if (actionBar != null) {
            actionBar.setTitle(R.string.nusantarawings_title);
        }

        BubbleNavigationConstraintView bubbleNavigationConstraintView =  (BubbleNavigationConstraintView) view.findViewById(R.id.bottom_navigation_view_constraint);
        ViewPager viewPager = view.findViewById(R.id.viewpager);
        PagerAdapter mPagerAdapter = new PagerAdapter(getFragmentManager());
        viewPager.setAdapter(mPagerAdapter);

        bubbleNavigationConstraintView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
            int id = view.getId();
               if (id == R.id.system) {
                   viewPager.setCurrentItem(0);
               } else if (id == R.id.lockscreen) {
                   viewPager.setCurrentItem(1);
               } else if (id == R.id.statusbar) {
                   viewPager.setCurrentItem(2);
               } else if (id == R.id.hardware) {
                   viewPager.setCurrentItem(3);
               }
           }
       });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        return view;
    }

    class PagerAdapter extends FragmentPagerAdapter {

        String titles[] = getTitles();
        private Fragment frags[] = new Fragment[titles.length];

        PagerAdapter(FragmentManager fm) {
            super(fm);
            frags[0] = new System();
            frags[1] = new Lockscreen();
            frags[2] = new Statusbar();
            frags[3] = new Hardware();
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
        String titleString[];
        titleString = new String[]{
            getString(R.string.bottom_nav_system_title),
            getString(R.string.bottom_nav_lockscreen_title),
            getString(R.string.bottom_nav_statusbar_title),
            getString(R.string.bottom_nav_hardware_title)};
        return titleString;
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
        inflater.inflate(R.menu.menu_option, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.team) {
            Intent intent = new Intent(mContext, TeamActivity.class);
            mContext.startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.changelog) {
            Intent intent = new Intent(mContext, ChangelogActivity.class);
            mContext.startActivity(intent);
            return true;
        }
        return false;
    }
}
