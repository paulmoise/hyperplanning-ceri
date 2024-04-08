package fr.ceri.ceriplanning.helper;


import fr.ceri.ceriplanning.model.CreateConnexion;
import fr.ceri.ceriplanning.model.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Boolean.parseBoolean;

public class Conexion {

    private   ArrayList<User> studentList = new ArrayList<>();

    public ArrayList<User> getStudentList() {
        return studentList;
    }

    public void setStudentList(ArrayList<User> studentList) {
        this.studentList = studentList;
    }

    public ArrayList<User> getTeacherList() {
        return teacherList;
    }

    public void setTeacherList(ArrayList<User> teacherList) {
        this.teacherList = teacherList;
    }

    private  ArrayList<User> teacherList = new ArrayList<>();

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

    private void readTeacherFile(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(";");
            if (parts.length == 4) {
                teacherList.add(new User(parts[0],parts[1], true,  parts[2], "", parseBoolean(parts[3])));
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
            String[] parts = line.split(";");
            if (parts.length == 5) {
                studentList.add(new User(parts[0],parts[1], false,  parts[2], parts[3], parseBoolean(parts[4])));
            } else {
                System.err.println("Erreur: Format invalide dans le fichier des étudiants: " + line);
            }
        }
        reader.close();
    }
}
