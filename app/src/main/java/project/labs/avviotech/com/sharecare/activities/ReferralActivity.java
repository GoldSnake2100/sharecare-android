package project.labs.avviotech.com.sharecare.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import project.labs.avviotech.com.sharecare.R;
import project.labs.avviotech.com.sharecare.utils.API;
import project.labs.avviotech.com.sharecare.utils.Constant;
import project.labs.avviotech.com.sharecare.utils.SharedPrefUtil;
import project.labs.avviotech.com.sharecare.utils.UiUtil;

public class ReferralActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private EditText mEtReferral;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTVTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTVTitle.setText(getString(R.string.referral_code));

        mProgressBar = findViewById(R.id.progress);
        mProgressBar.setVisibility(View.GONE);

        mEtReferral = findViewById(R.id.referral_code);

        findViewById(R.id.confirm_button).setOnClickListener(mClickListener);
        findViewById(R.id.skip).setOnClickListener(mClickListener);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.confirm_button:
                    String code = mEtReferral.getText().toString().trim().toUpperCase();
                    if (code.isEmpty()) {
                        UiUtil.showShortToast("Please enter referral code or skip this step");
                        return;
                    }
                    referUser(code);
                    break;
                case R.id.skip:
                    gotoMainActivity();
                    break;
            }
        }
    };

    private void referUser(String code) {
        mProgressBar.setVisibility(View.VISIBLE);
        int userId = SharedPrefUtil.loadInt(this, Constant.PREF_USER_ID,  0);
        AndroidNetworking.post(API.REFER_USER)
                .addQueryParameter("userId", String.valueOf(userId))
                .addQueryParameter("code", code)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("success")) {
                                gotoMainActivity();
                            } else {
                                UiUtil.showShortToast("Invalid referral code");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(ANError anError) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void gotoMainActivity() {
        Intent i = new Intent(ReferralActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
