package com.exe.whateat.application.image;

public interface FirebaseImageService {

    /**
     * Uploads Base64 image and returns the URL path to said image on Firebase.
     *
     * @param base64Image Image in Base64 encoding.
     *
     * @return The URL to that image.
     */
    String uploadBase64Image(String base64Image);
}
