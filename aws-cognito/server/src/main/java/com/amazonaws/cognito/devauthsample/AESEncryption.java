/*
 * Copyright 2010-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazonaws.cognito.devauthsample;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

/**
 * A utility class to encrypt and decrypt strings.
 */
public class AESEncryption {

    /**
     * Encryption algorithm
     */
    public static final String ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * Encrypt a string with a given key.
     *
     * @param clearText string to be encrypted
     * @param key       encryption key
     * @return encrypted string
     */
    public static String wrap(String clearText, String key) {
        byte[] iv = getIv();

        byte[] cipherText = encrypt(clearText, key, iv);
        byte[] wrapped = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, wrapped, 0, iv.length);
        System.arraycopy(cipherText, 0, wrapped, 16, cipherText.length);

        return new String(Base64.encodeBase64(wrapped));
    }

    /**
     * Decrypt a string with given key.
     *
     * @param cipherText encrypted string
     * @param key        the key used in decryption
     * @return a decrypted string
     */
    public static String unwrap(String cipherText, String key) {
        byte[] dataToDecrypt = Base64.decodeBase64(cipherText.getBytes());
        byte[] iv = new byte[16];
        byte[] data = new byte[dataToDecrypt.length - 16];

        System.arraycopy(dataToDecrypt, 0, iv, 0, 16);
        System.arraycopy(dataToDecrypt, 16, data, 0, dataToDecrypt.length - 16);

        byte[] plainText = decrypt(data, key, iv);
        return new String(plainText);
    }

    private static byte[] encrypt(String clearText, String key, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
            params.init(new IvParameterSpec(iv));
            cipher.init(Cipher.ENCRYPT_MODE, getKey(key), params);
            return cipher.doFinal(clearText.getBytes());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Failed to encrypt.", e);
        }
    }

    private static byte[] decrypt(byte[] cipherBytes, String key, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
            params.init(new IvParameterSpec(iv));
            cipher.init(Cipher.DECRYPT_MODE, getKey(key), params);
            return cipher.doFinal(cipherBytes);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Failed to decrypt.", e);
        }
    }

    private static SecretKeySpec getKey(String key) {
        try {
            return new SecretKeySpec(Hex.decodeHex(key.toCharArray()), "AES");
        } catch (DecoderException e) {
            throw new RuntimeException("Failed to generate a secret key spec", e);
        }
    }

    private static byte[] getIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);

        return iv;
    }
}
