package controller.manager;

import com.example.projecthr.ProjectManager;
import com.example.projecthr.project.*;
import com.example.projecthr.Meeting;
import com.example.projecthr.ProjectApplication;
import com.example.projecthr.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import utility.Factory;
import java.io.IOException;
import java.util.ArrayList;

public class PMDashboardController {
    User manager;
    ArrayList<Project> projects;
    ArrayList<Meeting> meetings;
    ArrayList<ProjectProposal> proposals;
    ArrayList<Task> tasks;

    @FXML private TableView<Task> taskTable;
    @FXML private Label nameLabel, meetingList, proposeList, taskLabLab, Lab;
    @FXML private Label projCount, deadCount, taskCount, requestCount;
    @FXML private ComboBox<String> priorityComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Pane todayPane;

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
        priorityComboBox.setOnAction(_ -> { taskFilter(priorityComboBox.getSelectionModel().getSelectedItem());});

        statusComboBox.getItems().addAll("Completed", "Pending", "Not started");
        statusComboBox.setOnAction(_ -> { taskFilter(statusComboBox.getSelectionModel().getSelectedItem());});


        Lab.setText("No tasks found.");
        Lab.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-text-size: 20;");
        Lab.setWrapText(true);
        Lab.setLayoutX(20);
        Lab.setLayoutY(60);
        Lab.setVisible(false);

        tasks = Factory.getProjectServices().fetchNextTasks(((ProjectManager) manager).getProjects());
        displayTasksInTable(tasks);

        deadCount.setText(String.valueOf(tasks.size()));
        taskCount.setText(String.valueOf(Factory.getProjectServices().getPendingTaskCount(tasks)));
        requestCount.setText(String.valueOf(Factory.getProjectServices().getPendingExtensionRequests(projects)));
    }

    @FXML
    protected void onLogoutButtonClick() {
        ProjectApplication.switchScene("LoginForm.fxml");
    }

    @FXML
    protected void onProfileButtonClick() throws IOException {
        ProjectApplication.switchScene("/manager/PMProfile.fxml");
    }
    @FXML
    protected void onProjectButtonClick()throws IOException {
        ProjectApplication.switchScene("/manager/PMProjects.fxml");
    }
    @FXML
    protected void onProposalButtonClick() throws IOException {
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

    public void displayTasksInTable(ArrayList<Task> tsktsk) {
        if(tsktsk.isEmpty()){
            taskTable.setVisible(false);
            taskTable.setDisable(true);
            Lab.setVisible(true);
            return;
        }

        taskTable.getColumns().clear();
        taskTable.setDisable(false); taskTable.setVisible(true); Lab.setVisible(false);
        TableColumn<Task, String> taskNameColumn = new TableColumn<>("Task Name");
        taskNameColumn.setCellValueFactory(new PropertyValueFactory<>("taskName"));

        TableColumn<Task, String> priorityColumn = new TableColumn<>("Priority");
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));

        TableColumn<Task, String> deadlineColumn = new TableColumn<>("Deadline");
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));

        TableColumn<Task, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        taskTable.getColumns().addAll(taskNameColumn, priorityColumn, deadlineColumn, statusColumn);

        ObservableList<Task> taskList = FXCollections.observableArrayList(tsktsk);
        taskTable.setItems(taskList);
    }

    private void taskFilter(String filter){
        ArrayList<Task> filteredTasks = Factory.getProjectServices().filterTasks(tasks, filter);
        displayTasksInTable(filteredTasks);
    }
}
