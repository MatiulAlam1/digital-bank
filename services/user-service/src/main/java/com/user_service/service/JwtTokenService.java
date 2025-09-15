package com.user_service.service;

import com.user_service.config.CustomJwtAuthenticationFilter;
import com.user_service.config.UserSecretsManager;
import com.user_service.enums.TokenType;
import com.user_service.exceptions.InvalidJwtToken;
import com.user_service.utils.ApplicationConstants;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * JwtTokenService class for handling JWT token operations such as generation
 * and validation.
 * <p>
 * This class provides functionality for:
 * <ul>
 * <li>Generating JWT tokens (access and refresh tokens).</li>
 * <li>Validating refresh tokens to ensure they are valid and not expired.</li>
 * </ul>
 *
 * @author BJIT
 * @version 1.0
 */
@Service
@Slf4j
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final UserSecretsManager userSecretsManager;

    /**
     * Constructor for initializing the JWT encoder and decoder.
     *
     * @param jwtEncoder         the {@link JwtEncoder} responsible for encoding JWT tokens
     * @param jwtDecoder         the {@link JwtDecoder} responsible for decoding and validating JWT tokens
     * @param userSecretsManager the {@link UserSecretsManager} responsible for managing secrets
     */
    public JwtTokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, UserSecretsManager userSecretsManager) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.userSecretsManager = userSecretsManager;
    }

    public String generateToken(String userName, int expireTime, String tokenIssuer, TokenType tokenType,
                                int rsaKeyVersion, String scope, Collection<String> authorities) {
        log.info("Token expire time in minutes: {}, token type: {}, scope {}", expireTime, tokenType, scope);
        // Generate JWT claims
        Instant now = Instant.now();
        var claims = JwtClaimsSet.builder()
                .issuer(tokenIssuer)
                .issuedAt(now)
                .expiresAt(now.plus(expireTime, ChronoUnit.MINUTES))
                .subject(userName)
                .claim("authorities", authorities) // Add authorities claim
                .claim(ApplicationConstants.TOKEN_TYPE, tokenType).claim(ApplicationConstants.RSA_KEY_VERSION, rsaKeyVersion)
                .claim("scope", scope).build();

        // Encode and return the JWT token

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    /**
     * Validates a refresh token to ensure it is valid and not expired.
     *
     * @param refreshToken the JWT refresh token to validate
     * @return an {@link Authentication} object containing the decoded JWT token
     * @throws JwtException    if the token is invalid, expired, or not a refresh token
     * @throws InvalidJwtToken if the token is invalid or missing
     * @throws IOException     if an I/O error occurs
     */
    public Authentication validateRefreshToken(String refreshToken) throws JwtException, InvalidJwtToken, IOException {
        try {
            Optional<Integer> optionalVersion = Optional.ofNullable(userSecretsManager.getVaultKeyVersionFromToken(refreshToken));
            optionalVersion.ifPresent(version -> {
                log.info("RSA key version in JWT Token Service: {}", version);
                HttpServletRequest request = getCurrentRequest();
                if (request != null) {
                    setRequestAttributes(request, version);
                }
            });

            // Decode the JWT token
            Jwt decodedJwt = jwtDecoder.decode(refreshToken);
            validateTokenClaims(decodedJwt);

            // Return a JwtAuthenticationToken containing the decoded JWT
            Authentication authentication = new JwtAuthenticationToken(decodedJwt);
            log.info("Validated refresh token successfully");
            return authentication;
        } catch (JwtException e) {
            handleJwtException(e);
            return null;
        }
    }

    /**
     * Retrieves the current HTTP request.
     *
     * @return the current {@link HttpServletRequest}, or null if not available
     */
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * Sets request attributes for the given RSA key version.
     *
     * @param request the current {@link HttpServletRequest}
     * @param version the RSA key version
     */
    private void setRequestAttributes(HttpServletRequest request, Integer version) {
        RSAPublicKey publicKey = userSecretsManager.fetchPublicKey(version);
        if (publicKey != null) {
            String requestId = UUID.randomUUID().toString();
            log.info("Setting request attributes with request ID: {} and RSA key version: {}", requestId, version);
            request.setAttribute(ApplicationConstants.REQUEST_ID, requestId);
            CustomJwtAuthenticationFilter.getMapRsaPublicKey().put(version, publicKey);
            CustomJwtAuthenticationFilter.getMapVersion().put(requestId, version);
        } else {
            log.warn("Public key not found for version: {}", version);
        }
    }

    /**
     * Validates the claims of a decoded JWT token.
     *
     * @param decodedJwt the decoded {@link Jwt} token
     * @throws InvalidJwtToken if the token is invalid or missing required claims
     */
    private void validateTokenClaims(Jwt decodedJwt) throws InvalidJwtToken {
        String tokenType = decodedJwt.getClaim(ApplicationConstants.TOKEN_TYPE);
        log.info("Validating token claims, token type: {}", tokenType);
        Instant expiration = decodedJwt.getExpiresAt();

        if (!TokenType.REFRESH.name().equals(tokenType) || expiration == null) {
            log.error("Invalid or missing token claims");
            throw new InvalidJwtToken("Invalid or missing token.");
        }

        log.info("Token type: {}", tokenType);
    }

    /**
     * Handles exceptions related to JWT validation.
     *
     * @param e the {@link JwtException} to handle
     * @throws JwtException if the token is expired or invalid
     */
    private void handleJwtException(JwtException e) throws JwtException {
        String errMsg = e.getMessage();
        if (errMsg.contains("Jwt expired")) {
            log.error("Refresh token has expired: {}", errMsg);
            throw new JwtException("Refresh token has expired");
        } else {
            log.error("Invalid refresh token: {}", errMsg);
            throw new JwtException("Invalid refresh token", e);
        }
    }
}


