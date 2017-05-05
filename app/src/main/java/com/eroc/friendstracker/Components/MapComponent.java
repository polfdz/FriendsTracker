package com.eroc.friendstracker.Components;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eroc.friendstracker.Components.listFriendsAdapter.FriendsListAdapter;
import com.eroc.friendstracker.R;
import com.eroc.friendstracker.mediatorComponents.Colleague;
import com.eroc.friendstracker.mediatorComponents.IComponent;
import com.eroc.friendstracker.mediatorComponents.IMediator;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Pol on 21/11/2015.
 */
public class MapComponent extends Fragment implements IComponent, Colleague, View.OnClickListener, View.OnTouchListener{
   //Fragment attributes
    View rootView;
    Context context;
    SharedPreferences preferences;
    private static final String APP_KEY = "0nNGaHnuAWdI3zPGxGG3E5R7aUuM7S95";

    //Map attributes
    private MapView mMapView;
    private IMapController mMapController;
    private IMediator imediator;
    boolean mapClickStatus;
    boolean firstFriendTracking;
    //Markers and points
    Marker userMarker, friendMarker;
    GeoPoint userPoint, friendPoint;

    //Views and Buttons
    ListView listView;
    DrawerLayout drawerLayout;
    LinearLayout layoutButtons, layoutRoadInfo, layoutRoadButtons;
    Button bCenterMap, bDeleteRoute;
    TextView tRoadDistance, tRoadDuration;
    Button bMenu;
    //Road attributes
    Polyline roadOverlay; //road line
    RoadManager roadManager;
    Road road;
    double roadLength, roadDuration;
    String roadType = "fastest";
    ImageView imageRoad;
    //road class
    UpdateRoadTask updateRoad;

    //VARS
    //check tap screen
    public long touchStart = 0l;
    public long touchEnd = 0l;
    String[] options = { "Car", "Pedestrian", "Bicycle" };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        context = getActivity().getApplicationContext();
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        preferences = getActivity().getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);

        String appStatus = preferences.getString("appStatus", null);

        //Create map
        mMapView = (MapView) getView().findViewById(R.id.map);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        //mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mMapView.setOnTouchListener(this);

        mapClickStatus = true;
        firstFriendTracking = true;
        mMapController = mMapView.getController();

        //Layouts
        listView = (ListView) getActivity().findViewById(R.id.list_view);
        drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);


        listView.setAdapter(new ArrayAdapter(getActivity(),
                android.R.layout.simple_expandable_list_item_1, android.R.id.text1,
                options));


        setListView();

        layoutButtons = (LinearLayout) getActivity().findViewById(R.id.layout_map_buttons);
        layoutRoadInfo = (LinearLayout) getActivity().findViewById(R.id.layout_road_info);
       // layoutRoadButtons = (LinearLayout) getActivity().findViewById(R.id.layout_road_buttons);
        layoutButtons.setVisibility(View.GONE);
        layoutRoadInfo.setVisibility(View.GONE);
        //layoutRoadButtons.setVisibility(View.GONE);

        mapClickStatus = false; //layout not clicked

        //Views
        tRoadDistance = (TextView) getActivity().findViewById(R.id.tRoadDistance);
        tRoadDuration = (TextView) getActivity().findViewById(R.id.tRoadDuration);

        //Buttons
        bCenterMap = (Button) getActivity().findViewById(R.id.bCenterMap);
        //bDeleteRoute = (Button) getActivity().findViewById(R.id.bDeleteRoute);
        bCenterMap.setOnClickListener(this);
        //bDeleteRoute.setOnClickListener(this);
        bMenu = (Button) getActivity().findViewById(R.id.bMenu);
        bMenu.setOnClickListener(this);

        String last_lat = preferences.getString("location_latitude", null);
        String last_long = preferences.getString("location_longitude", null);
        double last_longitude;
        double last_latitude;

        //image
        imageRoad = (ImageView) getActivity().findViewById(R.id.imageTypeRoad);

        //Map center on start
        if(appStatus == null || userPoint == null) {
            mMapController.setZoom(15);
            if(last_lat != null && last_long != null){
                last_longitude = Double.parseDouble(last_long);
                last_latitude = Double.parseDouble(last_lat);
            }else{
                last_longitude = 48.123123;
                last_latitude = 14.3838;
            }
            GeoPoint start = new GeoPoint(last_latitude, last_longitude);
            mMapController.setCenter(start);
        }else{
            last_longitude = Double.parseDouble(last_long);
            last_latitude = Double.parseDouble(last_lat);
            GeoPoint start = new GeoPoint(last_latitude, last_longitude);
            mMapController.setZoom(22);
            mMapController.setCenter(start);
        }

        //Markers to be drawn in map
        userMarker = new Marker(mMapView);
        friendMarker  = new Marker(mMapView);



    }
    public void setListView(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                    long arg3) {
                if(friendPoint != null){
                    switch (options[arg2]){
                        case "Car":
                            imageRoad.setImageResource(R.drawable.image_car);
                            roadType = "fastest";
                            roadManager.addRequestOption("routeType=fastest");
                            mMapView.invalidate();
                            break;
                        case "Pedestrian":
                            imageRoad.setImageResource(R.drawable.image_pedrestian);
                            roadType = "pedestrian";
                            roadManager.addRequestOption("routeType=pedestrian");
                            mMapView.invalidate();
                            break;
                       /* case "Public transport":
                            roadType = "multimodal";
                            roadManager.addRequestOption("routeType=multimodal");
                            mMapView.invalidate();
                            break;*/
                        case "Bicycle":
                            imageRoad.setImageResource(R.drawable.image_bycicle);
                            roadType = "bicycle";
                            roadManager.addRequestOption("routeType=bicycle");
                            mMapView.invalidate();
                            break;
                    }
                    //Toast.makeText(getActivity(), "New road type: " + options[arg2],Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "First set a route!",
                            Toast.LENGTH_SHORT).show();
                }
                drawerLayout.closeDrawers();
            }
        });


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(listView)) {
                    drawerLayout.closeDrawers();
                } else {
                    drawerLayout.openDrawer(listView);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void sendLocation(Location _location) {

    }

    @Override
    public void sendFriendLocation(Location _location) {

    }

    @Override
    public void receiveLocation(Location _location) {
        //LOCATION ON

       // Log.i("LocationChange Recived", "" + _location.toString());

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("appStatus","opened");
        editor.commit();
        //retrieve location
        userPoint = new GeoPoint(_location.getLatitude(), _location.getLongitude());
        //set map center
        //mMapController.setCenter(userPoint);
        //set Marker
        userMarker.setPosition(userPoint);
        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView.getOverlays().add(userMarker);
        userMarker.setIcon(getResources().getDrawable(R.drawable.ubication_poi));
        userMarker.setTitle("Your position");
        mMapView.invalidate();
        //draw route with friend
        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(userPoint);
        if (friendPoint != null) {
            waypoints.add(friendPoint);
            updateRoad = new UpdateRoadTask();
            updateRoad.execute(waypoints);
        }
    }

    @Override
    public void recieveFirendLocation(Location _location) {
        //https://code.google.com/p/osmbonuspack/wiki/Tutorial_1

        if(firstFriendTracking){
            mMapController.setCenter(userPoint);
            firstFriendTracking = false;
        }
        //set friend position
        friendPoint = new GeoPoint(_location.getLatitude(), _location.getLongitude());
        //set friend marker
        friendMarker.setPosition(friendPoint);
        friendMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView.getOverlays().add(friendMarker);
       //mMapView.invalidate();

        //draw route with user
        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(userPoint);
        waypoints.add(friendPoint);
        updateRoad = new UpdateRoadTask();
        updateRoad.execute(waypoints);

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
            case R.id.bCenterMap:
                if(userPoint != null){
                    mMapController.setCenter(userPoint);
                }
                break;
            /*case R.id.bDeleteRoute:
                if(friendPoint != null){
                    if(getOverLayPosition(roadOverlay) != -1 && getOverLayPosition(friendMarker) != -1) {
                        Log.d("roadOverlay", "" + getOverLayPosition(roadOverlay));
                        Log.d("friendMarker", "" + getOverLayPosition(friendMarker));

                        //mMapView.getOverlays().remove(getOverLayPosition(roadOverlay));
                        //mMapView.getOverlays().remove(getOverLayPosition(friendMarker));
                        Log.d("allOverlays", "" + mMapView.getOverlays().toString());

                        friendPoint = null;
                        roadManager = null;
                        roadOverlay = null;
                        mMapView.getOverlays().clear();
                        layoutRoadInfo.setVisibility(View.INVISIBLE);
                        layoutRoadButtons.setVisibility(View.INVISIBLE);
                        updateRoad = null;
                        mMapView.invalidate();
                    }
                }
                break;*/
            case R.id.bMenu:
                if (drawerLayout.isDrawerOpen(listView)) {
                    drawerLayout.closeDrawers();
                } else {
                    drawerLayout.openDrawer(listView);
                }
                break;

        }
    }

    public int getOverLayPosition(Overlay overlay){
        for(int i = 0; i<mMapView.getOverlays().size(); i++){
            if(mMapView.getOverlays().get(i).equals(overlay)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int actionType = event.getAction();

        switch (actionType) {
            case MotionEvent.ACTION_MOVE:
                //touchStart = -1;
                break;
            case MotionEvent.ACTION_DOWN:
                touchStart = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                touchEnd = System.currentTimeMillis();
                long touchTime = touchEnd - touchStart;
                if(touchTime >= 200 && touchStart!= -1){
                    if(mapClickStatus){
                        layoutButtons.setVisibility(View.VISIBLE);
                        if(friendPoint != null){
                            //layoutRoadButtons.setVisibility(View.VISIBLE);
                            layoutRoadInfo.setVisibility(View.VISIBLE);
                        }else{
                           // layoutRoadButtons.setVisibility(View.INVISIBLE);
                            layoutRoadInfo.setVisibility(View.GONE);
                        }
                        mapClickStatus = false;
                    }else{
                        layoutButtons.setVisibility(View.GONE);
                        mapClickStatus = true;
                    }
                }
                break;


        }
        return false;
    }

    //drawing route in background class
    private class UpdateRoadTask extends AsyncTask<Object, Void, Road> {

        protected Road doInBackground(Object... params) {
            @SuppressWarnings("unchecked")
            ArrayList<GeoPoint> waypoints = (ArrayList<GeoPoint>)params[0];
            roadManager = new MapQuestRoadManager(APP_KEY);

            roadManager.addRequestOption("routeType="+roadType);

            return roadManager.getRoad(waypoints);
        }
        @Override
        protected void onPostExecute(Road result) {
            road = result;
            if(getOverLayPosition(roadOverlay) != -1){
                mMapView.getOverlays().remove(getOverLayPosition(roadOverlay));
            }
            // showing distance and duration of the road
            //Toast.makeText(getActivity(), "distance="+road.mLength, Toast.LENGTH_LONG).show();
            //Toast.makeText(getActivity(), "durée="+road.mDuration, Toast.LENGTH_LONG).show();

            if(road.mStatus != Road.STATUS_OK){
                Toast.makeText(getActivity(), "Error when loading the road - status=" + road.mStatus, Toast.LENGTH_SHORT).show();

            }else{
                roadOverlay = RoadManager.buildRoadOverlay(road,getActivity());
                roadLength = road.mLength;
                roadDuration = road.mDuration;
                //Convert to format
                TimeZone tz = TimeZone.getTimeZone("UTC");
                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                df.setTimeZone(tz);
                String time = df.format(new Date((long) (roadDuration*1000L)));

                //Set road info texts
                String roadTextlength = String.format("%.2f", roadLength);
                tRoadDistance.setText(roadTextlength);
                tRoadDuration.setText(time);
                Log.d("location road build","road in cronstruction"+roadLength);
            }

            mMapView.getOverlays().add(roadOverlay);
            mMapView.invalidate();
            //updateUIWithRoad(result);
        }
    }
}
