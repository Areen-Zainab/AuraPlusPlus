use worksphere;
drop database worksphere;
CREATE DATABASE worksphere ;
use worksphere;

-- 1 -- final
CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    Fname VARCHAR(50) NOT NULL,
    Lname VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('Employee', 'Project Manager', 'HR Manager', 'Client') NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    gender ENUM('Male', 'Female', 'Other'),
    DoB DATE,
    phone_no VARCHAR(20),
    address VARCHAR(255),
    join_date DATE NOT NULL,
    pfp_image BLOB -- this is to store pfp if needed
);

-- 2 -- final bas
CREATE TABLE Clients (
    client_id INT NOT NULL PRIMARY KEY,
    company_name VARCHAR(100),
    independent boolean,
    FOREIGN KEY (client_id) REFERENCES Users(user_id)
);

-- 3
CREATE TABLE HR_Managers (
    HR_id INT PRIMARY KEY,
    department VARCHAR(50) NOT NULL,
    years_of_experience INT NOT NULL,
    FOREIGN KEY (HR_id) REFERENCES Users(user_id)
);

-- 4
CREATE TABLE Employees (
    employee_id INT PRIMARY KEY,
    department VARCHAR(50) NOT NULL,
    position VARCHAR(50) NOT NULL,
    salary DECIMAL(10,2) NOT NULL,
    join_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- work_calendar_id INT,
    FOREIGN KEY (employee_id) REFERENCES Users(user_id)
    -- FOREIGN KEY (work_calendar_id) REFERENCES WorkCalendar(calendar_id)
);

-- 5
CREATE TABLE Skills (
    skill_id INT AUTO_INCREMENT PRIMARY KEY,
    skill_name VARCHAR(50) UNIQUE NOT NULL,
    skill_description TEXT
);

-- 6
CREATE TABLE EmployeeSkills (
    employee_id INT NOT NULL,
    skill_id INT NOT NULL,
    proficiency ENUM('Professional', 'Skilled', 'Beginner') NOT NULL,
    PRIMARY KEY (employee_id, skill_id),
    FOREIGN KEY (employee_id) REFERENCES Employees(employee_id),
    FOREIGN KEY (skill_id) REFERENCES Skills(skill_id)
);

-- 7
CREATE TABLE ProjectManagers (
    PM_id INT PRIMARY KEY,
    department VARCHAR(100) NOT NULL,
    years_of_experience INT NOT NULL,
    FOREIGN KEY (PM_id) REFERENCES Users(user_id)
);

-- 8 -- final table
CREATE TABLE ProjectProposal (
    ProposalID INT AUTO_INCREMENT PRIMARY KEY,
    project_manager_id INT,				-- intially null, added when approved
    client_id INT NOT NULL,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    duration varchar(50) NOT NULL,
    budget DECIMAL(12,2) NOT NULL,
    submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    review_date TIMESTAMP NULL,                          -- Updated when reviewed
    comments TEXT,
	pdf_path VARCHAR(255),                               -- Stores the file path of the proposal PDF
    status ENUM('Pending', 'Approved', 'Rejected') DEFAULT 'Pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (project_manager_id) REFERENCES ProjectManagers(PM_id),
    FOREIGN KEY (client_id) REFERENCES Clients(client_id)
);

-- 9 -- final i think
CREATE TABLE Projects (
    project_id INT AUTO_INCREMENT PRIMARY KEY,
    proposal_id INT NOT NULL,
    client_id INT NOT NULL,
    manager_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    status ENUM('Not Started','Active', 'Completed', 'Cancelled', 'Delayed') NOT NULL DEFAULT 'Not Started',
    final_cost DECIMAL(12,2),
    final_report VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES Clients(client_id),
    FOREIGN KEY (manager_id) REFERENCES ProjectManagers(PM_id),
    FOREIGN KEY (proposal_id) REFERENCES ProjectProposal(ProposalID)
);

-- 10
CREATE TABLE Milestones (
    milestoneID INT AUTO_INCREMENT PRIMARY KEY,
    project_id INT NOT NULL,
    milestone_name VARCHAR(100) NOT NULL,
    description TEXT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    comments TEXT,
    status ENUM('In Progress', 'Completed', 'Delayed') NOT NULL DEFAULT 'In Progress',
    priority ENUM('Low', 'Medium', 'High') DEFAULT 'Medium',
    milestone_report VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES Projects(project_id)
);

-- 11
CREATE TABLE Tasks (
    task_id INT AUTO_INCREMENT PRIMARY KEY,
    milestone_id INT NOT NULL,
    task_name VARCHAR(100) NOT NULL,
    description TEXT,
    deadline DATE NOT NULL,
    assigned_to INT NOT NULL,                             -- References Users table (Employee or Project Manager)
    assigned_date DATE NOT NULL,                                  -- Start date of the task
    priority ENUM('Low', 'Medium', 'High') DEFAULT 'Medium',
    status ENUM('Pending', 'Completed', 'Rejected - Re-Do') DEFAULT 'Pending',
    comments TEXT,
    task_details VARCHAR(255) DEFAULT NULL,
    task_attachment VARCHAR(255) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Timestamp for last update
    FOREIGN KEY (milestone_id) REFERENCES Milestones(milestoneID),
    FOREIGN KEY (assigned_to) REFERENCES Employees(employee_id)
);

-- 12
CREATE TABLE TaskReview (
    review_id INT AUTO_INCREMENT PRIMARY KEY,                -- Unique identifier for each review
    task_id INT NOT NULL,                                     -- Reference to the associated task
    reviewer_id INT NOT NULL,                                 -- ID of the person reviewing the task
    review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,          -- Date and time when the review was submitted
    status ENUM('Accepted', 'Rejected') NOT NULL,             -- Review decision (Accepted or Rejected)
    comments TEXT,                                            -- Comments provided by the reviewer
    feedback TEXT,                                            -- Detailed feedback or suggestions from the reviewer
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,           -- Timestamp when the review record was created
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Timestamp for last update
    FOREIGN KEY (task_id) REFERENCES Tasks(task_id),           -- Foreign key to the Tasks table
    FOREIGN KEY (reviewer_id) REFERENCES ProjectManagers(PM_id)       -- Foreign key to the Users table for reviewer identification
);

-- 13
CREATE TABLE TaskExtensions (
    extension_id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT NOT NULL,
    requested_by INT NOT NULL,                            -- References Employees table
    reason TEXT NOT NULL,
    new_deadline DATE NOT NULL,
    status ENUM('Pending', 'Approved', 'Rejected') DEFAULT 'Pending',
    FOREIGN KEY (task_id) REFERENCES Tasks(task_id),
    FOREIGN KEY (requested_by) REFERENCES Employees(employee_id)
);

-- 15 -- final table
CREATE TABLE Meetings (
    meeting_id INT AUTO_INCREMENT PRIMARY KEY, -- Unique identifier for the meeting
    host_id INT NOT NULL,                     -- ID of the host (Client or Project Manager)
    recipient_id INT NOT NULL,                -- ID of the recipient
    project_id INT NOT NULL,                  -- Related project ID
    agenda TEXT NOT NULL,                     -- Meeting agenda
    meeting_date DATE NOT NULL,                        -- Scheduled meeting date (nullable for pending requests)
    meeting_time TIME NOT NULL,                        -- Scheduled meeting time (nullable for pending requests)
    location VARCHAR(100),                   -- Meeting location (nullable for pending requests)
    meeting_title TEXT,
    priority ENUM('High', 'Medium', 'Low') DEFAULT 'Medium',      -- Meeting priority
    status ENUM('Request Pending', 'Scheduled', 'Rejected', 'Cancelled', 'Completed') DEFAULT 'Request Pending', -- Status
    minutes_file_path VARCHAR(255),          -- File path to meeting minutes (nullable)
    cancellation_reason TEXT,                -- Reason for cancellation (nullable)
    reschedule_notes TEXT,                   -- Notes for rescheduling (nullable)
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Timestamp when the request was created
    FOREIGN KEY (host_id) REFERENCES Users(user_id),  -- Host reference
    FOREIGN KEY (recipient_id) REFERENCES Users(user_id), -- Recipient reference
    FOREIGN KEY (project_id) REFERENCES Projects(project_id) -- Project reference
);

CREATE TABLE ProjectHistory (
    history_id INT AUTO_INCREMENT PRIMARY KEY,  -- Unique identifier for each history entry
    project_id INT NOT NULL,                     -- The related project
    milestone_id INT DEFAULT NULL,               -- Optional milestone associated with the action
    task_id INT DEFAULT NULL,                    -- Optional task associated with the action
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- When the action occurred
    actionType ENUM('Viewed', 'Printed', 'Downloaded', 'Created', 'Updated', 'Completed', 'Deleted', 'Status Changed') NOT NULL,
    description TEXT,                             -- Description of the action
    user_id INT,                                  -- ID of the user who performed the action
    FOREIGN KEY (project_id) REFERENCES Projects(project_id),
    FOREIGN KEY (milestone_id) REFERENCES Milestones(milestoneID) ON DELETE SET NULL,
    FOREIGN KEY (task_id) REFERENCES Tasks(task_id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE SET NULL
);

-- 17
-- log table
CREATE TABLE ViewHistory (
    view_id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT NOT NULL,
    history_id INT NOT NULL,
    view_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES Clients(client_id),
    FOREIGN KEY (history_id) REFERENCES ProjectHistory(history_id)
);

-- HR related Tables
-- 18
CREATE TABLE LeaveRequests (
    LeaveID INT AUTO_INCREMENT PRIMARY KEY,
    EmployeeID INT NOT NULL,                      -- References Employee Table
    LeaveType VARCHAR(30) NOT NULL,               -- e.g., Sick, Annual, Maternity
    StartDate DATE NOT NULL,
    EndDate DATE NOT NULL,
    Reason VARCHAR(255),
    status ENUM('Pending', 'Approved', 'Rejected') DEFAULT 'Pending',         -- e.g., Pending, Approved, Rejected
    AppliedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (EmployeeID) REFERENCES Employees(employee_id)
);

-- 19
CREATE TABLE Attendance (
    AttendanceID INT AUTO_INCREMENT PRIMARY KEY,
    EmployeeID INT NOT NULL,
    Date DATE NOT NULL,
    CheckInTime TIME,                             -- Time of check-in
    CheckOutTime TIME,                            -- Time of check-out
    Status VARCHAR(20) NOT NULL,                  -- e.g., Present, Absent, On Leave
    HoursWorked DECIMAL(5,2),
    FOREIGN KEY (EmployeeID) REFERENCES Employees(employee_id)
);

-- 20
CREATE TABLE HRReports (
    report_id INT AUTO_INCREMENT PRIMARY KEY,
    generated_by INT NOT NULL,
    employee_id INT NOT NULL,
    report_type ENUM('Attendance', 'Leave', 'Performance', 'Payroll') NOT NULL,
    generated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    report_data JSON,
    FOREIGN KEY (generated_by) REFERENCES HR_Managers(HR_id),
    FOREIGN KEY (employee_id) REFERENCES Employees(employee_id)
);

-- --------------------------------------------------------------------------------------
-- Logging Tables

CREATE TABLE ClientLog (
    log_id INT AUTO_INCREMENT PRIMARY KEY,           -- Unique identifier for the log entry
    client_id INT NOT NULL,                          -- The client who is affected by the change
    table_name VARCHAR(50) NOT NULL,                 -- The name of the table where the change occurred
    action_type ENUM('Created', 'Updated', 'Deleted') NOT NULL, -- Type of action (INSERT, UPDATE, DELETE)
    affected_record_id INT NOT NULL,                 -- ID of the record in the affected table (e.g., user_id, proposal_id, etc.)
    description TEXT,                                -- A detailed description of the action taken
    change_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- The timestamp of when the change occurred
    notification_seen ENUM('Yes', 'No') DEFAULT 'No',
    FOREIGN KEY (client_id) REFERENCES Clients(client_id) -- Foreign key reference to Clients table
);
-- -----------------------------------------------------------------------------------------
-- Procedures + functions

DELIMITER $$
CREATE PROCEDURE LogClientChange(
    IN p_client_id INT,
    IN p_table_name VARCHAR(50),
    IN p_action_type ENUM('Inserted', 'Created', 'Updated', 'Deleted'),
    IN p_affected_record_id INT,
    IN p_description TEXT
)
BEGIN
    INSERT INTO ClientLog (client_id, table_name, action_type, affected_record_id, description)
    VALUES (p_client_id, p_table_name, p_action_type, p_affected_record_id, p_description);
END $$

CREATE PROCEDURE InsertProjectHistoryProc(
    IN project_id INT,
    IN milestone_id INT,
    IN task_id INT,
    IN action_type ENUM('Created', 'Updated', 'Status Changed', 'Deleted'),
    IN description TEXT,
    IN user_id INT
)
BEGIN
    INSERT INTO ProjectHistory (project_id, milestone_id, task_id, actionType, description, user_id)
    VALUES (project_id, milestone_id, task_id, action_type, description, user_id);
END $$


CREATE FUNCTION GetUserName(user_id INT)
RETURNS VARCHAR(101)
DETERMINISTIC
BEGIN
    DECLARE full_name VARCHAR(101);
    SELECT CONCAT(Fname, ' ', Lname) INTO full_name
    FROM Users
    WHERE user_id = user_id
    LIMIT 1;
    RETURN full_name;
END $$

CREATE FUNCTION GetProjectName(project_id INT)  -- Return the project name
RETURNS VARCHAR(255)
DETERMINISTIC
BEGIN
    DECLARE project_name VARCHAR(255);
    SELECT title INTO project_name
    FROM Projects p
    JOIN ProjectProposal pp ON p.proposal_id = pp.ProposalID
    WHERE p.project_id = project_id
    LIMIT 1;
    RETURN project_name;
END $$

-- triggers
-- meeting
CREATE TRIGGER AfterMeetingInsert
AFTER INSERT ON Meetings
FOR EACH ROW
BEGIN
    IF EXISTS (SELECT 1 FROM Clients WHERE client_id = NEW.host_id) THEN
        CALL LogClientChange(
            NEW.host_id, 'Meetings', 'Created', NEW.meeting_id, CONCAT('New meeting request, ', NEW.meeting_title, ', created by the client (Host) for the project: ', GetProjectName(new.project_id))  -- Description
        );
    END IF;

    IF EXISTS (SELECT 1 FROM Clients WHERE client_id = NEW.recipient_id) THEN
        CALL LogClientChange(
            NEW.recipient_id, 'Meetings', 'Created', NEW.meeting_id, CONCAT('New meeting request, ',NEW.meeting_title, ', created by the Project Manager (Host) for the project: ', GetProjectName(new.project_id))  -- Description
        );
    END IF;
END $$


CREATE TRIGGER AfterProjectProposalInsert
AFTER INSERT ON ProjectProposal
FOR EACH ROW
BEGIN
    CALL LogClientChange(
        NEW.client_id, 'ProjectProposal', 'Created', NEW.ProposalID, CONCAT('Client ', GetUserName(NEW.client_id), ' submitted a new project proposal: ', NEW.title));
END $$

CREATE TRIGGER AfterProjectProposalUpdate
AFTER UPDATE ON ProjectProposal
FOR EACH ROW
BEGIN
    CALL LogClientChange(
        NEW.client_id, 'ProjectProposal', 'Updated', NEW.ProposalID, CONCAT('Client updated the project proposal: ', NEW.title)  -- Description
    );
END $$

CREATE TRIGGER AfterProjectInsert
AFTER INSERT ON Projects
FOR EACH ROW
BEGIN
	-- client_id, table name, action, action_id, description
    CALL LogClientChange( NEW.client_id, 'Projects', 'Created', NEW.project_id, CONCAT('Project Proposal (', GetProjectName(NEW.project_id), ') accepted. New project has been started for the client ', GetUserName(NEW.client_id),' .'));
	CALL InsertProjectHistoryProc( NEW.project_id, NULL, NULL, 'Created', CONCAT('New project "', GetProjectName(NEW.project_id), '" has been started for the client ', GetUserName(NEW.client_id),' .'), NEW.manager_id);
END $$

CREATE TRIGGER AfterProjectUpdate
AFTER UPDATE ON Projects
FOR EACH ROW
BEGIN
	-- log client relevant changes
    CALL LogClientChange(NEW.client_id, 'Projects', 'Updated', NEW.project_id, CONCAT('Project (', GetProjectName(NEW.project_id), ' for the Client, ', GetUserName(NEW.client_id), ') details were updated.'));
	IF NEW.status <> OLD.status THEN
        CALL InsertProjectHistoryProc(
            NEW.project_id, NULL, NULL, 'Status Changed',
            CONCAT('Project status changed from "', OLD.status, '" to "', NEW.status, '".'),
            NULL -- user_id
        );
    END IF;

    IF NEW.start_date <> OLD.start_date OR NEW.end_date <> OLD.end_date THEN
        CALL InsertProjectHistoryProc(
            NEW.project_id, NULL, NULL,
            'Updated', CONCAT('The project ,', GetProjectName(project_id), '\'s, timeline was updated.'),
            NULL                          -- user_id (optional, NULL if not available)
        );
    END IF;
END $$

CREATE TRIGGER AfterTaskInsert
AFTER INSERT ON Tasks
FOR EACH ROW
BEGIN
    CALL InsertProjectHistoryProc(
        (SELECT project_id FROM Milestones WHERE milestoneID = NEW.milestone_id LIMIT 1),                                       -- project_id associated with the task
        NEW.milestone_id, -- milestone_id related to the task
        NEW.task_id,                                          -- task_id of the newly created task
        'Created',                                            -- Action type for task creation
        CONCAT('Task "', NEW.task_name, '" has been created for milestone "', (SELECT milestone_name FROM Milestones WHERE milestoneID = NEW.milestone_id LIMIT 1), '" in Project, ', GetProjectName((SELECT project_id FROM Milestones WHERE milestoneID = NEW.milestone_id LIMIT 1)),'.'),                                        -- Description
        NEW.assigned_to                                        -- The user (assigned_to) performing the task creation (Employee or Project Manager)
    );
END $$

CREATE TRIGGER AfterTaskUpdate
AFTER UPDATE ON Tasks
FOR EACH ROW
BEGIN
    IF NEW.status <> OLD.status THEN
        CALL InsertProjectHistoryProc(
			(SELECT project_id FROM Milestones WHERE milestoneID = NEW.milestone_id LIMIT 1),                                       -- project_id associated with the task
			NEW.milestone_id, -- milestone_id related to the task
			NEW.task_id,                                          -- task_id of the newly created task
			'Updated',                                            -- Action type for task creation
			CONCAT('Task "', NEW.task_name, '" (Milestone: ', (SELECT milestone_name FROM Milestones WHERE milestoneID = NEW.milestone_id LIMIT 1), ' status has been updated from "', OLD.status, ' to ' , NEW.status, '".'),                                        -- Description
			NEW.assigned_to                                        -- The user (assigned_to) performing the task creation (Employee or Project Manager)
		);
    END IF;
END $$

CREATE TRIGGER AfterMilestoneInsert
AFTER INSERT ON Milestones
FOR EACH ROW
BEGIN
    CALL LogClientChange((SELECT client_id FROM Projects WHERE project_id = NEW.project_id LIMIT 1),
		'Milestones', 'Created', NEW.project_id, CONCAT('New Milestone, ', NEW.milestone_name, ', has been added to the project (', GetProjectName(NEW.project_id), ' .'));
    CALL InsertProjectHistoryProc(
        NEW.project_id, NEW.milestoneID,
        NULL,                                                    -- No task_id, since this is a milestone creation
        'Created', CONCAT('Milestone "', NEW.milestone_name, '" has been created for project "', GetProjectName(NEW.project_id)),
        (SELECT manager_id FROM Projects WHERE project_id = NEW.project_id LIMIT 1)    -- The user (manager_id) performing the milestone creation (Project Manager)
    );
END $$

CREATE TRIGGER AfterMilestoneUpdate
AFTER UPDATE ON Milestones
FOR EACH ROW
BEGIN
    -- Log milestone update
    CALL LogClientChange((SELECT client_id FROM Projects WHERE project_id = NEW.project_id LIMIT 1),
		'Milestones', 'Updated', NEW.project_id, CONCAT('Milestone, ', NEW.milestone_name, ' (Project: ', GetProjectName(NEW.project_id),' status has been updated from ', OLD.status, ' to ', NEW.status, ' .'));

    IF NEW.status <> OLD.status THEN
        CALL InsertProjectHistoryProc(
			NEW.project_id, NEW.milestoneID,
			NULL,                                                    -- No task_id, since this is a milestone creation
			'Created', CONCAT('Milestone, ', NEW.milestone_name, ' (Project: ', GetProjectName(NEW.project_id),' status has been updated from ', OLD.status, ' to ', NEW.status, ' .'),
			(SELECT manager_id FROM Projects WHERE project_id = NEW.project_id LIMIT 1)    -- The user (manager_id) performing the milestone creation (Project Manager)
		);
    END IF;
END $$

DELIMITER ;
-- --------------------------------------------------------------------------------------------------
-- Data Insertion
INSERT INTO Users (Fname, Lname, password, role, email, gender, DoB, phone_no, address, join_date)
VALUES
('Basharat', 'Hussain', 'pass123', 'Employee', 'basharat.hussain@gmail.com', 'Male', '1980-01-01', '123-456-7890', 'street 2 H11/4', '2024-01-15'),
('Hafsa', 'Imtiaz', 'hafsa7076', 'Project Manager', 'hafsa@gmail.com', 'Female', '2003-07-06', '987-654-3210', 'street 13 I8/4', '2022-08-10'),
('Aoun', 'Jee', 'jeejee', 'Client', 'aounjee@company.com', 'Male', '2004-07-06', '555-123-4567', 'street 43 Model Town', '2023-11-01'),
('Mahum', 'Hamid', 'pass101', 'Employee', ' mahumhamid11@gmail.com', 'Female', '2003-07-01', '555-234-5678', 'street 13 F7/1', '2024-02-01'),
('Areen', 'Zainab', 'pass202', 'HR Manager', ' areenzainab@gmail.com', 'Female', '2003-03-25', '555-345-6789', 'street 58 G11/2', '2020-06-15'),
('Ali', 'Abdullah', 'pass303', 'Client', 'ali777@business.com', 'Male', '2005-08-14', '555-456-7890', 'street 40 G10/4', '2023-12-05');

-- Insert data into Clients table
INSERT INTO Clients (client_id, company_name)
VALUES
(3, 'Code Smith'),
(6, 'Genesis IT Lab');

-- Insert data into ProjectManagers table
INSERT INTO ProjectManagers (PM_id, department, years_of_experience)
VALUES
(2, 'IT', 5);

-- Insert data into Employees table
INSERT INTO Employees (employee_id, department, position, salary, join_date)
VALUES
(1, 'Development', 'Software Engineer', 60000.00, '2024-01-15'),
(4, 'Designing', 'UI/UX Specialist', 50000.00, '2024-02-01');

-- Insert data into HR_Managers table
INSERT INTO HR_Managers (HR_id, department, years_of_experience)
VALUES
(5, 'HR', 10);  -- Areen (HR Manager) has 10 years of experience in the HR department not in company

-- Insert data into Skills table
INSERT INTO Skills (skill_name, skill_description)
VALUES
('Java', 'Proficiency in Java programming language'),
('Project Management', 'Expertise in managing IT projects'),
('HR Management', 'Experience in human resources management');


-- Insert data into EmployeeSkills table
INSERT INTO EmployeeSkills (employee_id, skill_id)
VALUES
(1, 1),  -- Basharat (Employee) has Java skill
(4, 2);  -- Mahum (Employee) has Project Management skill

use worksphere;
INSERT INTO ProjectProposal (client_id, title, description, duration, budget, comments, pdf_path, status, project_manager_id) VALUES
(3, 'Aaap App', 'The Smart Home Automation System is designed to provide seamless control and monitoring of household appliances through a mobile app or web interface. The system uses a combination of sensors, cloud computing, and IoT devices to allow users to control lights, heating, security systems, and other devices from anywhere.
Users can automate tasks such as adjusting thermostat settings based on the time of day or controlling lights remotely via voice commands.
The platform is built with user convenience in mind, featuring easy integration with existing home systems and compatibility with popular smart assistants like Alexa and Google Home.
Security features include real-time alerts and video surveillance accessible through the mobile app. Additionally, the system provides detailed analytics to help users track energy consumption and optimize resource usage. The overall aim is to enhance home safety, comfort, and energy efficiency through innovative and', '6 months', 25000, 'Initial proposal for review',
'"C:\Users\Hafsa\OneDrive\Documents\uni\Semester 5\Theory of Automata\i22-0959_H_A1.pdf"', 'Pending', null),
(3, 'Sada', 'Research and feasibility study for a mobile app.', '6 months', 25000, 'Initial proposal for review', '"C:\Users\Hafsa\OneDrive\Documents\uni\Semester 5\Theory of Automata\i22-0959_H_A1.pdf"', 'Pending', null),
(3, 'Sada', 'Research and feasibility study for a mobile app.', '6 months', 25000, 'Initial proposal for review', '"C:\Users\Hafsa\OneDrive\Documents\uni\Semester 5\Theory of Automata\i22-0959_H_A1.pdf"', 'Approved', 2);

-- Insert data into Projects table
INSERT INTO Projects (proposal_id, client_id, manager_id, start_date, end_date, status, final_cost)
VALUES
(1, 3, 2, '2024-01-15', '2024-06-30', 'Active',  100000.00),
(2, 3, 2, '2024-02-01', '2024-07-31', 'Active', 50000.00),
(3, 3, 2, '2024-02-01', '2024-07-31', 'Completed', 50000.00);


use worksphere;
-- Insert data into Milestones table
INSERT INTO Milestones (project_id, milestone_name, description, start_date, end_date, comments, status)
VALUES
(1, 'Initial Planning', 'Initial planning and project scoping for Project ABC', '2024-01-15', '2024-01-30', 'Kickoff meeting scheduled', 'In Progress'),  -- Milestone for Project 1 (Project ABC)
(1, 'Development', 'Development phase for Project ABC', '2024-02-01', '2024-03-01', 'Work on modules and tasks', 'In Progress'), -- Milestone for Project 1 (Project ABC)
(2, 'Research', 'Research and feasibility study for Project XYZ', '2024-02-01', '2024-02-20', 'Research phase ongoing', 'In Progress');  -- Milestone for another project (XYZ)

-- Insert data into Tasks table
INSERT INTO Tasks (milestone_id, assigned_to, task_name, description, deadline, priority, status, assigned_date)
VALUES
(1, 1, 'Develop Login Module', 'Develop the login module for the new system', '2024-02-10', 'High', 'Pending', '2024-02-01'),  -- Task for Basharat
(1, 4, 'Design login page', 'Design the login page for the website ', '2024-02-15', 'Medium', 'Pending', '2024-02-01');  -- Task for Mahum

-- Insert data into TaskExtensions table
INSERT INTO TaskExtensions (task_id, requested_by, reason, new_deadline, status)
VALUES 
(1, 1, 'Need more time for testing', '2024-02-20', 'Pending');  -- Basharat requests extension for task 1

-- Insert data into ProjectHistory table
INSERT INTO ProjectHistory (project_id, actionType, description)
VALUES 
(1, 'Viewed', 'Project ABC viewed by Hafsa'),  -- Project ABC viewed by Project Manager Hafsa
(2, 'Downloaded', 'Project ABC downloaded by Mahum');  -- Project ABC downloaded by Employee Mahum

-- Insert data into ViewHistory table
INSERT INTO ViewHistory (client_id, history_id)
VALUES 
(3, 1),  -- Aoun viewed Project ABC history
(6, 2);  -- Ali downloaded Project ABC history

-- Insert data into LeaveRequests table
INSERT INTO LeaveRequests (EmployeeID, LeaveType, StartDate, EndDate, Reason, status)
VALUES 
(1, 'Annual', '2024-03-01', '2024-03-05', 'Annual vacation', 'Pending');  -- Basharat requested annual leave

-- Insert data into Attendance table
INSERT INTO Attendance (EmployeeID, Date, CheckInTime, CheckOutTime, Status, HoursWorked)
VALUES 
(1, '2024-01-15', '09:00:00', '17:00:00', 'Present', 8),  -- Basharat (Employee) present on 2024-01-15
(4, '2024-02-01', '08:30:00', '17:30:00', 'Present', 8);  -- Mahum (Employee) present on 2024-02-01

-- Insert data into HRReports table
INSERT INTO HRReports (generated_by, employee_id, report_type, report_data)
VALUES 
(5, 1, 'Attendance', '{"attendance": "9/10", "leave": "1/2"}'),  -- HR Manager Areen generates report for Basharat (Employee)
(5, 4, 'Leave', '{"leave": "1/3", "pending": "1"}');  -- HR Manager Areen generates leave report for Mahum (Employee)


INSERT INTO Meetings (host_id, recipient_id, project_id,agenda,meeting_date,meeting_time,location,meeting_title,priority,status) 
VALUES (
    2, -- host_id (Project Manager)
    3, -- recipient_id (Client)
    1, -- project_id
    'Discussion on project deliverables and next steps', -- agenda
    '2023-11-30', -- meeting_date
    '10:00:00', -- meeting_time
    'Office: Conference Room A', -- location
    'Project Deliverables Meeting', -- meeting_title
    'High', -- priority
    'Scheduled' -- status
);