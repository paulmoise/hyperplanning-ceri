package fr.ceri.ceriplanning.model;

public class User {

  private String username;
  private String password;
  private boolean isTeacher;
  private String formation;

    public User(String username, String password, boolean isTeacher, String formation) {
        this.username = username;
        this.password = password;
        this.isTeacher = isTeacher;
        this.formation = formation;
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
                '}';
    }


}
