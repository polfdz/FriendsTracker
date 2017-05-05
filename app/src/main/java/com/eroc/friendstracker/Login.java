package com.eroc.friendstracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.eroc.friendstracker.ServerConnections.ServerLogin;
import com.eroc.friendstracker.ServerConnections.ServerRegister;
import java.util.concurrent.ExecutionException;


/**
 * Created by Pol on 21/11/2015.
 */
public class Login extends Activity implements View.OnClickListener, TaskCompleted{
    Button bRegister,bLogin;
    EditText tEmail, tPassword, tUserName;
    TextView tWarning;
    Context context;
    static  String BASE_URL;
    SharedPreferences preferences;
    int result;
    String result2;
    ServerRegister register;
    ServerLogin login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        context = getApplicationContext();

        BASE_URL = getResources().getString(R.string.BASE_URL);

        preferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);

        bRegister = (Button) findViewById(R.id.bRegister);
        bRegister.setOnClickListener(this);
        bLogin = (Button) findViewById(R.id.bLogin);
        bLogin.setOnClickListener(this);

        tEmail = (EditText) findViewById(R.id.tEmail);
        tPassword = (EditText) findViewById(R.id.tPassword);
        tUserName = (EditText) findViewById(R.id.textUserName);

        tWarning = (TextView) findViewById(R.id.tWarning);
        checkUserRegisterStatus(); //consulting sharedPreferences

    }

    public void checkUserRegisterStatus()
    {
        //Check if usr is already logged in registered
        String registerStatus = preferences.getString("registerStatus", null);
        if(registerStatus != null){ //
            Intent uRegistered = new Intent(Login.this, MapMediator.class);
            startActivity(uRegistered);
            finish();
        }
    }

    public void checkParameters(int _i)
    {
        String email = tEmail.getText().toString();
        String password = tPassword.getText().toString();
        String name = tUserName.getText().toString();

        if(checkEmail(email)== 1) {
            if (checkUserName(name) == 1 || _i == 1){
                if (checkPassword(password) == 1) {
                    switch (_i) {
                        case 0: //ServerRegister
                            registerToServer(email, password,name);
                            break;
                        case 1://Login
                            loginToServer(email, password,name);
                            break;

                    }
                } else {
                    Toast.makeText(context, "password too short (6 characters)", 500).show();
                }
            }else{
                Toast.makeText(context, "set user name", 500).show();
            }
        }else{
            Toast.makeText(context, "email not valid", 500).show();
        }
    }

    @Override
    public void onTaskComplete(Integer res) {
        result = res;
    }

    public void registerToServer(final String _email , final String _password, final String _name){

        register = new ServerRegister(Login.this);
        try {
            result = register.execute(_email,_password,_name).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d("RESULT OF RESULT", "" + result);

        switch (result){
            case 200:
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("registerStatus", "200");
                editor.putString("userName",_name);
                editor.putString("email",_email);
                editor.commit();
                Intent menu = new Intent(this,MapMediator.class);
                startActivity(menu);
                finish();
                break;
            case 504:
                tWarning.setText("Email already exists");
                break;
            case 508:
                tWarning.setText("User name already exists");
                break;
            case 600:
                tWarning.setText("Connection error try again");
                break;
        }
    }

    private void loginToServer(final String _email, final String _password, final String _name) {
        login = new ServerLogin(Login.this);
        try {
            result2 = login.execute(_email,_password,_name).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d("RESULT OF RESULT", "" + result);

        switch (result2){
            case "504":
                tWarning.setText("Incorrect email or password");
                break;
            case "600":
                tWarning.setText("Connection error try again");
                break;
            default:
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("registerStatus", "200");
                editor.putString("userName",result2);
                editor.putString("email",_email);
                editor.commit();
                Intent menu = new Intent(this,MapMediator.class);
                startActivity(menu);
                finish();
                break;
        }
    }

    private void updateProfileSharedPreferences(String _id, String _email, String _country, String _gender)
    {
        //SHARED PREFERENCES
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("id", _id);
        editor.putString("gender",_gender);
        editor.putString("country", _country);
        editor.commit();
    }


    private int checkPassword(String _password) {
        if(_password.length() < 6){
            return 0; //incorrect
        }
        return 1; //correct
    }

    private int checkEmail(String _email) {
        String[] checkAt = _email.split("\\@");
        if(checkAt.length != 2){
            return 0; //incorrect
        }
        return 1;
    }
    private int checkUserName(String _name){
        if(_name.length() == 0){
            return 0; //incorrect
        }
        return 1; //correct
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bRegister:
                checkParameters(0);
                break;
            case R.id.bLogin:
                checkParameters(1);
                break;
        }
    }

    private void popUpToast(String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        android.os.Process.killProcess(android.os.Process.myPid());
    }


}
