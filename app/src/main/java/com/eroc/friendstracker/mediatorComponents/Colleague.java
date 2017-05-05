package com.eroc.friendstracker.mediatorComponents;

import android.location.Location;

/**
 * Created by Pol on 06/11/2015.
 */
public interface Colleague{
    public void sendLocation(Location _location);

    public void sendFriendLocation(Location _location);

    public void receiveLocation(Location _location);

    public void recieveFirendLocation(Location _location);
}
