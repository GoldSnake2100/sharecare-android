package project.labs.avviotech.com.sharecare.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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

import project.labs.avviotech.com.sharecare.R;
import project.labs.avviotech.com.sharecare.ShareCare;
import project.labs.avviotech.com.sharecare.activities.MainActivity;
import project.labs.avviotech.com.sharecare.models.PlaceModel;
import project.labs.avviotech.com.sharecare.models.UserModel;
import project.labs.avviotech.com.sharecare.utils.API;
import project.labs.avviotech.com.sharecare.utils.CircleTransform;
import project.labs.avviotech.com.sharecare.utils.CommonUtil;
import project.labs.avviotech.com.sharecare.utils.Constant;
import project.labs.avviotech.com.sharecare.utils.ImageUtil;
import project.labs.avviotech.com.sharecare.utils.SharedPrefUtil;

import static android.app.Activity.RESULT_OK;

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    public static LatLng myLocationLatLng;
    private static final int CHOOSE_CHILD_PHOTO = 1;
    private static final int TAG_ME = 10000;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private LocationManager mLocationManager;
    private List<UserModel> mList = new ArrayList<>();
    private ImageView mIvChildPhoto;
    private TextView description;
    private String selectedChildPhoto = "";
    private Marker myMarker;

    private String TAG = "MapFragment";

    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 3;

    public MapFragment() {
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
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        if (mMapView != null) {
            mMapView.getMapAsync(this);
        }

        view.findViewById(R.id.myLocBtn).setOnClickListener(mClickListener);
        return view;
    }


    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.myLocBtn)
                moveCamera(myLocationLatLng);
        }
    };

    private void fetchUsers() {
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
        String snippet = MainActivity.getAddressForLocation(model.getLatitude(), model.getLongitude());

        String imageUrl = API.PHOTO_BASEPATH + model.getEmail() + "/" + model.getImage();
        Bitmap bigImage = BitmapFactory.decodeResource(getResources(), R.drawable.marker_icon);
        Picasso.with(getActivity())
                .load(imageUrl)
                .resize(bigImage.getWidth() - 60, bigImage.getHeight() - 60)
                .transform(new CircleTransform())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        if (isAdded()) {
                            Bitmap mergedImage = ImageUtil.createSingleImageFromMultipleImages(bigImage, bitmap);

                            mGoogleMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromBitmap(mergedImage))
                                    .anchor(0.5f, 1.0f)
                                    .position(latLng)
                                    .title(title)
                                    .snippet(snippet)).setTag(index);
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        if (isAdded()) {
                            Bitmap smallImage = BitmapFactory.decodeResource(getResources(), R.drawable.default_user);
                            smallImage = Bitmap.createScaledBitmap(smallImage, bigImage.getWidth() - 60, bigImage.getHeight() - 60, true);
                            Bitmap mergedImage = ImageUtil.createSingleImageFromMultipleImages(bigImage, smallImage);

                            mGoogleMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromBitmap(mergedImage))
                                    .anchor(0.5f, 1.0f)
                                    .position(latLng)
                                    .title(title)
                                    .snippet(snippet)).setTag(index);
                        }
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    private void addMeOnMap() {
        String title = "";
        String snippet = "";
        List<PlaceModel> placeModels = ShareCare.dbHelper.getPlaces();
        int distance = 0;
        for (int i = 0; i < placeModels.size(); i++) {
            PlaceModel place = placeModels.get(i);
            distance = CommonUtil.getDistance(myLocationLatLng, new LatLng(place.getLatitude(), place.getLongitude()));
            Log.e("distance", String.valueOf(distance));
            if (distance < Constant.DISTANCE_OFFSET) {
                title = place.getName();
                snippet = place.getAddrss();
                break;
            }
        }
        Bitmap smallImage = BitmapFactory.decodeResource(getResources(), R.drawable.me);
        smallImage = Bitmap.createScaledBitmap(smallImage, 110, 110, true);
        MarkerOptions options = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(smallImage))
                .anchor(0.5f, 0.5f)
                .position(myLocationLatLng);
        if (!title.isEmpty())
            options.title(title).snippet(snippet);
        myMarker = mGoogleMap.addMarker(options);
        myMarker.setTag(TAG_ME);
        if (!title.isEmpty())
            myMarker.showInfoWindow();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchUsers();
            }
        }, 100);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        MapsInitializer.initialize(this.getActivity());
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        //set map type
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mGoogleMap.setOnMarkerClickListener(marker -> {
            int index = Integer.valueOf(marker.getTag().toString());
            if (index != TAG_ME) {
                UserModel model = mList.get(index);
//                showDialogForShare(model);
                String address = MainActivity.getAddressForLocation(model.getLatitude(), model.getLongitude());
                Log.e("address===", address);
            }
            return false;
        });
        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                String strTitle = marker.getTitle();
                if (strTitle == null || strTitle.isEmpty())
                    return  null;
                LinearLayout info = new LinearLayout(getActivity());
                info.setOrientation(LinearLayout.VERTICAL);
//                info.setBackgroundColor(Color.parseColor("#C00000FF"));

                TextView title = new TextView(getActivity());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTextColor(getResources().getColor(R.color.red));
                title.setTextSize(18);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getActivity());
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
        // wait marker initialization
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
            if (SharedPrefUtil.loadBoolean(getActivity(), Constant.PREF_LOCATION_ENABLE, false))
                addTrackedLocation();
            moveCamera(myLocationLatLng);
            addMeOnMap();

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

    public void addTrackedLocation() {
        int userId = SharedPrefUtil.loadInt(getActivity(), Constant.PREF_USER_ID, 0);

        JSONObject param = new JSONObject();
        try {
            param.put("userid", userId);
            param.put("latitude", myLocationLatLng.latitude);
            param.put("longitude", myLocationLatLng.longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(API.UPDATE_LOCATION)
                .addJSONObjectBody(param)
                .setTag("LocationList")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, anError.getMessage());
                    }
                });
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
        if (mMapView != null)
            mMapView.onResume();
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
}
