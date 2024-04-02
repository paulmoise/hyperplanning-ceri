package fr.ceri.ceriplanning;

import fr.ceri.ceriplanning.model.DataModel;
import fr.ceri.ceriplanning.model.Event;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static fr.ceri.ceriplanning.helper.Utils.addOneHourToDate;
import static fr.ceri.ceriplanning.helper.Utils.calculateNumberOf30MinIntervals;
import static fr.ceri.ceriplanning.helper.Utils.extractTime;
import static fr.ceri.ceriplanning.helper.Utils.generateTimeSlots;
import static fr.ceri.ceriplanning.helper.Utils.getDateFromWeekAndDay;
import static fr.ceri.ceriplanning.helper.Utils.getDayOfWeek;
import static fr.ceri.ceriplanning.helper.Utils.getMonthFromWeek;
import static fr.ceri.ceriplanning.helper.Utils.isActiveWeekOfYearEqualToEventStartWeekOfYear;
import static fr.ceri.ceriplanning.helper.Utils.isValidDateFormat;

public class HomeCalendarStudentController {


  @FXML
  public HBox calendarContentHBox;

  @FXML
  public HBox breadCrumbHBox;


  @FXML
  public Label activeMonth;

  @FXML
  public BreadCrumbBar<String> sampleBreadCrumbBar;
  public TextField filterComboBox;

  @FXML
  public ComboBox calendarTypeComboBox;

  // get the root VBox for this controller
  private @FXML VBox vbRoot;

  private DataModel dataModel;

  private String selectedCalendarCategory = "Formation";
  private ObservableList<Event> observableEvents = FXCollections.observableArrayList();
  Map<String, Integer> timeSlots = generateTimeSlots(LocalTime.of(8, 0), LocalTime.of(19, 0), Duration.ofMinutes(30));


  private AutoCompletionBinding<String> autoCompletionBinding;
  ZonedDateTime dateFocus;

  LocalDate todayDate = LocalDate.now();

  int activeYear = todayDate.getYear();

  int activeWeekOfYear = getTodayWeekOfYear();

  List<String> salleList = new ArrayList<>(
    Arrays.asList("Amphi ada", "Amphi Blaise", "Stat 7 = Info - C 128", "Stat 5 = Info - C 130", "S2 = C 040", "Stat 6 = Info - C 129",
      "Stat 1 = Info - C 137"));

  List<String> formationList = new ArrayList<>(Arrays.asList("M1-IA-ALT", "M1 INTEL", "M1-IA-CLA", "SICOM"));

  List<String> matiereList = new ArrayList<>(
    Arrays.asList("MODELES STOCHASTIQUES", "PROTOTYPAGE INTERFACE", "APPLICATION IA", "APPROCHE NEURONALES", "PROCCESUS STOCHASTIQUES",
      "ANGLAIS"));

  List<String> enseignantList = new ArrayList<>(
    Arrays.asList("RODIN Lilian", "HUET Stephane", "BONNEFOY Ludovic", "ESTEVE Yannick", "CECILLON Noe", "Salas Daniel"));


  //  BreadCrumbBar<String> sampleBreadCrumbBar = new BreadCrumbBar<>();
  TreeItem<String> homeItem = BreadCrumbBar.buildTreeModel("Home");

  public VBox getVBoxRoot() {

    autoCompletionBinding = TextFields.bindAutoCompletion(filterComboBox, formationList);
    addItemToBreadCrumbBar(selectedCalendarCategory);
    List<Event> events = dataModel.getEvents();

    displayDayCalendarGridPane();
    applyFilterToListOfAllEventsForDayCalendar(events);
    displayEventOnDayCalendarGridPane(new ArrayList<>(observableEvents));

//
//    observableEvents.addListener((ListChangeListener.Change<? extends Event> change) -> {
//      while (change.next()) {
//        if (change.wasAdded() || change.wasRemoved() || change.wasUpdated()) {
//          if (calendarTypeComboBox.getValue().equals("Jour")) {
//            calendarContentHBox.getChildren().clear();
//            applyFilterToListOfAllEventsForDayCalendar(events);
//            displayEventOnDayCalendarGridPane(new ArrayList<>(observableEvents));
//          } else if (calendarTypeComboBox.getValue().equals("Semaine")) {
////            calendarContentHBox.getChildren().clear();
//            displayWeekCalendarGridPane();
//            applyFilterToListOfAllEvents(activeWeekOfYear, events);
//            displayEventOnWeekCalendarGridPane(new ArrayList<>(observableEvents));
//          }
//        }
//      }
//    });


//    displayWeekCalendarGridPane();
//
//
//    List<Event> events = dataModel.getEvents();
//    applyFilterToListOfAllEvents(activeWeekOfYear, events);
//
//
//    displayEventOnWeekCalendarGridPane(new ArrayList<>(observableEvents));
//
    observableEvents.addListener((ListChangeListener.Change<? extends Event> change) -> {
      while (change.next()) {
        if (change.wasAdded() || change.wasRemoved() || change.wasUpdated()) {
          System.out.println("Change detected" + observableEvents.size());


          if (calendarTypeComboBox.getValue().equals("Jour")) {
            calendarContentHBox.getChildren().clear();
            displayDayCalendarGridPane();
            displayEventOnDayCalendarGridPane(new ArrayList<>(observableEvents));
          } else if (calendarTypeComboBox.getValue().equals("Semaine")) {
            calendarContentHBox.getChildren().clear();
            displayWeekCalendarGridPane();
            displayEventOnWeekCalendarGridPane(new ArrayList<>(observableEvents));
          }
        }
      }
    });


    return vbRoot;
  }


  // Method to add an item to the breadcrumb bar
  public void addItemToBreadCrumbBar(String newItem) {
    TreeItem<String> newItemTree = new TreeItem<>(newItem);
    homeItem.getChildren().add(newItemTree);
    sampleBreadCrumbBar.setSelectedCrumb(newItemTree); // Update the breadcrumb bar to reflect the addition
  }


  public void removeItemFromBreadCrumbBar(String itemName) {
    TreeItem<String> itemToRemove = BreadCrumbBar.buildTreeModel(itemName);
    if (itemToRemove.getParent() != null) {
      itemToRemove.getParent().getChildren().remove(itemToRemove);
      sampleBreadCrumbBar.setSelectedCrumb(itemToRemove.getParent());
    }
  }


  public void initModel(DataModel model) {
    if (this.dataModel != null) {
      throw new IllegalStateException("Model can only be initialized once");
    }
    this.dataModel = model;

    System.out.println("HomeCalendar Student Controller initModel");

  }


  public void displayWeekCalendarGridPane() {

    GridPane gridPane = new GridPane();
    Label allDays = new Label("");
    StackPane allDaysPane = new StackPane(allDays);
    allDaysPane.setAlignment(Pos.CENTER); // Center the label within the stack pane

    String headerWeekTitleStyle = "-fx-font-weight: bold; -fx-font-size: 15px;";

    LocalDate mondayDate = getDateFromWeekAndDay(2024, activeWeekOfYear, 1);

    Label monday = new Label(mondayDate.getDayOfMonth() + " Lundi " + mondayDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.FRANCE));
    monday.setStyle(headerWeekTitleStyle);
    StackPane mondayPane = new StackPane(monday);
    mondayPane.setAlignment(Pos.CENTER);


    LocalDate tuesdayDate = getDateFromWeekAndDay(activeYear, activeWeekOfYear, 2);
    Label tuesday = new Label(tuesdayDate.getDayOfMonth()+" Mardi " + tuesdayDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.FRANCE));
    tuesday.setStyle(headerWeekTitleStyle);
    StackPane tuesdayPane = new StackPane(tuesday);
    tuesdayPane.setAlignment(Pos.CENTER);

    LocalDate wednesdayDate  = getDateFromWeekAndDay(activeYear, activeWeekOfYear, 3);
    Label wednesday = new Label(wednesdayDate.getDayOfMonth()+" Mercredi "+wednesdayDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.FRANCE));
    wednesday.setStyle(headerWeekTitleStyle);
    StackPane wednesdayPane = new StackPane(wednesday);
    wednesdayPane.setAlignment(Pos.CENTER);


    LocalDate thursdayDate  = getDateFromWeekAndDay(activeYear, activeWeekOfYear, 4);
    Label thursday = new Label(thursdayDate.getDayOfMonth()+" Jeudi "+thursdayDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.FRANCE));
    thursday.setStyle(headerWeekTitleStyle);
    StackPane thursdayPane = new StackPane(thursday);
    thursdayPane.setAlignment(Pos.CENTER);

    LocalDate fridayDate  = getDateFromWeekAndDay(activeYear, activeWeekOfYear, 5);
    Label friday = new Label(fridayDate.getDayOfMonth()+" Vendredi " +fridayDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.FRANCE));
    friday.setStyle(headerWeekTitleStyle);
    StackPane fridayPane = new StackPane(friday);
    fridayPane.setPrefHeight(40);
    fridayPane.setAlignment(Pos.CENTER);


    // box shasow effect
    DropShadow dropShadow = new DropShadow();
    dropShadow.setColor(Color.DARKGRAY);
    dropShadow.setOffsetX(5.0);
    dropShadow.setOffsetY(5.0);
    dropShadow.setRadius(10.0);


    gridPane.setVgap(0.5);

    gridPane.setPadding(new Insets(10, 10, 10, 10));


    ColumnConstraints column1 = new ColumnConstraints();
    ColumnConstraints column2 = new ColumnConstraints();
    ColumnConstraints column3 = new ColumnConstraints();
    ColumnConstraints column4 = new ColumnConstraints();
    ColumnConstraints column5 = new ColumnConstraints();
    ColumnConstraints column6 = new ColumnConstraints();

    gridPane.getColumnConstraints().addAll(column1, column2, column3, column4, column5, column6);

    int smallSize = 1200 / 11;
    column1.setPrefWidth(smallSize);
    column2.setPrefWidth(2 * smallSize);
    column3.setPrefWidth(2 * smallSize);
    column4.setPrefWidth(2 * smallSize);
    column5.setPrefWidth(2 * smallSize);
    column6.setPrefWidth(2 * smallSize);


    for (int col = 0; col < 6; col++) {
      for (int row = 1; row < 24; row++) {
        if (col == 0) {
          Label slot = new Label(timeSlots.keySet().toArray()[row - 1].toString());
          slot.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
          StackPane slotPane = new StackPane(slot);
          slotPane.setStyle("-fx-background-color: #f4f4f4;" +
            "-fx-border-color: #e1e1e1; " +
            "-fx-border-width: 1; " +
            "-fx-border-style: dashed;"
          );
          slotPane.setAlignment(Pos.CENTER); // Center the label within the stack pane
          gridPane.add(slotPane, col, row);

        } else {
          Button button = diplayEventAsButton(row, col);

          gridPane.add(button, col, row);
        }


      }
    }


    gridPane.add(allDaysPane, 0, 0);
    gridPane.add(mondayPane, 1, 0);
    gridPane.add(tuesdayPane, 2, 0);
    gridPane.add(wednesdayPane, 3, 0);
    gridPane.add(thursdayPane, 4, 0);
    gridPane.add(fridayPane, 5, 0);

//    gridPane.setEffect(dropShadow);


    gridPane.setId("weekCalendarGridPane");

    calendarContentHBox.getChildren().add(gridPane);
  }

  private Button diplayEventAsButton(int row, int col) {
    Button button = new Button();
    button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

    button.setStyle("-fx-background-color: #fafafa;" +
      "-fx-border-color: #e1e1e1; " +
      "-fx-border-width: 1; " +
      "-fx-border-style: dashed;");


    button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #cccccc; " +
      "-fx-border-color: black; " +
      "-fx-border-width: 1; " +
      "-fx-border-style: dashed; " +
      "-fx-background-insets: 0;"));
    button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #fafafa;" +
      "-fx-border-color: #e1e1e1; " +
      "-fx-border-width: 1; " +
      "-fx-border-style: dashed;"));

    return button;
  }


  public void displayDayCalendarGridPane() {
    GridPane dayGridPane = new GridPane();

    Label allDays = new Label("");
    StackPane allDaysPane = new StackPane(allDays);
    allDaysPane.setAlignment(Pos.CENTER); // Center the label within the stack pane

    String headerWeekTitleStyle = "-fx-font-weight: bold; -fx-font-size: 15px;";
    Label dayLabel = new Label(todayDate.getDayOfMonth() + " " + todayDate.getDayOfWeek().toString() + " " + todayDate.getYear());
    dayLabel.setId("dayLabel");
    dayLabel.setStyle(headerWeekTitleStyle);
    StackPane dayPane = new StackPane(dayLabel);
    dayPane.setAlignment(Pos.CENTER);


    dayGridPane.setPadding(new Insets(10, 10, 10, 10));


    ColumnConstraints column1 = new ColumnConstraints();
    ColumnConstraints column2 = new ColumnConstraints();


    dayGridPane.getColumnConstraints().addAll(column1, column2);


    int smallSize = 1200 / 11;
    column1.setPrefWidth(3 * smallSize);
    column2.setPrefWidth(8 * smallSize);

    for (int col = 0; col < 2; col++) {
      for (int row = 1; row < 24; row++) {
        if (col == 0) {
          Label slot = new Label(timeSlots.keySet().toArray()[row - 1].toString());
          slot.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
          StackPane slotPane = new StackPane(slot);
          slotPane.setStyle("-fx-background-color: #f4f4f4;" +
            "-fx-border-color: #e1e1e1; " +
            "-fx-border-width: 1; " +
            "-fx-border-style: dashed;"
          );
          slotPane.setAlignment(Pos.CENTER); // Center the label within the stack pane
          dayGridPane.add(slotPane, col, row);

        } else {
          Button button = diplayEventAsButton(row, col);

          dayGridPane.add(button, col, row);
        }


      }
    }


    dayGridPane.add(allDaysPane, 0, 0);
    dayGridPane.add(dayPane, 1, 0);


    dayGridPane.setId("dayCalendarGridPane");

    calendarContentHBox.getChildren().add(dayGridPane);

  }


  private void displayEventOnWeekCalendarGridPane(List<Event> events) {

    for (Event event : events) {

      if (!isValidDateFormat(event.getDtStart()) || !isValidDateFormat(event.getDtEnd())) {
        continue;
      }
      LocalDateTime updatedStartTime = addOneHourToDate(event.getDtStart());
      LocalDateTime updatedEndTime = addOneHourToDate(event.getDtEnd());


      int numberOf30MinutesSlots = calculateNumberOf30MinIntervals(updatedStartTime, updatedEndTime);
      int startCol = getDayOfWeek(updatedStartTime);


      String startTime = extractTime(updatedStartTime);
      String endTime = extractTime(updatedEndTime);

      String stringEvent =
        startTime + " - " + endTime + "\nSalle: " + event.getDescriptionDetails().getSalle() +
          "\nEnseignant : " + event.getDescriptionDetails().getEnseignant() + "\nType: " + event.getDescriptionDetails().getType() +
          "\nMatiere:" + event.getDescriptionDetails().getMatiere() + "\nFormation: " + event.getDescriptionDetails().getTd();


      if (timeSlots.containsKey(startTime) && startCol != -1) {
        int startRow = timeSlots.get(startTime);

        Button buttonEvent = new Button(stringEvent);

        // set color to based on type of event
        if (event.getDescriptionDetails().getType() != null &&
          event.getDescriptionDetails().getType().replaceAll("\\s", "").equals("Evaluation")) {
          buttonEvent.getStyleClass().addAll("btn-sm", "btn-danger");
        } else {
          buttonEvent.getStyleClass().addAll("btn-sm", "btn-info");
        }

        buttonEvent.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        GridPane gridPane = (GridPane) calendarContentHBox.lookup("#weekCalendarGridPane");

        gridPane.add(buttonEvent, startCol, startRow);
        GridPane.setRowSpan(buttonEvent, numberOf30MinutesSlots);
      }
    }
  }



  public void displayMonthCalendarGridPane(){
    GridPane monthGridPane = new GridPane();


  }

  private void applyFilterToListOfAllEvents(int activeWeekOfYear, List<Event> events) {
    observableEvents.clear();
    List<Event> filteredEvent = new ArrayList<>();
    for (Event event : events) {
      //to be updated
      if (isValidDateFormat(event.getDtStart()) && isValidDateFormat(event.getDtEnd()) &&
        isActiveWeekOfYearEqualToEventStartWeekOfYear(event.getDtStart(), activeWeekOfYear) &&
        (event.getDescriptionDetails().getTd().contains("IA") || event.getDescriptionDetails().getTd().contains("M1 INTEL"))) {
        {
          filteredEvent.add(event);
        }
      }
    }

    observableEvents.addAll(filteredEvent);
  }


  private void displayEventOnDayCalendarGridPane(List<Event> events) {

    for (Event event : events) {

      if (!isValidDateFormat(event.getDtStart()) || !isValidDateFormat(event.getDtEnd())) {
        continue;
      }
      LocalDateTime updatedStartTime = addOneHourToDate(event.getDtStart());
      LocalDateTime updatedEndTime = addOneHourToDate(event.getDtEnd());


      int numberOf30MinutesSlots = calculateNumberOf30MinIntervals(updatedStartTime, updatedEndTime);


      String startTime = extractTime(updatedStartTime);
      String endTime = extractTime(updatedEndTime);

      String stringEvent =
        startTime + " - " + endTime + "\nSalle: " + event.getDescriptionDetails().getSalle() +
          "\nEnseignant : " + event.getDescriptionDetails().getEnseignant() + "\nType: " + event.getDescriptionDetails().getType() +
          "\nMatiere:" + event.getDescriptionDetails().getMatiere() + "\nFormation: " + event.getDescriptionDetails().getTd();


      if (timeSlots.containsKey(startTime)) {
        int startRow = timeSlots.get(startTime);

        Button buttonEvent = new Button(stringEvent);

        if (event.getDescriptionDetails().getType() != null &&
          event.getDescriptionDetails().getType().replaceAll("\\s", "").equals("Evaluation")) {
          buttonEvent.getStyleClass().addAll("btn-sm", "btn-danger");
        } else {
          buttonEvent.getStyleClass().addAll("btn-sm", "btn-info");
        }

        buttonEvent.setTextAlignment(TextAlignment.LEFT);

        buttonEvent.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        GridPane gridPane = (GridPane) calendarContentHBox.lookup("#dayCalendarGridPane");

        gridPane.add(buttonEvent, 1, startRow);
        GridPane.setRowSpan(buttonEvent, numberOf30MinutesSlots);
      }
    }
  }


  private void applyFilterToListOfAllEventsForDayCalendar(List<Event> events) {
    observableEvents.clear();
    List<Event> filteredEvent = new ArrayList<>();
    for (Event event : events) {
      //to be updated
      if (isValidDateFormat(event.getDtStart()) && isValidDateFormat(event.getDtEnd()) &&
        isEventInActiveDay(event.getDtStart()) &&
        (event.getDescriptionDetails().getTd().contains("IA") || event.getDescriptionDetails().getTd().contains("M1 INTEL"))) {
        {
          filteredEvent.add(event);
        }
      }
    }

    System.out.println("Filtered event for day calendar: " + filteredEvent.size());
    observableEvents.addAll(filteredEvent);
  }

  public boolean isEventInActiveDay(String dtStart) {
    // Create a formatter with the specified pattern
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

    // Parse the string to LocalDateTime
    LocalDateTime activeDate = LocalDateTime.parse(dtStart, formatter);

    // Convert activeDate to LocalDate for comparison
    LocalDate activeLocalDate = activeDate.toLocalDate();

    // Compare todayDate with the date part of activeDate
    return todayDate.isEqual(activeLocalDate); // True if it's the same day, false otherwise
  }

  public void moveToTheNextDay() {
    todayDate = todayDate.plusDays(1);
    Label label = (Label) calendarContentHBox.lookup("#dayLabel");
    System.out.println("Next day: " + todayDate.getDayOfWeek().toString());
    label.setText(todayDate.getDayOfMonth() + " " + todayDate.getDayOfWeek().toString() + " " + todayDate.getYear());
    applyFilterToListOfAllEventsForDayCalendar(dataModel.getEvents());

  }


  public void moveToThePrevDay() {
    todayDate = todayDate.minusDays(1);
    Label label = (Label) calendarContentHBox.lookup("#dayLabel");
    System.out.println("Next day: " + todayDate.getDayOfWeek().toString());
    label.setText(todayDate.getDayOfMonth() + " " + todayDate.getDayOfWeek().toString() + " " + todayDate.getYear());
    applyFilterToListOfAllEventsForDayCalendar(dataModel.getEvents());
  }

  public void onNextWeek(ActionEvent actionEvent) {

    if (calendarTypeComboBox.getValue().equals("Jour")) {
      moveToTheNextDay();


    } else {
      activeWeekOfYear++;
      System.out.println("Next week: " + activeWeekOfYear);
      applyFilterToListOfAllEvents(activeWeekOfYear, dataModel.getEvents());
      updateActiveMonthLabel();
    }
  }

  public void onPrevWeek(ActionEvent actionEvent) {

    if (calendarTypeComboBox.getValue().equals("Jour")) {
      moveToThePrevDay();

    } else {

      activeWeekOfYear--;
      System.out.println("Prev week: " + activeWeekOfYear);
      applyFilterToListOfAllEvents(activeWeekOfYear, dataModel.getEvents());
      updateActiveMonthLabel();

    }
  }


  public int getTodayWeekOfYear() {
    Calendar now = Calendar.getInstance(); // Gets the current date and time
    return now.get(Calendar.WEEK_OF_YEAR);
  }

  public void onToday(ActionEvent actionEvent) {
    this.activeWeekOfYear = getTodayWeekOfYear();
    applyFilterToListOfAllEvents(activeWeekOfYear, observableEvents);
    updateActiveMonthLabel();
  }

  private void updateActiveMonthLabel() {
    String monthName = getMonthFromWeek(activeYear, activeWeekOfYear);
    String capitalizedMonthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();

    activeMonth.setText(capitalizedMonthName + " " + activeYear);
  }

  public void handleBtnOnActionEnseignant(ActionEvent actionEvent) {
    removeItemFromBreadCrumbBar(selectedCalendarCategory);
    selectedCalendarCategory = "Enseignant";
    addItemToBreadCrumbBar(selectedCalendarCategory);
    autoCompletionBinding.dispose();
    autoCompletionBinding = TextFields.bindAutoCompletion(filterComboBox, enseignantList);

  }

  public void handleBtnOnActionMatiere(ActionEvent actionEvent) {
    removeItemFromBreadCrumbBar(selectedCalendarCategory);
    selectedCalendarCategory = "Matiere";
    addItemToBreadCrumbBar(selectedCalendarCategory);
    autoCompletionBinding.dispose();
    autoCompletionBinding = TextFields.bindAutoCompletion(filterComboBox, matiereList);
  }

  public void handleBtnOnActionSalle(ActionEvent actionEvent) {
    removeItemFromBreadCrumbBar(selectedCalendarCategory);
    selectedCalendarCategory = "Salle";
    addItemToBreadCrumbBar(selectedCalendarCategory);
    autoCompletionBinding.dispose();
    autoCompletionBinding = TextFields.bindAutoCompletion(filterComboBox, salleList);
  }

  public void handleBtnOnActionFormation(ActionEvent actionEvent) {

    removeItemFromBreadCrumbBar(selectedCalendarCategory);
    selectedCalendarCategory = "Formation";
    addItemToBreadCrumbBar(selectedCalendarCategory);
    autoCompletionBinding.dispose();
    autoCompletionBinding = TextFields.bindAutoCompletion(filterComboBox, formationList);
  }

  public void handleBtnOnActionSearch(ActionEvent actionEvent) {
    System.out.println("Search button clicked " + filterComboBox.getText());
  }

  public void handleCalendarTypeComboBoxAction(ActionEvent actionEvent) {

    if (calendarTypeComboBox.getValue().equals("Jour")) {
      calendarContentHBox.getChildren().remove(0);
      displayDayCalendarGridPane();
      applyFilterToListOfAllEventsForDayCalendar(dataModel.getEvents());
    } else if (calendarTypeComboBox.getValue().equals("Semaine")) {
      calendarContentHBox.getChildren().remove(0);
      displayWeekCalendarGridPane();
      applyFilterToListOfAllEvents(activeWeekOfYear, dataModel.getEvents());
    }

  }
}
