package rapid.responce.RUCU;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class myGarage extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mygarage);

        // Retrieve the User object from the Intent
        UserData user = getIntent().getParcelableExtra("user");

        // Get references to button1 and button2
        Button button1 = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button2);

        // Set the text of button1 to the username
        button1.setText(user.getGarageName());

        // Set the text of button2 to the region
        button2.setText(user.getRegion());

        bottomNavigationView = findViewById(R.id.bottomNavigationView1);
        bottomNavigationView.setSelectedItemId(R.id.garage);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {


            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(myGarage.this, garageHome.class));
                        return true;
                    case R.id.garage:
                        startActivity(new Intent(myGarage.this,myGarage.class));
                        return true;

                    case R.id.settings:
                        // Handle settings item click
                        return true;
                }
                return false;
            }
        });

    }
}
