package fr.ceri.ceriplanning;

import fr.ceri.ceriplanning.model.DataModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.ToggleSwitch;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import static fr.ceri.ceriplanning.helper.Utils.updateDarkMode;

public class MainViewController {
  @FXML
  public Label activeUser;

  @FXML
  public ToggleSwitch darkMode;

  @FXML
  public Label darkModeText;

  @FXML
  public Button btnAjouterEvent;
  @FXML
  private StackPane spSubScene;
  @FXML
  private Button btnHome;


  private DataModel dataModel;


  public void setSubSceneInitialNode() {





    darkMode.setSelected(dataModel.getActiveUser().isDarkMode());

    btnAjouterEvent.setDisable(!dataModel.getActiveUser().isTeacher());

    darkMode.selectedProperty().addListener((obs, oldVal, newVal) -> {
      URL darkModeStyle = getClass().getResource("css/dark-mode-style.css");
      URL liteModeStyle = getClass().getResource("css/lite-mode-style.css");
      darkModeText.setText(newVal ? "Dark Mode" : "Lite Mode");
      URL style = newVal ? darkModeStyle: liteModeStyle;
      System.out.println("newVal = " + newVal);
      Scene scene = darkMode.getScene();
      scene.getStylesheets().clear();
      scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
      scene.getStylesheets().add(Objects.requireNonNull(style).toExternalForm());

        if (dataModel.getActiveUser().isTeacher()) {
          updateDarkMode("data/teacher.txt", dataModel.getActiveUser().getUsername(), String.valueOf(newVal));
        }else{
            updateDarkMode("data/etudiant.txt", dataModel.getActiveUser().getUsername(), String.valueOf(newVal));
        }
    });
    activeUser.setText(dataModel.getActiveUser().getUsername());


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
