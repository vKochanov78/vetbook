package bg.vetbook.ui;

import javax.swing.JOptionPane;
import java.awt.Component;

/**
 * Съобщенията към потребителя на едно място.
 * <p>
 * Заданието (т. 6) иска грешките да излизат като съобщение, а не като срив.
 * Всички екрани минават оттук, за да изглеждат съобщенията еднакво.
 */
public final class Dialogs {

    private Dialogs() {
    }

    /** Грешка, за която потребителят може да направи нещо — липсващо поле, нарушено правило. */
    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Грешка", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Неочаквана грешка. Показва разбираемия текст на потребителя, а
     * техническите подробности отиват в конзолата за нас.
     */
    public static void error(Component parent, String message, Throwable cause) {
        cause.printStackTrace();
        JOptionPane.showMessageDialog(parent, message, "Грешка", JOptionPane.ERROR_MESSAGE);
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "VetBook", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Въпрос с „Да“ и „Не“. Връща true само при „Да“. */
    public static boolean confirm(Component parent, String question) {
        int answer = JOptionPane.showConfirmDialog(
                parent, question, "Потвърждение", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        return answer == JOptionPane.YES_OPTION;
    }
}
