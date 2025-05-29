package io.github.helpdesk.util;

import java.security.SecureRandom;

import static java.util.stream.IntStream.range;

public final class OtpUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String generateOtp(final Integer length) {
        StringBuilder otp = new StringBuilder();

        range(0, length).mapToObj(i -> SECURE_RANDOM.nextInt(10)).forEach(otp::append);

        return otp.toString();
    }

}
