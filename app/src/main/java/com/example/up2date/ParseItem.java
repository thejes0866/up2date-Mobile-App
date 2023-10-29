package com.example.up2date;


public class ParseItem {
    private String imgUrl;
    private String title;
    private String detailUrl;
    private int i;


    public ParseItem() {

    }

    public ParseItem(String imgUrl, String title, String detailUrl, int i) {
        this.imgUrl = imgUrl;
        this.title = title;
        this.detailUrl = detailUrl;
        this.i = i;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetailUrl() {
        return detailUrl;
    }
    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }
    public int geti() {
        return i;
    }
    public void seti(int i) {
        this.i = i;
    }

}
