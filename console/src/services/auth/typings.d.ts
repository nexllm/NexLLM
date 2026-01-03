declare namespace API {
  type AuthUser = {
    userId?: string;
    username?: string;
    tenantId?: string;
    roles?: string[];
  };

  type LoginRequest = {
    username: string;
    password: string;
  };

  type LoginResponse = {
    accessToken: string;
    refreshToken: string;
    expiresIn: number;
    tokenType: string;
  };

  type RegisterRequest = {
    username: string;
    password: string;
  };

  type RegisterResponse = {
    username: string;
    userId: string;
    password: string;
    status: number;
    tenantId: string;
    createdAt: string;
    updatedAt: string;
  };
}
