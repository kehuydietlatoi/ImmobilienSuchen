package com.example.immobiliensuchen;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class MaklerActivity extends AppCompatActivity {

    private static final String TAG = "MaklerActivity";
    private static final int REQUEST_CODE = 0;
    private final String className = this.getClass().getSimpleName();

    private Button neuAngebotButton, angebotVerwaltenButton;
    private ArrayList<Angebot> angebotContainer = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makler_acitivity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Makler");

        neuAngebotButton = findViewById(R.id.neuAngebotButton);
        angebotVerwaltenButton = findViewById(R.id.angebotVerwaltenButton);


        neuAngebotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readFile();
                Intent intent = new Intent(MaklerActivity.this, NeuAngebotActivity.class);
                intent.putExtra("neuBeitragsID", angebotContainer.size());
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        angebotVerwaltenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readFile();
                Intent intent = new Intent(MaklerActivity.this, BrowseActivity.class);
                intent.putParcelableArrayListExtra("angebotContainer", angebotContainer);
                intent.putExtra("title", "Angebot verwalten");
                intent.putExtra("prevActivity", className);
                startActivityForResult(intent, REQUEST_CODE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 1: // new angebot succes
                Angebot neuesAngebot = data.getParcelableExtra("neuesAngebot");
                angebotContainer.add(neuesAngebot);
                saveToFile();
                Toast.makeText(this, "Neu Angebot wird gespeichert!",
                        Toast.LENGTH_SHORT).show();
                break;
            case 2 :// new angebot cancel
                Toast.makeText(this, "Neu Angebot abgebrochen",
                        Toast.LENGTH_SHORT).show();
                break;
            case 3 :// error
                Toast.makeText(this, "Error reading file",
                        Toast.LENGTH_SHORT).show();
                break;

            case 4:
                angebotContainer = data.getParcelableArrayListExtra("angebotContainer");
                saveToFile();
                break;
            case 5:
                break;
        }

    }
}
