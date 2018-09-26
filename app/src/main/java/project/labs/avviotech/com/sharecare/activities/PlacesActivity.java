package project.labs.avviotech.com.sharecare.activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import project.labs.avviotech.com.sharecare.R;
import project.labs.avviotech.com.sharecare.ShareCare;
import project.labs.avviotech.com.sharecare.adapters.PlaceAdapter;
import project.labs.avviotech.com.sharecare.models.PlaceModel;
import project.labs.avviotech.com.sharecare.utils.RecyclerTouchListener;
import project.labs.avviotech.com.sharecare.utils.UiUtil;

/**
 * Created by NJX on 3/15/2018.
 */

public class PlacesActivity extends AppCompatActivity {
    private List<PlaceModel> mList;
    private PlaceAdapter adapter;
    private Button addBtn;
    EditText addressEt, nameEt, latEt, lngEt;
    private static final int PLACE_PICKER_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTVTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTVTitle.setText(getString(R.string.place));

        addBtn = findViewById(R.id.inviteBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlace(new PlaceModel());
            }
        });

        RecyclerView recyclerView = findViewById(R.id.caregiver_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mList = new ArrayList<>();
        adapter = new PlaceAdapter(this, mList);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                PlaceModel model = mList.get(position);
                addPlace(model);
            }

            @Override
            public void onLongClick(View view, final int position) {
                PlaceModel model = mList.get(position);
                deletePlace(model);
            }
        }));

        loadData();
    }

    private void deletePlace(PlaceModel model) {
        new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Do you want to remove this place?")
                .setCustomImage(R.drawable.logo)
                .setConfirmText(getString(R.string.delete))
                .setCancelText(getString(R.string.cancel))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        ShareCare.dbHelper.deletePlace(model);
                        sweetAlertDialog.dismissWithAnimation();
                        loadData();
                    }
                }).show();
    }

    private void loadData() {
        mList.clear();
        mList.addAll(ShareCare.dbHelper.getPlaces());
        Log.e("count", String.valueOf(mList.size()));
        adapter.notifyDataSetChanged();
        if (mList.size() == 5)
            addBtn.setEnabled(false);
        else
            addBtn.setEnabled(true);
    }

    private void addPlace(final PlaceModel model) {
        View view = getLayoutInflater().inflate(R.layout.dialog_place, null);
        nameEt = view.findViewById(R.id.place_name);
        addressEt = view.findViewById(R.id.address);
        lngEt = view.findViewById(R.id.longitude);
        latEt = view.findViewById(R.id.latitude);
        lngEt.setEnabled(false);
        latEt.setEnabled(false);
        if (model.getId() != 0) {
            nameEt.setText(model.getName());
            addressEt.setText(model.getAddrss());
            lngEt.setText(String.valueOf(model.getLongitude()));
            latEt.setText(String.valueOf(model.getLatitude()));
        }

        addressEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showPlacePicker();
                }
            }
        });

         new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("")
                .setCustomView(view)
                .setCustomImage(R.drawable.logo)
                .setConfirmText(getString(R.string.save))
                .setCancelText(getString(R.string.cancel))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        double longitude, latitude;
                        String name = nameEt.getText().toString().trim();
                        String address = addressEt.getText().toString().trim();
                        String strLongitude = lngEt.getText().toString();
                        String strLatitude = latEt.getText().toString();
                        if (name.isEmpty()) {
                            UiUtil.showShortToast("Please enter place name");
                            return;
                        }
                        if (address.isEmpty()) {
                            UiUtil.showShortToast("Please enter address");
                            return;
                        }

                        longitude = Double.valueOf(lngEt.getText().toString());
                        latitude = Double.valueOf(latEt.getText().toString());

                        model.setName(name);
                        model.setAddrss(address);
                        model.setLatitude(latitude);
                        model.setLongitude(longitude);
                        ShareCare.dbHelper.savePlace(model);
                        sweetAlertDialog.dismissWithAnimation();
                        loadData();
                    }
                }).show();
    }

    private void showPlacePicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private Address getLocationForAddress(String address) {
        Geocoder coder = new Geocoder(this);
        List<Address> addresses;
        Address location = null;
        try {
            addresses = coder.getFromLocationName(address, 5);
            if (addresses == null || addresses.size() == 0) {
                return null;
            }

            location = addresses.get(0);

        } catch (IOException e) {
            return null;
        }
        return location;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PLACE_PICKER_REQUEST) {
            Place place = PlacePicker.getPlace(data, this);
            if (addressEt != null) {
                addressEt.setText(place.getAddress());
                latEt.setText(String.valueOf(place.getLatLng().latitude));
                lngEt.setText(String.valueOf(place.getLatLng().longitude));
            }
        }
    }
}
