package com.example.filmes_populares;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class FilmeAdapter extends ArrayAdapter<Filme> {

    private Context context;
    private List<Filme> filmes;

    public FilmeAdapter(Context context, List<Filme> filmes) {
        super(context, -1, filmes);

        this.context = context;
        this.filmes = filmes;
    }

    @Override
    public int getCount() {
        return filmes.size();
    }

    private class FilmeViewHolder{
        ImageView filmeImageView;
        TextView tituloTextView;
        TextView sinopseTextView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FilmeViewHolder vh = null;

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);

            convertView = inflater.inflate(R.layout.list_item, parent, false);

            vh = new FilmeViewHolder();

            vh.filmeImageView = convertView.findViewById(R.id.filmeImageView);
            vh.tituloTextView = convertView.findViewById(R.id.tituloTextView);
            vh.sinopseTextView = convertView.findViewById(R.id.sinopseTextView);

            convertView.setTag(vh);
        }
        else
            vh = (FilmeViewHolder) convertView.getTag();

        Filme filme = filmes.get(position);

        vh.tituloTextView.setText(filme.title);
        vh.sinopseTextView.setText(filme.overview);
        vh.filmeImageView.setImageBitmap(filme.posterImage);

        return convertView;
    }

}
