package jobblett.ui;

import static jobblett.ui.JobblettScenes.CREATE_GROUP;
import static jobblett.ui.JobblettScenes.GROUP_HOME;
import static jobblett.ui.JobblettScenes.USER_HOME;

import jobblett.core.Group;
import jobblett.core.User;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class CreateGroupControllerTest extends JobbLettTest {

  @Override public JobblettScenes giveId() {
    return CREATE_GROUP;
  }

  @Override public User optionalActiveUser() {
    return user1;
  }

  @Test public void testCreateGroup_validGroupName() {
    tryToCreateGroup("Group Name");
    uiAssertions.assertOnScene(GROUP_HOME);
    uiAssertions.assertLabel("groupName", "Group Name");
    int groupID = getControllerMap().getActiveGroupId();
    clickOn("#backToHome");
    uiAssertions.assertOnScene(USER_HOME);
    uiAssertions.assertListViewHasItem("groups", groupID);
  }

  @Test public void testCreateGroup_invalidGroupName() {
    tryToCreateGroup("    ");
    uiAssertions.assertOnScene(CREATE_GROUP);
    uiAssertions.assertLabel("errorMessage", "Group name must have at least 2 characters");
  }

  private void tryToCreateGroup(String groupName) {
    clickOn("#groupNameField").write(groupName);
    clickOn("#createGroupButton");
  }
}
