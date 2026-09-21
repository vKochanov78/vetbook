package bg.vetbook;

import bg.vetbook.config.AppConfig;
import bg.vetbook.dao.Database;
import bg.vetbook.ui.MainWindow;

import javax.swing.*;

// Оттук тръгва програмата.
public class App {

    public static void main(String[] args) {
        // Прозорецът да изглежда като нормална Windows програма.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ако не стане, програмата пак работи, само изглежда по-иначе.
        }

        // Swing иска интерфейсът да се прави в неговата нишка.
        SwingUtilities.invokeLater(() -> start());
    }

    private static void start() {
        try {
            AppConfig config = new AppConfig();

            Database database = new Database(config);
            database.setup();

            MainWindow window = new MainWindow(config, database);
            window.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Програмата не можа да се стартира.\n\n" + e.getMessage(),
                    "VetBook",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
