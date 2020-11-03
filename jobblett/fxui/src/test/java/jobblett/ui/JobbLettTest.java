package jobblett.ui;

import static jobblett.ui.SceneController.switchScene;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.stage.Stage;
import jobblett.core.*;

import java.time.Duration;
import java.time.LocalDateTime;

//Abstract class which all other UI test classes inherit from
public abstract class JobbLettTest extends ApplicationTest {

  protected User user1, user2;
  protected Group group1, group2;
  protected JobShift jobShift1, jobShift2;


  protected SceneController controller;

  protected UIAssertions uiAssertions;

  // Subclasses implement this method to give the scene ID
  // for the starting scene of the test
  protected abstract JobblettScenes giveID();

  // Subclasses implement these methods to give the active user and group
  // for the starting scene of the test
  protected abstract User giveActiveUser();
  protected abstract Group giveActiveGroup();

 
  private UserList userList;
  private GroupList groupList;

  @Override
  public void start(final Stage primaryStage) throws Exception {
    App.loadScenes(primaryStage);
    setupData();
    getAccess().setLists(userList,groupList);
    SceneController.setActiveUser(giveActiveUser());
    SceneController.setActiveGroup(giveActiveGroup());
    switchScene(giveID());
    primaryStage.show();
    controller = giveID().getController();
  }

  public JobblettAccess getAccess() {
    return SceneController.getAccess();
  }

  protected void setupData() {
    userList = new UserList();
    groupList = new GroupList();
    user1 = new User("CorrectUsername", HashedPassword.hashPassword("CorrectPassword12345"), "Ole", "Dole");
    user2 = new User("CorrectUsername2", HashedPassword.hashPassword("CorrectPassword12345"), "Hans", "Henrik");
    userList.add(user1);
    userList.add(user2);
    group1 = groupList.newGroup("Test Group 1");
    group2 = groupList.newGroup("Test Group 2");
    group1.addUser(user1);
    group1.addAdmin(user1);
    group1.addUser(user2);
    jobShift1 = new JobShift(user1, LocalDateTime.now().plusHours(5), Duration.ofHours(5), "Tester jobshift1");
    jobShift2 = new JobShift(user1, LocalDateTime.now().plusHours(2), Duration.ofHours(5), "Tester jobshift2");
    group1.addJobShift(jobShift1,user1);
  }

  @BeforeEach
  public void setUp(){
    uiAssertions = new UIAssertions();
  }

  @Test 
  public void testController(){ 
    assertNotNull(controller); 
  }

  @Test
  public void testInitialScene(){
    uiAssertions.assertOnScene(giveID());
  }
}