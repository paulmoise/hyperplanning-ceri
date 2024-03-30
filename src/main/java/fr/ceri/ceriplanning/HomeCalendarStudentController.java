package fr.ceri.ceriplanning;

import fr.ceri.ceriplanning.model.DataModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Map;

import static fr.ceri.ceriplanning.helper.Utils.generateTimeSlots;

public class SettingsSubsceneController {
  private @FXML VBox vbRoot;

  private DataModel dataModel;



  VBox getVBoxRoot()
  {
    return vbRoot;
  }

  public void initModel(DataModel model) {
    if (this.dataModel != null) {
      throw new IllegalStateException("Model can only be initialized once");
    }
    this.dataModel = model ;

    System.out.println("SettingsSubsceneController initModel");



  }




  public void handleBtnOnActionHome(ActionEvent actionEvent) {
  }
}
