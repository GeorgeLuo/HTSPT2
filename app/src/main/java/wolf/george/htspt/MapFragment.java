package wolf.george.htspt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends SupportMapFragment {

    private static GoogleMap mMap;
    private static SupportMapFragment mapFrag;
    private static MainActivity activity;
    private boolean hidden = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity = (MainActivity) getActivity();
        //activity.getSupportActionBar().setTitle(R.string.map);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMap = getMap();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(38.034586, -78.498864), 17));
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                //activity.getSupportActionBar().setSelectedNavigationItem(0);
                String title = marker.getTitle();
                //ArrayList<String> barBoard = barToBoardMap.get(title);
                activity.getSupportActionBar().setSelectedNavigationItem(1);
                activity.sendMessage("getSpotBoard/%/" + "Charlottesville" + "/%/" + title);

                //FirstFragment.adapter.notifyDataSetChanged();
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                if(hidden) {
                    activity.getSupportActionBar().show();
                    hidden = false;
                }
                else {
                    activity.getSupportActionBar().hide();
                    hidden = true;
                }
            }
        });


    }

    public void addMarkers(LatLng coordinate, String key) {
        mMap.addMarker(new MarkerOptions().position(coordinate).title(key).icon(BitmapDescriptorFactory.fromResource(R.drawable.mm_20_red)));
    }
}
