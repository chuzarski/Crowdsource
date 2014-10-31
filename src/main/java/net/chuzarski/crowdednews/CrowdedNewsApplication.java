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
