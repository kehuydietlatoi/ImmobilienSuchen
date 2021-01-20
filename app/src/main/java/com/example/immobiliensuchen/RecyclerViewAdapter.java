package com.example.immobiliensuchen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";
    private static final String pathToPicture = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ImmobilienSuche/images";
    private ArrayList<Angebot> angebotContainer;
    private Angebot angebot;
    private Context context;
    private String prevActivity;

    static final int REQUEST_CODE = 0;

    public RecyclerViewAdapter(ArrayList<Angebot> angebotContainer,  Context context, String prevActivity) {
        this.angebotContainer = angebotContainer;
        this.context = context;
        this.prevActivity = prevActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent , false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        //Log.d(TAG, "onBindViewHolder: called.");
        angebot = angebotContainer.get(position);
        holder.image.setImageBitmap(BitmapFactory.decodeFile(pathToPicture + File.separator + angebot.getImages().get(0)));  //first picture in imagesId List
        holder.Titel.setText(angebot.getTitel());
        holder.Preis.setText(Double.toString(angebot.getPreis()));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "onClick: " + mTitel.get(position));
                //Toast.makeText(context,angebotContainer.get(position).getTitel(),Toast.LENGTH_SHORT).show();

                if(prevActivity.equals(KundenActivity.class.getSimpleName())) {
                    angebot = angebotContainer.get(position);
                    Intent intent = new Intent(context, AngebotActivity.class);
                    intent.putExtra("angebot", angebot);
                    ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
                }

                else if(prevActivity.equals(MaklerActivity.class.getSimpleName())){
                    angebot = angebotContainer.get(position);
                    Intent intent = new Intent(context, AngebotAktualisierenActivity.class);
                    intent.putExtra("angebot", angebot);
                    ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return angebotContainer.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView Titel;
        TextView Preis;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.images);
            Titel = itemView.findViewById(R.id.angebotTitel);
            Preis = itemView.findViewById(R.id.angebotpreis);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }

}
