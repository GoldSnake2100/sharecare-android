package project.labs.avviotech.com.sharecare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import project.labs.avviotech.com.sharecare.R;
import project.labs.avviotech.com.sharecare.ShareCare;
import project.labs.avviotech.com.sharecare.adapters.UserAdapter;
import project.labs.avviotech.com.sharecare.models.UserModel;
import project.labs.avviotech.com.sharecare.onesignal.PostNotification;
import project.labs.avviotech.com.sharecare.utils.API;
import project.labs.avviotech.com.sharecare.utils.CommonUtil;
import project.labs.avviotech.com.sharecare.utils.Constant;
import project.labs.avviotech.com.sharecare.utils.RecyclerTouchListener;
import project.labs.avviotech.com.sharecare.utils.SharedPrefUtil;
import project.labs.avviotech.com.sharecare.utils.UiUtil;

public class CareGiverActivity extends AppCompatActivity {

    private List<UserModel> mList;
    private List<UserModel> mInvitedList;
    private UserAdapter adapter;
    private UserAdapter invitedAdapter;
    private Button inviteBtn;
    private ProgressBar mProgressBar;
    private String appDownloadLink = "";
    private boolean sentInvitation = false;
    private String invitedEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_giver);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTVTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTVTitle.setText(getString(R.string.caregiver));

        mProgressBar = findViewById(R.id.progress);
        mProgressBar.setVisibility(View.GONE);
        inviteBtn = findViewById(R.id.inviteBtn);
        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inviteUser();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.caregiver_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mList = new ArrayList<>();
        adapter = new UserAdapter(this, mList);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                UserModel model = mList.get(position);
                showAlert(model, true);
            }

            @Override
            public void onLongClick(View view, final int position) {

            }
        }));

        RecyclerView invitedrecyclerView = findViewById(R.id.invited_recycle);
        invitedrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        invitedrecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mInvitedList = new ArrayList<>();
        invitedAdapter = new UserAdapter(this, mInvitedList);
        invitedrecyclerView.setAdapter(invitedAdapter);
        invitedrecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                invitedrecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                UserModel model = mInvitedList.get(position);
                showAlert(model, false);
            }

            @Override
            public void onLongClick(View view, final int position) {

            }
        }));

        fetchUsers();
        getAppDownloadLink();
    }

    private void getAppDownloadLink() {
        AndroidNetworking.get(API.APP_DOWNLOAD_LINK)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        appDownloadLink = response;
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    private void inviteUser() {
        EditText content = new EditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        content.setLayoutParams(params);
        content.setGravity(Gravity.CENTER);
        content.setMovementMethod(new ScrollingMovementMethod());
        content.setVerticalScrollBarEnabled(true);
        content.setHint("Email/Phone Number");
        new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Invite up to three Care-Givers")
                .setCustomView(content)
                .setCustomImage(R.drawable.logo)
                .setConfirmText(getString(R.string.invite))
                .setCancelText(getString(R.string.cancel))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        String userData = content.getText().toString().trim();
                        if (CommonUtil.validatePhonNumber(userData) || CommonUtil.validateEmail(userData)) {
                            inviteWithEmail(userData);
                        } else {
                            UiUtil.showShortToast("Invalid information");
                        }
                        sweetAlertDialog.dismissWithAnimation();
                    }
                }).show();
    }

    private void inviteWithEmail(String email) {
        int id = SharedPrefUtil.loadInt(this, Constant.PREF_USER_ID, 0);
        AndroidNetworking.get(API.SEARCH_USER)
                .addHeaders("Content-Type", "application/json")
                .addQueryParameter("query", email)
                .addQueryParameter("id", String.valueOf(id))
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String code = SharedPrefUtil.loadString(CareGiverActivity.this, Constant.PREF_USER_REFERRAL, "");
                        String sender = SharedPrefUtil.loadString(CareGiverActivity.this, Constant.PREF_USER_EMAIL, "");
                        if (response.length() > 0) {
                            try {
                                Gson gson = new Gson();
                                JSONObject data = new JSONObject();
                                data.put(Constant.PUSH_TYPE, Constant.PUSH_TYPE_INVITE);
                                data.put("referralCode", code);
                                data.put("sender", sender);
                                List<String> tokens = new ArrayList<>();

                                for (int i = 0; i < response.length(); i++) {
                                    String item = response.getString(i);
                                    UserModel model = gson.fromJson(item, UserModel.class);
                                    tokens.add(model.getPush_token());
                                }
                                PostNotification.send(sender + " would like to invite you", data, tokens);
                            } catch (JSONException e) {

                            }
                        } else  {
//                            appDownloadLink = "https://www.dropbox.com/s/xvbptaqq2pr9os8/sharecare.apk?dl=0";
                            String subject = "I would like to invite you to ShareCare";
                            String body = "Use my invite code " + code + " You can download this app from here: " + appDownloadLink;

                            if (CommonUtil.validateEmail(email)) {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                                intent.putExtra(Intent.EXTRA_TEXT, body);

                                intent.setType("message/rfc822");

                                startActivity(Intent.createChooser(intent, "Select Email Sending App :"));
                                sentInvitation = true;
                                invitedEmail = email;
                                return;
                            } else if (CommonUtil.validatePhonNumber(email)) {
                                CommonUtil.sendSMS(CareGiverActivity.this, email, subject + "\n" + body);
                                UserModel model = new UserModel();
                                model.setUserName("");
                                model.setEmail(email);
                                ShareCare.dbHelper.saveCareGiver(model);
                                fetchUsers();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }

    private void fetchUsers() {
        mProgressBar.setVisibility(View.VISIBLE);
        mList.clear();
        mList.addAll(ShareCare.dbHelper.getCareGivers());
        Log.e("count", String.valueOf(mList.size()));
        adapter.notifyDataSetChanged();
        if (mList.size() == 5)
            inviteBtn.setEnabled(false);
        else
            inviteBtn.setEnabled(true);

        String referralCode = SharedPrefUtil.loadString(this, Constant.PREF_USER_REFERRAL, "");
        AndroidNetworking.get(API.GET_CAREGIVERS)
                .addQueryParameter("referralCode", referralCode)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        mInvitedList.clear();
                        try {
                            Gson gson = new Gson();
                            for (int i = 0; i < response.length(); i++) {
                                String item = response.getString(i);
                                UserModel model = gson.fromJson(item, UserModel.class);
                                mInvitedList.add(model);

                                for (int j = 0; j < mList.size(); j++) {
                                    UserModel careGiver = mList.get(j);
                                    if (careGiver.getEmail().equals(model.getEmail())) {
                                        ShareCare.dbHelper.deleteCareGiver(careGiver);
                                        mList.remove(careGiver);
                                        Log.e("count2", String.valueOf(mList.size()));
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        } catch (JSONException e) {

                        }
                        invitedAdapter.notifyDataSetChanged();
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(ANError anError) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void showAlert(UserModel model, boolean isPending) {
        TextView content = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        content.setLayoutParams(params);
        content.setGravity(Gravity.CENTER);
        content.setMovementMethod(new ScrollingMovementMethod());
        content.setVerticalScrollBarEnabled(true);
        content.setText("Do you want to remove this user\nfrom CareGivers?");
        new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("")
                .setCustomView(content)
                .setCustomImage(R.drawable.logo)
                .setConfirmText(getString(R.string.ok))
                .setCancelText(getString(R.string.cancel))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        if (isPending) {
                            ShareCare.dbHelper.deleteCareGiver(model);
                            mList.remove(model);
                            adapter.notifyDataSetChanged();
                        } else {
                            deleteCareGiver(model);
                        }
                        sweetAlertDialog.dismissWithAnimation();
                    }
                }).show();
    }

    private void deleteCareGiver(UserModel model) {
        String referralCode = SharedPrefUtil.loadString(this, Constant.PREF_USER_REFERRAL, "");
        AndroidNetworking.delete(API.DELETE_CAREGIVER)
                .addQueryParameter("userId", String.valueOf(model.getUserID()))
                .addQueryParameter("referralCode", referralCode)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("success"))
                                ShareCare.dbHelper.deleteCareGiver(model);
                                fetchUsers();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sentInvitation) {
            sentInvitation = false;
            UserModel model = new UserModel();
            model.setUserName("");
            model.setEmail(invitedEmail);
            ShareCare.dbHelper.saveCareGiver(model);
            fetchUsers();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
