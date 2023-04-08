package rapid.responce.RUCU;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.Query;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private static final String TAG = "SearchActivity";

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garages);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();

            bottomNavigationView = findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setSelectedItemId(R.id.garage);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {


                @SuppressLint("NonConstantResourceId")
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
                            // Handle settings item click
                            return true;
                    }
                    return false;
                }
            });
        }
        recyclerView = findViewById(R.id.listView);

        // Set up RecyclerView with LinearLayoutManager and empty ItemAdapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ItemAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);


        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Search");

        // Retrieve data from Firebase and populate adapter
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Item> items = new ArrayList<>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String garageName = childSnapshot.child("name").getValue(String.class);
                    String garageDescription = childSnapshot.child("description").getValue(String.class);
                    Item item = new Item(garageName, garageDescription);
                    items.add(item);
                }
                adapter.setItems(items);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });

// Initialize search view
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String searchQuery = newText.toLowerCase(); // Convert search text to lowercase

                Query query = myRef.orderByChild("name").startAt(searchQuery).endAt(searchQuery + "\uf8ff");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot == null || !dataSnapshot.exists()) {
                            adapter.clear();
                            adapter.notifyDataSetChanged();
                            return;
                        }
                        List<Item> items = new ArrayList<>();
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String garageName = childSnapshot.child("name").getValue(String.class);
                            String garageDescription = childSnapshot.child("description").getValue(String.class);
                            if (garageName != null && garageName.toLowerCase().contains(searchQuery)) { // Convert database data to lowercase and check if it contains the search query
                                Item item = new Item(garageName, garageDescription);
                                items.add(item);
                            }
                        }
                        adapter.setItems(items);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "onCancelled", databaseError.toException());
                    }

                });
                return false;
            }


        });
    }}