package com.example.weatherforecast;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

class DisplayDatabaseActivity extends AppCompatActivity {

    private TextView databaseContentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        databaseContentTextView = findViewById(R.id.database_content_textview);

        // Open the SQLite database
        SQLiteDatabase db = openOrCreateDatabase("your_database_name.db", MODE_PRIVATE, null);

        // Query the database and retrieve the contents
        Cursor cursor = db.rawQuery("SELECT * FROM your_table_name", null);
        StringBuilder databaseContent = new StringBuilder();
        if (cursor.moveToFirst()) {
            do {
                // Assuming you have columns named "column1" and "column2" in your table
                String column1Data = Cursor.getString(Cursor.getColumnIndex("column1"));
                String column2Data = Cursor.getString(Cursor.getColumnIndex("column2"));
                // Append the retrieved data to the StringBuilder
                databaseContent.append("Column1: ").append(column1Data).append(", Column2: ").append(column2Data).append("\n");
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Display the database contents in the TextView
        databaseContentTextView.setText(databaseContent.toString());

        // Close the database
        db.close();
    }
}
