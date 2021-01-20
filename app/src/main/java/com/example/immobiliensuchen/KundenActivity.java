package com.example.immobiliensuchen;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class KundenActivity extends AppCompatActivity {

    private static final String TAG = "KundenActivity";
    static final int REQUEST_CODE = 0;
    private final String className = this.getClass().getSimpleName();

    private ArrayList<Angebot> angebotContainer;
    private ArrayList<Angebot> searchedAngebotContainer;

    private String stadtName, searchedArt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kunden);
        Toolbar toolbar = findViewById(R.id.Angebote);
        setSupportActionBar(toolbar);
        setTitle("Kunden");

        Button suchenButton, favoritButton;
        suchenButton = findViewById(R.id.SuchenButton);
        suchenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readFile();
                EditText stadtEditText =  findViewById(R.id.StadtEditText);
                stadtName = stadtEditText.getText().toString();         // get Cityname
                if(stadtName.equals("")){
                    Toast t;
                    t = Toast.makeText(KundenActivity.this.getApplicationContext(),
                            "Stadt Name kann nicht leer sein. Bitte etwas eingeben ", Toast.LENGTH_SHORT);
                    t.show();
                }
                else{
                    onRadioButtonClicked();   // set the value of bool , control if searching for Miet oder Kauf Object
                    openBrowseActivitySearch();
                }

            }
        });

        favoritButton = findViewById(R.id.FavoritButton);
        favoritButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                readFile();
                openBrowseActivityFavorit();
            }
        });
    }

    public void readFile(){
        String titel, beschreibung, stadt, art, email;
        double preis;
        int beitragID, favorit;
        String alleausgaben = "";
        angebotContainer = new ArrayList<>();
        ArrayList<String> images, nachrichten;

        try{
            File myFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/ImmobilienSuche/Angebote.txt");

            FileInputStream fis = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader( new InputStreamReader(fis, StandardCharsets.UTF_8.name()));

            String line;
            while((line = myReader.readLine())!= null ){
                alleausgaben += line;
            }

            JSONArray jsonArray = new JSONArray(alleausgaben);

            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                beitragID = Integer.parseInt(jsonObject.getString("BeitragsID"));
                titel = jsonObject.getString("Titel");
                art = jsonObject.getString("Art");
                preis = Double.parseDouble(jsonObject.getString("Preis"));
                stadt = jsonObject.getString("Stadt");
                email = jsonObject.getString("Email");
                beschreibung = jsonObject.getString("Beschreibung");
                favorit = jsonObject.getInt("Favorit");
                JSONArray jsonArrayImages, jsonArrayNachrichten;
                jsonArrayImages = jsonObject.getJSONArray("Images");
                jsonArrayNachrichten = jsonObject.getJSONArray("Nachrichten");

                images = new ArrayList<>();
                for (int j = 0; j < jsonArrayImages.length(); j++){
                    images.add(jsonArrayImages.getString(j));
                }
                nachrichten = new ArrayList<>();
                for (int j = 0; j < jsonArrayNachrichten.length(); j++){
                    nachrichten.add(jsonArrayNachrichten.getString(j));
                }

                Angebot a = new Angebot(beitragID,art,stadt,preis,titel,email,beschreibung,favorit, images, nachrichten);
                angebotContainer.add(a);
            }
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void saveToFile(){
        try {
            File myFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ImmobilienSuche/Angebote.txt");
            FileOutputStream fos = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fos);

            JSONArray jsonarray = new JSONArray();

            for (int i = 0; i < angebotContainer.size(); i++) {
                Angebot a = angebotContainer.get(i);
                JSONObject object = new JSONObject();
                JSONArray jsonArrayImages = new JSONArray();
                JSONArray jsonArrayNachrichten = new JSONArray();
                object.put("BeitragsID", a.getBeitragID());
                object.put("Art", a.getArt());
                object.put("Titel",a.getTitel());
                object.put("Preis", a.getPreis());
                object.put("Stadt", a.getStadt());
                object.put("Email", a.getEmail());
                object.put("Beschreibung", a.getBeschreibung());
                object.put("Favorit", a.getFavorit());

                ArrayList<String> images = a.getImages();
                for (int j = 0; j < images.size(); j++) {
                    jsonArrayImages.put(images.get(j));
                }
                object.put("Images", jsonArrayImages);

                ArrayList<String> nachrichten = a.getNachricht();
                for (int j = 0; j < nachrichten.size(); j++) {
                    jsonArrayNachrichten.put(nachrichten.get(j));
                }
                object.put("Nachrichten", jsonArrayNachrichten);
                jsonarray.put(object);
            }
            myOutWriter.write(jsonarray.toString());
            myOutWriter.close();
            fos.close();

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public void openBrowseActivitySearch() {
        searchedAngebotContainer = new ArrayList<>();
        for(int i = 0; i < angebotContainer.size(); i++){
            Angebot a = angebotContainer.get(i);
            if (a.getStadt().equals(stadtName) && a.getArt().equals(searchedArt)){
                searchedAngebotContainer.add(a);
            }
        }

        if(searchedAngebotContainer.size()!= 0) {
            Intent intent = new Intent(KundenActivity.this, BrowseActivity.class);
            intent.putParcelableArrayListExtra("angebotContainer", searchedAngebotContainer);
            intent.putExtra("prevActivity", className);
            intent.putExtra("title", "Angebote in " + stadtName);
            startActivityForResult(intent, REQUEST_CODE);
        }
        else{
            Toast t;
            t = Toast.makeText(KundenActivity.this.getApplicationContext(),
                    " Stadt nicht gefunden oder es gibt kein Angebot in dieser Stadt ", Toast.LENGTH_SHORT);
            t.show();
        }

    }

    public void openBrowseActivityFavorit() {
        searchedAngebotContainer = new ArrayList<>();
        for(int i = 0; i < angebotContainer.size(); i++){
            Angebot a = angebotContainer.get(i);
            if (a.getFavorit() == 1){
                searchedAngebotContainer.add(a);
            }
        }

        if(searchedAngebotContainer.size()!= 0) {
            Intent intent = new Intent(KundenActivity.this, BrowseActivity.class);
            intent.putParcelableArrayListExtra("angebotContainer", searchedAngebotContainer);
            intent.putExtra("prevActivity", this.getClass().getSimpleName());
            intent.putExtra("title", "Meine Favoriten");
            startActivityForResult(intent,REQUEST_CODE);
        }
        else {
            Toast t;
            t = Toast.makeText(KundenActivity.this.getApplicationContext(),
                    " Sie haben kein favorites Angebot ", Toast.LENGTH_SHORT);
            t.show();
        }
    }

    public void onRadioButtonClicked() {
        // Is the view now checked?
        RadioButton cb1 = findViewById(R.id.KaufButton);
        RadioButton cb2 = findViewById(R.id.MietButton);

        // Check which checkbox was clicked

        if (cb1.isChecked()) {
            searchedArt = "K";
        }
        if (cb2.isChecked()) {
            searchedArt = "M";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 1 :
                searchedAngebotContainer = data.getParcelableArrayListExtra("angebotContainer");
                for(int i = 0 ; i < angebotContainer.size() ; i++)
                {
                    for(int j = 0 ; j < searchedAngebotContainer.size() ; j++){
                        if(angebotContainer.get(i).getBeitragID() == searchedAngebotContainer.get(j).getBeitragID()){
                            angebotContainer.set(i,searchedAngebotContainer.get(j));
                        }
                    }
                }
                saveToFile();
                break;
        }

    }
}
