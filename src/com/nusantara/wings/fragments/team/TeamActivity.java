/*
 * Copyright (C) 2020 The Dirty Unicorns Project
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

package com.nusantara.wings.fragments.team;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.android.settings.R;

public class TeamActivity extends Activity {

    private List<DevInfoAdapter> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_recyclerview);
        
        initTeam();
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.dimAmount = 0.75f;
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setAttributes(layoutParams);
    }
    private void initTeam(){
        RecyclerView mRecycleview = findViewById(R.id.listView);
        mRecycleview.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Scrolling up
                    TextView tv = findViewById(R.id.title_dev);
                    tv.setVisibility(View.GONE);
                } else {
                    // Scrolling down
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Do something
                    TextView tv = findViewById(R.id.title_dev);
                    tv.setVisibility(View.VISIBLE);
                } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    // Do something
                    TextView tv = findViewById(R.id.title_dev);
                    //tv.setVisibility(View.GONE);
                } else {
                    // Do something
                }
            }
        });

        // Project Manager
        setTeamMember("Aoihara", getString(R.string.projectmanager_title)
                + " / " + getString(R.string.maintainer_title), 
                "Aoihara", "Aoihara", R.drawable.aoihara);
        // Developers
        setTeamMember("Muhammad Fikri", getString(R.string.developer_title)
                + " / " + getString(R.string.maintainer_title), 
                "Genkzsz11", "Genkzsz11", R.drawable.genkzsz11);
        setTeamMember("Rafi Ramadhan", getString(R.string.developer_title)
                + " / " + getString(R.string.maintainer_title), 
                "Rafiester", "Rafiester", R.drawable.rafiester);
        setTeamMember("Joko Narimo", getString(R.string.developer_title)
                + " / " + getString(R.string.maintainer_title), 
                "703joko", "jrInfected", R.drawable.jrinfected);
        // Designers & Website Developers
        setTeamMember("Bisma Windiyanto", getString(R.string.contributor_title)
                + " / " + getString(R.string.designer_title), 
                "bismawy", "bismawy", R.drawable.bwcraft);
        // Maintainers
        setTeamMember("Hatsune", getString(R.string.maintainer_title), 
                "Hatsune71", "hats721", R.drawable.hatsune);
        setTeamMember("Alien Icecream", getString(R.string.maintainer_title), 
                "1cecreamm", "whothefvckareyou", R.drawable.icecream);
        setTeamMember("Harun Al Rasyid", getString(R.string.maintainer_title), 
                "goodmeow", "aarunalr", R.drawable.harun);
        setTeamMember("Alanndz", getString(R.string.maintainer_title), 
                "alanndz", "alanndz", R.drawable.alanndz);
        setTeamMember("Anggi Al Ansori", getString(R.string.maintainer_title), 
                "anggialansori404", "hikarinochikara", R.drawable.anggi);
        setTeamMember("Irawan's", getString(R.string.maintainer_title), 
                "dodyirawan85", "dodyirawan85", R.drawable.dodyirawan);
        setTeamMember("Aditya Kevin", getString(R.string.maintainer_title), 
                "sweetyicecare", "Gabrielse", R.drawable.gabrielse);
        setTeamMember("Heinz Der Flugel", getString(R.string.maintainer_title), 
                "fortifying", "heinzdf", R.drawable.heinz);
        setTeamMember("MocaRafee", getString(R.string.maintainer_title), 
                "Mocarafee", "yuu_ak12", R.drawable.moca);
        setTeamMember("【Ｅｋａｒｉｓｔｉ】", getString(R.string.maintainer_title), 
                "Risti699", "kaguyaasama",  R.drawable.risti);
        setTeamMember("TRISF", getString(R.string.maintainer_title), 
                "trisfaizal", "trisf", R.drawable.trisf);
        setTeamMember("Galih Gusti Priatna", getString(R.string.maintainer_title), 
                "wulan17", "wulan17", R.drawable.wulan17);
        setTeamMember("yincen17", getString(R.string.maintainer_title), 
                "yincen17", "Yincen", R.drawable.yincen17);
        setTeamMember("Wadie Habib", getString(R.string.maintainer_title), 
                "wadiehabib", "mans0amador", R.drawable.wadie);
        setTeamMember("Zahid M", getString(R.string.maintainer_title), 
                "zahidmgsk", "zahidmgsk", R.drawable.zahidm);
        setTeamMember("家 | Zenka Bestia | 家", getString(R.string.maintainer_title), 
                "ZenkaBestia", "ZenkaBestia", R.drawable.zenkab);
        setTeamMember("Nadins", getString(R.string.maintainer_title), 
                "NadinAlissa", "NadinSylaa", R.drawable.nadins);
        setTeamMember("officialputuid", getString(R.string.maintainer_title), 
                "officialputuid", "officialputuid", R.drawable.officialputuid);
        setTeamMember("ToxicLord", getString(R.string.maintainer_title), 
                "ToxicLord", "ToxicLord501", R.drawable.toxiclord);
        setTeamMember("Bayerische Motoren Werke", getString(R.string.maintainer_title), 
                "BayerischeMotorenWerke", "bmwtheseries", R.drawable.bmw);
        setTeamMember("Sena", getString(R.string.maintainer_title), 
                "namaskaragusthi", "namaskaragusthi", R.drawable.sena);
        setTeamMember("Edgars", getString(R.string.maintainer_title),
                "Edgars-Cirulis", "CirulisEdgars", R.drawable.edgars);
        setTeamMember("ItoRenz", getString(R.string.maintainer_title),
                "ItoRenz", "ItoRenz", R.drawable.itorenz);
        setTeamMember("ThunderkeX", getString(R.string.maintainer_title),
                "thunderkex", "ThunderkeXxX", R.drawable.thunderkex);
        setTeamMember("Felix Andy", getString(R.string.maintainer_title),
                "felixandi79", "Felandy79", R.drawable.felixandi);
        setTeamMember("BH36B", getString(R.string.maintainer_title),
                "BH36B", "BH36B", R.drawable.bh36b);
        setTeamMember("Rohman Urip", getString(R.string.maintainer_title),
                "Rohmanurip", "haru_sama", R.drawable.rohman);
        setTeamMember("Harshhaa Reddy", getString(R.string.maintainer_title),
                "Harshhaa", "NotHarshhaa", R.drawable.harshhaa);
        setTeamMember("Arif JeNong", getString(R.string.maintainer_title),
                "ariffjenong", "Arifjenong46", R.drawable.arifjenong);
        setTeamMember("Fraschze97", getString(R.string.maintainer_title),
                "AIV-97", "noob_permanently", R.drawable.fraschze97);
        setTeamMember("Frost", getString(R.string.maintainer_title),
                "frostg-012", "frostg-012", R.drawable.frost);
        setTeamMember("Glassy", getString(R.string.maintainer_title),
                "MatchaKoi", "whdbfl", R.drawable.glassy);
        setTeamMember("Galang Saputra", getString(R.string.maintainer_title),
                "mgs28-mh", "hand08", R.drawable.hand08);
        setTeamMember("Mikazuuu07", getString(R.string.maintainer_title),
                "Mikazuuu07", "Mikazuuu07", R.drawable.mikazuuu);
        setTeamMember("A. Dzaky", getString(R.string.maintainer_title),
                "zkyzzz", "zkyzzz", R.drawable.zkyzzz);
        // Contributors
        setTeamMember("Andra Ramadan", getString(R.string.contributor_title)
                + " / " + getString(R.string.maintainer_title), 
                "andrraa", "Andrraa", R.drawable.andra);
        setTeamMember("Rizky Benggolo", getString(R.string.contributor_title), 
                "travarilo", "travarilo", R.drawable.travarilo);
        setTeamMember("Ayra Hikari", getString(R.string.contributor_title), 
                "AyraHikari", "AyraHikari", R.drawable.ted);
        setTeamMember("Ahmad Thoriq Najahi", getString(R.string.contributor_title), 
                "najahiiii", "Najahiii", R.drawable.najahi);
        setTeamMember("Kry9toN", getString(R.string.contributor_title), 
                "Kry9toN", "Kry9toN", R.drawable.kry9ton);
        setTeamMember("G M L R", getString(R.string.contributor_title), 
                "GMLR", "warga_sosmed", R.drawable.gmlr);
        // Veterans
        setTeamMember("Julian Surya", getString(R.string.veteran_title), 
                "juliansurya", "JulianSurya",R.drawable.juliansurya);
        setTeamMember("Dimas Yudha Pratama", getString(R.string.veteran_title), 
                "dimasyudhaofficial", "dimasyudha", R.drawable.dimasyudha);
        setTeamMember("Leonardo Araujo Pimentel", getString(R.string.veteran_title), 
                "batmann4bot", "pimentelleo", R.drawable.rick);
        setTeamMember("Reza Bagas Rifaldy", getString(R.string.veteran_title), 
                "GrettoRosella", "GrettoRosella", R.drawable.reja);
        setTeamMember("𝑺𝒏𝒐𝒘𝒇𝒍𝒂𝒌𝒆.", getString(R.string.veteran_title), 
                "nothingpeace", "yaudahlahgpp", R.drawable.yaudahlahgpp);
        setTeamMember("Hadad", getString(R.string.veteran_title), 
                "hdrjt", "hadad", R.drawable.hadad);
        setTeamMember("Hiitagiii", getString(R.string.veteran_title), 
                "azrim", "Hiitagiii", R.drawable.hitagi);
        setTeamMember("Muhammad Fauzan", getString(R.string.veteran_title), 
                "buglessx", "xzanxz", R.drawable.mfauzan);
        setTeamMember("Aradhea", getString(R.string.veteran_title),
                "InVictusXV", "Vrasteizz", R.drawable.araara);
        setTeamMember("ZHANtech™", getString(R.string.veteran_title),
                "zhantech", "zhantech", R.drawable.zhantech);
        setTeamMember("Syarif Ibnu", getString(R.string.veteran_title),
                "Syarifibnu-19", "Syarifibnu", R.drawable.ibnu);
        
        ListAdapter mAdapter = new ListAdapter(mList);
        mRecycleview.setAdapter(mAdapter);
        mRecycleview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.notifyDataSetChanged();
    }

    private void setTeamMember(String devName, String devTitle,
                               String githubLink, String telegram, int devImage) {
        DevInfoAdapter adapter;

        adapter = new DevInfoAdapter();
        adapter.setImage(devImage);
        adapter.setDevName(devName);
        adapter.setDevTitle(devTitle);
        adapter.setGithubName(githubLink);
        adapter.setTelegramName(telegram);
        mList.add(adapter);
    }
}
