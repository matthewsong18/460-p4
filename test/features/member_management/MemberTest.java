package features.member_management;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberTest {

    @Test
    void blankArguments() {
        // Missing Name
        assertThrows(IllegalArgumentException.class, () -> new Member(
                "",
                "123-456-7890",
                "test.email@example.com",
                "2000-10-14",
                "123-456-7890"));

        // Missing telephone
        assertThrows(IllegalArgumentException.class, () -> new Member(
                "Bob",
                "",
                "test.email@example.com",
                "2000-10-14",
                "123-456-7890"));

        // Missing Email
        assertThrows(IllegalArgumentException.class, () -> new Member(
                "Bob",
                "123-456-7890",
                "",
                "2000-10-14",
                "123-456-7890"));

        // Missing Date Of Birth
        assertThrows(IllegalArgumentException.class, () -> new Member(
                "Bob",
                "123-456-7890",
                "test.email@example.com",
                "",
                "123-456-7890"));

        // Missing Email
        assertThrows(IllegalArgumentException.class, () -> new Member(
                "Bob",
                "123-456-7890",
                "test.email@example.com",
                "2000-10-14",
                ""));
    }

    @Test
    void nullArguments() {
        // Missing Name
        assertThrows(IllegalArgumentException.class, () -> new Member(
                null,
                "123-456-7890",
                "test.email@example.com",
                "2000-10-14",
                "123-456-7890"));

        // Missing Telephone
        assertThrows(IllegalArgumentException.class, () -> new Member(
                "Bob",
                null,
                "test.email@example.com",
                "2000-10-14",
                "123-456-7890"));

        // Missing Email
        assertThrows(IllegalArgumentException.class, () -> new Member(
                "Bob",
                "123-456-7890",
                null,
                "2000-10-14",
                "123-456-7890"));

        // Missing Date of Birth
        assertThrows(IllegalArgumentException.class, () -> new Member(
                "Bob",
                "123-456-7890",
                "test.email@example.com",
                null,
                "123-456-7890"));

        // Missing Emergency
        assertThrows(IllegalArgumentException.class, () -> new Member(
                "Bob",
                "123-456-7890",
                "test.email@example.com",
                "2000-10-14",
                null));
    }

    @Test
    void validMember() {
        assertDoesNotThrow(() -> new Member(
                "Bob",
                "123-456-7890",
                "test.email@example.com",
                "2000-10-14",
                "123-456-7890"));
    }

    @Disabled
    @Test
    void emailValidation() {
        // Valid emails
        assertDoesNotThrow(() -> new Member(
                "Bob",
                "123-456-7890",
                "testemail@example.com",
                "2000-10-14",
                "123-456-7890"));

        assertDoesNotThrow(() -> new Member(
                "Bob",
                "123-456-7890",
                "test@gmail.com",
                "2000-10-14",
                "123-456-7890"));

        // Invalid emails
        assertThrows(IllegalArgumentException.class, () -> new Member(
                "Bob",
                "123-456-7890",
                "test",
                "2000-10-14",
                "123-456-7890"));


        assertThrows(IllegalArgumentException.class, () -> new Member(
                "Bob",
                "123-456-7890",
                "test@",
                "2000-10-14",
                "123-456-7890"));

        assertThrows(IllegalArgumentException.class, () -> new Member(
                "Bob",
                "123-456-7890",
                "test@gmail",
                "2000-10-14",
                "123-456-7890"));

        assertThrows(IllegalArgumentException.class, () -> new Member(
                "Bob",
                "123-456-7890",
                "@gmail.com",
                "2000-10-14",
                "123-456-7890"));
    }
}