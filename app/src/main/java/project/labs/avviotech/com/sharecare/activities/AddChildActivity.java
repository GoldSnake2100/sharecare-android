package project.labs.avviotech.com.sharecare.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.squareup.picasso.Picasso;

import java.io.File;

import project.labs.avviotech.com.sharecare.R;
import project.labs.avviotech.com.sharecare.ShareCare;
import project.labs.avviotech.com.sharecare.models.ChildModel;
import project.labs.avviotech.com.sharecare.utils.API;
import project.labs.avviotech.com.sharecare.utils.Constant;
import project.labs.avviotech.com.sharecare.utils.SharedPrefUtil;

public class AddChildActivity extends AppCompatActivity {

    private ImageView mIvPhoto;
    private EditText mTvUsername;
    private ProgressBar mProgressBar;
    private String childImage;
    private int childIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTVTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTVTitle.setText(getString(R.string.child));

        TextView tvSave = toolbar.findViewById(R.id.save_profile);
        tvSave.setOnClickListener(mClickListener);


        mProgressBar = findViewById(R.id.progress);
        mTvUsername = findViewById(R.id.username);
        mIvPhoto = findViewById(R.id.profile_image);
        mIvPhoto.setOnClickListener(mClickListener);

        childIndex = getIntent().getIntExtra("index", 0);
        initUI(childIndex);
    }

    private void initUI(int index) {
        String email = SharedPrefUtil.loadString(this, Constant.PREF_USER_EMAIL, "");
        ChildModel model = ShareCare.dbHelper.getChildByIndex(index);
        childImage = model.getImage();
        mTvUsername.setText(model.getName());
        if (!childImage.isEmpty())
            Picasso.with(this).load(API.PHOTO_BASEPATH + email + "/" + childImage).error(R.drawable.default_user).into(mIvPhoto);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.profile_image)
                ImagePicker.create(AddChildActivity.this).single().start();
            else if (view.getId() == R.id.save_profile)
                saveChildData();
        }
    };

    private void saveChildData() {
        String name = mTvUsername.getText().toString().trim();
        String image = childImage;
        ShareCare.dbHelper.saveChildData(image, name, childIndex);
        setResult(RESULT_OK);
        finish();
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
                        mIvPhoto.setImageURI(Uri.fromFile(new File(path)));
                        childImage = name;
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
