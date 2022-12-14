package com.example.delevrini.Chef;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.delevrini.LocationFinder;
import com.example.delevrini.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.util.List;


public class MapFragment extends Fragment {

    private MapView map;
    private IMapController mapController;
    private ImageButton currenPosition;
    private SearchView searchPosition;
    private Marker marker;
    private Button confPosition;
    private List<Address> addresses;
    private GeocoderNominatim geoCoder;




    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        //handle permissions, before map is created
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // load/initialize the osmdroid configuration, this can be done
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        // sets the HTTP User Agent
        geoCoder = new GeocoderNominatim("DelevriniApp");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View v =inflater.inflate(R.layout.fragment_map, container, false);

         //Getting the map view from xml
        map = v.findViewById(R.id.map);
        currenPosition = v.findViewById(R.id.positionBtn);
        searchPosition = v.findViewById(R.id.searchPosition);
        confPosition = v.findViewById(R.id.ConfPositionBtn);

        // call the setting map function
        setupMap(map);

        clickHandler();

         return v ;
    }


    //Initialise the Map theme and params
    private void setupMap(MapView map) {

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.setDestroyMode(true);
        map.setMinZoomLevel(3.6);
        map.setMaxZoomLevel(20.6);
        map.setTilesScaledToDpi(true);

        double IsetnLatitude = 36.43693;
        double IsetnLongitude = 10.67707;

        mapController = map.getController();
        mapController.setZoom(8);
        GeoPoint startPoint = new GeoPoint(IsetnLatitude, IsetnLongitude);
        mapController.setCenter(startPoint);

        marker = new Marker(map);
        marker.setIcon(getResources().getDrawable(R.drawable.ic_locate));

//        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        marker.setDraggable(true);
        marker.setPosition(startPoint);
        map.getOverlays().add(marker);


        final Context fragmentActivity = this.getActivity();
        final DisplayMetrics dm = fragmentActivity.getResources().getDisplayMetrics();
        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);

        // set scale bar on the bottom of the screen
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, dm.heightPixels - 40);
        map.getOverlays().add(mScaleBarOverlay);
    }


    private void clickHandler() {

        // get touched location
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                map.invalidate();
                mapController.animateTo(p);
                marker.setPosition(p);
                confPosition.setClickable(true);
                confPosition.setAlpha(1);
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };

        // call touch event on map layer
        MapEventsOverlay OverlayEvents = new MapEventsOverlay(getContext(), mReceive);
        map.getOverlays().add(OverlayEvents);

        // get  marker dragged position
        marker.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                map.invalidate();
                confPosition.setClickable(true);
                confPosition.setAlpha(1);
            }

            @Override
            public void onMarkerDragStart(Marker marker) {

            }
        });

        // self Localization
        currenPosition.setOnClickListener(view -> {

                //check permission from user
                int permissionCheck = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                } else {
                    LocationFinder finder;
                    finder = new LocationFinder(requireContext());
                    finder.getLocation();
                    double latitude = finder.getLatitude();
                    double longitude = finder.getLongitude();

                    GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                    mapController.animateTo(geoPoint); // focus on the current position
                    marker.setPosition(geoPoint); // set marker to the current position
                    confPosition.setClickable(true); // make confirmation button enable
                    confPosition.setAlpha(1); // make confirmation button visible 100%

                }


        });
        // search bar
        searchPosition.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                map.invalidate();
                    SearchLocation(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        confPosition.setOnClickListener(view -> {
            try {
                    // get location information
                    addresses = geoCoder.getFromLocation(marker.getPosition().getLatitude(), marker.getPosition().getLongitude(), 1);

                    Intent intent = new Intent("broadcast address");
                    intent.putExtra("addresses", addresses.get(0));
                    LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);
                    getParentFragmentManager().popBackStackImmediate();

            } catch (Exception e) { // catch indefinite location name exception
                e.printStackTrace();
            }
        });
    }

    // search place by name function
    private void SearchLocation(String location) {
        map.invalidate();
        try {
            addresses = geoCoder.getFromLocationName(location, 1); // return the first item found  in the addresses list
            if (addresses.listIterator().hasNext()) { // if we have result
                if (addresses.get(0).getCountryName().equals("Tunisia") ) { // if result in Tunisia
                    // get extra information from the address
                    Bundle extras = addresses.get(0).getExtras();

                    // Zooming to the searched place bounds
                    if (!extras.get("boundingbox").toString().isEmpty()) {
                        BoundingBox box = extras.getParcelable("boundingbox");
                        double north = box.getActualNorth();
                        double south = box.getLatSouth();
                        double west = box.getLonWest();
                        double east = box.getLonEast();

                        BoundingBox boundingBox = new BoundingBox(north, west, south, east);
                        map.zoomToBoundingBox(boundingBox, true, 1, 15.1, (long) 2000);
                        marker.setPosition(boundingBox.getCenterWithDateLine()); // set marker to the center of the box position
                        confPosition.setClickable(true);
                        confPosition.setAlpha(1);
                    }
                }
            } else {

                Toast.makeText(getContext(), "Sorry can't find the place , try again", Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            e.printStackTrace();// TODO Auto-generated catch block

        }
    }


}
