package com.example.filmes_populares;

import android.graphics.Bitmap;

public class Filme {

    public final String title;
    public final String overview;
    public final String poster_path;
    public final Bitmap posterImage;
    public final double average;
    public final String titleOrig;
    public final String votes;

    public Filme(String title, String overview, String poster_path, Bitmap posterImage, double average, String titleOrig, String votes){
        this.title = title;
        this.overview = overview;
        this.poster_path = poster_path;
        this.posterImage = posterImage;
        this.average = average;
        this.titleOrig = titleOrig;
        this.votes = votes;
    }

}
