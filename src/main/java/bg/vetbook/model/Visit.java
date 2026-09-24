package bg.vetbook.model;

// Един преглед.
public class Visit {

    // Колони от таблицата visits.
    private int id;
    private int animalId;
    private int doctorId;
    private String visitAt;
    private String complaint;
    private String diagnosis;
    private String status = VisitStatus.SCHEDULED;
    private double consultationFee;

    // Идват от другите таблици, само за показване на екрана.
    private String animalName;
    private String ownerName;
    private String doctorName;


//    Getters
    public int getId() {
        return id;
    }

    public int getAnimalId() {
        return animalId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public String getVisitAt() {
        return visitAt;
    }

    public String getComplaint() {
        return complaint;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getStatus() {
        return status;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public String getAnimalName() {
        return animalName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getDoctorName() {
        return doctorName;
    }


//    Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setAnimalId(int animalId) {
        this.animalId = animalId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public void setVisitAt(String visitAt) {
        this.visitAt = visitAt;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    //    Помощни методи

    public String getVisitDate() {
        if (visitAt == null || visitAt.length() < 10) {
            return "";
        } else {
            return visitAt.substring(0,10);
        }
    }

    public String getVisitTime() {
        if (visitAt == null || visitAt.length() < 16) {
            return "";
        } else {
            return visitAt.substring(11,16);
        }
    }

    public void setVisitDateAndTime(String date, String time) {
        visitAt = (date + " " + time);
    }

    public boolean isNew() {
        return id == 0;
    }

    public boolean isCompleted() {
        return VisitStatus.COMPLETED.equals(status);
    }
}
