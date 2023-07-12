package rapid.responce.RUCU;// RequestActivity.java
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import rapid.responce.RUCU.activity_set_request;

public class RequestActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button request;
    private Button call;
    private  Button sms;
    private Button whatsapp;
    private BottomNavigationView bottomNavigationView;
    private TextView nameTextView;
    private TextView contactTextView;
    private TextView locationTextView;
    private TextView descriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(RequestActivity.this, MapsActivity.class));
                        return true;
                    case R.id.garage:
                        startActivity(new Intent(RequestActivity.this,SearchActivity.class));
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(RequestActivity.this, ProfileActivity.class));

                        return true;
                    case R.id.settings:
                        startActivity(new Intent(RequestActivity.this, SettingActivity.class));
                        return true;
                }
                return false;
            }
        });

        request = findViewById(R.id.requesting);
        call = findViewById(R.id.direct);
        sms = findViewById(R.id.messagee);
        whatsapp = findViewById(R.id.whatsapp);
        imageView = findViewById(R.id.garageImage);
        nameTextView = findViewById(R.id.textView3);
        contactTextView = findViewById(R.id.textView4);
        locationTextView = findViewById(R.id.textView5);
        descriptionTextView = findViewById(R.id.textView6);

        Intent intent = getIntent();
        if (intent != null) {
            String image = intent.getStringExtra("imageUrl");
            String garageName = intent.getStringExtra("garageName");
            String contact = intent.getStringExtra("phone");
            String region = intent.getStringExtra("region");
            String description = intent.getStringExtra("status");

            // Set the data in the views
            if (image != null && !image.isEmpty()) {
                Picasso.get().load(image).into(imageView);
            }
            nameTextView.setText(garageName);
            contactTextView.setText(contact);
            locationTextView.setText(region);
            descriptionTextView.setText(description);
        }

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = contactTextView.getText().toString();
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:" + phoneNumber));
                startActivity(intent);
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = contactTextView.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            }
        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = contactTextView.getText().toString();
                phoneNumber = formatPhoneNumber(phoneNumber); // Format the phone number if necessary
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber));
                startActivity(intent);
            }
        });

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemId = getIntent().getStringExtra("id");
                Intent intent = new Intent(RequestActivity.this, activity_set_request.class);
                intent.putExtra("itemId", itemId);
                startActivity(intent);
            }
        });
    }

    private String formatPhoneNumber(String phoneNumber) {
        // Add country code if the phone number doesn't start with it
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = "+255" + phoneNumber; // Replace with the desired country code
        }
        return phoneNumber;
    }
}
