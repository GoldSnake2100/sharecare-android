package project.labs.avviotech.com.sharecare.utils;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import project.labs.avviotech.com.sharecare.models.ChildModel;
import project.labs.avviotech.com.sharecare.models.NotificationModel;
import project.labs.avviotech.com.sharecare.models.PlaceModel;
import project.labs.avviotech.com.sharecare.models.UserModel;

/**
 * Created by Administrator on 6/6/2017.
 */

public class DBHelper {

    private SQLiteHelper sqLiteHelper;

    private static final String TABLE_CHILD_PHOTO = "tbl_childphoto";
    private static final String TABLE_CAREGIVER = "tbl_caregiver";
    private static final String TABLE_NOTIFICATION = "tbl_notification";
    private static final String TABLE_PLACES = "tbl_places";

    public DBHelper(Context context){
        sqLiteHelper = new SQLiteHelper(context);
        try {
            sqLiteHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllData() {
        sqLiteHelper.openDataBase();
        String sql = "delete from " + TABLE_CAREGIVER;
        sqLiteHelper.executeSQL(sql);
        sql = "delete from " + TABLE_CHILD_PHOTO;
        sqLiteHelper.executeSQL(sql);
        sql = "delete from " + TABLE_NOTIFICATION;
        sqLiteHelper.executeSQL(sql);
        sql = "delete from " + TABLE_PLACES;
        sqLiteHelper.executeSQL(sql);
        sqLiteHelper.close();
    }

    // child photo
    public void saveChildData(String path, String name, int index) {
        sqLiteHelper.openDataBase();
        String sql = "INSERT OR REPLACE INTO " + TABLE_CHILD_PHOTO + " (id, image, name) VALUES (" +
                index + ", '" + path + "' ,'" + name + "');";
        Log.e("sql", sql);
        sqLiteHelper.executeSQL(sql);
        sqLiteHelper.close();
    }

    public ChildModel getChildByIndex(int index) {
        ChildModel model = new ChildModel();
        sqLiteHelper.openDataBase();
        String sql = "select image, name from " + TABLE_CHILD_PHOTO + " where id=" + index;
        Cursor cursor = sqLiteHelper.rawQuery(sql);
        if (cursor.moveToFirst()) {
            model.setImage(cursor.getString(0));
            model.setName(cursor.getString(1));
        }
        cursor.close();

        sqLiteHelper.close();
        return model;
    }

    public List<ChildModel> getChildPhotos() {
        List<ChildModel> list = new ArrayList<>();
        sqLiteHelper.openDataBase();
        String sql = "select image, name from " + TABLE_CHILD_PHOTO;
        Cursor cursor = sqLiteHelper.rawQuery(sql);
        if (cursor.moveToFirst()) {
            do {
                ChildModel model = new ChildModel();
                model.setImage(cursor.getString(0));
                model.setName(cursor.getString(1));
                list.add(model);
            }while(cursor.moveToNext());
        }
        cursor.close();

        sqLiteHelper.close();
        return list;
    }

    // CareGiver
    public void saveCareGiver(UserModel model) {
        sqLiteHelper.openDataBase();
        String sql = "insert into " + TABLE_CAREGIVER + " (userId, name, email, image) VALUES (" +
                model.getUserID() + ", '" + model.getUserName() + "', '" + model.getEmail() + "', '" + model.getImage() + "')";
        sqLiteHelper.executeSQL(sql);
        sqLiteHelper.close();
    }

    public void deleteCareGiver(UserModel model) {
        sqLiteHelper.openDataBase();
        String sql = "delete from " + TABLE_CAREGIVER + " where email='" + model.getEmail() + "'";
        sqLiteHelper.executeSQL(sql);
        sqLiteHelper.close();
    }

    public List<UserModel> getCareGivers() {
        List<UserModel> list = new ArrayList<>();
        sqLiteHelper.openDataBase();
        String sql = "select * from " + TABLE_CAREGIVER;
        Cursor cursor = sqLiteHelper.rawQuery(sql);
        if (cursor.moveToFirst()) {
            do {
                UserModel model = new UserModel();
                model.setUserID(cursor.getInt(1));
                model.setUserName(cursor.getString(2));
                model.setEmail(cursor.getString(3));
                model.setImage(cursor.getString(4));
                list.add(model);
            }while(cursor.moveToNext());
        }
        cursor.close();

        sqLiteHelper.close();
        return list;
    }

    public String getCareGiverIds() {
        String ids = "";
        sqLiteHelper.openDataBase();
        String sql = "select userId from " + TABLE_CAREGIVER;
        Cursor cursor = sqLiteHelper.rawQuery(sql);
        if (cursor.moveToFirst()) {
            do {
                int userid = cursor.getInt(0);
                ids += String.valueOf(userid) + ",";
            }while(cursor.moveToNext());
        }
        cursor.close();

        sqLiteHelper.close();
        if (!ids.isEmpty())
            ids = ids.substring(0, ids.length() - 1);
        return ids;
    }

    // default places
    public List<PlaceModel> getPlaces() {
        List<PlaceModel> list = new ArrayList<>();
        sqLiteHelper.openDataBase();
        String sql = "select * from " + TABLE_PLACES;
        Cursor cursor = sqLiteHelper.rawQuery(sql);
        if (cursor.moveToFirst()) {
            do {
                PlaceModel model = new PlaceModel();
                model.setId(cursor.getInt(0));
                model.setName(cursor.getString(1));
                model.setAddrss(cursor.getString(2));
                model.setLongitude(cursor.getDouble(3));
                model.setLatitude(cursor.getDouble(4));
                list.add(model);
            }while(cursor.moveToNext());
        }
        cursor.close();
        sqLiteHelper.close();
        return list;
    }

    public void savePlace(PlaceModel model) {
        sqLiteHelper.openDataBase();
        String sql = "INSERT OR REPLACE INTO " + TABLE_PLACES + " (place_name, address, longitude, latitude) VALUES ('" +
                model.getName() + "', '" + model.getAddrss() + "', " + model.getLongitude() + ", " + model.getLatitude() + ")";
        Log.e("address===", sql);
        sqLiteHelper.executeSQL(sql);
        sqLiteHelper.close();
    }

    public void deletePlace(PlaceModel model) {
        sqLiteHelper.openDataBase();
        String sql = "delete from " + TABLE_PLACES + " where id=" + model.getId();
        sqLiteHelper.executeSQL(sql);
        sqLiteHelper.close();
    }

    // Notifications
    public void saveNotification(NotificationModel model) {
        sqLiteHelper.openDataBase();
        String sql = "INSERT INTO " + TABLE_NOTIFICATION + " (message, place, timestamp) VALUES ('" +
                model.getType() + "', '" + model.getPlace() + "', " + model.getTimestamp() + ")";
        sqLiteHelper.executeSQL(sql);
        sqLiteHelper.close();
    }

    public List<NotificationModel> getNotifications() {
        List<NotificationModel> list = new ArrayList<>();
        sqLiteHelper.openDataBase();
        String sql = "Select * from " + TABLE_NOTIFICATION + " Order By timestamp Desc";
        Cursor cursor = sqLiteHelper.rawQuery(sql);
        if (cursor.moveToFirst()) {
            do {
                NotificationModel model = new NotificationModel();
                model.setId(cursor.getInt(0));
                model.setType(cursor.getString(1));
                model.setPlace(cursor.getString(2));
                model.setTimestamp(cursor.getLong(3));
                list.add(model);
            } while (cursor.moveToNext());
        }
        sqLiteHelper.close();
        return list;
    }
}
