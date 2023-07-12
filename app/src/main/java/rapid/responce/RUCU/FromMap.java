package rapid.responce.RUCU;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FromMap extends AppCompatActivity {

    private List<Item> items = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_map);
        getSupportActionBar().hide();
        // Retrieve the documentId from the intent

        String documentId = getIntent().getStringExtra("documentId");

        // Retrieve data from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("UserGarages");

        collectionRef.document(documentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String id = document.getId();
                        String garageName = document.getString("garageName");
                        String region = document.getString("region");
                        String image = document.getString("imageUrl");
                        String district = document.getString("district");
                        String garageCompany = document.getString("garageCompany");
                        String contact = document.getString("phone");
                        String description = document.getString("description");

                        // Create an Item object with the retrieved data
                        Item item = new Item(id, garageName, region, image, district, garageCompany, contact, description);

                        // Add the item to the items list
                        items.add(item);

                        // Pass the item's data to the RequestActivity
                        Intent intent = new Intent(FromMap.this, RequestActivity.class);
                        intent.putExtra("id", item.getId());
                        intent.putExtra("imageUrl", item.getImage());
                        intent.putExtra("garageName", item.getGarageName());
                        intent.putExtra("garageCompany", item.getGarageCompany());
                        intent.putExtra("phone", item.getContact());
                        intent.putExtra("district", item.getDistrict());
                        intent.putExtra("region", item.getRegion());

                        startActivity(intent);
                        // Finish the FromMap activity to prevent going back to it
                        finish();
                    } else {
                        // Document doesn't exist
                        Toast.makeText(FromMap.this, "Data failed to be retrieved.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    // Error getting document
                    Toast.makeText(FromMap.this, "Failed to retrieve data.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}
