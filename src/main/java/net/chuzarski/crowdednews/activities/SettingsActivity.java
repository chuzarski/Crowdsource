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