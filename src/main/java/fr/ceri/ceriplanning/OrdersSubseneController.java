package fr.ceri.ceriplanning;

import fr.ceri.ceriplanning.model.DataModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author blj0011
 */
public class OrdersSubseneController {

  private @FXML VBox vbRoot;
  private @FXML ListView<String> listView;

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

    listView.setItems(FXCollections.observableArrayList(model.simulateGetDataFromDatabase()));
  }
}
