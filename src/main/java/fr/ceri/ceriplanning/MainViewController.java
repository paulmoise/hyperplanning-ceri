package fr.ceri.ceriplanning;

import fr.ceri.ceriplanning.model.DataModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainViewController {
  @FXML
  public Label activeUser;
  @FXML
  private StackPane spSubScene;
  @FXML
  private Button btnHome;


  private DataModel dataModel;


  public void setSubSceneInitialNode() {
    activeUser.setText(dataModel.getActiveUser());
    btnHome.fire();
  }

  public void handleBtnOnActionProfile(ActionEvent actionEvent) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("user-profile-view.fxml"));
      loader.load();
      UserProfileController userProfileController = loader.getController();
      userProfileController.initModel(dataModel);

      spSubScene.getChildren().clear();
      spSubScene.getChildren().add(userProfileController.getVBoxRoot());
    } catch (IOException ex) {
      System.out.println(ex.toString());
    }
  }

  public void handleEventManagementButton(ActionEvent actionEvent) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("events-management-view.fxml"));
      loader.load();
      EventsManagementController eventsManagementController = loader.getController();
      eventsManagementController.initModel(dataModel);

      spSubScene.getChildren().clear();
      spSubScene.getChildren().add(eventsManagementController.getVBoxRoot());
    } catch (IOException ex) {
      System.out.println(ex.toString());
    }
  }

  public void handleBtnOnActionSettings(ActionEvent actionEvent) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("settings-view.fxml"));
      loader.load();
      SettingsController settingsController = loader.getController();
      settingsController.initModel(dataModel);

      spSubScene.getChildren().clear();
      spSubScene.getChildren().add(settingsController.getVBoxRoot());
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


      HomeCalendarStudentController homeCalendarStudentController = loader.getController();
      homeCalendarStudentController.initModel(dataModel);

      spSubScene.getChildren().clear();
      spSubScene.getChildren().add(homeCalendarStudentController.getVBoxRoot());
    } catch (IOException ex) {
      System.out.println(ex.toString());
    }
  }


}
