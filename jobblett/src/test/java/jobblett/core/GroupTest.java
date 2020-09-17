
package jobblett.core;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GroupTest {
    
    private Group group;
    User user1;

    @BeforeEach
    public void setUp(){
        group = new Group("test", 1);
        user1 = new User("test1", "Passord123", "Kari", "Testermann");
    }
	
	@Test
	public void testAddUser() {
        group.addUser(user1);
        assertEquals(user1, group.getUser("test1"));
		//Testing if it's possible to add an user twice to the same group
		try {
			group.addUser(user1);
			fail("Should not be able to add an user twice to the same group!");
		}
		catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
        }
	}
	
	@Test
	public void testRemoveUser() {
        group.addUser(user1);
		//testing if and user is really removed when your try to remove the use
		group.removeUser(user1);
		assertEquals(null, group.getUser("test1"));
    }
    
    @Test
	public void testConstructor_invalidGroupName() {
        try{
        Group group = new Group("a ", 1);
        } catch(Exception e){
            assertTrue(e instanceof IllegalArgumentException);
        }
    }
    
    @Test
	public void testGetGroupSize() {
        assertEquals(0, group.getGroupSize());
        group.addUser(user1);
        assertEquals(1, group.getGroupSize());
    }

    @Test
    public void testToString() {
        User user2 = new User("test2", "Passord123", "Kari", "Testermann");
        group.addUser(user1);
        group.addUser(user2);
        assertEquals("test: Kari Testermann (@test1), Kari Testermann (@test2)", group.toString());
    }
    
    //Burde kanskje ha test for iterable også
	
	
}
