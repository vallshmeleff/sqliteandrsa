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
// Ready-made technical project of a Java application for working with the SQLite database and
// RSA encryption of some record fields. A good template for easy and fast development of your commercial project.
//
// SQLite database with fields: user, email, phone, notes. Notes field is RSA encrypted on write. LARGE text support
// Source:
// https://github.com/vallshmeleff/androidrsa - RSA encryptin repository
// https://github.com/vallshmeleff/sqlite - SQLite repository
//
// GUIDE https://github.com/vallshmeleff/sqliteandrsa/blob/main/GUIDE.pdf
//
// RSA NOTE field encrytion
//
// 16.11.2022 Add LARGE Text Encryption Methods
// 24.11.2022 Debugging and GUI refinement
// 01.12.2022 Encrypts large texts, exports/imports database and keys. There are only 4 fields in the database record
// Let's add the number of record fields to 8 and, then - add the upload of JPG files
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
    EditText evPhoto;
    EditText evCity;
    EditText evStreet;
    EditText evOffice;
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
    public static byte[] fragmentBytes = null; //RSA
    public static byte[] decodefragmentBytes = null; //RSA
    public File file = new File("key.pub"); // File for keys save

    public String note = ""; // onClick - evNote.getText().toString(); // Notes
    public Key[] KeyPMass = new Key[2]; //An array of two keys to return values from a method
    public Key[] KeyMass = new Key[2]; //An array of two keys to return values from a method
    // LARGE Text to Code
    public static String gtestText = "WiKi: Slovak Republic (Slovenská republika), is a landlocked country in Central Europe. It is bordered by Poland to the north, Ukraine to the east, Hungary to the south, Austria to the southwest, and the Czech Republic to the northwest. Slovakia's mostly mountainous territory spans about 49,000 square kilometres (19,000 sq mi), with a population of over 5.4 million. The capital and largest city is Bratislava, while the second largest city is Košice.";
    public String[] eFragment = new String[100]; // The array contains a large text to encrypt, divided into chunks
    public String[] eeFragment = new String[100]; // The array contains a encrypted large text, divided into chunks

    long rowID;
    int ie=0; // Database record pointer
    int it=0; // Update ID
    public int ecounter; // How many records are in the database

    DBHelper dbHelper;

    private static final int STORAGE_PERMISSION_CODE = 101;
    // Storage Permissions - Для API 23+ необходимо запросить разрешения на чтение / запись, даже если они уже есть в вашем манифесте
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
            publicKeyBytes = publicKey.getEncoded();  //Записать в массив байт publicKey, закодированный в X.509
            privateKeyBytes = privateKey.getEncoded();  //Записать в массив байт privateKey, закодированный в PKCS#8

            str = Base64.encodeToString(publicKeyBytes, Base64.DEFAULT); //Convert Byte Array (Public Key) to String
            rsagente.Save("key.pub", str, Maincontext);  //Write Public Key to file key.txt  from   str
            str = Base64.encodeToString(privateKeyBytes, Base64.DEFAULT); //Convert Byte Array (Private Key) to String
            rsagente.Save("pkey.pri", str, Maincontext);  //Write Private Key to file pkey.txt  from   str
        } // If key.pub already exist - do not generate new keys

        // ============================================================

        buttonAdd = (Button) findViewById(R.id.btnAdd);
        buttonAdd.setOnClickListener((View.OnClickListener) this);

        buttonRead = (Button) findViewById(R.id.btnRead); // Set Record 0
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
                evPhoto = (EditText) findViewById(R.id.etPhoto); // Here is a link to the graphic file
                evCity = (EditText) findViewById(R.id.etCity);
                evStreet = (EditText) findViewById(R.id.etStreet);
                evOffice = (EditText) findViewById(R.id.etOffice);
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


        //======== Encode and Decode Large Text - USE Two Methods =====
        //==String CzechText = "WiKi: The Czech Republic, also known as Czechia, is a landlocked country in Central Europe. Historically known as Bohemia, it is bordered by Austria to the south, Germany to the west, Poland to the northeast, and Slovakia to the southeast. The Czech Republic has a hilly landscape that covers an area of 78,871 square kilometers (30,452 sq mi) with a mostly temperate continental and oceanic climate. The capital and largest city is Prague; other major cities and urban areas include Brno, Ostrava, Plzeň and Liberec.";
        //==String CzechCodeText = LargeTextCode(CzechText);
        //==Log.d("LARGE Method", "== == DECODED Large Czech 2 == ==" + CzechCodeText);
        //==String CzechDEText = LargeTextDECode(CzechCodeText);
        //==Log.d("LARGE Method", "== == DECODED Large Czech 3 == ==" + CzechDEText);
        //======== Encode and Decode Large Text - USE Two Methods =====

        TotalRecords(); // Display Records Counter
        // If NO Records Disable Buttons |Save REC|, |Next|, |Prev|
        if (ecounter == 0) {
            buttonUpdate.setEnabled(false); // Save Edited Record
            buttonPrev.setEnabled(false);
            buttonNext.setEnabled(false);
            buttonRead.setEnabled(false); // Set Record 0
            buttonClear.setEnabled(false); // Delete DataBase
            buttonExport.setEnabled(false);
            buttonImport.setEnabled(false);
        }

    } //On Create

    @Override
    public void onClick(View v) {
        String name = evName.getText().toString();
        String email = evEmail.getText().toString();
        String phone = evPhone.getText().toString();
        note = evNote.getText().toString(); // Notes
        String photo = evPhoto.getText().toString(); // Image Link
        String city = evCity.getText().toString();
        String street = evStreet.getText().toString();
        String office = evOffice.getText().toString();

        SQLiteDatabase database = dbHelper.getWritableDatabase(); // Connecting to the Database
        ContentValues contentValues = new ContentValues(); // Create an object for the data



        switch (v.getId()) {

            case R.id.btnAdd: // Create REC
                evName.setText("");
                evEmail.setText("");
                evPhone.setText("");
                evPhoto.setText("");
                evCity.setText("");
                evStreet.setText("");
                evOffice.setText("");
                evNote.setText("");

                contentValues.put(DBHelper.KEY_NAME, name); // Adds a new row KEY_NAME to the table
                contentValues.put(DBHelper.KEY_MAIL, email); // Adds a new row KEY_MAIL to the table
                contentValues.put(DBHelper.KEY_PHONE, phone); // Adds a new row KEY_PHONE to the table
                contentValues.put(DBHelper.KEY_PHOTO, photo); // Adds a new row KEY_PHOTO to the table
                contentValues.put(DBHelper.KEY_CITY, city); // Adds a new row KEY_CITY to the table
                contentValues.put(DBHelper.KEY_STREET, street); // Adds a new row KEY_STREET to the table
                contentValues.put(DBHelper.KEY_OFFICE, office); // Adds a new row KEY_OFFICE to the table
                    // ============================================================
                    // Encode the original text with RSA private key
                    // ============================================================
                    // RSACode rsagente = new RSACode(); // Class instance RSACode
                    //encodedBytes = rsagente.RSATextEncode(publicKey, privateKey, note); //Encode text (note) via RSACode.java class
                    //note = Base64.encodeToString(encodedBytes, Base64.DEFAULT);
                    // ============================================================

                // => // NOTEncrypt(); // NOTE encryption method. Return note variable - SMALL TEXT SIZE
                note = LargeTextCode(evNote.getText().toString()); // Encryption LARGE Text and Create 1st Record

                // Обычный вариант contentValues.put(DBHelper.KEY_NOTE, note); // Adds a new row KEY_NOTE to the table
                contentValues.put(DBHelper.KEY_NOTE, note); // Adds a new encrypted row KEY_NOTE to the table

                Log.d("SQLite","== == == == == == ADD Button " + note);

                rowID = database.insert(DBHelper.TABLE_CONTACTS, null, contentValues); // Write to the database and get its ID
                Log.d("SQLite","== == Row inserted, ID = " + rowID);
                // Buttons is ON
                buttonUpdate.setEnabled(true); // Save Edited Record
                buttonPrev.setEnabled(true);
                buttonNext.setEnabled(true);
                buttonRead.setEnabled(true); // Set Record 0
                buttonClear.setEnabled(true); // Delete DataBase
                buttonExport.setEnabled(true);
                buttonImport.setEnabled(true);

                break;

            case R.id.btnUpdate: // Save REC
                    //== String lnote = evNote.getText().toString(); // Read from evNote EditText
                contentValues.put(DBHelper.KEY_NAME, evName.getText().toString()); // Adds a new row KEY_NAME to the table
                contentValues.put(DBHelper.KEY_MAIL, evEmail.getText().toString()); // Adds a new row KEY_MAIL to the table
                contentValues.put(DBHelper.KEY_PHONE, evPhone.getText().toString()); // Adds a new row KEY_PHONE to the table
                contentValues.put(DBHelper.KEY_PHOTO, evPhoto.getText().toString()); // Adds a new row KEY_PHOTO to the table
                contentValues.put(DBHelper.KEY_CITY, evCity.getText().toString()); // Adds a new row KEY_CITY to the table
                contentValues.put(DBHelper.KEY_STREET, evStreet.getText().toString()); // Adds a new row KEY_STREET to the table
                contentValues.put(DBHelper.KEY_OFFICE, evOffice.getText().toString()); // Adds a new row KEY_OFFICE to the table
                // => // NOTEncrypt(); // NOTE encryption method. Return note variable - SMALL TEXT SIZE

                Log.d("= Save REC =","== == == == == ==| UPDATE Button |== == == == " +  evNote.getText().toString());
                Log.d("SQLite",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + evNote.getText().toString());
                note = LargeTextCode(evNote.getText().toString()); // Read from evNote EditText and EnCrypt => For Test LARGE text
                Log.d("SQLite",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>==" + note);
                Log.d("= Save REC =","== == == == == ==| UPDATE Button |== == == == " +  note);
                contentValues.put(DBHelper.KEY_NOTE, note); // Adds a new row KEY_MAIL to the table
                it = ie; // it - Record ID
                database.update(DBHelper.TABLE_CONTACTS, contentValues, "_id=" + it, null); // Write to the database and get its ID
                Log.d("SQLite","== == == == == == SAVE REC Button " + rowID + " " + it);
                break;

            case R.id.btnRead: // Set Record 0
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
                int photoIndex = cursor.getColumnIndex(DBHelper.KEY_PHOTO);
                int cityIndex = cursor.getColumnIndex(DBHelper.KEY_CITY);
                int streetIndex = cursor.getColumnIndex(DBHelper.KEY_STREET);
                int officeIndex = cursor.getColumnIndex(DBHelper.KEY_OFFICE);
                int noteIndex = cursor.getColumnIndex(DBHelper.KEY_NOTE);

                ie =1;
                cursor.moveToFirst(); // Go to first record - Position 0
                Log.d("mLog", "== == SQLite == == " + " ie: " + String.valueOf(ie) + " ID: " + cursor.getInt(idIndex) +
                        ", Name = " + cursor.getString(nameIndex) +
                        ", E-mail = " + cursor.getString(emailIndex) +
                        ", Phone = " + cursor.getString(phoneIndex) +
                        ", Photo = " + cursor.getString(photoIndex) +
                        ", City = " + cursor.getString(cityIndex) +
                        ", Street = " + cursor.getString(streetIndex) +
                        ", Office = " + cursor.getString(officeIndex) +
                        ", Note = " + cursor.getString(noteIndex));
                evName.setText(cursor.getString(nameIndex));
                evEmail.setText(cursor.getString(emailIndex));
                evPhone.setText(cursor.getString(phoneIndex));
                evPhoto.setText(cursor.getString(photoIndex));
                evCity.setText(cursor.getString(cityIndex));
                evStreet.setText(cursor.getString(streetIndex));
                evOffice.setText(cursor.getString(officeIndex));
                str2 = cursor.getString(noteIndex); // Read NOTES field
                // => // NOTEDecrypt(); // NOTE Decryption method. Return note variable - SMALL TEXT SIZE
                note = LargeTextDECode(str2); // Decode LARGE Text
                evNote.setText(note);

                //////// evNote.setText(cursor.getString(noteIndex));

                cursor.close();
                break;

            case R.id.btnClear: // Delete all entries
                database.delete(DBHelper.TABLE_CONTACTS, null, null);
                break;

            case R.id.btnNext: // Next record
                Cursor cursorN = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
                //==ecounter = database.rawQuery("SELECT _ID FROM NTable", null).getCount();
                    ecounter = database.rawQuery("SELECT _ID FROM NTable", null).getCount();
                    Log.d("== == SQLite","Count rows = " + String.valueOf(ecounter));
                    TextView elCounter = (TextView)findViewById(R.id.Rowcount);
                    elCounter.setText("Total records: " + String.valueOf(ecounter) + " Row ID:  " + String.valueOf(ie));

                idIndex = cursorN.getColumnIndex(DBHelper.KEY_ID);
                nameIndex = cursorN.getColumnIndex(DBHelper.KEY_NAME);
                emailIndex = cursorN.getColumnIndex(DBHelper.KEY_MAIL);
                phoneIndex = cursorN.getColumnIndex(DBHelper.KEY_PHONE);
                photoIndex = cursorN.getColumnIndex(DBHelper.KEY_PHOTO);
                cityIndex = cursorN.getColumnIndex(DBHelper.KEY_CITY);
                streetIndex = cursorN.getColumnIndex(DBHelper.KEY_STREET);
                officeIndex = cursorN.getColumnIndex(DBHelper.KEY_OFFICE);
                noteIndex = cursorN.getColumnIndex(DBHelper.KEY_NOTE);

                if (ecounter > 0) { // If the number of records is NOT ZERO

                ie++;
                if (ie > ecounter-1) {
                    ie = 0;
                    }
                    cursorN.moveToPosition(ie); // Go to post if Reads Counter > 0
                    Log.d("SQLite","====== Record =======" + " ie: " + String.valueOf(ie));
                    ////cursorN.moveToPosition(ie); // Перейти к записи
                    Log.d("mLog", "== == SQLite == == " + " ie: " + String.valueOf(ie) + " ID: " + cursorN.getInt(idIndex) +
                            ", Name = " + cursorN.getString(nameIndex) +
                            ", E-mail = " + cursorN.getString(emailIndex) +
                            ", Phone = " + cursorN.getString(phoneIndex) +
                            ", Photo = " + cursorN.getString(photoIndex) +
                            ", City = " + cursorN.getString(cityIndex) +
                            ", Street = " + cursorN.getString(streetIndex) +
                            ", Office = " + cursorN.getString(officeIndex) +
                            ", Note = " + cursorN.getString(noteIndex));
                    evName.setText(cursorN.getString(nameIndex));
                    evEmail.setText(cursorN.getString(emailIndex));
                    evPhone.setText(cursorN.getString(phoneIndex));
                    evPhoto.setText(cursorN.getString(photoIndex));
                    evCity.setText(cursorN.getString(cityIndex));
                    evStreet.setText(cursorN.getString(streetIndex));
                    evOffice.setText(cursorN.getString(officeIndex));
                    str2 = cursorN.getString(noteIndex); // Read NOTES field
                    // => // NOTEDecrypt(); // NOTE Decryption method. Return note variable - SMALL TEXT SIZE
                    note = LargeTextDECode(str2); // Decode LARGE Text
                    evNote.setText(note);
                    // evNote.setText(cursorN.getString(noteIndex));

                } else {
                    ie = 0; // If NO Records in DataBase
                }


                cursorN.close();
                break;

            case R.id.btnPrev: // Previous record
                Cursor cursorP = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
                //==ecounter = database.rawQuery("SELECT _ID FROM NTable", null).getCount();
                    ecounter = database.rawQuery("SELECT _ID FROM NTable", null).getCount(); // Total Records
                    Log.d("== == SQLite == btnPrev ==","Count rows = " + String.valueOf(ecounter));
                    TextView enCounter = (TextView)findViewById(R.id.Rowcount);
                    enCounter.setText("Total records: " + String.valueOf(ecounter) + " Row ID:  " + String.valueOf(ie));

                idIndex = cursorP.getColumnIndex(DBHelper.KEY_ID);
                nameIndex = cursorP.getColumnIndex(DBHelper.KEY_NAME);
                emailIndex = cursorP.getColumnIndex(DBHelper.KEY_MAIL);
                phoneIndex = cursorP.getColumnIndex(DBHelper.KEY_PHONE);
                photoIndex = cursorP.getColumnIndex(DBHelper.KEY_PHOTO);
                cityIndex = cursorP.getColumnIndex(DBHelper.KEY_CITY);
                streetIndex = cursorP.getColumnIndex(DBHelper.KEY_STREET);
                officeIndex = cursorP.getColumnIndex(DBHelper.KEY_OFFICE);
                noteIndex = cursorP.getColumnIndex(DBHelper.KEY_NOTE);

                if (ecounter > 0) { // If the number of records is NOT ZERO
                ie--;
                if (ie <0 ) {
                    ie = ecounter-1;
                }
                cursorP.moveToPosition(ie); // Go to post

                Log.d("SQLite","====== Record 2 =======" + " ie: " + String.valueOf(ie));
                ////cursorP.moveToPosition(ie); // Перейти к записи
                Log.d("mLog", "== == SQLite == == " + " ie: " + String.valueOf(ie) + " ID: " + cursorP.getInt(idIndex) +
                        ", Name = " + cursorP.getString(nameIndex) +
                        ", E-mail = " + cursorP.getString(emailIndex) +
                        ", Phone = " + cursorP.getString(phoneIndex) +
                        ", Photo = " + cursorP.getString(photoIndex) +
                        ", City = " + cursorP.getString(cityIndex) +
                        ", Street = " + cursorP.getString(streetIndex) +
                        ", Office = " + cursorP.getString(officeIndex) +
                        ", Note = " + cursorP.getString(noteIndex));
                evName.setText(cursorP.getString(nameIndex));
                evEmail.setText(cursorP.getString(emailIndex));
                evPhone.setText(cursorP.getString(phoneIndex));
                    evPhoto.setText(cursorP.getString(photoIndex));
                    evCity.setText(cursorP.getString(cityIndex));
                    evStreet.setText(cursorP.getString(streetIndex));
                    evOffice.setText(cursorP.getString(officeIndex));
                str2 = cursorP.getString(noteIndex); // Read NOTES field
                // => //  NOTEDecrypt(); // NOTE Decryption method. Return note variable - SMALL TEXT SIZE
                note = LargeTextDECode(str2); // Decode LARGE Text
                evNote.setText(note);
                //evNote.setText(cursorP.getString(noteIndex));
                }

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

    //==========================================================
    //
    // LARGE TEXT
    // RSA Encryption Large Text
    // gtestText - Source Text
    // EncodeLargeText - return LARGE coded string
    //
    //==========================================================
    public String LargeTextCode(String gtestText) {
        RSACode rsagente = new RSACode(); // Class instance RSACode
        String EncodeLargeText = ""; // RSA Crypted Large Text
        if (gtestText.length() >= 50) { // If LARGE text lenght > 50 bytes - LONG Text
            String eFragment[] = new String[rsagente.eFragment(gtestText).length]; // Array eFragment[] RESIZE
            String eeFragment[] = new String[eFragment.length]; // eFragment.lenght = eeFragment.lenght
            eFragment = rsagente.eFragment(gtestText); // Fragmentation - eFragment[] Fragment Array
            int el = eFragment.length; // Fragment number
            int n = 0;
            while  (n < el) {
                if (n == 0) {
                    EncodeLargeText = Base64.encodeToString(rsagente.RSATextEncode(publicKey, privateKey, eFragment[n]), Base64.DEFAULT);
                }
                if (n > 0) {
                    EncodeLargeText = EncodeLargeText + "<oflameron>" + Base64.encodeToString(rsagente.RSATextEncode(publicKey, privateKey, eFragment[n]), Base64.DEFAULT);
                }
                n++;
            }
        } else { // If text lenght < 50 bytes - SHORT Text
            EncodeLargeText = Base64.encodeToString(rsagente.RSATextEncode(publicKey, privateKey, gtestText), Base64.DEFAULT);
        }
        return EncodeLargeText; //Return FULL Ecoded Large Text
    } // LargeTextCode


    //==========================================================
    //
    // LARGE TEXT
    // RSA DEcryption Large Text
    // gtestText - Source Text
    // RestoreText - return LARGE DEcoded string
    //
    //==========================================================
    public String LargeTextDECode(String gtestText) {
        RSACode rsagente = new RSACode(); // Class instance RSACode
        eeFragment = rsagente.eDEFragment(gtestText);
        int g = eeFragment.length;
        int h = 0;
        String RestoreText = ""; // Restored Large Text
        for(h = 0; h < g; h++) {
            //Log.d("==LARGE Text==", "== ==| Split LARGE Text |== == " + eeFragment[h]);
            fragmentBytes = Base64.decode(eeFragment[h], Base64.DEFAULT);;
            decodefragmentBytes  = rsagente.RSATextDecode(publicKey, privateKey,fragmentBytes); //Text decoding (publicKey = KeyMass[0], privateKey = KeyMass[1])
            RestoreText = RestoreText + new String(decodefragmentBytes);
        }
        Log.d("==LARGE Text==", "== ==| RESTORED Source Large Text |== == " +  RestoreText); // Full RESTORED Original Text
        return RestoreText; //Return FULL Ecoded Large Text
    } // LargeTextDECode

    //==========================================================
    // Records Counter
    //==========================================================
    public void TotalRecords() {
        // ============================================================
        // Records Counter
        // ============================================================
        SQLiteDatabase crdatabase = dbHelper.getWritableDatabase(); // Connecting to the Database
        crdatabase = dbHelper.getWritableDatabase(); // Connecting to the Database
        Cursor cursor = crdatabase.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
        ecounter = crdatabase.rawQuery("SELECT _ID FROM NTable", null).getCount();
        Log.d("== == SQLite ON Create == ==","Count rows = " + String.valueOf(ecounter));
        TextView evCounter = (TextView)findViewById(R.id.Rowcount);
        evCounter.setText("Total records: " + String.valueOf(ecounter) + " Row ID:  " + Objects.toString(rowID, null));
        dbHelper.close();
    }
    //==========================================================







}