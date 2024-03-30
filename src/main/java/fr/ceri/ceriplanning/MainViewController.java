package fr.ceri.ceriplanning;

import fr.ceri.ceriplanning.model.DataModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Map;

import static fr.ceri.ceriplanning.helper.Utils.generateTimeSlots;

public class MainViewController {

  @FXML
  private StackPane spSubScene;
  @FXML
  private Button btnHome;


  private DataModel dataModel;


  public void setSubSceneInitalNode() {
    btnHome.fire();
  }

  public void handleBtnOnActionOverview(ActionEvent actionEvent) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("OverviewSubscene.fxml"));
      loader.load();
      OverviewSubsceneController overviewSubsceneController = loader.getController();
      overviewSubsceneController.initModel(dataModel);

      spSubScene.getChildren().clear();
      spSubScene.getChildren().add(overviewSubsceneController.getVBoxRoot());
    } catch (IOException ex) {
      System.out.println(ex.toString());
    }
  }

  public void handleBtnOnActionOrders(ActionEvent actionEvent) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("OrdersSubscene.fxml"));
      loader.load();
      OrdersSubseneController ordersSubseneController = loader.getController();
      ordersSubseneController.initModel(dataModel);

      spSubScene.getChildren().clear();
      spSubScene.getChildren().add(ordersSubseneController.getVBoxRoot());
    } catch (IOException ex) {
      System.out.println(ex.toString());
    }
  }

  public void handleBtnOnActionSettings(ActionEvent actionEvent) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("SettingsSubscene.fxml"));
      loader.load();
      SettingsSubsceneController settingsSubsceneController = loader.getController();
      settingsSubsceneController.initModel(dataModel);

      spSubScene.getChildren().clear();
      spSubScene.getChildren().add(settingsSubsceneController.getVBoxRoot());
    } catch (IOException ex) {
      System.out.println(ex.toString());
    }
  }

  public void initModel(DataModel model) {

    if (this.dataModel != null) {
      throw new IllegalStateException("Model can only be initialized once");
    }

    this.dataModel = model;
  }

  public void handleBtnExit(ActionEvent actionEvent) {
    Platform.exit();
  }

  public void handleBtnOnActionHome(ActionEvent actionEvent) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("home-calendar.fxml"));
      loader.load();


      HomeCalendarStudentController  homeCalendarStudentController = loader.getController();
      homeCalendarStudentController.initModel(dataModel);

      spSubScene.getChildren().clear();
      spSubScene.getChildren().add(homeCalendarStudentController.getVBoxRoot());
    } catch (IOException ex) {
      System.out.println(ex.toString());
    }
  }

  public void handleBtnOnActionNewEvent(ActionEvent actionEvent) {

  }

  public void handleBtnOnActionProfile(ActionEvent actionEvent) {
  }

}
