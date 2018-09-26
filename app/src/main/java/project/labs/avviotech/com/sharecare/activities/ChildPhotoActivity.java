package project.labs.avviotech.com.sharecare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import project.labs.avviotech.com.sharecare.R;
import project.labs.avviotech.com.sharecare.ShareCare;
import project.labs.avviotech.com.sharecare.models.ChildModel;
import project.labs.avviotech.com.sharecare.utils.API;
import project.labs.avviotech.com.sharecare.utils.Constant;
import project.labs.avviotech.com.sharecare.utils.SharedPrefUtil;

public class ChildPhotoActivity extends AppCompatActivity {

    private ImageView mFirstImage, mSecondImage, mThirdImage,
        mCheckFirst, mCheckSecond, mCheckThird;
    private TextView mFirstName, mSecondName, mThirdName;
    private ProgressBar mProgressBar;
    private int selectedIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_photo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTVTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTVTitle.setText(getString(R.string.childphoto));

        mProgressBar = findViewById(R.id.progress);
        mFirstImage = findViewById(R.id.first_child);
        mSecondImage = findViewById(R.id.second_child);
        mThirdImage = findViewById(R.id.third_child);
        mCheckFirst = findViewById(R.id.first_check);
        mCheckSecond = findViewById(R.id.second_check);
        mCheckThird = findViewById(R.id.third_check);
        mFirstName = findViewById(R.id.first_name);
        mSecondName = findViewById(R.id.second_name);
        mThirdName = findViewById(R.id.third_name);

        mFirstImage.setOnClickListener(mClickListener);
        mSecondImage.setOnClickListener(mClickListener);
        mThirdImage.setOnClickListener(mClickListener);

        loadData();

    }

    private void loadData() {
        List<ChildModel> childs = ShareCare.dbHelper.getChildPhotos();
        String email = SharedPrefUtil.loadString(this, Constant.PREF_USER_EMAIL, "");
        String photoBasePath = API.PHOTO_BASEPATH + email + File.separator;

        for (int i = 0; i < childs.size(); i++) {
            ChildModel model = childs.get(i);
            if (i==0) {
                Picasso.with(this).load(photoBasePath + model.getImage()).into(mFirstImage);
                mCheckFirst.setImageResource(R.drawable.check);
                mCheckFirst.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mCheckFirst.setColorFilter(ContextCompat.getColor(ChildPhotoActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                mFirstImage.setAlpha(1.0f);
                mFirstName.setText(model.getName());
            }
            else if (i == 1) {
                Picasso.with(this).load(photoBasePath + model.getImage()).into(mSecondImage);
                mCheckSecond.setImageResource(R.drawable.check);
                mCheckSecond.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mCheckSecond.setColorFilter(ContextCompat.getColor(ChildPhotoActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                mSecondImage.setAlpha(1.0f);
                mSecondName.setText(model.getName());
            }
            else if (i == 2) {
                Picasso.with(this).load(photoBasePath + model.getImage()).into(mThirdImage);
                mCheckThird.setImageResource(R.drawable.check);
                mCheckThird.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mCheckThird.setColorFilter(ContextCompat.getColor(ChildPhotoActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                mThirdImage.setAlpha(1.0f);
                mThirdName.setText(model.getName());
            }
        }
    }

    private View.OnClickListener mClickListener = view -> {
        switch (view.getId()) {
            case R.id.first_child:
                selectedIndex = 1;
                pickPhoto();
                break;
            case R.id.second_child:
                selectedIndex = 2;
                pickPhoto();
                break;
            case R.id.third_child:
                selectedIndex = 3;
                pickPhoto();
                break;
        }
    };

    private void pickPhoto() {
        Intent i = new Intent(this, AddChildActivity.class);
        i.putExtra("index", selectedIndex);
        startActivityForResult(i, selectedIndex);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            loadData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
