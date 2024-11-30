package utility;

import com.example.projecthr.Meeting;
import com.example.projecthr.ProjectApplication;
import javafx.scene.control.Alert;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class MeetingUtility {
    private static MeetingUtility instance;
    private MeetingUtility() {}

    public static MeetingUtility getInstance() {
        if (instance == null){
            instance = new MeetingUtility();
        }
        return instance;
    }

    public ArrayList<Meeting> Filter(ArrayList<Meeting> meetings, String filter) {
        if (filter != null && !filter.equals("All")) {
            if(filter.equalsIgnoreCase("Scheduled") || filter.equalsIgnoreCase("Completed")
                    || filter.equalsIgnoreCase("Cancelled") || filter.equalsIgnoreCase("Request Pending")) {
                meetings = FilterStatusMeetings(meetings, filter);
            }
            else if(filter.equalsIgnoreCase("Latest") || filter.equalsIgnoreCase("Oldest")){
                timeOrder(meetings, filter);
            }
        }
        return meetings;
    }

    public ArrayList<Meeting> filterMeetings(ArrayList<Meeting> meetings, String query) {
        String lowerCaseQuery = query.toLowerCase();

        return  new ArrayList<>(
                meetings.stream()
                        .filter(meeting -> meeting.getAgenda().toLowerCase().contains(lowerCaseQuery)
                                || meeting.getLocation().toLowerCase().contains(lowerCaseQuery)
                                || meeting.getTitle().toLowerCase().contains(lowerCaseQuery)
                                || meeting.getStatus().toLowerCase().contains(lowerCaseQuery))
                        .toList()
        );
    }

    public ArrayList<Meeting> FilterStatusMeetings(ArrayList<Meeting> meetings, String status){
        return meetings.stream()
                .filter(meeting -> (meeting.getStatus().equalsIgnoreCase(status)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void timeOrder(ArrayList<Meeting> meetings, String filter) {
        meetings.sort((m1, m2) -> {
            if (filter.equals("Oldest")) {
                return Comparator
                        .comparing(Meeting::getMeetingDate)
                        .thenComparing(Meeting::getMeetingTime)
                        .compare(m1, m2);
            } else {
                return Comparator
                        .comparing(Meeting::getMeetingDate)
                        .thenComparing(Meeting::getMeetingTime)
                        .reversed()
                        .compare(m1, m2);
            }
        });
    }

    public Meeting scheduleMeeting(int host, int recipient, int projectId, String title, String agenda, LocalDate date, Time time, String location, String address, String priority){
        location = (location != null)? (location + (address != null ? ": " + address : "")) : null;
        Meeting meet = new Meeting(0, host, recipient, projectId, title, agenda, date, time.toLocalTime(), location, "Request Pending", priority, null, null, null);
        try {
            meet.saveMeeting();
            return meet;
        }
        catch (Exception e){
            ProjectApplication.showAlert(Alert.AlertType.ERROR, "Unexpected Error", "Meeting could not be saved!");
            return null;
        }
    }
}

