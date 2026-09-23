package bg.vetbook.model;

// Статус на преглед.
// В базата се пази на английски, на екрана се показва на български.
public class VisitStatus {
    
    public static final String SCHEDULED = "SCHEDULED";
    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String COMPLETED = "COMPLETED";
    public static final String CANCELLED = "CANCELLED";
    

    public static String[] all() {
        return new String[] {SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED};
    }

    public static String toBulgarian(String status) {
        if (SCHEDULED.equals(status)) {
            return "записан";
        } else if (IN_PROGRESS.equals(status)) {
            return "в процес";
        } else if (COMPLETED.equals(status)) {
            return "приключен";
        } else if (CANCELLED.equals(status)) {
            return "отменен";
        } else {
            return "Неизвестен статус";
        }
    }
}