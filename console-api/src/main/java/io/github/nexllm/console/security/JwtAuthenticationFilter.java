package io.github.nexllm.console.security;

import static io.github.nexllm.common.constants.NexLLMConstants.ERROR_URL;

import com.auth0.jwt.exceptions.TokenExpiredException;
import io.github.nexllm.common.constants.ErrorCode;
import io.github.nexllm.common.util.JsonUtils;
import io.github.nexllm.console.auth.config.AuthProperties;
import io.github.nexllm.console.exception.ConsoleHttpErrorMapper;
import io.github.nexllm.security.SecurityConstants;
import io.github.nexllm.security.auth.ConsoleAuthenticationToken;
import io.github.nexllm.security.jwt.JwtClaims;
import io.github.nexllm.security.jwt.JwtUtils;
import io.github.nexllm.security.jwt.StaticKeyProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(AuthProperties authProperties) throws Exception {
        this.jwtUtils = new JwtUtils(
            new StaticKeyProvider(authProperties.jwt().privateKey(), authProperties.jwt().publicKey()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String accessToken = (authHeader != null && authHeader.startsWith("Bearer "))
            ? authHeader.substring(7)
            : null;
        if (accessToken != null) {
            try {
                JwtClaims decodedJWT = jwtUtils.verify(accessToken);
                setAuthentication(decodedJWT);
                filterChain.doFilter(request, response);
            } catch (TokenExpiredException e) {
                String refreshToken = extractRefreshToken(request);
                if (refreshToken != null) {
                    try {
                        JwtClaims claims = jwtUtils.verify(refreshToken);
                        String newAccessToken = jwtUtils.sign(claims, SecurityConstants.ACCESS_TOKEN_DURATION);
                        setAuthentication(jwtUtils.decode(newAccessToken));
                        filterChain.doFilter(request, response);
                        ResponseCookie accessTokenCookie = ResponseCookie.from(SecurityConstants.ACCESS_TOKEN,
                                newAccessToken)
                            .httpOnly(true)
                            .secure(true)
                            .path("/")
                            .maxAge(SecurityConstants.ACCESS_TOKEN_DURATION)
                            .sameSite("Strict")
                            .build();
                        response.addCookie(new Cookie(SecurityConstants.ACCESS_TOKEN, accessTokenCookie.getValue()));
                    } catch (Exception ex) {
                        handleUnauthorizedError("invalid refresh token", request, response);
                    }
                } else {
                    handleUnauthorizedError("token expired", request, response);
                }
            }
        } else {
            handleUnauthorizedError("Unauthorized", request, response);
        }
    }

    private static void handleUnauthorizedError(String message, HttpServletRequest request,
        HttpServletResponse response)
        throws IOException {
        ErrorCode errorCode = ErrorCode.AU_INVALID_TOKEN;
        HttpStatus httpStatus = ConsoleHttpErrorMapper.map(errorCode);
        ProblemDetail problemDetail = ProblemDetail.forStatus(httpStatus);
        problemDetail.setTitle("Invalid credentials");
        problemDetail.setDetail(message);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setType(URI.create(ERROR_URL));
        problemDetail.setProperty("error_code", errorCode.getCode());
        response.setStatus(httpStatus.value());
        response.setContentType("application/json");
        response.getWriter().write(JsonUtils.toJsonWithoutNull(problemDetail));
    }

    private String extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (SecurityConstants.REFRESH_TOKEN.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void setAuthentication(JwtClaims claims) {
        ConsoleAuthenticationToken auth = new ConsoleAuthenticationToken(claims);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

}
