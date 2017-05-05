package com.eroc.friendstracker.Components.listFriendsAdapter;

import android.graphics.Bitmap;
import android.location.Location;

/**
 * Created by Pol on 22/11/2015.
 */
public class Friend {
    Bitmap image;
    String friendName, runnerDistance;
    Bitmap connection;
    Location lastLocation;
    String status;
    public Friend(String name, Location _lastLocation, String _status) {
        super();
        friendName = name;
        lastLocation = _lastLocation;
        status =_status;
    }
    public Friend(String name, String _status) {
        super();
        friendName = name;
        status =_status;
    }

    public String getFriendName() {
        return friendName;
    }
    public void setFriend(String _friendName) {
        friendName = _friendName;
    }
    public String getFriendStatus() {
        return status;
    }

    public Location getFriendLocation() {
        return lastLocation;
    }
    public void setFriendLocation(Location _friendLocation) {
        lastLocation = _friendLocation;
    }

   /* public Bitmap getImage() {
        return image;
    }
    public void setImage(Bitmap image) {
        this.image = image;
    }
    public String getRunnerDistance(){ return runnerDistance; }
    public void setRunnerDistance(String _distance) {
        runnerDistance = _distance;
    }

    public Bitmap getImageConnection(){ return this.connection;}
    public void setConnection(Bitmap connection){ this.connection = connection;}*/
}
