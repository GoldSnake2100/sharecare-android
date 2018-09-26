package project.labs.avviotech.com.sharecare.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import project.labs.avviotech.com.sharecare.R;
import project.labs.avviotech.com.sharecare.ShareCare;
import project.labs.avviotech.com.sharecare.models.UserModel;
import project.labs.avviotech.com.sharecare.utils.API;
import project.labs.avviotech.com.sharecare.utils.Constant;
import project.labs.avviotech.com.sharecare.utils.SharedPrefUtil;
import project.labs.avviotech.com.sharecare.utils.UiUtil;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mIvPhoto;
    private EditText mEtUsername;
    private ProgressBar mProgressBar;
    private TextView mTvSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTVTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTVTitle.setText(getString(R.string.profile));

        mTvSave = toolbar.findViewById(R.id.save_profile);
        mTvSave.setOnClickListener(mClickListener);

        mProgressBar = findViewById(R.id.progress);
        mEtUsername = findViewById(R.id.username);
        mIvPhoto = findViewById(R.id.profile_image);
        mIvPhoto.setOnClickListener(mClickListener);
        initUI();
    }

    private void initUI() {
        String username = SharedPrefUtil.loadString(this, Constant.PREF_USER_NAME, "");
        if (!username.isEmpty()) {
            mTvSave.setText(R.string.edit);
        }
        String image = SharedPrefUtil.loadString(this, Constant.PREF_USER_IMAGE, "");
        String email = SharedPrefUtil.loadString(this, Constant.PREF_USER_EMAIL, "");
        mEtUsername.setText(username);
        if (!image.isEmpty())
            Picasso.with(this).load(API.PHOTO_BASEPATH + email + "/" + image).error(R.drawable.default_user).into(mIvPhoto);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.profile_image)
                ImagePicker.create(ProfileActivity.this).single().start();
            else if (view.getId() == R.id.save_profile)
                updateProfile();
        }
    };

    private void updateProfile() {
        String username = mEtUsername.getText().toString().trim();
        if (username.isEmpty()) {
            UiUtil.showShortToast("Please enter your username");
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);
        int userid = SharedPrefUtil.loadInt(this, Constant.PREF_USER_ID, 0);
        JSONObject param = new JSONObject();
        try {
            param.put("userid", userid);
            param.put("name", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(API.UPDATE_PROFILE)
                .setContentType("application/json")
                .addJSONObjectBody(param)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("success")) {
                                SharedPrefUtil.saveString(ProfileActivity.this, Constant.PREF_USER_NAME, username);
                                UiUtil.showShortToast("Profile updated successfully");
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

    private void updateProfilePhoto(String image) {

        mProgressBar.setVisibility(View.VISIBLE);
        int userid = SharedPrefUtil.loadInt(this, Constant.PREF_USER_ID, 0);
        JSONObject param = new JSONObject();
        try {
            param.put("userid", userid);
            param.put("image", image);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(API.UPDATE_PROFILE_PHOTO)
                .setContentType("application/json")
                .addJSONObjectBody(param)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("success")) {
                                SharedPrefUtil.saveString(ProfileActivity.this, Constant.PREF_USER_IMAGE, image);
                                UiUtil.showShortToast("Profile photo updated successfully");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image image = ImagePicker.getFirstImageOrNull(data);
            uploadImage(image.getPath(), image.getName());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImage(String path, String name) {
        mProgressBar.setVisibility(View.VISIBLE);
        String userEmail = SharedPrefUtil.loadString(this, Constant.PREF_USER_EMAIL, "");
        AndroidNetworking.upload(API.UPLOAD_CHILD_PHOTO)
                .addPathParameter("email", userEmail)
                .addMultipartFile("file", new File(path))
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        updateProfilePhoto(name);
                        mIvPhoto.setImageURI(Uri.fromFile(new File(path)));
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
