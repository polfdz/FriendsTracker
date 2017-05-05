package com.eroc.friendstracker.Components.listFriendsAdapter;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eroc.friendstracker.Components.GPSComponent;
import com.eroc.friendstracker.R;
import com.eroc.friendstracker.ServerConnections.ServerGetLocation;
import com.eroc.friendstracker.ServerConnections.ServerLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Pol on 22/11/2015.
 */
public class FriendsListAdapter extends ArrayAdapter<Friend> implements View.OnClickListener{
    Context context;
    int layoutResourceId;
    ArrayList<Friend> friends = new ArrayList<Friend>();
    int friendPosition;
    Friend item;
    GPSComponent gpsComponent;
    public Thread thread;
    public boolean stopThread = true;
    boolean track = true;
    public FriendsListAdapter(Context context, int layoutResourceId, ArrayList<Friend> data, GPSComponent _gpsComponent) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.friends = data;
        gpsComponent = _gpsComponent;
    }
    public FriendsListAdapter(Context context, int layoutResourceId,ArrayList<Friend> data){
        super(context, layoutResourceId, data);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RecordHolder();
            holder.tName = (TextView) row.findViewById(R.id.tFriendName);
            holder.bTrack = (Button) row.findViewById(R.id.bTrackFriend);
            holder.bStopTracking = (Button) row.findViewById(R.id.bStopTracking);
            holder.bStopTracking.setVisibility(View.INVISIBLE);

            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        item = friends.get(position);
        friendPosition = position;
        holder.tName.setText(item.getFriendName());
        if(item.getFriendStatus().equals("0")){
            holder.bTrack.setClickable(false);
            holder.bTrack.setText("Offline");
            holder.bStopTracking.setVisibility(View.INVISIBLE);
        }else{
            final RecordHolder hold = holder;
            holder.bTrack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if(track) {
                        track = false;
                        hold.bStopTracking.setVisibility(View.VISIBLE);
                        hold.bTrack.setVisibility(View.INVISIBLE);
                        thread = new Thread() {
                            JSONObject result;
                            String location, status, latitude, longitude;

                            @Override
                            public void run() {
                                try {
                                    while (true) {
                                        String friend_name = friends.get(position).getFriendName(); //UPDATE!!!!
                                        ServerGetLocation getLocation = new ServerGetLocation(context);
                                        try {
                                            result = getLocation.execute("", friend_name).get();
                                            if (result != null) {
                                                location = result.getString("location");
                                                status = result.getString("location_status");
                                            }

                                        } catch (ExecutionException e) {
                                            e.printStackTrace();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        Location loc = new Location("");//provider name is unecessary
                                        String[] loc_arr = location.split(";");
                                        loc.setLatitude(Double.parseDouble(loc_arr[0]));//your coords of course
                                        loc.setLongitude(Double.parseDouble(loc_arr[1]));
                                        sleep(2000);
                                        item.setFriendLocation(loc);
                                        gpsComponent.sendFriendLocation(loc);
                                        Log.d("location friend send", loc.toString());
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();
                    }else{
                       popUpToast("Stop tracking first");
                    }
                }
            });
            holder.bStopTracking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    track = true;
                    thread.interrupt();
                    thread = null;
                    hold.bTrack.setVisibility(View.VISIBLE);
                    hold.bTrack.setClickable(true);
                    hold.bStopTracking.setVisibility(View.INVISIBLE);
                }
            });
        }
        return row;
    }

    public void stopThread(boolean stop){
        stopThread = false;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
          /*  case R.id.bTrackFriend:
                 thread = new Thread() {
                    JSONObject result;
                    String location, status, latitude, longitude;
                    @Override
                    public void run() {
                        try {
                            while(true) {
                                String friend_name = item.getFriendName(); //UPDATE!!!!
                                ServerGetLocation getLocation = new ServerGetLocation(context);
                                try {
                                    result = getLocation.execute("", friend_name).get();
                                    if(result != null){
                                        location = result.getString("location");
                                        status = result.getString("location_status");
                                    }


                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Location loc = new Location("");//provider name is unecessary
                                String[] loc_arr = location.split(";");
                                loc.setLatitude(Double.parseDouble(loc_arr[0]));//your coords of course
                                loc.setLongitude(Double.parseDouble(loc_arr[1]));
                                sleep(2000);
                                item.setFriendLocation(loc);
                                gpsComponent.sendFriendLocation(loc);
                                Log.d("location friend send", loc.toString());
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
                break;*/
        }
    }

    static class RecordHolder {
        TextView tName;
        Button bTrack, bStopTracking;

    }
    private void popUpToast(String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
