package com.example.sqlitersa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExportImport extends AppCompatActivity  {

    // ===============================================
    // ExportImport.class - Export/Import SQLite database files to/from standard Download phone folders
    // Normal operation of reading/writing binary files from the application area to the Smartphone's Downloads folder and vice versa
    // Tested for API 29
    // Call as:
    //           ExportImport Export = new ExportImport(); // Class instance ExportImport.java
    //           Export.dbExport("oflameronDB"); // Export database file oflameronDB
    //           Export.dbImport("oflameronDB"); // Import database file oflameronDB
    //
    // ===============================================
    public static Context context;


    // Copying the oflameronDB file of the SQLite database from the application area to the smartphone's standard Download folder
    public void dbExport(String fdbname) {
        context = MainActivity.Maincontext;

        File file = new File(context.getApplicationInfo().dataDir.toString() + "/databases/" + fdbname);
        if(file.exists()) { // If DataBase file exist
            try (FileInputStream fin = new FileInputStream(context.getApplicationInfo().dataDir.toString() + "/databases/" + fdbname); // Application folder databases
                 FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/Download" + "/" + fdbname)) // Phone folder Download
            {
                byte[] buffer = new byte[fin.available()];
                // Reading the buffer
                fin.read(buffer, 0, buffer.length);
                // Write from buffer to file
                fos.write(buffer, 0, buffer.length);
                Log.e("== LOG ==", "== == ==  Exported == == ==");

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                Log.e("== LOG ==", "== == ==  Export ERROR == == ==");
            }
        }
    }


    // Copying the oflameronDB file of the SQLite database from the smartphone's standard Download folder to the application area
    public void dbImport(String fdbname) {
        context = MainActivity.Maincontext;

        File file = new File(Environment.getExternalStorageDirectory().toString() + "/Download" + "/" + fdbname);
        if(file.exists()) { // If DataBase file exist
            try (FileInputStream fin = new FileInputStream(Environment.getExternalStorageDirectory().toString() + "/Download" + "/" + fdbname); // From Download folder
                 FileOutputStream fos = new FileOutputStream(context.getApplicationInfo().dataDir.toString() + "/databases/" + fdbname)) // To the application/files folder
            {
                byte[] buffer = new byte[fin.available()];
                // Reading the buffer
                fin.read(buffer, 0, buffer.length);
                // Write from buffer to fileWrite from buffer to file
                fos.write(buffer, 0, buffer.length);
                Log.e("== LOG ==", "== == ==  Imported == == ==");

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                Log.e("== LOG ==", "== == ==  Import ERROR == == ==");
            } // (c) by Valery SHmelev  https://www.linkedin.com/in/valery-shmelev-479206227/
        }
    }



}