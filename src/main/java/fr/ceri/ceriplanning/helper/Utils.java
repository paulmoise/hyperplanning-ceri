package fr.ceri.ceriplanning.helper;
import java.awt.Desktop;

import fr.ceri.ceriplanning.model.DescriptionDetails;
import fr.ceri.ceriplanning.model.Event;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static fr.ceri.ceriplanning.helper.ICSFileParser.parseIcsFile;

public class Utils {


  public static DescriptionDetails getDescriptionDetailsFromHTML(String summary) {
    DescriptionDetails details = new DescriptionDetails();

    // Extract the X-ALT-DESC part
    String startIndicator = "X-ALT-DESC;FMTTYPE=text/html:";

    if (!summary.contains(startIndicator)) {
      return details;
    }
    String htmlPart = summary.substring(summary.indexOf(startIndicator) + startIndicator.length());

    // Split by <br/> to get each detail

    String regex = "<\\s*br\\s*/?>|<\\s*b\\s*r\\s*/>";


    String[] lines = htmlPart.replace("\n ", "").split(regex);
    for (String line : lines) {
      line = line.trim();
      if (line.contains(":")) {
        String[] parts = line.split(":", 2);
        if (parts.length < 2) continue; // Skip if there's no ":" in the line

        String key = parts[0].trim();
        String value = parts[1].trim();


        if (key.equals("Matière")) {
          details.setMatiere(value);
        } else if (key.replace(" ", "").equals("Enseignants") || key.replace(" ", "").equals("Enseignant")) {
          details.setEnseignant(value);
        } else if (key.replace(" ", "").equals("Salle")) {
          details.setSalle(value);
        } else if (key.replace(" ", "").equals("Promotions") || key.replace(" ", "").equals("Promotion")) {
          details.setTd(value);
        } else if (key.replace(" ", "").equals("Type")) {
          details.setType(value);
        } else if (key.strip().replace(" ", "").equals("TD")) {
          details.setTd(value);
        }
      }
    }

    return details;
  }


  public String parseDateIntoString(String s) {
    String dateString = "20240313T100900Z";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX").withZone(ZoneId.of("UTC"));
    Instant instant = Instant.from(formatter.parse(dateString));

    System.out.println(instant);
    return instant.toString();
  }


  public static int calculateNumberOf30MinIntervals(LocalDateTime startDate, LocalDateTime endDate) {

    // Calculate duration between the two instants
    Duration duration = Duration.between(startDate, endDate);
    // Convert duration to minutes and calculate the number of 30-minute intervals
    long totalMinutes = duration.toMinutes();
    return (int) (totalMinutes / 30);
  }

  public static String extractTime(LocalDateTime date) {
    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH'H'mm");
    return date.format(outputFormatter);
  }

  public static boolean isActiveWeekOfYearEqualToEventStartWeekOfYear(String givenDateString, int activeWeekOfYear) {

    // Create a formatter with the specified pattern
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

    // Parse the date string using the formatter
    LocalDateTime startEventDate = LocalDateTime.parse(givenDateString, formatter);
    // Convert activeDate to LocalDate for comparison
    LocalDate startEventDateLocalDate = startEventDate.toLocalDate();


    int weekOfYear = startEventDateLocalDate.get(WeekFields.of(Locale.FRANCE).weekOfYear());
    return activeWeekOfYear == weekOfYear;
  }

  public static boolean isValidDateFormat(String dateStr) {
    String regex = "^\\d{8}T\\d{6}Z$";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(dateStr);
    return matcher.matches();
  }

  public static int getDayOfWeek(LocalDateTime date) {
    Map<DayOfWeek, Integer> dayOfWeekMap = Map.of(
      DayOfWeek.MONDAY, 1,
      DayOfWeek.TUESDAY, 2,
      DayOfWeek.WEDNESDAY, 3,
      DayOfWeek.THURSDAY, 4,
      DayOfWeek.FRIDAY, 5);


    // Extract the day of the week from the ZonedDateTime
    DayOfWeek dayOfWeek = date.getDayOfWeek();

    // Use the day of the week to find the corresponding number from the map
    return dayOfWeekMap.getOrDefault(dayOfWeek, -1);
  }


  public static Map<String, Integer> generateTimeSlots(LocalTime startTime, LocalTime endTime, Duration slotDuration) {
    List<String> timeSlots = new ArrayList<>();
    LocalTime currentTime = startTime;
    int counter = 1; // Initialize counter to map slots to integers
    Map<String, Integer> formattedTimeSlotsWithCounter = new LinkedHashMap<>(); // Use LinkedHashMap to maintain order

    while (!currentTime.isAfter(endTime)) {
      timeSlots.add(currentTime.toString());
      currentTime = currentTime.plus(slotDuration);
    }

    for (String time : timeSlots) {
      String formattedTime = time.replace(":", "H"); // Adjusting format to include trailing "00" for minutes
      formattedTimeSlotsWithCounter.put(formattedTime, counter++);
    }

    return formattedTimeSlotsWithCounter;
  }


  public static LocalDateTime addOneHourToDate(String dateStr) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
    LocalDateTime dateTime = LocalDateTime.parse(dateStr, formatter);
    return dateTime.plusHours(2);
  }


  public static String getMonthFromWeek(int year, int weekOfYear) {

    // Creating a map of month numbers to month names
    Map<Integer, String> monthMap = new HashMap<>();
    monthMap.put(1, "Janvier");
    monthMap.put(2, "Février");
    monthMap.put(3, "Mars");
    monthMap.put(4, "Avril");
    monthMap.put(5, "Mai");
    monthMap.put(6, "Juin");
    monthMap.put(7, "Juillet");
    monthMap.put(8, "Août");
    monthMap.put(9, "Septembre");
    monthMap.put(10, "Octobre");
    monthMap.put(11, "Novembre");
    monthMap.put(12, "Décembre");


    Calendar calendar = Calendar.getInstance();

    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, Calendar.JANUARY);
    calendar.set(Calendar.DAY_OF_MONTH, 1);

    calendar.add(Calendar.WEEK_OF_YEAR, weekOfYear - 1);

    calendar.add(Calendar.DAY_OF_MONTH, 3);

    int month = calendar.get(Calendar.MONTH) + 1;

    return monthMap.get(month);
  }

  public static LocalDate getDateFromWeekAndDay(int year, int weekOfYear, int dayOfWeek) {
    // Use the ISO-8601 standard; it can be adjusted for different locales
    WeekFields weekFields = WeekFields.ISO;
    TemporalField weekOfYearField = weekFields.weekOfWeekBasedYear();
    TemporalField dayOfWeekField = weekFields.dayOfWeek();

    // Start from the first day of the year
    LocalDate date = LocalDate.of(year, 1, 1)
      // Move to the first day of the first week (to handle years where the first week starts in the previous year)
      .with(TemporalAdjusters.previousOrSame(weekFields.getFirstDayOfWeek()))
      // Then, adjust to the desired week of the year
      .with(weekOfYearField, weekOfYear)
      // Finally, adjust to the desired day of the week
      .with(dayOfWeekField, dayOfWeek);

    return date;
  }

  public static LocalDateTime getDateTimeFromYearMonthDay(int year, int month, int dayOfMonth) {
    // Create a LocalDate object from the year, month, and day
    LocalDate date = LocalDate.of(year, month, dayOfMonth);
    return date.atStartOfDay();
  }

  public static void main(String[] args) {
//        Map<String, Integer> timeSlots = generateTimeSlots(LocalTime.of(8, 0), LocalTime.of(19, 0), Duration.ofMinutes(30));
//        timeSlots.forEach((key, value) -> System.out.println(key + " => " + value));
//
//        String originalDate = "20231018T123000Z";
//        LocalDateTime newDate = addOneHourToDate(originalDate);
//        int n  = calculateNumberOf30MinIntervals(LocalDateTime.now(), LocalDateTime.now().plusHours(1));
//        System.out.println("Original date: " + originalDate);
//        System.out.println("New date after adding 1 hour: " + newDate);
//        System.out.println("number of 30 min: " + n);

//        int year = 2024; // Example year
//        int weekOfYear = 13; // Example week of the year
//        int dayOfWeek = 3; // Example day of the week (Wednesday)
//
//        LocalDate date = getDateFromWeekAndDay(year, weekOfYear, dayOfWeek);
//        System.out.println("The date is: " + date);

    String filePath = "data/calendar.ics";
    List<Event> events = parseIcsFile(filePath);

//        List<String> listEnseignant = getTeacherNames(events);
    //print all the enseignant
//        listEnseignant.forEach(System.out::println);


    List<String> salles = getMatiereList(events);
    salles.forEach(System.out::println);
  }


  private static ObservableList<String> getTeacherNames(List<Event> events) {

    ObservableList<String> observableList = FXCollections.observableArrayList();

    Set<String> listEnseignant = new HashSet<>();
    for (Event e : events) {
      if (e.getDescriptionDetails().getEnseignant() != null) {
        String enseignants = e.getDescriptionDetails().getEnseignant();
        if (enseignants.contains("\\")) {
          enseignants = enseignants.replaceAll("\\\\", ""); // Remove backslashes (if any
          String[] enseignantList = enseignants.split(",");
          for (String enseignant : enseignantList) {
            listEnseignant.add(enseignant.trim());
          }
        } else {
          listEnseignant.add(enseignants);
        }
      }
    }
    observableList.addAll(listEnseignant.stream().toList());
    return observableList;
  }

  public static ObservableList<String> getMatiereList(List<Event> events) {

    ObservableList<String> observableList = FXCollections.observableArrayList();

    Set<String> listMatiere = new HashSet<>();
    for (Event e : events) {
      if (e.getDescriptionDetails().getMatiere() != null) {
        listMatiere.add(e.getDescriptionDetails().getMatiere().trim());
      }
    }
    observableList.addAll(listMatiere.stream().toList());
    return observableList;
  }


  public static ObservableList<String> getListTypeCours() {
    ObservableList<String> observableList = FXCollections.observableArrayList();

    List<String> listTypeCours = new ArrayList<>();
    listTypeCours.add("CM");
    listTypeCours.add("TD");
    listTypeCours.add("TP");
    listTypeCours.add("CM/TD");
    listTypeCours.add("Conference");
    listTypeCours.add("Evaluation");
    observableList.addAll(listTypeCours);
    return observableList;
  }

  private ObservableList<String> getListPromotion() {
    ObservableList<String> listPromotion = FXCollections.observableArrayList();
    listPromotion.add("L1");
    listPromotion.add("L2");
    listPromotion.add("L3");
    listPromotion.add("M1");
    listPromotion.add("M2");
    return listPromotion;
  }

  public static ObservableList<String> getListGroupPedagogic() {
    ObservableList<String> listGroupPedagogic = FXCollections.observableArrayList();
    listGroupPedagogic.add("M1-IA-IL-ALT");
    listGroupPedagogic.add("M1-IA-IL-CLA");
    listGroupPedagogic.add("M1-ILSEN-alt-GR1");
    listGroupPedagogic.add("M1-ILSEN-alt-GR2");
    listGroupPedagogic.add("M1-ILSEN-cla-Gr1");
    listGroupPedagogic.add("M1-ILSEN-cla-Gr2");
    listGroupPedagogic.add("m1-sicom-alt");
    listGroupPedagogic.add("m1-sicom-cla");
    return listGroupPedagogic;
  }

  // get the list of salle
  public static ObservableList<String> getListSalle(List<Event> events) {
    List<String> exceptionListSalle = new ArrayList<>();
    ObservableList<String> observableList = FXCollections.observableArrayList();

    exceptionListSalle.add("Amphi Bl aise");
    exceptionListSalle.add("Amphi Bla ise");
    exceptionListSalle.add("Amphi Ad a");
    exceptionListSalle.add("Amph i Ada");
    exceptionListSalle.add("S1 = C 042 Nodes");
    Set<String> listSalle = new HashSet<>();
    for (Event e : events) {
      if (e.getDescriptionDetails().getSalle() != null && !exceptionListSalle.contains(e.getDescriptionDetails().getSalle())) {
        listSalle.add(e.getDescriptionDetails().getSalle());
      }
    }
    listSalle.add("S1 = C 042");
    observableList.addAll(listSalle.stream().toList());
    return observableList;
  }

  public static void sendEmailToTeacher(String teacherEmail) {
    if (teacherEmail != null && !teacherEmail.isEmpty()) {
      String mailtoLink = "mailto:" + teacherEmail;
      try {
        System.out.println("Ae.");
        Desktop.getDesktop().mail(new URI(mailtoLink));
      } catch (IOException | URISyntaxException ex) {
        ex.printStackTrace();
      }
    }

  }
  public static String constructMail(String name) {
    String[] parts = name.split(" ");
    if (parts.length >= 2) {
      String firstName = parts[0];
      String lastName = parts[1].replace("\\","").replace(",","");
      return lastName + "." + firstName + "@univ-avignon.fr";
    } else {
      return "";
    }
  }


  public static void updateDarkMode(String filePath, String uuid, String newValue) {
    try {
      // Read all lines from the file
      Path path = Paths.get(filePath);
      List<String> lines = Files.readAllLines(path);

      // Update the last value for lines that contain the UUID
      List<String> updatedLines = lines.stream()
        .map(line -> {
          if (line.contains(uuid)) {
            String[] parts = line.split(";");
            parts[parts.length - 1] = newValue; // Update the last part
            return String.join(";", parts);
          } else {
            return line;
          }
        })
        .collect(Collectors.toList());

      // Write the updated lines back to the file
      Files.write(path, updatedLines);
      System.out.println("File updated successfully.");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}

