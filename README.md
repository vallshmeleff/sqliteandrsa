# sqliteandrsa
SQLite database prototype for Android with support for RSA-encrypted records. The application can create a database, check in, delete, delete and register most of the database, and export/import the database. The application can generate, heal, restore, export and import takeover keys. The database has 8 fields in each entry. One field is encrypted using RSA. For RSA encryption, method 2 has been added to encrypt/decode large texts. An application can scroll through an Activity. This project is a good template for an essential database on trade, women, partners. You just need to enable and configure the necessary features and create a look. The Java code has great comments. Supports RSA encryption with a long key of 1024 or 2048 bits. You can choose the size of the encryption key. Also, you can use obfuscation. To do this, there is a special method in which you simply perform your secret actions.  Owerview - https://docs.google.com/document/d/1VzJr0YVOun0mtGTyCn4uQwsNllINjqg_mc2qq_5FvVM/edit?usp=sharing

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
(05.12.2022) - Developing Java code for uploading images to a SQLite database
(07.12.2022) - Java code for loading images was written and separately debugged.
