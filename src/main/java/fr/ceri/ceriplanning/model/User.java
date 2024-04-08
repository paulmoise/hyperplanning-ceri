package fr.ceri.ceriplanning.model;

public class User {

  private String username;
  private String password;
  private boolean isTeacher;
  private String formation;

  private String fullName;

  private boolean darkMode;

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

  public User(String username, String password, boolean isTeacher, String fullName, String formation, boolean darkMode) {
    this.password = password;
    this.username = username;
    this.isTeacher = isTeacher;
    this.formation = formation;
    this.fullName = fullName;
    this.darkMode = darkMode;
  }

  public String getFullName() {
    return fullName;
  }


  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public User() {
  }


  public User(String username, boolean isTeacher, String formation) {
    this.username = username;
    this.isTeacher = isTeacher;
    this.formation = formation;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public boolean isTeacher() {
    return isTeacher;
  }

  public String getFormation() {
    return formation;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setTeacher(boolean isTeacher) {
    this.isTeacher = isTeacher;
  }

  public void setFormation(String formation) {
    this.formation = formation;
  }

  @Override
  public String toString() {
    return "User{" +
      "username='" + username + '\'' +
      ", isTeacher=" + isTeacher +
      ", formation='" + formation + '\'' +
      ", fullName='" + fullName + '\'' +
      ", password='" + password + '\'' +
      ", darkeMode='" + darkMode + '\'' +
      '}';
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    User other = (User) obj;
    return username.equals(other.username) && password.equals(other.password);
  }


}
