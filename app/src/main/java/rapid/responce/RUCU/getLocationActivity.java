package rapid.responce.RUCU;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class getLocationActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private Button addButton;
    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.latitude_longitude);

        addButton = findViewById(R.id.button6);
        latitudeEditText = findViewById(R.id.editTextText2);
        longitudeEditText = findViewById(R.id.editTextText3);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getLocationActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    obtainLocation();
                }
            }
        });
    }

    private void obtainLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    latitudeEditText.setText(String.valueOf(latitude));
                    longitudeEditText.setText(String.valueOf(longitude));
                    saveLocationToDatabase(latitude, longitude);
                } else {
                    Toast.makeText(getLocationActivity.this, "Failed to obtain location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveLocationToDatabase(double latitude, double longitude) {
        // Your code to save the latitude and longitude to the database
        // Here you can use the previous code snippet to save the location to a real-time database
        // or any other database of your choice
        Toast.makeText(getLocationActivity.this, "Location saved: " + latitude + ", " + longitude, Toast.LENGTH_SHORT).show();
    }
}
