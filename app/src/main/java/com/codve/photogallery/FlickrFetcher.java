package com.codve.photogallery;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickrFetcher {

    private static final String TAG = "FlickrFetcher";
    private static final String API_KEY = "f21e87e7a6beb65edab0dde2b1888b9e";

    // 从指定 URL 中获取字节
    public byte[] getUrlBytes(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InputStream inputStream = connection.getInputStream(); // 这时才真正发起 HTTP 请求

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage());
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            return outputStream.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlString) throws IOException {
        return new String(getUrlBytes(urlString));
    }

    // 构造请求 URL: https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=f21e87e7a6beb65edab0dde2b1888b9e&format=json&nojsoncallback=1&extras=url_s
    public List<GalleryItem> fetchItems() {

        List<GalleryItem> items = new ArrayList<>();

        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();
            String jsonString = getUrlString(url);
            Gson gson = new Gson();
            FlickrData data = gson.fromJson(jsonString, FlickrData.class);
            items = data.getPhoto();
//            JSONObject jsonBody = new JSONObject(jsonString);
//            parseItems(items, jsonBody);
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items", e);
        }
        return items;
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody)
        throws IOException, JSONException {
        JSONObject photos = jsonBody.getJSONObject("photos"); // 获取"photos" 的值
        JSONArray photoArr = photos.getJSONArray("photo"); // 获取"photo" 的值

        for (int i = 0; i < photoArr.length(); i++) {
            JSONObject photo = photoArr.getJSONObject(i);

            GalleryItem item = new GalleryItem();
            item.setId(photo.getString("id"));
            item.setCaption(photo.getString("title"));

            if (!photo.has("url_s")) {
                continue;
            }
            item.setUrl(photo.getString("url_s"));
            items.add(item);
        }
    }
}
