package com.example.projecthr;

import java.sql.Connection;
import java.sql.ResultSet;

public class ManagerServices {
    private static ManagerServices instance;

    private ManagerServices() {}
    public static ManagerServices getInstance() {
        if (instance == null) {
            instance = new ManagerServices();
        }
        return instance;
    }

    public boolean loadProfile(User user) {
        ResultSet pmResult = null;
        Connection connection = null;

        try {
            connection = DBHandler.getConnection();
            String query = "SELECT * FROM ProjectManagers WHERE PM_id = ?";
            pmResult = Factory.getFactory().getDb().executeSelectQuery(query, user.getUserId());
            if (pmResult != null && pmResult.next()) {
                ((ProjectManager) user).setDepartment(pmResult.getString("department"));
                ((ProjectManager) user).setExperienceYears(pmResult.getInt("years_of_experience"));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            Factory.getFactory().getDb().closeResources(pmResult, null, connection);
        }
    }

}
