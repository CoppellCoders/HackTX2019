package com.cc.parkx;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient locationProviderClient;
    JSONArray array = new JSONArray();
    private RecyclerViewHorizontalListAdapter adapter;
    private RecyclerView recyclerView;
    private List<ParkingSpot> parkingSpots = new ArrayList<>();
    Place place;
    LatLng currentLatLng;
    ImageButton resrverSpot;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps, container, false);
        String apiKey = getString(R.string.google_api_key);
        super.onCreate(savedInstanceState);
        Places.initialize(getContext(), apiKey);
        PlacesClient placesClient = Places.createClient(getContext());
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        setLocation();
        resrverSpot = view.findViewById(R.id.reserveSpot);
        recyclerView = view.findViewById(R.id.idRecyclerViewHorizontalList);
        // add a divider after each item for more clarity
        adapter = new RecyclerViewHorizontalListAdapter(parkingSpots, getContext());
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setAdapter(adapter);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.getView().setBackgroundColor(Color.WHITE);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(final Place p) {
                place = p;
                map.clear();
                array = new JSONArray();
                // TODO: Get info about the selected place.
                Log.i("MapActivity", "Place: " + place.getName() + ", " + place.getLatLng());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
                addMarkersFromDB();


            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("MapActivity", "An error occurred: " + status);
            }
        });
        mapFragment.getMapAsync(this);

        resrverSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ParkingSelectActivity.class);
                i.putExtra("name", adapter.current.address);
                i.putExtra("price", adapter.current.price);
                i.putExtra("dist", adapter.current.distance);
                i.putExtra("img", adapter.current.url);
                i.putExtra("phone", adapter.current.phone);
                getActivity().startActivity(i);
            }
        });



        return view;
    }


    private void done() throws JSONException {
        parkingSpots.clear();
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = (JSONObject) array.get(i);
            final LatLng latLng = new LatLng((float) object.getDouble("lat"), (float) object.getDouble("long"));
            Log.e("hahah", latLng.toString());
            if (distance(latLng, place==null?currentLatLng:place.getLatLng()) <= 3) {
                Log.e("xd", String.valueOf(distance(latLng, place==null?currentLatLng:place.getLatLng())));
                double dis = distance(latLng, place==null?currentLatLng:place.getLatLng());
                parkingSpots.add(new ParkingSpot(object.getString("name"), object.getDouble("price"), dis, latLng, map, resrverSpot, object.getString("img"), object.getString("phone")));
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.parking_logo)));
                    }
                });
            }
        }
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });

    }

    private void setLocation() {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            setLocation();
        } else {
            locationProviderClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                            }
                        }
                    });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                if(reason == REASON_GESTURE) {
                    resrverSpot.setVisibility(View.GONE);
                }
            }
        });
        addMarkersFromDB();
    }

    public void addMarkersFromDB() {
        final Block<Document> printBlock = new Block<Document>() {
            @Override
            public void apply(final Document document) {
                try {
                    array.put(new JSONObject(document.toJson()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        MongoClientURI uri = new MongoClientURI(
                "mongodb://cc:cc@cluster0-shard-00-00-zme9k.azure.mongodb.net:27017,cluster0-shard-00-01-zme9k.azure.mongodb.net:27017,cluster0-shard-00-02-zme9k.azure.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&&w=majority");

        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("data");
        final MongoCollection<Document> collection = database.getCollection("locations");
        new Thread(new Runnable() {
            @Override
            public void run() {
                collection.find().forEach(printBlock);
                try {
                    done();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static double distance(LatLng ll1, LatLng ll2) {
        double lat1 = ll1.latitude;
        double lat2 = ll2.latitude;
        double lon1 = ll1.longitude;
        double lon2 = ll2.longitude;
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            return (dist);
        }
    }

}
