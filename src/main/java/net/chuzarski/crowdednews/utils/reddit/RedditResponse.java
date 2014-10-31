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

package net.chuzarski.crowdednews.utils.reddit;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class RedditResponse {

    private List<RedditPost> posts;
    private String postsAfter;
    private String postsBefore;
    private Date reponseCreated;

    RedditResponse(List<RedditPost> postList, String before, String after) {

        this.posts = postList;
        this.postsAfter = after;
        this.postsBefore = before;

        //mark down when this was created
        reponseCreated = new Date();
    }

    public List<RedditPost> getPosts() {
        return posts;
    }

    public String getPostsAfter() {
        return postsAfter;
    }

    public String getPostsBefore() {
        return postsBefore;
    }

    public Date getCreated() {
        return reponseCreated;
    }

    /**
     * Determines if there are any more posts AFTER this response
     * @return true/false more posts
     */
    public boolean morePostsAvailable() {
        if(postsAfter == null) {
           return false;
        } else {
            return true;
        }
    }
}
