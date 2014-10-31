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
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.Params;

import net.chuzarski.crowdednews.CrowdedNewsApplication;
import net.chuzarski.crowdednews.R;
import net.chuzarski.crowdednews.adapters.PostsAdapter;
import net.chuzarski.crowdednews.events.ArticleLoadCompleteEvent;
import net.chuzarski.crowdednews.events.RedditRetrieveCompletedEvent;
import net.chuzarski.crowdednews.jobs.RedditRetrieveJob;
import net.chuzarski.crowdednews.jobs.util.RedditJobParams;
import net.chuzarski.crowdednews.utils.AppSession;
import net.chuzarski.crowdednews.utils.RedditSources;
import net.chuzarski.crowdednews.utils.reddit.RedditResponse;


import de.greenrobot.event.EventBus;
import timber.log.Timber;

import static android.view.View.OnClickListener;
import static android.widget.AbsListView.OnScrollListener;
import static android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {

    //UI
    private ListView vList;
    private ListView vDrawerList;
    private ProgressBar vWaitIndicator;
    private ActionBar vOurActionBar;
    private TextView abTitle;
    private DrawerLayout vNavDrawer;
    private AlertDialog filterDialog;

    //Jobs and Events
    private JobManager jobs;
    private EventBus events;

    //Event Handlers
    private DrawerHandler navEventHandler;
    private PageListener pageLoader;

    //MISC resources
    private RedditSources mNewsSources;
    private SharedPreferences mSettings;

    //Adapters
    private PostsAdapter mAdapter;
    private ArrayAdapter<String> mDrawerAdapter;

    //Activity State
    private RedditResponse mResponseData;
    private int mDefaultPostLimit;
    private int mDefaultPostFilter;
    private int mDefaultArticleSource; //persisted value
    private AppSession mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Start logging
        Timber.tag("MainActivity");

        //Since this app is going to be released quickly, we may have Activity Lifecycle quirks
        //allow ONLY portrait orientation for now
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //UI References
        vWaitIndicator = (ProgressBar) findViewById(R.id.waiting_icon);
        vList = (ListView) findViewById(R.id.posts_list_view);
        vOurActionBar = getActionBar();
        vNavDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer_layout);
        vDrawerList = (ListView) findViewById(R.id.navigation_list);
        abTitle = (TextView) findViewById(getResources().getIdentifier("action_bar_title", "id", "android"));

        //MISC
        mNewsSources = new RedditSources();
        navEventHandler = new DrawerHandler();
        pageLoader = new PageListener();
        filterDialog = createFilterDialog();

        //Settings
        PreferenceManager.setDefaultValues(this, R.xml.settings_layout, true);
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);

        //setup navigation
        mDrawerAdapter = new ArrayAdapter<String>(this, R.layout.navigation_list_item, R.id.navigation_item_list,
                getResources().getStringArray(R.array.news_channels_strings));
        vDrawerList.setAdapter(mDrawerAdapter);
        vDrawerList.setOnItemClickListener(navEventHandler);

        //setup the listview
        mAdapter = new PostsAdapter(getApplicationContext());
        vList.setAdapter(mAdapter);

        //multitasking
        events = EventBus.getDefault();
        jobs = CrowdedNewsApplication.getInstance().getJobManager();
        events.register(this);

        //wire event handlers
        vList.setOnItemClickListener(new ListHandler());
        vList.setOnScrollListener(pageLoader);

        //app Session
        mSession = new AppSession();

    }

    @Override
    protected void onStart() {
        super.onStart();
        setActivitySettings();

        initializeFirstArticles();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //anywhere else and you couldn't click on abTitle
        abTitle.setOnClickListener(new ButtonHandler());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id) {
            case R.id.action_settings:
                startSettingsActivity();
                break;
            case R.id.action_refresh:
                refreshAllArticles();
                break;
            case R.id.action_set_article_filter:
                filterDialog.show();
                break;
            case R.id.action_show_licenses:
                startLicenseActivity();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //-------------------------------------------------------------------------------
    //      Jobqueue
    //-------------------------------------------------------------------------------
    private void dispatchRedditRetrieveJob(RedditJobParams params) {
        jobs.addJob(new RedditRetrieveJob(params));
    }

    //-------------------------------------------------------------------------------
    //      EventBus
    //-------------------------------------------------------------------------------

    public void onEventMainThread(RedditRetrieveCompletedEvent event) {
        this.mResponseData = event.getData();
        handleResponse();
        events.post(new ArticleLoadCompleteEvent());
    }

    public void onEvent(ArticleLoadCompleteEvent e) {
        uiDoneFetchingArticles();
    }


    //-------------------------------------------------------------------------------
    //      Misc
    //-------------------------------------------------------------------------------
    private AlertDialog createFilterDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getResources().getString(R.string.select_filter));
        dialog.setSingleChoiceItems(getResources().getStringArray(R.array.news_sorting_strings), 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO Fix this to somehow use the news_filter_values int array
                        articleFilterSwitch(which + 1); //add plus 1 to correspond with Reddit Filters
                        dialog.dismiss();
                    }
                });
        return dialog.create();
    }


    //-------------------------------------------------------------------------------
    //      UI Methods
    //-------------------------------------------------------------------------------

    private void uiFetchingArticles() {
        vWaitIndicator.setVisibility(View.VISIBLE);
        vList.setVisibility(View.GONE);

    }

    private void uiDoneFetchingArticles() {
        vWaitIndicator.setVisibility(View.GONE);
        vList.setVisibility(View.VISIBLE);
    }

    //-------------------------------------------------------------------------------
    //      Article Methods
    //-------------------------------------------------------------------------------



    private void refreshAllArticles() {
        mAdapter.clearAllPosts();
        if(mSession.useSource() && mSession.useFilter()) {
            getArticles(mSession.getSource(), mSession.getFilter());
        } else if(mSession.useSource()) {
            getArticles(mSession.getSource(), mDefaultPostFilter);
        } else if(mSession.useFilter()) {
            getArticles(mDefaultArticleSource, mSession.getFilter());
        } else {
            getArticles(mDefaultArticleSource, mDefaultPostFilter);
        }
    }

    private void loadAddtionalArticles() {
        //look busy

        if(mSession.useSource() && mSession.useFilter()) {
            getAdditionalArticles(mSession.getSource(), mSession.getFilter());
        } else if(mSession.useSource()) {
            getAdditionalArticles(mSession.getSource(), mDefaultPostFilter);
        } else if(mSession.useFilter()) {
            getAdditionalArticles(mDefaultArticleSource, mSession.getFilter());
        } else {
            getAdditionalArticles(mDefaultArticleSource, mDefaultPostFilter);
        }
    }

    private void getAdditionalArticles(int src, int filter) {

        //get posts
        RedditJobParams p = new RedditJobParams.Builder(new Params(1),
                mDefaultPostLimit,
                mNewsSources.getSource(src),
                filter)
                .setAfter(mResponseData.getPostsAfter())
                .build();
        dispatchRedditRetrieveJob(p);
        Timber.d("Fetching articles");

    }

    private void getArticles(int src, int filter) {
        //make it look like we're doing something
        uiFetchingArticles();

        //get posts
        RedditJobParams p = new RedditJobParams.Builder(new Params(1),
                mDefaultPostLimit,
                mNewsSources.getSource(src),
                filter)
                .build();
        dispatchRedditRetrieveJob(p);
        Timber.d("Fetching articles");
    }

    private void initializeFirstArticles() {

        //if posts do not exist in the dataset, post loading will crash the application
        //if we switch to a different channel and clear the dataset, post loading will cause it to crash
        //disable post loading and get ALL articles first, then re-enable for when the user scrolls
        if(pageLoader.isPostLoadingEnabled()) {
            pageLoader.enablePostLoading(false);
        }
        refreshAllArticles();
        pageLoader.enablePostLoading(true);
    }

    //-------------------------------------------------------------------------------
    //      Utility Methods
    //-------------------------------------------------------------------------------

    /**
     * mResponseData is modified, this method should be called
     */
    private void handleResponse() {
        mAdapter.addAll(mResponseData.getPosts());
    }

    private void articleSourceSwitch(int chanNum) {
        //notify session
        mSession.setSource(chanNum);
        //obvious
        initializeFirstArticles();
    }

    private void articleFilterSwitch(int filter) {
        mSession.setFilter(filter);
        initializeFirstArticles();
    }

    /**
     * Load settings into activity state
     */
    private void setActivitySettings() {
        //could probably just reference the mSettings directly, this makes usage of the values
        //easier in the activity
        this.mDefaultPostLimit = Integer.parseInt(mSettings.getString("setting_post_limit", "20"));
        this.mDefaultPostFilter = Integer.parseInt(mSettings.getString("setting_default_sort", "1"));
        this.mDefaultArticleSource = Integer.parseInt(mSettings.getString("setting_news_channel", "0"));
    }


    /**
     * Open article in either ArticleView activity or any installed web browser
     * @param pos position of the article in the dataset
     */
    private void openArticle(int pos) {
        //TODO this method will determine rather to use an external browser or the ArticleViewActivity
        //get the URL to open
        String url = mAdapter.getPosts().get(pos).getLinkURL();

        //start the new activity
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(i);
    }


    private void startSettingsActivity() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    private void startLicenseActivity() {
        Intent i = new Intent(this, LicenseActivity.class);
        startActivity(i);
    }


    /**
     * For handling Button Clicks
     */
    private class ButtonHandler implements OnClickListener {
        @Override
        public void onClick(View v) {

            if(vNavDrawer.isDrawerOpen(Gravity.LEFT)) {
                vNavDrawer.closeDrawers();
            } else {
                vNavDrawer.openDrawer(Gravity.LEFT);
            }
        }
    }

    /**
     * Handles clicks on an article
     */
    private class ListHandler implements OnItemClickListener {

        //TODO this needs to use the ArticleViewActivity
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            openArticle(position);
        }
    }

    /**
     * Default event handler for NavigationDrawer
     */
    private class DrawerHandler implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String[] titles = getResources().getStringArray(R.array.news_channels_strings);
            articleSourceSwitch(position);
            //set the title on the action bar
            vOurActionBar.setTitle(titles[position]);
            vNavDrawer.closeDrawers();
        }

    }

    /**
     * VERY IMPORTANT this handles continuous scrolling
     * loads articles as the user reaches the bottom of the List
     */
    private class PageListener implements OnScrollListener {
        private int visibleConstraint = 8;
        private int lastKnownTotal;
        private boolean loadingPage = false;
        private boolean postLoadingEnabled = false;

        public PageListener() {

            EventBus.getDefault().register(this);
        }

        public PageListener(int visibleConstraint) {
            EventBus.getDefault().register(this);
            this.visibleConstraint = visibleConstraint;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {

            if(postLoadingEnabled) {

                if(loadingPage) {
                    if(totalItemCount > lastKnownTotal) {
                        loadingPage = false;
                        lastKnownTotal = totalItemCount;
                    }
                }

                if(!loadingPage && (totalItemCount - visibleItemCount) <=
                        (firstVisibleItem + visibleConstraint)) {
                    Timber.d("Loading more posts");
                    loadAddtionalArticles();
                    loadingPage = true;
                }
            }

        }

        public void onEvent(ArticleLoadCompleteEvent e) {

            //no longer loading a page
            loadingPage = false;

            //check if we have any more articles afterwards
            //if not, disable page loading
            if(mResponseData.getPostsAfter().equals("null")) {
                Timber.d("There are no more posts");
                this.postLoadingEnabled = false;
                //TODO add some code to show the user we're out of posts
            }
        }

        public void enablePostLoading(boolean l) {
            this.postLoadingEnabled = l;
        }

        public boolean isPostLoadingEnabled() { return this.postLoadingEnabled; }
    }
}
