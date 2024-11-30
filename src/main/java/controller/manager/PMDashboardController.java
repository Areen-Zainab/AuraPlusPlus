package controller.manager;

import com.example.projecthr.ProjectManager;
import com.example.projecthr.project.*;
import com.example.projecthr.Meeting;
import com.example.projecthr.ProjectApplication;
import com.example.projecthr.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import utility.Factory;
import java.io.IOException;
import java.util.ArrayList;

public class PMDashboardController {
    User manager;
    ArrayList<Project> projects;
    ArrayList<Meeting> meetings;
    ArrayList<ProjectProposal> proposals;

    @FXML private Label nameLabel, meetingList, proposeList;
    @FXML private Label projCount, deadCount, taskCount, requestCount;
    @FXML private ComboBox<String> priorityComboBox;
    @FXML private ComboBox<String> statusComboBox;

    @FXML
    public void initialize() {
        setCurrentUser();
        nameLabel.setText(manager.getFirstName() + "'s Dashboard");

        meetings = ((ProjectManager)manager).getMeetings();
        int count = 1;
        StringBuilder mlist = new StringBuilder();
        for (Meeting meeting : meetings) {
            mlist.append((count++)).append(". ").append(meeting.getTitle()).append("\n").append(meeting.getMeetingDate()).append(' ').append(meeting.getMeetingTime()).append("\n\n");
        }
        meetingList.setText(mlist.toString());

        proposals = ((ProjectManager)manager).getProposals();
        if(!proposals.isEmpty()) {
            count = 1;
            mlist = new StringBuilder();
            for (ProjectProposal proposal : proposals) {
                mlist.append((count++)).append(". ").append(proposal.getTitle()).append("\n").append(proposal.getSubmission_date()).append("\n\n");
            }
            proposeList.setText(mlist.toString());
        }
        else {
            proposeList.setText("No current pending proposals.");
        }
        projCount.setText(String.valueOf(((ProjectManager)manager).getOngoingCount()));


        priorityComboBox.getItems().addAll("Urgent", "High", "Medium", "Low");
        statusComboBox.getItems().addAll("Completed", "Pending", "Not started");
    }

    @FXML
    protected void onLogoutButtonClick() {
        System.out.println("Logout Button clicked");
        ProjectApplication.switchScene("LoginForm.fxml");
    }

    @FXML
    protected void onProfileButtonClick() throws IOException {
        System.out.println("Profile Button clicked");
        ProjectApplication.switchScene("/manager/PMProfile.fxml");
    }
    @FXML
    protected void onProjectButtonClick()throws IOException {
        System.out.println("Project Button clicked");
        ProjectApplication.switchScene("/manager/PMProjects.fxml");
    }
    @FXML
    protected void onProposalButtonClick() throws IOException {
        System.out.println("Create Project Button clicked");
        ProjectApplication.switchScene("/manager/PMProposal.fxml");
    }

    @FXML protected void onMeetingButtonClick() {
        ProjectApplication.switchScene("/manager/manageMeetings.fxml");
    }


    @FXML
    protected void onWorkcalendarButtonClick() {
        System.out.println("Update Button clicked");
    }

    @FXML
    protected void onDashboardButtonClick() throws IOException {
        System.out.println("Dashboard Button clicked");
        ProjectApplication.switchScene("/manager/PMDashboard.fxml");
    }

    @FXML
    public void onMouseEntered(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color:  rgba(88,109,128,255); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }

    @FXML
    public void onMouseExited(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }

    @FXML
    public void onOtherMouseExit(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color:   rgba(23,39,52,255); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }

    @FXML
    public void onDarkMouseHoverEnter(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color: rgba(10, 10, 10, 0.5); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }

    @FXML
    public void onDarkMouseHoverExit(MouseEvent event) {
        if (event.getSource() instanceof Button button) {
            button.setStyle("-fx-background-color: rgba(68,85,101,255); -fx-text-fill: white;  -fx-background-radius: 20;");
        }
    }

    public void loadProfile() {

    }

    private void setCurrentUser(){
        manager = Factory.getFactory().getMainApp().getUser();
    }
}
