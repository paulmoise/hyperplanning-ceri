package fr.ceri.ceriplanning.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

import static fr.ceri.ceriplanning.helper.ICSFileParser.parseIcsFile;

public class DataModel {

  public User activeUser;

  public void DataModel() {
    activeUser = new User();
  }

  public List<String> simulateGetDataFromDatabase() {
    List<String> name = new ArrayList();

    name.add("John Doe");
    name.add("Jane Doe");
    name.add("Kim Jackson");
    name.add("James Jones");

    return name;
  }


  public List<Event> getEvents() {

    String filePath = "data/new_calendar.ics";
    return parseIcsFile(filePath);
  }

  public User getActiveUser() {
    return activeUser;
  }

  public void setActiveUser(User user) {

    System.out.println("DataModel setActiveUser" + user);
    activeUser.setUsername(user.getUsername());
    activeUser.setTeacher(user.isTeacher());
    activeUser.setFormation(user.getFormation());
  }
}
