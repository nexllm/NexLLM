package io.github.nexllm.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ========== Common ==========
    CM_INVALID_PARAM("CM400001",  "Invalid parameter: {0}"),
    CM_MISSING_PARAM("CM400002",  "Missing required parameter: {0}"),
    CM_NOT_FOUND("CM404001",  "{0}[{1}] not found"),
    CM_SYSTEM_ERROR("CM500001",  "System error occurred"),

    // ========== Auth ==========
    AU_INVALID_TOKEN("AU401001",  "Invalid token"),
    AU_INVALID_CREDENTIALS("AU401002",  "Invalid username/password"),
    AU_UNAUTHORIZED("AU401001",  "Unauthorized"),
    AU_FORBIDDEN("AU401002",  "Forbidden"),
    AU_DUPLICATE_USER("AU044090",  "Username[{0}] already exists"),

    // ========== Console ==========
    ADM_PROVIDER_ASSOCIATED_MODEL("ADM044001", 
        "This provider is associated with one or more models. Please remove those models before deleting the provider."),
    ADM_PROVIDER_ASSOCIATED_KEY("ADM044002", 
        "This provider has associated keys. Please delete the keys before proceeding."),
    ADM_CONFLICT("ADM044090",  "{0} already exists"),

    // ========== Gateway ==========
    GW_TOO_MANY_REQUESTS("GW070001",  "Rate limit exceeded"),

    // ========== Connector ==========
    CN_UNSUPPORTED_MODEL("CN400001",  "Unsupported model: {0}"),
    CN_VMODEL_NOT_BIND("CN400010",  "Virtual model: {0} not bind any models"),
    ;

    private final String code;
    private final String message;
}