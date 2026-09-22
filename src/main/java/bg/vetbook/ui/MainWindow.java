package bg.vetbook.ui;

import bg.vetbook.config.AppConfig;
import bg.vetbook.dao.Database;

import javax.swing.*;
import java.awt.*;

// Главният прозорец. Отляво са бутоните, отдясно се сменят екраните.
public class MainWindow extends JFrame {

    // CardLayout държи всички екрани един върху друг и показва само един.
    private CardLayout cards = new CardLayout();
    private JPanel content = new JPanel(cards);
    private ScreenPanel[] screens;

    public MainWindow(AppConfig config, Database database) {

        screens = new ScreenPanel[] {
                new VisitsPanel(config, database),
                new VisitCardPanel(config, database),
                new CatalogPanel(config, database),
                new ReportPanel(config, database)
        };

        for (int i = 0; i < screens.length; i++) {
            content.add(
                    screens[i],
                    screens[i].getTitle()
            );
        }

        setTitle(config.getWindowTitle());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createMenu(), BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
        add(
                createStatusBar(config, database),
                BorderLayout.SOUTH
        );

        setSize(
                config.getWindowWidth(),
                config.getWindowHeight()
        );

        setMinimumSize(
                new Dimension(900, 600)
        );

        setLocationRelativeTo(null);

        showScreen(
                screens[0].getTitle()
        );
    }

    // Показва екрана с даденото име.
    public void showScreen(String title) {

        cards.show(content, title);

        for (int i = 0; i < screens.length; i++) {

            if (screens[i]
                    .getTitle()
                    .equals(title)) {

                screens[i].onShown();
            }
        }
    }

    // Отваря карта на конкретен преглед
    public void openVisit(int visitId) {

        ScreenPanel screen =
                getScreen("Карта на преглед");

        if (screen instanceof VisitCardPanel) {

            VisitCardPanel visitCardPanel =
                    (VisitCardPanel) screen;

            visitCardPanel.setVisitId(
                    visitId
            );

            showScreen(
                    "Карта на преглед"
            );
        }
    }

    // Дава достъп до конкретен екран.
    public ScreenPanel getScreen(String title) {

        for (int i = 0; i < screens.length; i++) {

            if (screens[i]
                    .getTitle()
                    .equals(title)) {

                return screens[i];
            }
        }

        return null;
    }

    // Лентата с бутоните отляво.
    private JPanel createMenu() {

        JPanel menu = new JPanel();

        menu.setLayout(
                new BoxLayout(
                        menu,
                        BoxLayout.Y_AXIS
                )
        );

        menu.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        16,
                        20,
                        16
                )
        );

        menu.setPreferredSize(
                new Dimension(220, 0)
        );

        JLabel logo =
                new JLabel("VetBook");

        logo.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        20
                )
        );

        logo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        menu.add(logo);

        JLabel subtitle =
                new JLabel(
                        "ветеринарна амбулатория"
                );

        subtitle.setBorder(
                BorderFactory.createEmptyBorder(
                        2,
                        0,
                        24,
                        0
                )
        );

        subtitle.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        menu.add(subtitle);

        for (int i = 0; i < screens.length; i++) {

            String title =
                    screens[i].getTitle();

            JButton button =
                    new JButton(title);

            button.setHorizontalAlignment(
                    SwingConstants.LEFT
            );

            button.setAlignmentX(
                    Component.LEFT_ALIGNMENT
            );

            button.setMaximumSize(
                    new Dimension(
                            Integer.MAX_VALUE,
                            38
                    )
            );

            button.addActionListener(
                    e -> showScreen(title)
            );

            menu.add(button);

            menu.add(
                    Box.createVerticalStrut(8)
            );
        }

        return menu;
    }

    // Долната лента.
    private JPanel createStatusBar(
            AppConfig config,
            Database database
    ) {

        JPanel bar =
                new JPanel(
                        new BorderLayout()
                );

        bar.setBorder(
                BorderFactory.createEmptyBorder(
                        6,
                        12,
                        6,
                        12
                )
        );

        JLabel left =
                new JLabel(
                        "База: "
                                + database.getPath()
                );

        JLabel right =
                new JLabel(
                        config.getSource()
                );

        bar.add(
                left,
                BorderLayout.WEST
        );

        bar.add(
                right,
                BorderLayout.EAST
        );

        return bar;
    }
}