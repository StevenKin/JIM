package com.github.stevenkin.jim.gateway.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class KeyPairGenUtil {
    private static final int KEY_SIZE = 2048;
    private static Map<Integer, String> keyMap = new HashMap();

    public KeyPairGenUtil() {
    }

    public static Map<Integer, String> genKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
        String publicKeyString = java.util.Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyString = java.util.Base64.getEncoder().encodeToString(privateKey.getEncoded());
        keyMap.put(0, publicKeyString);
        keyMap.put(1, privateKeyString);
        log.info("server public key {}", publicKeyString);
        log.info("server private key {}", privateKeyString);
        return new HashMap<>(keyMap);
    }

    public static String encrypt(String str, String publicKey) throws Exception {
        byte[] decoded = java.util.Base64.getDecoder().decode(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(1, pubKey);
        String outStr = java.util.Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }

    public static String decrypt(String str, String privateKey) throws Exception {
        byte[] inputByte = java.util.Base64.getDecoder().decode(str);
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey)KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(2, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }

    public static void main(String[] args) throws Exception {
        long temp = System.currentTimeMillis();
        genKeyPair();
        System.out.println("??????:" + (String)keyMap.get(0));
        System.out.println("??????:" + (String)keyMap.get(1));
        System.out.println("????????????????????????:" + (double)(System.currentTimeMillis() - temp) / 1000.0D + "???");
        String message = "RSA??????ABCD~!@#$";
        System.out.println("??????:" + message);
        temp = System.currentTimeMillis();
        String messageEn = encrypt(message, (String)keyMap.get(0));
        System.out.println("??????:" + messageEn);
        System.out.println("??????????????????:" + (double)(System.currentTimeMillis() - temp) / 1000.0D + "???");
        temp = System.currentTimeMillis();
        String messageDe = decrypt(messageEn, (String)keyMap.get(1));
        System.out.println("??????:" + messageDe);
        System.out.println("??????????????????:" + (double)(System.currentTimeMillis() - temp) / 1000.0D + "???");
    }

    public static String getPublicKey() {
        return keyMap.get(0);
    }

    public static String getPrivateKey() {
        return keyMap.get(1);
    }
}

