package bg.vetbook.ui;

import javax.swing.*;
import java.awt.*;

// Съобщенията към потребителя на едно място, за да изглеждат еднакво.
public class Dialogs {

    // Грешка, за която потребителят може да направи нещо.
    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Грешка", JOptionPane.ERROR_MESSAGE);
    }

    // Неочаквана грешка. Потребителят вижда текста, а подробностите
    // отиват в конзолата, за да можем да ги погледнем.
    public static void error(Component parent, String message, Exception cause) {
        cause.printStackTrace();
        JOptionPane.showMessageDialog(parent, message, "Грешка", JOptionPane.ERROR_MESSAGE);
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "VetBook", JOptionPane.INFORMATION_MESSAGE);
    }

    // Въпрос с „Да“ и „Не“. Връща true само при „Да“.
    public static boolean confirm(Component parent, String question) {
        int answer = JOptionPane.showConfirmDialog(parent, question, "Потвърждение",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return answer == JOptionPane.YES_OPTION;
    }
}
