package com.eroc.friendstracker.ServerConnections;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.eroc.friendstracker.TaskCompleted;
import com.eroc.friendstracker.utilities.ServerResponse;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Pol on 30/11/2015.
 */
public class ServerAddFriend extends AsyncTask<String, String, Integer> {
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private Context context;
    private Constants constants;
    String name, friend;
    int result;
    HttpRequest conn;
    public ServerAddFriend(Context context)
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
            name = userInfo[0];
            friend = userInfo[1];
            searchName(name, friend);

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


    public int searchName(String _user, String _friend){
        constants = new Constants();
        AsyncHttpClient client = new SyncHttpClient();
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("email",_user);
        param.put("friend",_friend);
        RequestParams params = new RequestParams(param);

        //client.get(constants.BASE_URL + "addFriend.php?email=" + _user + "&friend=" + _friend, new JsonHttpResponseHandler() {
        client.get(constants.BASE_URL + "addFriend.php",params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jObject) {
                Log.d("RESULT OF RESULT", jObject.toString());
                try {
                    if (jObject.getInt("MessageCode") == 200) //OK
                    {
                        //success of search added friend
                        result = 200;
                    } else if (jObject.getInt("MessageCode") == 504) //Wrong Query Result
                    {
                        //name doesn't exist
                        result = 504;
                    } else if (jObject.getInt("MessageCode") == 508) //Wrong Query Result
                    {
                        //already added
                        result = 508;
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
