package net.chuzarski.crowdednews.Reddit;

import junit.framework.TestCase;

import net.chuzarski.crowdednews.exceptions.RedditResponseExcetion;
import net.chuzarski.crowdednews.utils.reddit.RedditPost;
import net.chuzarski.crowdednews.utils.reddit.RedditRequest;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

//public class RedditPostsTests extends TestCase {
//
//    RedditRequest req;
//    RedditResponseDEP resp;
//
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//
//        //setup the object
//        //setup the object
//        try {
//            req = new RedditRequest.Builder("r/tassitstaticsubreddit", RedditRequest.RequestFilters.HOT)
//                    .setLimit(2)
//                    .build();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//
//        //fire the request
//        resp = req.fireRequest();
//    }
//
//    public void testRedditPost() {
//        RedditPost p = null;
//
//        try {
//            p = rr.createSingleRedditPost(1);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (RedditResponseExcetion redditResponseExcetion) {
//            redditResponseExcetion.printStackTrace();
//        }
//
//        assertThat(p.getTitle()).isEqualTo("Second Post");
//        assertThat(p.getCreatedTimeUTC()).isEqualTo(1412278687);
//        assertThat(p.getLinkURL()).isEqualTo("https://www.google.com/");
//        assertThat(p.getRedditId()).isEqualTo("2i4bmt");
//        assertThat(p.getRedditName()).isEqualTo("t3_2i4bmt");
//        assertThat(p.isStickied()).isEqualTo(false);
//    }
//
//    public void testRedditPostsList() {
//        ArrayList<RedditPost> posts = (ArrayList<RedditPost>) resp.getPosts();
//
//        assertThat(posts.size()).isEqualTo(2);
//        assertThat(posts.get(0).getRedditName()).isEqualTo("t3_2i48sy");
//    }
//}
