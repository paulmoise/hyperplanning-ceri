package fr.ceri.prototypeinterface.ceriplanning.helper;

import fr.ceri.prototypeinterface.ceriplanning.model.CreateConnexion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Conexion {

    private   ArrayList<CreateConnexion> studentList = new ArrayList<>();

    public ArrayList<CreateConnexion> getStudentList() {
        return studentList;
    }

    public void setStudentList(ArrayList<CreateConnexion> studentList) {
        this.studentList = studentList;
    }

    public ArrayList<CreateConnexion> getTeacherList() {
        return teacherList;
    }

    public void setTeacherList(ArrayList<CreateConnexion> teacherList) {
        this.teacherList = teacherList;
    }

    private  ArrayList<CreateConnexion> teacherList = new ArrayList<>();

    public static final String teacherFilename = "data/teacher.txt";
    public static final String studentFilename = "data/etudiant.txt";

    public Conexion() {
        readFiles();
    }

    private void readFiles() {
        try {
            readTeacherFile(teacherFilename);
            readStudentFile(studentFilename);
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture des fichiers.");
            e.printStackTrace();
        }
    }

    private void readTeacherFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            if (parts.length == 2) {
                teacherList.add(new CreateConnexion(parts[0], parts[1]));
            } else {
                System.err.println("Erreur: Format invalide dans le fichier des enseignants: " + line);
            }
        }
        reader.close();
    }

    private void readStudentFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            if (parts.length == 2) {
                studentList.add(new CreateConnexion(parts[0], parts[1]));
            } else {
                System.err.println("Erreur: Format invalide dans le fichier des étudiants: " + line);
            }
        }
        reader.close();
    }
}
