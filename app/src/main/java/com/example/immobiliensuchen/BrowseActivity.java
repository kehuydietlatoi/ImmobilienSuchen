package com.example.immobiliensuchen;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BrowseActivity extends AppCompatActivity {

    private static final String TAG = "BrowseActivity";

    private RecyclerView recyclerView;

    private ArrayList<Angebot> angebotContainer;
    private Angebot angebot;

    private RecyclerViewAdapter adapter;
    private Intent intentWithResult;
    private String prevActivity;

    private ImageView sortImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        Toolbar toolbar = (Toolbar) findViewById(R.id.Angebote);
        setSupportActionBar(toolbar);
        setTitle(getIntent().getStringExtra("title"));

        sortImageView = findViewById(R.id.sort);

//      pass variable from KundenActivity or MaklerActivity
        angebotContainer = getIntent().getParcelableArrayListExtra("angebotContainer");
        prevActivity = getIntent().getStringExtra("prevActivity");
        initRecyclerView();

        sortImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortMenu(BrowseActivity.this);
            }
        });
    }

    private void initRecyclerView(){
        //Log.d(TAG, "initRecyclerView: intit RecyclerView");
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new RecyclerViewAdapter(angebotContainer,this, prevActivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 1 :
                angebot = data.getParcelableExtra("angebot");
                for(int i = 0 ; i < angebotContainer.size() ; i++)
                {
                    if(angebotContainer.get(i).getBeitragID() == angebot.getBeitragID()){
                        angebotContainer.set(i,angebot);
                    }
                }
                adapter = new RecyclerViewAdapter(angebotContainer,this, prevActivity);
                recyclerView.setAdapter(adapter);
                intentWithResult = new Intent();
                intentWithResult.putExtra("angebotContainer" , angebotContainer);
                setResult(1, intentWithResult);
                break;
            case 2 :
                angebot = data.getParcelableExtra("angebot");
                for(int i = 0 ; i < angebotContainer.size() ; i++)
                {
                    if(angebotContainer.get(i).getBeitragID() == angebot.getBeitragID()){
                        angebotContainer.set(i,angebot);
                    }
                }
                Toast.makeText(this, "Angebot wird aktualisiert!",
                        Toast.LENGTH_SHORT).show();
                adapter = new RecyclerViewAdapter(angebotContainer,this, prevActivity);
                recyclerView.setAdapter(adapter);
                intentWithResult = new Intent();
                intentWithResult.putExtra("angebotContainer" , angebotContainer);
                setResult(4, intentWithResult);
                break;
            case 3 :
                Toast.makeText(this, "Änderungen abgebrochen!",
                        Toast.LENGTH_SHORT).show();
                setResult(5);
                break;
            case 4 :
                int beitragsId = data.getIntExtra("beitragsId", 0);
                for(int i = 0 ; i < angebotContainer.size() ; i++)
                {
                    if(angebotContainer.get(i).getBeitragID() == beitragsId){
                        angebotContainer.remove(i);
                    }
                }
                Toast.makeText(this, "Angebot wird gelöscht!",
                        Toast.LENGTH_SHORT).show();
                recyclerView.setAdapter(adapter);
                intentWithResult = new Intent();
                intentWithResult.putExtra("angebotContainer" , angebotContainer);
                setResult(4, intentWithResult);
                break;
        }
    }

    private void sortMenu(Context context) {
        final CharSequence[] options = {"Neueste", "Preis aufsteigend", "Preis absteigend"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Angebote Sortierung");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if(options[item].equals("Neueste")){ //sort with ID
                    sortNeueste();
                    adapter = new RecyclerViewAdapter(angebotContainer,BrowseActivity.this, prevActivity);
                    recyclerView.setAdapter(adapter);
                    dialog.dismiss();
                }
                else if (options[item].equals("Preis aufsteigend")) {
                    sortPreisAuf();
                    adapter = new RecyclerViewAdapter(angebotContainer,BrowseActivity.this, prevActivity);
                    recyclerView.setAdapter(adapter);
                    dialog.dismiss();
                }
                else if (options[item].equals("Preis absteigend")) {
                    sortPreisAb();
                    adapter = new RecyclerViewAdapter(angebotContainer,BrowseActivity.this, prevActivity);
                    recyclerView.setAdapter(adapter);
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void sortNeueste(){
        Angebot tempAngebot;
        tempAngebot = angebotContainer.get(0);
        int n = angebotContainer.size();
        for (int i = 0 ; i < n ;i++){
            for(int j=1; j < (n-i); j++){
                if(angebotContainer.get(j-1).getBeitragID()<angebotContainer.get(j).getBeitragID()){
                    tempAngebot = angebotContainer.get(j-1);
                    angebotContainer.set((j-1),angebotContainer.get(j));
                    angebotContainer.set(j,tempAngebot);
                }
            }
        }
    }

    private void sortPreisAuf(){
        Angebot tempAngebot;
        tempAngebot = angebotContainer.get(0);
        int n = angebotContainer.size();
        for (int i = 0 ; i < n ;i++){
            for(int j=1; j < (n-i); j++){
                if(angebotContainer.get(j-1).getPreis()>angebotContainer.get(j).getPreis()){
                    tempAngebot = angebotContainer.get(j-1);
                    angebotContainer.set((j-1),angebotContainer.get(j));
                    angebotContainer.set(j,tempAngebot);
                }
            }
        }
    }

    private void sortPreisAb(){
        Angebot tempAngebot;
        tempAngebot = angebotContainer.get(0);
        int n = angebotContainer.size();
        for (int i = 0 ; i < n ;i++){
            for(int j=1; j < (n-i); j++){
                if(angebotContainer.get(j-1).getPreis()<angebotContainer.get(j).getPreis()){
                    tempAngebot = angebotContainer.get(j-1);
                    angebotContainer.set((j-1),angebotContainer.get(j));
                    angebotContainer.set(j,tempAngebot);
                }
            }
        }
    }


}

