package it.mulders.traqqr.domain.shared;

import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.IntStream;

public class RandomStringUtils {
    private static final Random RANDOM = new SecureRandom();
    private static final char[] ALLOWED_CHARS = new char[] {
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
        'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    private RandomStringUtils() {}

    /**
     * Generate a human-readable random string that contains only letters and digits. This method skips a few
     * letters from the alphanumeric input space:
     * <ul>
     *     <li>to avoid confusion between the number zero and the letter o, the number 0 is not used</li>
     *     <li>to avoid confusion between the casing of letters, only uppercase letters are used</li>
     *     <li>no special characters are used</li>
     * </ul>
     * @param length The length of the generated string.
     * @return A {@link String} of {@code length} characters long.
     */
    public static String generateRandomIdentifier(final int length) {
        if (length > 1024) {
            throw new IllegalArgumentException("Length must be less than or equal to 1024");
        }

        return IntStream.range(0, length)
                .map(i -> RANDOM.nextInt(ALLOWED_CHARS.length))
                .mapToObj(i -> ALLOWED_CHARS[i])
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    /**
     * Generate a human-readable random string that contains only letters and digits. This method skips a few
     * letters from the alphanumeric input space:
     * <ul>
     *     <li>to avoid confusion between the number zero and the letter o, the number 0 is not used</li>
     *     <li>to avoid confusion between the casing of letters, only uppercase letters are used</li>
     *     <li>no special characters are used</li>
     * </ul>
     * @param length The length of the generated string.
     * @return A {@link String} of {@code length} characters long.
     */
    public static String generateRandomAlphaString(final int length) {
        if (length > 1024) {
            throw new IllegalArgumentException("Length must be less than or equal to 1024");
        }

        return IntStream.range(0, length)
                .map(i -> RANDOM.nextInt(ALLOWED_CHARS.length - 9))
                .mapToObj(i -> ALLOWED_CHARS[i])
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
