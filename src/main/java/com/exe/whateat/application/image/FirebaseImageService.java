package com.exe.whateat.application.image;

public interface FirebaseImageService {

    enum DeleteType {

        ID,
        URL
    }

    /**
     * Uploads Base64 image and returns the URL path to said image on Firebase.
     *
     * @param base64Image Image in Base64 encoding.
     *
     * @return {@link FirebaseImageResponse} containing the ID and URL of the image.
     */
    FirebaseImageResponse uploadBase64Image(String base64Image);

    /**
     * Deletes image on Firebase using the image's argument (attribute).
     *
     * @param imageArg The argument of the image. Supports ID and base URL.
     */
    void deleteImage(String imageArg, DeleteType deleteType);
}
