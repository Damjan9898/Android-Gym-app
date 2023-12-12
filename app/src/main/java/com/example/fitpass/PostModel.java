package com.example.fitpass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;

public class PostModel {

    private String id, name, address, phone, weekday_time, sunday_time, img, about_us, type, city;
    private double review, lat, lng;

    public PostModel(String id, String name, String address, String phone, double review, String weekday_time, String sunday_time, String img, String about_us, String type, String city, double lat, double lng) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.review = review;
        this.weekday_time = weekday_time;
        this.sunday_time = sunday_time;
        this.img = img;
        this.about_us = about_us;
        this.type = type;
        this.city = city;
        this.lat = lat;
        this.lng = lng;



    }

    public PostModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getReview() {
        return review;
    }

    public void setReview(double review) {
        this.review = review;
    }

    public String getWeekday_time() {
        return weekday_time;
    }

    public void setWeekday_time(String weekday_time) {
        this.weekday_time = weekday_time;
    }

    public String getSunday_time() {
        return sunday_time;
    }

    public void setSunday_time(String sunday_time) {
        this.sunday_time = sunday_time;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getAbout_us() {
        return about_us;
    }

    public void setAbout_us(String about_us) {
        this.about_us = about_us;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public static PostModel parseJSONObject (JSONObject object) {
        PostModel post = new PostModel();

        try{
            if(object.has("id")){
                post.setId(object.getString("id"));
            }
            if(object.has("address")){
                post.setAddress(object.getString("address"));
            }

            if(object.has("name")){
                post.setName(object.getString("name"));
            }
            if(object.has("phone")){
                post.setPhone(object.getString("phone"));
            }
            if(object.has("review")){
                post.setReview(object.getDouble("review"));
            }
            if(object.has("weekday_time")){
                post.setWeekday_time(object.getString("weekday_time"));
            }
            if(object.has("sunday_time")){
                post.setSunday_time(object.getString("sunday_time"));
            }
            if(object.has("img")){
                post.setImg(object.getString("img"));
            }
            if(object.has("about_us")){
                post.setAbout_us(object.getString("about_us"));
            }
            if(object.has("type")){
                post.setType(object.getString("type"));
            }
            if(object.has("city")){
                post.setCity(object.getString("city"));
            }
            if(object.has("lat")){
                post.setLat(object.getDouble("lat"));
            }
            if(object.has("lng")){
                post.setLng(object.getDouble("lng"));
            }

        }catch(Exception e){

        }




        return post;
    }


    public static LinkedList<PostModel> parseJSONArray (JSONArray array){

        LinkedList<PostModel> list = new LinkedList<>();

        try{

            for(int i = 0; i < array.length(); i++){
                PostModel post = parseJSONObject(array.getJSONObject(i));
                list.add(post);
            }

        }catch(Exception e){

        }

        return list;

    }

}
