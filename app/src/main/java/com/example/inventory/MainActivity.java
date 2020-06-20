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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseHandler dbh;
    SQLiteDatabase sqLiteDatabase;
    private Button[][] buttons = new Button[3][3];
    private Button switchButton;
    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbh = new DatabaseHandler(this);
        sqLiteDatabase = dbh.getWritableDatabase();

        switchButton = (Button) findViewById(R.id.button_switch);

        String[] column = {"itemName"};
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.query("Inventory", column, null, null, null, null, null);

        String count = "SELECT count(*) FROM Inventory"; //Get number of items
        Cursor countCursor = sqLiteDatabase.rawQuery(count, null);
        countCursor.moveToFirst();
        int intCount = countCursor.getInt(0);

        for (int i = 0; i < 3; i++) { //Loop to add click listeners to buttons and add database data
            for (int j = 0; j < 3; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this);

                if(intCount != 0) { //If the inventory table is not empty
                    if (buttons[i][j].getText().toString().equals("")) { //If this button does not have a value
                        cursor.moveToNext();
                        intCount--;
                        buttons[i][j].setText(cursor.getString(0)); //Give button the database value
                    }
                    else{
                        buttons[i][j].setText(""); //If there is no item for the current slot
                    }
                }

            }
        }

        countCursor.close();

        switchButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                openStorage();
            }
        });

    }

    public void onClick(View v) {

        if(SystemClock.elapsedRealtime() - lastClickTime < 50){ //Stops instant double clicks
            return;
        }

        lastClickTime = SystemClock.elapsedRealtime();

        Button button = (Button) v;

        if(!(button.getText().toString().equals(""))){
            removeItem(button);
        }

    }

    public void openStorage(){
        Intent intent = new Intent(this, Storage.class);
        startActivity(intent);
    }

    public void removeItem(Button button){

        String[] column = {"itemName"};
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.query("Inventory", column, null, null, null, null, null);

        String item = button.getText().toString();

        System.out.println(item);

        String query = "DELETE FROM Inventory WHERE itemName = '" + item + "'";

        System.out.println(query);
        sqLiteDatabase.execSQL(query);

        //System.out.println("Removed!");

        button.setText("");

        ContentValues contentValues = new ContentValues();

        contentValues.put("itemName", item);
        sqLiteDatabase.insert("Storage", null, contentValues);

    }

}
