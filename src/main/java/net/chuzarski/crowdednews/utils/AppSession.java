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

package net.chuzarski.crowdednews.utils;

public class AppSession {


    //boolean vars to check if these have been set
    private boolean useSource = false;
    private boolean useFilter = false;

    private int newsSource;
    private int newsFilter;

    /**
     * Sets the current user selected source
     * @param s Source to set (See RedditSources class)
     */
    public void setSource(int s) {

        if(!useSource) {
            //source is now set by user
            useSource = true;
        }
        this.newsSource = s;
    }

    /**
     * Sets the current user selected filter
     * @param f Filter to set (See RedditRequest)
     */
    public void setFilter(int f) {

        if(!useFilter) {
            //filter is now set by user
            useFilter = true;
        }

        this.newsFilter = f;
    }

    public int getSource() {
        return newsSource;
    }

    public int getFilter() {
        return newsFilter;
    }

    public boolean useSource() {
        return useSource;
    }

    public boolean useFilter() {
        return useFilter;
    }
}
