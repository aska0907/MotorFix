package rapid.responce.RUCU;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class my_name extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mDescriptionEditText;
    private  EditText mPhoneNumber;
    private Button mSaveButton;

    // Define the Firebase database reference
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_name);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // Initialize the Firebase database reference
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Search");

        mNameEditText = findViewById(R.id.myName);
        mDescriptionEditText = findViewById(R.id.description);
        mSaveButton = findViewById(R.id.myButton);
        mPhoneNumber=findViewById(R.id.myNumber);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveName();
            }
        });
    }

    private void saveName() {
        String name = mNameEditText.getText().toString().trim().toLowerCase();
        String description = mDescriptionEditText.getText().toString().trim();
        String phone=mPhoneNumber.getText().toString().trim();
        // Check if name and description are not empty
        if (!name.isEmpty() && !description.isEmpty() && !phone.isEmpty()) {
            // Get a new unique key for the user
            String userId = mDatabaseRef.push().getKey();

            // Save the name and description under the user's key
            mDatabaseRef.child(userId).child("name").setValue(name);
            mDatabaseRef.child(userId).child("description").setValue(description);
            mDatabaseRef.child(userId).child("phone").setValue(phone);
            Toast.makeText(this, "saved successfully", Toast.LENGTH_SHORT).show();
            mNameEditText.setText("");
            mDescriptionEditText.setText("");
            mPhoneNumber.setText("");
            Intent intent = new Intent(this, loginActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Please enter a name and description", Toast.LENGTH_SHORT).show();
        }
    }
}

