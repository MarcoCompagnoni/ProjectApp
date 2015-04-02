package com.banana.projectapp.db;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.banana.projectapp.campagne.Campaign;
import com.banana.projectapp.shop.ShoppingItem;

public class DBManager{

    public static final String DATABASE_NAME = "FRIENDZ.db";
    public static final int DATABASE_VERSION = 2;

    public static final String CAMPAIGNS_TABLE = "CAMPAIGNS";
    public static final String SHOPPING_ITEMS_TABLE = "SHOPPING_ITEMS";

    public static final String ID = "_id";
    public static final String URL = "url";
    public static final String LOGO = "logo";
    public static final String NAME = "name";
    public static final String CREDITS = "credits";
    public static final String TYPE = "type";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    private SQLiteDatabase db;
    private DBHelper dbHelper;

    private boolean isConnected;

    public DBManager(Context context) throws NullPointerException{
        if(context == null)
            throw new NullPointerException();
        this.dbHelper = new DBHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.isConnected = false;
    }

    public DBManager open() throws SQLiteException{
        db = dbHelper.getWritableDatabase();
        isConnected = true;
        return this;
    }

    public void close() throws SQLiteException{
        db.close();
        isConnected = false;
    }

    public long insert(Campaign campaign) throws SQLiteException,NullPointerException{
        if(campaign == null)
            throw new NullPointerException("missing campaign");
        ContentValues values = toContentValue(campaign);
        return db.insert(CAMPAIGNS_TABLE, null, values);
    }
    public long insert(ShoppingItem item) throws SQLiteException,NullPointerException{
        if(item == null)
            throw new NullPointerException("missing item");
        ContentValues values = toContentValue(item);
        return db.insert(SHOPPING_ITEMS_TABLE, null, values);
    }

    private ContentValues toContentValue(Campaign campaign) throws NullPointerException{
        if(campaign == null)
            throw new NullPointerException("missing campaign");
        ContentValues values = new ContentValues();
        values.put(ID, campaign.getId());
        values.put(URL, campaign.getUrl());
        values.put(LOGO, getBytes(campaign.getLogo()));
        values.put(NAME, campaign.getName());
        values.put(CREDITS, campaign.getUserGain());
        values.put(TYPE, campaign.getType().toString());
        values.put(LATITUDE, campaign.getLatitude());
        values.put(LONGITUDE, campaign.getLongitude());
        return values;
    }
    private ContentValues toContentValue(ShoppingItem item) throws NullPointerException{
        if(item == null)
            throw new NullPointerException("missing item");
        ContentValues values = new ContentValues();
        values.put(ID, item.getId());
        values.put(URL, item.getUrl());
        values.put(LOGO, getBytes(item.getLogo()));
        values.put(NAME, item.getName());
        values.put(CREDITS, item.getCredits());
        return values;
    }

    public int remove(long idx, String table) throws SQLiteException{
        return db.delete(table, ID + "=" + idx, null);
    }

    public int deleteCampaigns() throws SQLiteException{
        return db.delete(DBManager.CAMPAIGNS_TABLE, "1=1", null);
    }

    public int deleteShoppingItems() throws SQLiteException{
        return db.delete(DBManager.SHOPPING_ITEMS_TABLE, "1=1", null);
    }

    private Cursor getAllEntries(String table) throws SQLiteException{
        return db.query(table, null, null, null, null, null, null);
    }

    public List<Campaign> getCampaigns() throws SQLiteException{
        Cursor cursor = this.getAllEntries(CAMPAIGNS_TABLE);
        ArrayList<Campaign> list = new ArrayList<>();
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            long id = cursor.getLong(cursor.getColumnIndex(DBManager.ID));
            String name = cursor.getString(cursor.getColumnIndex(DBManager.NAME));
            String url = cursor.getString(cursor.getColumnIndex(DBManager.URL));
            Bitmap logo = getImage(cursor.getBlob(cursor.getColumnIndex(DBManager.LOGO)));
            float userGain = cursor.getFloat(cursor.getColumnIndex(DBManager.CREDITS));
            String type = cursor.getString(cursor.getColumnIndex(DBManager.TYPE));
            double latitude = cursor.getDouble(cursor.getColumnIndex(DBManager.LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndex(DBManager.LONGITUDE));

            Campaign newCampaign = new Campaign(
                    id, url, name, userGain, Campaign.CampaignType.valueOf(type),
                    latitude, longitude);
            newCampaign.setLogo(logo);

            list.add(newCampaign);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }
    public List<ShoppingItem> getShoppingItems() throws SQLiteException{
        Cursor cursor = this.getAllEntries(SHOPPING_ITEMS_TABLE);
        ArrayList<ShoppingItem> list = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            long id = cursor.getLong(cursor.getColumnIndex(DBManager.ID));
            String url = cursor.getString(cursor.getColumnIndex(DBManager.URL));
            String name = cursor.getString(cursor.getColumnIndex(DBManager.NAME));
            Bitmap logo = getImage(cursor.getBlob(cursor.getColumnIndex(DBManager.LOGO)));
            float credits = cursor.getFloat(cursor.getColumnIndex(DBManager.CREDITS));

            ShoppingItem newItem = new ShoppingItem(id, url, logo, name, credits);

            list.add(newItem);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public boolean dbIsOpen() {
        return isConnected;
    }

    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        private static final String SQL_CREATE_TABLE_CAMPAIGNS = "create table "
                + CAMPAIGNS_TABLE + " (" //
                + ID + " integer primary key, "
                + URL + " text not null, "
                + NAME + " text not null, "
                + LOGO + " blob not null,"
                + CREDITS + " real not null,"
                + TYPE + " text not null,"
                + LATITUDE + " real not null,"
                + LONGITUDE + " real not null);";

        private static final String SQL_CREATE_TABLE_SHOPPING_ITEMS = "create table "
                + SHOPPING_ITEMS_TABLE + " (" //
                + ID + " integer primary key, "
                + URL + " text not null,"
                + NAME + " text not null, "
                + LOGO + " blob not null, "
                + CREDITS + " real not null);";

        public void onCreate(SQLiteDatabase db) throws SQLiteException {

            db.execSQL(SQL_CREATE_TABLE_CAMPAIGNS);
            db.execSQL(SQL_CREATE_TABLE_SHOPPING_ITEMS);

        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) throws SQLiteException{
            db.execSQL("drop table if exists "+SHOPPING_ITEMS_TABLE+";");
            db.execSQL("drop table if exists "+CAMPAIGNS_TABLE+";");
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) throws SQLiteException{
            db.execSQL("drop table if exists "+SHOPPING_ITEMS_TABLE+";");
            db.execSQL("drop table if exists "+CAMPAIGNS_TABLE+";");
            onCreate(db);
        }

    }
    public static byte[] getBytes(Bitmap bitmap)
    {
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,0, stream);
        return stream.toByteArray();
    }
    public static Bitmap getImage(byte[] image)
    {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}