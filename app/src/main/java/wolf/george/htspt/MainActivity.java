package wolf.george.htspt;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Pattern;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;


public class MainActivity extends ActionBarActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    HashMap<String, LatLng> hotspotArray = new HashMap<String, LatLng>();
    private int x = 1;
    private ArrayList<LatLng> spotArray;
    private int lastFragment = 2;
    private String gender;
    private String feedType;
    static String currentTab;
    static ArrayList<String> collegeBoard = new ArrayList<String>();
    static ArrayList<String> postBoard = new ArrayList<String>();
    private static final String TAG = "wolf.george.htspt";
    private SupportMapFragment mapFragment;
    private static GoogleMap googleMap;
    private Location coordinates;
    boolean mUpdatesRequested;
    SharedPreferences prefs;
    static final WebSocketConnection mConnection = new WebSocketConnection();
    private static Map<String, ArrayList<String>> barToBoardMap;
    private int idnum;
    private String currentSpot;
    private String currentCity; //TODO
    private LinkedList<LatLng> lastFiveLocations = new LinkedList<LatLng>();


    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static int
            NEW_POST = 24;

    final int radius = 6371000;

    private LocationRequest mLocationRequest;
    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;
    private LocationClient mLocationClient;
    private LatLng currLatLng;

    public void changeUrgencyDown()
    {
        mLocationClient.removeLocationUpdates(this);
        Log.d("changeUrgency", "Called");
        mLocationRequest.setInterval(180000);
        mLocationRequest.setFastestInterval(180000);
        mLocationClient.connect();
    }

    public void changeFeedType(String ft)
    {
        feedType = ft;
    }

    public void setLastFragment(int lastfrag)
    {
        lastFragment = lastfrag;
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            prefs = getSharedPreferences("SharedPreferences",
                    Context.MODE_PRIVATE);

            gender = prefs.getString("gender_is",
                    "not_defined");

            idnum = prefs.getInt("Login_ID",
                10000000);

            Log.d("MainActivity says", gender);
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(5000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(
                    LocationRequest.PRIORITY_HIGH_ACCURACY);
            mPrefs = getSharedPreferences("SharedPreferences",
                    Context.MODE_PRIVATE);
            mEditor = mPrefs.edit();
            mLocationClient = new LocationClient(this, this, this);
            mUpdatesRequested = false;
            setContentView(R.layout.activity_main);
            start();
            setupTabs();
    }

    @Override
    protected void onPause() {
        // Save the current setting for updates
        mEditor.putBoolean("KEY_UPDATES_ON", mUpdatesRequested);
        mEditor.commit();
        super.onPause();
    }



    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
         * Get any previous setting for location updates
         * Gets "false" if an error occurs
         */
        if (mPrefs.contains("KEY_UPDATES_ON")) {
            mUpdatesRequested =
                    mPrefs.getBoolean("KEY_UPDATES_ON", false);

            // Otherwise, turn off location updates
        } else {
            mEditor.putBoolean("KEY_UPDATES_ON", false);
            mEditor.commit();
        }
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        // If already requested, start periodic updates
        mUpdatesRequested = true;
        if (mUpdatesRequested) {
            mLocationClient.requestLocationUpdates(mLocationRequest, this);
        }
    }

    private void setUpId()
    {
        if(idnum == 10000000)
        {
            try {
                mConnection.sendTextMessage("RequestingNewID/%/" + gender + "/%/" + coordinates.getLatitude() + "/%/" + coordinates.getLongitude() + "/%/Charlottesville");
            }
            catch(Exception e)
            {
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                Log.i("tag", "This'll run 2 seconds");
                                setUpId();
                            }
                        },
                        2000);
            }
        }
    }

    private void start() {

        final String wsuri = "ws://107.170.187.49:160";
        //final String wsuri = "ws://192.168.1.13:160";

        try {
            mConnection.connect(wsuri, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    Log.d(TAG, "Status: Connected to " + wsuri);
                    setUpId();
                }

                @Override
                public void onTextMessage(String payload) {
                    Log.d(TAG, "Got echo: " + payload);
                    String[] decodelist = payload.split(">:>");
                    String requestType = decodelist[0];

                    if (requestType.equals("citydata"))
                    {


                        String content = decodelist[1];
                        String[] contentlist = content.split(",");
                        spotArray = new ArrayList<LatLng>();
                       for(int i = 0; i < contentlist.length-1; i++)
                       {
                           String[] tempLatLng = contentlist[i].trim().split("\\s+");
                           Log.d("heat point", tempLatLng[0] + " " + tempLatLng[1]);
                           LatLng point = new LatLng(Double.parseDouble(tempLatLng[0]),Double.parseDouble(tempLatLng[1]));
                           spotArray.add(point);
                           SupportMapFragment mfrag = (SupportMapFragment) getSupportFragmentManager().findFragmentByTag("gmapfrag");
                           //gmap.addMarker(new MarkerOptions().position(point).title("marker title").icon(BitmapDescriptorFactory.fromResource(R.drawable.mm_20_red)));

                       }

                        addHeatMap();
                    }

                    if (requestType.equals("hotspotsare"))
                    {
                        String jsonCode = decodelist[1];
                        Map<String, String> gsonMap = new Gson().fromJson(jsonCode, new TypeToken<HashMap<String, String>>() {}.getType());
                        MapFragment mfrag = (MapFragment) getSupportFragmentManager().findFragmentByTag("gmapfrag");
                        googleMap = mfrag.getMap();
                        for(String key : gsonMap.keySet())
                        {
                            String coor = gsonMap.get(key);
                            String[] tempLatLng = coor.trim().split("\\s+");
                            LatLng point = new LatLng(Double.parseDouble(tempLatLng[0]),Double.parseDouble(tempLatLng[1]));
                            hotspotArray.put(key, point);
                            mfrag.addMarkers(point,key);
                            Log.d(TAG, key);
                        }

                        if(postBoard.isEmpty()) {
                            postBoard.add(0, "Main UVA Feed");
                            for (String key : gsonMap.keySet())
                                postBoard.add(key);
                        }

                    }

                    if(requestType.equals("yourNewID"))
                    {
                        idnum = Integer.parseInt(decodelist[1]);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("Login_ID", idnum);
                        editor.commit();
                    }

                    if (requestType.equals("collegeBoardData"))
                    {
                        processCollegeBoard(decodelist);
                    }

                    if (requestType.equals(("hopSpotData")))
                    {
                        processSpotData(decodelist);
                    }
                }

                public void processSpotData(String[] decodelist)
                {
                    try {
                        String city = decodelist[1];
                        String hopspot = decodelist[2];
                        String content = decodelist[3];
                        FirstFragment.adapter.clear();
                        collegeBoard.clear();
                        String[] temp = content.split(Pattern.quote("/@$/"));
                        for (int i = 0; i < temp.length; i++)
                            collegeBoard.add(temp[i]);
                        FirstFragment.adapter.notifyDataSetChanged();
                        getSupportActionBar().setSelectedNavigationItem(1);
                    }

                    catch(Exception e)
                    {
                        getSupportActionBar().setSelectedNavigationItem(1);
                    }

                }

                public void processCollegeBoard(String[] decodelist)
                {
                    try {
                        String content = decodelist[1];
                        FirstFragment.adapter.clear();
                        collegeBoard.clear();
                        String[] temp = content.split(Pattern.quote("/@$/"));
                        for (int i = 0; i < temp.length; i++)
                            collegeBoard.add(temp[i]);
                        FirstFragment.adapter.notifyDataSetChanged();
                        getSupportActionBar().setSelectedNavigationItem(1);
                    }

                    catch(Exception e)
                    {
                        getSupportActionBar().setSelectedNavigationItem(1);
                    }

                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d(TAG, "Connection lost.");
                }
            });
        } catch (WebSocketException e) {

            Log.d(TAG, e.toString());
        }
    }

    public void sendMessage(String message) {
            if(mConnection.isConnected())
                mConnection.sendTextMessage(message);
            else {
                start();
                mConnection.sendTextMessage(message);
            }
    }

    public void setupSendMessage(String message) {
        try {

                mConnection.sendTextMessage(message);
        }

        catch(Exception e)
        {
            final String send = message;
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            Log.i("tag", "This'll run 2 seconds");
                            mConnection.sendTextMessage(send);
                        }
                    },
                    2000);
        }
    }

    private void setupTabs() {

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("HopSpot");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#222326")));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#222326")));

        ActionBar.Tab tab1 = actionBar.newTab().setText("Navigate");
        tab1.setTabListener(new SpotFeedTabListener<SpotFragment> (R.id.fragment_container, this, "first", SpotFragment.class));

        ActionBar.Tab tab2 = actionBar.newTab().setText("Feed");
        tab2.setTabListener(new SupportFragmentTabListener<FirstFragment> (R.id.fragment_container, this, "second", FirstFragment.class));

        ActionBar.Tab tab3 = actionBar.newTab().setText("Local Map");
        tab3.setTabListener(new SupportMapFragmentTabListener<MapFragment> (R.id.fragment_container, this, "gmapfrag", MapFragment.class));

        actionBar.addTab(tab1, 0, false);
        actionBar.addTab(tab2, 1, false);
        actionBar.addTab(tab3, 2, true);

    }

    private void addHeatMap() {

        int[] colors = {
                Color.rgb(54, 246, 140), // green
                Color.rgb(230, 246, 54),
                Color.rgb(246, 54, 81)    // red
        };

        float[] startPoints = {
                0.2f, 0.6f, 1f
        };

        Gradient gradient = new Gradient(colors, startPoints);

        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .data(spotArray)
                .gradient(gradient)
                .build();

        MapFragment mfrag = (MapFragment) getSupportFragmentManager().findFragmentByTag("gmapfrag");
        googleMap = mfrag.getMap();

        mProvider.setOpacity(0.7);
        mProvider.setRadius(50);
        // Add a tile overlay to the map, using the heat map tile provider.
        TileOverlay mOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.new_post) {
            if(currentTab.equals("PostCollegeBoard"))
                openPostCollegeBoard();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPostCollegeBoard() {

        Intent myIntent = new Intent(MainActivity.this, PostActivity.class);
        myIntent.putExtra("feedType", feedType);
        myIntent.putExtra("currentSpot", currentSpot);
        mLocationClient.requestLocationUpdates(mLocationRequest, this);
        MainActivity.this.startActivityForResult(myIntent, 23);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Choose what to do based on the request code
        if(requestCode==23 && resultCode==RESULT_OK)
            if (intent.hasExtra("post_data")) {
                String post = intent.getStringExtra("post_data");
                String feedType = intent.getStringExtra("what_feed");
                if (feedType.equals("general"))
                {
                    sendMessage("sendCollegePost/%/" + post + "/%/Charlottesville" + "/%/" + idnum);
                }
                else
                {
                    sendMessage("postToHopSpot/%/" + post + "/%/Charlottesville" + "/%/" + idnum + "/%/" + feedType);
                }
            }
        switch (requestCode) {
            // If the request code matches the code sent in onConnectionFailed
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:

                        // Log the result
                        //Log.d(LocationUtils.APPTAG, getString(R.string.resolved));

                        // Display the result
                        break;

                    // If any other result was returned by Google Play services
                    default:
                        // Log the result
                        //Log.d(LocationUtils.APPTAG, getString(R.string.no_resolution));

                        // Display the result

                        break;
                }

                // If any other request code was received
            default:
                // Report that this Activity received an unknown requestCode
                //Log.d(LocationUtils.APPTAG,
                        //getString(R.string.unknown_activity_request_code, requestCode));

                break;
        }
    }

    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Get the error code
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);
            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(
                        getSupportFragmentManager(),
                        "Location Updates");
            }
            return false;
        }
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        coordinates = location;

        if(mConnection.isConnected() && idnum != 10000000) {
            mConnection.sendTextMessage("locationis/%/" + location.getLatitude() + "/%/" + location.getLongitude() + "/%/" + idnum);
        }
        else{
            start();
        }
        if(HelperFunctions.setRefreshRate("calibrate", lastFiveLocations, new LatLng(location.getLatitude(), location.getLongitude())))
        {
            changeUrgencyDown();
            return;
        }
    matchLocation(location);
    }

    private void matchLocation(Location location)
    {
        currentSpot = "not_marked";
        Log.d("This was called", "matchLocation");
        currLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        for(String key : hotspotArray.keySet())
        {
            LatLng loc = hotspotArray.get(key);
            /*
            double lat = HelperFunctions.toRad(loc.latitude - currLatLng.latitude);
            double lon = HelperFunctions.toRad(loc.longitude - currLatLng.longitude);
            double a = Math.sin(lat / 2) * Math.sin(lat / 2) +
                    Math.cos(HelperFunctions.toRad(loc.latitude)) * Math.cos(HelperFunctions.toRad(currLatLng.latitude)) *
                            Math.sin(lon / 2) * Math.sin(lon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            */
            double distance = HelperFunctions.distanceTwoPoints(loc, currLatLng);
            Log.d("Distance to " + key, Double.toString(distance));
            if (distance < 16)
            {
                currentSpot = key;
                Log.d("Current Location Changed", key);
            }
        }


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        }
    }

    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
}