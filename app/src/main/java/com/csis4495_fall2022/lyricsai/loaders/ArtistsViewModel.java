package com.csis4495_fall2022.lyricsai.loaders;

import android.content.Context;



import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.csis4495_fall2022.lyricsai.models.Artist;

public class ArtistsViewModel extends ViewModel {
    private MutableLiveData<List<Artist>> artists;

    public LiveData<List<Artist>> getArtists(@NonNull final Context context) {
        if (artists == null) {
            artists = new MutableLiveData<>();
            loadUsers(context);
        }
        return artists;
    }

    private void loadUsers(@NonNull Context context) {
        artists.setValue(ArtistProvider.getAllArtists(context));
    }
}