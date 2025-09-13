// @ts-ignore
/* eslint-disable */
import request from '@/utils/request';

/** 此处后端没有提供注释 GET /api/v1/providers */
export async function getProviders(options?: { [key: string]: any }) {
  return request<API.ProviderResponse[]>('/api/v1/providers', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /api/v1/providers */
export async function createProvider(
  body: API.CreateProviderRequest,
  options?: { [key: string]: any },
) {
  return request<API.ProviderResponse>('/api/v1/providers', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 DELETE /api/v1/providers */
export async function batchDeleteProvider(
  body: API.BatchDeleteRequest,
  options?: { [key: string]: any },
) {
  return request<any>('/api/v1/providers', {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /api/v1/providers/${param0} */
export async function getProvider(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getProviderParams,
  options?: { [key: string]: any },
) {
  const { providerId: param0, ...queryParams } = params;
  return request<API.ProviderResponse>(`/api/v1/providers/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /api/v1/providers/${param0}/models */
export async function getProviderModels(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getProviderModelsParams,
  options?: { [key: string]: any },
) {
  const { providerId: param0, ...queryParams } = params;
  return request<API.ModelResponse[]>(`/api/v1/providers/${param0}/models`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /api/v1/providers/tree */
export async function getProviderTree(options?: { [key: string]: any }) {
  return request<API.TreeNode[]>('/api/v1/providers/tree', {
    method: 'GET',
    ...(options || {}),
  });
}
