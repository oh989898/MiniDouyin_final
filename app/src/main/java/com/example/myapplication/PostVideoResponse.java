package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class PostVideoResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("item")
    private Item item;

    public static class Item {
        @SerializedName("student_id")
        private String student_id;

        @SerializedName("user_name")
        private String user_name;

        @SerializedName("image_url")
        private String image_url;

        @SerializedName("video_url")
        private String video_url;
    }

}
