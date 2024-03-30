package fr.ceri.ceriplanning;

import java.util.StringJoiner;

import fr.ceri.ceriplanning.model.DataModel;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class OverviewSubsceneController {
  private @FXML VBox vbRoot;
  private @FXML TextArea textArea;

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

    StringJoiner stringJoiner = new StringJoiner(", ");
    model.simulateGetDataFromDatabase().forEach(stringJoiner::add);
    textArea.setText(stringJoiner.toString());
  }
}