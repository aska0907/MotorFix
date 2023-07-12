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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterGarageActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    CheckBox showPasswordCheckBox;
     private String Uid;
     private String Status;
     private  String ImageUrl;
    private EditText Garage_name;
    private EditText Garage_company;
    private EditText Full_name;
    private EditText region;
    private EditText district;
    private EditText address;
    private EditText phoneNumber;
    private EditText Nida_number;
    private EditText Tin_number;
    private TextView role;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garage_register);
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
            Garage_name = findViewById(R.id.garage_name);
            Garage_company = findViewById(R.id.company_name);
            Full_name = findViewById(R.id.full_name);
            Nida_number = findViewById(R.id.nida);
            Tin_number = findViewById(R.id.tin);
            region = findViewById(R.id.region);
            district = findViewById(R.id.district);
            address = findViewById(R.id.address);
            phoneNumber = findViewById(R.id.number);
            role = findViewById(R.id.textRole);
            showPasswordCheckBox = findViewById(R.id.passCheck);
            confirmPasswordEditText = findViewById(R.id.pass2);
            mAuth = FirebaseAuth.getInstance();
            mFirestore = FirebaseFirestore.getInstance();
            CollectionReference usersCollectionRef = mFirestore.collection("UserGarages");

            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = emailEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();
                    String garage = Garage_name.getText().toString().trim();
                    String company = Garage_company.getText().toString().trim();
                    String fullname = Full_name.getText().toString().trim();
                    String Region = region.getText().toString().trim();
                    String District = district.getText().toString().trim();
                    String Address = address.getText().toString().trim();
                    String Phone = phoneNumber.getText().toString().trim();

                    String nida = Nida_number.getText().toString().trim();
                    String tin = Tin_number.getText().toString().trim();

                    // Perform password validation
                    if (!isValidPassword(password)) {
                        // Password validation failed
                        new AlertDialog.Builder(RegisterGarageActivity.this)
                                .setTitle("Invalid Password")
                                .setMessage("Password must be at least eight characters long and contain at least one lowercase letter, one uppercase letter, and one digit.")
                                .setPositiveButton(android.R.string.ok, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return;
                    }

                    // Perform password confirmation validation
                    if (!password.equals(confirmPasswordEditText.getText().toString().trim())) {
                        // Password confirmation validation failed
                        new AlertDialog.Builder(RegisterGarageActivity.this)
                                .setTitle("Password Mismatch")
                                .setMessage("The entered passwords do not match.")
                                .setPositiveButton(android.R.string.ok, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return;
                    }

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String userId = mAuth.getCurrentUser().getUid();
                                        Uid = userId;

                                        UserData user = new UserData(email,password,garage, company,
                                                fullname, Region, District, Address, Phone,nida,tin,Uid,ImageUrl,Status);

                                        // Store the user object in Cloud Firestore under "UserGarages" collection
                                        usersCollectionRef.document(userId)
                                                .set(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Store the garage name in the "Search" path with the user ID as the key
                                                        mFirestore.collection("Search")
                                                                .document(userId)
                                                                .set(new HashMap<String, Object>() {
                                                                    {
                                                                        put("garageName", garage);
                                                                    }
                                                                })
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        // Show registration success dialog and navigate to the next activity
                                                                        new AlertDialog.Builder(RegisterGarageActivity.this)
                                                                                .setTitle("Registration Successful")
                                                                                .setMessage("You have successfully registered.")
                                                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                        Intent intent = new Intent(RegisterGarageActivity.this, loginActivity.class);
                                                                                        startActivity(intent);
                                                                                    }
                                                                                })
                                                                                .setIcon(android.R.drawable.ic_dialog_info)
                                                                                .show();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        // Show registration failure dialog
                                                                        new AlertDialog.Builder(RegisterGarageActivity.this)
                                                                                .setTitle("Registration Failed")
                                                                                .setMessage("Failed to store garage name in the 'Search' collection.")
                                                                                .setPositiveButton(android.R.string.ok, null)
                                                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                                                .show();
                                                                    }
                                                                });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Show registration failure dialog
                                                        new AlertDialog.Builder(RegisterGarageActivity.this)
                                                                .setTitle("Registration Failed")
                                                                .setMessage("Failed to store user data in the 'UserGarages' collection.")
                                                                .setPositiveButton(android.R.string.ok, null)
                                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                                .show();
                                                    }
                                                });
                                    } else {
                                        // Show registration failure dialog
                                        new AlertDialog.Builder(RegisterGarageActivity.this)
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
    private boolean isValidPassword(String password) {
        // Regex pattern to match the password requirements
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";

        // Check if the password matches the pattern
        return password.matches(passwordPattern);
    }
    private boolean isValidNidaNumber(String nida) {
        // Check if the Nida number contains only digits and has a length of exactly 20 digits
        if (!nida.matches("\\d{20}")) {
            return false;
        }

        // Check if the Nida number starts with 1 or 2
        if (!nida.startsWith("1") && !nida.startsWith("2")) {
            return false;
        }

        // Check if the fifth and sixth digits represent a valid month (01 to 12)
        String monthDigits = nida.substring(4, 6);
        int month = Integer.parseInt(monthDigits);
        if (month < 1 || month > 12) {
            return false;
        }

        // Check if the seventh and eighth digits represent a valid day (01 to 31)
        String dayDigits = nida.substring(6, 8);
        int day = Integer.parseInt(dayDigits);
        if (day < 1 || day > 31) {
            return false;
        }

        // All checks passed, the Nida number is valid
        return true;
    }


    private boolean isValidFullName(String fullName) {
        // Regex pattern to match the full name requirements
        String fullNamePattern = "^[A-Za-z]{6,}(?: [A-Za-z]{6,})*$";

        // Check if the full name matches the pattern
        return fullName.matches(fullNamePattern);
    }
    private boolean isValidTanzaniaPhoneNumber(String phoneNumber) {
        // Regex pattern to match the Tanzania phone number format
        String phoneNumberPattern = "^(\\+?255|0)[67]\\d{8}$";

        // Check if the phone number matches the pattern
        return phoneNumber.matches(phoneNumberPattern);
    }
    private boolean isValid(String district) {
        // Regex pattern to match the district requirements
        String districtPattern = "^[A-Za-z]{3,}$";

        // Check if the district matches the pattern
        return district.matches(districtPattern);
    }
    private boolean isValidRegion(String region) {
        // Define a list of valid regions in Tanzania
        String[] validRegions = {"Arusha", "Dar es Salaam", "Dodoma", "Geita", "Iringa", "Kagera", "Katavi", "Kigoma", "Kilimanjaro", "Lindi", "Manyara", "Mara", "Mbeya", "Morogoro", "Mtwara", "Mwanza", "Njombe", "Pemba Kaskazini", "Pemba Kusini", "Pwani", "Rukwa", "Ruvuma", "Shinyanga", "Simiyu", "Singida", "Songwe", "Tabora", "Tanga", "Unguja Kaskazini", "Unguja Kusini", "Unguja Mjini Magharibi", "Unguja Mjini Magharibi", "Unguja Mjini Magharibi"};

        // Convert the entered region to lowercase for case-insensitive comparison
        String regionLowerCase = region.toLowerCase();

        // Check if the entered region is in the list of valid regions
        for (String validRegion : validRegions) {
            if (validRegion.toLowerCase().equals(regionLowerCase)) {
                return true;
            }
        }

        return false;
    }

}