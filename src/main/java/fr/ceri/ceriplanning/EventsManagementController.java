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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    List<String> Allsalle =new ArrayList<>();
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
        List<String> listtypeCours = new ArrayList<>();
        List<String> listTime = new ArrayList<>();
        //List<String> listTime = new ArrayList<>();
        List<String> listType = new ArrayList<>();

        for (int hour = 8; hour <= 19; hour++) {
            for (int minute = 00; minute < 60; minute += 30) {
                String time = String.format("%02dh%02d", hour, minute);
                listTime.add(time);

            }
        }


        for (Event e : this.dataModel.getEvents()) {
            listMatiere.add(e.getDescriptionDetails().getMatiere());
            listSalle.add(e.getDescriptionDetails().getSalle());
            Allsalle.add(e.getDescriptionDetails().getSalle());
            listFormation.add(e.getDescriptionDetails().getTd());
            listType.add(e.getDescriptionDetails().getType());
            //System.out.println(e.getDtStart()+ " dstart ");
            //  System.out.println(e.getDtStamp()+ " STAMPS ");

      /* System.out.println(e.getDescriptionDetails().getType()+ " type ");
      System.out.println(e.getCategories()+ " categorie");
      System.out.println(e.getSummary()+ " summary");*/


        }

    /*    matiereComboBox.setItems(FXCollections.observableArrayList(listMatiere));
        salleComboBox.setItems(FXCollections.observableArrayList(listSalle));
        formationComboBox.setItems(FXCollections.observableArrayList(listFormation));
        HdebutComboBox.setItems(FXCollections.observableArrayList(listTime));
        HFinComboBox.setItems(FXCollections.observableArrayList(listTime));
*/       Set<String> uniqueType = new HashSet<>(listType);
        typeevent.setItems(FXCollections.observableArrayList(uniqueType));
        Set<String> uniqueMatiere = new HashSet<>(listMatiere);
        matiereComboBox.setItems(FXCollections.observableArrayList(uniqueMatiere));

        Set<String> uniqueSalle = new HashSet<>(listSalle);
        salleComboBox.setItems(FXCollections.observableArrayList(uniqueSalle));

        Set<String> uniqueFormation = new HashSet<>(listFormation);
        formationComboBox.setItems(FXCollections.observableArrayList(uniqueFormation));

        Set<String> uniqueTime = new HashSet<>(listTime);
        HdebutComboBox.setItems(FXCollections.observableArrayList(uniqueTime));
        HFinComboBox.setItems(FXCollections.observableArrayList(uniqueTime));


        // listView.setItems(FXCollections.observableArrayList(model.simulateGetDataFromDatabase()));
    }


    public void ajouterSeance(ActionEvent actionEvent) {
    }

    public void valider(ActionEvent actionEvent) {
        Formulaire.setVisible(true);
    }

    public void Presentiel(ActionEvent actionEvent) {
    }

  /*  public void validerFormulaire(ActionEvent actionEvent) {
        Event e = new Event(); // Assurez-vous que la classe Event a un constructeur prenant les dates de début et de fin en paramètre
        String salle = salleComboBox.getValue();
        List <Event> listeAvailableSalleinEvent =new ArrayList<>();
        LocalDate tiledata = Ddebut.getValue(); // Récupérer la date sélectionnée dans le DatePicker
        String hdebut = HdebutComboBox.getValue(); // Récupérer l'heure de début sélectionnée dans le ComboBox
        String hfin = HFinComboBox.getValue(); // Récupérer l'heure de fin sélectionnée dans le ComboBox

        // Concaténer la date et l'heure de début et de fin pour créer des LocalDateTime
        LocalDateTime startDateTime = LocalDateTime.of(tiledata, parseHeure(hdebut));
        LocalDateTime endDateTime = LocalDateTime.of(tiledata, parseHeure(hfin));

        boolean available = true; // Initialiser la disponibilité à true

        // Parcourir tous les événements pour vérifier la disponibilité
        for (Event event : this.dataModel.getEvents()) {


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

            // Vérifier si les intervalles de temps se chevauchent
            if ((startDateTime.isBefore(eventEndDateTime) || startDateTime.equals(eventEndDateTime)) &&
                    (endDateTime.isAfter(eventStartDateTime) || endDateTime.equals(eventStartDateTime))) {
                // Il y a chevauchement, donc la salle n'est pas disponible
                available = false;
                // Sortir de la boucle dès qu'un chevauchement est trouvé

            }
            else{// recuper les salle disponible a ce mommet en recueoerant les evenement
                if(event.getDescriptionDetails().getSalle() != null){
                    listeAvailableSalleinEvent.add(event);
                    //System.out.println(event.getDescriptionDetails().getSalle()+" cette salle est disponible "+event.getDescriptionDetails().getMatiere());

                }

            }
        }

        if (!listeAvailableSalleinEvent.isEmpty()) {
            System.out.println("Les salles disponibles pour cette période sont :");
            for (Event event : listeAvailableSalleinEvent) {
                System.out.println(event.getDescriptionDetails().getSalle() + " - " + event.getDescriptionDetails().getDescr);
            }
        } else {
            System.out.println("Aucune salle disponible pour cette période.");
        }

        // Afficher le résultat
        if (available ) {
            // si il ya des crebneuax libre  a la date chosi verifier  si les venement qui
            System.out.println("La salle est disponible pour la réservation à la date et l'heure sélectionnées.");
        } else {
            System.out.println("La salle n'est pas disponible à la date et l'heure sélectionnées.");
        }
    }
*/

    public static LocalTime parseHeure(String heure) {

        int heures = Integer.parseInt(heure.substring(0, 2));
        int minutes = Integer.parseInt(heure.substring(3, 5));
        return LocalTime.of(heures, minutes);

    }
// pas mal
  /*  public void validerFormulaire(ActionEvent actionEvent) {
        Event e = new Event(); // Assurez-vous que la classe Event a un constructeur prenant les dates de début et de fin en paramètre
        String salle = salleComboBox.getValue();
        List<Event> listeAvailableSalleinEvent = new ArrayList<>();
        LocalDate tiledata = Ddebut.getValue(); // Récupérer la date sélectionnée dans le DatePicker
        String hdebut = HdebutComboBox.getValue(); // Récupérer l'heure de début sélectionnée dans le ComboBox
        String hfin = HFinComboBox.getValue(); // Récupérer l'heure de fin sélectionnée dans le ComboBox

        // Concaténer la date et l'heure de début et de fin pour créer des LocalDateTime
        LocalDateTime startDateTime = LocalDateTime.of(tiledata, parseHeure(hdebut));
        LocalDateTime endDateTime = LocalDateTime.of(tiledata, parseHeure(hfin));

        boolean available = true; // Initialiser la disponibilité à true

        // Parcourir tous les événements pour vérifier la disponibilité
        for (Event event : this.dataModel.getEvents()) {
            String s = event.getDescriptionDetails().getSalle();
            if (s != null && event.getDescriptionDetails().getSalle().equals(salle)) {

                if (isOverlap(startDateTime, endDateTime, event)) {
                    // Il y a chevauchement, donc la salle n'est pas disponible
                    available = false;
                } else {
                    // S'il n'y a pas de chevauchement, ajouter l'événement à la liste des salles disponibles
                    if (event.getDescriptionDetails().getSalle() != null) {
                        listeAvailableSalleinEvent.add(event);
                        System.out.println(event.getDescriptionDetails().getSalle() + " cette salle est disponible " + event.getDtStart() + "****" + event.getDtEnd());
                    }
                }
            }
        }

        // Afficher le résultat
        if (available) {
            System.out.println("La salle est disponible pour la réservation à la date et l'heure sélectionnées.");
        } else {
            System.out.println("La salle n'est pas disponible à la date et l'heure sélectionnées.");
        }
    }*/
public void validerFormulaire(ActionEvent actionEvent) {
    Event e = new Event(); // Assurez-vous que la classe Event a un constructeur prenant les dates de début et de fin en paramètre
    String salle = salleComboBox.getValue();
    List<Event> listeAvailableSalleinEvent = new ArrayList<>();
    LocalDate tiledata = Ddebut.getValue(); // Récupérer la date sélectionnée dans le DatePicker
    String hdebut = HdebutComboBox.getValue(); // Récupérer l'heure de début sélectionnée dans le ComboBox
    String hfin = HFinComboBox.getValue(); // Récupérer l'heure de fin sélectionnée dans le ComboBox

    // Concaténer la date et l'heure de début et de fin pour créer des LocalDateTime
    LocalDateTime startDateTime = LocalDateTime.of(tiledata, parseHeure(hdebut));
    LocalDateTime endDateTime = LocalDateTime.of(tiledata, parseHeure(hfin));

    if(checksallestatus( startDateTime,  endDateTime, salle)){
        System.out.println("disponibles ");
        e.setDtEnd(String.valueOf(endDateTime));
        e.setDtStart(String.valueOf(startDateTime));
        DescriptionDetails d= new DescriptionDetails();
        d.setSalle(salle);
        d.setType("TP");
        d.setEnseignant("Moi");
        d.setTd(formationComboBox.getValue());
        e.setDescriptionDetails(d);
        this.dataModel.addEvent(e);
       // System.out.println(e.getDescriptionDetails().toString()+"______>"+endDateTime+ "-=====>" + startDateTime);
        showAlert("Succès", null, "Réservation réussie !", "success");


    }
    else {
        //for ( String s :getavailablesalle( startDateTime, endDateTime) ) {System.out.println(s+" alternative");}
        Set<String> uniquesalle = new HashSet<>(getavailablesalle( startDateTime, endDateTime));
        salleComboBox.setItems(FXCollections.observableArrayList(uniquesalle ));
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

    List<String> getavailablesalle(LocalDateTime startDateTime, LocalDateTime endDateTime){
        List<String> Availablesalle = new ArrayList<>();
        for (String salle : Allsalle) {
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


