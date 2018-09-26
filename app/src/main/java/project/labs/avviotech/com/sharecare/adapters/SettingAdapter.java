package project.labs.avviotech.com.sharecare.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.squareup.picasso.Picasso;

import java.util.List;

import project.labs.avviotech.com.sharecare.R;
import project.labs.avviotech.com.sharecare.models.SettingModel;
import project.labs.avviotech.com.sharecare.models.UserModel;

/**
 * Created by NJX on 2/20/2018.
 */

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.CustomViewHolder> {

    private Context mContext;
    private List<SettingModel> mList;

    public SettingAdapter(Context context, List<SettingModel> list) {
        mList = list;
        mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_setting, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        SettingModel model = mList.get(position);
        holder.setIsRecyclable(false);
        holder.tvSetting.setText(model.getItem());
        if (model.isSwitch) {
            holder.ivNext.setVisibility(View.GONE);
            holder.switchButton.setVisibility(View.VISIBLE);
            holder.switchButton.setChecked(model.isChecked);
        } else {
            holder.ivNext.setVisibility(View.VISIBLE);
            holder.switchButton.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView tvSetting;
        private ImageView ivNext;
        private SwitchButton switchButton;

        public CustomViewHolder(View view) {
            super(view);
            tvSetting = view.findViewById(R.id.setting_item);
            ivNext = view.findViewById(R.id.next_arrow);
            switchButton = view.findViewById(R.id.switchBtn);
            switchButton.setClickable(false);
        }

    }

}
