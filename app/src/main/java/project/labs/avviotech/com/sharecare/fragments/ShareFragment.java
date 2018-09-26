package project.labs.avviotech.com.sharecare.fragments;


import android.Manifest;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import project.labs.avviotech.com.sharecare.R;
import project.labs.avviotech.com.sharecare.adapters.UserAdapter;
import project.labs.avviotech.com.sharecare.bt.ConnectionManager;
import project.labs.avviotech.com.sharecare.models.BTDevice;
import project.labs.avviotech.com.sharecare.models.UserModel;
import project.labs.avviotech.com.sharecare.onesignal.PostNotification;
import project.labs.avviotech.com.sharecare.utils.API;
import project.labs.avviotech.com.sharecare.utils.CircleTransform;
import project.labs.avviotech.com.sharecare.utils.Constant;
import project.labs.avviotech.com.sharecare.utils.Constants;
import project.labs.avviotech.com.sharecare.utils.ImageUtil;
import project.labs.avviotech.com.sharecare.utils.RecyclerTouchListener;
import project.labs.avviotech.com.sharecare.utils.SharedPrefUtil;
import project.labs.avviotech.com.sharecare.utils.Util;

import static android.app.Activity.RESULT_OK;

public class ShareFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private final String TAG = "SharedFragment";

    private View view;
    private RelativeLayout layoutClip, layoutKF, layoutDoor;

    private static final int CHOOSE_CHILD_PHOTO = 1;
    private static final int TAG_ME = 10000;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private List<UserModel> mList = new ArrayList<>();
    private ImageView mIvChildPhoto;
    private TextView description;
    private UserAdapter adapter;
    private String selectedChildPhoto = "";

    private LocationManager mLocationManager;
    private LatLng myLocationLatLng;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 3;

    public ShareFragment() {
        // Required empty public constructor
    }

    public static ShareFragment newInstance(String param1, String param2) {
        ShareFragment fragment = new ShareFragment();

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
        view = inflater.inflate(R.layout.fragment_share, container, false);

        init();
        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        if (mMapView != null) {
            mMapView.getMapAsync(this);
        }

        RecyclerView recyclerView = view.findViewById(R.id.user_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        mList = new ArrayList<>();
        adapter = new UserAdapter(getActivity(), mList);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                UserModel model = mList.get(position);
                showDialogForShare(model);
            }

            @Override
            public void onLongClick(View view, final int position) {

            }
        }));
        return view;
    }

    public void init()
    {
        layoutClip = view.findViewById(R.id.layout_clip);
        layoutKF = view.findViewById(R.id.layout_keyfob);
        layoutDoor = view.findViewById(R.id.layout_door);
        updateUI();
    }

    public void updateUI()
    {
        if(Util.isDeviceConnected("FLEXCLIP"))
        {
            layoutClip.setVisibility(View.VISIBLE);

            if(Constants.deviceMap != null) {
                for (Map.Entry<String, BTDevice> entry : Constants.deviceMap.entrySet()) {
                    if (Constants.connectionManagerMap != null && Constants.connectionManagerMap.get(entry.getKey()) != null) {
                        ConnectionManager manager = Constants.connectionManagerMap.get(entry.getKey());
                        BTDevice device = Constants.deviceMap.get(entry.getKey());
                        String devName = device.getName();

                        if(devName.indexOf("FLEXCLIP1") !=  -1 || devName.indexOf("FLEXCLIP2") !=  -1 || devName.indexOf("FLEXCLIP3") !=  -1)
                        {
                            if(manager.isConnected())
                            {
                                view.findViewById(R.id.clip_temp).setVisibility(View.VISIBLE);
                                BluetoothGattCharacteristic battChar =  manager.getCharacteristic(Constants.CHAR_BATTERY_LEVEL_UUID);
                                if (battChar != null && battChar.getValue() != null) {
                                    int value = battChar.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                                    ((TextView)view.findViewById(R.id.clip_battery)).setText(value + "%");
                                }

                                BluetoothGattCharacteristic tempChar =  manager.getCharacteristic(Constants.CHAR_HTP_TEMPERATURE_MEASUREMENT);
                                if (tempChar != null && tempChar.getValue() != null) {
                                    int temp = Util.convertBytetoInt(tempChar.getValue());
                                    double fahrenheit = (9.0/5.0)*(temp / 10) + 32;
                                    double tempValue = new Double(fahrenheit).intValue();
                                    ((TextView)view.findViewById(R.id.clip_temp)).setText(tempValue + "\u2109" + "");


                                }
                            }


                        }
                    }
                }
            }
        }
        else
            layoutClip.setVisibility(View.GONE);

        if(Util.isDeviceConnected("DOORSENSOR") || Util.isDeviceConnected("DRIVERDOORSENSOR"))
        {
            layoutDoor.setVisibility(View.VISIBLE);
            if(Constants.deviceMap != null) {
                for (Map.Entry<String, BTDevice> entry : Constants.deviceMap.entrySet()) {
                    if (Constants.connectionManagerMap != null && Constants.connectionManagerMap.get(entry.getKey()) != null) {
                        ConnectionManager manager = Constants.connectionManagerMap.get(entry.getKey());
                        BTDevice device = Constants.deviceMap.get(entry.getKey());
                        String devName = device.getName();

                        if(devName.indexOf("DOORSENSOR1") !=  -1 || devName.indexOf("DRIVERDOORSENSOR") !=  -1)
                        {
                            if(manager.isConnected())
                            {
                                view.findViewById(R.id.temp_door).setVisibility(View.VISIBLE);
                                BluetoothGattCharacteristic battChar =  manager.getCharacteristic(Constants.CHAR_BATTERY_LEVEL_UUID);
                                if (battChar != null && battChar.getValue() != null) {
                                    int value = battChar.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                                    ((TextView)view.findViewById(R.id.door_battery)).setText(value + "%");
                                }

                                BluetoothGattCharacteristic tempChar =  manager.getCharacteristic(Constants.CHAR_HTP_TEMPERATURE_MEASUREMENT);
                                if (tempChar != null && tempChar.getValue() != null) {
                                    int temp = Util.convertBytetoInt(tempChar.getValue());
                                    double fahrenheit = (9.0/5.0)*(temp / 10) + 32;
                                    double tempValue = new Double(fahrenheit).intValue();
                                    ((TextView)view.findViewById(R.id.temp_door)).setText(tempValue + "\u2109" + "");


                                }
                            }
                        }
                    }
                }
            }

        }
        else
            layoutDoor.setVisibility(View.GONE);

        if(Util.isDeviceConnected("KEYFOB"))
        {
            layoutKF.setVisibility(View.VISIBLE);
            if(Constants.deviceMap != null) {
                for (Map.Entry<String, BTDevice> entry : Constants.deviceMap.entrySet()) {
                    if (Constants.connectionManagerMap != null && Constants.connectionManagerMap.get(entry.getKey()) != null) {
                        ConnectionManager manager = Constants.connectionManagerMap.get(entry.getKey());
                        BTDevice device = Constants.deviceMap.get(entry.getKey());
                        String devName = device.getName();

                        if(devName.indexOf("KEYFOB1") !=  -1)
                        {
                            if(manager.isConnected())
                            {
                                view.findViewById(R.id.keyfob_temp).setVisibility(View.VISIBLE);


                                BluetoothGattCharacteristic battChar =  manager.getCharacteristic(Constants.CHAR_BATTERY_LEVEL_UUID);
                                if (battChar != null && battChar.getValue() != null) {
                                    int value = battChar.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                                    ((TextView)view.findViewById(R.id.kf_battery)).setText(value + "%");
                                }

                                BluetoothGattCharacteristic tempChar =  manager.getCharacteristic(Constants.CHAR_HTP_TEMPERATURE_MEASUREMENT);
                                if (tempChar != null && tempChar.getValue() != null) {
                                    int temp = Util.convertBytetoInt(tempChar.getValue());
                                    double fahrenheit = (9.0/5.0)*(temp / 10) + 32;
                                    double tempValue = new Double(fahrenheit).intValue();
                                    ((TextView)view.findViewById(R.id.keyfob_temp)).setText(tempValue + "\u2109");


                                }
                            }


                        }

                    }
                }
            }
        }
        else
            layoutKF.setVisibility(View.GONE);
    }


    private void fetchUsers() {
        String token = SharedPrefUtil.loadString(getActivity(), Constant.PREF_PUSH_TOKEN, "");
        Log.e("push_token", token);

        String referralCode = SharedPrefUtil.loadString(getActivity(), Constant.PREF_USER_REFERRAL, "");
        AndroidNetworking.get(API.GET_CAREGIVERS)
                .addQueryParameter("referralCode", referralCode)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Gson gson = new Gson();
                            for (int i = 0; i < response.length(); i++) {
                                String item = response.getString(i);
                                UserModel model = gson.fromJson(item, UserModel.class);
                                mList.add(model);
                                if (isAdded())
                                    addMarker(i, model);
                            }
                        } catch (JSONException e) {

                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }

    private void clearMap() {
        mGoogleMap.clear();
    }

    private void addMarker(int index, final UserModel model) {

        LatLng latLng = new LatLng(model.getLatitude(), model.getLongitude());
        final String title;
        if (model.getUserName().isEmpty())
            title = model.getEmail();
        else
            title = model.getUserName();
        String imageUrl = API.PHOTO_BASEPATH + model.getEmail() + "/" + model.getImage();
        Log.e(TAG, imageUrl);
        Bitmap bigImage = BitmapFactory.decodeResource(getResources(), R.drawable.marker_icon);
        Picasso.with(getActivity())
                .load(imageUrl)
                .resize(bigImage.getWidth() - 60, bigImage.getHeight() - 60)
                .transform(new CircleTransform())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Bitmap mergedImage = ImageUtil.createSingleImageFromMultipleImages(bigImage, bitmap);
                        mGoogleMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromBitmap(mergedImage))
                                .anchor(0.5f, 1.0f)
                                .position(latLng)
                                .title(title)).setTag(index);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        Bitmap smallImage = BitmapFactory.decodeResource(getResources(), R.drawable.default_user);
                        smallImage = Bitmap.createScaledBitmap(smallImage,bigImage.getWidth() - 60, bigImage.getHeight() - 60, true);
                        Bitmap mergedImage = ImageUtil.createSingleImageFromMultipleImages(bigImage, smallImage);
                        mGoogleMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromBitmap(mergedImage))
                                .anchor(0.5f, 1.0f)
                                .position(latLng)
                                .title(title)).setTag(index);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        MapsInitializer.initialize(this.getActivity());
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        //set map type
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mGoogleMap.setOnMarkerClickListener(marker -> {
            int index = Integer.valueOf(marker.getTag().toString());
            if (index != TAG_ME) {
                UserModel model = mList.get(index);
                showDialogForShare(model);
            }
            return true;
        });
        if (isAdded())
            moveToMyLocation();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == CHOOSE_CHILD_PHOTO) {
            String email = SharedPrefUtil.loadString(getActivity(), Constant.PREF_USER_EMAIL, "");
            String photoBasePath = API.PHOTO_BASEPATH + email + File.separator;
            selectedChildPhoto = data.getStringExtra("url");
            Picasso.with(getActivity()).load(photoBasePath + selectedChildPhoto).into(mIvChildPhoto);
            mIvChildPhoto.setVisibility(View.VISIBLE);
            description.setVisibility(View.GONE);
        }
    }

    private void showDialogForShare(UserModel model) {
        String title;
        if (model.getUserName().isEmpty())
            title = model.getEmail();
        else
            title = model.getUserName();

        new SweetAlertDialog(getActivity(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Share with " + title)
//                .setCustomView(view)
                .setCustomImage(R.drawable.logo)
                .setConfirmText(getString(R.string.title_share))
                .setCancelText(getString(R.string.cancel))
                .setConfirmClickListener(sweetAlertDialog -> {
                    JSONObject data = new JSONObject();

                    String token = SharedPrefUtil.loadString(getActivity(), Constant.PREF_PUSH_TOKEN, "");
                    String message = SharedPrefUtil.loadString(getActivity(), Constant.PREF_USER_NAME, "");
                    String email = SharedPrefUtil.loadString(getActivity(), Constant.PREF_USER_EMAIL, "");
                    if (message.isEmpty())
                        message = email;
                    message += " want to share children";

                    try {
                        data.put(Constant.PUSH_TYPE, Constant.PUSH_TYPE_SHARE);
                        data.put("url", selectedChildPhoto);
                        data.put("sender", email);
                        data.put("sender_token", token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    List<String> receivers = new ArrayList<>();
                    receivers.add(model.getPush_token());
                    PostNotification.send(message, data, receivers);
                    sweetAlertDialog.dismissWithAnimation();
                }).show();
    }


    private void moveToMyLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_ACCESS_FINE_LOCATION);
                return;
            }
        }

        mGoogleMap.setMyLocationEnabled(false);
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Get Current Location
        Location myLocation = getLastKnownLocation();
        if (myLocation != null) {
            // Get latitude of the current location
            double latitude = myLocation.getLatitude();

            // Get longitude of the current location
            double longitude = myLocation.getLongitude();
            // Create a LatLng object for the current location
            myLocationLatLng = new LatLng(latitude, longitude);
            moveCamera(myLocationLatLng);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fetchUsers();
                }
            }, 500);
        } else {
            mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
        }
    }

    private Location getLastKnownLocation() {
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            Location l = mLocationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }

    private void moveCamera(LatLng latLng){
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    moveToMyLocation();
                } else {
                    return;
                }
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null)
            mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null)
            mMapView.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
