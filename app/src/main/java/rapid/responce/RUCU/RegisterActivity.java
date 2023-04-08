package rapid.responce.RUCU;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    CheckBox showPasswordCheckBox;

    private EditText firstName;
    private EditText secondName;
    private EditText lastName;
    private EditText region;
    private EditText district;
    private EditText address;
    private EditText phoneNumber;
    private TextView role;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
            showPasswordCheckBox = findViewById(R.id.passCheck);
            confirmPasswordEditText = findViewById(R.id.pass2);
            showPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // If the CheckBox view is checked, show the password
                    if (isChecked) {
                        passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        confirmPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    }
                    // If the CheckBox view is unchecked, hide the password
                    else {
                        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }

                    // Invalidate the EditText views to redraw and show the updated input type
                    passwordEditText.invalidate();
                    confirmPasswordEditText.invalidate();
                }
            });
            emailEditText = findViewById(R.id.email);
            passwordEditText = findViewById(R.id.pass1);
            registerButton = findViewById(R.id.button_register);
            firstName = findViewById(R.id.name_first);
            secondName = findViewById(R.id.name_second);
            lastName = findViewById(R.id.name_third);
            region = findViewById(R.id.region);
            district = findViewById(R.id.district);
            address = findViewById(R.id.address);
            phoneNumber = findViewById(R.id.number);
            role = findViewById(R.id.textRole);
            showPasswordCheckBox = findViewById(R.id.passCheck);
            confirmPasswordEditText = findViewById(R.id.pass2);
            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("UserData");
            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = emailEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();
                    String first_name = firstName.getText().toString().trim();
                    String second_name = secondName.getText().toString().trim();
                    String last_name = lastName.getText().toString().trim();
                    String Region = region.getText().toString().trim();
                    String District = district.getText().toString().trim();
                    String Address = address.getText().toString().trim();
                    String Phone = phoneNumber.getText().toString().trim();
                    String Role = role.getText().toString().trim();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Create a User object with the additional data
                                        User user = new User(email, password,Role, first_name, second_name, last_name, Region, District, Address, Phone);

                                        // Get the unique ID of the newly registered user
                                        String userId = mAuth.getCurrentUser().getUid();

                                        // Store the user object in the Realtime Database under "UserData"
                                        DatabaseReference userRef = mDatabase.child("UserData").child(userId);
                                        userRef.setValue(user);

                                        new AlertDialog.Builder(RegisterActivity.this)
                                                .setTitle("Registration Successful")
                                                .setMessage("You have successfully registered.")
                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(RegisterActivity.this, loginActivity.class);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .setIcon(android.R.drawable.ic_dialog_info)
                                                .show();
                                    } else {
                                        new AlertDialog.Builder(RegisterActivity.this)
                                                .setTitle("Registration Failed")
                                                .setMessage("Registration failed.")
                                                .setPositiveButton(android.R.string.ok, null)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();
                                    }

                                }
                            });


                }
            });
        }

    }
}
