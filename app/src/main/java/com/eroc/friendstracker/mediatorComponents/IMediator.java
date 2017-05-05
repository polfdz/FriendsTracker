package com.eroc.friendstracker.mediatorComponents;

import android.location.Location;

/**
 * Created by Pol on 06/11/2015.
 */
public interface IMediator {
    public void sendLocation(Location location, Colleague colleague);
    public void sendFriendLocation(Location location, Colleague colleague);
    public void addColleagues(Colleague _colleague);
}
