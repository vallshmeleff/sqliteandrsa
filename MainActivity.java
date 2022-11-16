package com.example.sqlitersa;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.security.Key;
import java.util.Objects;
//============================================================
//
// SQLite database with fields: user, email, phone, notes. Notes field is RSA encrypted on write.
// Source:
// https://github.com/vallshmeleff/androidrsa - RSA encryptin repository
// https://github.com/vallshmeleff/sqlite - SQLite repository
//
// RSA NOTE field encrytion
//
// Develop in progress ...
//
//============================================================
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

    public static Key publicKey = null; //RSA
    public static Key privateKey = null; //RSA
    public static String str=" "; //File contents oflameron.txt
    public static String str2=" "; //File contents oflameron.txt
    public static String str3=" "; //File contents key.txt - public key
    public static String str4=" "; //File contents pkey.txt - private key
    public static byte[] privateKeyBytes = null; //RSA
    public static byte[] publicKeyBytes = null; //RSA
    public static byte[] encodedBytes = null; //RSA
    public static byte[] decodedBytes = null; //RSA
    public File file = new File("key.pub"); // File for keys save

    public String note = ""; // onClick - evNote.getText().toString(); // Notes
    public Key[] KeyPMass = new Key[2]; //An array of two keys to return values from a method
    public Key[] KeyMass = new Key[2]; //An array of two keys to return values from a method
    // Text to Code
    public static String gtestText = "WiKi: Slovak Republic (Slovenská republika), is a landlocked country in Central Europe. It is bordered by Poland to the north, Ukraine to the east, Hungary to the south, Austria to the southwest, and the Czech Republic to the northwest. Slovakia's mostly mountainous territory spans about 49,000 square kilometres (19,000 sq mi), with a population of over 5.4 million. The capital and largest city is Bratislava, while the second largest city is Košice.";

    long rowID;
    int ie=0; // First record pointer
    int it=0; // Update ID
    int ecounter; // How many records are in the database

    DBHelper dbHelper;

    private static final int STORAGE_PERMISSION_CODE = 101;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Maincontext = getApplicationContext(); //To work with context

        // File key.pab already exist ?
        String[] files = fileList();
        for (String file : files) {
            if (file.equals("key.pub")) {
                //file exits
                //// Log.d("== Restored Keys ==","====== Keys Pub ======= ");
            }
        }



        if(new File(getApplicationContext().getFilesDir().toString() + "/key.pub").exists()) { // If key.pub already exist - do not generate new keys
                            // Read keys from file
            RSACode rsagente = new RSACode(); // Class instance RSACode
            str3 = rsagente.Load("key.pub", str3,  Maincontext); //Here we read and decode Public Key (RSACode class)
            str4 = rsagente.Load("pkey.pri", str4,  Maincontext); //Here we read and decode Private Key (RSACode class)
            KeyMass = rsagente.RSAKeyReGenerate(str3, str4); // We pass to the method str3 and str4 - String from the file. Get the recovered keys as an array
            publicKey = KeyMass[0];
            privateKey = KeyMass[1];

            Log.d("== Restored Keys ==","====== ****** Keys ======= " +  publicKey);
            Log.d("== Restored Keys ==","====== PUBLIC Key ======= " + getApplicationContext().getFilesDir().toString() + "/key.pub");


        } else { // If key.pub NO exist - generate new keys
            // ============================================================
            // Generate key pair for 1024-bit RSA encryption and decryption
            // ============================================================
            RSACode rsagente = new RSACode(); // Class instance RSACode
            // Key[] KeyPMass = new Key[2]; //An array of two keys to return values from a method
            KeyPMass = rsagente.RSAKeyGen(); //GENERATE Key Pair
            publicKey = KeyPMass[0];
            privateKey = KeyPMass[1];
            Log.d("== Generate Keys ==","====== Keys ======= " + getApplicationContext().getFilesDir().toString() + " " + publicKey.toString());
            //--------------------------------------------------------
            // The most important part of encryption/decoding is saving
            // and restoring the public and private keys. Otherwise, after
            // restarting the application, you will not be able to decrypt
            // the encoded text, because new keys will be generated.
            //
            // Save Keys -> to file
            //--------------------------------------------------------
            publicKeyBytes = publicKey.getEncoded(); 
            privateKeyBytes = privateKey.getEncoded(); 

            str = Base64.encodeToString(publicKeyBytes, Base64.DEFAULT); //Convert Byte Array (Public Key) to String
            rsagente.Save("key.pub", str, Maincontext);  //Write Public Key to file key.txt  from   str
            str = Base64.encodeToString(privateKeyBytes, Base64.DEFAULT); //Convert Byte Array (Private Key) to String
            rsagente.Save("pkey.pri", str, Maincontext);  //Write Private Key to file pkey.txt  from   str
        } // If key.pub already exist - do not generate new keys

        // ============================================================

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
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
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
        note = evNote.getText().toString(); // Notes

        SQLiteDatabase database = dbHelper.getWritableDatabase(); // Connecting to the Database
        ContentValues contentValues = new ContentValues(); // Create an object for the data

        switch (v.getId()) {

            case R.id.btnAdd:
                contentValues.put(DBHelper.KEY_NAME, name); // Adds a new row KEY_NAME to the table
                contentValues.put(DBHelper.KEY_MAIL, email); // Adds a new row KEY_MAIL to the table
                contentValues.put(DBHelper.KEY_PHONE, phone); // Adds a new row KEY_PHONE to the table
                    // ============================================================
                    // Encode the original text with RSA private key
                    // ============================================================
                    // RSACode rsagente = new RSACode(); // Class instance RSACode
                    //encodedBytes = rsagente.RSATextEncode(publicKey, privateKey, note); //Encode text (note) via RSACode.java class
                    //note = Base64.encodeToString(encodedBytes, Base64.DEFAULT);
                    // ============================================================

                NOTEncrypt(); // NOTE encryption method. Return note variable

                // Обычный вариант contentValues.put(DBHelper.KEY_NOTE, note); // Adds a new row KEY_MAIL to the table
                contentValues.put(DBHelper.KEY_NOTE, note); // Adds a new encrypted row KEY_NOTE to the table

                Log.d("SQLite","== == == == == == ADD Button " + note);

                rowID = database.insert(DBHelper.TABLE_CONTACTS, null, contentValues); // Write to the database and get its ID
                Log.d("SQLite","== == Row inserted, ID = " + rowID);
                break;

            case R.id.btnUpdate:
                contentValues.put(DBHelper.KEY_NAME, name); // Adds a new row KEY_NAME to the table
                contentValues.put(DBHelper.KEY_MAIL, email); // Adds a new row KEY_MAIL to the table
                contentValues.put(DBHelper.KEY_PHONE, phone); // Adds a new row KEY_MAIL to the table
                NOTEncrypt(); // NOTE encryption method. Return note variable
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

                ie =1;
                cursor.moveToFirst(); // Go to first record - Position 0
                Log.d("mLog", "== == SQLite == == " + " ie: " + String.valueOf(ie) + " ID: " + cursor.getInt(idIndex) +
                        ", Name = " + cursor.getString(nameIndex) +
                        ", E-mail = " + cursor.getString(emailIndex) +
                        ", Phone = " + cursor.getString(phoneIndex) +
                        ", Note = " + cursor.getString(noteIndex));
                evName.setText(cursor.getString(nameIndex));
                evEmail.setText(cursor.getString(emailIndex));
                evPhone.setText(cursor.getString(phoneIndex));
                str2 = cursor.getString(noteIndex); // Read NOTES field
                NOTEDecrypt(); // NOTE Decryption method. Return note variable
                evNote.setText(note);

                //////// evNote.setText(cursor.getString(noteIndex));

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
                cursorN.moveToPosition(ie); 
                Log.d("mLog", "== == SQLite == == " + " ie: " + String.valueOf(ie) + " ID: " + cursorN.getInt(idIndex) +
                        ", Name = " + cursorN.getString(nameIndex) +
                        ", E-mail = " + cursorN.getString(emailIndex) +
                        ", Phone = " + cursorN.getString(phoneIndex) +
                        ", Note = " + cursorN.getString(noteIndex));
                evName.setText(cursorN.getString(nameIndex));
                evEmail.setText(cursorN.getString(emailIndex));
                evPhone.setText(cursorN.getString(phoneIndex));
                NOTEDecrypt(); // NOTE Decryption method. Return note variable
                evNote.setText(note);
                // evNote.setText(cursorN.getString(noteIndex));

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
                cursorP.moveToPosition(ie); 
                Log.d("mLog", "== == SQLite == == " + " ie: " + String.valueOf(ie) + " ID: " + cursorP.getInt(idIndex) +
                        ", Name = " + cursorP.getString(nameIndex) +
                        ", E-mail = " + cursorP.getString(emailIndex) +
                        ", Phone = " + cursorP.getString(phoneIndex) +
                        ", Note = " + cursorP.getString(noteIndex));
                evName.setText(cursorP.getString(nameIndex));
                evEmail.setText(cursorP.getString(emailIndex));
                evPhone.setText(cursorP.getString(phoneIndex));
                NOTEDecrypt(); // NOTE Decryption method. Return note variable
                evNote.setText(note);
                //evNote.setText(cursorP.getString(noteIndex));

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

    //==========================================================
    // RSA text encryption
    //==========================================================
    public String NOTEncrypt() {
        // ============================================================
        // Encode NOTES text with RSA private key
        // ============================================================
        RSACode rsagente = new RSACode(); // Class instance RSACode
        encodedBytes = rsagente.RSATextEncode(publicKey, privateKey, note); //Encode text (note) via RSACode.java class
        note = Base64.encodeToString(encodedBytes, Base64.DEFAULT);
        Log.e("== Exception ==", "== == NOTE == == " + note);
        return note; //Return Text (Library Information)
    }
    //==========================================================

    //==========================================================
    // RSA text Decryption
    //==========================================================
    public String NOTEDecrypt() {
        // ============================================================
        // Decode NOTES text with RSA private key
        // ============================================================
        RSACode rsagente = new RSACode(); // Class instance RSACode
        encodedBytes = Base64.decode(str2, Base64.DEFAULT); //Convert String to Byte Array
        decodedBytes = rsagente.RSATextDecode(KeyMass[0], KeyMass[1], encodedBytes); //Text decoding (publicKey = KeyMass[0], privateKey = KeyMass[1])
        note = new String(decodedBytes);
        return note; //Return Text (Decode NOTES)
    } // Valery Shmelev https://www.linkedin.com/in/valery-shmelev-479206227/  Creative programming
    //==========================================================






}