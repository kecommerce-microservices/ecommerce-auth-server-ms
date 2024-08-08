package com.kaua.ecommerce.auth.infrastructure.services;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface KeysService {

    void generateAndSaveKeys(String publicKeyName, String privateKeyName);

    String encrypt(String data, String publicKeyName);

    String decrypt(String data, String privateKeyName);

    PublicKey getPublicKey(String publicKeyName);

    PrivateKey getPrivateKey(String privateKeyName);

    KeyPair getKeyPairOrGenerate(String publicKeyName, String privateKeyName);

    boolean keysExists(String publicKeyName, String privateKeyName);
}
