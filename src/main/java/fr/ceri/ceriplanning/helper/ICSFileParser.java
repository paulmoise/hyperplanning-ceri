package fr.ceri.ceriplanning.helper;


import fr.ceri.ceriplanning.model.Event;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static fr.ceri.ceriplanning.helper.Utils.getDescriptionDetailsFromHTML;


public class ICSFileParser {



    public static List<Event> parseIcsFile(String filePath) {
        List<Event> events = new ArrayList<>();
        boolean isSummary = false; // Flag to track when we are accumulating summary lines

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            Event event = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("BEGIN:VEVENT")) {
                    event = new Event();
                    isSummary = false; // Reset summary flag for the new event
                } else if (line.startsWith("END:VEVENT")) {
                    assert event != null;
                    event.setDescriptionDetails(getDescriptionDetailsFromHTML(event.getSummary()));
                    events.add(event);
                    event = null;
                    isSummary = false; // Reset summary flag at the end of the event
                } else if (event != null) {
                    if (isSummary) {
                        // If we are within a summary, accumulate the lines
                        event.setSummary(event.getSummary() + line.trim()+ " ");
                    } else if (line.startsWith("SUMMARY;")) {
                        event.setSummary( line.substring(line.indexOf(":") + 1) + " "); // Start accumulating summary lines
                        isSummary = true; // Set flag to start accumulating summary lines
                    } else {
                        // Split the line by the first colon to handle normal cases and special parameters
                        String[] parts = line.split(":", 2);
                        if (parts.length < 2) continue; // Skip if there's no ":" in the line

                        String fieldName = parts[0];
                        String value = parts[1].trim(); // Trim whitespace from the value

                        switch (fieldName) {
                            case "CATEGORIES":
                                event.setCategories(value);
                                break;
                            case "DTSTAMP":
                                event.setDtStamp(value);
                                break;
                            case "LAST-MODIFIED":
                                event.setLastModified(value);
                                break;
                            case "UID":
                                event.setUid(value);
                                break;
                            case "DTSTART":
                            case "DTSTART;VALUE=DATE":
                                event.setDtStart(value);
                                break;
                            case "DTEND":
                            case "DTEND;VALUE=DATE":
                                event.setDtEnd(value);
                                break;
                            default:
                                if (!fieldName.startsWith("SUMMARY")) {
                                    isSummary = false; // Reset summary flag if we encounter a new field
                                }
                                break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return events;
    }

    public static void main(String[] args) {
        String filePath = "data/calendar.ics"; // Replace with your file path
        List<Event> events = parseIcsFile(filePath);
        for (Event event : events) {
            System.out.println("**********************************************************");
            System.out.println("categories='" + event.getCategories() + '\n' +
                    "dtStamp='" + event.getDtStamp() + '\n' +
                    "lastModified='" + event.getLastModified() + '\n' +
                    "uid='" + event.getUid() + '\n' +
                    "dtStart='" + event.getDtStart() + '\n' +
                    "dtEnd='" + event.getDtEnd() + '\n' +
                    "description='" + event.getDescriptionDetails() + '\n' + // Trim the summary to remove any leading/trailing whitespace
                    "**********************************************************");

        }
    }
}
