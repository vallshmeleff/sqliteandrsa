# sqliteandrsa
The prototype of the SQLite database for Android with support for RSA encryption of records. The application can create a database, add, browse, view and edit records in the database and export/import the database..
The application can generate, save, restore, export and import encryption keys.
The application will encrypt 9 out of 10 fields in each entry. The unencrypted field will be used to search for records.
For RSA encryption, 2 methods have been added for encrypting / decoding large texts.
The application can do scrolling Activity.
This project is a good template for a real database for trade, clients, partners. You just need to enable and configure the desired functions.
Java codes are well commented.
No external libraries are used.
Very strong RSA encryption is used. You can choose more reliable encryption key sizes in the program. Additionally, you can use obfuscation. There is a corresponding method for this.  Owerview - https://docs.google.com/document/d/1VzJr0YVOun0mtGTyCn4uQwsNllINjqg_mc2qq_5FvVM/edit?usp=sharing


(16.11.2022) Methods for encrypting large texts have been tested on a prototype. Now I'm adding to this project.
(18.11.2022) Add GUIDE.pdf - pdf guide
(23.11.2022) - Debugging
(24.11.2022) - GUI Updated
(27.11.2022) - Zero text to ecrypt error detected
(28.11.2022) - Fixed bug with text processing before encryption
(28.11.2022) - Let's move database operations to a separate thread. We will do the same with encryption/decryption. For this, I made a separate application project. The project will not be added to the repository.
(30.11.2022) - Work with streams is done in a separate project. If someone needs it, you can add. For now, let's add a few more fields to the database records. To increase the universal properties of the application. And then we'll add image display for records in the SQLite database.
(01.12.2022) - 4 more fields have been added to the database (now a total of 8 fields). The next part of the project is adding a function for working with pictures in JPG format
(04.12.2022) - Fixed bug
