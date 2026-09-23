package bg.vetbook.model;

// Вид на реда в един преглед: процедура, ваксина или медикамент.
// В базата се пази на английски, на екрана се показва на български.
public class ItemType {
    
    public static final String EXAM = "EXAM";
    public static final String VACCINE = "VACCINE";
    public static final String MANIPULATION = "MANIPULATION";
    public static final String MEDICINE = "MEDICINE";

    public static String[] all() {
        return new String[] {EXAM, VACCINE, MANIPULATION, MEDICINE};
    }

    public static String toBulgarian(String type) {
        if (EXAM.equals(type)) {
            return "преглед";
        } else if (VACCINE.equals(type)) {
            return "ваксина";
        } else if (MANIPULATION.equals(type)) {
            return "манипулация";
        } else if (MEDICINE.equals(type)) {
            return "медикамент";
        } else {
            return "Неизвестен тип";
        }
    }
}
