package com.example.inventory;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Storage extends AppCompatActivity implements View.OnClickListener {

    DatabaseHandler dbh;
    SQLiteDatabase sqLiteDatabase;
    private Button[][] buttons = new Button[4][4];
    private Button switchButton;
    private int count = 0;
    private int invSize = 9;
    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        dbh = new DatabaseHandler(this);
        sqLiteDatabase = dbh.getWritableDatabase();

        switchButton = (Button) findViewById(R.id.button_switch);

        String[] column = {"itemName"};
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.query("Storage", column, null, null, null, null, null);

        String count = "SELECT count(*) FROM Storage";
        Cursor countCursor = sqLiteDatabase.rawQuery(count, null);
        countCursor.moveToFirst();
        int intCount = countCursor.getInt(0);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this);

                if(intCount != 0) {
                    if (buttons[i][j].getText().toString().equals("")) {
                        cursor.moveToNext();
                        intCount--;
                        buttons[i][j].setText(cursor.getString(0));
                    }
                }

            }
        }

        switchButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                openInventory();
            }
        });
    }

    public void onClick(View v) {

        if(SystemClock.elapsedRealtime() - lastClickTime < 50){
            return;
        }

        lastClickTime = SystemClock.elapsedRealtime();

        Button button = (Button) v;

        if(!(button.getText().toString().equals(""))){
            removeItem(button);
        }
    }

    public void openInventory(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void removeItem(Button button){

        String count = "SELECT count(*) FROM Inventory";

        Cursor countCursor = sqLiteDatabase.rawQuery(count, null);

        countCursor.moveToFirst();

        int intCount = countCursor.getInt(0);

        System.out.println("Storage: " + intCount);
        System.out.println("invCount: " + invSize);

        if(intCount >= invSize){
            return;
        }

        String[] column = {"itemName"};
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.query("Storage", column, null, null, null, null, null);

        String item = button.getText().toString();

        System.out.println(item);

        String query = "DELETE FROM Storage WHERE itemName = '" + item + "'";

        System.out.println(query);
        sqLiteDatabase.execSQL(query);

        button.setText("");

        ContentValues contentValues = new ContentValues();

        contentValues.put("itemName", item);
        sqLiteDatabase.insert("Inventory", null, contentValues);

    }
}
