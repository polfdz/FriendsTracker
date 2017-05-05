package com.eroc.friendstracker.ServerConnections;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.HashMap;

/**
 * Created by Pol on 03/12/2015.
 */
public class ServerGetLocation extends AsyncTask<String, String, JSONObject> {
    private Context context;
    private Constants constants;
    String name, email;
    JSONObject result;
    HttpRequest conn;
    public ServerGetLocation(Context context)
    {
        this.context = context;
        constants = new Constants();
        result = null;
        conn = new HttpRequest();
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }


    @Override
    protected JSONObject doInBackground(String... userInfo) {

        try {
            email = userInfo[0];
            name = userInfo[1];
            getLocation(email, name);
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        Log.d("ANDRO_ASYNC", progress[0]);
    }

    @Override
    protected void onPostExecute(JSONObject res) {
        super.onPostExecute(res);
    }


    public JSONObject getLocation(String _email, String _name) throws SocketTimeoutException {
        constants = new Constants();
        AsyncHttpClient client = new SyncHttpClient();
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("email",_email);
        param.put("name",_name);

        RequestParams params = new RequestParams(param);

        //client.get(constants.BASE_URL + "addFriend.php?email=" + _user + "&friend=" + _friend, new JsonHttpResponseHandler() {
        client.get(constants.BASE_URL + "getLocation.php",params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jObject) {
                Log.d("RESULT OF RESULT", jObject.toString());
                result = jObject;
            }
        });

        return result;

    }
}
