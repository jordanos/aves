package com.example.aves.Domain;

public class CategoryDomain {
    private String title;
    private String pic;
    private String url = "https://pixabay.com/api/?key=5303976-fd6581ad4ac165d1b75cc15b3&q=food&image_type=photo&pretty=true";

    public CategoryDomain(String title, String pic) {
        this.title = title;
        this.pic = pic;
    }

    public CategoryDomain(String title, String pic, String url) {
        this.title = title;
        this.pic = pic;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
