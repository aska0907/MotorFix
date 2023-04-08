
        package rapid.responce.RUCU;

        import android.content.DialogInterface;
        import android.content.Intent;
        import android.os.Bundle;
        import android.text.InputType;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.CheckBox;
        import android.widget.CompoundButton;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;
        import android.widget.ToggleButton;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.ActionBar;
        import androidx.appcompat.app.AlertDialog;
        import androidx.appcompat.app.AppCompatActivity;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;

        import java.util.Map;

        public class loginActivity extends AppCompatActivity {
            private FirebaseAuth mAuth;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_login);
                ActionBar actionBar = getSupportActionBar();
                Log.d("lifecycle","onCreate login invoked "+getApplicationContext());
                if (actionBar != null) {
                    actionBar.hide();
                }
                CheckBox showPasswordCheckBox = findViewById(R.id.check);
                EditText passwordEditText = findViewById(R.id.password);

                showPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        } else {
                            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
                        // Move the cursor to the end of the text
                        passwordEditText.setSelection(passwordEditText.getText().length());
                    }
                });


                mAuth = FirebaseAuth.getInstance();

                Button loginButton = findViewById(R.id.login);
                loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText emailEditText = findViewById(R.id.email);
                        EditText passwordEditText = findViewById(R.id.password);

                        String email = emailEditText.getText().toString();
                        String password = passwordEditText.getText().toString();

                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(loginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            updateUI(user);
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Toast.makeText(loginActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                            updateUI(null);
                                        }
                                    }
                                });
                    }
                });

                TextView textView = findViewById(R.id.sign_up);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(loginActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    }
                });
                TextView textView2 = findViewById(R.id.forgot_password);
                textView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(loginActivity.this, ForgotPasswordActivity.class);
                        startActivity(intent);
                    }
                });
                TextView textView1 = findViewById(R.id.register_garage);
                textView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(loginActivity.this, RegisterGarageActivity.class);
                        startActivity(intent);
                    }
                });
            }

            private void updateUI(FirebaseUser user) {
                if (user != null) {
                    // User is valid, redirect to MapsActivity
                    Intent intent = new Intent(loginActivity.this, MapsActivity.class);
                    startActivity(intent);
                    finish(); // finish current activity to prevent returning to login screen
                } else {
                    // User is invalid, show dialog notification
                    AlertDialog.Builder builder = new AlertDialog.Builder(loginActivity.this);
                    builder.setTitle("Authentication failed")
                            .setMessage("Invalid email or password.")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }
        }
