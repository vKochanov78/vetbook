package bg.vetbook;

import bg.vetbook.config.AppConfig;
import bg.vetbook.dao.Database;
import bg.vetbook.ui.MainWindow;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Началната точка на приложението.
 * <p>
 * Прави три неща и нищо повече: чете настройките, подготвя базата и отваря
 * главния прозорец. Ако нещо от трите се счупи, показва съобщение вместо
 * стената от червен текст в конзолата.
 */
public final class App {

    private App() {
    }

    public static void main(String[] args) {
        applySystemLookAndFeel();
        SwingUtilities.invokeLater(App::start);
    }

    private static void start() {
        try {
            AppConfig config = AppConfig.load();

            Database database = new Database(config);
            database.initialise();

            new MainWindow(config, database).setVisible(true);
        } catch (RuntimeException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Приложението не можа да стартира.\n\n" + e.getMessage(),
                    "VetBook",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    /** Прозорецът да изглежда като Windows приложение, а не като Java от 2005 г. */
    private static void applySystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Не е фатално — приложението работи и с вида по подразбиране.
        }
    }
}
