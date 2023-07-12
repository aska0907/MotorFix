package rapid.responce.RUCU;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

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

public class ViewFeedback extends AppCompatActivity implements FeedbackAdapter.OnItemClickListener {

    private static final String TAG = "ViewFeedback";

    private RecyclerView recyclerView;
    private FeedbackAdapter adapter;
    private CollectionReference collectionRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_feedback);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        recyclerView = findViewById(R.id.listView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FeedbackAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // Initialize Firestore and Firebase Authentication
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Retrieve the current user ID
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userID = currentUser.getUid();

            // Initialize the collection reference
            collectionRef = db.collection("Approval");

            // Retrieve data from Firestore and populate adapter
            collectionRef.whereEqualTo("SenderID", userID).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<Approval> approvals = new ArrayList<>();
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            Approval approval = documentSnapshot.toObject(Approval.class);
                            approvals.add(approval);
                        }
                    }
                    adapter.setRequests(approvals);
                    adapter.notifyDataSetChanged();

                    if (approvals.isEmpty()) {
                        showNoApprovalDialog();
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

                Query query = collectionRef.whereGreaterThanOrEqualTo("garageName", searchQuery)
                        .whereLessThanOrEqualTo("garageName", searchQuery + "\uf8ff");

                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot == null || querySnapshot.isEmpty()) {
                            adapter.clear();
                            adapter.notifyDataSetChanged();
                            showNoApprovalDialog();
                            return;
                        }

                        List<Approval> approvals = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            Approval approval = documentSnapshot.toObject(Approval.class);
                            if (approval != null && approval.getGarageName().toLowerCase().contains(searchQuery)) {
                                approvals.add(approval);
                            }
                        }

                        adapter.setRequests(approvals);
                        adapter.notifyDataSetChanged();

                        if (approvals.isEmpty()) {
                            showNoApprovalDialog();
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
    public void onItemClick(Approval approval) {
        // Retrieve additional data based on the selected approval
        String requestId = approval.getRequestId();
        String senderID = approval.getSenderID();
        String TimeStamp = String.valueOf(approval.getTimeStamp());
        String garageName = approval.getGarageName();
        String status = approval.getStatus();

        // Pass the retrieved data to the next activity
        Intent intent = new Intent(ViewFeedback.this, LatituLang.class);
        intent.putExtra("requestId", requestId);
        intent.putExtra("senderID", senderID);
        intent.putExtra("TimeStamp", TimeStamp);
        intent.putExtra("GarageName", garageName);
        intent.putExtra("Status", status);
        startActivity(intent);
    }

    private void showNoApprovalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Approvals");
        builder.setMessage("There are no approvals available.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle OK button click
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
}
