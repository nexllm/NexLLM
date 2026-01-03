// @ts-ignore
/* eslint-disable */
import request from '@/utils/request';

/** 此处后端没有提供注释 GET /api/v1/auth/profile */
export async function profile(options?: { [key: string]: any }) {
  return request<API.AuthUser>('/api/v1/auth/profile', {
    method: 'GET',
    ...(options || {}),
  });
}
