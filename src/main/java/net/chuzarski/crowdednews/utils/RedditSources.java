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

public class RedditSources {

    private String[] sources;
    private String base = "user/crowdsourceapp/";

    public RedditSources() {

        //These are all of the multireddits that will drive the app.
        //incredibly stupid easy, this will do for the time being.
        sources = new String[6]; //CHANGE THIS LENGTH WHEN ADDING/REMOVING SOURCES
        sources[0] = "m/newsfeed";
        sources[1] = "m/worldfeed";
        sources[2] = "m/politicsfeed";
        sources[3] = "m/worldpoliticsfeed";
        sources[4] = "m/technologyfeed";
        sources[5] = "m/entertainmentfeed";
    }

    public String[] getAllSources() {
        return sources;
    }

    public int getNumSources() {
        return sources.length;
    }

    public String getSource(int i) {
        //check that it exists
        if(i > sources.length) {
            //TODO Throw an error
        }

        return base + sources[i];
    }

}
