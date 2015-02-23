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

import com.banana.projectapp.campagne.CompanyCampaign;
import com.banana.projectapp.profile.Social;
import com.banana.projectapp.shop.ShoppingItem;

public class DBManager{

    public static final String DATABASE_NAME = "PROJECT.db";
    public static final int DATABASE_VERSION = 1;

    public static final String CAMPAIGNS_TABLE = "CAMPAIGNS";
    public static final String SOCIALS_TABLE = "SOCIALS";
    public static final String SHOPPING_ITEMS_TABLE = "SHOPPING_ITEMS";

    public static final String ID = "_id";
    public static final String LOGO = "logo";
    public static final String NAME = "name";
    public static final String CREDITS = "credits";

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

    public long insert(Social social) throws SQLiteException,NullPointerException{
        if(social == null)
            throw new NullPointerException("missing account");
        ContentValues values = toContentValue(social);
        return db.insert(SOCIALS_TABLE, null, values);
    }
    public long insert(CompanyCampaign campaign) throws SQLiteException,NullPointerException{
        if(campaign == null)
            throw new NullPointerException("missing account");
        ContentValues values = toContentValue(campaign);
        return db.insert(CAMPAIGNS_TABLE, null, values);
    }
    public long insert(ShoppingItem item) throws SQLiteException,NullPointerException{
        if(item == null)
            throw new NullPointerException("missing account");
        ContentValues values = toContentValue(item);
        return db.insert(SHOPPING_ITEMS_TABLE, null, values);
    }

    private ContentValues toContentValue(CompanyCampaign campaign) throws NullPointerException{
        if(campaign == null)
            throw new NullPointerException("missing account");
        ContentValues values = new ContentValues();
        values.put(LOGO, getBytes(campaign.getLogo()));
        values.put(NAME, campaign.getName());
        values.put(CREDITS, campaign.getCredits());
        return values;
    }
    private ContentValues toContentValue(ShoppingItem item) throws NullPointerException{
        if(item == null)
            throw new NullPointerException("missing account");
        ContentValues values = new ContentValues();
        values.put(LOGO, getBytes(item.getLogo()));
        values.put(NAME, item.getName());
        values.put(CREDITS, item.getCredits());
        return values;
    }
    private ContentValues toContentValue(Social social) throws NullPointerException{
        if(social == null)
            throw new NullPointerException("missing account");
        ContentValues values = new ContentValues();
        values.put(LOGO, getBytes(social.getLogo()));
        values.put(NAME, social.getName());
        return values;
    }

/*
    public void update(Account account) throws SQLiteException,NullPointerException{
        if(account == null)
            throw new NullPointerException("missing account");
        ContentValues updatedValues = toContentValue(account);	//inserisco i dati dell'account in contentValues
        String where_clause = ACCOUNTS_KEY_ID + "=" + account.getId();	//cerco l'id dell'account
        db.update(ACCOUNTS_TABLE, updatedValues, where_clause, null);	//lo sostituisco
    }
*/

    public void remove(long idx, String table) throws SQLiteException{
        if(db.delete(table, ID + "=" + idx, null) <= 0)
            throw new SQLiteException();
    }

    private Cursor getAllEntries(String table) throws SQLiteException{
        return db.query(table, null, null, null, null, null, null);
    }

    public List<Social> getSocials() throws SQLiteException{
        Cursor cursor = this.getAllEntries(SOCIALS_TABLE);		//prendo tutte le righe
        ArrayList<Social> list = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {		//finche' non arrivo alla fine
            String name = cursor.getString(cursor.getColumnIndex(DBManager.NAME));
            Bitmap logo = getImage(cursor.getBlob(cursor.getColumnIndex(DBManager.LOGO)));

            Social newSocial = new Social(logo,name);	//creo l' account

            newSocial.setId(cursor.getLong(cursor.getColumnIndex(DBManager.ID)));	//gli setto l'id
            list.add(0,newSocial);		//lo aggiungo alla lista
            cursor.moveToNext();		//passo alla prossima riga
        }
        cursor.close();
        return list;
    }
    public List<CompanyCampaign> getCampaigns() throws SQLiteException{
        Cursor cursor = this.getAllEntries(CAMPAIGNS_TABLE);		//prendo tutte le righe
        ArrayList<CompanyCampaign> list = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {		//finche' non arrivo alla fine
            String name = cursor.getString(cursor.getColumnIndex(DBManager.NAME));
            Bitmap logo = getImage(cursor.getBlob(cursor.getColumnIndex(DBManager.LOGO)));
            int credits = cursor.getInt(cursor.getColumnIndex(DBManager.CREDITS));

            CompanyCampaign newCampaign = new CompanyCampaign(logo,name,credits);	//creo l' account

            newCampaign.setId(cursor.getLong(cursor.getColumnIndex(DBManager.ID)));	//gli setto l'id
            list.add(0,newCampaign);		//lo aggiungo alla lista
            cursor.moveToNext();		//passo alla prossima riga
        }
        cursor.close();
        return list;
    }
    public List<ShoppingItem> getShoppingItems() throws SQLiteException{
        Cursor cursor = this.getAllEntries(SHOPPING_ITEMS_TABLE);		//prendo tutte le righe
        ArrayList<ShoppingItem> list = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {		//finche' non arrivo alla fine
            String name = cursor.getString(cursor.getColumnIndex(DBManager.NAME));
            Bitmap logo = getImage(cursor.getBlob(cursor.getColumnIndex(DBManager.LOGO)));
            int credits = cursor.getInt(cursor.getColumnIndex(DBManager.CREDITS));

            ShoppingItem newItem = new ShoppingItem(logo,name,credits);	//creo l' account

            newItem.setId(cursor.getLong(cursor.getColumnIndex(DBManager.ID)));	//gli setto l'id
            list.add(0,newItem);		//lo aggiungo alla lista
            cursor.moveToNext();		//passo alla prossima riga
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
                + ID + " integer primary key autoincrement, "
                + NAME + " text not null, "
                + LOGO + " blob not null, "
                + CREDITS + " text not null);";
        private static final String SQL_CREATE_TABLE_SHOPPING_ITEMS = "create table "
                + SHOPPING_ITEMS_TABLE + " (" //
                + ID + " integer primary key autoincrement, "
                + NAME + " text not null, "
                + LOGO + " blob not null, "
                + CREDITS + " text not null);";
        private static final String SQL_CREATE_TABLE_SOCIALS = "create table "
                + SOCIALS_TABLE + " (" //
                + ID + " integer primary key autoincrement, "
                + NAME + " text not null, "
                + LOGO + " blob not null);";

        public void onCreate(SQLiteDatabase db) throws SQLiteException {

            db.execSQL(SQL_CREATE_TABLE_CAMPAIGNS);
            db.execSQL(SQL_CREATE_TABLE_SOCIALS);
            db.execSQL(SQL_CREATE_TABLE_SHOPPING_ITEMS);

        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) throws SQLiteException{
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