package calendar.user;

import calendar.user.dto.UserIdDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Class SuperAdminControllerTest
 *
 * @author Axel Nilsson (axnion)
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SuperAdminControllerTest {
    @Mock
    private UserDAO dao;
    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private SuperAdminController sut;

    @Test
    public void makeSuperAdministratorValid() throws Exception {
        User actorMock = mock(User.class);
        User targetMock = mock(User.class);
        UserIdDTO dtoMock = mock(UserIdDTO.class);

        when(currentUser.getEmailAddres()).thenReturn("test@test.com");
        when(dao.getUserByEmail("test@test.com")).thenReturn(actorMock);
        when(dao.getUserById("1")).thenReturn(targetMock);
        when(dtoMock.getId()).thenReturn("1");
        when(actorMock.canPromoteToSuperAdmin(targetMock)).thenReturn(true);

        try {
            sut.makeSuperAdministrator(dtoMock);
        }
        catch (Exception expt) {
            fail(expt.getMessage());
        }

        verify(targetMock, times(1)).setRole("SUPER_ADMIN");
        verify(dao, times(1)).update(targetMock);
    }

    @Test
    public void makeSuperAdministratorInvalid() throws Exception {
        User actorMock = mock(User.class);
        User targetMock = mock(User.class);
        UserIdDTO dtoMock = mock(UserIdDTO.class);

        when(currentUser.getEmailAddres()).thenReturn("test@test.com");
        when(dao.getUserByEmail("test@test.com")).thenReturn(actorMock);
        when(dao.getUserById("1")).thenReturn(targetMock);
        when(dtoMock.getId()).thenReturn("1");
        when(actorMock.canPromoteToSuperAdmin(targetMock)).thenReturn(false);

        try {
            sut.makeSuperAdministrator(dtoMock);
        }
        catch (Exception expt) {
            fail(expt.getMessage());
        }

        verify(targetMock, never()).setRole(anyString());
        verify(dao, never()).update(any(User.class));
    }
}
