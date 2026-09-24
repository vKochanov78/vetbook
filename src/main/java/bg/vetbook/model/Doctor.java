package bg.vetbook.model;

// Лекуващ лекар.
public class Doctor {

    private int id;
    private String fullName;
    private String speciality;

    public Doctor(int id, String fullName, String speciality) {
        this.id = id;
        this.fullName = fullName;
        this.speciality = speciality;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    // Показва се, когато лекарят се сложи в падащо меню.
    @Override
    public String toString() {
        return fullName;
    }
}
