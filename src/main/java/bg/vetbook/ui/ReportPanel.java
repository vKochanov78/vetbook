package bg.vetbook.ui;

import bg.vetbook.config.AppConfig;
import bg.vetbook.dao.Database;
import bg.vetbook.dao.ReportDao;

import java.awt.*;
import javax.swing.*;

// Екран „Справка“ - обобщение за избран период.
// Прави го Стоян.
public class ReportPanel extends ScreenPanel {

    private ReportDao reportDao;
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
        this.reportDao= new ReportDao(database);
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

        btnGenerate.addActionListener(e -> {
            String fromDate = fromDateField.getText().trim();
            String toDate = toDateField.getText().trim();

            if (fromDate.isEmpty() || toDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Моля, въведете и двете дати!", "Внимание", JOptionPane.WARNING_MESSAGE);
                return;
            }

            reportArea.setText("Справка за период: " + fromDate + " до " + toDate + "\n");
            reportArea.append("==================================================\n\n");

            try {
                // Тъй като в базата датите са с часове (напр. "2026-09-22 14:00"),
                // добавяме " 00:00" и " 23:59", за да хванем целите дни.
                String startQuery = fromDate + " 00:00";
                String endQuery = toDate + " 23:59";

                // --- 1. Брой прегледи по статус ---
                java.util.Map<String, Integer> statusCount = reportDao.getVisitCountByStatus(startQuery, endQuery);
                reportArea.append("=== БРОЙ ПРЕГЛЕДИ ПО СТАТУС ===\n");
                if (statusCount.isEmpty()) {
                    reportArea.append("Няма намерени прегледи за периода.\n");
                } else {
                    for (java.util.Map.Entry<String, Integer> entry : statusCount.entrySet()) {
                        reportArea.append(String.format("%-20s : %d бр.\n", entry.getKey(), entry.getValue()));
                    }
                }
                reportArea.append("\n");

                // --- 2. Приход по лекари ---
                java.util.Map<String, Double> incomeStats = reportDao.getIncomeByDoctor(startQuery, endQuery);
                reportArea.append("=== ПРИХОД ПО ЛЕКУВАЩ ЛЕКАР ===\n");
                if (incomeStats.isEmpty()) {
                    reportArea.append("Няма отчетени приходи за периода.\n");
                } else {
                    double totalAll = 0;
                    for (java.util.Map.Entry<String, Double> entry : incomeStats.entrySet()) {
                        reportArea.append(String.format("%-20s : %.2f лв.\n", entry.getKey(), entry.getValue()));
                        totalAll += entry.getValue();
                    }
                    reportArea.append("--------------------------------------------------\n");
                    reportArea.append(String.format("%-20s : %.2f лв.\n", "ОБЩО ПРИХОДИ", totalAll));
                }

                btnExport.setEnabled(true); // Отключваме бутона за CSV файла

            } catch (java.sql.SQLException ex) {
                JOptionPane.showMessageDialog(this, "Грешка при извличане на данните: " + ex.getMessage(), "Грешка", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        btnExport.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Запазване на справката като CSV");

            // Задаваме име по подразбиране
            fileChooser.setSelectedFile(new java.io.File("spravka_" + fromDateField.getText() + ".csv"));

            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();

                // Проверяваме дали файлът завършва на .csv, ако не - добавяме го
                if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                    fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".csv");
                }

                // Използваме PrintWriter и задаваме UTF-8 кодиране
                try (java.io.PrintWriter writer = new java.io.PrintWriter(
                        new java.io.OutputStreamWriter(new java.io.FileOutputStream(fileToSave), java.nio.charset.StandardCharsets.UTF_8))) {

                    // ДОБАВКА: Записваме BOM (Byte Order Mark) символ.
                    // Той казва на Excel, че файлът е UTF-8, за да не излиза кирилицата на "маймуници".
                    writer.write('\ufeff');

                    // Взимаме целия текст от екрана и го разделяме ред по ред
                    String[] lines = reportArea.getText().split("\\n");
                    for (String line : lines) {
                        // Махаме излишните знаци "=" и "-"
                        if (line.contains("==") || line.contains("--")) {
                            continue;
                        }
                        // Заменяме разделителя " : " със запетая, за да стане на колони в CSV
                        String csvLine = line.replace(" : ", ",");
                        writer.println(csvLine);
                    }

                    JOptionPane.showMessageDialog(this, "Справката е запазена успешно!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                } catch (java.io.IOException ex) {
                    JOptionPane.showMessageDialog(this, "Грешка при запазване на файла: " + ex.getMessage(), "Грешка", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
    }

    public String getTitle() {
        return "Справка";
    }
}
