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

        //fire it
        try {
            response = request.fireRequest();
        } catch (RedditException e) {
            Timber.e("Caught RedditException, error code %d", e.getRedditErrorCode());
            //TODO this should trigger an errorEvent
        }

        //done
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
