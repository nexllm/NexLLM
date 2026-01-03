declare namespace API {
  type AuthUser = {
    userId?: string;
    username?: string;
    tenantId?: string;
    roles?: string[];
  };

  type BatchDeleteRequest = {
    ids: string[];
  };

  type CreateModelRequest = {
    name: string;
    providerId: string;
    enabled: boolean;
    features: (
      | 'CHAT'
      | 'COMPLETION'
      | 'EMBEDDING'
      | 'VISION'
      | 'TOOL_CALLING'
      | 'MULTI_MODAL'
      | 'SPEECH'
      | 'CODE_INTERPRETER'
      | 'STREAMING'
      | 'JSON_MODE'
      | 'SYSTEM_PROMPT'
    )[];
  };

  type CreateProviderKeyRequest = {
    name: string;
    providerId: string;
    priority: number;
    key: string;
    description?: string;
    enabled: boolean;
  };

  type CreateProviderRequest = {
    name: string;
    baseUrl: string;
    description?: string;
    providerType: string;
    extraConfig?: Record<string, any>;
  };

  type CreateVirtualKeyRequest = {
    name: string;
  };

  type CreateVirtualModelRequest = {
    name?: string;
    description?: string;
    modelIds?: string[];
  };

  type getModelsParams = {
    providerId?: string;
    status?: string;
  };

  type getProviderKeysParams = {
    providerId?: string;
  };

  type getProviderKeyValueParams = {
    providerKeyId: string;
  };

  type getProviderModelsParams = {
    providerId: string;
  };

  type getProviderParams = {
    providerId: string;
  };

  type MappedModelResponse = {
    model: ModelResponse;
    priority: number;
    weight: number;
  };

  type ModelResponse = {
    modelId: string;
    provider: ProviderResponse;
    name: string;
    description?: string;
    features: (
      | 'CHAT'
      | 'COMPLETION'
      | 'EMBEDDING'
      | 'VISION'
      | 'TOOL_CALLING'
      | 'MULTI_MODAL'
      | 'SPEECH'
      | 'CODE_INTERPRETER'
      | 'STREAMING'
      | 'JSON_MODE'
      | 'SYSTEM_PROMPT'
    )[];
    contextLength: number;
    status:
      | 'HEALTHY'
      | 'PENDING'
      | 'AUTH_FAILED'
      | 'RATE_LIMITED'
      | 'INVALID_CONFIG'
      | 'UNREACHABLE'
      | 'UNKNOWN_ERROR';
    maxOutputTokens: number;
    enabled: boolean;
    defaultParams?: Record<string, any>;
    createdAt: string;
    updatedAt: string;
  };

  type patchModelParams = {
    modelId: string;
  };

  type PatchModelRequest = {
    name?: string;
    features?: (
      | 'CHAT'
      | 'COMPLETION'
      | 'EMBEDDING'
      | 'VISION'
      | 'TOOL_CALLING'
      | 'MULTI_MODAL'
      | 'SPEECH'
      | 'CODE_INTERPRETER'
      | 'STREAMING'
      | 'JSON_MODE'
      | 'SYSTEM_PROMPT'
    )[];
    enabled?: boolean;
  };

  type patchProviderKeyParams = {
    providerKeyId: string;
  };

  type PatchProviderKeyRequest = {
    name?: string;
    key?: string;
    priority: number;
    description?: string;
    enabled?: boolean;
  };

  type patchVirtualModelParams = {
    virtualModelId: string;
  };

  type PatchVirtualModelRequest = {
    name?: string;
    description?: string;
    modelIds?: string[];
  };

  type ProviderKeyResponse = {
    providerKeyId: string;
    provider: ProviderResponse;
    name: string;
    description?: string;
    priority: number;
    enabled: boolean;
    createdAt: string;
    updatedAt: string;
  };

  type ProviderKeyValueResponse = {
    key: string;
  };

  type ProviderResponse = {
    providerId: string;
    name: string;
    baseUrl: string;
    description?: string;
    providerType: string;
    system: boolean;
    enabled: boolean;
    extraConfig?: Record<string, any>;
    modelCount: number;
    createdAt: string;
    updatedAt: string;
  };

  type TreeNode = {
    title: string;
    value: string;
    key: string;
    selectable: boolean;
    children: any[];
  };

  type VirtualKeyResponse = {
    virtualKeyId: string;
    key: string;
    name: string;
    allowedModels?: string[];
    status: number;
    expireAt?: string;
    createdAt: string;
    updatedAt: string;
  };

  type VirtualModelResponse = {
    name: string;
    virtualModelId: string;
    enabled: boolean;
    description?: string;
    models: MappedModelResponse[];
    createdAt: string;
    updatedAt: string;
  };
}
