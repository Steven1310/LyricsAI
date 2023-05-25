package com.csis4495_fall2022.lyricsai.adapters;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_fall2022.lyricsai.R;
import com.csis4495_fall2022.lyricsai.models.Album;
import com.csis4495_fall2022.lyricsai.models.Song;

import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SimpleViewHolder> {

    private final SongSelectedListener mSongSelectedListener;
    private final Context mContext;
    private List<Song> mSongs;
    private Album mAlbum;
    SimpleViewHolder holder;

    public SongsAdapter(@NonNull final Context context, @NonNull final Album album) {
        mContext = context;
        mAlbum = album;
        mSongs = mAlbum.getSongs();
        mSongSelectedListener = (SongSelectedListener) mContext;
    }

    public void swapSongs(@NonNull final Album album) {
        mAlbum = album;
        mSongs = mAlbum.getSongs();
        notifyDataSetChanged();
    }

    public void updateHolder(SimpleViewHolder holder){
        this.holder=holder;
    }

    public void displayLyrics(String lyrics){
        this.holder.tvLyrics.setText(lyrics);
    }



    @Override
    @NonNull
    public SimpleViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {

        final View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.song_item, parent, false);
        return new SimpleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder holder, final int position) {

        final Song song = mSongs.get(holder.getAdapterPosition());
        final String songTitle = song.getSongTitle();

        final int songTrack = Song.formatTrack(song.getTrackNumber());

        holder.track.setText(String.valueOf(songTrack));
        holder.title.setText(songTitle);
        holder.duration.setText(Song.formatDuration(song.getSongDuration()));
        updateHolder(holder);
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    public interface SongSelectedListener {
        void onSongSelected(@NonNull final Song song, @NonNull final List<Song> songs);
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {

        final TextView track, title, duration;
        private boolean sSongLongPressed = false;
        Button editSongDetials;
        Dialog dialog = new Dialog(mContext);
        TextView okay_text, cancel_text;
        EditText etSongName,etAlbumName, etArtistName;
        MediaScannerConnection scanner;
        TextView tvLyrics;

        SimpleViewHolder(@NonNull final View itemView) {
            super(itemView);

            track = itemView.findViewById(R.id.track);
            title = itemView.findViewById(R.id.title);
            duration = itemView.findViewById(R.id.duration);
            editSongDetials=itemView.findViewById(R.id.editSongDetails);
            tvLyrics=itemView.findViewById(R.id.tvLyrics);
            tvLyrics.setText("");
            editSongDetials.setOnClickListener(this);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setOnTouchListener(this);
        }

        @Override
        public void onClick(@NonNull final View v) {
            final Song song = mSongs.get(getAdapterPosition());
            if(v.getId()==R.id.editSongDetails)
            {
                dialog.setContentView(R.layout.edit_dialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

                okay_text = dialog.findViewById(R.id.okay_text);
                cancel_text = dialog.findViewById(R.id.cancel_text);
                etSongName=dialog.findViewById(R.id.etSongName);
                etAlbumName=dialog.findViewById(R.id.etAlbumName);
                etArtistName=dialog.findViewById(R.id.etArtistName);

                okay_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //dialog.dismiss();
                        //Toast.makeText(mContext, "okay clicked", Toast.LENGTH_SHORT).show();
                        if(etSongName.getText().toString().equals(""))
                            Toast.makeText(mContext, "Enter song name", Toast.LENGTH_SHORT).show();
                        else if(etAlbumName.getText().toString().equals(""))
                            Toast.makeText(mContext, "Enter album name", Toast.LENGTH_SHORT).show();
                        else if(etArtistName.getText().toString().equals(""))
                            Toast.makeText(mContext, "Enter artist name", Toast.LENGTH_SHORT).show();
                        else
                        {
                            String oldSongName="";
                            File src = new File(song.getSongPath());
                            MusicMetadataSet src_set = null;
                            try {
                                src_set = new MyID3().read(src);
                            } catch (IOException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            } // read metadata

                            if (src_set == null) // perhaps no metadata
                            {
                                Log.i("NULL", "NULL");
                            }
                            else
                            {
                                String song_title="name";
                                try{
                                    IMusicMetadata metadata = src_set.getSimplified();
                                    String artist = metadata.getArtist();
                                    String album = metadata.getAlbum();
                                    song_title = metadata.getSongTitle();
                                    Number track_number = metadata.getTrackNumber();
                                    //Log.i("artist", artist);
                                    //Log.i("album", album);
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                                File dst = new File(song.getSongPath());
                                MusicMetadata meta = new MusicMetadata(song_title);
                                meta.setSongTitle(etSongName.getText().toString());
                                meta.setAlbum(etAlbumName.getText().toString());
                                meta.setArtist(etArtistName.getText().toString());
                                try {
                                    new MyID3().update(src, src_set, meta);
                                } catch (UnsupportedEncodingException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (ID3WriteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }  // write updated metadata
                                oldSongName=song_title;
                            }
                            dialog.dismiss();
                            final String newPath=song.getSongPath().replace(oldSongName,etSongName.getText().toString());
                            scanner=new MediaScannerConnection(mContext,
                                    new MediaScannerConnection.MediaScannerConnectionClient() {
                                        public void onScanCompleted(String path, Uri uri) {
                                            Log.i("SonagAdapter",""+path);
                                            scanner.disconnect();
                                        }
                                        public void onMediaScannerConnected() {
                                            scanner.scanFile(song.getSongPath(), "audio/*");
                                        }
                                    });

                            scanner.connect();
                            Toast.makeText(mContext, "Song updated successfully!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                cancel_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        //Toast.makeText(mContext, "Cancel clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.show();
            }
            else
                mSongSelectedListener.onSongSelected(song, mAlbum.getSongs());
        }

        @Override
        public boolean onLongClick(@NonNull final View v) {
            if (!sSongLongPressed) {
                itemView.setSelected(true);
                sSongLongPressed = true;
            }
            return true;
        }

        @Override
        @SuppressLint("ClickableViewAccessibility")
        public boolean onTouch(@NonNull final View v, @NonNull final MotionEvent event) {
            if (sSongLongPressed && event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_OUTSIDE || event.getAction() == MotionEvent.ACTION_MOVE) {
                itemView.setSelected(false);
                sSongLongPressed = false;
            }
            return false;
        }
    }
}