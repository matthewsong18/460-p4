package common;

import java.util.Objects;

public class InputValidation {
    public static boolean checkBlank(Object object) {
        if (Objects.isNull(object)) {
            return true;
        }
        return object instanceof String s && s.isBlank();
    }
}