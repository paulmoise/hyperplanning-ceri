package fr.ceri.ceriplanning;

import fr.ceri.ceriplanning.model.DataModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class SettingsController {
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
