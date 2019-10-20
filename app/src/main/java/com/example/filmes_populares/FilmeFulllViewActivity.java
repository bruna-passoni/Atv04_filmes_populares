package com.example.filmes_populares;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FilmeFulllViewActivity extends AppCompatActivity {

    private ImageView filmeImageView;
    private TextView tituloTextView;
    private TextView sinopseTextView;
    private TextView tituloOrigTextView;
    private TextView votesTextView;
    private RatingBar averageRatingBar;

    private ProgressDialog load;
    private String poster_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filme_fulll_view);

        filmeImageView = findViewById(R.id.filmeImageView);
        tituloTextView = findViewById(R.id.tituloTextView);
        tituloOrigTextView = findViewById(R.id.tituloOrigTextView);
        averageRatingBar = findViewById(R.id.averageRatingBar);
        sinopseTextView = findViewById(R.id.overviewTextView);
        votesTextView = findViewById(R.id.votesTextView);

        //Bitmap filme = BitmapFactory.(getIntent().getSerializableExtra("poster_filme"));

        String votes = getIntent().getStringExtra("votes_filme");
        String title = getIntent().getStringExtra("title_filme");
        String titleOrig = getIntent().getStringExtra("title_filme_orig");
        String overview = getIntent().getStringExtra("overview_filme");
        double average = getIntent().getDoubleExtra("average_filme", 0);
        poster_path = getIntent().getStringExtra("poster_path");

        tituloTextView.setText(title);
        sinopseTextView.setText(overview);
        tituloOrigTextView.setText(titleOrig);
        averageRatingBar.setRating(Float.parseFloat(Double.toHexString(average)));
        votesTextView.setText(votes);

        BaixaPoster baixaPoster = new BaixaPoster();
        baixaPoster.execute();
    }

    private class BaixaPoster extends AsyncTask<Void, Void, Bitmap> {
        private int page;

        protected void setPage(int page){
            this.page=page;
        }

        @Override
        protected void onPreExecute(){
            load = ProgressDialog.show(FilmeFulllViewActivity.this,
                    getString(R.string.please_wait), getString(R.string.looking_for_moview));
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap figura = null;

            try{
                URL url = new URL(getString(R.string.url_download_poster, poster_path));
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                InputStream is = conn.getInputStream();

                InputStreamReader isr = new InputStreamReader(is);

                figura = BitmapFactory.decodeStream(is);

                is.close();
                conn.disconnect();
            }
            catch (MalformedURLException ex){
                ex.printStackTrace();
            }
            catch (IOException ex){
                ex.printStackTrace();
            }

            return figura;
        }

        @Override
        protected void onPostExecute(Bitmap figura){
            filmeImageView.setImageBitmap(figura);

            load.dismiss();
            load.cancel();
        }
    }

}
