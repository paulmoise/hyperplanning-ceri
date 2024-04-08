package fr.ceri.ceriplanning.model;

import java.util.ArrayList;
import java.util.List;

import static fr.ceri.ceriplanning.helper.ICSFileParser.parseIcsFile;

public class DataModel {

  private User activeUser;
  public List<Event> allEvents = new ArrayList<>();


  public DataModel() {
    allEvents = parseIcsFile("data/calendar.ics");
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
    return this.allEvents;
  }

  public void addEvent(Event e) {
    System.out.println( "DataModel addEvent" + e.toString());
    System.out.println("Before addEvent =" + allEvents.size());
    this.allEvents.add(e);
    System.out.println("After addedEvent =" + allEvents.size());
  }

  public User getActiveUser() {
    return activeUser;
  }

  public void setActiveUser(User user) {
    activeUser.setUsername(user.getUsername());
    activeUser.setTeacher(user.isTeacher());
    activeUser.setFormation(user.getFormation());
    activeUser.setPassword(user.getPassword());
    activeUser.setFullName(user.getFullName());
    activeUser.setDarkMode(user.isDarkMode());
  }
}
