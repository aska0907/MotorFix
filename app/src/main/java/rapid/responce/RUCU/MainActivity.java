package rapid.responce.RUCU;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private MessageAdapter mAdapter;
    private EditText mEditText;
    private Button mButton;
    private String apiUrl = "https://api.openai.com/v1/completions";
    private String accessToken = "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";
    private List<Message> mMessages;
    private ScheduledExecutorService executorService;
    private ScheduledFuture<?> retryFuture;
    private long retryDelay = 1000; // Initial retry delay in milliseconds
    private long maxRetryDelay = 60000; // Maximum retry delay in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recycler_view);
        mEditText = findViewById(R.id.edit_text);
        mButton = findViewById(R.id.button);

        mMessages = new ArrayList<>();
        mAdapter = new MessageAdapter(mMessages);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAPI();
            }
        });

        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    private void callAPI() {
        String text = mEditText.getText().toString();
        mMessages.add(new Message(text, true));
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        mEditText.getText().clear();

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("model", "text-davinci-003");
            requestBody.put("prompt", text);
            requestBody.put("max_tokens", 100);
            requestBody.put("temperature", 1);
            requestBody.put("top_p", 1);
            requestBody.put("frequency_penalty", 0.0);
            requestBody.put("presence_penalty", 0.0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, apiUrl, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray choicesArray = response.getJSONArray("choices");
                            JSONObject choiceObject = choicesArray.getJSONObject(0);
                            String text = choiceObject.getString("text");
                            Log.e("API Response", response.toString());
                            mMessages.add(new Message(text.replaceFirst("\n", "").replaceFirst("\n", ""), false));
                            mAdapter.notifyItemInserted(mMessages.size() - 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null && error.networkResponse.statusCode == 429) {
                            // Retry after a delay
                            retryFuture = executorService.schedule(new Runnable() {
                                @Override
                                public void run() {
                                    callAPI(); // Retry the API call
                                }
                            }, retryDelay, TimeUnit.MILLISECONDS);

                            // Increase the retry delay for subsequent retries
                            retryDelay = Math.min(retryDelay * 2, maxRetryDelay);
                        } else {
                            Log.e("API Error", error.toString());
                        }
                    }

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }

        };

        int timeoutMs = 25000; // 25 seconds timeout
        RetryPolicy policy = new DefaultRetryPolicy(timeoutMs, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);


        // Add the request to the RequestQueue
        MySingleton.getInstance(this).addToRequestQueue(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cancel the retry future if it exists
        if (retryFuture != null && !retryFuture.isDone()) {
            retryFuture.cancel(true);
        }

        // Shutdown the executor service
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }
}
