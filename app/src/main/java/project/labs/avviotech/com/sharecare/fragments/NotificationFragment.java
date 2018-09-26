package project.labs.avviotech.com.sharecare.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import project.labs.avviotech.com.sharecare.R;
import project.labs.avviotech.com.sharecare.ShareCare;
import project.labs.avviotech.com.sharecare.activities.MainActivity;
import project.labs.avviotech.com.sharecare.adapters.NotificationAdapter;
import project.labs.avviotech.com.sharecare.models.NotificationModel;
import project.labs.avviotech.com.sharecare.utils.CommonUtil;
import project.labs.avviotech.com.sharecare.utils.Constant;
import project.labs.avviotech.com.sharecare.utils.UiUtil;

public class NotificationFragment extends Fragment {

    private String TAG = "NotificationFragment";
    private TextView mTvLastNotification, mTvLastNotiTime;
    private Button cancelNotification;
    private List<NotificationModel> mList;
    private NotificationAdapter adapter;

    private void loadData() {
        mList.clear();
        List<NotificationModel> list = ShareCare.dbHelper.getNotifications();
        for (int i = 0; i < list.size(); i++) {
            NotificationModel model = list.get(i);
            if (i == 0) {
                mTvLastNotification.setText(model.getPlace() + "\t" + model.getType());
                mTvLastNotiTime.setText("@" + CommonUtil.convertDateWithTimeStamp(String.valueOf(model.getTimestamp())));
                if (model.getType().contains("ON"))
                    cancelNotification.setVisibility(View.VISIBLE);
            } else {
                mList.add(model);
            }
        }
        adapter.notifyDataSetChanged();
    }

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        mTvLastNotification = view.findViewById(R.id.notification_content);
        mTvLastNotiTime = view.findViewById(R.id.notification_time);

        RecyclerView recyclerView = view.findViewById(R.id.notification_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        mList = new ArrayList<>();
        adapter = new NotificationAdapter(getActivity(), mList);
        recyclerView.setAdapter(adapter);

        cancelNotification = view.findViewById(R.id.event_cancel);
        cancelNotification.setVisibility(View.GONE);
        cancelNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationModel model = mList.get(0);
                String notification = model.getType();
                notification = notification.replace("ON", "OFF");
                ((MainActivity)getActivity()).notifyEvent(Constant.PUSH_TYPE_CHILD_EVENT_OFF, notification);
                UiUtil.showShortToast("This event cancelled");
                cancelNotification.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadData();
                    }
                }, 500);
            }
        });

        loadData();
        return view;
    }
}
