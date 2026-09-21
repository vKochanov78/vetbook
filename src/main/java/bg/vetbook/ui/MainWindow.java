package bg.vetbook.ui;

import bg.vetbook.config.AppConfig;
import bg.vetbook.dao.Database;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Главният прозорец: навигация отляво, екранът отдясно, лента със състоянието долу.
 * <p>
 * Екраните са наредени в {@link CardLayout} — всички са създадени наведнъж, но
 * се вижда само един. Смяната става с бутоните отляво.
 */
public class MainWindow extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final Map<String, ScreenPanel> screens = new LinkedHashMap<>();
    private final Map<String, JToggleButton> navigationButtons = new LinkedHashMap<>();

    public MainWindow(AppConfig config, Database database) {
        super(config.windowTitle());

        List<ScreenPanel> panels = List.of(
                new VisitsPanel(config, database),
                new VisitCardPanel(config, database),
                new CatalogPanel(config, database),
                new ReportPanel(config, database)
        );

        for (ScreenPanel panel : panels) {
            screens.put(panel.title(), panel);
            content.add(panel, panel.title());
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(buildNavigation(), BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
        add(buildStatusBar(config, database), BorderLayout.SOUTH);

        setSize(config.windowWidth(), config.windowHeight());
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        showScreen(panels.get(0).title());
    }

    /**
     * Показва екрана с даденото име и натиска съответния бутон в навигацията.
     * Ползва се и отвън — например когато от списъка с прегледи се отваря
     * картата на конкретен преглед.
     */
    public void showScreen(String title) {
        ScreenPanel panel = screens.get(title);
        if (panel == null) {
            throw new IllegalArgumentException("Няма екран с име „" + title + "“.");
        }
        cards.show(content, title);
        navigationButtons.get(title).setSelected(true);
        panel.onShown();
    }

    /** Връща екран по име, за да могат екраните да си говорят помежду си. */
    public ScreenPanel screen(String title) {
        return screens.get(title);
    }

    private JPanel buildNavigation() {
        JPanel navigation = new JPanel();
        navigation.setLayout(new BoxLayout(navigation, BoxLayout.Y_AXIS));
        navigation.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));
        navigation.setPreferredSize(new Dimension(220, 0));

        JLabel logo = new JLabel("VetBook");
        logo.setFont(logo.getFont().deriveFont(Font.BOLD, 20f));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        navigation.add(logo);

        JLabel subtitle = new JLabel("ветеринарна амбулатория");
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 11f));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(2, 0, 24, 0));
        navigation.add(subtitle);

        ButtonGroup group = new ButtonGroup();
        for (ScreenPanel panel : screens.values()) {
            JToggleButton button = new JToggleButton(panel.title());
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setFocusPainted(false);
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            button.addActionListener(event -> showScreen(panel.title()));

            group.add(button);
            navigationButtons.put(panel.title(), button);
            navigation.add(button);
            navigation.add(Box.createVerticalStrut(8));
        }

        navigation.add(Box.createVerticalGlue());
        return navigation;
    }

    private JPanel buildStatusBar(AppConfig config, Database database) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, bar.getBackground().darker()),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));

        JLabel left = new JLabel("База: " + database.file());
        JLabel right = new JLabel(config.sourceDescription());
        left.setFont(left.getFont().deriveFont(Font.PLAIN, 11f));
        right.setFont(right.getFont().deriveFont(Font.PLAIN, 11f));

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    /** Имената на екраните в реда, в който стоят в навигацията. */
    public List<String> screenTitles() {
        return new ArrayList<>(screens.keySet());
    }
}
