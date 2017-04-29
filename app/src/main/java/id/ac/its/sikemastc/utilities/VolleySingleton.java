package id.ac.its.sikemastc.utilities;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {

    private static VolleySingleton mInstance;
    private RequestQueue requestQueue;
    private static Context mContext;

    public VolleySingleton(Context context)
    {
        mContext = context;
        requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue()
    {
        if (requestQueue == null)
        {
            requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized VolleySingleton getmInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    public<T> void addToRequestQueue(Request<T> request)
    {
        requestQueue.add(request);
    }
}
