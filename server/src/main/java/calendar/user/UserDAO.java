package calendar.user;

import calendar.user.dto.ChangePasswordDTO;
import calendar.user.dto.RegistrationDTO;
import calendar.user.dto.UserDetailsUpdateDTO;

/**
 * Interface UserDAO
 *
 * @author Axel Nilsson (axnion)
 */
interface UserDAO {
    User getUserById(String id) throws Exception;
    User getUserByEmail(String email) throws Exception;
    User createUser(RegistrationDTO dto) throws Exception;
    void deleteUser(String id) throws Exception;
    User updateUserDetails(UserDetailsUpdateDTO dto) throws Exception;
    void changePassword(ChangePasswordDTO dto) throws Exception;

    void resetPassword(String password, String passwordConfirmation) throws Exception;
    void verifyEmailAddress(String urlId) throws Exception;

    User[] getPendingRegistrations() throws Exception;
    void approveRegistration(String id) throws Exception;

    void makeAdministrator(String id) throws Exception;
    void removeAdministrator(String id) throws Exception;
    void makeSuperAdministrator(String id) throws Exception;
}
