package rapid.responce.RUCU;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private CheckBox showPasswordCheckBox;

    private EditText lastName;
    private EditText region;
    private ImageView image;
    private EditText phoneNumber;

    private Button registerButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

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
        image = findViewById(R.id.myinvisible);
        lastName = findViewById(R.id.name_third);
        region = findViewById(R.id.region);
        phoneNumber = findViewById(R.id.number);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String last_name = lastName.getText().toString().trim();
                String regionValue = region.getText().toString().trim();
                String phone = phoneNumber.getText().toString().trim();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Create a User object with the additional data
                                    User user = new User(email, password, last_name, regionValue, phone, currentPhotoPath);

                                    // Get the unique ID of the newly registered user
                                    String userId = mAuth.getCurrentUser().getUid();

                                    // Store the user object in the Firestore under "UserData" collection
                                    mFirestore.collection("UserData")
                                            .document(userId)
                                            .set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    new AlertDialog.Builder(RegisterActivity.this)
                                                            .setTitle("Registration Successful")
                                                            .setMessage("You have successfully registered.")
                                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Intent intent = new Intent(RegisterActivity.this, MapsActivity.class);
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
                                                    new AlertDialog.Builder(RegisterActivity.this)
                                                            .setTitle("Registration Failed")
                                                            .setMessage("Registration failed.")
                                                            .setPositiveButton(android.R.string.ok, null)
                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                            .show();
                                                }
                                            });
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

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("RegisterActivity", "Error creating image file: " + ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "your.package.name.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Display the captured image using Glide library
            Glide.with(this)
                    .load(currentPhotoPath)
                    .apply(RequestOptions.centerCropTransform())
                    .into(image);
        }
    }
}
