package com.example.rsalargetext;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

//=====================================================
//
// DEMO Class
//
// eFragment = eFragment(eText); - Divide large text in a string variable into parts of 100 bytes for RSA encryption,
// adding tags, removing tags and reassembling large text
// Adds a separator <oflameron> to the beginning of every block except the first
//
// splitted = eDEFragment(toDecode); - Separates the encrypted body by the <oflameron> separator and puts it in an array for decoding
//
// 09.11.2022
//
// Tested for API 29
//
//=====================================================
public class MainActivity extends AppCompatActivity {
    public String eText = "The Czech Republic, also known as Czechia, is a landlocked country in Central Europe. Historically known as Bohemia, it is bordered by Austria to the south, Germany to the west, Poland to the northeast, and Slovakia to the southeast. The Czech Republic has a hilly landscape that covers an area of 78,871 square kilometers (30,452 sq mi) with a mostly temperate continental and oceanic climate. The capital and largest city is Prague; other major cities and urban areas include Brno, Ostrava, Plzeň and Liberec.";
    public String toDecode = "The Czech Republic, also known as Czechia, <oflameron>is a landlocked country in Central Europe. Historically known as Bohemia, <oflameron>it is bordered by Austria to the south, Germany to the west, Poland to the northeast, <oflameron>and Slovakia to the southeast. The Czech Republic has a hilly landscape <oflameron>that covers an area of 78,871 square kilometers (30,452 sq mi) with a mostly temperate continental and oceanic climate. The capital <oflameron>and largest city is Prague; other major cities and <oflameron>urban areas include Brno, Ostrava, Plzeň and Liberec.";
    public int eL = eText.length(); // eText line length
    public String[] masString = new String[(int) (eL/100)+1]; // Array for string fragments - full 100 bytes each and for the rest
    public String[] eFragment; // The array contains a large text to encrypt, divided into chunks
    public String[] splitted; // The array contains the read encrypted blocks before compiling the entire write string
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eL = eText.length(); // eText line length

        eFragment = eFragment(eText); // Fragmentation
        Log.d("== eFragment==", "== == Fragmented == == " + eFragment [0]);
        Log.d("== eFragment==", "== == Fragmented == == " + eFragment [1]);
        Log.d("== eFragment==", "== == Fragmented == == " + eFragment [2]);

        splitted = eDEFragment(toDecode); // Defragmentation
        // Log.d("== Block==", "== == DEFragmented == == " + splitted [0]);
        // Log.d("== Block==", "== == DEFragmented == == " + splitted [1]);
        // Log.d("== Block==", "== == DEFragmented == == " + splitted [2]);
        // Log.d("== Block==", "== == splitted[] Size == == " + splitted.length); // Array Lenght


    } // onCreate

    public String[] eFragment(String eText) {
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
                if (e == 0) {
                    masString [e] = Text100; // Write to block array
                } else {
                    masString[e] = "<oflameron>" + Text100; // Write to block array
                }
                Log.d("== Block==", "== == eText Block == == " + masString [e]);
                e = e + 1; // Next Block Array Number
                y = y + 100;
            }
            if (i == 0) {
                Text100 =eText.substring(y, eL);
                masString [e] = "<oflameron>" + Text100; // Write to block array
                Log.d("== Block==", "== == eText Block == == " + masString [e]);
            }
            // Crypto RSA (c) by Valery Shmelev https://www.linkedin.com/in/valery-shmelev-479206227/
        }
        return masString; //Return Text array

    }




    public String[] eDEFragment(String toDecode) {
        // ============================================================
        // Defragmenting an eText string
        // ============================================================
        String[] splitted = toDecode.split("<oflameron>"); // Split encrypted string by delimiter and write to array
        return splitted; //Return Text array
    }



    }