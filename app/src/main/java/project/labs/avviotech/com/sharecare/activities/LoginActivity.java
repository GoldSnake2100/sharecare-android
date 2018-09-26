package project.labs.avviotech.com.sharecare.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import project.labs.avviotech.com.sharecare.R;
import project.labs.avviotech.com.sharecare.ShareCare;
import project.labs.avviotech.com.sharecare.utils.API;
import project.labs.avviotech.com.sharecare.utils.CommonUtil;
import project.labs.avviotech.com.sharecare.utils.Constant;
import project.labs.avviotech.com.sharecare.utils.SharedPrefUtil;
import project.labs.avviotech.com.sharecare.utils.UiUtil;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTVTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTVTitle.setText(getString(R.string.login));

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mProgressBar = findViewById(R.id.progress);
        mProgressBar.setVisibility(View.GONE);

        findViewById(R.id.login).setOnClickListener(mClickListener);
        findViewById(R.id.signup).setOnClickListener(mClickListener);
        findViewById(R.id.forget_password).setOnClickListener(mClickListener);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.login) {
                doLogin();
            } else if (view.getId() == R.id.signup) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            } else if (view.getId() == R.id.forget_password) {
                showForgotPasswordAlert();
            }
        }
    };

    private void showForgotPasswordAlert() {
        EditText content = new EditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        content.setLayoutParams(params);
        content.setGravity(Gravity.CENTER);
        content.setHint(getString(R.string.enter_email));
        new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("You can reset your password through email")
                .setCustomView(content)
                .setCustomImage(R.drawable.logo)
                .setConfirmText(getString(R.string.send))
                .setCancelText(getString(R.string.cancel))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        String email = content.getText().toString().trim();
                        if (!CommonUtil.validateEmail(email)) {
                            UiUtil.showShortToast("Please enter valid email");
                            return;
                        }
                        treatForgotPassword(email);
                        sweetAlertDialog.dismissWithAnimation();
                    }
                }).show();
    }

    private void treatForgotPassword(String email) {
        mProgressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(API.FORGOT_PASSWORD)
                .addBodyParameter(email)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                UiUtil.showShortToast("We sent reset password email, you can set password in your email");
                            } else {
                                UiUtil.showShortToast("Sorry, something went wrong, please try again");
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


    private void doLogin() {
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String push_token = SharedPrefUtil.loadString(this, Constant.PREF_PUSH_TOKEN, "");

        if (push_token.isEmpty()) {
            push_token = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
            SharedPrefUtil.saveString(this, Constant.PREF_PUSH_TOKEN, push_token);
        }
        if (email.isEmpty() || password.isEmpty()) {
            UiUtil.showShortToast("Please fill in your email and password");
            return;
        }
        if (!CommonUtil.validateEmail(email) && !CommonUtil.validatePhonNumber(email)) {
            UiUtil.showShortToast("Please fill in validate email/phone number");
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);

        AndroidNetworking.get(API.LOGIN)
                .addQueryParameter("email", email)
                .addQueryParameter("password", password)
                .addQueryParameter("token", push_token)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int userid = response.getInt("userID");
                            if (userid > 0) {
                                int oldid = SharedPrefUtil.loadInt(LoginActivity.this, Constant.PREF_USER_ID, 0);
                                if (oldid != 0 && oldid != userid) {
                                    ShareCare.dbHelper.deleteAllData();
                                }

                                SharedPrefUtil.saveInt(LoginActivity.this, Constant.PREF_USER_ID, userid);
                                SharedPrefUtil.saveString(LoginActivity.this, Constant.PREF_USER_EMAIL, email);
                                SharedPrefUtil.saveString(LoginActivity.this, Constant.PREF_USER_NAME, response.getString("userName"));
                                SharedPrefUtil.saveBoolean(LoginActivity.this, Constant.PREF_AUTOLOGIN, true);
                                String push_token = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
                                SharedPrefUtil.saveString(LoginActivity.this, Constant.PREF_PUSH_TOKEN, push_token);

                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            } else if (userid == -100) {
                                UiUtil.showShortToast("The referral code is invalid");
                            } else {
                                UiUtil.showShortToast("The user information is invalid");
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
}
