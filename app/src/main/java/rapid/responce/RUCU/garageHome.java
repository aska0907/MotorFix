package rapid.responce.RUCU;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class garageHome extends AppCompatActivity implements RequestAdapter.OnItemClickListener {
    private BottomNavigationView bottomNavigationView;
    private static final String TAG = "garageHome";

    private RecyclerView recyclerView;
    private RequestAdapter adapter;
    private CollectionReference collectionRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garagehome);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(garageHome.this, garageHome.class));
                        return true;

                    case R.id.garage:
                        startActivity(new Intent(garageHome.this, myGarage.class));
                        return true;
                    case R.id.settings:
                        startActivity(new Intent(garageHome.this, SettingActivity2.class));
                        return true;
                }
                return false;
            }
        });

        recyclerView = findViewById(R.id.listView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RequestAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // Initialize Firestore and Firebase Authentication
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Retrieve the current user ID
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userID = currentUser.getUid();

            // Initialize the collection reference
            collectionRef = db.collection("requests");

            // Retrieve data from Firestore and populate adapter
            collectionRef.whereEqualTo("itemId", userID).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<Request> requests = new ArrayList<>();
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            Request request = documentSnapshot.toObject(Request.class);
                            requests.add(request);
                        }
                    }
                    adapter.setRequests(requests);
                    adapter.notifyDataSetChanged();

                    if (requests.isEmpty()) {
                        showNoRequestDialog();
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            });
        }

        // Initialize search view
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String searchQuery = newText.toLowerCase();

                Query query = collectionRef.whereGreaterThanOrEqualTo("username", searchQuery)
                        .whereLessThanOrEqualTo("username", searchQuery + "\uf8ff");

                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot == null || querySnapshot.isEmpty()) {
                            adapter.clear();
                            adapter.notifyDataSetChanged();
                            showNoRequestDialog();
                            return;
                        }

                        List<Request> requests = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            Request request = documentSnapshot.toObject(Request.class);
                            if (request != null && request.getUsername().toLowerCase().contains(searchQuery)) {
                                requests.add(request);
                            }
                        }

                        adapter.setRequests(requests);
                        adapter.notifyDataSetChanged();

                        if (requests.isEmpty()) {
                            showNoRequestDialog();
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });

                return false;
            }
        });
    }




    @Override
    public void onItemClick(Request request) {
        String name = request.getUsername();
        String location = request.getLocationDescription();
        String automobileProblem = request.getAutomobileType();
        String problemdescription = request.getProblemDescription();
        String SenderID = request.getUserId();
        String RequestID = request.getRequestId();
        String TimeStamp = String.valueOf(request.getTimestamp());


        Intent intent = new Intent(garageHome.this, ViewRequests.class);
        intent.putExtra("name", name);
        intent.putExtra("location", location);
        intent.putExtra("automobileProblem", automobileProblem);
        intent.putExtra("problemDescription", problemdescription);
        intent.putExtra("SenderID",SenderID);
        intent.putExtra("RequestID", RequestID);
        intent.putExtra("TimeStamp",TimeStamp);
        startActivity(intent);
    }



    private void showNoRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Requests");
        builder.setMessage("There are no requests available.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(garageHome.this, myGarage.class));
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void updateStatus(String userId, String status) {
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("UserGarages").document(userId);
        userRef.update("status", status)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Status Update", "Status updated successfully");
                        } else {
                            Log.e("Status Update", "Error updating status", task.getException());
                        }
                    }
                });
    }
}
