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
public class ServerRegister extends AsyncTask<String, String, Integer> {
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private Context context;
    private Constants constants;
    String email, password, name;
    int result;
    HttpRequest conn;
    public ServerRegister(Context context)
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
            password = userInfo[1];
            name = userInfo[2];
            Log.d("RESULT OF RESULT", ""+email+""+password);

            registerToServer(email,password,name);

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


    public int registerToServer(final String _email , final String _password, String _name){
        constants = new Constants();
        AsyncHttpClient client = new SyncHttpClient();
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("Email", _email);
        param.put("Password", _password);
        RequestParams params = new RequestParams(param);

        client.get(constants.BASE_URL + "register.php?name="+_name+"&email="+_email+"&password="+password, new JsonHttpResponseHandler() {
        //client.get(constants.BASE_URL+"register.php?password="+_password+"&email="+_email, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jObject) {
                Log.d("RESULT OF RESULT", jObject.toString());
                try {
                    if (jObject.getInt("MessageCode") == 200) //OK
                    {
                        //success of register
                        result = 200;

                    } else if (jObject.getInt("MessageCode") == 504) //Wrong Query Result
                    {
                        //email already exists
                        result = 504;
                    }else if (jObject.getInt("MessageCode") == 508) //Wrong Query Result
                    {
                        //username already exists
                        result = 508;
                    } else {
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
