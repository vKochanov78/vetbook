package bg.vetbook.ui;

import javax.swing.*;
import java.awt.*;

// Временно съдържание за екран, който още не е направен.
public class Placeholder {

    public static JPanel create(String screenName, String person, String[] tasks) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        JLabel heading = new JLabel(screenName);
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(heading);

        JLabel who = new JLabel("Отговорник: " + person + "  ·  екранът още не е направен");
        who.setBorder(BorderFactory.createEmptyBorder(6, 0, 20, 0));
        who.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(who);

        JLabel todo = new JLabel("Какво трябва да има тук:");
        todo.setFont(new Font("SansSerif", Font.BOLD, 13));
        todo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(todo);

        for (int i = 0; i < tasks.length; i++) {
            JLabel row = new JLabel("•  " + tasks[i]);
            row.setBorder(BorderFactory.createEmptyBorder(6, 8, 0, 0));
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(row);
        }

        return panel;
    }
}
