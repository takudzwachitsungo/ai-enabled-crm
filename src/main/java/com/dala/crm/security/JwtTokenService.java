package com.dala.crm.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * Issues and validates tenant-aware access tokens for browser sessions.
 */
@Component
public class JwtTokenService {

    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();

    private final ObjectMapper objectMapper;
    private final byte[] signingKey;
    private final long accessTokenTtlMinutes;

    public JwtTokenService(
            ObjectMapper objectMapper,
            @Value("${app.security.jwt.secret}") String jwtSecret,
            @Value("${app.security.jwt.access-token-ttl-minutes}") long accessTokenTtlMinutes
    ) {
        this.objectMapper = objectMapper;
        this.signingKey = jwtSecret.getBytes(StandardCharsets.UTF_8);
        this.accessTokenTtlMinutes = accessTokenTtlMinutes;
    }

    public JwtToken issueAccessToken(TenantUserPrincipal principal) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(accessTokenTtlMinutes * 60);

        Map<String, Object> header = Map.of(
                "alg", "HS256",
                "typ", "JWT"
        );

        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("sub", principal.getEmail());
        claims.put("uid", principal.getUserId());
        claims.put("tenantId", principal.getTenantId());
        claims.put("tenantName", principal.getTenantName());
        claims.put("fullName", principal.getFullName());
        claims.put(
                "authorities",
                principal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .sorted()
                        .toList()
        );
        claims.put("iat", issuedAt.getEpochSecond());
        claims.put("exp", expiresAt.getEpochSecond());

        String encodedHeader = encodeJson(header);
        String encodedClaims = encodeJson(claims);
        String signature = sign(encodedHeader + "." + encodedClaims);
        return new JwtToken(encodedHeader + "." + encodedClaims + "." + signature, expiresAt);
    }

    public JwtClaims parseAccessToken(String token) {
        String[] segments = token.split("\\.");
        if (segments.length != 3) {
            throw new JwtAuthenticationException("Invalid access token.");
        }

        String signingInput = segments[0] + "." + segments[1];
        String expectedSignature = sign(signingInput);
        if (!MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                segments[2].getBytes(StandardCharsets.UTF_8)
        )) {
            throw new JwtAuthenticationException("Invalid access token signature.");
        }

        Map<String, Object> claims = decodeJson(segments[1]);
        String email = readStringClaim(claims, "sub");
        String tenantId = readStringClaim(claims, "tenantId");
        String tenantName = readStringClaim(claims, "tenantName");
        String fullName = readStringClaim(claims, "fullName");
        Long userId = readLongClaim(claims, "uid");
        Instant issuedAt = Instant.ofEpochSecond(readLongClaim(claims, "iat"));
        Instant expiresAt = Instant.ofEpochSecond(readLongClaim(claims, "exp"));
        if (expiresAt.isBefore(Instant.now())) {
            throw new JwtAuthenticationException("Access token has expired.");
        }

        Object rawAuthorities = claims.get("authorities");
        if (!(rawAuthorities instanceof List<?> items)) {
            throw new JwtAuthenticationException("Access token authorities are invalid.");
        }
        List<String> authorities = items.stream()
                .map(String::valueOf)
                .toList();

        return new JwtClaims(userId, tenantId, tenantName, fullName, email, authorities, issuedAt, expiresAt);
    }

    private String encodeJson(Map<String, Object> value) {
        try {
            byte[] jsonBytes = objectMapper.writeValueAsBytes(value);
            return URL_ENCODER.encodeToString(jsonBytes);
        } catch (Exception ex) {
            throw new JwtAuthenticationException("Unable to issue access token.");
        }
    }

    private Map<String, Object> decodeJson(String segment) {
        try {
            byte[] jsonBytes = URL_DECODER.decode(segment);
            return objectMapper.readValue(jsonBytes, new TypeReference<>() {
            });
        } catch (Exception ex) {
            throw new JwtAuthenticationException("Invalid access token payload.");
        }
    }

    private String sign(String signingInput) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(signingKey, "HmacSHA256"));
            byte[] signatureBytes = mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8));
            return URL_ENCODER.encodeToString(signatureBytes);
        } catch (Exception ex) {
            throw new JwtAuthenticationException("Unable to sign access token.");
        }
    }

    private String readStringClaim(Map<String, Object> claims, String key) {
        Object value = claims.get(key);
        if (!(value instanceof String stringValue) || stringValue.isBlank()) {
            throw new JwtAuthenticationException("Access token claim '" + key + "' is invalid.");
        }
        return stringValue;
    }

    private Long readLongClaim(Map<String, Object> claims, String key) {
        Object value = claims.get(key);
        if (value instanceof Number numberValue) {
            return numberValue.longValue();
        }
        if (value instanceof String stringValue) {
            try {
                return Long.parseLong(stringValue);
            } catch (NumberFormatException ex) {
                throw new JwtAuthenticationException("Access token claim '" + key + "' is invalid.");
            }
        }
        throw new JwtAuthenticationException("Access token claim '" + key + "' is invalid.");
    }

    public record JwtToken(String value, Instant expiresAt) {
    }

    public record JwtClaims(
            Long userId,
            String tenantId,
            String tenantName,
            String fullName,
            String email,
            List<String> authorities,
            Instant issuedAt,
            Instant expiresAt
    ) {
    }
}
