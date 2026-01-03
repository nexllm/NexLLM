package io.github.nexllm.gateway.infra.storage;

import java.io.InputStream;

public interface ObjectStorage {
    String put(String bucket, String objectName, InputStream stream);
    InputStream get(String bucket, String objectName);
}
