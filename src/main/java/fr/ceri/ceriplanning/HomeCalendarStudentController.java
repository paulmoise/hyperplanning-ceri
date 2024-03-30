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
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static fr.ceri.ceriplanning.helper.Utils.addOneHourToDate;
import static fr.ceri.ceriplanning.helper.Utils.calculateNumberOf30MinIntervals;
import static fr.ceri.ceriplanning.helper.Utils.extractTime;
import static fr.ceri.ceriplanning.helper.Utils.generateTimeSlots;
import static fr.ceri.ceriplanning.helper.Utils.getDayOfWeek;
import static fr.ceri.ceriplanning.helper.Utils.isActiveWeekOfYearEqualToEventStartWeekOfYear;
import static fr.ceri.ceriplanning.helper.Utils.isValidDateFormat;

public class HomeCalendarStudentController {


  @FXML
  public HBox calendarContentHBox;

  @FXML
  public Label activeMonth;
  // get the root VBox for this controller
  private @FXML VBox vbRoot;

  private DataModel dataModel;

  private ObservableList<Event> observableEvents = FXCollections.observableArrayList();
  Map<String, Integer> timeSlots = generateTimeSlots(LocalTime.of(8, 0), LocalTime.of(19, 0), Duration.ofMinutes(30));

  ZonedDateTime dateFocus;

  int activeWeekOfYear = 12;

  public VBox getVBoxRoot() {

    displayWeekCalendarGridPane();


    List<Event> events = dataModel.getEvents();
    applyFilterToListOfAllEvents(activeWeekOfYear, events);


    displayEventOnWeekCalendarGridPane(new ArrayList<>(observableEvents));

    observableEvents.addListener((ListChangeListener.Change<? extends Event> change) -> {
      while (change.next()) {
        if (change.wasAdded() || change.wasRemoved() || change.wasUpdated()) {
          System.out.println("Change detected" + change);
          calendarContentHBox.getChildren().clear();
          displayWeekCalendarGridPane();
          displayEventOnWeekCalendarGridPane(new ArrayList<>(observableEvents));

        }
      }
    });


    return vbRoot;
  }


  public void initModel(DataModel model) {
    if (this.dataModel != null) {
      throw new IllegalStateException("Model can only be initialized once");
    }
    this.dataModel = model;

    System.out.println("HomeCalendar Student Controller initModel");

  }


  public void handleBtnOnActionHome(ActionEvent actionEvent) {

  }


  public void displayWeekCalendarGridPane() {

    GridPane gridPane = new GridPane();
    Label allDays = new Label("");
    StackPane allDaysPane = new StackPane(allDays);
    allDaysPane.setAlignment(Pos.CENTER); // Center the label within the stack pane

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


  public GridPane displayDayGridPane() {
    GridPane dayGridPane = new GridPane();

    Label allDays = new Label("");
    StackPane allDaysPane = new StackPane(allDays);
    allDaysPane.setAlignment(Pos.CENTER); // Center the label within the stack pane

    String headerWeekTitleStyle = "-fx-font-weight: bold; -fx-font-size: 15px;";
    Label monday = new Label("Lundi");
    monday.setStyle(headerWeekTitleStyle);
    StackPane mondayPane = new StackPane(monday);
    mondayPane.setAlignment(Pos.CENTER);

    dayGridPane.add(allDaysPane, 0, 0);
    dayGridPane.add(mondayPane, 1, 0);

    return dayGridPane;
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


  public void onNextWeek(ActionEvent actionEvent) {
    activeWeekOfYear++;
    System.out.println("Next week: " + activeWeekOfYear);
    applyFilterToListOfAllEvents(activeWeekOfYear, dataModel.getEvents());
  }

  public void onPrevWeek(ActionEvent actionEvent) {
    activeWeekOfYear--;
    System.out.println("Prev week: " + activeWeekOfYear);
    applyFilterToListOfAllEvents(activeWeekOfYear, dataModel.getEvents());
  }


  public void onToday(ActionEvent actionEvent) {
    Calendar now = Calendar.getInstance(); // Gets the current date and time
    this.activeWeekOfYear = now.get(Calendar.WEEK_OF_YEAR);
    applyFilterToListOfAllEvents(activeWeekOfYear, observableEvents);
  }

}
