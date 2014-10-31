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

package net.chuzarski.crowdednews.jobs.util;

import com.path.android.jobqueue.Params;


public class RedditJobParams {

    private Params jobQueueParams;
    private String postsBefore;
    private String postsAfter;
    private String reddit; //the subreddit/multireddit of the posts
    private int numPosts;
    private int postCount;
    private int filter;

    public static class Builder {

        private Params params;

        private String before = null;
        private String after = null;
        private String sub;
        private int posts;
        private int filter;
        private int count = 0;

        public Builder(Params params, int posts, String sub, int filter) {
            this.params = params;
            this.posts = posts;
            this.sub = sub;
            this.filter = filter;
        }


        public Builder setBefore(String b) {
            this.before = b;
            return this;
        }

        public Builder setAfter(String a) {
            this.after = a;
            return this;
        }

        public Builder setCount(int c) {
            this.count = c;
            return this;
        }

        public RedditJobParams build() {
            return new RedditJobParams(this);
        }

    }

    private RedditJobParams(Builder b) {
        this.jobQueueParams = b.params;
        this.postsAfter = b.after;
        this.postsBefore = b.before;
        this.numPosts = b.posts;
        this.reddit = b.sub;
        this.filter = b.filter;
        this.postCount = b.count;
    }

    public Params getJobQueueParams() {
        return jobQueueParams;
    }

    public String getPostsBefore() {
        return postsBefore;
    }

    public String getPostsAfter() {
        return postsAfter;
    }

    public int getNumPosts() {
        return numPosts;
    }

    public String getReddit() {
        return reddit;
    }

    public int getFilter() {
        return filter;
    }

    public int getPostCount() {
        return postCount;
    }
}
