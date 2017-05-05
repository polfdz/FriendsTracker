package com.eroc.friendstracker.ServerConnections;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Pol on 03/12/2015.
 */
public class ServerSendLocation extends AsyncTask<String, String, Integer> {
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private Context context;
    private Constants constants;
    String latitude, longitude, email;
    int result;
    HttpRequest conn;
    public ServerSendLocation(Context context)
    {
        this.context = context;
        constants = new Constants();
        result = 600;
        conn = new HttpRequest();
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }


    @Override
    protected Integer doInBackground(String... userInfo) {

        try {
            email = userInfo[0];
            latitude = userInfo[1];
            longitude = userInfo[2];

            sendLocation(email,latitude, longitude);

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
    protected void onPostExecute(Integer res) {
        super.onPostExecute(res);
    }


    public int sendLocation(String _email, String _latitude, String _longitude){
        constants = new Constants();
        AsyncHttpClient client = new SyncHttpClient();
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("email",_email);
        param.put("latitude",_latitude);
        param.put("longitude",_longitude);

        RequestParams params = new RequestParams(param);

        //client.get(constants.BASE_URL + "addFriend.php?email=" + _user + "&friend=" + _friend, new JsonHttpResponseHandler() {
        client.get(constants.BASE_URL + "sendLocation.php",params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jObject) {
                Log.d("RESULT OF RESULT", jObject.toString());
                try {
                    if (jObject.getInt("MessageCode") == 200) //OK
                    {
                        //success of search added friend
                        result = 200;
                    }else {
                        //error
                        result = 600;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    result = 600;
                }
            }
        });

        return result;

    }
}
