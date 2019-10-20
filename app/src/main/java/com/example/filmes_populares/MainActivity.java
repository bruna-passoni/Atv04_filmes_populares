package com.example.filmes_populares;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView filmesListView;
    private List<Filme> filmesList;
    private FilmeAdapter adapter;
    private int Page;
    private int TotalPages;
    private ProgressDialog load;
    private BaixaFilmes baixaFilmes;
    private boolean buscandoFilmes = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        filmesListView = findViewById(R.id.filmesListView);
        filmesList = new ArrayList<Filme>();

        adapter = new FilmeAdapter(this, filmesList);
        filmesListView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((View v) ->{
            buscandoFilmes = true;

            Page = 1;
            filmesList.clear();

            if(baixaFilmes != null && !baixaFilmes.isCancelled())
                baixaFilmes.cancel(true);

            baixaFilmes = new BaixaFilmes();

            baixaFilmes.setPage(Page);
            baixaFilmes.execute();

        });

        filmesListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(totalItemCount == 0 || buscandoFilmes)
                    return;

                final int lastItem = firstVisibleItem + visibleItemCount;

                if(lastItem == totalItemCount && Page < TotalPages){
                    buscandoFilmes = true;

                    if(!baixaFilmes.isCancelled())
                        baixaFilmes.cancel(true);

                    baixaFilmes = new BaixaFilmes();
                    baixaFilmes.setPage(++Page);
                    baixaFilmes.execute();
                }
            }
        });

        filmesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(MainActivity.this, FilmeFulllViewActivity.class);
                //intent.putExtra("poster_filme", filmesList.get(position).posterImage);
                intent.putExtra("votes_filme", filmesList.get(position).votes);
                intent.putExtra("title_filme_orig", filmesList.get(position).titleOrig);
                intent.putExtra("average_filme", filmesList.get(position).average);
                intent.putExtra("title_filme", filmesList.get(position).title);
                intent.putExtra("overview_filme", filmesList.get(position).overview);
                intent.putExtra("poster_path", filmesList.get(position).poster_path);

                startActivity(intent);
            }
        });

    }

    private class FilmeJson{
        String page;
        String total_results;
        String total_pages;

        List<ResultItemJson> results;
    }

    private class ResultItemJson{
        String popularity;
        String vote_count;
        String video;
        String poster_path;
        String id;
        String adult;
        String backdrop_path;
        String original_language;
        String original_title;
        List<String> genre_ids;
        String title;
        String vote_average;
        String overview;
        String release_date;
        Bitmap posterImage;
    }


    private class BaixaFilmes extends AsyncTask<Void, Void, FilmeJson> {
        private int page;

        protected void setPage(int page){
            this.page=page;
        }

        @Override
        protected void onPreExecute(){
            load = ProgressDialog.show(MainActivity.this,
                    getString(R.string.please_wait), getString(R.string.looking_for_moview));
        }

        @Override
        protected FilmeJson doInBackground(Void... params) {
            FilmeJson filmeJson = null;

            try{
                URL url = new URL(getString(R.string.web_service_url, getString(R.string.api_key), getString(R.string.language_api), Integer.toString(this.page)));
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                InputStream is = conn.getInputStream();

                InputStreamReader isr = new InputStreamReader(is);

                BufferedReader reader = new BufferedReader(isr);

                String linha = null;
                StringBuilder resultado = new StringBuilder();

                while ((linha = reader.readLine()) != null)
                    resultado.append(linha);

                Gson gson = new Gson();

                filmeJson = gson.fromJson(resultado.toString(), FilmeJson.class);

                TotalPages = Integer.parseInt(filmeJson.total_pages);

                reader.close();
                conn.disconnect();
                is.close();
                is = null;

                for (ResultItemJson item: filmeJson.results) {
                    url = new URL(getString(R.string.url_download_poster, item.poster_path));

                    conn = (HttpURLConnection)url.openConnection();

                    is = conn.getInputStream();

                    Bitmap figura = BitmapFactory.decodeStream(is);

                    is.close();
                    conn.disconnect();

                    item.posterImage = figura;
                }

            }
            catch (MalformedURLException ex){
                ex.printStackTrace();
            }
            catch (IOException ex){
                ex.printStackTrace();
            }

            return filmeJson;
        }

        @Override
        protected void onPostExecute(FilmeJson filmeJson){
            for (ResultItemJson item: filmeJson.results) {
                filmesList.add(new Filme(item.title, item.overview, item.poster_path, item.posterImage, Double.parseDouble(item.vote_average), item.original_title, item.vote_count));
            }

            adapter.notifyDataSetChanged();
            load.dismiss();
            load.cancel();
            buscandoFilmes = false;
        }
    }

}


