package rapid.responce.RUCU;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ViewRequests extends AppCompatActivity {
    private TextView nameTextView;
    String accept= "ACCEPTED";
    String reject = "REJECTED";
    private Button subButton;
    private TextView locationt;
    private  Button rejButton;
    private TextView locationTextView;
    private TextView problemTextView;
    private TextView automobile;
    String X;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_requests);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        nameTextView = findViewById(R.id.name);
        locationTextView = findViewById(R.id.Location);
        problemTextView = findViewById(R.id.problem);
        automobile = findViewById(R.id.automobile);
        subButton = findViewById(R.id.submit);
        rejButton = findViewById(R.id.reject);
        locationt=findViewById(R.id.latt);

        Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("name");
            String location = intent.getStringExtra("location");
            String automob = intent.getStringExtra("automobileProblem");
            String problem = intent.getStringExtra("problemDescription");
            String SenderID = intent.getStringExtra("SenderID");
            String RequestID = intent.getStringExtra("RequestID");
            String TimeStamp = intent.getStringExtra("TimeStamp");

            nameTextView.setText("NAME" + "    " + name);
            locationTextView.setText("LOCATION" + "    " + location);
            problemTextView.setText("PROBLEM IN SHORT" + "    " + problem);
            automobile.setText("TYPE OF AUTOMOBILE" + "    " + automob);

            subButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Store the data in Firestore with the document ID as SenderID
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference docRef = db.collection("Approval").document(RequestID);

                    // Create a new map to hold the data
                    Map<String, Object> data = new HashMap<>();
                    data.put("RequestID", RequestID);
                    data.put("SenderID", SenderID);
                    data.put("TimeStamp", TimeStamp);
                    data.put("status", accept);
                    data.put("garageName",X);

                    docRef.set(data)
                            .addOnSuccessListener(aVoid -> {
                                // Show success message
                                Toast.makeText(ViewRequests.this, "Data stored successfully", Toast.LENGTH_SHORT).show();

                                // Move back to garageHome activity
                                Intent intent = new Intent(ViewRequests.this, garageHome.class);
                                startActivity(intent);
                                finish(); // Optional: Call finish() to close the current activity
                            })
                            .addOnFailureListener(e -> {
                                // Show error message
                                Toast.makeText(ViewRequests.this, "Error storing data", Toast.LENGTH_SHORT).show();
                            });
                }
            });
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                String userID = currentUser.getUid();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("UserGarages").document(userID);
                docRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String garageName = documentSnapshot.getString("garageName");
                        // Use the garageName as needed
                         X = garageName;
                    } else {
                        // Document does not exist
                    }
                }).addOnFailureListener(e -> {
                    // Error occurred while fetching document
                });
            }

            rejButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subButton.setVisibility(View.INVISIBLE);
                    rejButton.setVisibility(View.INVISIBLE);
                    locationt.setVisibility(View.VISIBLE);

                    // Store the data in Firestore with the document ID as SenderID
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference docRef = db.collection("Approval").document(RequestID);

                    // Create a new map to hold the data
                    Map<String, Object> data = new HashMap<>();
                    data.put("RequestID", RequestID);
                    data.put("SenderID", SenderID);
                    data.put("TimeStamp", TimeStamp);
                    data.put("status", reject);
                    data.put("garageName",X);
                    docRef.set(data)
                            .addOnSuccessListener(aVoid -> {
                                // Show success message
                                Toast.makeText(ViewRequests.this, "Data stored successfully", Toast.LENGTH_SHORT).show();

                                // Move back to garageHome activity
                                Intent intent = new Intent(ViewRequests.this, garageHome.class);
                                startActivity(intent);
                                finish(); // Optional: Call finish() to close the current activity
                            })
                            .addOnFailureListener(e -> {
                                // Show error message
                                Toast.makeText(ViewRequests.this, "Error storing data", Toast.LENGTH_SHORT).show();
                            });
                }
            });
        }
        }
}
