package com.javaquery.spring.aws;

import com.javaquery.util.Is;
import com.javaquery.util.io.Files;
import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

/**
 * @author vicky.thakor
 * @since 2026-01-01
 */
@Component
public class S3Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(S3Service.class);

    @Value("${aws.s3.region:us-east-1}")
    private String defaultRegion;

    @Value("${aws.signature.duration:5}")
    private int signatureDuration;

    private final AwsCredentialsProvider awsCredentialsProvider;

    public S3Service(AwsCredentialsProvider awsCredentialsProvider) {
        this.awsCredentialsProvider = awsCredentialsProvider;
    }

    /**
     * Generate a presigned URL for uploading an object to S3 using the default region.
     *
     * @param bucketName The name of the S3 bucket.
     * @param objectKey  The key (path) of the object to be uploaded.
     * @return A presigned URL as a String.
     */
    public String getPutPresignedUrl(String bucketName, String objectKey) {
        return getPutPresignedUrl(Region.of(defaultRegion), bucketName, objectKey);
    }

    /**
     * Generate a presigned URL for uploading an object to S3.
     *
     * @param region     The AWS region where the S3 bucket is located.
     * @param bucketName The name of the S3 bucket.
     * @param objectKey  The key (path) of the object to be uploaded.
     * @return A presigned URL as a String.
     */
    public String getPutPresignedUrl(Region region, String bucketName, String objectKey) {
        try (S3Presigner preSigner = S3Presigner.builder()
                .region(region)
                .credentialsProvider(awsCredentialsProvider)
                .build()) {
            PutObjectRequest putObjectRequest =
                    PutObjectRequest.builder().bucket(bucketName).key(objectKey).build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .putObjectRequest(putObjectRequest)
                    .signatureDuration(Duration.ofMinutes(signatureDuration))
                    .build();

            PresignedPutObjectRequest presignedRequest = preSigner.presignPutObject(presignRequest);
            return presignedRequest.url().toString();
        }
    }

    /**
     * Generate a presigned URL for downloading an object from S3 using the default region.
     *
     * @param bucketName The name of the S3 bucket.
     * @param objectKey  The key (path) of the object to be downloaded.
     * @return A presigned URL as a String.
     */
    public String getGetPresignedUrl(String bucketName, String objectKey) {
        return getGetPresignedUrl(Region.of(defaultRegion), bucketName, objectKey);
    }

    /**
     * Generate a presigned URL for downloading an object from S3.
     *
     * @param region     The AWS region where the S3 bucket is located.
     * @param bucketName The name of the S3 bucket.
     * @param objectKey  The key (path) of the object to be downloaded.
     * @return A presigned URL as a String.
     */
    public String getGetPresignedUrl(Region region, String bucketName, String objectKey) {
        try (S3Presigner preSigner = S3Presigner.builder()
                .region(region)
                .credentialsProvider(awsCredentialsProvider)
                .build()) {
            GetObjectRequest getObjectRequest =
                    GetObjectRequest.builder().bucket(bucketName).key(objectKey).build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .getObjectRequest(getObjectRequest)
                    .signatureDuration(Duration.ofMinutes(signatureDuration))
                    .build();

            PresignedGetObjectRequest presignedRequest = preSigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        }
    }

    /**
     * Upload an object to S3 and return a presigned URL for downloading it.
     *
     * @param uploadObject The S3UploadObject containing upload details.
     * @return A presigned URL as a String.
     */
    public String uploadObject(S3UploadObject uploadObject) {
        try (S3AsyncClient s3AsyncClient = S3AsyncClient.builder()
                .region(uploadObject.getRegion())
                .credentialsProvider(awsCredentialsProvider)
                .build()) {

            String contentType = null;
            if (Is.nonNullNonEmpty(uploadObject.getMetadata())) {
                contentType = uploadObject.getMetadata().get("Content-Type");
                if (Is.nullOrEmpty(contentType)) {
                    contentType = Files.determineContentType(uploadObject.getSource());
                }
            }

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(uploadObject.getBucketName())
                    .key(uploadObject.getDestination())
                    .contentType(contentType)
                    .metadata(uploadObject.getMetadata())
                    .build();

            CompletableFuture<PutObjectResponse> response = s3AsyncClient.putObject(
                    putObjectRequest, Paths.get(uploadObject.getSource().getAbsolutePath()));

            // Wait for upload to finish
            response.join();

            if (Is.nonNullNonEmpty(uploadObject.getTags())) {
                Tagging tagging = Tagging.builder()
                        .tagSet(uploadObject.getTags().entrySet().stream()
                                .map(entry -> Tag.builder()
                                        .key(entry.getKey())
                                        .value(entry.getValue())
                                        .build())
                                .collect(Collectors.toList()))
                        .build();
                PutObjectTaggingRequest taggingRequest = PutObjectTaggingRequest.builder()
                        .bucket(uploadObject.getBucketName())
                        .key(uploadObject.getDestination())
                        .tagging(tagging)
                        .build();
                s3AsyncClient.putObjectTagging(taggingRequest).join();
            }
            return getGetPresignedUrl(
                    uploadObject.getRegion(), uploadObject.getBucketName(), uploadObject.getDestination());
        }
    }

    /**
     * Download an object from S3 to a temporary file using the default region.
     *
     * @param bucketName The name of the S3 bucket.
     * @param objectKey  The key (path) of the object to be downloaded.
     * @return The file path of the downloaded object.
     */
    public String downloadObject(String bucketName, String objectKey) {
        return downloadObject(Region.of(defaultRegion), bucketName, objectKey);
    }

    /**
     * Download an object from S3 to a temporary file.
     *
     * @param region     The AWS region where the S3 bucket is located.
     * @param bucketName The name of the S3 bucket.
     * @param objectKey  The key (path) of the object to be downloaded.
     * @return The file path of the downloaded object.
     */
    public String downloadObject(Region region, String bucketName, String objectKey) {
        try (S3AsyncClient s3AsyncClient = S3AsyncClient.builder()
                .region(region)
                .credentialsProvider(awsCredentialsProvider)
                .build()) {

            String downloadFilePath = Files.SYSTEM_TMP_DIR + File.separator + objectKey;
            GetObjectRequest getObjectRequest =
                    GetObjectRequest.builder().bucket(bucketName).key(objectKey).build();

            CompletableFuture<GetObjectResponse> response =
                    s3AsyncClient.getObject(getObjectRequest, Paths.get(downloadFilePath));

            response.join();
            return downloadFilePath;
        }
    }

    /**
     * Delete a single object from an S3 bucket using the default region.
     *
     * @param bucketName The name of the S3 bucket.
     * @param objectKey  The key (path) of the object to be deleted.
     */
    public void deleteObject(String bucketName, String objectKey) {
        deleteObjects(Region.of(defaultRegion), bucketName, List.of(objectKey));
    }

    /**
     * Delete single/multiple objects from an S3 bucket.
     *
     * @param region      The AWS region where the S3 bucket is located.
     * @param bucketName  The name of the S3 bucket.
     * @param objectKeys  An iterable of object keys (paths) to be deleted.
     */
    public void deleteObjects(Region region, String bucketName, Iterable<String> objectKeys) {
        try (S3AsyncClient s3AsyncClient = S3AsyncClient.builder()
                .region(region)
                .credentialsProvider(awsCredentialsProvider)
                .build()) {

            List<ObjectIdentifier> objectIdentifiers = new ArrayList<>();
            for (String objectKey : objectKeys) {
                objectIdentifiers.add(ObjectIdentifier.builder().key(objectKey).build());
            }

            Delete delete = Delete.builder().objects(objectIdentifiers).build();

            DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(delete)
                    .build();

            CompletableFuture<DeleteObjectsResponse> response = s3AsyncClient.deleteObjects(deleteObjectsRequest);

            response.whenComplete((deleteObjectsResponse, ex) -> {
                if (deleteObjectsResponse == null) {
                    LOGGER.error(ex.getMessage(), ex);
                } else {
                    List<S3Error> errors = deleteObjectsResponse.errors();
                    if (Is.nonNullNonEmpty(errors)) {
                        for (S3Error error : errors) {
                            LOGGER.error(
                                    "Failed to delete object: {} - Code: {}, Message: {}",
                                    error.key(),
                                    error.code(),
                                    error.message());
                        }
                    }
                }
            });
            response.thenApply(r -> null);
        }
    }

    /**
     * Copy an object from one S3 location to another.
     *
     * @param region                 The AWS region where the S3 buckets are located.
     * @param sourceBucketName       The name of the source S3 bucket.
     * @param sourceKey              The key (path) of the source object.
     * @param destinationBucketName  The name of the destination S3 bucket.
     * @param destinationKey         The key (path) for the copied object.
     * @return A CompletableFuture containing the copy result as a String.
     */
    public CompletableFuture<String> copyObjectAsync(
            Region region,
            String sourceBucketName,
            String sourceKey,
            String destinationBucketName,
            String destinationKey) {
        try (S3AsyncClient s3AsyncClient = S3AsyncClient.builder()
                .region(region)
                .credentialsProvider(awsCredentialsProvider)
                .build()) {

            CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                    .sourceBucket(sourceBucketName)
                    .sourceKey(sourceKey)
                    .destinationBucket(destinationBucketName)
                    .destinationKey(destinationKey)
                    .build();

            CompletableFuture<CopyObjectResponse> response = s3AsyncClient.copyObject(copyObjectRequest);

            response.whenComplete((copyObjectResponse, ex) -> {
                if (copyObjectResponse == null) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            });
            return response.thenApply(CopyObjectResponse::copyObjectResult).thenApply(Object::toString);
        }
    }

    /**
     * Copy an object within the same S3 bucket.
     *
     * @param region            The AWS region where the S3 bucket is located.
     * @param sourceBucketName  The name of the S3 bucket.
     * @param sourceKey         The key (path) of the source object.
     * @param destinationKey    The key (path) for the copied object.
     * @return A CompletableFuture containing the copy result as a String.
     */
    public CompletableFuture<String> copyObjectAsync(
            Region region, String sourceBucketName, String sourceKey, String destinationKey) {
        return copyObjectAsync(region, sourceBucketName, sourceKey, sourceBucketName, destinationKey);
    }

    /**
     * Move an object from one S3 location to another by copying and then deleting the source object.
     *
     * @param region                 The AWS region where the S3 buckets are located.
     * @param sourceBucketName       The name of the source S3 bucket.
     * @param sourceKey              The key (path) of the source object.
     * @param destinationBucketName  The name of the destination S3 bucket.
     * @param destinationKey         The key (path) for the moved object.
     */
    public void moveObject(
            Region region,
            String sourceBucketName,
            String sourceKey,
            String destinationBucketName,
            String destinationKey) {
        CompletableFuture<String> response =
                copyObjectAsync(region, sourceBucketName, sourceKey, destinationBucketName, destinationKey);
        response.whenComplete((copyRes, ex) -> {
            if (copyRes != null) {
                deleteObjects(region, sourceBucketName, List.of(sourceKey));
            } else {
                LOGGER.error(ex.getMessage(), ex);
            }
        });
        response.join();
    }

    /**
     * S3 Upload Object details.
     */
    @Getter
    @Builder
    public static class S3UploadObject {
        @Builder.Default
        private Region region = Region.US_EAST_1;

        private String bucketName;
        private String destination;
        private File source;
        private Map<String, String> tags;
        private Map<String, String> metadata;
    }
}
