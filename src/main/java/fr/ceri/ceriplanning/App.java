package fr.ceri.ceriplanning;

import fr.ceri.ceriplanning.model.DataModel;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

public class App extends Application {



//  @Override
//  public void start(Stage stage) throws IOException {
//    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("hello-view.fxml"));
//    Scene scene = new Scene(fxmlLoader.load(), 320, 240);
//    stage.setTitle("Hello!");
//    stage.setScene(scene);
//    stage.show();
//  }

  @Override
  public void start(Stage stage) throws IOException {
    DataModel dataModel = new DataModel();

    FXMLLoader rootLoader = new FXMLLoader(App.class.getResource("main-view.fxml"));

    rootLoader.load();

    MainViewController mainViewController = rootLoader.getController();
    mainViewController.initModel(dataModel);
    mainViewController.setSubSceneInitalNode();

    Scene scene = new Scene(rootLoader.getRoot(), 1000, 750);
    scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

    stage.setScene(scene);
    stage.show();
  }


  public static void main(String[] args) {
    launch();
  }

}