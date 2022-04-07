package com.example.aves.Domain;

import android.util.Log;

import com.example.aves.Interface.Api;

import java.net.URI;
import java.net.URISyntaxException;

public class ContentDomain {

    private String id;
    private String title;
    private String pic;
    private double likes;

    public ContentDomain(String id, String title, String pic, double likes) {
        this.id = id;
        this.title = title;
        this.pic = pic;
        this.likes = likes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getLikes() {
        String new_likes = "";

        if(this.likes < 1000) {
            new_likes = Integer.toString((int)this.likes);
        }
        else if (this.likes >= 1000 && this.likes < 1000000) {
            double lk = Math.round(this.likes/1000.0);
            new_likes = Double.toString(lk) + "k";
        } else if (this.likes >= 1000000) {
            double lk = Math.round(this.likes/1000000.0);
            new_likes = Double.toString(lk) + "m";
        }
        return new_likes;
    }

    public void setLikes(double likes) {
        this.likes = likes;
    }

    public String getNewImageUrl(String url){
        return Api.BASE_URL + url.substring(22);
    }

}
