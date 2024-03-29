package com.csis4495_fall2022.lyricsai.loaders;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.csis4495_fall2022.lyricsai.models.Album;
import com.csis4495_fall2022.lyricsai.models.Song;

public class SongProvider {

    private static final int TITLE = 0;
    private static final int TRACK = 1;
    private static final int YEAR = 2;
    private static final int DURATION = 3;
    private static final int PATH = 4;
    private static final int ALBUM = 5;
    private static final int ARTIST_ID = 6;
    private static final int ARTIST = 7;

    private static final String[] BASE_PROJECTION = new String[]{
            AudioColumns.TITLE,// 0
            AudioColumns.TRACK,// 1
            AudioColumns.YEAR,// 2
            AudioColumns.DURATION,// 3
            AudioColumns.DATA,// 4
            AudioColumns.ALBUM,// 5
            AudioColumns.ARTIST_ID,// 6
            AudioColumns.ARTIST,// 7
    };

    private static final List<Song> mAllDeviceSongs = new ArrayList<>();

    @NonNull
    public static List<Song> getAllDeviceSongs() {
        return mAllDeviceSongs;
    }

    @NonNull
    public static List<Song> getAllArtistSongs(@NonNull final List<Album> albums) {
        final List<Song> songsList = new ArrayList<>();
        try {
            for (Album album : albums) {
                songsList.addAll(album.getSongs());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return songsList;
    }

    @NonNull
    static List<Song> getSongs(@Nullable final Cursor cursor) {
        final List<Song> songs = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                final Song song = getSongFromCursorImpl(cursor);
                if (song.getSongDuration() >= 5000) {
                    songs.add(song);
                    mAllDeviceSongs.add(song);
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        if (songs.size() > 1) {
            sortSongsByTrack(songs);
        }
        return songs;
    }

    private static void sortSongsByTrack(@NonNull final List<Song> songs) {
        Collections.sort(songs, (obj1, obj2) -> Long.compare(obj1.getTrackNumber(), obj2.getTrackNumber()));
    }

    @NonNull
    private static Song getSongFromCursorImpl(@NonNull final Cursor cursor) {
        final String title = cursor.getString(TITLE);
        final int trackNumber = cursor.getInt(TRACK);
        final int year = cursor.getInt(YEAR);
        final int duration = cursor.getInt(DURATION);
        final String uri = cursor.getString(PATH);
        final String albumName = cursor.getString(ALBUM);
        final int artistId = cursor.getInt(ARTIST_ID);
        final String artistName = cursor.getString(ARTIST);

        return new Song(title, trackNumber, year, duration, uri, albumName, artistId, artistName);
    }

    @Nullable
    static Cursor makeSongCursor(@NonNull final Context context, @NonNull final String sortOrder) {
        try {
            return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    BASE_PROJECTION, null, null, sortOrder);
        } catch (SecurityException e) {
            return null;
        }
    }
}
