package rapid.responce.RUCU;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_SELECT = 2;
    private static final int PERMISSION_REQUEST_CODE = 3;

    private CircleImageView imageView;

    private Uri imageUri;
    private Bitmap originalBitmap;
    private Button mLastNameButton;
    private Button mPhoneButton;
    private Button regionButton;
    private  Button updateProfile;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        updateProfile=findViewById(R.id.button9);

       updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to move to the target activity
                Intent intent = new Intent(ProfileActivity.this, Edit_Password.class);
                startActivity(intent);
            }
        });
        imageView = findViewById(R.id.profile_image);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Construct the file name based on the user's ID
        String fileName = "profile_picture_" + userId + ".jpg";
        File file = new File(getFilesDir(), fileName);
        if (file.exists()) {
            // Load the image from the file and set it to the ImageView
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        } else {
            // The image file does not exist for the user
            // You can set a default image or take appropriate action
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageOptions();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();

            // Get a reference to the Realtime Database
            mDatabase = FirebaseDatabase.getInstance().getReference();

            // Get references to the Button fields
            mLastNameButton = findViewById(R.id.button4);
            mPhoneButton = findViewById(R.id.button5);
            regionButton=findViewById(R.id.button7);


            // Get the unique ID of the currently logged in user
             FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Retrieve the last name and phone number of the user from the Realtime Database
            mDatabase.child("UserData").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Get the user object
                    User user = dataSnapshot.getValue(User.class);

                    // Set the last name and phone number in the EditText fields
                    mLastNameButton.setText(user.getLastName().toString());
                    mPhoneButton.setText(user.getPhone().toString());
                    regionButton.setText(user.getRegion().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors
                }
            });
            bottomNavigationView = findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setSelectedItemId(R.id.profile);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {


                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.home:
                            startActivity(new Intent(ProfileActivity.this, MapsActivity.class));
                            return true;
                        case R.id.garage:
                            startActivity(new Intent(ProfileActivity.this,SearchActivity.class));
                            return true;
                        case R.id.profile:
                            startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                            return true;
                        case R.id.settings:
                            // Handle settings item click
                            return true;
                    }
                    return false;
                }
            });

        }

    }

    private void showImageOptions() {
        // Check for camera permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        } else {
            // Show options dialog
            showOptionsDialog();
        }
    }

    private void showOptionsDialog() {
        // Display dialog to choose between camera and storage
        // You can implement your own custom dialog or use libraries like AlertDialog or DialogFragment

        // Example: Using AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        takePhoto();
                        break;
                    case 1:
                        selectImage();
                        break;
                }
            }
        });
        builder.show();
    }

    private void takePhoto() {
        // Launch camera activity to capture an image
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Image from Camera");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void selectImage() {
        // Launch gallery activity to select an image
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_SELECT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Image captured from camera
                originalBitmap = loadBitmapFromUri(imageUri);
                imageView.setImageBitmap(originalBitmap);
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                storeImageLocally(originalBitmap, userId); // Store the image locally
            } else if (requestCode == REQUEST_IMAGE_SELECT && data != null) {
                // Image selected from gallery
                Uri selectedImageUri = data.getData();
                originalBitmap = loadBitmapFromUri(selectedImageUri);
                imageView.setImageBitmap(originalBitmap);
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                storeImageLocally(originalBitmap, userId); // Store the image locally
            }
        }
    }



    private Bitmap loadBitmapFromUri(Uri imageUri) {
        Bitmap bitmap = null;
        try {
            ContentResolver resolver = getContentResolver();
            InputStream inputStream = resolver.openInputStream(imageUri);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void storeImageLocally(Bitmap imageBitmap, String userId) {
        if (imageBitmap != null) {
            // Save the image bitmap to the app's internal storage
            String fileName = "profile_picture_" + userId + ".jpg";
            File file = new File(getFilesDir(), fileName);

            try {
                FileOutputStream fos = new FileOutputStream(file);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                Toast.makeText(ProfileActivity.this, "Profile Picture Updated", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ProfileActivity.this, "Failed to Update Profile Picture", Toast.LENGTH_SHORT).show();
            }
        }
    }



}
