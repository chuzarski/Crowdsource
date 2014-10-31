package net.chuzarski.crowdednews.jobs;

import com.path.android.jobqueue.Job;

import net.chuzarski.crowdednews.events.RedditRetrieveCompletedEvent;
import net.chuzarski.crowdednews.jobs.util.RedditJobParams;
import net.chuzarski.crowdednews.utils.reddit.RedditException;
import net.chuzarski.crowdednews.utils.reddit.RedditRequest;
import net.chuzarski.crowdednews.utils.reddit.RedditResponse;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class RedditRetrieveJob extends Job {

    RedditJobParams params;

    RedditRequest request;
    RedditResponse response;

    public RedditRetrieveJob(RedditJobParams p) {
        super(p.getJobQueueParams());
        this.params = p;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {

        //make the request
        request = new RedditRequest.Builder(params.getReddit(), params.getFilter())
                .setLimit(params.getNumPosts())
                .setAfter(params.getPostsAfter())
                .setBefore(params.getPostsBefore())
                .setCount(params.getPostCount())
                .build();

        //should be done playing with the request, fire it
        try {
            response = request.fireRequest();
        } catch (RedditException e) {
            Timber.e("Caught RedditException, error code %d", e.getRedditErrorCode());
            //TODO this should trigger an errorEvent
        }

        //throw out the event to all subscribers
        EventBus.getDefault().post(new RedditRetrieveCompletedEvent(response));

    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
