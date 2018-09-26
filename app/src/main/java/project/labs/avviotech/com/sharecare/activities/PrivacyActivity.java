package project.labs.avviotech.com.sharecare.activities;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import project.labs.avviotech.com.sharecare.R;
import project.labs.avviotech.com.sharecare.adapters.SettingAdapter;
import project.labs.avviotech.com.sharecare.models.SettingModel;
import project.labs.avviotech.com.sharecare.utils.Constant;
import project.labs.avviotech.com.sharecare.utils.RecyclerTouchListener;
import project.labs.avviotech.com.sharecare.utils.SharedPrefUtil;
import project.labs.avviotech.com.sharecare.utils.UiUtil;
import project.labs.avviotech.com.sharecare.utils.Util;

public class PrivacyActivity extends AppCompatActivity {

    private List<SettingModel> mList;
    private SettingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTVTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTVTitle.setText(getString(R.string.privacy));

        RecyclerView recyclerView = findViewById(R.id.privacy_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mList = new ArrayList<>();
        initRecycleView();
        adapter = new SettingAdapter(this, mList);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                SettingModel model = mList.get(position);
                model.isChecked = !model.isChecked;
                switch (position) {
                    case 0:
                        SharedPrefUtil.saveBoolean(PrivacyActivity.this, Constant.PREF_AUTO_CONNECT, model.isChecked);
                        break;
                    case 1:
                        Intent i = new Intent(Constant.BROADCAST_LOCATION_STATE);
                        i.putExtra("state", model.isChecked);
                        Log.e("MainActivity", "privaty===" + String.valueOf(model.isChecked));
                        LocalBroadcastManager.getInstance(PrivacyActivity.this).sendBroadcast(i);
                        break;
                    case 2:
                        Util.enableOnline(PrivacyActivity.this, model.isChecked);
                        break;
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    private void initRecycleView() {
        SettingModel connect = new SettingModel("Auto Connect", false, true);
        SettingModel location = new SettingModel("Enable Location", true, true);
        SettingModel online = new SettingModel("Enable Online", false, true);

        connect.isChecked = SharedPrefUtil.loadBoolean(this, Constant.PREF_AUTO_CONNECT, true);
        location.isChecked = SharedPrefUtil.loadBoolean(this, Constant.PREF_LOCATION_ENABLE, true);
        online.isChecked = Util.isNetworkAvailable(this);

        mList.add(connect);
        mList.add(location);
        mList.add(online);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
