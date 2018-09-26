package project.labs.avviotech.com.sharecare.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import project.labs.avviotech.com.sharecare.R;
import project.labs.avviotech.com.sharecare.activities.CareGiverActivity;
import project.labs.avviotech.com.sharecare.activities.ChildPhotoActivity;
import project.labs.avviotech.com.sharecare.activities.LoginActivity;
import project.labs.avviotech.com.sharecare.activities.PlacesActivity;
import project.labs.avviotech.com.sharecare.activities.PrivacyActivity;
import project.labs.avviotech.com.sharecare.activities.ProfileActivity;
import project.labs.avviotech.com.sharecare.adapters.SettingAdapter;
import project.labs.avviotech.com.sharecare.models.SettingModel;
import project.labs.avviotech.com.sharecare.utils.Constant;
import project.labs.avviotech.com.sharecare.utils.RecyclerTouchListener;
import project.labs.avviotech.com.sharecare.utils.SharedPrefUtil;

public class SettingFragment extends Fragment {

    private List<SettingModel> mList;
    private SettingAdapter adapter;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.setting_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        mList = new ArrayList<>();
        initRecycleView();
        adapter = new SettingAdapter(getActivity(), mList);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                if (position == 0) {
                    Intent i = new Intent(getActivity(), PrivacyActivity.class);
                    startActivity(i);
                } else if (position == 1) {
                    Intent i = new Intent(getActivity(), ProfileActivity.class);
                    startActivity(i);
                } else if (position == 2) {
                    Intent i = new Intent(getActivity(), CareGiverActivity.class);
                    startActivity(i);
                } else if (position == 3) {
                    Intent i = new Intent(getActivity(), ChildPhotoActivity.class);
                    startActivity(i);
                } else if (position == 4) {
                    Intent i = new Intent(getActivity(), PlacesActivity.class);
                    startActivity(i);
                } else if (position == 5) {
                    logout();
                }
            }

            @Override
            public void onLongClick(View view, final int position) {

            }
        }));
        return view;
    }

    private void logout() {
            TextView content = new TextView(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            content.setLayoutParams(params);
            content.setGravity(Gravity.CENTER);
            content.setMovementMethod(new ScrollingMovementMethod());
            content.setVerticalScrollBarEnabled(true);
            content.setText("Do you want to log out?");
            new SweetAlertDialog(getActivity(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText("")
                    .setCustomView(content)
                    .setCustomImage(R.drawable.logo)
                    .setConfirmText(getString(R.string.ok))
                    .setCancelText(getString(R.string.cancel))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                            SharedPrefUtil.saveBoolean(getActivity(), Constant.PREF_AUTOLOGIN, false);
                            Intent i = new Intent(getActivity(), LoginActivity.class);
                            startActivity(i);
                            getActivity().finish();
                        }
                    }).show();
    }

    private void initRecycleView() {
        mList.add(new SettingModel("Privacy", false, false));
        mList.add(new SettingModel("Profile", false, false));
        mList.add(new SettingModel("CareGivers", false, false));
        mList.add(new SettingModel("Child Photo", false, false));
        mList.add(new SettingModel("Default Place", false, false));
        mList.add(new SettingModel("Log out", false, false));
    }

}
