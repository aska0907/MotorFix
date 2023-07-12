package rapid.responce.RUCU;

// SearchActivity.java
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements ItemAdapter.OnItemClickListener {
    private BottomNavigationView bottomNavigationView;
    private static final String TAG = "SearchActivity";

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private  ImageView menuIcon;
    private CollectionReference collectionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garages);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
       menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.garage);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(SearchActivity.this, MapsActivity.class));
                        return true;
                    case R.id.garage:
                        startActivity(new Intent(SearchActivity.this, SearchActivity.class));
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(SearchActivity.this, ProfileActivity.class));
                        return true;
                    case R.id.settings:
                        startActivity(new Intent(SearchActivity.this, SettingActivity.class));
                        return true;
                }
                return false;
            }
        });

        recyclerView = findViewById(R.id.listView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ItemAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        collectionRef = db.collection("UserGarages");

        // Retrieve data from Firestore and populate adapter
        collectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Item> items = new ArrayList<>();
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        String id = documentSnapshot.getId(); // Get the document ID
                        String garageName = documentSnapshot.getString("garageName");
                        String region = documentSnapshot.getString("region");
                        String district= documentSnapshot.getString("district");
                        String image = documentSnapshot.getString("imageUrl");
                        String garageCompany = documentSnapshot.getString("garageCompany");
                        String contact = documentSnapshot.getString("phone");
                        String description = documentSnapshot.getString("district");
                        Item item = new Item(id, garageName, region, district, image, garageCompany, contact, description);
                        items.add(item);
                    }
                }
                adapter.setItems(items);
                adapter.notifyDataSetChanged();
            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });

        // Initialize search view
        SearchView searchView = findViewById(R.id.searchView123);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String searchQuery = newText.toLowerCase();

                Query query = collectionRef.whereGreaterThanOrEqualTo("garageName", searchQuery)
                        .whereLessThanOrEqualTo("garageName", searchQuery + "\uf8ff");




                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot == null || querySnapshot.isEmpty()) {
                            adapter.clear();
                            adapter.notifyDataSetChanged();
                            return;
                        }

                        List<Item> items = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            String id = documentSnapshot.getId(); // Get the document ID
                            String garageName = documentSnapshot.getString("garageName");
                            String region = documentSnapshot.getString("region");
                            String image = documentSnapshot.getString("imageUrl");
                            String district= documentSnapshot.getString("district");
                            String garageCompany = documentSnapshot.getString("garageCompany");
                            String contact = documentSnapshot.getString("phone");
                            String description = documentSnapshot.getString("description");

                            if (garageName != null && garageName.toLowerCase().contains(searchQuery)) {
                                Item item = new Item(id, garageName, region, district, image, garageCompany, contact, description);
                                items.add(item);
                            }
                        }

                        adapter.setItems(items);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });

                return false;
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Item item = adapter.getItem(position);

        // Pass the item's data to the RequestActivity
        Intent intent = new Intent(SearchActivity.this, RequestActivity.class);
        intent.putExtra("id", item.getId()); // Pass the document ID
        intent.putExtra("imageUrl", item.getImage());
        intent.putExtra("garageName", item.getGarageName());

        intent.putExtra("garageCompany", item.getGarageCompany());
        intent.putExtra("phone", item.getContact());
        intent.putExtra("district", item.getDistrict());
        intent.putExtra("region", item.getRegion());
        startActivity(intent);
    }
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.menu_sort);

        // Set a listener to handle menu item clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return handleMenuItemClick(item);
            }
        });

        popupMenu.show();
    }

    private boolean handleMenuItemClick(MenuItem item) {
        // Handle menu item clicks here
        switch (item.getItemId()) {
            case R.id.menu_sort_region:
                // Handle "Sort by Region" click
                // ...
                return true;
            case R.id.menu_sort_alphabetical:
                // Handle "Sort Alphabetical" click
                // ...
                return true;
            case R.id.menu_sort_district:
                // Handle "Sort by District" click
                // ...
                return true;
            case R.id.menu_default:
                // Handle "Default" click
                // ...
                return true;
            case R.id.menu_exit:
                // Handle "Exit" click
                // ...
                return true;
            default:
                return false;
        }
    }
}
