package com.example.immobiliensuchen;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.content.Context;
import android.os.Environment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.graphics.Bitmap;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import android.net.Uri;

import android.content.Intent;
import java.io.IOException;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;


public class NeuAngebotActivity extends AppCompatActivity {

    private static final String pathToPicture = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ImmobilienSuche/images";
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = "NeuAngebotActivity";
    private static final String FILE_NAME = "/ImmobilienSuche/Angebote.txt";
    private String titel, beschreibung, stadt, email, art;
    private double preis;
    private int beitragID, favorit;
    private ArrayList<String> images;
    private ArrayList<Bitmap> imagesBitmap;
    private RadioButton verkaufen, vermieten;
    private ImageView addPhoto;
    private LayoutInflater layoutInflater;
    private LinearLayout gallery;
    private Bitmap selectedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neu_angebot);
        Toolbar toolbar = findViewById(R.id.Angebote);
        setSupportActionBar(toolbar);
        setTitle("Neues Angebot erstellen");

        addPhoto = findViewById(R.id.addphoto);
        gallery = findViewById(R.id.gallery);
        layoutInflater = LayoutInflater.from(this);

        //Reading Data from Main Activity
        beitragID = getIntent().getIntExtra("neuBeitragsID", -1) + 1;

        if (beitragID == 0) { //error beim reading data
            setResult(3);
            finish();
        }

        Button abschickenButton = findViewById(R.id.buttonAbschicken2);
        Button abbrechenButton = findViewById(R.id.buttonCancel2);

        images = new ArrayList<>();
        imagesBitmap = new ArrayList<>();

        abschickenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verkaufen = (RadioButton) findViewById(R.id.verkaufenButton2);
                vermieten = (RadioButton) findViewById(R.id.vermietenButton2);
                if (verkaufen.isChecked()) {
                    art = "K";
                } else {
                    art = "M";
                }
                EditText stadtEditText = (EditText) findViewById(R.id.stadtTextEdit2);
                EditText titelEditText = (EditText) findViewById(R.id.titelTextEdit2);
                EditText preisEditText = (EditText) findViewById(R.id.preisTextEdit2);
                EditText beschreibungEditText = (EditText) findViewById(R.id.beschreibungTextEdit2);
                EditText emailEditText = (EditText) findViewById(R.id.emailTextEdit2);
                String s = preisEditText.getText().toString();
                preis = Double.parseDouble(s);
                stadt = stadtEditText.getText().toString();
                titel = titelEditText.getText().toString();
                beschreibung = beschreibungEditText.getText().toString();
                email = emailEditText.getText().toString();
                favorit = 0;
                if (images.size() == 0) {
                    images.add("default_image");
                    images.add("default_image");
                    images.add("house_image");
                    images.add("house_image");
                    images.add("default_image");
                }

                Angebot neuesAngebot = new Angebot(beitragID, art, stadt, preis, titel, email, beschreibung, favorit, images);

                //Sending result and Extra back to Main Activity
                Intent intentWithResult = new Intent();
                intentWithResult.putExtra("neuesAngebot", neuesAngebot);
                setResult(1, intentWithResult);
                finish();
            }
        });
        abbrechenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Sending result back to Main Activity
                setResult(2);
                finish();
            }
        });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(NeuAngebotActivity.this);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        File fileImage = new File (pathToPicture);
        for (int i = 0; i < imagesBitmap.size(); i++){
            storeImage(imagesBitmap.get(i),images.get(i),fileImage);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        selectedImage = (Bitmap) data.getExtras().get("data");

                        View view = layoutInflater.inflate(R.layout.item, gallery, false);

                        ImageView imageView = view.findViewById(R.id.imageView3);
                        imageView.setImageBitmap(selectedImage);
                        gallery.addView(view);

                        images.add(beitragID + "_" + imagesBitmap.size());
                        imagesBitmap.add(selectedImage);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);

                                View view = layoutInflater.inflate(R.layout.item, gallery, false);
                                ImageView imageView = view.findViewById(R.id.imageView3);
                                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                gallery.addView(view);
                                cursor.close();

                                images.add(beitragID + "_" + imagesBitmap.size());
                                imagesBitmap.add(BitmapFactory.decodeFile(picturePath));
                            }
                        }

                    }
                    break;
            }
        }
    }

    private void selectImage(Context context) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Delete last photo", "Cancel"};
        //Set a list of items to be displayed in the dialog as the content, you will be notified of the selected item via the supplied listener.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Picture..");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    if(checkCameraPermission()){
                        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, 0);
                    }
                   else requestCameraPermission();

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);

                } else if (options[item].equals("Delete last photo")) {
                    dialog.dismiss();
                    if(images.size() > 0){
                        images.remove(images.size()-1);
                        imagesBitmap.remove(imagesBitmap.size()-1);
                        gallery.removeViewAt(images.size()+1);//w cause there are addimage
                    }
                    else {
                        Toast t;
                        t = Toast.makeText(NeuAngebotActivity.this.getApplicationContext(),
                                "No photo added", Toast.LENGTH_SHORT);
                        t.show();
                    }
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void storeImage(Bitmap image, String filename, File imageDir) {
        File pictureFile = new File(imageDir + File.separator + filename);
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(NeuAngebotActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            return true;
        } else{
            return false;
        }
    }

    private void requestCameraPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(NeuAngebotActivity.this, Manifest.permission.CAMERA)) {
            Toast.makeText(NeuAngebotActivity.this, "Write External Storage permission allows us to create files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(NeuAngebotActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Permission GRANTED", Toast.LENGTH_SHORT ).show();
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                } else {
                    Toast.makeText(this,"Permission DENIED", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
