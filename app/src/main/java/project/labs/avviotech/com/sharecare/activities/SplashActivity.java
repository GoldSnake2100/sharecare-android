package project.labs.avviotech.com.sharecare.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import project.labs.avviotech.com.sharecare.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

//        Bundle bundle = getIntent().getExtras();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
//                if (bundle != null) {
//                    i.putExtra("notiData", bundle.getString("notiData"));
//                    Log.e("notiData", bundle.getString("notiData"));
//                }
                startActivity(i);
                finish();
            }
        }, 3000);
    }
}
