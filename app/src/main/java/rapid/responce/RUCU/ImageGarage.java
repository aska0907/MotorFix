package rapid.responce.RUCU;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageGarage extends AppCompatActivity {

    private ImageView imageView;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mygarage);

        // Initialize ImageView
        imageView = findViewById(R.id.garage_image);

        // Start the image selection process
        selectImage();
    }

    private void selectImage() {
        // You can display a dialog or use any other method to let the user choose the image source (storage or camera)
        // For this example, we'll start the image selection from storage
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // The user has selected an image from storage
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
                saveImageToFirestore();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            // The user has captured an image using the camera
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(bitmap);
            saveImageToFirestore();
        }
    }

    private void saveImageToFirestore() {
        // Convert image in ImageView to Bitmap
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        // Compress Bitmap into a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        // Initialize Firestore instance
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Create a new document reference in the "images" collection
        CollectionReference imagesCollection = firestore.collection("images");
        DocumentReference imageRef = imagesCollection.document();

        // Upload the image to Firestore
        imageRef.set(new ImageData(imageData))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Image uploaded successfully
                        showToast("Image uploaded successfully");
                        // Custom success logic
                        // ...
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                        showToast("Image upload failed: " + e.getMessage());
                        // Custom failure logic
                        // ...
                    }
                });
    }

    public class ImageData {
        private byte[] imageData;

        public ImageData() {
            // Required empty constructor for Firestore
        }

        public ImageData(byte[] imageData) {
            this.imageData = imageData;
        }

        public byte[] getImageData() {
            return imageData;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
