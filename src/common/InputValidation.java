package common;

import java.util.Objects;

public class InputValidation {
    public static boolean checkStringBlank(String string) {
        return string == null || string.isEmpty();
    }
}