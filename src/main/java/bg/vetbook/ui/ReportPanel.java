package bg.vetbook.ui;

import bg.vetbook.config.AppConfig;
import bg.vetbook.dao.Database;

import java.awt.*;
import javax.swing.*;

// Екран „Справка“ - обобщение за избран период.
// Прави го Стоян.
public class ReportPanel extends ScreenPanel {

    private AppConfig config;
    private Database database;
    private JTextField fromDateField;
    private JTextField toDateField;
    private JButton btnGenerate;
    private JButton btnExport;
    private JTextArea reportArea;


    public ReportPanel(AppConfig config, Database database) {
        this.config = config;
        this.database = database;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- ГОРЕН ПАНЕЛ (Филтри и Бутони) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));

        topPanel.add(new JLabel("От дата (ГГГГ-ММ-ДД):"));
        fromDateField = new JTextField(10);
        topPanel.add(fromDateField);

        topPanel.add(new JLabel("До дата (ГГГГ-ММ-ДД):"));
        toDateField = new JTextField(10);
        topPanel.add(toDateField);

        btnGenerate = new JButton("Генерирай");
        btnExport = new JButton("Експорт в CSV");

        // Бутонът за експорт е неактивен, докато не генерираме нещо
        btnExport.setEnabled(false);

        topPanel.add(btnGenerate);
        topPanel.add(btnExport);

        add(topPanel, BorderLayout.NORTH);

        // --- ЦЕНТРАЛЕН ПАНЕЛ (Екран за резултата) ---
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        // Слагаме Monospaced шрифт, за да могат таблиците и числата да се подравняват идеално
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Резултат от справката"));

        add(scrollPane, BorderLayout.CENTER);

        // --- ДЕЙСТВИЯ НА БУТОНИТЕ (Засега само тестови) ---
        btnGenerate.addActionListener(e -> {
            reportArea.setText("Зареждане на справката...\n\n");
            reportArea.append("Тук ще излезе бройката по статуси.\n");
            reportArea.append("Тук ще излезе приходът по лекари.\n");

            btnExport.setEnabled(true); // Отключваме експорта
        });

        btnExport.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Функцията за CSV предстои да се напише!");
        });
    }

    public String getTitle() {
        return "Справка";
    }
}
