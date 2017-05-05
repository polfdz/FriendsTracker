package com.eroc.friendstracker.ServerConnections;

import android.os.Looper;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

/**
 * Created by Pol on 01/12/2015.
 */
public class HttpRequest {

    // A SyncHttpClient is an AsyncHttpClient
    public static AsyncHttpClient syncHttpClient= new SyncHttpClient();
    public static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

/*    public static void setCookieStore(PersistentCookieStore cookieStore) {
        getClient().setCookieStore(cookieStore);
    }
*/
    public static void get(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
        getClient().get(url, params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        getClient().post(url, params, responseHandler);
    }

    /**
     * @return an async client when calling from the main thread, otherwise a sync client.
     */
    private static AsyncHttpClient getClient()
    {
        // Return the synchronous HTTP client when the thread is not prepared
        if (Looper.myLooper() == null)
            return syncHttpClient;
        //return asyncHttpClient;
        return asyncHttpClient;
    }
}
