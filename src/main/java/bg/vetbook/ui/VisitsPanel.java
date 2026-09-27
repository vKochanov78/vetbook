package bg.vetbook.ui;

import bg.vetbook.config.AppConfig;
import bg.vetbook.dao.Database;
import bg.vetbook.dao.VisitDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class VisitsPanel extends ScreenPanel {

    private final VisitDao visitDao;

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField searchField;
    private JTextField dateField;
    private JComboBox<String> statusComboBox;

    public VisitsPanel(AppConfig config, Database database) {
        this.visitDao = new VisitDao(database);

        setLayout(new BorderLayout(10, 10));

        createInterface();
        loadVisits();
    }

    // ==================================================
    // ИНТЕРФЕЙС
    // ==================================================

    private void createInterface() {

        JLabel title = new JLabel("Прегледи");
        title.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        // Търсене
        searchField = new JTextField(15);
        searchField.setToolTipText(
                "Търсене по животно или собственик"
        );

        // Дата
        dateField = new JTextField(10);
        dateField.setToolTipText(
                "Дата във формат ГГГГ-ММ-ДД"
        );

        // Статус
        String[] statuses = {
                "Всички",
                "Записан",
                "В процес",
                "Приключен",
                "Отказан"
        };

        statusComboBox =
                new JComboBox<>(statuses);

        // Бутони
        JButton searchButton =
                new JButton("Търси");

        JButton clearButton =
                new JButton("Изчисти");

        JButton newVisitButton =
                new JButton("Нов преглед");

        // ==================================================
        // ФИЛТРИ
        // ==================================================

        JPanel filterPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                5
                        )
                );

        filterPanel.add(
                new JLabel("Животно / собственик:")
        );

        filterPanel.add(searchField);

        filterPanel.add(
                new JLabel("Дата:")
        );

        filterPanel.add(dateField);

        filterPanel.add(
                new JLabel("Статус:")
        );

        filterPanel.add(statusComboBox);

        filterPanel.add(searchButton);
        filterPanel.add(clearButton);
        filterPanel.add(newVisitButton);

        // ==================================================
        // ГОРЕН ПАНЕЛ
        // ==================================================

        JPanel topPanel =
                new JPanel(
                        new BorderLayout()
                );

        topPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        15,
                        15,
                        5,
                        15
                )
        );

        topPanel.add(
                title,
                BorderLayout.NORTH
        );

        topPanel.add(
                filterPanel,
                BorderLayout.SOUTH
        );

        add(
                topPanel,
                BorderLayout.NORTH
        );

        // ==================================================
        // ТАБЛИЦА
        // ==================================================

        String[] columns = {
                "ID",
                "Дата и час",
                "Животно",
                "Собственик",
                "Лекар",
                "Статус",
                "Оплакване",
                "Диагноза",
                "Такса"
        };

        tableModel =
                new DefaultTableModel(
                        columns,
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        table = new JTable(tableModel);

        table.setRowHeight(25);

        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane =
                new JScrollPane(table);

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        15,
                        15,
                        15
                )
        );

        add(
                scrollPane,
                BorderLayout.CENTER
        );

        // ==================================================
        // ДЕЙСТВИЯ
        // ==================================================

        searchButton.addActionListener(
                e -> searchVisits()
        );

        clearButton.addActionListener(
                e -> clearFilters()
        );

        newVisitButton.addActionListener(
                e -> openNewVisit()
        );

        // Enter в търсенето
        searchField.addActionListener(
                e -> searchVisits()
        );

        // Enter в датата
        dateField.addActionListener(
                e -> searchVisits()
        );

        // Смяна на статуса
        statusComboBox.addActionListener(
                e -> searchVisits()
        );

        // Двоен клик върху ред
        table.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e
                    ) {

                        if (e.getClickCount() == 2) {
                            openSelectedVisit();
                        }
                    }
                }
        );
    }

    // ==================================================
    // ЗАРЕЖДАНЕ НА ВСИЧКИ ПРЕГЛЕДИ
    // ==================================================

    private void loadVisits() {

        tableModel.setRowCount(0);

        try (ResultSet resultSet =
                     visitDao.getAllVisits()) {

            fillTable(
                    resultSet,
                    "",
                    ""
            );

        } catch (SQLException e) {

            showDatabaseError(e);
        }
    }

    // ==================================================
    // ТЪРСЕНЕ И ФИЛТРИ
    // ==================================================

    private void searchVisits() {

        String searchText =
                searchField
                        .getText()
                        .trim()
                        .toLowerCase(
                                Locale.forLanguageTag("bg")
                        );

        String dateText =
                dateField
                        .getText()
                        .trim();

        // Проверка на формата на датата
        if (!isDateValid(dateText)) {
            return;
        }

        String selectedStatus =
                (String)
                        statusComboBox
                                .getSelectedItem();

        String databaseStatus =
                convertStatusToDatabase(
                        selectedStatus
                );

        tableModel.setRowCount(0);

        try (ResultSet resultSet =
                     visitDao.searchVisits(
                             databaseStatus
                     )) {

            fillTable(
                    resultSet,
                    searchText,
                    dateText
            );

        } catch (SQLException e) {

            showDatabaseError(e);
        }
    }

    // ==================================================
    // ПОПЪЛВАНЕ НА ТАБЛИЦАТА
    // ==================================================

    private void fillTable(
            ResultSet resultSet,
            String searchText,
            String dateText
    ) throws SQLException {

        while (resultSet.next()) {

            String animalName =
                    resultSet.getString(
                            "animal_name"
                    );

            String ownerName =
                    resultSet.getString(
                            "owner_name"
                    );

            String visitDate =
                    resultSet.getString(
                            "visit_at"
                    );

            // Търсене по животно / собственик
            if (!matchesSearch(
                    animalName,
                    ownerName,
                    searchText
            )) {
                continue;
            }

            // Филтър по дата
            if (!matchesDate(
                    visitDate,
                    dateText
            )) {
                continue;
            }

            Object[] row = {

                    resultSet.getInt("id"),

                    visitDate,

                    animalName,

                    ownerName,

                    resultSet.getString(
                            "doctor_name"
                    ),

                    translateStatus(
                            resultSet.getString(
                                    "status"
                            )
                    ),

                    resultSet.getString(
                            "complaint"
                    ),

                    resultSet.getString(
                            "diagnosis"
                    ),

                    String.format(
                            "%.2f €",
                            resultSet.getDouble(
                                    "consultation_fee"
                            )
                    )
            };

            tableModel.addRow(row);
        }
    }

    // ==================================================
    // ТЪРСЕНЕ ПО ИМЕ
    // ==================================================

    private boolean matchesSearch(
            String animalName,
            String ownerName,
            String searchText
    ) {

        if (searchText == null
                || searchText.isBlank()) {

            return true;
        }

        Locale bulgarian =
                Locale.forLanguageTag("bg");

        String animalLower =
                animalName == null
                        ? ""
                        : animalName
                        .toLowerCase(bulgarian);

        String ownerLower =
                ownerName == null
                        ? ""
                        : ownerName
                        .toLowerCase(bulgarian);

        return animalLower.contains(searchText)
                || ownerLower.contains(searchText);
    }

    // ==================================================
    // ФИЛТЪР ПО ДАТА
    // ==================================================

    private boolean matchesDate(
            String visitDate,
            String dateText
    ) {

        if (dateText == null
                || dateText.isBlank()) {

            return true;
        }

        return visitDate != null
                && visitDate.startsWith(dateText);
    }

    // ==================================================
    // ПРОВЕРКА НА ДАТАТА
    // ==================================================

    private boolean isDateValid(
            String dateText
    ) {

        if (dateText == null
                || dateText.isBlank()) {

            return true;
        }

        if (!dateText.matches(
                "\\d{4}-\\d{2}-\\d{2}"
        )) {

            JOptionPane.showMessageDialog(
                    this,
                    "Въведи датата във формат:\n"
                            + "ГГГГ-ММ-ДД\n\n"
                            + "Пример: 2026-09-22",
                    "Невалидна дата",
                    JOptionPane.WARNING_MESSAGE
            );

            return false;
        }

        return true;
    }

    // ==================================================
    // ОТВАРЯНЕ НА ИЗБРАН ПРЕГЛЕД
    // ==================================================

    private void openSelectedVisit() {

        int selectedRow =
                table.getSelectedRow();

        if (selectedRow == -1) {
            return;
        }

        int visitId =
                (int)
                        tableModel.getValueAt(
                                selectedRow,
                                0
                        );

        openVisit(visitId);
    }

    // ==================================================
    // НОВ ПРЕГЛЕД
    // ==================================================

    private void openNewVisit() {

        // -1 означава нов преглед
        openVisit(-1);
    }

    // ==================================================
    // ОТВАРЯ КАРТА НА ПРЕГЛЕД
    // ==================================================

    private void openVisit(
            int visitId
    ) {

        Window window =
                SwingUtilities
                        .getWindowAncestor(this);

        if (window instanceof MainWindow) {

            ((MainWindow) window)
                    .openVisit(visitId);
        }
    }

    // ==================================================
    // ИЗЧИСТВАНЕ НА ФИЛТРИТЕ
    // ==================================================

    private void clearFilters() {

        searchField.setText("");

        dateField.setText("");

        statusComboBox.setSelectedIndex(0);

        loadVisits();
    }

    // ==================================================
    // БЪЛГАРСКИ СТАТУС -> БАЗА
    // ==================================================

    private String convertStatusToDatabase(
            String status
    ) {

        if (status == null) {
            return "ALL";
        }

        return switch (status) {

            case "Записан" ->
                    "SCHEDULED";

            case "В процес" ->
                    "IN_PROGRESS";

            case "Приключен" ->
                    "COMPLETED";

            case "Отказан" ->
                    "CANCELLED";

            default ->
                    "ALL";
        };
    }

    // ==================================================
    // СТАТУС ОТ БАЗАТА -> БЪЛГАРСКИ
    // ==================================================

    private String translateStatus(
            String status
    ) {

        if (status == null) {
            return "";
        }

        return switch (status) {

            case "SCHEDULED" ->
                    "Записан";

            case "IN_PROGRESS" ->
                    "В процес";

            case "COMPLETED" ->
                    "Приключен";

            case "CANCELLED" ->
                    "Отказан";

            default ->
                    status;
        };
    }

    // ==================================================
    // ГРЕШКА В БАЗАТА
    // ==================================================

    private void showDatabaseError(
            SQLException e
    ) {

        JOptionPane.showMessageDialog(
                this,
                "Грешка при зареждане на прегледите:\n"
                        + e.getMessage(),
                "VetBook",
                JOptionPane.ERROR_MESSAGE
        );

        e.printStackTrace();
    }

    @Override
    public String getTitle() {

        return "Прегледи";
    }
}