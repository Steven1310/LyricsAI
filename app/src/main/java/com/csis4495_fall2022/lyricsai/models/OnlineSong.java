package com.csis4495_fall2022.lyricsai.models;

public class OnlineSong {

    private String title;
    private String poster;
    private String artist;
    private String url;


    public OnlineSong(String title, String poster, String artist, String url) {
        this.title = title;
        this.poster = poster;
        this.artist = artist;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
