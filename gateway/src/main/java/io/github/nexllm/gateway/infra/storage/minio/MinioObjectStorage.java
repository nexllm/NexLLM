package io.github.nexllm.gateway.infra.storage.minio;

import io.github.nexllm.gateway.infra.storage.ObjectStorage;
import java.io.InputStream;

public class MinioObjectStorage implements ObjectStorage {

    @Override
    public String put(String bucket, String objectName, InputStream stream) {
        return "";
    }

    @Override
    public InputStream get(String bucket, String objectName) {
        return null;
    }
}
