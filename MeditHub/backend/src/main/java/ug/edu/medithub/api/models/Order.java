package ug.edu.medithub.api.models;

import java.util.ArrayList;
import java.util.UUID;

public class Order {
    private String id;
    private String patientId;
    private String doctorName;
    private String date;
    private String time;
    private String description;

    private ArrayList<Tooth> toothArray;

    public Order() {
        this.id = UUID.randomUUID().toString();
    }

    public Order(String patientId, String doctorName, String date, String time, String description, ArrayList<Tooth> toothArray) {
        this();
        this.patientId = patientId;
        this.doctorName = doctorName;
        this.date = date;
        this.time = time;
        this.description = description;
        this.toothArray = toothArray;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public ArrayList<Tooth> getToothArray() {
        return toothArray;
    }

    public void setToothArray(ArrayList<Tooth> toothArray) {
        this.toothArray = toothArray;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", patientId='" + patientId + '\'' +
                ", doctorId='" + doctorName + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", description='" + description + '\'' +
                ", toothArray=" + toothArray +
                '}';
    }
}
