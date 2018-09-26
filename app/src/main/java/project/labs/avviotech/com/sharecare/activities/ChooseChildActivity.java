package project.labs.avviotech.com.sharecare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
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
import project.labs.avviotech.com.sharecare.utils.UiUtil;

public class ChooseChildActivity extends AppCompatActivity {

    private ImageView mIvFirst, mIvSecond, mIvThird;
    private TextView mTvDescription;
    private List<ChildModel> images;
    private String mSelectedImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_child);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTVTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTVTitle.setText(getString(R.string.childphoto));

        toolbar.findViewById(R.id.choose_photo).setOnClickListener(mClickListener);

        mTvDescription = findViewById(R.id.description);
        mIvFirst = findViewById(R.id.first_image);
        mIvSecond = findViewById(R.id.second_image);
        mIvThird = findViewById(R.id.third_image);

        mIvFirst.setOnClickListener(mClickListener);
        mIvSecond.setOnClickListener(mClickListener);
        mIvThird.setOnClickListener(mClickListener);

        loadData();
    }

    private View.OnClickListener mClickListener = view -> {
        switch (view.getId()) {
            case R.id.choose_photo:
                if (mSelectedImage.isEmpty()) {
                    UiUtil.showShortToast("Please choose child photo to share");
                    return;
                }

                Intent i = new Intent();
                i.putExtra("url", mSelectedImage);
                setResult(RESULT_OK, i);
                finish();
                break;
            case R.id.first_image:
                mIvFirst.setAlpha(0.5f);
                mIvSecond.setAlpha(1.0f);
                mIvThird.setAlpha(1.0f);
                mSelectedImage = images.get(0).getImage();
                break;
            case R.id.second_image:
                mIvFirst.setAlpha(1f);
                mIvSecond.setAlpha(0.5f);
                mIvThird.setAlpha(1.0f);
                mSelectedImage = images.get(1).getImage();
                break;
            case R.id.third_image:
                mIvFirst.setAlpha(1.0f);
                mIvSecond.setAlpha(1.0f);
                mIvThird.setAlpha(0.5f);
                mSelectedImage = images.get(2).getImage();
                break;
        }
    };

    private void loadData() {
        images = ShareCare.dbHelper.getChildPhotos();
        String email = SharedPrefUtil.loadString(this, Constant.PREF_USER_EMAIL, "");
        String photoBasePath = API.PHOTO_BASEPATH + email + File.separator;

        if (images.size() > 0) {
            mTvDescription.setVisibility(View.GONE);
        }

        for (int i = 0; i < images.size(); i++) {
            if (i==0) {
                Picasso.with(this).load(photoBasePath + images.get(i)).into(mIvFirst);
                mIvFirst.setVisibility(View.VISIBLE);
            }
            else if (i == 1) {
                Picasso.with(this).load(photoBasePath + images.get(i)).into(mIvSecond);
                mIvSecond.setVisibility(View.VISIBLE);
            }
            else if (i == 2) {
                Picasso.with(this).load(photoBasePath + images.get(i)).into(mIvThird);
                mIvThird.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
