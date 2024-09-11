package com.bantads.msauth.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class CriptografiaSenha {

    /* Declaração de variáveis */
    private static final int iterations = 10000;
    private static final int keylength = 256;

    /* Método para gerar o valor do sal */
    public static String getSaltvalue(int length) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /* Método para gerar o valor do hash */
    public static byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keylength);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Erro ao fazer hash da senha: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }

    /* Método para criptografar a senha usando a senha original e o valor do sal */
    public static String generateSecurePassword(String password, String salt) {
        String finalval = null;
        byte[] securePassword = hash(password.toCharArray(), salt.getBytes());
        finalval = Base64.getEncoder().encodeToString(securePassword);
        return finalval;
    }

    /* Método para verificar se as senhas coincidem */
    public static boolean verifyUserPassword(String providedPassword, String securedPassword, String salt) {
        boolean finalval = false;

        /* Gerar nova senha segura com o mesmo sal */
        String newSecurePassword = generateSecurePassword(providedPassword, salt);

        /* Verificar se as duas senhas são iguais */
        finalval = newSecurePassword.equalsIgnoreCase(securedPassword);

        return finalval;
    }
}
