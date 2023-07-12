package rapid.responce.RUCU;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

public class activity_set_request extends AppCompatActivity {
    private EditText username;
    private EditText locationDescription;
    private EditText problemDescription;
    private EditText automobileType;

    private Button confirmButton;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath;
    private String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_request);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        username = findViewById(R.id.name);
        locationDescription = findViewById(R.id.Location);
        confirmButton = findViewById(R.id.submit);
        problemDescription = findViewById(R.id.problem);
        automobileType = findViewById(R.id.automobile);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Create a reference to the "requests" collection
        CollectionReference requestsRef = firestore.collection("requests");

        // Get the item ID passed from RequestActivity
        itemId = getIntent().getStringExtra("itemId");

        confirmButton.setOnClickListener(view -> {
            // Get the input values
            String name = username.getText().toString().trim();
            String location = locationDescription.getText().toString().trim();
            String problem = problemDescription.getText().toString().trim();
            String automobile = automobileType.getText().toString().trim();
            String userId = auth.getCurrentUser().getUid();

            // Create a new document in the "requests" collection
            DocumentReference requestRef = requestsRef.document();
            String requestId = requestRef.getId();

            // Get the current timestamp
            Date currentTime = Calendar.getInstance().getTime();

            // Create a Request object with the current timestamp
            Request request = new Request(requestId, itemId, userId, name, location, problem, automobile, currentTime);

            // Store the Request object in Firestore
            requestRef.set(request)
                    .addOnSuccessListener(documentReference -> {
                        // Request stored successfully
                        // Perform any additional actions or show a success message
                        Toast.makeText(activity_set_request.this, "Request sent successfully", Toast.LENGTH_SHORT).show();

                        // Start the SearchActivity
                        Intent intent = new Intent(activity_set_request.this, SearchActivity.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        // Error occurred while storing the request
                        // Handle the error or show an error message
                        Toast.makeText(activity_set_request.this, "Failed to send request", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
