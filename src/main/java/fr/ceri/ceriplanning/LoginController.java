package fr.ceri.ceriplanning;

import fr.ceri.ceriplanning.helper.Conexion;
import fr.ceri.ceriplanning.model.CreateConnexion;
import fr.ceri.ceriplanning.model.DataModel;
import fr.ceri.ceriplanning.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class LoginController {
  @FXML
  private TextField usernameField;

  @FXML
  private PasswordField passwordField;

  @FXML
  private CheckBox studentCheckbox;

  @FXML
  private CheckBox professorCheckbox;

  @FXML
  private Label statusLabel;


  Conexion c = null;

  @FXML
  public void initialize() {
    c = new Conexion();
  }

  @FXML
  void handleLogin(ActionEvent event) {
    String username = usernameField.getText();
    String password = passwordField.getText();

    boolean isStudent = studentCheckbox.isSelected();
    boolean isProfessor = professorCheckbox.isSelected();
    User user = new User(username, password);

//    switchToAcueil(event, new User("uapv2400954", "123", false, "GANGBADJA Paul", "IA", false ));

    if (isStudent) {
      statusLabel.setText("Identifiant ou mot de passe incorrect!");
      for (User e : c.getStudentList()) {
        if (e.equals(user)) {
          System.out.println("user " + e);
          switchToAcueil(event, e);
        }
      }
    } else if (isProfessor) {
      statusLabel.setText("Identifiant ou mot de passe incorrect!");
      for (User e : c.getTeacherList()) {
        if (e.equals(user)) {
          switchToAcueil(event, e);
        }
      }
    } else {
      statusLabel.setText("Veuillez sélectionner un type de compte.");
    }
  }

  void switchToAcueil(ActionEvent event, User user) {

    DataModel dataModel = new DataModel();


    // a utiliser pour gerer le mode sombre et le mode clair
    URL darkModeStyle = getClass().getResource("css/dark-mode-style.css");
    URL liteModeStyle = getClass().getResource("css/lite-mode-style.css");

    dataModel.setActiveUser(user);
    boolean isDarkMode = dataModel.getActiveUser().isDarkMode();
    System.out.println("isDarkMode " + isDarkMode);
    URL style = isDarkMode ? darkModeStyle: liteModeStyle;

    System.out.println("active user " + dataModel.getActiveUser());
    try {

      FXMLLoader rootLoader = new FXMLLoader(App.class.getResource("main-view.fxml"));
      rootLoader.load();

      MainViewController mainViewController = rootLoader.getController();
      mainViewController.initModel(dataModel);
      mainViewController.setSubSceneInitialNode();
      Scene scene = new Scene(rootLoader.getRoot(), 1000, 750);

      // ajouter les styles
      scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

      // ajouter un if pour gerer le mode sombre et le mode clair
      scene.getStylesheets().add(Objects.requireNonNull(style).toExternalForm());

      Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

      // Remplacer la scène actuelle par la nouvelle scène
      stage.setScene(scene);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
