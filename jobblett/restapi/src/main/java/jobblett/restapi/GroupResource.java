package jobblett.restapi;

import static jobblett.restapi.JobShiftListResource.JOB_SHIFT_LIST_RESOURCE_PATH;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import jobblett.core.Group;
import jobblett.core.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GroupResource extends RestApiClass {
  private Group group;
  protected static final Logger LOG = LoggerFactory.getLogger(GroupResource.class);

  public GroupResource(Group group) {
    this.group = group;
  }

  /**
   * Returns the Group.
   *
   * @return Group-object
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Group getGroup() {
    debug("Returns the group: " + group.toString());
    return group;
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/add")
  public void addUser(User user) {
    group.addUser(user);
  }

  /**
   * Checks if user with the username is admin.
   *
   * @param userName users's username
   * @return if admin or not
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/isAdmin/{userName}")
  public boolean isAdmin(@PathParam("userName") String userName) {
    User user = group.getUser(userName);
    if (user == null) {
      return false;
    }
    return group.isAdmin(user);
  }

  /**
   * Adds user with the username as admin.
   *
   * @param userName user's username
   * @return admin added or not
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/addAdmin/{userName}")
  public boolean addAdmin(@PathParam("userName") String userName) {
    User user = group.getUser(userName);
    if (user == null) {
      return false;
    }
    debug("If admin is added: " + userName);
    return group.addAdmin(user);
  }

  /**
   * Returns JobShiftResource.
   *
   * @return JobShiftResource
   */
  @Path("/" + JOB_SHIFT_LIST_RESOURCE_PATH)
  @Produces(MediaType.APPLICATION_JSON)
  public JobShiftListResource getJobShiftList() {
    debug("Sub-resource for JobShiftList in Group: " + group);
    return new JobShiftListResource(group);
  }

  @Override protected Logger logger() {
    return LOG;
  }

}
