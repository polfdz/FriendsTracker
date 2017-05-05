package com.eroc.friendstracker.Components.listRequestsAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.eroc.friendstracker.AddFriends;
import com.eroc.friendstracker.Components.GPSComponent;
import com.eroc.friendstracker.Components.listFriendsAdapter.Friend;
import com.eroc.friendstracker.R;
import com.eroc.friendstracker.ServerConnections.ServerAcceptRequest;
import com.eroc.friendstracker.ServerConnections.ServerSendRequest;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Pol on 22/11/2015.
 */
public class RequestsListAdapter extends ArrayAdapter<Friend> implements View.OnClickListener{
    Context context;
    int layoutResourceId;
    ArrayList<Friend> friends = new ArrayList<Friend>();
    int friendPosition;
    Friend item;
    AddFriends addComponent;
    RecordHolder holder;
    SharedPreferences preferences;
    ServerAcceptRequest serverAcceptRequest;
    int result;

    public RequestsListAdapter(Context context, int layoutResourceId, ArrayList<Friend> data, AddFriends _addComponent) {

        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.friends = data;
        addComponent = _addComponent;
        preferences = context.getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RecordHolder();
            holder.tName = (TextView) row.findViewById(R.id.tRequestName);
            holder.bCancel = (Button) row.findViewById(R.id.bCancelRequest);
            holder.bAccept = (Button) row.findViewById(R.id.bAcceptRequest);

            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        item = friends.get(position);
        friendPosition = position;
        holder.tName.setText(item.getFriendName());
        holder.bCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                friends.remove(position); //or some other task
                notifyDataSetChanged();
            }
        });
        holder.bAccept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                String user = preferences.getString("email", null);
                //String friend = holder.tName.getText().toString();
                String friend = friends.get(position).getFriendName();
                serverAcceptRequest = new ServerAcceptRequest(context);
                try {
                    result = serverAcceptRequest.execute(user, friend).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Log.d("FirendAcceptedResult", "" + result);

                switch (result){
                    case 200:
                        Log.d("FRIEND ADDED", "requesta accepted"+friend);
                        break;
                    case 504:
                        break;
                    case 508:
                        break;
                    case 600:
                        break;
                }
                friends.remove(position); //or some other task
                notifyDataSetChanged();
            }
        });






        return row;
    }

    @Override
    public void onClick(View v) {
        /*switch (v.getId()){
            case R.id.bAcceptRequest:

                break;
            case R.id.bCancelRequest:

                break;
        }*/
    }

    static class RecordHolder {
        TextView tName;
        Button bAccept, bCancel;
    }
}
