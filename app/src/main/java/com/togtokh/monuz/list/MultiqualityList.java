package com.togtokh.monuz.list;

public class MultiqualityList {
    String quality;
    String url;

    public MultiqualityList(String quality, String url) {
        this.quality = quality;
        this.url = url;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
