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

package net.chuzarski.crowdednews;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;

import net.chuzarski.crowdednews.utils.ProductionTree;

import timber.log.Timber;

public class CrowdedNewsApplication extends Application {

    private static CrowdedNewsApplication instance;
    private JobManager jobManager;

    public CrowdedNewsApplication() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //perfect spot to start logging
        Crashlytics.start(this);
        if(BuildConfig.DEBUG) {
            //log for debug
            Timber.plant(new Timber.DebugTree());
        } else {
            //log for production
            Timber.plant(new ProductionTree());
        }

        Timber.tag("CrowdedNewsApplication");

        //Configure JobQueue
        Configuration conf = new Configuration.Builder(this)
                .maxConsumerCount(3)
                .minConsumerCount(1)
                .loadFactor(3)
                .consumerKeepAlive(120)
                .id("Main Job Manager")
                .build();

        jobManager = new JobManager(this, conf);
    }


    public JobManager getJobManager() {
        return jobManager;
    }

    public static CrowdedNewsApplication getInstance() {
        return instance;
    }
}
