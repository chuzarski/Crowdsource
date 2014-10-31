package net.chuzarski.crowdednews.utils.reddit;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

public class RedditRequest {
    //filters
    public static final int FILTER_HOT = 1;
    public static final int FILTER_NEW = 2;
    public static final int FILTER_RISING = 3;
    public static final int FILTER_CONTROVERSIAL = 4;
    public static final int FILTER_TOP = 5;

    private URL requestURL;
    private String urlString;

    //required fields
    private String targetReddit;
    private int requestFilter;

    //URL Params
    private int limit;
    private int count;
    private String requestPostsBefore;
    private String requestPostsAfter;

    //state
    private int selfPosts;

    public static class Builder {

        //ABSOLUTELY REQUIRED
        private String targetReddit; //sub or multi

        private int requestFilter;
        //remaining can have default values
        //misc URL parameters
        private int limit;

        private int count;
        private String after;
        private String before;


        public Builder(String targetReddit, int requestFilter) {
            this.targetReddit = targetReddit;
            this.requestFilter = requestFilter;
        }

        public Builder setLimit(int limit) {
            this.limit = limit;
            return this;
        }


        public Builder setCount(int count) {
            this.count = count;
            return this;
        }

        public Builder setAfter(String after) {
            this.after = after;
            return this;
        }

        public Builder setBefore(String before) {
            this.before = before;
            return this;
        }

        public RedditRequest build() throws MalformedURLException { return new RedditRequest(this); }
    }

    private RedditRequest(Builder b) throws MalformedURLException {

        //need a logger
        Timber.tag("RedditRequest");

        //set the values
        setCount(b.count);
        setLimit(b.limit);
        setRequestPostsAfter(b.after);
        setRequestPostsBefore(b.before);
        setRequestFilter(b.requestFilter);
        setTargetReddit(b.targetReddit);

        //build the URL string
        this.urlString = buildURL();

        //create the url object
        requestURL = new URL(this.getUrlString());

    }


    private String buildURL() {
        String theURL = "http://www.reddit.com/";

        //add the target
        theURL += getTargetReddit();

        //need a filter
        switch(getRequestFilter()) {
            case FILTER_HOT:
                theURL += "/hot";
                break;
            case FILTER_NEW:
                theURL += "/new";
                break;
            case FILTER_RISING:
                theURL += "/rising";
                break;
            case FILTER_CONTROVERSIAL:
                theURL += "/controversial";
                break;
            case FILTER_TOP:
                theURL += "/top";
                break;
        }

        //of course we're working with JSON
        theURL += ".json?";

        theURL += "limit=" + String.valueOf(getLimit()) + "&";
        theURL += "count=" + String.valueOf(getCount()) + "&";

        //after and before can be empty
        if(!(getRequestPostsAfter() == null)) {
            theURL += "after=" + getRequestPostsAfter() + "&";
        }

        if(!(getRequestPostsBefore() == null)) {
            theURL += "before=" + getRequestPostsBefore();
        }

        return theURL;
    }

    public RedditResponse fireRequest() throws IOException, RedditException {
        //create a connection
        HttpURLConnection connection = null;
        RedditResponse response = null;
        InputStream in;

        connection = (HttpURLConnection) getRequestURL().openConnection();
        in = new BufferedInputStream(connection.getInputStream());

        //create the response object
        response = parseRedditData(IOUtils.toString(in));

        //sever the connection
        connection.disconnect();

        return response;
    }

    private RedditResponse parseRedditData(String json) throws RedditException {

        JSONObject data;
        String postsBefore;
        String postsAfter;
        int redditError;
        boolean additionalPages;

        List<RedditPost> posts;

        try {
            data = new JSONObject(json).getJSONObject("data");
        } catch (JSONException e) {
            Timber.e("Data object could not be created");
            throw new RedditException(RedditErrors.REDDIT_PARSE_ERROR);
        }

        if(data.length() == 1) {
            //this looks like an error?
            try {
                redditError = data.getInt("error");
            } catch (JSONException e) {
                Timber.e("Expected an error to pop up, got something else");
                throw new RedditException(RedditErrors.REDDIT_PARSE_ERROR);
            }

            switch(redditError) {
                case RedditErrors.REDDIT_404:
                    throw new RedditException(RedditErrors.REDDIT_404);
                case RedditErrors.REDDIT_500:
                    throw new RedditException(RedditErrors.REDDIT_500);
            }
        }

        //continue on if errors are out of the way, set posts before and after
        try {
            postsAfter = data.getString("after");
            postsBefore = data.getString("before");
        } catch (JSONException e) {
            Timber.e("Posts before and after could not be parsed");
            throw new RedditException(RedditErrors.REDDIT_PARSE_ERROR);
        }

        //create posts
        try {
            posts = createPostList(data.getJSONArray("children"));
        } catch (JSONException e) {
            Timber.e("Failed to create the post array from \"children\"");
            throw new RedditException(RedditErrors.REDDIT_PARSE_ERROR);
        }

        //should be ready to create the Response
        return new RedditResponse(posts, postsBefore, postsAfter);

    }

    public void setCount(int count) {
        if(count > 100) {
            this.count = 100;
            Timber.w("The post count was over 100, set to default of 100");
        } else if (limit < 0) {
            this.limit = 0;
            Timber.w("A post count less than 0 was passed, set to the default of zero");
        } else {
            this.count = count;
        }
    }

    private List<RedditPost> createPostList(JSONArray postsJSON) throws RedditException {

        List<RedditPost> posts = new ArrayList<RedditPost>();

        //fill the list
        for(int i = 0; i < postsJSON.length(); i++) {
            try {
                posts.add(createSingleRedditPost(postsJSON.getJSONObject(i).getJSONObject("data")));
            } catch (JSONException e) {
                Timber.e("Could not create Reddit post");
                throw new RedditException(RedditErrors.REDDIT_PARSE_ERROR);
            } catch(RedditException e) {
                if((e.getRedditErrorCode() == RedditErrors.REDDIT_SELF_POST)) {
                    //good job, well done
                    Timber.i("Caught self post");
                    this.selfPosts++;

                } else {
                    throw new RedditException(RedditErrors.REDDIT_PARSE_ERROR);
                }
            }
        }

        return posts;
    }

    /**
     * Creates a single RedditPost object from the given index of the postsJSONArray
     * @return a RedditPost
     * @throws JSONException
     */
    public RedditPost createSingleRedditPost(JSONObject postObj) throws JSONException, RedditException {

        RedditPost post;

        //all data in a post
        String title;
        String url;
        String name;
        String id;
        String domain;

        long createdUTC;
        boolean stickied;

        title = StringEscapeUtils.unescapeHtml4(postObj.getString("title"));
        name = postObj.getString("name");
        url = postObj.getString("url");
        id = postObj.getString("id");
        stickied = postObj.getBoolean("stickied");
        createdUTC = postObj.getLong("created_utc");
        domain = postObj.getString("domain");

        //meet our conditions
        if(domain.contains("self.")) {
            throw new RedditException(RedditErrors.REDDIT_SELF_POST);
        }

        post = new RedditPost.Builder(title, url)
                .redditName(name)
                .redditId(id)
                .isStickied(stickied)
                .timeCreated(createdUTC)
                .linkDomain(domain)
                .build();

        return post;
    }

    public String getRequestPostsBefore() {
        return requestPostsBefore;
    }

    public void setRequestPostsBefore(String requestPostsBefore) {
        this.requestPostsBefore = requestPostsBefore;
    }

    public String getRequestPostsAfter() {
        return requestPostsAfter;
    }

    public void setRequestPostsAfter(String requestPostsAfter) {
        this.requestPostsAfter = requestPostsAfter;
    }

    public String getUrlString() {
        return this.urlString;
    }

    public URL getRequestURL() {
        return this.requestURL;
    }

    public String getTargetReddit() {
        return targetReddit;
    }

    private void setTargetReddit(String targetReddit) {
        this.targetReddit = targetReddit;
    }

    public int getRequestFilter() {
        return requestFilter;
    }

    public void setRequestFilter(int requestFilter) {
        this.requestFilter = requestFilter;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit > 100) {
            this.limit = 100;
            Timber.w("The request limit passed was higher than 100, limit set to 100");
        } else if(limit < 0) {
            this.limit = 20;
            Timber.w("The request limit passed was less than 0, set to default of 20");
        } else if(limit == 0) {
            this.limit = 20;
            Timber.w("The request limit passed was 0, set to the default of 20");
        } else {
            this.limit = limit;
        }
    }

    public int getCount() {
        return count;
    }
}
