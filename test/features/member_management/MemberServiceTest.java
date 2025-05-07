package features.member_management;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private IMemberDatabaseAccess mockMemberDatabaseAccess;

    @InjectMocks
    private MemberService memberService;

    @Test
    void registerMember() {
        String name = "Bob";
        String telephone = "123-456-1789";
        String email = "test.email@example.com";
        String dateOfBirth = "2000-10-14";
        String emergencyContact = "123-456-7890";

        Long expectedResult = 1L;

        when(mockMemberDatabaseAccess.findMemberByEmail(email)).thenReturn(Optional.empty());
        when(mockMemberDatabaseAccess.createMember(any(Member.class))).thenReturn(expectedResult);

        Long actualRegisteredId = memberService.registerMember(name, telephone, email, dateOfBirth, emergencyContact);

        assertEquals(expectedResult, actualRegisteredId);

        verify(mockMemberDatabaseAccess).findMemberByEmail(email);
        verify(mockMemberDatabaseAccess).createMember(
                argThat(member -> member.email().equals(email) && member.id() == null));
    }

    @Test
    void registerMemberWithBadEmail() {
        String name = "Bob";
        String telephone = "123-456-1789";
        String email = "test.email@example.com";
        String dateOfBirth = "2000-10-14";
        String emergencyContact = "123-456-7890";

        Member fakeMember = new Member(name, telephone, email, dateOfBirth, emergencyContact);
        when(mockMemberDatabaseAccess.findMemberByEmail(email)).thenReturn(Optional.of(fakeMember));

        assertThrows(IllegalArgumentException.class,
                () -> memberService.registerMember(name, telephone, email, dateOfBirth, emergencyContact));

        verify(mockMemberDatabaseAccess).findMemberByEmail(email);
    }

    @Test
    void findMemberById() {
        // Member found
        Member fakeMember = new Member("Bob",
                "123-456-1789",
                "test.email@example.com",
                "2000-10-14",
                "123-456-7890");

        when(mockMemberDatabaseAccess.findMemberById(1L)).thenReturn(Optional.of(fakeMember));

        Optional<Member> actualMember = memberService.findMemberById(1L);
        assertTrue(actualMember.isPresent());
        assertEquals(fakeMember, actualMember.get());

        verify(mockMemberDatabaseAccess).findMemberById(1L);

        // Member not found
        when(mockMemberDatabaseAccess.findMemberById(2L)).thenReturn(Optional.empty());
        Optional<Member> actualMember2 = memberService.findMemberById(2L);
        assertFalse(actualMember2.isPresent());

        verify(mockMemberDatabaseAccess).findMemberById(2L);
    }

    @Test
    void updateMember() {
        // Valid
        Member fakeMember = new Member(
                1L,
                "Bob",
                "123-456-1789",
                "test.email@example.com",
                "2000-10-14",
                "123-456-7890");

        assertDoesNotThrow(() -> memberService.updateMember(fakeMember));

        // Missing id
        Member missingId = new Member(
                "Bob",
                "123-456-1789",
                "test.email@example.com",
                "2000-10-14",
                "123-456-7890");

        assertThrows(IllegalArgumentException.class, () -> memberService.updateMember(missingId));
    }

    @Disabled
    @Test
    void deleteMemberById() {
        // TODO
    }
}