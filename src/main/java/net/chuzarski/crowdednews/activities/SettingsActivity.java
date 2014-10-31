/**
 *
 * Copyright 2014 Cody Huzarski (chuzarski.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *
 */

package net.chuzarski.crowdednews.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import net.chuzarski.crowdednews.R;

public class SettingsActivity extends PreferenceActivity {

    private ListPreference defaultChannel;
    private ListPreference defaultFilter;
    private ActionBar ourActionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_layout);

        //references to the UI
        defaultChannel = (ListPreference) findPreference("setting_news_channel");
        defaultFilter = (ListPreference) findPreference("setting_default_sort");
        ourActionBar = getActionBar();

        //change the icon
        ourActionBar.setIcon(R.drawable.ic_menu_settings);

        //initalize list preferences
        defaultChannel.setEntries(R.array.news_channels_strings);
        defaultChannel.setEntryValues(R.array.channel_position);

        defaultFilter.setEntries(R.array.news_sorting_strings);
        defaultFilter.setEntryValues(R.array.news_filter_values);


    }
}