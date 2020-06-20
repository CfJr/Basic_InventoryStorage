package com.example.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {

    public DatabaseHandler(@Nullable Context context) {
        super(context, "Database1", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createInv = "create table Inventory(itemName TEXT)";
        db.execSQL(createInv);

        String createStorage = "create table Storage(itemName TEXT)";
        db.execSQL(createStorage);

        ContentValues contentValues = new ContentValues();

        for(int i = 1; i < 17; i++) {
            contentValues.put("itemName", "Item_" + Integer.toString(i));
            db.insert("Storage", null, contentValues);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addStorage(String item){

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("itemName", item);

        database.insert("Storage", null, contentValues);

    }

    public void addInventory(String item){

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("itemName", item);

        database.insert("Inventory", null, contentValues);

    }

}