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
    CreateConnexion individu = new CreateConnexion(username, password);

    switchToAcueil(event);

//    if (isStudent) {
//      statusLabel.setText("Connexion en tant qu'étudiant : " + username);
//      for (CreateConnexion e : c.getStudentList()) {
//        if (e.equals(individu)) {
//          switchToAcueil(event);
//        }
//      }
//    } else if (isProfessor) {
//      for (CreateConnexion e : c.getTeacherList()) {
//        if (e.equals(individu)) {
//          switchToAcueil(event);
//        }
//      }
//    } else {
//      statusLabel.setText("Veuillez sélectionner un type de compte.");
//    }
  }

  void switchToAcueil(ActionEvent event) {

    DataModel dataModel = new DataModel();
    System.out.println("DataModel created "+ dataModel.getActiveUser());

    // a utiliser pour gerer le mode sombre et le mode clair
    URL darkModeStyle = getClass().getResource("css/dark-mode-style.css");
    URL liteModeStyle = getClass().getResource("css/lite-mode-style.css");

    dataModel.activeUser = new User(usernameField.getText(), false, "M1-IA-IL-ALT" );
    System.out.println("active user "+ dataModel.getActiveUser());
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
      scene.getStylesheets().add(Objects.requireNonNull(darkModeStyle).toExternalForm());

      Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      stage.setTitle("CERI Planning");

      // Remplacer la scène actuelle par la nouvelle scène
      stage.setScene(scene);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
