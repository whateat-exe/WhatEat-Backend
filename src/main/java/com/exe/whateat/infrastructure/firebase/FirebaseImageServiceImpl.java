package com.exe.whateat.infrastructure.firebase;

import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.image.FirebaseImageService;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(rollbackOn = Exception.class)
public class FirebaseImageServiceImpl implements FirebaseImageService {

    private static final Map<String, String> mimes;

    static {
        mimes = Map.of("data:image/jpeg;base64", ".jpeg",
                "data:image/jpg;base64", ".jpg",
                "data:image/png;base64", ".png");
    }

    private final StorageClient storageClient;

    @Autowired
    public FirebaseImageServiceImpl(StorageClient storageClient) {
        this.storageClient = storageClient;
    }

    @Override
    public String uploadBase64Image(String base64Image) {
        final String[] base64ImageParts = base64Image.split(",");
        if (base64ImageParts.length != 2 || !mimes.containsKey(base64ImageParts[0])) {
            throw WhatEatException.builder()
                    .code(WhatEatErrorCode.WEV_0006)
                    .reason("image", "Image must be a valid Base64 representation.")
                    .build();
        }
        final String contentType = base64ImageParts[0].substring(5, base64ImageParts[0].indexOf(";"));
        final byte[] imageBytes = Base64.getDecoder().decode(base64ImageParts[1]);
        final String imageId = UUID.randomUUID().toString();
        Blob blob = storageClient.bucket().create(imageId, imageBytes, contentType,
                Bucket.BlobTargetOption.doesNotExist());
        blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
        return blob.getMediaLink();
    }
}
