package com.example.immobiliensuchen;

import android.os.Parcelable;
import android.os.Parcel;

import java.util.ArrayList;

class Angebot implements Parcelable {

    private int beitragID, favorit;
    private String art;
    private String stadt;
    private String titel;
    private String beschreibung;
    private String email;
    private double preis;
    private ArrayList<String> images;


    private ArrayList<String> nachrichten;

    public Angebot(int BeitragID, String art, String stadt, double preis, String titel, String email, String beschreibung, int favorit, ArrayList<String> images, ArrayList<String> nachrichten)
    {
        this.beitragID = BeitragID;
        this.art = art;
        this.beschreibung= beschreibung;
        this.titel = titel;
        this.stadt = stadt;
        this.preis = preis;
        this.email = email;
        this.images = images;
        this.favorit = favorit;  //0 = false, 1 = true;
        this.nachrichten = nachrichten;
    }

    public Angebot(int BeitragID, String art, String stadt, double preis, String titel, String email, String beschreibung, int favorit, ArrayList<String> images)
    {
        this.beitragID = BeitragID;
        this.art = art;
        this.beschreibung= beschreibung;
        this.titel = titel;
        this.stadt = stadt;
        this.preis = preis;
        this.email = email;
        this.images = images;
        this.favorit = favorit;  //0 = false, 1 = true;
        this.nachrichten = new ArrayList<>();
    }

    //Getter und Setter
    public int getBeitragID() {
        return beitragID;
    }

    public void setBeitragID(int beitragID) {
        this.beitragID = beitragID;
    }

    public String getArt() {
        return art;
    }

    public void setArt(String art) {
        this.art = art;
    }

    public String getStadt() {
        return stadt;
    }

    public void setStadt(String stadt) {
        this.stadt = stadt;
    }

    public void setFavorit(int favorit){
        this.favorit = favorit;
    }


    public int getFavorit(){
        return favorit;
    }

    public double getPreis() {
        return preis;
    }

    public void setPreis(double preis) {
        this.preis = preis;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public ArrayList<String> getNachricht() {
        return nachrichten;
    }

    public void setNachricht(ArrayList<String> nachrichten) {
        this.nachrichten = nachrichten;
    }


    public Angebot(Parcel in){
        beitragID = in.readInt();
        art = in.readString();
        stadt = in.readString();
        preis = in.readDouble();
        titel = in.readString();
        email = in.readString();
        beschreibung = in.readString();
        favorit = in.readInt();
        images = new ArrayList<>();
        in.readList(images, String.class.getClassLoader());
        nachrichten = new ArrayList<>();
        in.readList(nachrichten, String.class.getClassLoader());
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(beitragID);
        out.writeString(art);
        out.writeString(stadt);
        out.writeDouble(preis);
        out.writeString(titel);
        out.writeString(email);
        out.writeString(beschreibung);
        out.writeInt(favorit);
        out.writeList(images);
        out.writeList(nachrichten);
    }

    public static final Parcelable.Creator<Angebot> CREATOR = new Parcelable.Creator<Angebot>() {
        @Override
        public Angebot createFromParcel(Parcel in) {
            return new Angebot(in);
        }

        @Override
        public Angebot[] newArray(int size) {
            return new Angebot[size];
        }
    };
}