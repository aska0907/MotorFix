package rapid.responce.RUCU;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

public class Edit_Password extends AppCompatActivity {

    private EditText oldPasswordEditText, newPasswordEditText;
    private Button changePasswordButton;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_paswword);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        oldPasswordEditText = findViewById(R.id.Opassword);
        newPasswordEditText = findViewById(R.id.Npassword);
        changePasswordButton = findViewById(R.id.EditP);
        TextView textView2 = findViewById(R.id.forgott_password);
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Edit_Password.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
        // Set click listener for change password button
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    private void changePassword() {
        String oldPassword = oldPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(oldPassword)) {
            Toast.makeText(this, "Please enter your old password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Please enter your new password", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get the current user ID

        DatabaseReference userRef = databaseRef.child("UserData").child(userId);
        userRef.child("password").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String storedPassword = dataSnapshot.getValue(String.class);
                if (storedPassword != null && storedPassword.equals(oldPassword)) {
                    userRef.child("password").setValue(newPassword)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Edit_Password.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                    clearFields();
                                    navigateToProfileActivity(); // Navigate to the ProfileActivity

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Edit_Password.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(Edit_Password.this, "Invalid old password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Edit_Password.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearFields() {
        oldPasswordEditText.setText("");
        newPasswordEditText.setText("");
    }
    private void navigateToProfileActivity() {
        Intent intent = new Intent(Edit_Password.this, ProfileActivity.class);
        startActivity(intent);
    }
}
