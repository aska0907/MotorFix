package rapid.responce.RUCU;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;

import java.util.List;

public class MapMap extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, OnSymbolClickListener, MapboxMap.OnMapClickListener {

    private static final int PERMISSIONS_REQUEST_CODE = 123;
    private MapView mapView;
    private Button startButton;
    private BottomNavigationView bottomNavigationView;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private SymbolManager symbolManager;

    private Point originPosition;
    private Location originLocation;
    private Point destinationPosition;
    private Marker destinationMarker;

    private LocationEngine locationEngine;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoicmFwaWRyZXNwb25jZWJ1aWxkZXJzIiwiYSI6ImNsaXNtbWNwcDBnZDAzaG83bWF0cmoyZ3oifQ.mW0JLFIrvfD7RmyU1hxB5A");
        setContentView(R.layout.mapbox);
        startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle start button click
            }
        });
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(MapMap.this, MapMap.class));
                        return true;
                    case R.id.garage:
                        startActivity(new Intent(MapMap.this, SearchActivity.class));
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(MapMap.this, ProfileActivity.class));
                        return true;
                    case R.id.settings:
                        startActivity(new Intent(MapMap.this, SettingActivity.class));
                        return true;
                }
                return false;
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.addOnMapClickListener(this);
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                if (PermissionsManager.areLocationPermissionsGranted(MapMap.this)) {
                    enableLocationComponent(style);
                } else {
                    permissionsManager = new PermissionsManager(MapMap.this);
                    permissionsManager.requestLocationPermissions(MapMap.this);
                }
                symbolManager = new SymbolManager(mapView, mapboxMap, style);
                symbolManager.setIconAllowOverlap(true);
                symbolManager.addClickListener(MapMap.this);
                addMarkers(style); // Add markers here
            }
        });
    }

    @SuppressWarnings("MissingPermission")
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are granted and request if not
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE);
        }

        // Create and activate the LocationComponent
        LocationComponent locationComponent = mapboxMap.getLocationComponent();
        locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, loadedMapStyle).build());
        locationComponent.setLocationComponentEnabled(true);
        locationComponent.setCameraMode(CameraMode.TRACKING);
        locationComponent.setRenderMode(RenderMode.COMPASS);

        // Set up the LocationEngine
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        // Create a LocationEngineCallback to listen for location updates
        LocationEngineCallback<LocationEngineResult> callback = new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                if (result.getLastLocation() != null) {
                    originLocation = result.getLastLocation();
                }
            }

            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle location engine failure
            }
        };

        // Request location updates
        locationEngine.requestLocationUpdates(new LocationEngineRequest.Builder(1000)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .build(), callback, getMainLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void addMarkers(Style style) {
        // Add marker for Kenny Fundi
        LatLng kennyFundiLatLng = new LatLng(-7.7776810, 35.6944910);
        SymbolOptions kennyFundiOptions = new SymbolOptions()
                .withLatLng(kennyFundiLatLng)
                .withIconImage("mapbox.png")
                .withIconSize(1.0f)
                .withTextAnchor("top")
                .withTextField("Kenny Fundi");
        symbolManager.create(kennyFundiOptions);

        // Add marker for Motorcycle Repair Ltd
        LatLng motorcycleRepairLatLng = new LatLng(-7.7803450, 35.6942440);
        SymbolOptions motorcycleRepairOptions = new SymbolOptions()
                .withLatLng(motorcycleRepairLatLng)
                .withIconImage("mapbox.png")
                .withIconSize(1.0f)
                .withTextAnchor("top")
                .withTextField("Motorcycle Repair Ltd");
        symbolManager.create(motorcycleRepairOptions);

        // Add marker for Mwijaku Garage
        LatLng mwijakuGarageLatLng = new LatLng(-7.7787500, 35.6943970);
        SymbolOptions mwijakuGarageOptions = new SymbolOptions()
                .withLatLng(mwijakuGarageLatLng)
                .withIconImage("mapbox.png")
                .withIconSize(1.0f)
                .withTextAnchor("top")
                .withTextField("Mwijaku Garage");
        symbolManager.create(mwijakuGarageOptions);

        // Add marker for HomeC
        LatLng homeCLatLng = new LatLng(-7.7748890, 35.6927280);
        SymbolOptions homeCOptions = new SymbolOptions()
                .withLatLng(homeCLatLng)
                .withIconImage("mapbox.png")
                .withIconSize(1.0f)
                .withTextAnchor("top")
                .withTextField("HomeC");
        symbolManager.create(homeCOptions);
    }
    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        // Present toast or dialog explaining why the permission is required
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                    addMarkers(style); // Add markers here
                }
            });
        } else {
            // Permission not granted, handle accordingly
        }
    }

    @Override
    public boolean onAnnotationClick(Symbol symbol) {
        // Handle symbol click event
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        if (destinationMarker != null) {
            mapboxMap.removeMarker(destinationMarker);
        }
        destinationPosition = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        destinationMarker = mapboxMap.addMarker(new MarkerOptions().position(point));
        startButton.setEnabled(true);
        startButton.setBackgroundResource(R.color.mapboxBlue);
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
