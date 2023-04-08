package rapid.responce.RUCU;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class loginGarage extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        // Do nothing
    }

    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        databaseRef = FirebaseDatabase.getInstance().getReference();

        // Get references to the email and password EditText fields
        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);

        // Get reference to the sign-in button
        Button signInButton = findViewById(R.id.login);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Authenticate the user
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                Query query = databaseRef.child("UserGarages").orderByChild("email").equalTo(email);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Retrieve the user data from the snapshot
                            DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();
                            Log.d("DataSnapshot", dataSnapshot.toString());

                            User user = userSnapshot.getValue(User.class);

                            // Check if the entered password matches the password stored in the database
                            if (user.getPassword().equals(password)) {
                                // Authentication successful
                                Intent intent = new Intent(loginGarage.this, myGarage.class);
                                startActivity(intent);
                                // Authentication successful

                            } else {
                                new AlertDialog.Builder(loginGarage.this)
                                        .setTitle("INVALID DATA")
                                        .setMessage("invalid email or password.")
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(loginGarage.this, loginGarage.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .show();
                            }
                        } else {
                            new AlertDialog.Builder(loginGarage.this)
                                    .setTitle("INVALID USER")
                                    .setMessage("Your not a valid user.")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(loginGarage.this, loginGarage.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                        Toast.makeText(loginGarage.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        TextView textView = findViewById(R.id.sign_up);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(loginGarage.this, RegisterGarageActivity.class);
                startActivity(intent);
            }
        });
    }
}
