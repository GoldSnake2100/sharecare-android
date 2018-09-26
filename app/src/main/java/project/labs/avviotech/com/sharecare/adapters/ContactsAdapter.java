package project.labs.avviotech.com.sharecare.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import project.labs.avviotech.com.sharecare.R;
import project.labs.avviotech.com.sharecare.models.UserModel;
import project.labs.avviotech.com.sharecare.utils.ImageUtil;

/**
 * Created by Administrator on 5/22/2017.
 */

public class ContactsAdapter extends ArrayAdapter<UserModel> implements Filterable {

    private static class ViewHolder {
        TextView contactName;
        TextView contactEmail;
        TextView contactPhone;
        ImageView photoView;
    }

    private Context mContext;
    private ArrayList<UserModel> objects;
    private boolean isFiltered;

    public ContactsAdapter(Context context, ArrayList<UserModel> contacts, boolean filterFlag) {
        super(context, R.layout.listview_cell_contacts, contacts);
        mContext = context;
        objects = contacts;
        isFiltered = filterFlag;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        UserModel contact = getItem(position);

        ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_cell_contacts, parent, false);

            viewHolder.contactName = (TextView) convertView.findViewById(R.id.contacts_name);
            viewHolder.contactEmail = (TextView) convertView.findViewById(R.id.contacts_email);
            viewHolder.photoView = (ImageView) convertView.findViewById(R.id.contacts_photo);

            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.contactName.setText(contact.getUserName());
        viewHolder.contactEmail.setText(contact.getEmail());
        ImageUtil.setCircularImage(mContext, contact.getImage(), viewHolder.photoView);

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return myFilter;
    }

    Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<UserModel> tempList=new ArrayList<UserModel>();
            //constraint is the result from text you want to filter against.
            //objects is your data set you will filter from
            if(constraint != null && objects!=null) {
                int length=objects.size();
                int i=0;
                while(i<length){
                    UserModel item=objects.get(i);
                    //do whatever you wanna do here
                    //adding result set output array
                    if (item.getUserName().contains(constraint)) {
                        tempList.add(item);
                    }

                    i++;
                }
                //following two lines is very important
                //as publish result can only take FilterResults objects
                filterResults.values = tempList;
                filterResults.count = tempList.size();
            }
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence contraint, FilterResults results) {
            objects.clear();
            objects.addAll((ArrayList<UserModel>)results.values);
            Log.e("result count/", String.valueOf(results.count));
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    };
}