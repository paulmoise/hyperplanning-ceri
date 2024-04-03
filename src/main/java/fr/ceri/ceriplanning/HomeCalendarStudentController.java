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
import javafx.scene.layout.RowConstraints;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
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
import static fr.ceri.ceriplanning.helper.Utils.getDateTimeFromYearMonthDay;
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

  LocalDate todayDate = LocalDate.now();



  ZonedDateTime today = ZonedDateTime.now();
  ;

  List<String> salleList = new ArrayList<>(
    Arrays.asList("Amphi ada", "Amphi Blaise", "Stat 7 = Info - C 128", "Stat 5 = Info - C 130", "S2 = C 040", "Stat 6 = Info - C 129",
      "Stat 1 = Info - C 137"));

  List<String> formationList = new ArrayList<>(Arrays.asList("M1-IA-ALT", "M1 INTEL", "M1-IA-CLA", "SICOM"));

  List<String> matiereList = new ArrayList<>(
    Arrays.asList("MODELES STOCHASTIQUES", "PROTOTYPAGE INTERFACE", "APPLICATION IA", "APPROCHE NEURONALES", "PROCCESUS STOCHASTIQUES",
      "ANGLAIS"));

  List<String> enseignantList = new ArrayList<>(
    Arrays.asList("RODIN Lilian", "HUET Stephane", "BONNEFOY Ludovic", "ESTEVE Yannick", "CECILLON Noe", "Salas Daniel"));


  TreeItem<String> homeItem = BreadCrumbBar.buildTreeModel("Home");

  public VBox getVBoxRoot() {

    autoCompletionBinding = TextFields.bindAutoCompletion(filterComboBox, formationList);
    addItemToBreadCrumbBar(selectedCalendarCategory);
    List<Event> events = dataModel.getEvents();

    displayDayCalendarGridPane();
    applyFilterToListOfAllEventsForDayCalendar(events);
    displayEventOnDayCalendarGridPane(new ArrayList<>(observableEvents));


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


    int weekOfYear = todayDate.get(WeekFields.of(Locale.FRANCE).weekOfYear());

    GridPane gridPane = new GridPane();
    Label allDays = new Label("");
    StackPane allDaysPane = new StackPane(allDays);
    allDaysPane.setAlignment(Pos.CENTER); // Center the label within the stack pane

    String headerWeekTitleStyle = "-fx-font-weight: bold; -fx-font-size: 15px;";

    Label monday = getLabelForWeekCalendar(1,"Lundi");
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



    RowConstraints rowContrainConstraints = new RowConstraints(50);
    gridPane.getRowConstraints().add(rowContrainConstraints);


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

    return new Label(dayName+ " " +date.getDayOfMonth() );
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


    RowConstraints rowContrainConstraints = new RowConstraints(50);
    dayGridPane.getRowConstraints().add(rowContrainConstraints);


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
    return capitalizedDayName + " " +todayDate.getDayOfMonth();
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
        System.out.println( "col = "+col + " row = " + row);

        int correctedRow = row;
        if (dateOffset == 7){
         correctedRow = row - 1;
        }


        if (calculatedDate > dateOffset) {
          int currentDate = calculatedDate - dateOffset;

          if (currentDate <= monthMaxDate) {

            LocalDate cellDate =
              LocalDate.from(getDateTimeFromYearMonthDay(todayDate.getYear(), todayDate.getMonthValue(), currentDate));
            List<Event> eventsPerDay = countNumberOfEventPerDay(dataModel.getEvents(), cellDate);
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

    column1.setPrefWidth(200);
    column2.setPrefWidth(200);
    column3.setPrefWidth(200);
    column4.setPrefWidth(200);
    column5.setPrefWidth(200);
    column6.setPrefWidth(200);
    column7.setPrefWidth(200);

    double percentWidth = 100.0 / 6; // This divides the grid equally among the 6 columns


    column1.setPercentWidth(percentWidth);
    column2.setPercentWidth(percentWidth);
    column3.setPercentWidth(percentWidth);

    column4.setPercentWidth(percentWidth);
    column5.setPercentWidth(percentWidth);
    column6.setPercentWidth(percentWidth);
    column7.setPercentWidth(percentWidth);


    RowConstraints rowContrainConstraints = new RowConstraints(50);
    monthGridPane.getRowConstraints().add(rowContrainConstraints);

    monthGridPane.setId("monthCalendarGridPane");
    calendarContentHBox.getChildren().add(monthGridPane);


    for (int col = 0; col < 7; col++) {
      for (int row = 1; row < 6; row++) {
        Button button = createEventAsButton();
        button.setPrefHeight(50);
        monthGridPane.add(button, col, row);
      }

    }
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
    updateMonthLabelOnDayOneMove();
  }


  public void moveToThePrevDay() {


    todayDate = todayDate.minusDays(1);
    updateMonthLabelOnDayOneMove();
  }

  private void updateMonthLabelOnDayOneMove() {
    Label label = (Label) calendarContentHBox.lookup("#dayLabel");

    label.setText(getDayLabelStr());
    applyFilterToListOfAllEventsForDayCalendar(dataModel.getEvents());

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
    } else if (calendarTypeComboBox.getValue().equals("Semaine")) {
      todayDate = todayDate.plusWeeks(1);
      int weekOfYear = todayDate.get(WeekFields.of(Locale.FRANCE).weekOfYear());

      applyFilterToListOfAllEvents(weekOfYear, dataModel.getEvents());
      updateActiveMonthLabel();
    } else if (calendarTypeComboBox.getValue().equals("Mois")) {
      forwardOneMonth();
    }
  }

  public void onPrevButtonHandle(ActionEvent actionEvent) {

    if (calendarTypeComboBox.getValue().equals("Jour")) {
      moveToThePrevDay();

    } else if (calendarTypeComboBox.getValue().equals("Semaine")) {

      todayDate = todayDate.minusWeeks(1);
      int weekOfYear = todayDate.get(WeekFields.of(Locale.FRANCE).weekOfYear());
      applyFilterToListOfAllEvents(weekOfYear, dataModel.getEvents());
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
      applyFilterToListOfAllEvents(weekOfYear, observableEvents);
      updateActiveMonthLabel();
    } else {
      this.todayDate = LocalDate.now();
      applyFilterToListOfAllEventsForDayCalendar(dataModel.getEvents());
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

      int weekOfYear = todayDate.get(WeekFields.of(Locale.FRANCE).weekOfYear());
      applyFilterToListOfAllEvents(weekOfYear, dataModel.getEvents());
    } else {
      calendarContentHBox.getChildren().remove(0);
      displayMonthCalendarGridPane();
      displayEventOnMonthCalendarGridPane();
    }

  }
}
