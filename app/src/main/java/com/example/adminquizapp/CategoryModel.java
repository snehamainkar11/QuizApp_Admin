package com.example.adminquizapp;

public class CategoryModel {

    private String name;
    private int sets;
    private String url;
    String key;
    public CategoryModel()
    {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public CategoryModel(String name, int sets, String url, String key) {
        this.name = name;
        this.sets = sets;
        this.url = url;
        this.key=key;
    }



}
