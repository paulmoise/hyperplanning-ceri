package fr.ceri.ceriplanning;

import fr.ceri.ceriplanning.model.DataModel;
import fr.ceri.ceriplanning.model.Event;
import fr.ceri.ceriplanning.model.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.control.SearchableComboBox;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.ceri.ceriplanning.helper.Utils.addOneHourToDate;
import static fr.ceri.ceriplanning.helper.Utils.calculateNumberOf30MinIntervals;
import static fr.ceri.ceriplanning.helper.Utils.constructMail;
import static fr.ceri.ceriplanning.helper.Utils.extractTime;
import static fr.ceri.ceriplanning.helper.Utils.generateTimeSlots;
import static fr.ceri.ceriplanning.helper.Utils.getDateFromWeekAndDay;
import static fr.ceri.ceriplanning.helper.Utils.getDateTimeFromYearMonthDay;
import static fr.ceri.ceriplanning.helper.Utils.getDayOfWeek;
import static fr.ceri.ceriplanning.helper.Utils.getListGroupPedagogic;
import static fr.ceri.ceriplanning.helper.Utils.getListSalle;
import static fr.ceri.ceriplanning.helper.Utils.getListTypeCours;
import static fr.ceri.ceriplanning.helper.Utils.getMatiereList;
import static fr.ceri.ceriplanning.helper.Utils.getMonthFromWeek;
import static fr.ceri.ceriplanning.helper.Utils.isActiveWeekOfYearEqualToEventStartWeekOfYear;
import static fr.ceri.ceriplanning.helper.Utils.isValidDateFormat;
import static fr.ceri.ceriplanning.helper.Utils.sendEmailToTeacher;

public class HomeCalendarStudentController {


  @FXML
  public HBox calendarContentHBox;

  @FXML
  public HBox breadCrumbHBox;


  @FXML
  public Label activeMonth;

  @FXML
  public BreadCrumbBar<String> sampleBreadCrumbBar;

  @FXML
  public TextField filterAutocompleteTextField;

  @FXML
  public ComboBox<String> calendarTypeComboBox;

  @FXML
  public ComboBox<String> filterBy;
  public SearchableComboBox<String> filterByOption;

  @FXML
  public Label searchCategoryType;

  // get the root VBox for this controller
  private @FXML VBox vbRoot;

  private DataModel dataModel;

  private String selectedCalendarCategory = "Formation";

  private List<Event> userEvents = new ArrayList<>();
  private ObservableList<Event> observableEvents = FXCollections.observableArrayList();

  Map<String, Integer> timeSlots = generateTimeSlots(LocalTime.of(8, 0), LocalTime.of(19, 0), Duration.ofMinutes(30));


  private AutoCompletionBinding<String> autoCompletionBinding;

  LocalDate todayDate = LocalDate.now();


  ZonedDateTime today = ZonedDateTime.now();


  List<String> formationList = new ArrayList<>(Arrays.asList("M1 IA", "M1 ILSEN", "M1 SICOM", "L3-INFORMATIQUE"));

  List<String> matiereList = new ArrayList<>(
    Arrays.asList("MODELES STOCHASTIQUES", "PROTOTYPAGE INTERFACE", "APPLICATION IA", "APPROCHE NEURONALES", "PROCCESUS STOCHASTIQUES",
      "ANGLAIS"));

  List<String> enseignantList = new ArrayList<>(
    Arrays.asList("RONDIN Lilian", "HUET Stephane", "BONNEFOY Ludovic", "ESTEVE Yannick", "CECILLON Noe", "Salas Daniel"));


  public VBox getVBoxRoot() {

    //initialize active user events
    userEvents = getEventsBasedOnUserProfile(dataModel.getActiveUser(), dataModel.getEvents());


    // initialize the active month label
    String monthName = todayDate.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE);
    String capitalizedMonthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();
    activeMonth.setText(capitalizedMonthName + " " + todayDate.getYear());


    // initialize the search bar with the default value as Formation
    autoCompletionBinding = TextFields.bindAutoCompletion(filterAutocompleteTextField, formationList);


    // display day calendar by default
    displayDayCalendarGridPane();
    // First level of filter
    applyFilterBasedOnUserProfile();
    // display default events on the day calendar
    applyFilterForDayCalendar(new ArrayList<>(observableEvents));
    // display default events on the day calendar
    displayEventOnDayCalendarGridPane(new ArrayList<>(observableEvents));


    userEvents = dataModel.getEvents();
//    calendarTypeComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
//      @Override
//      public void changed(ObservableValue observable, Object oldValue, Object newValue) {
//        System .out.println("Selected oldValue: " + oldValue + " newValue: " + newValue);
//        if (calendarTypeComboBox.getValue().equals("Jour")) {
//          calendarContentHBox.getChildren().remove(0);
//          displayDayCalendarGridPane();
//          applyFilterForDayCalendar(dataModel.getEvents());
//        } else if (calendarTypeComboBox.getValue().equals("Semaine")) {
//          calendarContentHBox.getChildren().remove(0);
//          displayWeekCalendarGridPane();
//          int weekOfYear = todayDate.get(WeekFields.of(Locale.FRANCE).weekOfYear());
//          applyFilterForEventInActiveWeek(weekOfYear, dataModel.getEvents());
//        } else {
//          calendarContentHBox.getChildren().remove(0);
//          displayMonthCalendarGridPane();
//          displayEventOnMonthCalendarGridPane();
//        }
//      }
//    });


//    calendarTypeComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
//      System .out.println("Selected oldValue: " + oldValue + " newValue: " + newValue);
//      if (calendarTypeComboBox.getValue().equals("Jour")) {
//        calendarContentHBox.getChildren().remove(0);
//        displayDayCalendarGridPane();
//        applyFilterForDayCalendar(userEvents);
//      } else if (calendarTypeComboBox.getValue().equals("Semaine")) {
//        calendarContentHBox.getChildren().remove(0);
//        displayWeekCalendarGridPane();
//        int weekOfYear = todayDate.get(WeekFields.of(Locale.FRANCE).weekOfYear());
//        applyFilterForEventInActiveWeek(weekOfYear, userEvents);
//      } else {
//        calendarContentHBox.getChildren().remove(0);
//        displayMonthCalendarGridPane();
//        displayEventOnMonthCalendarGridPane();
//      }
//    });

    // add event on filterBy combo box change
//    filterBy.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
//      handleBtnOnActionSearch(null);
//    });


    observableEvents.addListener((ListChangeListener.Change<? extends Event> change) -> {
      while (change.next()) {
        if (change.wasAdded() || change.wasRemoved() || change.wasUpdated()) {


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

    vbRoot.setAlignment(Pos.TOP_CENTER);


    return vbRoot;
  }


  private List<Event> getEventsBasedOnUserProfile(User activeUser, List<Event> events) {
    List<Event> filteredEvent = new ArrayList<>();
    System.out.println(activeUser.isTeacher());
    if (activeUser.isTeacher()) {
      for (Event event : events) {
        if (event.getDescriptionDetails().getEnseignant() != null &&
          event.getDescriptionDetails().getEnseignant().contains(activeUser.getFullName())) {
          {
            filteredEvent.add(event);
          }
        }
      }
    } else {
      for (Event event : events) {
        if (event.getDescriptionDetails().getTd() != null &&
          event.getDescriptionDetails().getTd().contains(activeUser.getFormation())) {
          {

            filteredEvent.add(event);
          }
        }
      }
    }

    return filteredEvent;
  }

  private void applyFilterBasedOnUserProfile() {
    observableEvents.clear();
    observableEvents.addAll(userEvents);
  }


  public void initModel(DataModel model) {
    if (this.dataModel != null) {
      throw new IllegalStateException("Model can only be initialized once");
    }
    this.dataModel = model;

    System.out.println("HomeCalendar Student Controller initModel");

  }


  public void displayWeekCalendarGridPane() {


    int weekOfYear = todayDate.get(WeekFields.of(Locale.FRANCE).weekOfYear());

    GridPane gridPane = new GridPane();
    Label allDays = new Label("");
    StackPane allDaysPane = new StackPane(allDays);
    allDaysPane.setAlignment(Pos.CENTER); // Center the label within the stack pane

    String headerWeekTitleStyle = "-fx-font-weight: bold; -fx-font-size: 15px;";

    Label monday = getLabelForWeekCalendar(1, "Lundi");
    monday.setStyle(headerWeekTitleStyle);
    StackPane mondayPane = new StackPane(monday);
    mondayPane.setAlignment(Pos.CENTER);


    Label tuesday = getLabelForWeekCalendar(2, "Mardi");
    tuesday.setStyle(headerWeekTitleStyle);
    StackPane tuesdayPane = new StackPane(tuesday);
    tuesdayPane.setAlignment(Pos.CENTER);

    Label wednesday = getLabelForWeekCalendar(3, "Mercredi");
    wednesday.setStyle(headerWeekTitleStyle);
    StackPane wednesdayPane = new StackPane(wednesday);
    wednesdayPane.setAlignment(Pos.CENTER);


    Label thursday = getLabelForWeekCalendar(4, "Jeudi");
    thursday.setStyle(headerWeekTitleStyle);
    StackPane thursdayPane = new StackPane(thursday);
    thursdayPane.setAlignment(Pos.CENTER);

    Label friday = getLabelForWeekCalendar(5, "Vendredi");
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

    // set vertical space
    gridPane.setVgap(1);
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


    int numberOfRows = 24;
    double totalHeight = 500;
    double rowHeight = totalHeight / numberOfRows;
    for (int row = 0; row < numberOfRows; row++) {
      RowConstraints rowConstraints = new RowConstraints();
      rowConstraints.setMinHeight(rowHeight); // Use the calculated height
      rowConstraints.setPrefHeight(rowHeight); // Use the calculated height for preferred height as well
      gridPane.getRowConstraints().add(rowConstraints);
    }

    for (int col = 0; col < 6; col++) {
      for (int row = 1; row < 24; row++) {
        if (col == 0 && row % 2 != 0) {
          Label slot = new Label(timeSlots.keySet().toArray()[row - 1].toString());
          slot.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
          StackPane slotPane = new StackPane(slot);
          slotPane.setStyle("-fx-background-color: #f4f4f4;" +
            "-fx-border-color: #e1e1e1; " +
            "-fx-border-width: 1; " +
            "-fx-border-style: dashed;"
          );
//          slotPane.getStyleClass().add("panel-body");
          slotPane.setAlignment(Pos.CENTER); // Center the label within the stack pane
          gridPane.add(slotPane, col, row);

        } else {
          Button button = createEventAsButton();
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

  private Label getLabelForWeekCalendar(int dayOfWeek, String dayName) {
    int weekOfYear = todayDate.get(WeekFields.of(Locale.FRANCE).weekOfYear());
    LocalDate date = getDateFromWeekAndDay(todayDate.getYear(), weekOfYear, dayOfWeek);

    return new Label(dayName + " " + date.getDayOfMonth());
  }

  private Button createEventAsButton() {
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

  private Button createBlankEventButton() {
    Button button = new Button();
    button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    return button;
  }


  public void displayDayCalendarGridPane() {
    GridPane dayGridPane = new GridPane();

    Label allDays = new Label("");
    StackPane allDaysPane = new StackPane(allDays);
    allDaysPane.setAlignment(Pos.CENTER); // Center the label within the stack pane

    String headerWeekTitleStyle = "-fx-font-weight: bold; -fx-font-size: 15px;";
    Label dayLabel = new Label(getDayLabelStr());
    dayLabel.setId("dayLabel");
    dayLabel.setStyle(headerWeekTitleStyle);
    StackPane dayPane = new StackPane(dayLabel);
    dayPane.setAlignment(Pos.CENTER);


    dayGridPane.setPadding(new Insets(10, 10, 10, 10));


    ColumnConstraints column1 = new ColumnConstraints();
    ColumnConstraints column2 = new ColumnConstraints();


    dayGridPane.getColumnConstraints().addAll(column1, column2);


    RowConstraints rowConstraint = new RowConstraints(30);
    dayGridPane.getRowConstraints().add(rowConstraint);


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
          Button button = createEventAsButton();

          dayGridPane.add(button, col, row);
        }


      }
    }


    dayGridPane.add(allDaysPane, 0, 0);
    dayGridPane.add(dayPane, 1, 0);


    dayGridPane.setId("dayCalendarGridPane");


    calendarContentHBox.getChildren().add(dayGridPane);

  }

  private String getDayLabelStr() {
    String monthName = todayDate.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE);
    String capitalizedMonthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();
    String dayName = todayDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRANCE);
    String capitalizedDayName = dayName.substring(0, 1).toUpperCase() + dayName.substring(1).toLowerCase();
    return capitalizedDayName + " " + todayDate.getDayOfMonth();
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

//        Button buttonEvent = new Button(stringEvent);

        VBox vBox = new VBox();

        vBox.setPadding(new Insets(2, 2, 2, 2));
        vBox.getStyleClass().addAll("panel"); // Use the card style class for the primary styling

        HBox headerHbox = new HBox();
        headerHbox.setSpacing(30);
        headerHbox.getStyleClass().add("panel-heading"); // Updated style class for the header
        headerHbox.setStyle("-fx-font-weight: bold;");
        headerHbox.getChildren().add(new Label(startTime + " - " + endTime));

        headerHbox.getChildren().add(new Label(event.getDescriptionDetails().getType()));
        headerHbox.setAlignment(Pos.CENTER_LEFT);
        vBox.getChildren().add(headerHbox);

        VBox vBoxContent = new VBox();
        vBoxContent.getStyleClass().add("panel-body"); // Updated style class for the content
        vBoxContent.getChildren().add(new Label("Salle: " + event.getDescriptionDetails().getSalle()));
        Hyperlink hyperlink = new Hyperlink(event.getDescriptionDetails().getEnseignant());
        hyperlink.setOnAction(e -> sendEmailToTeacher(constructMail(event.getDescriptionDetails().getEnseignant())));
        HBox teacherHbox = new HBox();
        teacherHbox.getChildren().add(new Label("Enseignant: "));
        teacherHbox.getChildren().add(hyperlink);
        teacherHbox.setAlignment(Pos.CENTER_LEFT);
        vBoxContent.getChildren().add(teacherHbox);
        vBoxContent.getChildren().add(new Label("Matiere: " + event.getDescriptionDetails().getMatiere()));
        vBoxContent.getChildren().add(new Label("Formation: " + event.getDescriptionDetails().getTd()));
        vBox.getChildren().add(vBoxContent);


//        vBox.getStyleClass().add("panel-primary");

        // set color to based on type of event
        if (event.getDescriptionDetails().getType() != null &&
          event.getDescriptionDetails().getType().replaceAll(" ", "").equalsIgnoreCase("Evaluation")) {
          vBox.getStyleClass().addAll("panel-danger");
        } else {
          vBox.getStyleClass().add("panel-primary");
        }


        GridPane gridPane = (GridPane) calendarContentHBox.lookup("#weekCalendarGridPane");
        gridPane.add(vBox, startCol, startRow);
        GridPane.setRowSpan(vBox, numberOf30MinutesSlots);
      }
    }
  }


  public List<Event> countNumberOfEventPerDay(List<Event> events, LocalDate date) {
    List<Event> eventsPerDay = new ArrayList<>();
    for (Event event : events) {
      if (!isValidDateFormat(event.getDtStart()) || !isValidDateFormat(event.getDtEnd())) {
        continue;
      }
      LocalDateTime updatedStartTime = addOneHourToDate(event.getDtStart());
      if (updatedStartTime.toLocalDate().isEqual(date)) {
        eventsPerDay.add(event);
      }
    }
    return eventsPerDay;
  }


  public void displayEventOnMonthCalendarGridPane() {

    int monthMaxDate = todayDate.getMonth().maxLength();
    System.out.println("Month max date: " + monthMaxDate);

    //Check for leap year
    if (todayDate.getYear() % 4 != 0 && monthMaxDate == 29) {
      monthMaxDate = 28;
    }

    ZoneId zoneId = ZoneId.of("Europe/Paris");
    ZonedDateTime zonedDateTime = todayDate.atStartOfDay(zoneId);
//    int dateOffset = todayDate.getDayOfMonth();

    LocalDate firstDayOfMonth = todayDate.withDayOfMonth(1);
    int dateOffset = firstDayOfMonth.getDayOfWeek().getValue();
    System.out.println("Date offset: " + dateOffset);

//    int dateOffset =
//      ZonedDateTime.of(dateFocus.getYear(), dateFocus.getMonthValue(), 1, 0, 0, 0, 0, dateFocus.getZone()).getDayOfWeek().getValue();


//    System.out.println("Date offset: " + dateOffset);


    GridPane monthGridPane = (GridPane) calendarContentHBox.lookup("#monthCalendarGridPane");
    for (int col = 0; col < 7; col++) {
      for (int row = 1; row < 7; row++) {

        int calculatedDate = (col + 1) + (7 * (row - 1));
        System.out.println("Calculated date: " + calculatedDate + " Month max date: " + monthMaxDate + " Date offset: " + dateOffset);
        System.out.println("col = " + col + " row = " + row);

        int correctedRow = row;
        if (dateOffset == 7) {
          correctedRow = row - 1;
        }


        if (calculatedDate > dateOffset) {
          int currentDate = calculatedDate - dateOffset;

          if (currentDate <= monthMaxDate) {

            LocalDate cellDate =
              LocalDate.from(getDateTimeFromYearMonthDay(todayDate.getYear(), todayDate.getMonthValue(), currentDate));
            List<Event> eventsPerDay = countNumberOfEventPerDay(userEvents, cellDate);
            Button button = createEventAsButton();
            String buttonText = currentDate + "\n" + eventsPerDay.size() + " Seance(s)";
            button.setText(buttonText);
            button.setPrefHeight(50);


            if (cellDate.getYear() == today.getYear() && cellDate.getMonth() == today.getMonth() &&
              today.getDayOfMonth() == currentDate) {
              Button buttonEvent = createBlankEventButton();
              buttonEvent.setText(buttonText);
              buttonEvent.getStyleClass().addAll("btn-sm", "btn-success");
              monthGridPane.add(buttonEvent, col, correctedRow);
            } else {
              monthGridPane.add(button, col, correctedRow);
            }

          }
        }

      }

    }

  }


  public void displayMonthCalendarGridPane() {
    GridPane monthGridPane = new GridPane();


    String headerWeekTitleStyle = "-fx-font-weight: bold; -fx-font-size: 15px;";
    Label monday = new Label("Lundi");
    monday.setStyle(headerWeekTitleStyle);
    StackPane mondayPane = new StackPane(monday);
    mondayPane.setAlignment(Pos.CENTER);

    Label tuesday = new Label("Mardi");
    tuesday.setStyle(headerWeekTitleStyle);
    StackPane tuesdayPane = new StackPane(tuesday);
    tuesdayPane.setAlignment(Pos.CENTER);

    Label wednesday = new Label("Mercredi");
    wednesday.setStyle(headerWeekTitleStyle);
    StackPane wednesdayPane = new StackPane(wednesday);
    wednesdayPane.setAlignment(Pos.CENTER);

    Label thursday = new Label("Jeudi");
    thursday.setStyle(headerWeekTitleStyle);
    StackPane thursdayPane = new StackPane(thursday);
    thursdayPane.setAlignment(Pos.CENTER);

    Label friday = new Label("Vendredi");
    friday.setStyle(headerWeekTitleStyle);
    StackPane fridayPane = new StackPane(friday);
    fridayPane.setAlignment(Pos.CENTER);

    Label saturday = new Label("Samedi");
    saturday.setStyle(headerWeekTitleStyle);
    StackPane saturdayPane = new StackPane(saturday);
    saturday.setAlignment(Pos.CENTER);

    Label sunday = new Label("Dimanche");
    sunday.setStyle(headerWeekTitleStyle);
    StackPane sundayPane = new StackPane(sunday);
    sunday.setAlignment(Pos.CENTER);


    monthGridPane.add(sundayPane, 0, 0);

    monthGridPane.add(mondayPane, 1, 0);
    monthGridPane.add(tuesdayPane, 2, 0);
    monthGridPane.add(wednesdayPane, 3, 0);
    monthGridPane.add(thursdayPane, 4, 0);
    monthGridPane.add(fridayPane, 5, 0);
    monthGridPane.add(saturdayPane, 6, 0);

//        monthGridPane.setHgap(5);
    monthGridPane.setVgap(0.5);

    monthGridPane.setPadding(new Insets(10, 10, 10, 10));


    ColumnConstraints column1 = new ColumnConstraints();
    ColumnConstraints column2 = new ColumnConstraints();
    ColumnConstraints column3 = new ColumnConstraints();
    ColumnConstraints column4 = new ColumnConstraints();
    ColumnConstraints column5 = new ColumnConstraints();
    ColumnConstraints column6 = new ColumnConstraints();
    ColumnConstraints column7 = new ColumnConstraints();

    monthGridPane.getColumnConstraints().addAll(column1, column2, column3, column4, column5, column6, column7);

    column1.setPrefWidth(150);
    column2.setPrefWidth(150);
    column3.setPrefWidth(150);
    column4.setPrefWidth(150);
    column5.setPrefWidth(150);
    column6.setPrefWidth(150);
    column7.setPrefWidth(150);

    double percentWidth = 100.0 / 6; // This divides the grid equally among the 6 columns

//
//    column1.setPercentWidth(percentWidth);
//    column2.setPercentWidth(percentWidth);
//    column3.setPercentWidth(percentWidth);
//
//    column4.setPercentWidth(percentWidth);
//    column5.setPercentWidth(percentWidth);
//    column6.setPercentWidth(percentWidth);
//    column7.setPercentWidth(percentWidth);


    RowConstraints rowConstraints = new RowConstraints(40);
    monthGridPane.getRowConstraints().add(rowConstraints);


    for (int col = 0; col < 7; col++) {
      for (int row = 1; row < 6; row++) {
        Button button = createEventAsButton();
        button.setPrefHeight(50);
        monthGridPane.add(button, col, row);
      }

    }

    monthGridPane.setId("monthCalendarGridPane");
    calendarContentHBox.getChildren().add(monthGridPane);
  }

  private void applyFilterForEventInActiveWeek(int activeWeekOfYear, List<Event> events) {
    observableEvents.clear();
    List<Event> filteredEvent = new ArrayList<>();
    for (Event event : events) {
      if (isValidDateFormat(event.getDtStart()) &&
        isValidDateFormat(event.getDtEnd()) &&
        isActiveWeekOfYearEqualToEventStartWeekOfYear(event.getDtStart(), activeWeekOfYear)
      ) {
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

        VBox vBoxEvent = new VBox();

        vBoxEvent.setPadding(new Insets(2, 2, 2, 2));
        vBoxEvent.getStyleClass().addAll("panel"); // Use the card style class for the primary styling

        HBox headerHbox = new HBox();
        headerHbox.setSpacing(30);
        headerHbox.getStyleClass().add("panel-heading"); // Updated style class for the header
        headerHbox.setStyle("-fx-font-weight: bold;");
        headerHbox.getChildren().add(new Label(startTime + " - " + endTime));

        headerHbox.getChildren().add(new Label(event.getDescriptionDetails().getType()));
        headerHbox.setAlignment(Pos.CENTER_LEFT);
        vBoxEvent.getChildren().add(headerHbox);

        VBox vBoxContent = new VBox();
        vBoxContent.getStyleClass().add("panel-body"); // Updated style class for the content
        vBoxContent.getChildren().add(new Label("Salle: " + event.getDescriptionDetails().getSalle()));
        Hyperlink hyperlink = new Hyperlink(event.getDescriptionDetails().getEnseignant());
        hyperlink.setOnAction(e -> sendEmailToTeacher(constructMail(event.getDescriptionDetails().getEnseignant())));
        HBox teacherHbox = new HBox();
        teacherHbox.getChildren().add(new Label("Enseignant: "));
        teacherHbox.getChildren().add(hyperlink);
        teacherHbox.setAlignment(Pos.CENTER_LEFT);
        vBoxContent.getChildren().add(teacherHbox);
        vBoxContent.getChildren().add(new Label("Matiere: " + event.getDescriptionDetails().getMatiere()));
        vBoxContent.getChildren().add(new Label("Formation: " + event.getDescriptionDetails().getTd()));
        vBoxEvent.getChildren().add(vBoxContent);


//        vBox.getStyleClass().add("panel-primary");

        // set color to based on type of event
        if (event.getDescriptionDetails().getType() != null &&
          event.getDescriptionDetails().getType().replaceAll(" ", "").equalsIgnoreCase("Evaluation")) {
          vBoxEvent.getStyleClass().addAll("panel-danger");
        } else {
          vBoxEvent.getStyleClass().add("panel-primary");
        }

        GridPane gridPane = (GridPane) calendarContentHBox.lookup("#dayCalendarGridPane");

        gridPane.add(vBoxEvent, 1, startRow);
        GridPane.setRowSpan(vBoxEvent, numberOf30MinutesSlots);
      }
    }
  }


  private void applyFilterToListOfAllEventsForDayCalendar(List<Event> events) {

    List<Event> filteredEvent = new ArrayList<>();
    for (Event event : events) {
      if (isValidDateFormat(event.getDtStart()) &&
        isValidDateFormat(event.getDtEnd()) &&
        isEventOnTheActiveDay(event.getDtStart())) {
        {
          filteredEvent.add(event);
        }
      }
    }

    observableEvents.addAll(filteredEvent);
  }

  public void applyFilterForDayCalendar(List<Event> listEvent) {
    observableEvents.clear();
    List<Event> filteredEvents = new ArrayList<>();
    for (Event event : listEvent) {
      if (isValidDateFormat(event.getDtStart()) &&
        isValidDateFormat(event.getDtEnd())
        && isEventOnTheActiveDay(event.getDtStart())) {
        {
          filteredEvents.add(event);
        }
      }

    }
    observableEvents.addAll(filteredEvents);
  }

  public boolean isEventOnTheActiveDay(String dtStart) {
    // Create a formatter with the specified pattern
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

    // Parse the string to LocalDateTime
    LocalDateTime startEventDate = LocalDateTime.parse(dtStart, formatter);

    // Convert activeDate to LocalDate for comparison
    LocalDate startEventDateLocalDate = startEventDate.toLocalDate();

    // Compare todayDate with the date part of activeDate
    return todayDate.isEqual(startEventDateLocalDate); // True if it's the same day, false otherwise
  }

  public void moveToTheNextDay() {

    todayDate = todayDate.plusDays(1);
    updateMonthLabelOnDayOneMove();
  }


  public void moveToThePrevDay() {


    todayDate = todayDate.minusDays(1);
    updateMonthLabelOnDayOneMove();
  }

  private void updateMonthLabelOnDayOneMove() {
    Label label = (Label) calendarContentHBox.lookup("#dayLabel");

    label.setText(getDayLabelStr());
    applyFilterForDayCalendar(userEvents);

    String monthName = todayDate.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE);
    String capitalizedMonthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();
    activeMonth.setText(capitalizedMonthName + " " + todayDate.getYear());
  }

  public void forwardOneMonth() {
    todayDate = todayDate.plusMonths(1);
    updateMonthGridPaneForOneMonthMove();
  }

  public void backOneMonth() {
    todayDate = todayDate.minusMonths(1);
    updateMonthGridPaneForOneMonthMove();
  }

  private void updateMonthGridPaneForOneMonthMove() {
    calendarContentHBox.getChildren().clear();
    displayMonthCalendarGridPane();
    displayEventOnMonthCalendarGridPane();
    String monthName = todayDate.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE);
    String capitalizedMonthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();
    activeMonth.setText(capitalizedMonthName + " " + todayDate.getYear());
  }

  public void onNextButtonHandle(ActionEvent actionEvent) {


    if (calendarTypeComboBox.getValue().equals("Jour")) {
      moveToTheNextDay();
      applyFilterForDayCalendar(userEvents);
    } else if (calendarTypeComboBox.getValue().equals("Semaine")) {
      todayDate = todayDate.plusWeeks(1);
      int weekOfYear = todayDate.get(WeekFields.of(Locale.FRANCE).weekOfYear());

      applyFilterForEventInActiveWeek(weekOfYear, userEvents);
      updateActiveMonthLabel();
    } else if (calendarTypeComboBox.getValue().equals("Mois")) {
      forwardOneMonth();
    }
  }

  public void onPrevButtonHandle(ActionEvent actionEvent) {

    if (calendarTypeComboBox.getValue().equals("Jour")) {
      moveToThePrevDay();
      applyFilterForDayCalendar(userEvents);
    } else if (calendarTypeComboBox.getValue().equals("Semaine")) {

      todayDate = todayDate.minusWeeks(1);
      int weekOfYear = todayDate.get(WeekFields.of(Locale.FRANCE).weekOfYear());
      applyFilterForEventInActiveWeek(weekOfYear, userEvents);
      updateActiveMonthLabel();
    } else if (calendarTypeComboBox.getValue().equals("Mois")) {
      backOneMonth();
    }
  }


  public int getTodayWeekOfYear() {
    Calendar now = Calendar.getInstance(); // Gets the current date and time
    return now.get(Calendar.WEEK_OF_YEAR);
  }

  public void getBackToToDay(ActionEvent actionEvent) {


    if (calendarTypeComboBox.getValue().equals("Mois")) {
      this.todayDate = LocalDate.now();
      calendarContentHBox.getChildren().clear();
      displayMonthCalendarGridPane();
      displayEventOnMonthCalendarGridPane();
      updateActiveMonthLabel();

    } else if (calendarTypeComboBox.getValue().equals("Semaine")) {

      this.todayDate = LocalDate.now();
      int weekOfYear = todayDate.get(WeekFields.of(Locale.FRANCE).weekOfYear());
      applyFilterForEventInActiveWeek(weekOfYear, userEvents);
      updateActiveMonthLabel();
    } else {
      this.todayDate = LocalDate.now();
      applyFilterForDayCalendar(userEvents);
      updateMonthLabelOnDayOneMove();
      updateActiveMonthLabel();
    }
  }

  private void updateActiveMonthLabel() {
    int weekOfYear = todayDate.get(WeekFields.of(Locale.FRANCE).weekOfYear());
    String monthName = getMonthFromWeek(today.getYear(), weekOfYear);
    String capitalizedMonthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();

    activeMonth.setText(capitalizedMonthName + " " + todayDate.getYear());
  }

  public void handleBtnOnActionEnseignant(ActionEvent actionEvent) {
    selectedCalendarCategory = "Enseignant";
    searchCategoryType.setText("Rechercher un emploi du temps par " + selectedCalendarCategory);
    autoCompletionBinding.dispose();
    autoCompletionBinding = TextFields.bindAutoCompletion(filterAutocompleteTextField, enseignantList);

  }

  public void handleBtnOnActionMatiere(ActionEvent actionEvent) {
    selectedCalendarCategory = "Matiere";
    autoCompletionBinding.dispose();
    autoCompletionBinding = TextFields.bindAutoCompletion(filterAutocompleteTextField, matiereList);
  }

  public void handleBtnOnActionSalle(ActionEvent actionEvent) {
    selectedCalendarCategory = "Salle";
    searchCategoryType.setText("Rechercher un emploi du temps par " + selectedCalendarCategory);
    autoCompletionBinding.dispose();
    autoCompletionBinding = TextFields.bindAutoCompletion(filterAutocompleteTextField, getListSalle(dataModel.getEvents()));
  }

  public void handleBtnOnActionFormation(ActionEvent actionEvent) {
    selectedCalendarCategory = "Formation";
    searchCategoryType.setText("Rechercher un emploi du temps par " + selectedCalendarCategory);
    autoCompletionBinding.dispose();
    autoCompletionBinding = TextFields.bindAutoCompletion(filterAutocompleteTextField, formationList);
  }

  public void handleBtnOnActionSearch(ActionEvent event) {

    observableEvents.clear();
    List<Event> filteredEvents = filterBasedOnFirstStepFilterCategory(dataModel.getEvents());
    observableEvents.addAll(filteredEvents);

  }

  public boolean isDayOrWeekCalendar(Event event) {
    if (calendarTypeComboBox.getValue().equals("Jour") ) {
      return isEventOnTheActiveDay(event.getDtStart());
    }else if (calendarTypeComboBox.getValue().equals("Semaine")){
        int weekOfYear = todayDate.get(WeekFields.of(Locale.FRANCE).weekOfYear());
        return isActiveWeekOfYearEqualToEventStartWeekOfYear(event.getDtStart(), weekOfYear);
    }
    return true;
  }

  public List<Event> filterBasedOnFirstStepFilterCategory(List<Event> events) {

    String filterText = filterAutocompleteTextField.getText().toLowerCase();

    List<Event> filteredEvents = new ArrayList<>();



    filteredEvents = switch (selectedCalendarCategory) {
      case "Formation" -> events
        .stream()
        .filter(event ->
          isValidDateFormat(event.getDtStart()) &&
            isValidDateFormat(event.getDtEnd()) &&
            isDayOrWeekCalendar(event) &&
            event.getDescriptionDetails().getTd() != null &&
            isFormationDerivativeNameEqual(filterText, event.getDescriptionDetails().getTd()))
        .collect(Collectors.toList());
      case "Salle" -> events
        .stream()
        .filter(event ->
          isValidDateFormat(event.getDtStart()) &&
            isValidDateFormat(event.getDtEnd()) &&
            isDayOrWeekCalendar(event) &&
            event.getDescriptionDetails().getSalle() != null &&
            event.getDescriptionDetails().getSalle().toLowerCase().contains(filterText)

        )
        .collect(Collectors.toList());
      case "Enseignant" -> events
        .stream()
        .filter(event ->
          isValidDateFormat(event.getDtStart()) &&
            isValidDateFormat(event.getDtEnd()) &&
            isDayOrWeekCalendar(event) &&
            event.getDescriptionDetails().getEnseignant() != null &&
            event.getDescriptionDetails().getEnseignant().toLowerCase().contains(filterText))
        .collect(Collectors.toList());
      default -> filteredEvents;
    };

    return filteredEvents;
  }

  private boolean isFormationDerivativeNameEqual(String filterText, String eventTd) {


    Map<String, List<String>> filterMap = getFormationDerivativeNameStringListMap();

    if (filterText.isEmpty()) {
      return true;
    }

    if (!formationList.contains(filterText.toUpperCase())) {
      return false;
    }

    String formationKey = filterText.split(" ")[1];

    for (String formationDerivativeName : filterMap.get(formationKey)) {
      if (eventTd.contains(formationDerivativeName)) {
        return true;
      }
    }
    return false;
  }

  private Map<String, List<String>> getFormationDerivativeNameStringListMap() {
    Map<String, List<String>> filterMap = new HashMap<>();
    List<String> iaList = new ArrayList<>(
      Arrays.asList("M1 INTELLIGENCE ARTIFICIELLE (IA)", "M1 INTELLIGENCE ARTIFICIELLE", "M1-IA-IL-CLA", "M1-IA-IL-ALT"));
    List<String> sicomList = new ArrayList<>(Arrays.asList("M1 SICOM", "M1 SYSTEMES INFORMATIQUES COMMUNICANTS (SICOM)",
      "m1-sicom-alt", "m1-sicom-cla"));
    List<String> ilsenList =
      new ArrayList<>(Arrays.asList("M1-ILSEN-alt-GR1", "M1-ILSEN-alt-GR2", "M1-ILSEN-cla-Gr1", "M1-ILSEN-cla-Gr2"));
    List<String> l3info = new ArrayList<>(Arrays.asList("L3 INFORMATIQUE", "L3-INFO-ALT", "L3-INFO-CLA"));
    filterMap.put("ia", iaList);
    filterMap.put("sicom", sicomList);
    filterMap.put("ilsen", ilsenList);
    filterMap.put("informatique", l3info);
    return filterMap;
  }

  public void handleCalendarTypeComboBoxAction(ActionEvent actionEvent) {

    if (calendarTypeComboBox.getValue().equals("Jour")) {
      calendarContentHBox.getChildren().remove(0);
      displayDayCalendarGridPane();
      applyFilterForDayCalendar(userEvents);
    } else if (calendarTypeComboBox.getValue().equals("Semaine")) {
      calendarContentHBox.getChildren().remove(0);
      displayWeekCalendarGridPane();
      int weekOfYear = todayDate.get(WeekFields.of(Locale.FRANCE).weekOfYear());
      applyFilterForEventInActiveWeek(weekOfYear, userEvents);
    } else {
      calendarContentHBox.getChildren().remove(0);
      displayMonthCalendarGridPane();
      displayEventOnMonthCalendarGridPane();
    }

  }

  public void handleFilterByAction(ActionEvent actionEvent) {

    if (calendarTypeComboBox.getValue().equals("Jour")) {
      // remove the previous grid pane

        calendarContentHBox.getChildren().clear();

      System.out.println("before apply filter "+observableEvents.size());
      applyFilterForDayCalendar(userEvents);
      System.out.println("before apply filter "+observableEvents.size());
      displayEventOnDayCalendarGridPane(observableEvents);
    } else if (calendarTypeComboBox.getValue().equals("Semaine")) {

      calendarContentHBox.getChildren().clear();
      int weekOfYear = todayDate.get(WeekFields.of(Locale.FRANCE).weekOfYear());
      applyFilterForEventInActiveWeek(weekOfYear, userEvents);
      displayEventOnWeekCalendarGridPane(observableEvents);
    } else {
      displayWeekCalendarGridPane();
      displayEventOnMonthCalendarGridPane();
    }

    System.out.println("Filter by selected: " + filterBy.getValue() );
    switch (filterBy.getValue()) {
      case "Groupe":
        filterByOption.setItems(getListGroupPedagogic());
        break;
      case "Type":
        filterByOption.setItems(getListTypeCours());
        break;
      case "Matiere":
        filterByOption.setItems(getMatiereList(dataModel.getEvents()));
        break;
      case "Salle":
        filterByOption.setItems(getListSalle(dataModel.getEvents()));
        break;
    }
  }

  public void handleBnOnActionFilterOption(ActionEvent actionEvent) {
    System.out.println("Filter option selected: " + filterByOption.getValue());
    applyFilterToTheLastStepOption();
  }

  public void applyFilterToTheLastStepOption() {
    List<Event> filteredEvents = filterBasedOnFirstStepFilterCategory(userEvents);

    if (filterByOption.getValue() != null) {


      if (filterBy.getValue() != null && filterBy.getValue().equals("Groupe")) {
        applyFilterBasedOnGroupe(filterByOption.getValue(), filteredEvents);
      } else if (filterBy.getValue() != null && filterBy.getValue().equals("Type")) {
        applyFilterBasedOnType(filterByOption.getValue(), filteredEvents);
      } else if (filterBy.getValue() != null && filterBy.getValue().equals("Matiere")) {
        applyFilterBasedOnMatiere(filterByOption.getValue(), filteredEvents);
      }

    }
  }

  private void applyFilterBasedOnMatiere(String value, List<Event> filteredEvents) {
    observableEvents.clear();
    List<Event> filteredEvent = new ArrayList<>();
    for (Event event : filteredEvents) {
      if (event.getDescriptionDetails().getMatiere() != null && event.getDescriptionDetails().getMatiere().equalsIgnoreCase(value)) {
        {
          filteredEvent.add(event);
        }
      }
    }

    observableEvents.addAll(filteredEvent);
  }

  private void applyFilterBasedOnGroupe(String value, List<Event> events) {
    observableEvents.clear();
    List<Event> filteredEvent = new ArrayList<>();
    for (Event event : events) {
      System.out.println("Event td: " + event.getDescriptionDetails().getTd());
      System.out.println("Value: " + value);
      if (event.getDescriptionDetails().getTd() != null && event.getDescriptionDetails().getTd().contains(value)) {
        {
          filteredEvent.add(event);
        }
      }
    }

    observableEvents.addAll(filteredEvent);
  }

  private void applyFilterBasedOnSalle(String value, List<Event> events) {
    observableEvents.clear();
    List<Event> filteredEvent = new ArrayList<>();


    for (Event event : events) {
      if (event.getDescriptionDetails().getSalle() != null
        && event.getDescriptionDetails().getSalle().equalsIgnoreCase(value)) {
        {
          filteredEvent.add(event);
        }
      }
    }

    observableEvents.addAll(filteredEvent);
  }

  private void applyFilterBasedOnType(String value, List<Event> events) {
    observableEvents.clear();
    List<Event> filteredEvent = new ArrayList<>();
    for (Event event : events) {
      if (event.getDescriptionDetails().getType() != null &&
        event.getDescriptionDetails().getType().replaceAll(" ", "").equalsIgnoreCase(value)) {
        {
          filteredEvent.add(event);
        }
      }
    }

    observableEvents.addAll(filteredEvent);
  }


  public void clearButtonStyle() {
    List<String> listOfHeaderButtonIds = new ArrayList<>(Arrays.asList("btnFormation", "btnSalle", "btnEnseignant", "btnMatiere"));
    for (String id : listOfHeaderButtonIds) {
      ToggleButton button = (ToggleButton) vbRoot.lookup("#" + id);
      System.out.println("Button id: " + button.getId());
      button.getStyleClass().remove("button-header");
    }
  }

  public void onClearFilterButton(ActionEvent actionEvent) {
    filterByOption.setValue("");
    filterBy.setValue("");
  }

  public void onSearchEnterPressed(KeyEvent keyEvent) {
    if (keyEvent.getCode().equals(KeyCode.ENTER)) {
      observableEvents.clear();
      List<Event> filteredEvents = filterBasedOnFirstStepFilterCategory(dataModel.getEvents());
      observableEvents.addAll(filteredEvents);
    }
  }
}
