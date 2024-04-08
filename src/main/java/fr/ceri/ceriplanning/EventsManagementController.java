package fr.ceri.ceriplanning;

import fr.ceri.ceriplanning.helper.Utils;
import fr.ceri.ceriplanning.model.DataModel;
import fr.ceri.ceriplanning.model.DescriptionDetails;
import fr.ceri.ceriplanning.model.Event;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static fr.ceri.ceriplanning.helper.Utils.getListSalle;
import static fr.ceri.ceriplanning.helper.Utils.getListTypeCours;

/**
 * FXML Controller class
 *
 * @author blj0011
 */
public class EventsManagementController {
  @FXML
  public ComboBox<Integer> heureDebutComboBox;
  public DatePicker debut;
  public DatePicker DFinPicker;
  public ComboBox<String> HFinComboBox;
  @FXML
  public ComboBox<String> matiereComboBox;
  @FXML
  public ComboBox<String> formationComboBox;
  @FXML
  public ComboBox memoComboBox;
  @FXML
  public ComboBox typeComboBox;
  @FXML
  public ComboBox<String> salleComboBox;
  public VBox Formulaire;
  @FXML
  public ComboBox<String> HdebutComboBox;
  public DatePicker Ddebut;
  @FXML
  public ComboBox<String> typeevent;
  private @FXML AnchorPane vbRoot;
  //private @FXML ListView<String> listView;

  private DataModel dataModel;
  List<String> allSalle = new ArrayList<>();

  AnchorPane getVBoxRoot() {


    return vbRoot;
  }

  public void initModel(DataModel model) {
    if (this.dataModel != null) {
      throw new IllegalStateException("Model can only be initialized once");
    }
    this.dataModel = model;
    List<String> listMatiere = new ArrayList<>();
    List<String> listSalle = new ArrayList<>();
    List<String> listFormation = new ArrayList<>();
    List<String> listTime = new ArrayList<>();
    List<String> listType = getListTypeCours();

    for (int hour = 8; hour <= 19; hour++) {
      for (int minute = 00; minute < 60; minute += 30) {
        String time = String.format("%02dh%02d", hour, minute);
        listTime.add(time);

      }
    }


    for (Event e : this.dataModel.getEvents()) {
      listMatiere.add(e.getDescriptionDetails().getMatiere());
      listSalle.add(e.getDescriptionDetails().getSalle());
      allSalle.add(e.getDescriptionDetails().getSalle());
      listFormation.add(e.getDescriptionDetails().getTd());
    }


    typeevent.setItems(FXCollections.observableArrayList(listType));
    Set<String> uniqueMatiere = new HashSet<>(listMatiere);
    matiereComboBox.setItems(FXCollections.observableArrayList(uniqueMatiere));


    salleComboBox.setItems(FXCollections.observableArrayList(getListSalle(dataModel.getEvents())));

    List<String> formationList = new ArrayList<>(Arrays.asList("M1 IA", "M1 ILSEN", "M1 SICOM", "L3-INFORMATIQUE"));
    formationComboBox.setItems(FXCollections.observableArrayList(formationList));


    HdebutComboBox.setItems(FXCollections.observableArrayList(listTime));
    HFinComboBox.setItems(FXCollections.observableArrayList(listTime));


    // listView.setItems(FXCollections.observableArrayList(model.simulateGetDataFromDatabase()));
  }


  public void ajouterSeance(ActionEvent actionEvent) {
  }

  public void valider(ActionEvent actionEvent) {
    Formulaire.setVisible(true);
  }

  public void Presentiel(ActionEvent actionEvent) {
  }


  public static LocalTime parseHeure(String heure) {

    int heures = Integer.parseInt(heure.substring(0, 2));
    int minutes = Integer.parseInt(heure.substring(3, 5));
    return LocalTime.of(heures, minutes);

  }


  public String parseDateToString(LocalDateTime date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
    return date.format(formatter);
  }

  public void validerFormulaire(ActionEvent actionEvent) {
    Event e = new Event(); // Assurez-vous que la classe Event a un constructeur prenant les dates de début et de fin en paramètre
    String salle = salleComboBox.getValue();
    List<Event> listeAvailableSalleinEvent = new ArrayList<>();
    LocalDate tiledata = Ddebut.getValue(); // Récupérer la date sélectionnée dans le DatePicker
    String hdebut = HdebutComboBox.getValue(); // Récupérer l'heure de début sélectionnée dans le ComboBox
    String hfin = HFinComboBox.getValue(); // Récupérer l'heure de fin sélectionnée dans le ComboBox

    // Concaténer la date et l'heure de début et de fin pour créer des LocalDateTime
    LocalDateTime startDateTime = LocalDateTime.of(tiledata, parseHeure(hdebut));
    LocalDateTime updateDateTime = startDateTime.minusHours(2);
    LocalDateTime endDateTime = LocalDateTime.of(tiledata, parseHeure(hfin));
    LocalDateTime updateEndDateTime = endDateTime.minusHours(2);

    if (checksallestatus(startDateTime, endDateTime, salle)) {
      e.setDtEnd(parseDateToString(updateEndDateTime));
      e.setDtStart(parseDateToString(updateDateTime));
      DescriptionDetails d = new DescriptionDetails();
      d.setSalle(salle);
      d.setType(typeevent.getValue());
      System.out.println("active user " + dataModel.getActiveUser().toString());
      d.setEnseignant(dataModel.getActiveUser().getFullName());
      d.setTd(formationComboBox.getValue());
      d.setMatiere(matiereComboBox.getValue());
      e.setDescriptionDetails(d);
      this.dataModel.addEvent(e);
      // System.out.println(e.getDescriptionDetails().toString()+"______>"+endDateTime+ "-=====>" + startDateTime);
      showAlert("Succès", null, "Réservation réussie !", "success");


    } else {
      //for ( String s :getavailablesalle( startDateTime, endDateTime) ) {System.out.println(s+" alternative");}
      Set<String> uniquesalle = new HashSet<>(getavailablesalle(startDateTime, endDateTime));
      salleComboBox.setItems(FXCollections.observableArrayList(uniquesalle));
      showAlert("Succès", null, "Veuill rechoisir l'une des salle disponible actuellement  !", "danger");


    }


  }


  public boolean isOverlap(LocalDateTime startDateTime, LocalDateTime endDateTime, Event event) {
    // Récupérer la date et l'heure de début et de fin de l'événement
    LocalDateTime eventStartDateTime = null;
    LocalDateTime eventEndDateTime = null;
    try {
      eventStartDateTime = LocalDateTime.parse(event.getDtStart(), DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX"));
      eventEndDateTime = LocalDateTime.parse(event.getDtEnd(), DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX"));
    } catch (DateTimeParseException ex) {
      // Si le format "yyyyMMdd'T'HHmmssX" échoue, essayez d'ajouter manuellement les informations d'heure, de minute et de seconde
      String eventDateString = event.getDtStart() + "T000000Z"; // Ajouter l'heure, les minutes et les secondes à 0
      eventStartDateTime = LocalDateTime.parse(eventDateString, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX"));
      eventEndDateTime = LocalDateTime.parse(eventDateString, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX"));
    }
    System.out.println(eventStartDateTime);
    System.out.println(startDateTime);


    // Vérifier si les intervalles de temps se chevauchent
    return (startDateTime.isBefore(eventEndDateTime) || startDateTime.equals(eventEndDateTime)) &&
      (endDateTime.isAfter(eventStartDateTime) || endDateTime.equals(eventStartDateTime));
  }

  public boolean checksallestatus(LocalDateTime startDateTime, LocalDateTime endDateTime, String salle) {
    for (Event e : this.dataModel.getEvents()) {
      if (Utils.isValidDateFormat(e.getDtEnd()) && Utils.isValidDateFormat(e.getDtStart())) {
        String s = e.getDescriptionDetails().getSalle();
        //System.out.println(e.getDtEnd());
        if (s != null && s.equals(salle)) {
          if (isOverlap(startDateTime, endDateTime, e)) {

            return false;
          }
        }
      }
    }
    return true;
  }

  List<String> getavailablesalle(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    List<String> Availablesalle = new ArrayList<>();
    for (String salle : allSalle) {
      if (salle != null) {
        if (checksallestatus(startDateTime, endDateTime, salle)) {
          Availablesalle.add(salle);
        }
      }
    }
    return Availablesalle;
  }

  public void showAlert(String title, String header, String content, String alertType) {
    // Chargez le fichier CSS
    // scene.getStylesheets().add(getClass().getResource("alert-dialog.css").toExternalForm());

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);

    // Obtenez la fenêtre de dialogue de l'alerte
    DialogPane dialogPane = alert.getDialogPane();

    // Supprimez toutes les classes de style actuelles
    dialogPane.getStyleClass().removeAll("alert-success", "alert-info", "alert-warning", "alert-danger");

    // Ajoutez la classe d'alerte Bootstrap spécifiée à la fenêtre de dialogue
    dialogPane.getStyleClass().add("alert-" + alertType);

    // Ajoutez un bouton de confirmation à la boîte de dialogue
    alert.getButtonTypes().setAll(ButtonType.OK);

    // Affichez la boîte de dialogue et attendez la réponse de l'utilisateur
    alert.showAndWait();
  }
}


