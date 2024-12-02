package com.example.projecthr;

import utility.Factory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

//store information regarding the User parent class
public abstract class User {
    protected int userId;
    protected String email;
    protected String password;
    protected String firstName;
    protected String lastName;
    protected String gender;
    protected String dob;
    protected String phoneNo;
    protected String address;
    protected String joinDate;

    // Constructor
    public User(int userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public User(int userId){
        this.userId = userId;
    }

    public User(int userId, String email, String password, String firstName, String lastName, String gender, String dob, String phoneNo, String address) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dob = dob;
        this.phoneNo = phoneNo;
        this.address = address;
    }

    public abstract void loadDashboard() throws IOException;

    public boolean loadUserProfile() {
        return Factory.getUserServices().loadProfile(this);
    }

    public boolean saveUserProfile() {
        return Factory.getUserServices().updateProfile(this);
    }


    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setGender(String gender) { this.gender = gender; }
    public void setDob(String dob) { this.dob = dob; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }
    public void setAddress(String address) { this.address = address; }

    public int getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getGender() { return gender; }
    public String getPhoneNo() { return phoneNo; }
    public String getAddress() { return address; }
    public String getJoinDate() { return joinDate; }

    public LocalDate getDob() {
        String dobString = this.dob;
        if (dobString != null && !dobString.isEmpty()) {
            return LocalDate.parse(dobString, DateTimeFormatter.ISO_LOCAL_DATE);
        } else {
            return null;
        }
    }

    public boolean setUpComplete() {
        return (gender != null && !gender.isEmpty()) &&
                (dob != null && !dob.isEmpty()) &&
                (address != null && !address.isEmpty()) &&
                (phoneNo != null && !phoneNo.isEmpty());
    }
}