package rapid.responce.RUCU;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends Activity {
    private static final String API_KEY = "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";
    private static final String API_URL = "https://api.openai.com/v1/completions";

    private OkHttpClient client;
    private SimpleDateFormat dateFormat;
    private LinearLayout chatMessagesContainer;
    private EditText userInputEditText;
    private Button sendButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        client = new OkHttpClient();
        dateFormat = new SimpleDateFormat("HH:mm");

        chatMessagesContainer = findViewById(R.id.chatMessagesContainer);
        userInputEditText = findViewById(R.id.userInputEditText);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = userInputEditText.getText().toString();
                if (!userInput.isEmpty()) {
                    simulateChat(userInput);
                    userInputEditText.setText("");
                }
            }
        });
    }

    public String sendMessageToChatGPT(String message) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("prompt", message);
            requestBody.put("max_tokens", 50);

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestBody.toString());
            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                JSONObject responseBody = new JSONObject(response.body().string());
                JSONArray choices = responseBody.getJSONArray("choices");
                if (choices.length() > 0) {
                    JSONObject chatResponse = choices.getJSONObject(0);
                    return chatResponse.getString("text");
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            // Handle the exception and display an error message if necessary
        }

        return null;
    }


    public void simulateChat(final String userInput) {
        String timestamp = dateFormat.format(new Date());
        displayChatMessage("You", userInput, timestamp);

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return sendMessageToChatGPT(userInput);
            }

            @Override
            protected void onPostExecute(String botResponse) {
                String timestamp = dateFormat.format(new Date());
                displayChatMessage("Bot", botResponse, timestamp);
            }
        }.execute();
    }

    public void displayChatMessage(String sender, String message, String timestamp) {
        TextView chatMessageTextView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(16, 8, 16, 8);

        chatMessageTextView.setText(String.format("%s: %s (%s)", sender, message, timestamp));
        chatMessageTextView.setLayoutParams(params);

        chatMessagesContainer.addView(chatMessageTextView);
    }
}
