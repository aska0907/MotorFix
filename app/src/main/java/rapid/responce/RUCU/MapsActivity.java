package rapid.responce.RUCU;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.TravelMode;

import rapid.responce.RUCU.databinding.ActivityMapsBinding;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    private ActivityMapsBinding binding;
    private BottomNavigationView bottomNavigationView;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private GeoApiContext geoApiContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(MapsActivity.this, MapsActivity.class));
                        return true;
                    case R.id.garage:
                        startActivity(new Intent(MapsActivity.this,SearchActivity.class));
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(MapsActivity.this, ProfileActivity.class));
                        return true;
                    case R.id.settings:
                        startActivity(new Intent(MapsActivity.this, SettingActivity.class));
                }
                return false;
            }
        });

        // Restore selected item state if available
        if (savedInstanceState != null) {
            int selectedItemId = savedInstanceState.getInt("selectedItemId", R.id.home);
            bottomNavigationView.setSelectedItemId(selectedItemId);
        }

        // Initialize the GeoApiContext with your API key
        geoApiContext = new GeoApiContext.Builder()
                .apiKey("YOUR_API_KEY")
                .build();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set the initial camera position to Iringa, Tanzania
        LatLng iringa = new LatLng(-7.774045, 35.691721);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(iringa, 14));



        //marker for home
        LatLng home = new LatLng(-7.7748890, 35.6927280);
        MarkerOptions homeMarkerOptions = new MarkerOptions()
                .position(home)
                .title("ALBERT HOME")
                .snippet("Iswh1UDBrUgsukn1m0oiia38bLF2"); // Add the document ID here
        mMap.addMarker(homeMarkerOptions);

        LatLng mwijaku = new LatLng(-7.7787500, 35.6943970);
        MarkerOptions mwijakuMarkerOptions = new MarkerOptions()
                .position(mwijaku)
                .title("mwijaku garage")
                .snippet("Iswh1UDBrUgsukn1m0oiia38bLF2"); // Add the document ID here
        mMap.addMarker(mwijakuMarkerOptions);
        // Set the marker click listener
        mMap.setOnMarkerClickListener(this);

        // Check if the location permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, initialize the LocationManager
            initializeLocationManager();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @SuppressLint("MissingPermission")
    private void initializeLocationManager() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Define a location listener to listen for location updates
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Update the user's current location on the map
                LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.clear(); // Clear previous markers
                mMap.addMarker(new MarkerOptions().position(userLatLng).title("Current Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLatLng));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        // Request location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, initialize the LocationManager
                initializeLocationManager();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop listening for location updates
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedItemId", bottomNavigationView.getSelectedItemId());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int selectedItemId = savedInstanceState.getInt("selectedItemId", R.id.home);
        bottomNavigationView.setSelectedItemId(selectedItemId);
    }

    private void showPopupMenu() {
        // Inflate the popup_menu.xml layout file
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up, null);

        // Create the PopupWindow object
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // Allows tapping outside the window to dismiss
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // Set the animation style of the popup window
        popupWindow.setAnimationStyle(android.R.style.Animation_Dialog); // Default dialog animation style

        // Show the popup window at the left side of the screen
        popupWindow.showAtLocation(popupView, Gravity.START, 0, 0);

        // Handle the click events of the options in the popup menu
        Button logoutButton = popupView.findViewById(R.id.button11);
        Button privacyButton = popupView.findViewById(R.id.button10);
        Button contactsButton = popupView.findViewById(R.id.button12);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle logout action here
                popupWindow.dismiss();
            }
        });

        privacyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle privacy and policy action here
                popupWindow.dismiss();
            }
        });

        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle contacts action here
                popupWindow.dismiss();
            }
        });

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu();
            }
        });
    }

    private void navigateToLocation(LatLng destinationLatLng) {
        // Get the user's current location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                LatLng originLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                // Use the Directions API to request directions
                DirectionsApiRequest directionsRequest = DirectionsApi.newRequest(geoApiContext)
                        .mode(TravelMode.DRIVING)
                        .origin(new com.google.maps.model.LatLng(originLatLng.latitude, originLatLng.longitude))
                        .destination(new com.google.maps.model.LatLng(destinationLatLng.latitude, destinationLatLng.longitude));

                // Execute the request asynchronously
                directionsRequest.setCallback(new PendingResult.Callback<DirectionsResult>() {
                    @Override
                    public void onResult(DirectionsResult result) {
                        // Process the directions result
                        if (result.routes != null && result.routes.length > 0) {
                            DirectionsRoute route = result.routes[0];
                            if (route.legs != null && route.legs.length > 0) {
                                DirectionsStep step = route.legs[0].steps[0];
                                String instructions = step.htmlInstructions;

                                // Open the directions in a map app
                                Uri directionsUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" +
                                        destinationLatLng.latitude + "," + destinationLatLng.longitude);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, directionsUri);
                                startActivity(mapIntent);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        // Handle error
                    }
                });
            }
        }
    }
    private Marker lastClickedMarker = null;
    private String selectedMarkerSnippet = "";

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (lastClickedMarker != null && lastClickedMarker.equals(marker)) {
            // The marker is double-clicked
            String documentId = selectedMarkerSnippet; // Get the stored snippet value
            openOtherActivity(documentId);
            return true;
        }
        lastClickedMarker = marker;

        // Store the marker's snippet in the selectedMarkerSnippet variable
        selectedMarkerSnippet = marker.getSnippet();

        // Set the marker's snippet to an empty string
        marker.setSnippet("");

        return false;
    }

    private void openOtherActivity(String documentId) {
        Intent intent = new Intent(MapsActivity.this, FromMap.class);
        intent.putExtra("documentId", documentId);
        startActivity(intent);
    }






}
