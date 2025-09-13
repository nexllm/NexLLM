package io.github.nexllm.admin.model.request;

import io.github.nexllm.common.constants.ModelFeature;
import java.util.List;

public record PatchModelRequest(
    String name,
    List<ModelFeature> features,
    Boolean enabled
    ) {

}
