package com.csis4495_fall2022.lyricsai.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_fall2022.lyricsai.R;
import com.csis4495_fall2022.lyricsai.models.OnlineSong;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OnlineSongAdapter extends RecyclerView.Adapter<OnlineSongAdapter.ViewHolder> {

    private ArrayList<OnlineSong> onlineSongsArrayList;
    private Context context;

    public OnlineSongAdapter(ArrayList<OnlineSong> onlineSongsArrayList, Context context) {
        this.onlineSongsArrayList = onlineSongsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.online_song_card,parent,false);
        ViewHolder viewHolder=new ViewHolder(root);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OnlineSong song=onlineSongsArrayList.get(position);

        holder.song_title.setText(song.getTitle());
        holder.song_artist.setText(String.valueOf(song.getArtist()));
        holder.url=song.getUrl();
        Picasso.get().load(song.getPoster()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return onlineSongsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView song_title, song_artist;
        String url;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.song_imageView);
            song_title=itemView.findViewById(R.id.song_title);
            song_artist=itemView.findViewById(R.id.song_artist);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*Snackbar.make(view,"Click detected on item "+getAdapterPosition(),Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();*/
                    Intent intent= new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(onlineSongsArrayList.get(getAdapterPosition()).getUrl()));
                    context.startActivity(intent);
                }
            });
        }
    }
}

