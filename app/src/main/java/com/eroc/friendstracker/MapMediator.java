package com.eroc.friendstracker;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.eroc.friendstracker.ServerConnections.ServerUpdateStatus;
import com.eroc.friendstracker.fragmentAdapter.FragmentsPagerAdapter;
import com.eroc.friendstracker.mediatorComponents.Colleague;
import com.eroc.friendstracker.mediatorComponents.IMediator;
import com.eroc.friendstracker.R;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

public class MapMediator extends FragmentActivity implements IMediator, View.OnClickListener{

    private ViewPager viewPager;
    private FragmentsPagerAdapter mAdapter;
    //private ActionBar actionBar;
    SharedPreferences preferences;
    List<Colleague> colleagues;
    ServerUpdateStatus updateStatus;
    Button bAdd,bMap,bTrack;
    // Tab titles
   // private String[] tabs = { "Map", "POI", "GPS" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_mediator);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        preferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        //Buttons
        bAdd = (Button) findViewById(R.id.bSearch);
        bMap = (Button) findViewById(R.id.bMap);
        bTrack = (Button) findViewById(R.id.bTrackF);

        bAdd.setOnClickListener(this);
        bMap.setOnClickListener(this);
        bTrack.setOnClickListener(this);
        bTrack.setBackgroundResource(R.drawable.button_track_click);

        // Initilization
        colleagues = new Vector<>();

        viewPager = (ViewPager) findViewById(R.id.pager);
        //actionBar = getActionBar();
        mAdapter = new FragmentsPagerAdapter(getSupportFragmentManager(),this);

        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(1);
        //actionBar.setHomeButtonEnabled(false);
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        /*for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }*/
        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                //actionBar.setSelectedNavigationItem(position);
                switch (position){
                    case 0:
                        bMap.setBackgroundResource(R.drawable.button_map_click);
                        bTrack.setBackgroundResource(R.drawable.button_track);
                        bAdd.setBackgroundResource(R.drawable.button_add);
                        break;
                    case 1:
                        bMap.setBackgroundResource(R.drawable.button_map);
                        bTrack.setBackgroundResource(R.drawable.button_track_click);
                        bAdd.setBackgroundResource(R.drawable.button_add);
                        break;
                    case 2:
                        bMap.setBackgroundResource(R.drawable.button_map);
                        bTrack.setBackgroundResource(R.drawable.button_track);
                        bAdd.setBackgroundResource(R.drawable.button_add_click);
                        break;
                }
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }


    @Override
    public void sendLocation(Location _location, Colleague colleague) {
        for(Colleague c : colleagues){
            c.receiveLocation(_location);
        }
    }

    @Override
    public void sendFriendLocation(Location _location, Colleague colleague) {
        for(Colleague c : colleagues){
            c.recieveFirendLocation(_location);
        }
    }

    @Override
    public void addColleagues(Colleague _colleague) {
        colleagues.add(_colleague);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setLocation("0");
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("appStatus");
        editor.commit();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void setLocation(String _status){
        updateStatus = new ServerUpdateStatus(getApplicationContext());
        int result3 = 0;
        try {
            String email = preferences.getString("email",null);
            result3 = updateStatus.execute(email,_status).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bMap:
                bMap.setBackgroundResource(R.drawable.button_map_click);
                bTrack.setBackgroundResource(R.drawable.button_track);
                bAdd.setBackgroundResource(R.drawable.button_add);
                viewPager.setCurrentItem(0);
                break;
            case R.id.bTrackF:
                bMap.setBackgroundResource(R.drawable.button_map);
                bTrack.setBackgroundResource(R.drawable.button_track_click);
                bAdd.setBackgroundResource(R.drawable.button_add);
                viewPager.setCurrentItem(1);
                break;
            case R.id.bSearch:
                bMap.setBackgroundResource(R.drawable.button_map);
                bTrack.setBackgroundResource(R.drawable.button_track);
                bAdd.setBackgroundResource(R.drawable.button_add_click);
                viewPager.setCurrentItem(2);
                break;
        }
    }
}
