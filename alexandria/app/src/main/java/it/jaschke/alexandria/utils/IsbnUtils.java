package it.jaschke.alexandria.utils;

/**
 * Created by Paulina on 2015-10-29.
 */
public class IsbnUtils {

    public static String convertToISBN13(String isbn10) {
        String isbn13 = "978" + isbn10;
        char[] array = isbn13.toCharArray();
        int checksum = 0;

        for (int i = 0; i < array.length - 1; i++) {
            int value = Character.getNumericValue(array[i]);
            if (i % 2 == 0) {
                checksum += value;
            } else
                checksum += 3 * value;
        }

        checksum = 10 - checksum % 10;
        checksum = checksum == 10 ? 0 : checksum;

        array[array.length - 1] = Character.forDigit(checksum, 10);

        return new String(array);
    }

}
