package jobblett.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;


/**
 * Represents a group in jobblett.
 */
public class Group extends JobblettPropertyChangeSupporter 
    implements Iterable<User> {

  private final int groupId;
  private String groupName;
  private GroupMemberList groupMembers = new GroupMemberList();
  private JobShiftList jobShifts = new JobShiftList();
  private UserList admins = new UserList();

  /**
   * Initialize a instance of Group with a groupName and groupID.
   *
   * @param groupName the groupName
   * @param groupId   the groupID
   */
  public Group(String groupName, int groupId) {
    setGroupName(groupName);
    this.groupId = groupId;
  }


  /**
   * The given user adds a JobShift to the group.
   *
   * @param jobShift Jobshift to be added.
   * @param user the user that is creating the JobShift, must be admin.
   */
  public void addJobShift(JobShift jobShift, User user) {
    if (!groupMembers.contains(jobShift.getUser())) {
      throw new IllegalArgumentException("Job shift user is not a member of the group");
    }
    if (!isAdmin(user)) {
      throw new IllegalArgumentException("It's only admin that can add new job shift");
    }
    jobShifts.add(jobShift);
  }


  /**
   * Adds a user to the group. An exception thrown if the user is already a part of the group.
   *
   * @param user the user to be added
   * @throws IllegalArgumentException if the user is already a groupMember
   */
  public void addUser(User user) throws IllegalArgumentException {
    UserList oldUsers = new UserList();
    oldUsers.addAll(groupMembers);
    // checkExistingUser(user);
    this.groupMembers.add(user);
    firePropertyChange("groupMembers", oldUsers, groupMembers);
  }

  public boolean isAdmin(User user) {
    return admins.contains(user);
  }

  public Collection<User> getAdmins() {
    return admins.stream().collect(Collectors.toList());
  }

  /**
   * Sets the user to admin.
   *
   * @param user the user that is set to admin.
   * @return If user was set to admin or not.
   */
  public boolean addAdmin(User user) {
    if (!groupMembers.contains(user)) {
      throw new IllegalArgumentException("User is not a member of the group");
    }
    UserList oldAdmins = new UserList();
    oldAdmins.addAll(admins);

    boolean result = admins.add(user);

    firePropertyChange("admins", oldAdmins, admins);

    return result;
  }

  /**
   * Removes admin status from the user.
   *
   * @param user User that loses admin status.
   * @return If admin status was removed or not.
   */
  public boolean removeAdmin(User user) {
    UserList oldAdmins = new UserList();
    oldAdmins.addAll(admins);

    boolean result = admins.remove(user);

    firePropertyChange("admins", oldAdmins, admins);

    return result;
  }

  /**
   * Removes the user from groupMember-list.
   *
   * @param user the user that should be removed
   * @return returns true if the user was contained and removed, else false
   */
  public boolean removeUser(User user) {
    UserList oldUsers = new UserList();
    oldUsers.addAll(groupMembers);

    boolean result = this.groupMembers.remove(user);

    firePropertyChange("groupMembers", oldUsers, groupMembers);

    return result;
  }

  /**
   * Searches through users with the same username and returns the first found.
   *
   * @param username the username used to search
   * @return returns the user if found, else null.
   */
  public User getUser(String username) {
    return groupMembers.get(username);
  }

  /*
   * Checks if user is already a member of the group.
   *
   * @param user the user that should be checked
   * 
   * @throws IllegalArgumentException throws exception if the user already exist.
   */
  /*
   * private void checkExistingUser(User user) throws IllegalArgumentException { if
   * (this.groupMembers.contains(user)) { //Vet ikke om jeg skal ha det sånn, passer bedre for UIen,
   * //men er mer passende generelt å skrive "User is already a member of the group" throw new
   * IllegalArgumentException("You are already a member of the group"); } }
   */

  /**
   * Checks if the group name is more than 1 character.
   *
   * @param groupName the group name
   * @throws IllegalArgumentException throws exception if criteria is not fulfilled
   */
  public static void checkGroupName(String groupName) throws IllegalArgumentException {
    if (groupName.trim().length() < 2) {
      throw new IllegalArgumentException("Group name must have at least 2 characters");
    }
  }

  /**
   * Gets the number of members in a group.
   *
   * @return amount of members
   */
  public int getGroupSize() {
    return this.groupMembers.size();
  }

  /**
   * Gets the groupName. The name is NOT unique. Use groupID for that purpose.
   *
   * @return the groupName
   */
  public String getGroupName() {
    return this.groupName;
  }

  /**
   * Checks if the new group name is valid and changes the groupName.
   *
   * @param groupName the new group name
   * @throws IllegalArgumentException invalid group name throws exception
   */
  public void setGroupName(String groupName) throws IllegalArgumentException {
    checkGroupName(groupName);
    firePropertyChange("groupName", this.groupName, groupName);
    this.groupName = groupName;
  }

  /**
   * Gets the groupID. This is unique identifier for the group.
   *
   * @return the groupID
   */
  public int getGroupId() {
    return groupId;
  }


  //TODO: Dette er dårlig innkapsling,
  //burde fjernes
  public JobShiftList getJobShiftList() {
    return jobShifts;
  }

  @Override
  public String toString() {
    StringBuilder members = new StringBuilder();
    for (User user : this) {
      members.append(user.toString()).append(", ");
    }
    if (members.length() >= 2) {
      members.setLength(members.length() - 2);
    }
    return this.groupName + ": " + members;
  }

  @Override
  public Iterator<User> iterator() {
    return groupMembers.iterator();
  }
}
