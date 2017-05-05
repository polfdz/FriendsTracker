package com.eroc.friendstracker.fragmentAdapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.eroc.friendstracker.AddFriends;
import com.eroc.friendstracker.Components.FriendGPSComponent;
import com.eroc.friendstracker.Components.GPSComponent;
import com.eroc.friendstracker.Components.MapComponent;
import com.eroc.friendstracker.mediatorComponents.IMediator;


public class FragmentsPagerAdapter extends FragmentPagerAdapter {
	FragmentActivity mediator;
	MapComponent map = new MapComponent();
	FriendGPSComponent friend = new FriendGPSComponent();
	GPSComponent gps = new GPSComponent();
	AddFriends add = new AddFriends();

	public FragmentsPagerAdapter(FragmentManager fm, FragmentActivity _mediator) {
		super(fm);
		mediator = _mediator;
		map.setMediator((IMediator) mediator);
		friend.setMediator((IMediator) mediator);
		gps.setMediator((IMediator) mediator);
		((IMediator) mediator).addColleagues(map);
		((IMediator) mediator).addColleagues(friend);
		((IMediator) mediator).addColleagues(gps);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			return map.getViewFragment();
		case 1:
			return gps.getViewFragment();
		case 2:
			return add;
		}
		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 3;
	}

}
