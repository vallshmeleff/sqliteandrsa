package com.example.rsalargetext;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

//=====================================================
//
// Divide large text in a string variable into parts of 100 bytes for RSA encryption,
// adding tags, removing tags and reassembling large text
//
//=====================================================
public class MainActivity extends AppCompatActivity {
    public String eText = "The Czech Republic, also known as Czechia, is a landlocked country in Central Europe. Historically known as Bohemia, it is bordered by Austria to the south, Germany to the west, Poland to the northeast, and Slovakia to the southeast. The Czech Republic has a hilly landscape that covers an area of 78,871 square kilometers (30,452 sq mi) with a mostly temperate continental and oceanic climate. The capital and largest city is Prague; other major cities and urban areas include Brno, Ostrava, Plzeň and Liberec.";
    public String toDecode = "The Czech Republic, also known as Czechia, <oflameron>is a landlocked country in Central Europe. Historically known as Bohemia, <oflameron>it is bordered by Austria to the south, Germany to the west, Poland to the northeast, <oflameron>and Slovakia to the southeast. The Czech Republic has a hilly landscape <oflameron>that covers an area of 78,871 square kilometers (30,452 sq mi) with a mostly temperate continental and oceanic climate. The capital <oflameron>and largest city is Prague; other major cities and <oflameron>urban areas include Brno, Ostrava, Plzeň and Liberec.";
    public int eL = eText.length(); // eText line length
    public String[] masString = new String[(int) (eL/100)+1]; // Array for string fragments - full 100 bytes each and for the rest
    public String[] splitted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eL = eText.length(); // eText line length

        // eFragment();
        eDEFragment();


    } // onCreate

    public void eFragment() {
        // ============================================================
        // eText string fragmentation
        // ============================================================
        if (eText != null || eText.length() > 0) { // If eText string exists and length > zero
            String Text100 = "";
            int i = 0; // Number of blocks/fragments
            int y = 0; // The starting position of the block in the eText line
            int e = 0; // Fragment count in masString summit
            if (eL >= 100) {
                int x = eL / 100;
                i = (int) x; // Integer from division
            } else {
                i = 0;
            }


            while (i > 0) { // As long as there is text for at least one block
                Text100 = eText.substring(y, y + 100);
                i = i - 1;
                masString [e] = Text100; // Write to block array
                Log.d("== Block==", "== == eText Block == == " + masString [e]);
                e = e + 1; // Next Block Array Number
                y = y + 100;
            }
            if (i == 0) {
                Text100 =eText.substring(y, eL);
                masString [e] = Text100; // Write to block array
                Log.d("== Block==", "== == eText Block == == " + masString [e]);
            }
            // Crypto RSA (c) by Valery Shmelev https://www.linkedin.com/in/valery-shmelev-479206227/
        }
    }




    public void eDEFragment() {
        // ============================================================
        // Defragmenting an eText string
        // ============================================================
        splitted = toDecode.split("<oflameron>"); // Split encrypted string by delimiter and write to array

        Log.d("== Block==", "== == DEFragmented == == " + splitted [0]);
        Log.d("== Block==", "== == DEFragmented == == " + splitted [2]);
        Log.d("== Block==", "== == DEFragmented == == " + splitted [3]);
    }



    }