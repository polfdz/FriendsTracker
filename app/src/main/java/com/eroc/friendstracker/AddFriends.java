package com.eroc.friendstracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.eroc.friendstracker.Components.listFriendsAdapter.Friend;
import com.eroc.friendstracker.Components.listFriendsAdapter.FriendsListAdapter;
import com.eroc.friendstracker.Components.listRequestsAdapter.RequestsListAdapter;
import com.eroc.friendstracker.ServerConnections.ServerAddFriend;
import com.eroc.friendstracker.ServerConnections.ServerGetRequests;
import com.eroc.friendstracker.ServerConnections.ServerSendRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Pol on 21/11/2015.
 */
public class AddFriends extends Fragment implements View.OnClickListener{
    View rootView;
    TextView tWarnRequests, tWarnFriendName;
    ListView lFriendsRequests;
    EditText friendName;
    Button bAdd;
    ArrayList<Friend> requestsListArray;
    RequestsListAdapter requestsListAdapter;
    ServerSendRequest searchFriend;
    ServerGetRequests getRequests;
    SharedPreferences preferences;
    JSONArray result2;

    int result;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_friend, container, false);

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        preferences = getActivity().getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);

        tWarnFriendName = (TextView) getActivity().findViewById(R.id.tWarnAddingFriend);
        tWarnRequests = (TextView) getActivity().findViewById(R.id.tWarnNoFriendsRequests);
        lFriendsRequests = (ListView) getActivity().findViewById(R.id.listFriendsRequests);
        friendName = (EditText) getActivity().findViewById(R.id.editTextFriendName);
        bAdd = (Button) getActivity().findViewById(R.id.bAddFriend);
        bAdd.setOnClickListener(this);

        checkEmptyList();

    }
    public void checkEmptyList(){
        setRequestsListAdapter();
        if(requestsListArray.isEmpty()){
            tWarnRequests.setVisibility(View.VISIBLE);
        }else{
            tWarnRequests.setVisibility(View.GONE);
        }
    }
    public void setRequestsListAdapter(){
        String user = preferences.getString("email",null);
        getRequests = new ServerGetRequests(getActivity().getApplicationContext());

        try {
            result2 = getRequests.execute(user).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        try {
            requestsListArray = new ArrayList<Friend>();
            if(result2 != null) {
                for (int i = 0; i < result2.length(); i++) {
                    String name = result2.getJSONObject(i).getString("name");
                    String status = result2.getJSONObject(i).getString("status");
                    requestsListArray.add(new Friend(name, status));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        lFriendsRequests = (ListView) getActivity().findViewById(R.id.listFriendsRequests);
        requestsListAdapter = new RequestsListAdapter(getActivity(), R.layout.row_request, requestsListArray, this);
        lFriendsRequests.setAdapter(requestsListAdapter);
    }
    public void getName(){
        String user = preferences.getString("email",null);
        String friend = friendName.getText().toString();
        searchFriend = new ServerSendRequest(getActivity().getApplicationContext());
        try {
           result = searchFriend.execute(user, friend).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d("RESULT OF RESULT", "" + result);

        switch (result){
            case 200:
                tWarnFriendName.setText("  friend request sent to "+friend);
                searchFriend.cancel(true);
                break;
            case 504:
                tWarnFriendName.setText("user name not found");
                searchFriend.cancel(true);
                break;
            case 508:
                tWarnFriendName.setText("firend already added");
                searchFriend.cancel(true);
                break;
            case 600:
                tWarnFriendName.setText("Connection error try again");
                searchFriend.cancel(true);
                break;
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bAddFriend:
                getName();
                break;
        }
    }
}
