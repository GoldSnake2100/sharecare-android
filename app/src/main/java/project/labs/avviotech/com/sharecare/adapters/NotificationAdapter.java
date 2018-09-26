package project.labs.avviotech.com.sharecare.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import project.labs.avviotech.com.sharecare.R;
import project.labs.avviotech.com.sharecare.models.NotificationModel;
import project.labs.avviotech.com.sharecare.models.PlaceModel;
import project.labs.avviotech.com.sharecare.utils.CommonUtil;

/**
 * Created by NJX on 2/20/2018.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.CustomViewHolder> {

    private Context mContext;
    private List<NotificationModel> mList;

    public NotificationAdapter(Context context, List<NotificationModel> list) {
        mList = list;
        mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_notification, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        NotificationModel model = mList.get(position);
        holder.tvPlace.setText(model.getPlace());
        holder.tvMessage.setText(model.getType());
        holder.tvTime.setText("@" + CommonUtil.convertDateWithTimeStamp(String.valueOf(model.getTimestamp())));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView tvPlace, tvMessage, tvTime;

        public CustomViewHolder(View view) {
            super(view);
            tvPlace = view.findViewById(R.id.place);
            tvMessage = view.findViewById(R.id.type);
            tvTime = view.findViewById(R.id.time);
        }

    }

}
