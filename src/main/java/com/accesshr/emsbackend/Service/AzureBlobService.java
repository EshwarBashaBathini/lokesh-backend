package com.accesshr.emsbackend.Service;

import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class AzureBlobService {

    private final BlobContainerClient containerClient;

    @Value("${azure.storage.container-name}")
    private String containerName;

    public AzureBlobService(@Value("${azure.storage.connection-string}") String connectionString) {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
        this.containerClient = blobServiceClient.getBlobContainerClient(containerName);
    }

    public String uploadFileToBlob(MultipartFile file, String fileType) throws IOException {
        String fileName = fileType + "-" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();

        BlobClientBuilder blobClientBuilder = new BlobClientBuilder()
                .containerName(containerName)
                .blobName(fileName)
                .endpoint(containerClient.getBlobContainerUrl());

        blobClientBuilder.buildClient().upload(inputStream, file.getSize(), true);

        return blobClientBuilder.buildClient().getBlobUrl(); // Returns the URL of the uploaded file
    }

    public String uploadOptionalFile(MultipartFile file, String fileType) throws IOException {
        if (file != null && !file.isEmpty()) {
            return uploadFileToBlob(file, fileType); // Call the existing upload method
        }
        return null; // Return null or handle as needed if the file is not provided
    }

    public long getFileSize(String fileName) {
        return containerClient.getBlobClient(fileName).getProperties().getBlobSize();
    }
}
