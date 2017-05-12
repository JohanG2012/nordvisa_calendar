package calendar.user;

import calendar.user.dto.ChangePasswordDTO;
import calendar.user.dto.UserDetailsUpdateDTO;
import calendar.user.dto.UserIdDTO;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * Class UserController
 *
 * @author Axel Nilsson (axnion)
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    private UserDAO dao = new UserDAOMongo();
    private Email email = new Email();
    private UserInformationValidator informationValidator = new UserInformationValidator(dao);

    @RequestMapping(value = "", params = "id", method = RequestMethod.GET)
    public User getUserById(@RequestParam("id") String id) throws Exception {
        return dao.getUserById(id);
    }

    @RequestMapping(value = "", params = "email", method = RequestMethod.GET)
    public User getUserByEmail(@RequestParam("email") String email) throws Exception {
        return dao.getUserByEmail(email);
    }

    @RequestMapping(value = "", params = "organization", method = RequestMethod.GET)
    public ArrayList<User> getUsersByOrganization(
            @RequestParam("organization") String organization) throws Exception {
        return dao.getUsersByOrganization(organization);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ArrayList<User> getAllUsers() throws Exception {
        return dao.getAllUsers();
    }

    @RequestMapping(value = "/unregister", method = RequestMethod.POST)
    public void unregister(@ModelAttribute UserIdDTO dto) throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User actor = dao.getUserByEmail("test@test.com"); //TODO: Change argument to email
        User target = dao.getUserById(dto.getId());

        if(target == null) {
            throw new Exception("The user you are trying to unregister does not exist");
        }
        else if(actor.getId().equals(dto.getId())) {
            dao.deleteUser(dto.getId());
        }
        else if(actor.getRole().equals("ADMIN") || actor.getRole().equals("SUPER_ADMIN")
                && actor.getOrganization().getName().equals(target.getOrganization().getName())) { //TODO: NOT TESTED!
            dao.deleteUser(dto.getId());
        }
        else {
            throw new Exception("You are not authorized to unregister this account");
        }
    }

    @RequestMapping(value = "/update_user_details", method = RequestMethod.POST)
    public void updateUserDetails(@ModelAttribute UserDetailsUpdateDTO dto) throws Exception {
        informationValidator.validate(dto);
        User user = dao.updateUserDetails(dto);
        email.sendVerificationEmail(user.getValidateEmailLink().getUrl());
    }

    @RequestMapping(value = "/change_password", method = RequestMethod.POST)
    public void changePassword(@ModelAttribute ChangePasswordDTO dto) throws Exception {
        informationValidator.validate(dto);
        dao.changePassword(dto.getId(), dto.getPassword());
    }
}
