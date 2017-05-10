package calendar.user;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Class UserController
 *
 * @author Axel Nilsson (axnion)
 */
@RestController
@RequestMapping("/api/visitor")
public class VisitorController {
    // TODO: Figure out how to do dependency injection into these
    private UserDAO dao = new UserDAO();
    private Email email = new Email();

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public void registration(@ModelAttribute RegistrationDTO dto) throws Exception {
        dto.validate();
        User user = dao.createUser(dto);
        email.sendVerificationEmail(user.getValidateEmailLink().getUrl());
    }

    @RequestMapping(value = "/verify_email", method = RequestMethod.GET)
    public String verifyEmailAddress(HttpServletResponse response, @RequestParam("id") String urlId)
            throws Exception {
        try {
            dao.verifyEmailAddress(urlId);
        }
        catch (Exception expt) {
            return expt.getMessage();
        }
        response.sendRedirect("/");
        return "";
    }
}