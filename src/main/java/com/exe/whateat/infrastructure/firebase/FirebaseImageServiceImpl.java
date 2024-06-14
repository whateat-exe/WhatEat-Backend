package com.exe.whateat.infrastructure.firebase;

import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.image.FirebaseImageResponse;
import com.exe.whateat.application.image.FirebaseImageService;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional(rollbackOn = Exception.class)
public class FirebaseImageServiceImpl implements FirebaseImageService {

    private static final Map<String, String> MIMES;
    private static final String IMAGE_URL_FORMAT = "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media";
    private static final String IMAGE_URL_REGEX;
    private static final String STATIC_FOLDER = "static";

    private static final int MAX_IMAGE_SIZE = 3145728;      // 3MB
    private static final String JPEG_MAGIC_BYTES_1 = "/9j/";
    private static final String JPEG_MAGIC_BYTES_2 = "/9k/";
    private static final String PNG_MAGIC_BYTES = "iVBORw0KGgoAAAANSUhEUgAA";

    private static final WhatEatException INVALID_FORMAT_EXCEPTION = WhatEatException.builder()
            .code(WhatEatErrorCode.WEV_0006)
            .reason("image", "Image does not seem to be in correct Base64 format.")
            .build();

    static {
        String imageUrlRegex = IMAGE_URL_FORMAT.replace("/", "\\/");
        imageUrlRegex = imageUrlRegex.replace(".", "\\.");
        imageUrlRegex = imageUrlRegex.replace("?", "\\?");
        IMAGE_URL_REGEX = imageUrlRegex.replace("%s", "([\\S]+)");

        // Set up MIMES
        MIMES = Map.of("data:image/jpeg;base64", ".jpeg",
                "data:image/jpg;base64", ".jpg",
                "data:image/png;base64", ".png");
    }

    @Value("${whateat.firebase.storage}")
    private String firebaseStorageUrl;

    @Value("${spring.profiles.active}")
    private String profile;

    private final StorageClient storageClient;

    @Autowired
    public FirebaseImageServiceImpl(StorageClient storageClient) {
        this.storageClient = storageClient;
    }

    @Override
    public FirebaseImageResponse uploadBase64Image(String base64Image) {
        final String[] base64ImageParts = base64Image.split(",");
        if (base64ImageParts.length != 2 || !MIMES.containsKey(base64ImageParts[0])) {
            throw INVALID_FORMAT_EXCEPTION;
        }
        if (base64ImageParts[1].length() > MAX_IMAGE_SIZE) {
            throw WhatEatException.builder()
                    .code(WhatEatErrorCode.WEV_0009)
                    .reason("image", "Image is too big. Must be <= 3MB.")
                    .build();
        }
        final String contentType = base64ImageParts[0].substring(5, base64ImageParts[0].indexOf(";"));
        final String imageExtension = MIMES.get(base64ImageParts[0]);
        checkIfImageHasCorrectMagicBytes(base64ImageParts[1], imageExtension);
        final byte[] imageBytes = Base64.getDecoder().decode(base64ImageParts[1]);
        final UUID imageId = UUID.randomUUID();
        final String imagePath = String.format("%s/%s", profile, imageId);
        storageClient.bucket().create(imagePath, imageBytes, contentType,
                Bucket.BlobTargetOption.doesNotExist());
        return new FirebaseImageResponse(imagePath,
                String.format(IMAGE_URL_FORMAT, firebaseStorageUrl, imagePath.replace("/", "%2F")));
    }

    private void checkIfImageHasCorrectMagicBytes(String base64Image, String imageExtension) {
        if (StringUtils.equals(".jpg", imageExtension) || StringUtils.equals(".jpeg", imageExtension)) {
            if (base64Image.startsWith(JPEG_MAGIC_BYTES_1) || base64Image.startsWith(JPEG_MAGIC_BYTES_2)) {
                return;
            }
            throw INVALID_FORMAT_EXCEPTION;
        }
        if (StringUtils.equals(".png", imageExtension)) {
            if (base64Image.startsWith(PNG_MAGIC_BYTES)) {
                return;
            }
            throw INVALID_FORMAT_EXCEPTION;
        }
        throw INVALID_FORMAT_EXCEPTION;
    }

    /**
     * Removes image. If it is in static folder, skip.
     *
     * @param imageArg   The argument of the image. Supports ID and base URL.
     * @param deleteType Delete type.
     */
    @Override
    public void deleteImage(String imageArg, DeleteType deleteType) {
        if (deleteType == DeleteType.ID) {
            if (imageArg.startsWith(STATIC_FOLDER)) {
                return;
            }
            final Blob blob = storageClient.bucket().get(imageArg);
            if (blob != null) {
                blob.delete();
            }
            return;
        }
        final Pattern pattern = Pattern.compile(IMAGE_URL_REGEX);
        final Matcher matcher = pattern.matcher(imageArg);
        if (matcher.matches()) {
            final String imageId = matcher.group(2);
            if (StringUtils.isBlank(imageId)) {
                // Unknown image ID
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WES_0004)
                        .reason("image_url", "Unknown image URL format.")
                        .build();
            }
            if (imageId.startsWith(STATIC_FOLDER)) {
                return;
            }
            final Blob blob = storageClient.bucket().get(imageId);
            if (blob != null) {
                blob.delete();
            }
        }
    }
}
