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

public class RegisterGarageActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    CheckBox showPasswordCheckBox;

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
    private DatabaseReference mDatabase;

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
            mDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("UserGarages");
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
                    String Role = role.getText().toString().trim();
                    String nida = Nida_number.getText().toString().trim();
                    String tin = Tin_number.getText().toString().trim();

                    // Perform password validation
                    if (!isValidPassword(password)) {
                        // Password validation failed
                        new AlertDialog.Builder(RegisterGarageActivity.this)
                                .setTitle("Invalid Password")
                                .setMessage("Password must be at least eight characters long and contain at least one lowercase letter, one uppercase letter, and one numerical digit.")
                                .setPositiveButton(android.R.string.ok, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return;
                    }

                    // Perform Nida number validation
                    if (!isValidNidaNumber(nida)) {
                        // Nida number validation failed
                        new AlertDialog.Builder(RegisterGarageActivity.this)
                                .setTitle("Invalid Nida Number")
                                .setMessage("Nida number must contain only digits and have a length of exactly 20 digits.")
                                .setPositiveButton(android.R.string.ok, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return;
                    }
                    if (!isValidRegion(Region)) {
                        // Region validation failed
                        new AlertDialog.Builder(RegisterGarageActivity.this)
                                .setTitle("Invalid Region")
                                .setMessage("Please enter a valid region in Tanzania.")
                                .setPositiveButton(android.R.string.ok, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return;
                    }

                    if (!isValid(garage)) {
                        // Nida number validation failed
                        new AlertDialog.Builder(RegisterGarageActivity.this)
                                .setTitle("Invalid Garage Name")
                                .setMessage("Garage Name must contain At least three letter.")
                                .setPositiveButton(android.R.string.ok, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return;
                    }

                    if (!isValid(company)) {
                        // Nida number validation failed
                        new AlertDialog.Builder(RegisterGarageActivity.this)
                                .setTitle("Invalid Company Name")
                                .setMessage("Company Name must contain At least three letter.")
                                .setPositiveButton(android.R.string.ok, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return;
                    }
                    if (!isValid(garage)) {
                        // Nida number validation failed
                        new AlertDialog.Builder(RegisterGarageActivity.this)
                                .setTitle("Invalid Nida Number")
                                .setMessage("Enter a Valid nida Number.")
                                .setPositiveButton(android.R.string.ok, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return;
                    }
                    if (!isValidFullName(fullname)) {
                        // Full name validation failed
                        new AlertDialog.Builder(RegisterGarageActivity.this)
                                .setTitle("Invalid Full Name")
                                .setMessage("Full name must have at least six letters with no digits and no spaces between two names.")
                                .setPositiveButton(android.R.string.ok, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return;
                    }
                    if (!isValidTanzaniaPhoneNumber(Phone)) {
                        // Phone number validation failed
                        new AlertDialog.Builder(RegisterGarageActivity.this)
                                .setTitle("Invalid Phone Number")
                                .setMessage("Phone number must be a valid format.")
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
                                        // Create a User object with the additional data
                                        UserData user = new UserData(email, password, garage, company, fullname, Region, District, Address, Phone, nida, tin, Role);

                                        // Get the unique ID of the newly registered user
                                        String userId = mAuth.getCurrentUser().getUid();

                                        // Store the user object in the Realtime Database under "UserData"
                                        DatabaseReference userRef = mDatabase.child("UserGarages").child(userId);
                                        userRef.setValue(user);

                                        // Store the garage name in the "Searchh" path with the user ID as the key
                                        DatabaseReference searchhRef = mDatabase.child("Search").child(userId).child("garageName");
                                        searchhRef.setValue(garage);

                                        new AlertDialog.Builder(RegisterGarageActivity.this)
                                                .setTitle("Registration Successful")
                                                .setMessage("You have successfully registered.")

                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(RegisterGarageActivity.this, my_name.class);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .setIcon(android.R.drawable.ic_dialog_info)
                                                .show();
                                    } else {
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