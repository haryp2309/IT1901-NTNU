package jobblett.ui;

import org.junit.jupiter.api.Test;

import jobblett.core.Group;
import jobblett.core.User;

import static jobblett.ui.JobblettScenes.*;

public class LoginControllerTest extends JobbLettTest {

  @Override
  protected JobblettScenes giveID() {
    return LOGIN_ID;
  }

  @Override
  protected User giveActiveUser(){
    return null;
  }

  @Override
  protected Group giveActiveGroup(){
    return null;
  }

  @Test
  public void testLogin_wrongPasswordAndUsername(){
    tryToLogin("WrongUsername", "WrongPassword12345");
    uiAssertions.assertOnScene(LOGIN_ID);
    uiAssertions.assertLabel("errorMessage", "Wrong username or password");
  } 

  @Test
  public void testLogin_correctPasswordAndUsername(){
    tryToLogin(user1.getUserName(), "CorrectPassword12345");
    uiAssertions.assertOnScene(USER_HOME_ID);
    uiAssertions.assertLabel("userFullName", user1.getGivenName() + " " + user1.getFamilyName());
  }

  @Test
  public void testGoToCreateAccount(){
    clickOn("#createAccount");
    uiAssertions.assertOnScene(CREATE_USER_ID);
  }

  private void tryToLogin(String username, String password){
    clickOn("#usernameField").write(username);    
    clickOn("#passwordField").write(password);
    clickOn("#login");
  }
}