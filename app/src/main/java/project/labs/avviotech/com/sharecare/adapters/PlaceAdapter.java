package project.labs.avviotech.com.sharecare.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;

import java.util.List;

import project.labs.avviotech.com.sharecare.R;
import project.labs.avviotech.com.sharecare.models.PlaceModel;

/**
 * Created by NJX on 2/20/2018.
 */

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.CustomViewHolder> {

    private Context mContext;
    private List<PlaceModel> mList;

    public PlaceAdapter(Context context, List<PlaceModel> list) {
        mList = list;
        mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_place, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        PlaceModel model = mList.get(position);
        holder.tvPlace.setText(model.getName());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView tvPlace;

        public CustomViewHolder(View view) {
            super(view);
            tvPlace = view.findViewById(R.id.place_item);
        }

    }

}
