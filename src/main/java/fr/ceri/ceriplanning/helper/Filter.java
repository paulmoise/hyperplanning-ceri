package fr.ceri.ceriplanning.helper;





import fr.ceri.ceriplanning.model.Event;

import java.util.ArrayList;
import java.util.List;

import static fr.ceri.ceriplanning.helper.ICSFileParser.parseIcsFile;

public class Filter {

    private ArrayList<Event> listePersonnes;
    private ArrayList<Event> listeSalles;
    private ArrayList<Event> listeFormation;

    private String filePath = "data/calendar.ics"; // Replace with your file path
    private List<Event> events;

    public Filter() {
        listePersonnes = new ArrayList<>();
        listeSalles = new ArrayList<>();
        listeFormation = new ArrayList<>();
        events = parseIcsFile(filePath);
    }

    public ArrayList<Event> getPersonnesSchedule(String name) {
        listeSalles.clear(); // Efface les données précédentes
        if( name==null){

                return listePersonnes; // Si le nom est null, retournez la liste vide directement

        }
        for (Event e : events){
            String enseignant  = e.getDescriptionDetails().getEnseignant();
           // System.out.println(enseignant);
            if (enseignant != null && enseignant.replaceAll("\\s+", "").equalsIgnoreCase(name.replaceAll("\\s+", "")))  {
                listeSalles.add(e);
            }
        }
        return listeSalles;
    }
    public ArrayList<Event> getFormationSchedule(String name) {
        listeFormation.clear(); // Efface les données précédentes
        if (name == null) {
            return listeFormation; // Si le nom est null, retournez la liste vide directement
        }


        for (Event e : events) {
           String s = e.getDescriptionDetails().getTd();

            if  (s!= null && s.contains(name)) {
                listeFormation.add(e);
            }
        }
        return listeFormation;
    }

    /* public ArrayList<Event> getSalleSchedule(String name) {
        listePersonnes.clear(); // Efface les données précédentes
        for (Event e : events){
            String salle = e.getDescriptionDetails().getSalle();

            if (  salle != null && salle.equals(name)) {

                listePersonnes.add(e);
            }
        }
        return listePersonnes;
    }*/
   public ArrayList<Event> getSalleSchedule(String name) {
       listePersonnes.clear(); // Efface les données précédentes
       if (name == null) {
           return listePersonnes; // Si le nom est null, retournez la liste vide directement
       }
       for (Event e : events) {
           String salle = e.getDescriptionDetails().getSalle();
           if (salle != null && salle.replaceAll("\\s+", "").equalsIgnoreCase(name.replaceAll("\\s+", ""))) {
               listePersonnes.add(e);
           }
       }
       return listePersonnes;
   }



    public static void main(String[] args) {


        Filter f = new Filter();
        ArrayList<Event > listesalle =f.getSalleSchedule("S5 = C 024");
        ArrayList<Event > listeEnseignat  =f.getPersonnesSchedule("ESTEVE Yannick");
        for(Event e : listeEnseignat){
           // System.out.println(e.getDescriptionDetails());


        }
    }
}
