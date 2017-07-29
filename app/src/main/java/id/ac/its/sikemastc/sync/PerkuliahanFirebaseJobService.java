package id.ac.its.sikemastc.sync;

import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.RetryStrategy;

import android.content.Context;
import android.os.AsyncTask;

public class PerkuliahanFirebaseJobService extends JobService {

    private AsyncTask<Void, Void, Void> mFetchPerkuliahanTask;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        mFetchPerkuliahanTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Context context = getApplicationContext();
                SikemasSyncTask.syncPerkuliahan(context);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(jobParameters, false);
            }
        };

        mFetchPerkuliahanTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (mFetchPerkuliahanTask != null) {
            mFetchPerkuliahanTask.cancel(true);
        }
        return true;
    }
}

