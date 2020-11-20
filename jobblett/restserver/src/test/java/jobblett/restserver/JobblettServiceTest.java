package jobblett.restserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jobblett.core.*;
import jobblett.json.JobblettPersistence;
import jobblett.restapi.WorkspaceService;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.glassfish.jersey.test.grizzly.GrizzlyTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;

import static jobblett.restapi.JobShiftListResource.JOB_SHIFT_LIST_RESOURCE_PATH;
import static org.junit.jupiter.api.Assertions.*;


public class JobblettServiceTest extends JerseyTest {

  protected boolean shouldLog() {
    return false;
  }


  @Override protected ResourceConfig configure() {
    Workspace workspace = new JobblettPersistence().readDefault(Workspace.class);
    final JobblettConfig jobblettConfig = new JobblettConfig(workspace);
    if (shouldLog()) {
      enable(TestProperties.LOG_TRAFFIC);
      enable(TestProperties.DUMP_ENTITY);
      jobblettConfig.property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_SERVER, "WARNING");
    }
    return jobblettConfig;
  }

  @Override protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
    return new GrizzlyTestContainerFactory();
  }

  private ObjectMapper objectMapper;

  @Override @BeforeEach public void setUp() throws Exception {
    super.setUp();
    objectMapper = new JobblettModuleObjectMapperProvider().getContext(getClass());
    System.out.println(objectMapper);
  }

  @Override @AfterEach public void tearDown() throws Exception {
    super.tearDown();
  }

  private void assertUser(User user, String username, String givenName, String familyName) {
    assertEquals(user.getUsername(), username);
    assertEquals(user.getGivenName(), givenName);
    assertEquals(user.getFamilyName(), familyName);
  }

  @Test public void getUserListTest() {
    Response getResponse = target(WorkspaceService.WORKSPACE_SERVICE_PATH).path("userlist")
        .request(MediaType.APPLICATION_JSON + ";" + MediaType.CHARSET_PARAMETER + "=UTF8").get();
    assertEquals(200, getResponse.getStatus());
    try {
      UserList userList = objectMapper.readValue(getResponse.readEntity(String.class), UserList.class);
      Iterator<User> iterator = userList.iterator();
      assertTrue(iterator.hasNext());
      User user1 = iterator.next();
      assertTrue(iterator.hasNext());
      User user2 = iterator.next();
      assertTrue(iterator.hasNext());
      User user3 = iterator.next();
      assertTrue(iterator.hasNext());
      User user4 = iterator.next();

      assertUser(user1, "olav", "Olav", "Nordmann");
      assertUser(user2, "nora", "Nora", "Bekkestad");
      assertUser(user3, "petter", "Petter", "Petterson");
      assertUser(user4, "david", "David", "Berg");

    } catch (JsonProcessingException e) {
      fail(e.getMessage());
    }
  }

  @Test public void getGroupListTest() {
    Response getResponse = target(WorkspaceService.WORKSPACE_SERVICE_PATH).path("grouplist")
        .request(MediaType.APPLICATION_JSON + ";" + MediaType.CHARSET_PARAMETER + "=UTF8").get();
    assertEquals(200, getResponse.getStatus());
    try {
      GroupList groupList = objectMapper.readValue(getResponse.readEntity(String.class), GroupList.class);
      Iterator<Group> iterator = groupList.iterator();
      assertTrue(iterator.hasNext());
      Group group = iterator.next();
      assertEquals("Gruppe7", group.getGroupName());
      assertEquals(6803, group.getGroupId());


    } catch (JsonProcessingException e) {

    }
  }

  @Test public void testGetUser() {
    Response getResponse = target(WorkspaceService.WORKSPACE_SERVICE_PATH).path("userlist/get/olav")
        .request(MediaType.APPLICATION_JSON + ";" + MediaType.CHARSET_PARAMETER + "=UTF8").get();
    assertEquals(200, getResponse.getStatus());

    try {
      User user = objectMapper.readValue(getResponse.readEntity(String.class), User.class);
      assertUser(user, "olav", "Olav", "Nordmann");
    } catch (JsonProcessingException e) {

    }

  }

  @Test public void getGroupIDtest() {
    Response getResponse = target(WorkspaceService.WORKSPACE_SERVICE_PATH)
        .path("grouplist/get/6803")
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .get();
    assertEquals(200, getResponse.getStatus());

    try {
      Group group = objectMapper.readValue(getResponse.readEntity(String.class), Group.class);
      assertEquals(group.getGroupId(), 6803);
    } catch (JsonProcessingException e) {

    }

  }

  @Test public void newGroupTest() {
    Response getResponse = target(WorkspaceService.WORKSPACE_SERVICE_PATH)
        .path("grouplist/new")
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .post(Entity.json("testerGroup"));

    assertEquals(200, getResponse.getStatus());

    Response getResponseGroupList = target(WorkspaceService.WORKSPACE_SERVICE_PATH)
        .path("grouplist")
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .get();

    assertEquals(200, getResponseGroupList.getStatus());

    try {
      Group group = objectMapper.readValue(getResponse.readEntity(String.class), Group.class);
      GroupList groupList = objectMapper.readValue(getResponseGroupList.readEntity(String.class), GroupList.class);
      Group group1 = groupList.get(group.getGroupId());
      assertNotNull(group1);
      assertEquals(group.getGroupName(), group1.getGroupName());
      assertEquals(group.getGroupSize(), group1.getGroupSize());

    } catch (JsonProcessingException e) {

    }
  }


  @Test
  public void testAddJobshift(){
    User user = new User("olav", new HashedPassword("bestePassord123"), "Olav", "Nordmann");
    JobShift jobShift = new JobShift(user, LocalDateTime.of(2021, 12, 20, 12, 30), Duration.ofHours(3), "tester update jobshift");

    Response getResponse = target(WorkspaceService.WORKSPACE_SERVICE_PATH).path("grouplist/get/6803/"+JOB_SHIFT_LIST_RESOURCE_PATH+"/add")
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .put(Entity.json(new JobblettPersistence().writeValueAsString(jobShift)));

    // Excpecting 204 and 200 because put does not return anything
    assertEquals(204, getResponse.getStatus());

  }

  @Test public void testGetJobshifts() {
    Response getResponse = target(WorkspaceService.WORKSPACE_SERVICE_PATH).path("grouplist/get/6803/" + JOB_SHIFT_LIST_RESOURCE_PATH)
            .request(MediaType.APPLICATION_JSON + ";" + MediaType.CHARSET_PARAMETER + "=UTF8").get();
    assertEquals(200, getResponse.getStatus());

    try {
      JobShiftList jobShiftList = objectMapper.readValue(getResponse.readEntity(String.class), JobShiftList.class);
      Iterator<JobShift> iterator = jobShiftList.iterator();
      assertTrue(iterator.hasNext());
      JobShift jobShift = iterator.next();
      //assertEquals("7200", jobShift.getDuration().toSecondsPart());
      assertEquals("2021-10-15T17:44:04.738", jobShift.getStartingTime().toString());
      assertEquals("Dette er Olav sin skift.", jobShift.getInfo());

    } catch (JsonProcessingException e) {

    }

  }

  @Test public void testGetJobshiftResource() {
    Response getResponse = target(WorkspaceService.WORKSPACE_SERVICE_PATH)
        .path("grouplist/get/6803/" + JOB_SHIFT_LIST_RESOURCE_PATH + "/get/0")
        .request(MediaType.APPLICATION_JSON + ";" + MediaType.CHARSET_PARAMETER + "=UTF8").get();
    assertEquals(200, getResponse.getStatus());

    try {
      JobShift jobShift = objectMapper.readValue(getResponse.readEntity(String.class), JobShift.class);
      //assertEquals("7200", jobShift.getDuration().toSecondsPart());
      assertEquals("2021-10-15T17:44:04.738", jobShift.getStartingTime().toString());
      assertEquals("Dette er Olav sin skift.", jobShift.getInfo());

    } catch (JsonProcessingException e) {

    }

  }

}
  

    
