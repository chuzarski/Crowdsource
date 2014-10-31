package net.chuzarski.crowdednews.events;

import net.chuzarski.crowdednews.utils.reddit.RedditPost;
import net.chuzarski.crowdednews.utils.reddit.RedditResponse;

import java.util.List;


public class RedditRetrieveCompletedEvent {

    private RedditResponse data;

    public RedditRetrieveCompletedEvent(RedditResponse data) {
        this.data = data;
    }

    public RedditResponse getData() {
        return data;
    }
}
