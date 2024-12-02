package utility;

import com.example.projecthr.ProjectApplication;
import utility.user.ClientServices;
import utility.user.EmployeeServices;
import utility.user.ManagerServices;
import utility.user.UserServices;

public class Factory {
    private ProjectApplication mainApp;

    private static Factory factory;
    private Factory() {}

    public static Factory getFactory(){
        if(factory == null) {
            factory = new Factory();
        }
        return factory;
    }

    public static DBHandler getDb() {
        return DBHandler.getInstance();
    }

    public static UserServices getUserServices() {
        return UserServices.getInstance();
    }

    public static ClientServices getClientServices() {
        return ClientServices.getInstance();
    }

    public ProjectApplication getMainApp() {
        if (mainApp == null) {
            mainApp = new ProjectApplication();
        }
        return mainApp;
    }

    public static ProjectUtility getProjectServices() {
        return ProjectUtility.getInstance();
    }

    public static ManagerServices getManagerServices() {
        return ManagerServices.getInstance();
    }

    public static MeetingUtility getMeetingServices() {
        return MeetingUtility.getInstance();
    }

    public static EmployeeServices getEmployeeServices(){
        return EmployeeServices.getInstance();
    }
}
