package rapid.responce.RUCU;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LatituLang extends AppCompatActivity {

    private Button subButton;
    private Button rejButton;

    private TextView locationTextView;
    private TextView send;
    private Button sending;
    private TextView automobile;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.latitude_lang);
        getSupportActionBar().hide();

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        locationTextView = findViewById(R.id.Location);

        automobile = findViewById(R.id.automobile);
        subButton = findViewById(R.id.submit);
        rejButton = findViewById(R.id.reject);
        sending = findViewById(R.id.sending);
        send = findViewById(R.id.send);

        // Retrieve the data passed from the previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String status = extras.getString("Status");
            String requestId = extras.getString("requestId");
            String senderId = extras.getString("senderID");
            String timestamp = String.valueOf(extras.getLong("TimeStamp"));
            String garageName = extras.getString("GarageName");

            // Set the retrieved data to the TextViews
            locationTextView.setText("YOUR REQUEST FROM: " + garageName);
            automobile.setText("HAS BEEN: " + status);

            // Conditionally activate the button if the status is "ACCEPTED" or "REJECTED"
            if (status.equals("ACCEPTED")) {
                subButton.setVisibility(View.VISIBLE);
                subButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LatituLang.this, ViewFeedback.class);
                        startActivity(intent);
                        finish();
                    }
                });

                sending.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String sendText = send.getText().toString().trim();

                        // Create a new document in the "locations" collection
                        String documentId = db.collection("locations").document().getId();
                        db.collection("locations").document(documentId).set(
                                        new LocationData(garageName, currentUser.getUid(), sendText)
                                )
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(LatituLang.this, "Data stored successfully", Toast.LENGTH_SHORT).show();
                                        // Return to ViewFeedback activity
                                        Intent intent = new Intent(LatituLang.this, ViewFeedback.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LatituLang.this, "Failed to store data", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            } else if (status.equals("REJECTED")) {
                rejButton.setVisibility(View.VISIBLE);
            }
        }
        rejButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to MapsActivity
                Intent intent = new Intent(LatituLang.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
