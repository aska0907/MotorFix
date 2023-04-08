package rapid.responce;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class server {

    public static String createCustomTokenWithClaims(String uid, Map<String, Object> claims) throws IOException, FirebaseAuthException {
        FileInputStream serviceAccount = new FileInputStream("./rapid-responce-b1ca5-firebase-adminsdk-v5o46-ad18543590.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://rapid-responce-b1ca5-default-rtdb.firebaseio.com/")
                .build();

        FirebaseApp.initializeApp(options);

        // Update the user's custom claims
        FirebaseAuth.getInstance().setCustomUserClaims(uid, claims);

        // Generate and return the custom token
        String customToken = FirebaseAuth.getInstance().createCustomToken(uid, claims);

        return customToken;
    }

    public static void main(String[] args) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("isAdmin", true);

        String uid = "kcooUZCdQAN8Q47FKyMm7JpIRz92"; // Replace with the UID of the user you want to update

        try {
            String customToken = createCustomTokenWithClaims(uid, claims);
            System.out.println(customToken);
        } catch (IOException | FirebaseAuthException e) {
            e.printStackTrace();
        }
    }


}
