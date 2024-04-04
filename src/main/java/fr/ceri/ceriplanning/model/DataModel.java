package fr.ceri.ceriplanning.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

import static fr.ceri.ceriplanning.helper.ICSFileParser.parseIcsFile;

public class DataModel {

  private String activeUser;

  public void DataModel() {
  }

  public List<String> simulateGetDataFromDatabase()
  {
    List<String> name = new ArrayList();

    name.add("John Doe");
    name.add("Jane Doe");
    name.add("Kim Jackson");
    name.add("James Jones");

    return name;
  }


  public List<Event> getEvents() {

    String filePath = "data/calendar.ics";
    return parseIcsFile(filePath);
  }

  public String getActiveUser() {
    return activeUser;
  }

  public void setActiveUser(String text) {
    activeUser = text;
  }
}
