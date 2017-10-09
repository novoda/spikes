package com.novoda.seatmonitor;

import android.content.res.Resources;
import android.util.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

class CloudIotCorePasswordGenerator {

    private final String projectId;
    private final Resources resources;
    private final int privateKeyRawFileId;

    CloudIotCorePasswordGenerator(String projectId, Resources resources, int privateKeyRawFileId) {
        this.projectId = projectId;
        this.resources = resources;
        this.privateKeyRawFileId = privateKeyRawFileId;
    }

    char[] createJwtRsaPassword() {
        try {
            byte[] privateKeyBytes = decodePrivateKey(resources, privateKeyRawFileId);
            return createJwtRsaPassword(projectId, privateKeyBytes).toCharArray();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algorithm not supported. (developer error)", e);
        } catch (InvalidKeySpecException e) {
            throw new IllegalStateException("Invalid Key spec. (developer error)", e);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read private key file.", e);
        }
    }

    private static byte[] decodePrivateKey(Resources resources, int privateKeyRawFileId) throws IOException {
        try (InputStream inStream = resources.openRawResource(privateKeyRawFileId)) {
            return Base64.decode(inputToString(inStream), Base64.DEFAULT);
        }
    }

    private static String inputToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private static String createJwtRsaPassword(String projectId, byte[] privateKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return createPassword(projectId, privateKeyBytes, SignatureAlgorithm.RS256, "RSA"); // for ES256 just change the enum
    }

    private static String createPassword(String projectId, byte[] privateKeyBytes, SignatureAlgorithm signatureAlgorithm, String algorithmName) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Instant now = Instant.now();
        Date issueDate = Date.from(now);
        JwtBuilder jwtBuilder =
                Jwts.builder()
                        .setIssuedAt(issueDate)
                        // The device will be disconnected after the token expires, and will have to reconnect with a new token.
                        .setExpiration(Date.from(now.plus(Duration.ofMinutes(20))))
                        // The audience field should always be set to the GCP project id.
                        .setAudience(projectId);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory kf = KeyFactory.getInstance(algorithmName);

        return jwtBuilder.signWith(signatureAlgorithm, kf.generatePrivate(spec)).compact();
    }

}
