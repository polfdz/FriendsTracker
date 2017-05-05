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

import java.util.HashMap;

/**
 * Created by Pol on 01/12/2015.
 */
public class ServerGetRequests extends AsyncTask<String, String, JSONArray> {
    private Context context;
    private Constants constants;
    String name, friend;
    JSONArray result;
    HttpRequest conn;
    public ServerGetRequests(Context context)
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
    protected JSONArray doInBackground(String... userInfo) {

        try {
            name = userInfo[0];
            getRequests(name);
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
    protected void onPostExecute(JSONArray res) {
        super.onPostExecute(res);
    }


    public JSONArray getRequests(String _user){
        constants = new Constants();
        AsyncHttpClient client = new SyncHttpClient();
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("email",_user);
        RequestParams params = new RequestParams(param);

        //client.get(constants.BASE_URL + "addFriend.php?email=" + _user + "&friend=" + _friend, new JsonHttpResponseHandler() {
        client.get(constants.BASE_URL + "getRequests.php",params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jObject) {
                Log.d("RESULT OF RESULT", jObject.toString());
                JSONArray jData = null;
                try {
                    jData = jObject.getJSONArray("Data");
                    Log.d("RESULT OF ARRAY", jData.toString());

                    //JSONObject jsonObject = jData.getJSONObject(0);
                    result = jData;

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                result = jData;
                    /*if (jObject.getInt("MessageCode") == 200) //OK
                    {
                        //success of search added friend
                        result = 200;
                   */
            }
        });

        return result;

    }
}
