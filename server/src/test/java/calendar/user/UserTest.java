package calendar.user;

import calendar.user.dto.RegistrationDTO;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Class UserTest
 *
 * @author Axel Nilsson (axnion)
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTest {
    private Organization organization;

    private User sut;

    @Before
    public void setup() {
        organization = mock(Organization.class);

        sut = new User();

        sut.setOrganization(organization);
    }

    @Test
    public void createUserFromRegistratonDTO() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        RegistrationDTO dto = mock(RegistrationDTO.class);

        String email = "test@test.com";
        String password = "my_password";
        String organization = "";
        when(dto.getEmail()).thenReturn(email);
        when(dto.getPassword()).thenReturn(password);
        when(dto.getOrganization()).thenReturn(organization);

        User user = new User(dto);

        assertEquals(email, user.getEmail());
        assertTrue(encoder.matches(password, user.getPassword()));
        assertEquals("USER", user.getRole());

        assertEquals("", user.getResetPasswordLink().getUrl());
        assertEquals(0, user.getResetPasswordLink().getTimestamp());
        assertEquals("", user.getValidateEmailLink().getUrl());
        assertEquals(0, user.getValidateEmailLink().getTimestamp());

        assertTrue(DateTime.now().isAfter(user.getCreatedAt()));
        assertTrue(DateTime.now().minusMinutes(1).isBefore(user.getCreatedAt()));
        assertTrue(DateTime.now().isAfter(user.getUpdatedAt()));
        assertTrue(DateTime.now().minusMinutes(1).isBefore(user.getUpdatedAt()));

        assertNotNull(user.getOrganization());
        assertEquals("_", user.getOrganization().getChangePending());

        assertFalse(user.getOrganization().isApproved());

        assertEquals(email, user.getEmailChange());
    }

    @Test
    public void createUserFromRegistratonDTOWithOrg() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        RegistrationDTO dto = mock(RegistrationDTO.class);

        String email = "test@test.com";
        String password = "my_password";
        String organization = "org";
        when(dto.getEmail()).thenReturn(email);
        when(dto.getPassword()).thenReturn(password);
        when(dto.getOrganization()).thenReturn(organization);

        User user = new User(dto);

        assertEquals(email, user.getEmail());
        assertTrue(encoder.matches(password, user.getPassword()));
        assertEquals("USER", user.getRole());

        assertEquals("", user.getResetPasswordLink().getUrl());
        assertEquals(0, user.getResetPasswordLink().getTimestamp());
        assertEquals("", user.getValidateEmailLink().getUrl());
        assertEquals(0, user.getValidateEmailLink().getTimestamp());

        assertTrue(DateTime.now().isAfter(user.getCreatedAt()));
        assertTrue(DateTime.now().minusMinutes(1).isBefore(user.getCreatedAt()));
        assertTrue(DateTime.now().isAfter(user.getUpdatedAt()));
        assertTrue(DateTime.now().minusMinutes(1).isBefore(user.getUpdatedAt()));

        assertNotNull(user.getOrganization());
        assertEquals("org", user.getOrganization().getChangePending());

        assertFalse(user.getOrganization().isApproved());

        assertEquals(email, user.getEmailChange());
    }

    @Test
    public void getterSetterId() {
        String id = "id";
        sut.setId(id);
        assertEquals(id, sut.getId());
    }

    @Test
    public void getterSetterEmail() {
        String email = "test@test.com";
        sut.setEmail(email);
        assertEquals(email, sut.getEmail());
    }

    @Test
    public void getterSetterPassword() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "my_password";
        sut.setPassword(password);
        assertTrue(encoder.matches(password, sut.getPassword()));
    }

    @Test
    public void getterSetterRole() {
        String role = "USER";
        sut.setRole(role);
        assertEquals(role, sut.getRole());
    }

    @Test
    public void getterSetterResetPasswordLink() {
        AuthenticationLink resetPasswordLink = mock(AuthenticationLink.class);
        sut.setResetPasswordLink(resetPasswordLink);
        assertEquals(resetPasswordLink, sut.getResetPasswordLink());
    }

    @Test
    public void getterSetterValidateEmailLink() {
        AuthenticationLink resetPasswordLink = mock(AuthenticationLink.class);
        sut.setValidateEmailLink(resetPasswordLink);
        assertEquals(resetPasswordLink, sut.getValidateEmailLink());
    }

    @Test
    public void getterSetterOrganization() {
        Organization org = mock(Organization.class);
        sut.setOrganization(org);
        assertEquals(org, sut.getOrganization());
    }

    @Test
    public void getterSetterEmailChange() {
        String emailChange = "test@test.com";
        sut.setEmailChange(emailChange);
        assertEquals(emailChange, sut.getEmailChange());
    }

    @Test
    public void fetchAuthoritiesUser() {
        sut.setRole("USER");

        String[] auth = sut.fetchAuthorities();

        assertEquals(1, auth.length);
        assertEquals(auth[0], "USER");
    }

    @Test
    public void fetchAuthoritiesAdmin() {
        sut.setRole("ADMIN");

        String[] auth = sut.fetchAuthorities();

        assertEquals(2, auth.length);
        assertEquals(auth[0], "USER");
        assertEquals(auth[1], "ADMIN");
    }

    @Test
    public void fetchAuthoritiesSuperAdmin() {
        sut.setRole("SUPER_ADMIN");

        String[] auth = sut.fetchAuthorities();

        assertEquals(3, auth.length);
        assertEquals(auth[0], "USER");
        assertEquals(auth[1], "ADMIN");
        assertEquals(auth[2], "SUPER_ADMIN");
    }

    @Test
    public void userApprovedAndValidatedEmailIsValid() {
        Organization org = mock(Organization.class);
        when(org.isApproved()).thenReturn(true);
        sut.setEmail("test@test.com");
        sut.setEmailChange("");
        sut.setOrganization(org);

        assertTrue(sut.valid());
    }

    @Test
    public void userApprovedButInEmailChangeIsValid() {
        Organization org = mock(Organization.class);
        when(org.isApproved()).thenReturn(true);
        sut.setEmail("test@test.com");
        sut.setEmailChange("test2@test.com");
        sut.setOrganization(org);

        assertTrue(sut.valid());
    }

    @Test
    public void userApprovedAndButUnvalidatedEmailIsInvalid() {
        Organization org = mock(Organization.class);
        when(org.isApproved()).thenReturn(true);
        sut.setEmail("test@test.com");
        sut.setEmailChange("test@test.com");
        sut.setOrganization(org);

        assertFalse(sut.valid());
    }

    @Test
    public void userValidEmailButNotApprovedIsInvalid() {
        Organization org = mock(Organization.class);
        when(org.isApproved()).thenReturn(false);
        sut.setEmail("test@test.com");
        sut.setEmailChange("");
        sut.setOrganization(org);

        assertFalse(sut.valid());
    }

    @Test
    public void userNotApprovedAndEmailNotValidatedIsInvalid() {
        Organization org = mock(Organization.class);
        when(org.isApproved()).thenReturn(false);
        sut.setEmail("test@test.com");
        sut.setEmailChange("test@test.com");
        sut.setOrganization(org);

        assertFalse(sut.valid());
    }

    /*
     * ----- CAN MANAGE ----------------------------------------------------------------------------
     */

    @Test
    public void canNotManageUserWhoIsNull() {
        sut.setId("1");
        sut.setRole("USER");

        assertFalse(sut.canManage(null));
    }

    @Test
    public void canNotManageUnApprovedUser() {
        sut.setId("1");
        sut.setRole("USER");

        Organization targetOrgMock = mock(Organization.class);
        when(targetOrgMock.isApproved()).thenReturn(false);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("1");
        when(target.getRole()).thenReturn("USER");
        when(target.getOrganization()).thenReturn(targetOrgMock);

        assertFalse(sut.canManage(target));
    }

    @Test
    public void userCanManageHimself() {
        sut.setId("1");
        sut.setRole("USER");

        Organization targetOrgMock = mock(Organization.class);
        when(targetOrgMock.isApproved()).thenReturn(true);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("1");
        when(target.getRole()).thenReturn("USER");
        when(target.getOrganization()).thenReturn(targetOrgMock);

        assertTrue(sut.canManage(target));
    }

    @Test
    public void userCanNotManageOtherUser() {
        sut.setId("1");
        sut.setRole("USER");

        Organization targetOrgMock = mock(Organization.class);
        when(targetOrgMock.isApproved()).thenReturn(true);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("USER");
        when(target.getOrganization()).thenReturn(targetOrgMock);

        assertFalse(sut.canManage(target));
    }

    @Test
    public void userCanNotManageAdmin() {
        sut.setId("1");
        sut.setRole("USER");

        Organization targetOrgMock = mock(Organization.class);
        when(targetOrgMock.isApproved()).thenReturn(true);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);

        assertFalse(sut.canManage(target));
    }

    @Test
    public void userCanNotManageSuperAdmin() {
        sut.setId("1");
        sut.setRole("USER");

        Organization targetOrgMock = mock(Organization.class);
        when(targetOrgMock.isApproved()).thenReturn(true);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("SUPER_ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);

        assertFalse(sut.canManage(target));
    }

    @Test
    public void orgAdminCanManageHimself() {
        sut.setId("1");
        sut.setRole("ADMIN");

        Organization targetOrgMock = mock(Organization.class);
        when(targetOrgMock.isApproved()).thenReturn(true);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("my_org");

        User target = mock(User.class);
        when(target.getId()).thenReturn("1");
        when(target.getRole()).thenReturn("ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("my_org");

        assertTrue(sut.canManage(target));
    }

    @Test
    public void orgAdminCanManageUserWithSameOrg() {
        sut.setId("1");
        sut.setRole("ADMIN");

        Organization targetOrgMock = mock(Organization.class);
        when(targetOrgMock.isApproved()).thenReturn(true);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("my_org");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("USER");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("my_org");

        assertTrue(sut.canManage(target));
    }

    @Test
    public void orgAdminCanNotManageUserFromOtherOrgOrGlobal() {
        sut.setId("1");
        sut.setRole("ADMIN");

        Organization targetOrgMock = mock(Organization.class);
        when(targetOrgMock.isApproved()).thenReturn(true);
        when(organization.compare(targetOrgMock)).thenReturn(false);
        when(organization.getName()).thenReturn("my_org");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("my_org");

        assertFalse(sut.canManage(target));
    }

    @Test
    public void orgAdminCanNotManageSuperAdmin() {
        sut.setId("1");
        sut.setRole("ADMIN");

        Organization targetOrgMock = mock(Organization.class);
        when(targetOrgMock.isApproved()).thenReturn(true);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("my_org");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("SUPER_ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("my_org");

        assertFalse(sut.canManage(target));
    }

    @Test
    public void globalAdminCanManageHimself() {
        sut.setId("1");
        sut.setRole("ADMIN");

        Organization targetOrgMock = mock(Organization.class);
        when(targetOrgMock.isApproved()).thenReturn(true);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("1");
        when(target.getRole()).thenReturn("ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertTrue(sut.canManage(target));
    }

    @Test
    public void globalAdminCanManageUserWithoutOrg() {
        sut.setId("1");
        sut.setRole("ADMIN");

        Organization targetOrgMock = mock(Organization.class);
        when(targetOrgMock.isApproved()).thenReturn(true);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("USER");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertTrue(sut.canManage(target));
    }

    @Test
    public void globalAdminCanManageUserWithOrg() {
        sut.setId("1");
        sut.setRole("ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(targetOrgMock.isApproved()).thenReturn(true);
        when(organization.compare(targetOrgMock)).thenReturn(false);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("USER");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("my_org");

        assertTrue(sut.canManage(target));
    }

    @Test
    public void globalAdminCanManageOrgAdmin() {
        sut.setId("1");
        sut.setRole("ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(targetOrgMock.isApproved()).thenReturn(true);
        when(organization.compare(targetOrgMock)).thenReturn(false);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("my_org");

        assertTrue(sut.canManage(target));
    }

    @Test
    public void globalAdminCanNotManageOtherAdmin() {
        sut.setId("1");
        sut.setRole("ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(targetOrgMock.isApproved()).thenReturn(true);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canManage(target));
    }

    @Test
    public void globalAdminCanNotManageSuperAdmin() {
        sut.setId("1");
        sut.setRole("ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(targetOrgMock.isApproved()).thenReturn(true);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("SUPERADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canManage(target));
    }

    @Test
    public void superAdminCanManageHimself() {
        sut.setId("1");
        sut.setRole("SUPER_ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(targetOrgMock.isApproved()).thenReturn(true);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("1");
        when(target.getRole()).thenReturn("SUPERADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertTrue(sut.canManage(target));
    }

    @Test
    public void superAdminCanManageUserWithoutOrg() {
        sut.setId("1");
        sut.setRole("SUPER_ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(targetOrgMock.isApproved()).thenReturn(true);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("USER");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertTrue(sut.canManage(target));
    }

    @Test
    public void superAdminCanManageUserWithOrg() {
        sut.setId("1");
        sut.setRole("SUPER_ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(targetOrgMock.isApproved()).thenReturn(true);
        when(organization.compare(targetOrgMock)).thenReturn(false);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("USER");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("my_org");

        assertTrue(sut.canManage(target));
    }

    @Test
    public void superAdminCanManageOrgAdmin() {
        sut.setId("1");
        sut.setRole("SUPER_ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(targetOrgMock.isApproved()).thenReturn(true);
        when(organization.compare(targetOrgMock)).thenReturn(false);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("my_org");

        assertTrue(sut.canManage(target));
    }

    @Test
    public void superAdminCanManageGlobalAdmin() {
        sut.setId("1");
        sut.setRole("SUPER_ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(targetOrgMock.isApproved()).thenReturn(true);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertTrue(sut.canManage(target));
    }

    @Test
    public void superAdminCanNotManageOtherSuperAdmin() {
        sut.setId("1");
        sut.setRole("SUPER_ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(targetOrgMock.isApproved()).thenReturn(true);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("SUPER_ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canManage(target));
    }

    /*
     * ----- CAN CHANGE ROLE TO --------------------------------------------------------------------
     */

    @Test
    public void canNotPromoteUserWhoIsNull() {
        sut.setId("1");
        sut.setRole("USER");

        assertFalse(sut.canChangeRoleTo(null, "ADMIN"));
    }

    @Test
    public void userCanNotPromoteUserToUser() {
        sut.setId("1");
        sut.setRole("USER");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("USER");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canChangeRoleTo(target, "USER"));
    }

    @Test
    public void userCanNotPromoteUserToAdmin() {
        sut.setId("1");
        sut.setRole("USER");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("USER");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canChangeRoleTo(target, "ADMIN"));
    }

    @Test
    public void userCanNotPromoteUserToSuperAdmin() {
        sut.setId("1");
        sut.setRole("USER");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("USER");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canChangeRoleTo(target, "SUPER_ADMIN"));
    }

    @Test
    public void userCanNotDemoteAdminToUser() {
        sut.setId("1");
        sut.setRole("USER");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canChangeRoleTo(target, "USER"));
    }

    @Test
    public void userCanNotPromoteAdminToAdmin() {
        sut.setId("1");
        sut.setRole("USER");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canChangeRoleTo(target, "ADMIN"));
    }

    @Test
    public void userCanNotPromoteAdminToSuperAdmin() {
        sut.setId("1");
        sut.setRole("USER");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canChangeRoleTo(target, "SUPER_ADMIN"));
    }

    @Test
    public void userCanNotDemoteSuperAdminToUser() {
        sut.setId("1");
        sut.setRole("USER");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("SUPER_ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canChangeRoleTo(target, "USER"));
    }

    @Test
    public void userCanNotPromoteSuperAdminToSuperAdmin() {
        sut.setId("1");
        sut.setRole("USER");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("SUPER_ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canChangeRoleTo(target, "SUPER_ADMIN"));
    }

    @Test
    public void userCanNotDemoteSuperAdminToAdmin() {
        sut.setId("1");
        sut.setRole("USER");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("SUPER_ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canChangeRoleTo(target, "ADMIN"));
    }

    @Test
    public void adminCanNotPromoteUserToSuperAdmin() {
        sut.setId("1");
        sut.setRole("ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("USER");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canChangeRoleTo(target, "SUPER_ADMIN"));
    }

    @Test
    public void adminCanNotPromoteUserToUser() {
        sut.setId("1");
        sut.setRole("ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("USER");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canChangeRoleTo(target, "USER"));
    }

    @Test
    public void adminCanNotPromoteAdminToAdmin() {
        sut.setId("1");
        sut.setRole("ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canChangeRoleTo(target, "ADMIN"));
    }

    @Test
    public void adminCanNotPromoteSuperAdminToSuperAdmin() {
        sut.setId("1");
        sut.setRole("ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("SUPER_ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canChangeRoleTo(target, "SUPER_ADMIN"));
    }

    @Test
    public void adminCanNotPromoteAdminToSuperAdmin() {
        sut.setId("1");
        sut.setRole("ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canChangeRoleTo(target, "SUPER_ADMIN"));
    }

    @Test
    public void adminCanNotDemoteSuperAdminToAdmin() {
        sut.setId("1");
        sut.setRole("ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("SUPER_ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canChangeRoleTo(target, "ADMIN"));
    }

    @Test
    public void adminCanNotDemoteSuperAdminToUser() {
        sut.setId("1");
        sut.setRole("ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("SUPER_ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canChangeRoleTo(target, "USER"));
    }

    @Test
    public void orgAdminCanPromoteUserToAdminWithinOrg() {
        sut.setId("1");
        sut.setRole("ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("my_org");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("USER");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("my_org");

        assertTrue(sut.canChangeRoleTo(target, "ADMIN"));
    }

    @Test
    public void orgAdminCanNotPromoteUserToAdminOutsideOrg() {
        sut.setId("1");
        sut.setRole("ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(false);
        when(organization.getName()).thenReturn("my_org");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("USER");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("other_org");

        assertFalse(sut.canChangeRoleTo(target, "ADMIN"));
    }

    @Test
    public void orgAdminCanDemoteAdminToUserWithinOrg() {
        sut.setId("1");
        sut.setRole("ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(true);
        when(organization.getName()).thenReturn("my_org");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("my_org");

        assertTrue(sut.canChangeRoleTo(target, "USER"));
    }

    @Test
    public void orgAdminCanNotDemoteAdminToUserOutsideOrg() {
        sut.setId("1");
        sut.setRole("ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(false);
        when(organization.getName()).thenReturn("my_org");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("other_org");

        assertFalse(sut.canChangeRoleTo(target, "USER"));
    }

    @Test
    public void orgAdminCanNotDemoteGlobalAdminToUser() {
        sut.setId("1");
        sut.setRole("ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(false);
        when(organization.getName()).thenReturn("my_org");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canChangeRoleTo(target, "USER"));
    }

    @Test
    public void globalAdminCanPromoteUserWithOrgToAdmin() {
        sut.setId("1");
        sut.setRole("ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(false);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("USER");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("my_org");

        assertTrue(sut.canChangeRoleTo(target, "ADMIN"));
    }

    @Test
    public void globalAdminCanDemoteOrgAdminToUser() {
        sut.setId("1");
        sut.setRole("ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(false);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("my_org");

        assertTrue(sut.canChangeRoleTo(target, "USER"));
    }

    @Test
    public void globalAdminCanNotDemoteGlobalAdminToUser() {
        sut.setId("1");
        sut.setRole("ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(false);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canChangeRoleTo(target, "USER"));
    }

    @Test
    public void superAdminCanNotPromoteUserToUser() {
        sut.setId("1");
        sut.setRole("SUPER_ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(false);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("USER");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canChangeRoleTo(target, "USER"));
    }

    @Test
    public void superAdminCanPromoteUserToAdmin() {
        sut.setId("1");
        sut.setRole("SUPER_ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(false);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("USER");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertTrue(sut.canChangeRoleTo(target, "ADMIN"));
    }

    @Test
    public void superAdminCanPromoteUserToSuperAdmin() {
        sut.setId("1");
        sut.setRole("SUPER_ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(false);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("USER");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertTrue(sut.canChangeRoleTo(target, "SUPER_ADMIN"));
    }

    @Test
    public void superAdminCanNotPromoteAdminToAdmin() {
        sut.setId("1");
        sut.setRole("SUPER_ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(false);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canChangeRoleTo(target, "ADMIN"));
    }

    @Test
    public void superAdminCanPromoteAdminToSuperAdmin() {
        sut.setId("1");
        sut.setRole("SUPER_ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(false);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertTrue(sut.canChangeRoleTo(target, "SUPER_ADMIN"));
    }

    @Test
    public void superAdminCanDemoteAdminToUser() {
        sut.setId("1");
        sut.setRole("SUPER_ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(false);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertTrue(sut.canChangeRoleTo(target, "USER"));
    }

    @Test
    public void superAdminCanNotPromoteSuperAdminToSuperAdmin() {
        sut.setId("1");
        sut.setRole("SUPER_ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(false);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("SUPER_ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canChangeRoleTo(target, "SUPER_ADMIN"));
    }

    @Test
    public void superAdminCanNotDemoteSuperAdminToAdmin() {
        sut.setId("1");
        sut.setRole("SUPER_ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(false);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("SUPER_ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canChangeRoleTo(target, "ADMIN"));
    }

    @Test
    public void superAdminCanNotDemoteSuperAdminToUser() {
        sut.setId("1");
        sut.setRole("SUPER_ADMIN");
        Organization targetOrgMock = mock(Organization.class);
        when(organization.compare(targetOrgMock)).thenReturn(false);
        when(organization.getName()).thenReturn("");

        User target = mock(User.class);
        when(target.getId()).thenReturn("2");
        when(target.getRole()).thenReturn("SUPER_ADMIN");
        when(target.getOrganization()).thenReturn(targetOrgMock);
        when(targetOrgMock.getName()).thenReturn("");

        assertFalse(sut.canChangeRoleTo(target, "USER"));
    }
}
