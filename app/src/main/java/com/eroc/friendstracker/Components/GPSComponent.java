package com.eroc.friendstracker.Components;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.eroc.friendstracker.Components.listFriendsAdapter.Friend;
import com.eroc.friendstracker.Components.listFriendsAdapter.FriendsListAdapter;
import com.eroc.friendstracker.Login;
import com.eroc.friendstracker.R;
import com.eroc.friendstracker.ServerConnections.ServerGetFriends;
import com.eroc.friendstracker.ServerConnections.ServerSendLocation;
import com.eroc.friendstracker.ServerConnections.ServerUpdateStatus;
import com.eroc.friendstracker.mediatorComponents.Colleague;
import com.eroc.friendstracker.mediatorComponents.IComponent;
import com.eroc.friendstracker.mediatorComponents.IMediator;
import com.eroc.friendstracker.utilities.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Pol on 21/11/2015.
 */
public class GPSComponent  extends Fragment implements IComponent, Colleague, OnClickListener {
    View rootView;
    Context context;
    Switch swLocation;
    GPSTracker gps;
    IMediator imediator;
    LocationManager locationManager;
    ArrayList<Friend> friendsListArray;
    ListView lFriendsList;
    FriendsListAdapter friendsListAdapter;
    TextView tWarning, tUserName;
    Button bLogout;
    int i = 0;

    SharedPreferences preferences;
    ServerGetFriends getFriends;
    ServerSendLocation sendLocation;
    ServerUpdateStatus updateStatus;
    JSONArray result;
    int result2;
    String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_gps, container, false);
        context = getActivity().getApplicationContext();
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        preferences  = getActivity().getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        swLocation = (Switch) getActivity().findViewById(R.id.switchLocation);
        swLocation.setChecked(false);
        tWarning = (TextView) getActivity().findViewById(R.id.textWarningList);
        bLogout = (Button) getActivity().findViewById(R.id.bLogout);
        bLogout.setOnClickListener(this);

        tUserName = (TextView) getActivity().findViewById(R.id.tUserName);
        String userName = preferences.getString("userName",null);
        tUserName.setText(userName);

        if(swLocation.isChecked()) {
            gps = new GPSTracker(getActivity(), this);
            showFriendsList();
            startGPS();
            setLocation("1");
        }else{
            if(gps != null){
                gps.stopUsingGPS();
            }
            setLocation("0");
        }
        swLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    setLocation("1");
                    showFriendsList();
                    gps = new GPSTracker(getActivity(), GPSComponent.this);
                    startGPS();
                } else {
                    setLocation("0");
                    hideFriendsList();
                    gps.stopUsingGPS();
                }
                // do something, the isChecked will be
                // true if the switch is in the On position
            }
        });
    }
    private void setLocation(String _status){
        updateStatus = new ServerUpdateStatus(getActivity().getApplicationContext());
        int result3 = 0;
        try {
            email = preferences.getString("email",null);
            result3 = updateStatus.execute(email,_status).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    private void showFriendsList(){
        email = preferences.getString("email",null);
        setFirendsListAdapter(email);
        lFriendsList.setVisibility(View.VISIBLE);
        tWarning.setVisibility(View.GONE);
    }
    private void hideFriendsList(){
        lFriendsList.setVisibility(View.GONE);
        tWarning.setVisibility(View.VISIBLE);
    }

    private void setFirendsListAdapter(String _email) {

        getFriends = new ServerGetFriends(getActivity().getApplicationContext());
        try {
            result = getFriends.execute(_email).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        try {
            friendsListArray = new ArrayList<Friend>();
            if(result != null) {
                for (int i = 0; i < result.length(); i++) {
                    Location targetLocation = new Location("");//provider name is unecessary
                    String name = result.getJSONObject(i).getString("name");
                    String location = result.getJSONObject(i).getString("location");
                    String status = result.getJSONObject(i).getString("status");
                    if(location != "null"){
                        String[] loc = location.split(";");
                        targetLocation.setLatitude(Double.parseDouble(loc[0]));//your coords of course
                        targetLocation.setLongitude(Double.parseDouble(loc[1]));
                        friendsListArray.add(new Friend(name, targetLocation, status));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("RESULT OF RESULT", "" + result);
        lFriendsList = (ListView) getActivity().findViewById(R.id.listFriends);
        friendsListAdapter = new FriendsListAdapter(getActivity(), R.layout.row_friend, friendsListArray, this);
        lFriendsList.setAdapter(friendsListAdapter);

    }

    public void startGPS(){
        if(gps.canGetLocation()){
            gps.onLocationChanged(gps.getLocation());
        }else{
            gps.showSettingsAlert();
        }
    }

    public void gpsNotSet(){
        swLocation.setChecked(false);
        hideFriendsList();
    }
    public void gpsSet(){
        swLocation.setChecked(true);
        showFriendsList();
    }

    @Override
    public void sendLocation(Location _location) {
        if(_location != null) {

            imediator.sendLocation(_location, this);

            sendLocation = new ServerSendLocation(getActivity().getApplicationContext());
            try {
                result2 = sendLocation.execute(email, "" + _location.getLatitude(), "" + _location.getLongitude()).get();
                Log.i("LocationChange send", "" + result2);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            switch (result2) {
                case 200:
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("location_latitude", "" + _location.getLatitude());
                    editor.putString("location_longitude", "" + _location.getLongitude());
                    editor.commit();
                    Log.i("LocationChange", "" + _location.getLatitude());
                    break;
                case 600:
                    popUpToast("connection error");
                    break;
            }
        }else{
            popUpToast("no GPS service");
        }

           // popUpToast("LOCATION");
    }

    @Override
    public void sendFriendLocation(Location _location) {
        imediator.sendFriendLocation(_location, this);
    }

    @Override
    public void receiveLocation(Location _location) {
    }

    @Override
    public void recieveFirendLocation(Location _location) {
    }

    @Override
    public Fragment getViewFragment() {
        return this;
    }

    @Override
    public void setMediator(IMediator mediator) {
        imediator = mediator;

    }

    private void popUpToast(String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bLogout:
                setLocation("0");
                Intent logout = new Intent(getActivity(), Login.class);
                deleteSharedPreferences();
                startActivity(logout);
                this.getActivity().finish();
                break;
        }
    }
    public void deleteSharedPreferences(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("registerStatus");
        editor.remove("email");
        editor.remove("userName");
        editor.commit();
    }
}
