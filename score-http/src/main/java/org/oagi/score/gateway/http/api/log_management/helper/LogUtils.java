package org.oagi.score.gateway.http.api.log_management.helper;

import org.apache.commons.codec.binary.Hex;

import java.security.SecureRandom;

public abstract class LogUtils {

    private static class Holder {
        static final SecureRandom numberGenerator = new SecureRandom();
    }

    public static String generateHash() {
        SecureRandom ng = Holder.numberGenerator;

        byte[] randomBytes = new byte[20];
        ng.nextBytes(randomBytes);
        return Hex.encodeHexString(randomBytes);
    }
}
