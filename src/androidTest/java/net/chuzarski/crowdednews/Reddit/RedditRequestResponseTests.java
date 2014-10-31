package net.chuzarski.crowdednews.Reddit;

import junit.framework.TestCase;

import net.chuzarski.crowdednews.utils.reddit.RedditRequest;

import java.io.IOException;
import java.net.MalformedURLException;

import static org.assertj.core.api.Assertions.*;

//public class RedditRequestResponseTests extends TestCase {
//
//    private RedditRequest rr;
//    private RedditResponseDEP resp;
//
//    public RedditRequestResponseTests() {
//        super();
//    }
//
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//
//        //setup the object
//        try {
//            rr = new RedditRequest.Builder("r/tassitstaticsubreddit", RedditRequest.RequestFilters.HOT)
//                    .setLimit(1)
//                    .build();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void testURLBuild() {
//
//        //get the string
//        String urlString = rr.getUrlString();
//
//        assertThat(urlString)
//                .isEqualTo("http://www.reddit.com/r/tassitstaticsubreddit/hot.json?limit=1&count=0&");
//    }
//
//    public void testRequestFire() {
//        //fire the request
//        try {
//            resp = rr.fireRequest();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //make sure we got a reponse;
//        assertThat(resp.getRawJSONString())
//                //Probably a bad thing to test this with. Since it is known which subreddit
//                //we are requesting with this test. To ensure we got the JSON string, search the string
//                //for the Subreddit ID
//                .contains("t5_33tzo");
//    }
//
//}
