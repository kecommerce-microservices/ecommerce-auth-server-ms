package com.kaua.ecommerce.auth.infrastructure.services.local;

import com.kaua.ecommerce.auth.infrastructure.services.KeysService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAKeyLocalGeneratorService implements KeysService {

    private static final Logger log = LoggerFactory.getLogger(RSAKeyLocalGeneratorService.class);

    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;
    private static final String KEYS_FOLDER = "keys";

    @Override
    public void generateAndSaveKeys(final String publicKeyName, final String privateKeyName) {
        if (keysExists(publicKeyName, privateKeyName)) {
            return;
        }

        final var keyPair = generateKey();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        File keysFolder = new File(KEYS_FOLDER);
        if (!keysFolder.exists()) {
            log.debug("Creating keys folder");
            final var aOutput = keysFolder.mkdirs();
            if (!aOutput) {
                throw new RuntimeException("Could not create keys folder");
            }
            log.info("Keys folder created: {}", keysFolder.getAbsolutePath());
        }

        // Save public key
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
        try (FileOutputStream fos = new FileOutputStream(new File(keysFolder, publicKeyName))) {
            log.debug("Saving public key");
            fos.write(Base64.getEncoder().encode(x509EncodedKeySpec.getEncoded()));
            log.info("Public key saved: {}", publicKeyName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Save private key
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        try (FileOutputStream fos = new FileOutputStream(new File(keysFolder, privateKeyName))) {
            log.debug("Saving private key");
            fos.write(Base64.getEncoder().encode(pkcs8EncodedKeySpec.getEncoded()));
            log.info("Private key saved: {}", privateKeyName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("RSA keys generated and saved successfully");
    }

    @Override
    public String encrypt(final String data, final String publicKey) {
        try {
            final var key = getPublicKey(publicKey);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String decrypt(final String data, final String privateKey) {
        try {
            final var key = getPrivateKey(privateKey);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(data));
            return new String(decryptedBytes);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PublicKey getPublicKey(final String publicKeyName) {
        try {
            byte[] keyBytes = Base64.getDecoder()
                    .decode(Files.readAllBytes(Paths.get(KEYS_FOLDER, publicKeyName)));
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory.generatePublic(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PrivateKey getPrivateKey(final String privateKeyName) {
        try {
            byte[] keyBytes = Base64.getDecoder()
                    .decode(Files.readAllBytes(Paths.get(KEYS_FOLDER, privateKeyName)));
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory.generatePrivate(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public KeyPair getKeyPairOrGenerate(final String publicKeyName, final String privateKeyName) {
        if (!keysExists(publicKeyName, privateKeyName)) {
            generateAndSaveKeys(publicKeyName, privateKeyName);
            return new KeyPair(getPublicKey(publicKeyName), getPrivateKey(privateKeyName));
        }
        return new KeyPair(getPublicKey(publicKeyName), getPrivateKey(privateKeyName));
    }

    private KeyPair generateKey() {
        log.info("Generating RSA keys");
        KeyPair keyPair;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyGen.initialize(KEY_SIZE);
            keyPair = keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        log.info("RSA keys generated successfully");
        return keyPair;
    }

    @Override
    public boolean keysExists(
            final String publicKeyName,
            final String privateKeyName
    ) {
        final File publicKeyFile = new File(KEYS_FOLDER, publicKeyName);
        final File privateKeyFile = new File(KEYS_FOLDER, privateKeyName);

        if (publicKeyFile.exists() && privateKeyFile.exists()) {
            log.info("RSA keys [public:{}] [private:{}] already exists", publicKeyName, privateKeyName);
            return true;
        }

        return false;
    }
}
