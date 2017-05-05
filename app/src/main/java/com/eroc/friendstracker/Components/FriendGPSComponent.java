package com.eroc.friendstracker.Components;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eroc.friendstracker.R;
import com.eroc.friendstracker.mediatorComponents.Colleague;
import com.eroc.friendstracker.mediatorComponents.IComponent;
import com.eroc.friendstracker.mediatorComponents.IMediator;

/**
 * Created by Pol on 21/11/2015.
 */
/*
* NOT IN USE FUNCTIOANLITY IMPLEMENTED IN GPSComponent
* */
public class FriendGPSComponent extends Fragment implements IComponent, Colleague {
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_friend, container, false);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void sendLocation(Location _location) {

    }

    @Override
    public void sendFriendLocation(Location _location) {

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

    }
}
