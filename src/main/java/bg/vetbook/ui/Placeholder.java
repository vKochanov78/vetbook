package bg.vetbook.ui;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Font;

/**
 * Временно съдържание за екран, който още не е направен.
 * <p>
 * Показва кой отговаря за екрана и какво трябва да има в него според
 * заданието. Всеки от отбора трие това от своя екран, щом го напише.
 */
final class Placeholder {

    private Placeholder() {
    }

    static JPanel build(String screenName, String responsible, String... tasks) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        JLabel heading = new JLabel(screenName);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 22f));
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(heading);

        JLabel owner = new JLabel("Отговорник: " + responsible + "  ·  екранът още не е направен");
        owner.setFont(owner.getFont().deriveFont(Font.PLAIN, 13f));
        owner.setBorder(BorderFactory.createEmptyBorder(6, 0, 20, 0));
        owner.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(owner);

        JLabel todoHeading = new JLabel("Какво трябва да има тук според заданието:");
        todoHeading.setFont(todoHeading.getFont().deriveFont(Font.BOLD, 13f));
        todoHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(todoHeading);

        for (String task : tasks) {
            JLabel item = new JLabel("•  " + task);
            item.setBorder(BorderFactory.createEmptyBorder(6, 8, 0, 0));
            item.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(item);
        }

        return panel;
    }
}
