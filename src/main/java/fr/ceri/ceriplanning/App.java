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
import java.net.URL;
import java.util.Objects;

public class App extends Application {



  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("login-view.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 500, 350);
    stage.setScene(scene);
    stage.show();
  }



//  @Override
//  public void start(Stage stage) throws IOException {
//    DataModel dataModel = new DataModel();
//    URL darkModeStyle = getClass().getResource("css/dark-mode-style.css");
//    URL liteModeStyle = getClass().getResource("css/lite-mode-style.css");
//
//    FXMLLoader rootLoader = new FXMLLoader(App.class.getResource("main-view.fxml"));
//
//    rootLoader.load();
//
//    MainViewController mainViewController = rootLoader.getController();
//    mainViewController.initModel(dataModel);
//    mainViewController.setSubSceneInitalNode();
//
//    Scene scene = new Scene(rootLoader.getRoot(), 1000, 750);
//    scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
//    scene.getStylesheets().add(Objects.requireNonNull(darkModeStyle).toExternalForm());
//    stage.setTitle("CERI Planning");
//
//    stage.setScene(scene);
//    stage.show();
//  }


  public static void main(String[] args) {
    launch();
  }

}