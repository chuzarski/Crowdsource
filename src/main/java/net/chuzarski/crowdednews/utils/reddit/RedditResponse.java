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

    //helper function to determine if there are more posts
    public boolean morePostsAvailable() {
        if(postsAfter == null) {
           return false;
        } else {
            return true;
        }
    }
}
