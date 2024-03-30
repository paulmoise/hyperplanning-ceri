package fr.ceri.ceriplanning.model;

public class DescriptionDetails {
  String matiere;
  String enseignant;
  String salle;
  String td;
  String type;

  @Override
  public String toString() {
    return "DescriptionDetails{" +
      "matiere='" + matiere + '\'' +
      ", enseignant='" + enseignant + '\'' +
      ", salle='" + salle + '\'' +
      ", td='" + td + '\'' +
      ", type='" + type + '\'' +
      '}';
  }

  public String getMatiere() {
    return matiere;
  }

  public void setMatiere(String matiere) {
    this.matiere = matiere;
  }

  public String getEnseignant() {
    return enseignant;
  }

  public void setEnseignant(String enseignant) {
    this.enseignant = enseignant;
  }

  public String getSalle() {
    return salle;
  }

  public void setSalle(String salle) {
    this.salle = salle;
  }

  public String getTd() {
    return td;
  }

  public void setTd(String td) {
    this.td = td;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
