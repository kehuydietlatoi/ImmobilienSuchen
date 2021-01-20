package com.example.immobiliensuchen;


import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.os.Environment;

import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity"; //tag for controll
    private static final int WRITE_PERMISSION_REQUEST_CODE = 100; //random number for request code
    private static final int READ_PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Immobilien Suche"); //set title for toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if(checkWritePermission()) {
            if(checkReadPermission())
            checkFile(); //create "ImmobilienSuche" Directory in External Storage and create "Angebot.txt" with dummies Angebote
            else
                requestReadPermission();
        }
        else {
            requestWritePermission();
        }

        ImageView img = findViewById(R.id.imageView1);
        img.setImageResource(R.drawable.home1);

        Button kundenB = findViewById(R.id.KundenButton);
        Button maklerB = findViewById(R.id.maklerButton);

        kundenB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToKundenActivity();

            }
        });
        maklerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToMaklerActivity();
            }
        });

    }

    private void moveToMaklerActivity() {
        Intent intent = new Intent(MainActivity.this, MaklerActivity.class);
        startActivity(intent);
    }

    private void moveToKundenActivity() {
        Intent intent = new Intent(MainActivity.this, KundenActivity.class);
        startActivity(intent);
    }

    private boolean checkWritePermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return true;
        } else{
            return false;
        }
    }
    private boolean checkReadPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return true;
        } else{
            return false;
        }
    }

    private void requestWritePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to create files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST_CODE);
        }
    }

    private void requestReadPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to create files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_REQUEST_CODE);
        }
    }

    private void checkFile(){
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ImmobilienSuche");

        if (!dir.exists()) {
            if (dir.mkdir())
                Toast.makeText(MainActivity.this, "File Created", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(MainActivity.this, "File not Created", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "File Exist", Toast.LENGTH_LONG).show();
        }

        //store images as bitmap
        File imageDir = new File(dir, "images");
        if (!imageDir.exists()){
            imageDir.mkdir();
            Bitmap defaultImage = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.tiny_house);
            storeImage(defaultImage,"default_image", imageDir); //tiny house = default
            Bitmap defaultImage2 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.house_example);
            storeImage(defaultImage2,"house_image", imageDir); //add another
        }

        File myFile = new File(dir, "Angebote.txt");

        if (!myFile.exists()) {       //dummy Angebote
            ArrayList<Angebot> angebotContainer = new ArrayList<Angebot>();
            ArrayList<String> images = new ArrayList<>();

            images.add("default_image");
            images.add("default_image");
            images.add("default_image");

            Angebot a1 = new Angebot(1, "M", "Darmstadt", 370, "ein Zimmer in ein WG ",
                    "Musterhaus1@gmail.com", "Beschreibung von Haus 1",0, images);
            Angebot a2 = new Angebot(2, "K", "Darmstadt", 350000, "Schönes Haus im Stadtmitte",
                    "Musterhaus2@gmail.com", "Beschreibung von Haus 2",0,images);
            Angebot a3 = new Angebot(3, "K", "Darmstadt", 250000, "Haus im Stadtmitte",
                    "Musterhaus3@gmail.com", "Beschreibung von Haus 3",0, images);
            Angebot a4 = new Angebot(4, "K", "Darmstadt", 1000000, "Größe Haus",
                    "Musterhaus4@gmail.com", "Beschreibung von Haus 4",0, images);
            Angebot a5 = new Angebot(5, "M", "Darmstadt", 300, "WG-Zimmer",
                    "Musterhaus5@gmail.com", "Beschreibung von Haus 5", 0, images);
            Angebot a6 = new Angebot(6, "M", "Darmstadt", 1000, "3 Zimmer Wohnung",
                    "Musterhaus6@gmail.com", "Beschreibung von Haus 6", 0,images);
            angebotContainer.add(a1);
            angebotContainer.add(a2);
            angebotContainer.add(a3);
            angebotContainer.add(a4);
            angebotContainer.add(a5);
            angebotContainer.add(a6);
            try {
                FileOutputStream fos = new FileOutputStream(myFile);

                OutputStreamWriter myOutWriter = new OutputStreamWriter(fos);

                JSONArray jsonArrayImages = new JSONArray();
                JSONArray jsonArrayNachrichten = new JSONArray();
                JSONArray jsonarray = new JSONArray();

                for (int i = 0; i < images.size(); i++) {
                    jsonArrayImages.put(images.get(i));
                }

                for (int i = 0; i < angebotContainer.size(); i++) {
                    JSONObject object = new JSONObject();
                    Angebot a = angebotContainer.get(i);
                    object.put("BeitragsID", a.getBeitragID());
                    object.put("Titel", a.getTitel());
                    object.put("Art", a.getArt());
                    object.put("Preis", a.getPreis());
                    object.put("Stadt", a.getStadt());
                    object.put("Email", a.getEmail());
                    object.put("Beschreibung", a.getBeschreibung());
                    object.put("Favorit", a.getFavorit());
                    object.put("Images", jsonArrayImages);
                    object.put("Nachrichten", jsonArrayNachrichten);
                    jsonarray.put(object);
                }
                myOutWriter.write(jsonarray.toString());
                myOutWriter.close();
                fos.close();
                Toast.makeText(this,"File not found\n" + angebotContainer.size() + " Dummy Angebote werden gespeichert!",
                        Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    @Override //sponsored by google
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case WRITE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Permission GRANTED", Toast.LENGTH_SHORT ).show();
                    if (checkReadPermission())
                    checkFile();
                    else
                        requestReadPermission();
                } else {
                    Toast.makeText(this,"Permission DENIED", Toast.LENGTH_SHORT).show();
                }
                break;
            case READ_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Permission GRANTED", Toast.LENGTH_SHORT ).show();
                    checkFile();
                } else {
                    Toast.makeText(this,"Permission DENIED", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void storeImage(Bitmap image, String filename, File imageDir) {
        File pictureFile = new File(imageDir + File.separator + filename); // change \\ to / with separator
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos); //Write a compressed version of the bitmap to the specified outputstream.
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

}