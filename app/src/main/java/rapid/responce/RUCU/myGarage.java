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
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class myGarage extends AppCompatActivity {
    private FirebaseFirestore mFirestore;
    private ImageView imageView;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_SELECT = 2;
    private static final int PERMISSION_REQUEST_CODE = 3;

    private Uri imageUri;
    private Bitmap originalBitmap;
    private Button mLastNameButton;
    private Button mPhoneButton;
    private Button regionButton;
    private Button updateProfile;
    private BottomNavigationView bottomNavigationView;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mygarage);
        updateProfile = findViewById(R.id.button9);

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to move to the target activity
                Intent intent = new Intent(myGarage.this, Edit_Password.class);
                startActivity(intent);
            }
        });
        imageView = findViewById(R.id.garage_image);

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Load the user's profile image using Glide library
        mFirestore.collection("UserGarages")
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            String imageUrl = user.getImageUrl();

                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                // Load the image using Glide library
                                Glide.with(myGarage.this)
                                        .load(imageUrl)

                                        .into(imageView);
                            } else {
                                // The user's profile image is not available
                                // You can set a default image or take appropriate action
                            }
                        } else {
                            // The document does not exist
                            // Handle the case when the user data is not available
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors
                    }
                });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageOptions();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();

            // Get references to the Button fields
            mLastNameButton = findViewById(R.id.mygarage);
            mPhoneButton = findViewById(R.id.button5);
            regionButton = findViewById(R.id.button7);

            mFirestore.collection("UserGarages")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                UserData user = documentSnapshot.toObject(UserData.class);
                                if (user != null && user.getGarageName() != null) {
                                    mLastNameButton.setText(user.getGarageName());
                                }
                                if (user != null && user.getPhone() != null) {
                                    mPhoneButton.setText(user.getPhone());
                                }
                                if (user != null && user.getRegion() != null) {
                                    regionButton.setText(user.getRegion());
                                }
                            } else {
                                // The document does not exist
                                // Handle the case when the user data is not available
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle any errors
                        }
                    });

            bottomNavigationView = findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setSelectedItemId(R.id.garage);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.home:
                            startActivity(new Intent(myGarage.this, garageHome.class));
                            return true;

                        case R.id.garage:
                            startActivity(new Intent(myGarage.this, myGarage.class));
                            return true;
                        case R.id.settings:
                            startActivity(new Intent(myGarage.this, SettingActivity2.class));
                            return true;
                    }
                    return false;
                }
            });

        }

        // Retrieve the profile picture from storage
        File directory = getApplicationContext().getDir("profile_pictures", MODE_PRIVATE);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith(userId)) {
                    Bitmap profileBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    imageView.setImageBitmap(profileBitmap);
                    break;
                }
            }
        }
    }

    private void showImageOptions() {
        // Check for camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
            values.put(MediaStore.Images.Media.TITLE, "temp");
            values.put(MediaStore.Images.Media.DESCRIPTION, "temp image");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void selectImage() {
        // Launch gallery activity to select an image
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_SELECT);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Save image to the app's directory
        File storageDir = getApplicationContext().getDir("profile_pictures", MODE_PRIVATE);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && imageUri != null) {
                try {
                    originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    saveProfilePicture(originalBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_IMAGE_SELECT && data != null && data.getData() != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    originalBitmap = BitmapFactory.decodeStream(inputStream);
                    saveProfilePicture(originalBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ...

    private void saveProfilePicture(Bitmap bitmap) {
        if (bitmap != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Save the bitmap to storage
            try {
                File imageFile = createImageFile();
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();

                // Upload the image to Firebase Cloud Storage
                String fileName = userId + "_" + imageFile.getName();
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("GaragePictures").child(fileName);
                storageRef.putFile(Uri.fromFile(imageFile))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get the image URL from the uploaded file
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();

                                        // Update the profile picture URL in Firestore
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("imageUrl", imageUrl);

                                        mFirestore.collection("UserGarages")
                                                .document(userId)
                                                .set(data, SetOptions.merge())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Update the image view with the new profile picture
                                                        imageView.setImageBitmap(bitmap);
                                                        Toast.makeText(myGarage.this, "Profile picture saved successfully.", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Handle any errors
                                                    }
                                                });
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle any errors
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

// ...




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, show options dialog
                showOptionsDialog();
            } else {
                // Permission denied
                Toast.makeText(this, "Camera permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
