package com.example.yogshala.model;

import java.time.LocalDate;

public class Client { //d
    private String id;
    private String firstName;
    private String lastName;
    private String date;

    private String email;
    private String address;

    private String birthDate;
    private String phone;

    private String amount;
    private String status;
    private String program;




    // No-argument constructor
    public Client() { // Renamed from Enquiry() to Client()
    }

    public Client(String id, String firstName, String lastName, String date,  String email, String address,  String birthDate, String phone, String amount, String status, String program) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;

        this.email = email;
        this.address = address;

        this.birthDate = birthDate;
        this.phone = phone;
        this.amount = amount;
        this.status = status;
        this.program = program;



    }

    // Getters
    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDate() {
        return date;
    }




    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }



    public String getBirthDate() {
        return birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public String getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getProgram() {
        return program;
    }






    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDate(String date) {
        this.date = date;
    }



    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }



    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setProgram(String program) {
        this.program = program;
    }






}

