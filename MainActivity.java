package com.example.sqlbody;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;
//=======================================
//
// We use the application to work with the SQLite database and add RSA encryption
// An example of an application for reliable protection of personal data. 
// In the example, only one field in each record will be encrypted
//
// SQLite repository - https://github.com/vallshmeleff/sqlite
// RSA repository - https://github.com/vallshmeleff/androidrsa
//
// An example of using ready-made solutions in a new application
//
//=======================================

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{
    Button buttonAdd;
    Button buttonRead;
    Button buttonClear;
    Button buttonPrev;
    Button buttonNext;
    Button buttonUpdate;
    Button buttonExport;
    Button buttonImport;
    Button buttonExit;
    EditText evName;
    EditText evEmail;
    EditText evPhone;
    EditText evNote;
    public static Context Maincontext;

    long rowID;
    int ie=0; // First record pointer
    int it=0; // Update ID
    int ecounter; // How many records are in the database

    DBHelper dbHelper;

    private static final int STORAGE_PERMISSION_CODE = 101;
    // Storage Permissions - Для API 23+ необходимо запросить разрешения на чтение / запись, даже если они уже есть в вашем манифесте
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAdd = (Button) findViewById(R.id.btnAdd);
        buttonAdd.setOnClickListener((View.OnClickListener) this);

        buttonRead = (Button) findViewById(R.id.btnRead);
        buttonRead.setOnClickListener((View.OnClickListener) this);

        buttonClear = (Button) findViewById(R.id.btnClear);
        buttonClear.setOnClickListener((View.OnClickListener) this);

        buttonPrev = (Button) findViewById(R.id.btnPrev);
        buttonPrev.setOnClickListener((View.OnClickListener) this);

        buttonNext = (Button) findViewById(R.id.btnNext);
        buttonNext.setOnClickListener((View.OnClickListener) this);

        buttonUpdate = (Button) findViewById(R.id.btnUpdate);
        buttonUpdate.setOnClickListener((View.OnClickListener) this);

        buttonExport = (Button) findViewById(R.id.btnExport);
        buttonExport.setOnClickListener((View.OnClickListener) this);

        buttonImport = (Button) findViewById(R.id.btnImport);
        buttonImport.setOnClickListener((View.OnClickListener) this);

        buttonExit = (Button) findViewById(R.id.btnExit);
        buttonExit.setOnClickListener((View.OnClickListener) this);

        evName = (EditText) findViewById(R.id.etName);
        evEmail = (EditText) findViewById(R.id.etEmail);
        evPhone = (EditText) findViewById(R.id.etPhone);
        evNote = (EditText) findViewById(R.id.etNote);

        dbHelper = new DBHelper(this); // Create an object for creating and managing database versions

        // Storage PERMISSIONS
        Maincontext = getApplicationContext(); //To work with context
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE); // Запросить разрешения
        if (android.os.Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    2000);
        }


    }

    @Override
    public void onClick(View v) {
        String name = evName.getText().toString();
        String email = evEmail.getText().toString();
        String phone = evPhone.getText().toString();
        String note = evNote.getText().toString();

        SQLiteDatabase database = dbHelper.getWritableDatabase(); // Connecting to the Database
        ContentValues contentValues = new ContentValues(); // Create an object for the data

        switch (v.getId()) {

            case R.id.btnAdd:
                contentValues.put(DBHelper.KEY_NAME, name); // Adds a new row KEY_NAME to the table
                contentValues.put(DBHelper.KEY_MAIL, email); // Adds a new row KEY_MAIL to the table
                contentValues.put(DBHelper.KEY_PHONE, phone); // Adds a new row KEY_MAIL to the table
                contentValues.put(DBHelper.KEY_NOTE, note); // Adds a new row KEY_MAIL to the table
                Log.d("SQLite","== == == == == == ADD Button ");

                rowID = database.insert(DBHelper.TABLE_CONTACTS, null, contentValues); // Write to the database and get its ID
                Log.d("SQLite","== == Row inserted, ID = " + rowID);
                break;

            case R.id.btnUpdate:
                contentValues.put(DBHelper.KEY_NAME, name); // Adds a new row KEY_NAME to the table
                contentValues.put(DBHelper.KEY_MAIL, email); // Adds a new row KEY_MAIL to the table
                contentValues.put(DBHelper.KEY_PHONE, phone); // Adds a new row KEY_MAIL to the table
                contentValues.put(DBHelper.KEY_NOTE, note); // Adds a new row KEY_MAIL to the table
                Log.d("SQLite","== == == == == == UPDATE Button ");
                it = ie+1;
                database.update(DBHelper.TABLE_CONTACTS, contentValues, "_id=" + it, null); // Write to the database and get its ID
                Log.d("SQLite","== == == == == == UPDATE Button " + rowID + " " + it);
                break;

            case R.id.btnRead: // Set Recor 0
                // We make a request for all data from the TABLE_CONTACTS table, we get the cursor
                Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
                ecounter = database.rawQuery("SELECT _ID FROM NTable", null).getCount();
                Log.d("== == SQLite","Count rows = " + String.valueOf(ecounter));
                TextView evCounter = (TextView)findViewById(R.id.Rowcount);
                evCounter.setText("Total records: " + String.valueOf(ecounter) + " Row ID:  " + Objects.toString(rowID, null));

                int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
                int emailIndex = cursor.getColumnIndex(DBHelper.KEY_MAIL);
                int phoneIndex = cursor.getColumnIndex(DBHelper.KEY_PHONE);
                int noteIndex = cursor.getColumnIndex(DBHelper.KEY_NOTE);

                ie =0;
                cursor.moveToPosition(ie); // Go to first record Record 0
                Log.d("mLog", "== == SQLite == == " + " ie: " + String.valueOf(ie) + " ID: " + cursor.getInt(idIndex) +
                        ", Name = " + cursor.getString(nameIndex) +
                        ", E-mail = " + cursor.getString(emailIndex) +
                        ", Phone = " + cursor.getString(phoneIndex) +
                        ", Note = " + cursor.getString(noteIndex));
                evName.setText(cursor.getString(nameIndex));
                evEmail.setText(cursor.getString(emailIndex));
                evPhone.setText(cursor.getString(phoneIndex));
                evNote.setText(cursor.getString(noteIndex));

                cursor.close();
                break;

            case R.id.btnClear: // Delete all entries
                database.delete(DBHelper.TABLE_CONTACTS, null, null);
                break;

            case R.id.btnNext: // Next record
                Cursor cursorN = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
                ecounter = database.rawQuery("SELECT _ID FROM NTable", null).getCount();
                idIndex = cursorN.getColumnIndex(DBHelper.KEY_ID);
                nameIndex = cursorN.getColumnIndex(DBHelper.KEY_NAME);
                emailIndex = cursorN.getColumnIndex(DBHelper.KEY_MAIL);
                phoneIndex = cursorN.getColumnIndex(DBHelper.KEY_PHONE);
                noteIndex = cursorN.getColumnIndex(DBHelper.KEY_NOTE);

                ie++;
                if (ie > ecounter-1) {
                    ie = 0;
                }
                cursorN.moveToPosition(ie); // Go to post

                Log.d("SQLite","====== Record =======" + " ie: " + String.valueOf(ie));
                cursorN.moveToPosition(ie); // Перейти к записи
                Log.d("mLog", "== == SQLite == == " + " ie: " + String.valueOf(ie) + " ID: " + cursorN.getInt(idIndex) +
                        ", Name = " + cursorN.getString(nameIndex) +
                        ", E-mail = " + cursorN.getString(emailIndex) +
                        ", Phone = " + cursorN.getString(phoneIndex) +
                        ", Note = " + cursorN.getString(noteIndex));
                evName.setText(cursorN.getString(nameIndex));
                evEmail.setText(cursorN.getString(emailIndex));
                evPhone.setText(cursorN.getString(phoneIndex));
                evNote.setText(cursorN.getString(noteIndex));

                cursorN.close();
                break;

            case R.id.btnPrev: // Previous record
                Cursor cursorP = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
                ecounter = database.rawQuery("SELECT _ID FROM NTable", null).getCount();
                idIndex = cursorP.getColumnIndex(DBHelper.KEY_ID);
                nameIndex = cursorP.getColumnIndex(DBHelper.KEY_NAME);
                emailIndex = cursorP.getColumnIndex(DBHelper.KEY_MAIL);
                phoneIndex = cursorP.getColumnIndex(DBHelper.KEY_PHONE);
                noteIndex = cursorP.getColumnIndex(DBHelper.KEY_NOTE);

                ie--;
                if (ie <0 ) {
                    ie = ecounter-1;
                }
                cursorP.moveToPosition(ie); // Go to post

                Log.d("SQLite","====== Record 2 =======" + " ie: " + String.valueOf(ie));
                cursorP.moveToPosition(ie); // Перейти к записи
                Log.d("mLog", "== == SQLite == == " + " ie: " + String.valueOf(ie) + " ID: " + cursorP.getInt(idIndex) +
                        ", Name = " + cursorP.getString(nameIndex) +
                        ", E-mail = " + cursorP.getString(emailIndex) +
                        ", Phone = " + cursorP.getString(phoneIndex) +
                        ", Note = " + cursorP.getString(noteIndex));
                evName.setText(cursorP.getString(nameIndex));
                evEmail.setText(cursorP.getString(emailIndex));
                evPhone.setText(cursorP.getString(phoneIndex));
                evNote.setText(cursorP.getString(noteIndex));

                cursorP.close();
                break;

            case R.id.btnExport: // Export SQLite DataBase
                ExportImport Export = new ExportImport(); // Class instance ExportImport.java
                Export.dbExport("oflameronDB"); // Export database file oflameronDB
                break;

            case R.id.btnImport: // Import SQLite DataBase
                ExportImport Import = new ExportImport(); // Class instance ExportImport.java
                Import.dbImport("oflameronDB"); // Import database file oflameronDB
                break;

            case R.id.btnExit: // Application Exit
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                break;


        }

        dbHelper.close();
    }


    // Function to check and request permission
    public void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
            Log.e("== Exception ==", "== == Request PERMISSION == ==");
        }
        else {
            //Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
            Log.e("== Exception ==", "== == PERMISSION GRANTED == ==");

        }
    }




}