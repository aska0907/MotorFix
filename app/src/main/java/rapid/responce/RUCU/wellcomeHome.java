package rapid.responce.RUCU;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class wellcomeHome extends AppCompatActivity {

    // Delay time for splash screen in milliseconds
    private static final int SPLASH_DELAY_TIME = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wellcome);
        getSupportActionBar().hide();
        TextView textView = findViewById(R.id.textView);

// Create the animations
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(textView, "scaleX", 1f, 1.2f, 1f);
        scaleX.setDuration(2000);

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(textView, "scaleY", 1f, 1.2f, 1f);
        scaleY.setDuration(2000);

// Create the animation set and play it
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.start();


        // Using Handler to wait for SPLASH_DELAY_TIME milliseconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the new activity
                Intent intent = new Intent(wellcomeHome.this, loginActivity.class);
                startActivity(intent);

                // Close this activity
                finish();
            }
        }, SPLASH_DELAY_TIME);
    }
}
