package it.mulders.traqqr.domain.shared;

import jakarta.enterprise.context.ApplicationScoped;
import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.IntStream;

@ApplicationScoped
public class RandomStringUtils {
    private static final Random RANDOM = new SecureRandom();
    private static final char[] ALLOWED_CHARS = new char[] {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
        'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    /**
     * Generate a human-readable random string that contains only certain characters. This method skips a few
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
        return IntStream.range(0, length)
                .map(i -> RANDOM.nextInt(ALLOWED_CHARS.length))
                .mapToObj(i -> ALLOWED_CHARS[i])
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString()
                .toLowerCase();
    }
}
