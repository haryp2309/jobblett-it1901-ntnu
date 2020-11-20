package jobblett.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jobblett.core.Group;
import jobblett.core.HashedPassword;
import jobblett.core.JobShift;
import jobblett.core.User;
import jobblett.core.Workspace;
import jobblett.json.JobblettPersistence;


public class DirectWorkspaceAccess implements WorkspaceAccess, PropertyChangeListener {

  private Workspace workspace;

  //TODO: JAVAAADOC

  public DirectWorkspaceAccess(Workspace workspace) {
    this.workspace = workspace;
    this.workspace.addListener(this);
  }

  private void save() {
    new JobblettPersistence().writeValueOnDefaultLocation(workspace);
  }

  @Override public int newGroup(String groupName) {
    return workspace.getGroupList().newGroup(groupName).getGroupId();
  }

  private User getUser(String username) {
    checkUsername(username);
    return workspace.getUserList().get(username);
  }

  private void checkUsername(String username) {
    if (!hasUser(username)) {
      throw new IllegalArgumentException("No user with the username: " + username);
    }
  }

  @Override public void addUser(
      String username,
      String password,
      String givenName,
      String familyName) {
    User newUser = new User(username, new HashedPassword(password), givenName, familyName);
    workspace.getUserList().add(newUser);
  }

  @Override public boolean hasUser(String username) {
    return workspace.getUserList().get(username) != null;
  }

  @Override public boolean correctPassword(String username, String password) {
    if (!hasUser(username)) {
      return false;
    }
    return getUser(username).getPassword().matches(new HashedPassword(password));
  }

  @Override public String getUserFullName(String username) {
    User user = getUser(username);
    return user.getGivenName() + " " + user.getFamilyName();
  }

  @Override public String getUserToString(String username) {
    return getUser(username).toString();
  }

  private void checkGroupId(int groupId) {
    if (!hasGroup(groupId)) {
      throw new IllegalArgumentException("No group with the ID: " + groupId);
    }
  }

  private Group getGroup(int groupId) {
    checkGroupId(groupId);
    return workspace.getGroupList().get(groupId);
  }

  @Override public boolean hasGroup(int groupId) {
    return workspace.getGroupList().get(groupId) != null;
  }

  @Override public String getGroupName(int groupId) {
    return getGroup(groupId).getGroupName();
  }

  @Override public Collection<Integer> getAllGroupIds(String username) {
    Collection<Integer> allIds = new ArrayList<>();
    workspace.getGroupList().getGroups(getUser(username))
        .forEach((group) -> allIds.add(group.getGroupId()));
    return allIds;
  }

  @Override public Collection<String> getGroupUsernames(int groupId) {
    Collection<String> allUsernames = new ArrayList<>();
    getGroup(groupId).forEach((user) -> allUsernames.add(user.getUsername()));
    return allUsernames;
  }

  @Override public void addGroupMember(int groupId, String username) {
    getGroup(groupId).addUser(getUser(username));
  }

  @Override public boolean hasGroupUser(int groupId, String username) {
    return getGroup(groupId).getUser(username) != null;
  }

  @Override public void addGroupAdmin(int groupId, String username) {
    getGroup(groupId).addAdmin(getUser(username));
  }

  @Override public boolean isGroupAdmin(int groupId, String username) {
    return getGroup(groupId).isAdmin(getUser(username));
  }

  private JobShift getJobShift(int groupId, int index) {
    return getGroup(groupId).getJobShiftList().get(index);
  }

  @Override public void deleteJobShift(String adminUsername, int groupId, int index) {
    JobShift jobShift = getJobShift(groupId, index);
    User admin = getUser(adminUsername);
    getGroup(groupId).removeJobShift(admin, jobShift);
  }

  @Override
  public void addJobShift(String username, int groupId, String jobShiftUsername,
      LocalDateTime startingTime, Duration duration, String info) {
    JobShift jobShift = new JobShift(getUser(jobShiftUsername), startingTime, duration, info);
    getGroup(groupId).addJobShift(jobShift, getUser(username));
  }

  @Override public int getJobShiftsSize(int groupId) {
    return getGroup(groupId).getJobShiftList().size();
  }

  @Override public List<Integer> getJobShiftIndexes(int groupId, String username) {
    List<Integer> indexes = new ArrayList<>();
    List<JobShift> jobShifts = getGroup(groupId).getJobShiftList()
        .getJobShifts(getUser(username));
    jobShifts.stream().forEach((jobShift) -> indexes.add(jobShifts.indexOf(jobShift)));
    return indexes;
  }

  @Override public String getJobShiftUsername(int groupId, int index) {
    return getJobShift(groupId, index).getUser().getUsername();
  }

  @Override public LocalDateTime getJobShiftStartingTime(int groupId, int index) {
    return getJobShift(groupId, index).getStartingTime();
  }

  @Override public LocalDateTime getJobShiftEndingTime(int groupId, int index) {
    return getJobShift(groupId, index).getEndingTime();
  }

  @Override public String getJobShiftInfo(int groupId, int index) {
    return getJobShift(groupId, index).getInfo();
  }

  @Override public boolean jobShiftIsOutdated(int groupId, int index) {
    return getJobShift(groupId, index).isOutDated();
  }
  
  @Override public void deleteOutdatedJobShift(int groupId) {
    getGroup(groupId).deleteOutdatedJobShifts();
  }

  @Override public void propertyChange(PropertyChangeEvent evt) {
    save();
  }
}
