package com.example.restapi.service.implementation;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl {
    private final Storage storage;

    @Value("${gcp.storage.bucket}")
    private String bucketName;

    public String uploadFile(String fileName, byte[] content, String contentType) {
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();
        storage.create(blobInfo, content);
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }

    public List<String> listFiles() {
        List<String> fileNames = new ArrayList<>();
        Page<Blob> blobs = storage.list(bucketName);
        for (Blob blob : blobs.iterateAll()) {
            fileNames.add(blob.getName());
        }
        return fileNames;
    }

    public byte[] downloadFile(String fileName) {
        Blob blob = storage.get(BlobId.of(bucketName, fileName));
        return blob.getContent();
    }
}
