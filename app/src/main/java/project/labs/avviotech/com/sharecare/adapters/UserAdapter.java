package project.labs.avviotech.com.sharecare.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import project.labs.avviotech.com.sharecare.R;
import project.labs.avviotech.com.sharecare.models.UserModel;
import project.labs.avviotech.com.sharecare.utils.API;

/**
 * Created by NJX on 2/20/2018.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.CustomViewHolder> {

    private Context mContext;
    private List<UserModel> mList;
    private boolean isFollower = true;

    public UserAdapter(Context context, List<UserModel> list) {
        mList = list;
        mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_user, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        final UserModel model = mList.get(position);
        holder.setIsRecyclable(false);
        holder.tvUsername.setText(model.getUserName());
        holder.tvUserEmail.setText(model.getEmail());
        if (model.getImage().isEmpty()) {
            holder.ivUserPhoto.setImageResource(R.drawable.default_user);
        } else {
            String url = API.PHOTO_BASEPATH + model.getEmail() + "/" + model.getImage();
            Picasso.with(mContext).load(url)
                    .placeholder(R.drawable.default_user)
                    .error(R.drawable.default_user).into(holder.ivUserPhoto);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUsername, tvUserEmail;
        private ImageView ivUserPhoto;

        public CustomViewHolder(View view) {
            super(view);
            tvUsername = view.findViewById(R.id.friend_username);
            tvUserEmail = view.findViewById(R.id.friend_email);
            ivUserPhoto = view.findViewById(R.id.friend_user_photo);
        }

    }

}
