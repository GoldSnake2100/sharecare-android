package project.labs.avviotech.com.sharecare.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import project.labs.avviotech.com.sharecare.R;
import project.labs.avviotech.com.sharecare.utils.API;
import project.labs.avviotech.com.sharecare.utils.CommonUtil;
import project.labs.avviotech.com.sharecare.utils.Constant;
import project.labs.avviotech.com.sharecare.utils.SharedPrefUtil;
import project.labs.avviotech.com.sharecare.utils.UiUtil;

public class SignupActivity extends AppCompatActivity {

    private EditText mEmail, mPassword, mConfirm, mReferral;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTVTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTVTitle.setText(getString(R.string.signup));

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mConfirm = findViewById(R.id.password_confirm);
        mReferral = findViewById(R.id.referral_code);
        mProgressBar = findViewById(R.id.progress);
        mProgressBar.setVisibility(View.GONE);

        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSignup();
            }
        });
    }

    private void doSignup() {
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String confirm = mConfirm.getText().toString().trim();
        String referral = mReferral.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            UiUtil.showShortToast("Please fill in your email/phone number and password");
            return;
        }
        if (!CommonUtil.validatePhonNumber(email) && !CommonUtil.validateEmail(email)) {
            UiUtil.showShortToast("Please fill in validate email/phone number");
        }
        if (!password.equals(confirm)) {
            UiUtil.showShortToast("Confirm password is incorrect");
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);
        String push_token = SharedPrefUtil.loadString(this, Constant.PREF_PUSH_TOKEN, "");

        if (push_token.isEmpty()) {
            push_token = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
            SharedPrefUtil.saveString(this, Constant.PREF_PUSH_TOKEN, push_token);
        }

        AndroidNetworking.get(API.SIGNUP)
                .addQueryParameter("email", email)
                .addQueryParameter("password", password)
                .addQueryParameter("token", push_token)
                .addQueryParameter("referral", referral)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("success")) {
                                SharedPrefUtil.saveString(SignupActivity.this, Constant.PREF_USER_REFERRAL, response.getString("referral_code"));
                                UiUtil.showShortToast("Signed up successfully");
                                finish();
                            } else {
                                UiUtil.showShortToast("User already exist");
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
