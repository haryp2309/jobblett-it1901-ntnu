package jobblett.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jobblett.core.GroupList;
import jobblett.core.User;
import jobblett.core.UserList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserListPersistenceTest {

   private UserList userList = new UserList();

    @BeforeAll
    public  void setUp(){
        User user1 = new User("Ola1424", "Godmorgen1234", "Ola","Nordmann");
        User user2 = new User("Per2434", "Godkveld1234", "Per","Gudmunsen");
        User user3 = new User("Herman3434", "Godettermiddag1234", "Herman","Hermansen");
        userList.addUser(user1,user2,user3);
    }

    @Test
    public void persistenceTest() {

        // Serializing
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.registerModule(new CoreModule());
        String result = "";

        try {
            result = mapper.writeValueAsString(userList);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e);
        }

        // Deserializing
        mapper = new ObjectMapper();
        mapper.registerModule(new CoreModule());

        try {
            UserList newUserList = mapper.readValue(result,UserList.class);
            System.out.println(newUserList);
            System.out.println(userList);
            assert(newUserList.equals(userList));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail(e);
        }
    }

    public static void main(String[] args) {
        UserListPersistenceTest test = new UserListPersistenceTest();
        test.setUp();
        test.persistenceTest();
    }
}