package bg.vetbook.ui;

import bg.vetbook.config.AppConfig;
import bg.vetbook.dao.AnimalDao;
import bg.vetbook.dao.Database;
import bg.vetbook.dao.DoctorDao;
import bg.vetbook.dao.VisitCardDao;
import bg.vetbook.dao.VisitItemDao;
import bg.vetbook.model.Animal;
import bg.vetbook.model.Doctor;
import bg.vetbook.model.ItemType;
import bg.vetbook.model.Visit;
import bg.vetbook.model.VisitItem;
import bg.vetbook.model.VisitStatus;
import bg.vetbook.service.VisitService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

// Екран „Карта на преглед“ - въвеждане и редактиране на един преглед.
// Горе са данните на прегледа, долу процедурите и дължимата сума.
// Прави го Валентин.
public class VisitCardPanel extends ScreenPanel {

    private AppConfig config;

    private VisitCardDao visitCardDao;
    private VisitItemDao visitItemDao;
    private AnimalDao animalDao;
    private DoctorDao doctorDao;

    // Правилата на амбулаторията.
    private VisitService visitService;

    // Вдига се, докато екранът се пълни, за да не се задействат
    // слушателите на менютата от самото зареждане.
    private boolean loading = false;

    // Прегледът, който се редактира в момента.
    private Visit visit = new Visit();

    // Процедурите му. Пълни се от базата при всяко зареждане.
    private List<VisitItem> items;

    // Номерът, който идва от списъка с прегледи.
    // -1 или 0 означава нов преглед.
    private int visitId = -1;

    private JComboBox<Animal> animalCombo;
    private JComboBox<Doctor> doctorCombo;
    private JTextField dateField;
    private JTextField timeField;
    private JComboBox<String> statusCombo;
    private JTextField complaintField;
    private JTextField diagnosisField;

    private JTable itemsTable;
    private DefaultTableModel itemsModel;
    private JButton addItemButton;
    private JButton removeItemButton;

    private JLabel titleLabel;
    private JLabel totalLabel;
    private JButton saveButton;

    public VisitCardPanel(AppConfig config, Database database) {
        this.config = config;

        this.visitCardDao = new VisitCardDao(database);
        this.visitItemDao = new VisitItemDao(database);
        this.animalDao = new AnimalDao(database);
        this.doctorDao = new DoctorDao(database);
        this.visitService = new VisitService(config, database);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(createTop(), BorderLayout.NORTH);
        add(createItemsPart(), BorderLayout.CENTER);
        add(createBottom(), BorderLayout.SOUTH);
    }

    public String getTitle() {
        return "Карта на преглед";
    }

    // Списъкът с прегледи подава номера, преди да покаже екрана.
    public void setVisitId(int visitId) {
        this.visitId = visitId;
    }

    public int getVisitId() {
        return visitId;
    }

    // Вика се всеки път, когато екранът стане видим.
    public void onShown() {
        loadVisit();
    }

    // ---------- горната част: данните на прегледа ----------

    private JPanel createTop() {
        titleLabel = new JLabel("Нов преглед");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        animalCombo = new JComboBox<>();
        doctorCombo = new JComboBox<>();

        // Смени ли се животното, таксата може да се промени -
        // животно със златен картон не плаща консултация.
        animalCombo.addActionListener(e -> refreshFee());
        dateField = new JTextField(10);
        timeField = new JTextField(6);
        complaintField = new JTextField(30);
        diagnosisField = new JTextField(30);

        // Менюто пази английските кодове, а показва български текст.
        statusCombo = new JComboBox<>(VisitStatus.all());
        statusCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                setText(VisitStatus.toBulgarian((String) value));
                return this;
            }
        });

        // GridLayout прави мрежа от еднакви клетки: 3 реда по 4 колони.
        JPanel grid = new JPanel(new GridLayout(3, 4, 10, 8));
        grid.add(new JLabel("Животно:"));
        grid.add(animalCombo);
        grid.add(new JLabel("Лекуващ лекар:"));
        grid.add(doctorCombo);
        grid.add(new JLabel("Дата (ГГГГ-ММ-ДД):"));
        grid.add(dateField);
        grid.add(new JLabel("Час (ЧЧ:ММ):"));
        grid.add(timeField);
        grid.add(new JLabel("Статус:"));
        grid.add(statusCombo);
        grid.add(new JLabel(""));
        grid.add(new JLabel(""));

        JPanel texts = new JPanel(new GridLayout(2, 2, 10, 8));
        texts.add(new JLabel("Оплакване:"));
        texts.add(complaintField);
        texts.add(new JLabel("Диагноза:"));
        texts.add(diagnosisField);
        texts.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JPanel fields = new JPanel(new BorderLayout());
        fields.add(grid, BorderLayout.NORTH);
        fields.add(texts, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout());
        top.add(titleLabel, BorderLayout.NORTH);
        top.add(fields, BorderLayout.CENTER);
        return top;
    }

    // ---------- средната част: процедурите ----------

    private JPanel createItemsPart() {
        String[] columns = { "Вид", "Описание", "Количество", "Ед. цена", "Сума" };
        itemsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Редовете се променят само през бутоните, не с двоен клик.
                return false;
            }
        };

        itemsTable = new JTable(itemsModel);
        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        addItemButton = new JButton("Добави процедура");
        removeItemButton = new JButton("Премахни избраната");

        addItemButton.addActionListener(e -> addItem());
        removeItemButton.addActionListener(e -> removeItem());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        buttons.add(addItemButton);
        buttons.add(removeItemButton);

        JScrollPane scroll = new JScrollPane(itemsTable);
        scroll.setBorder(BorderFactory.createTitledBorder("Процедури и медикаменти"));

        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.add(buttons, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ---------- долната част: сумата и бутоните ----------

    private JPanel createBottom() {
        totalLabel = new JLabel(" ");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 15));

        saveButton = new JButton("Запази");
        saveButton.addActionListener(e -> saveVisit());

        JButton backButton = new JButton("Назад към списъка");
        backButton.addActionListener(e -> goToList());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.add(backButton);
        buttons.add(saveButton);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        bottom.add(totalLabel, BorderLayout.WEST);
        bottom.add(buttons, BorderLayout.EAST);
        return bottom;
    }

    // ---------- зареждане ----------

    private void loadVisit() {
        loading = true;
        try {
            fillCombos();

            if (visitId <= 0) {
                // Нов преглед: празен обект. Таксата се смята от правилата
                // според избраното животно.
                visit = new Visit();
                items = new java.util.ArrayList<>();
                titleLabel.setText("Нов преглед");
            } else {
                visit = visitCardDao.findById(visitId);
                if (visit == null) {
                    Dialogs.error(this, "Преглед с номер " + visitId + " не беше намерен.");
                    visit = new Visit();
                    items = new java.util.ArrayList<>();
                } else {
                    items = visitItemDao.findByVisit(visit.getId());
                    titleLabel.setText("Преглед №" + visit.getId()
                            + "  ·  " + VisitStatus.toBulgarian(visit.getStatus()));
                }
            }

            showVisitInFields();
            showItemsInTable();
            applyReadOnly();

            loading = false;

            // За нов преглед таксата зависи от избраното животно.
            if (visit.isNew()) {
                refreshFee();
            } else {
                updateTotal();
            }

        } catch (SQLException e) {
            Dialogs.error(this, "Прегледът не можа да се зареди.", e);
        } finally {
            loading = false;
        }
    }

    // Пита правилата каква е таксата за избраното животно и я показва.
    // Прави се само за нов преглед - при записан се пази начислената тогава.
    private void refreshFee() {
        if (loading || visit == null || !visit.isNew()) {
            return;
        }

        Animal animal = (Animal) animalCombo.getSelectedItem();
        if (animal == null) {
            return;
        }

        try {
            visit.setConsultationFee(visitService.feeFor(animal.getId()));
            updateTotal();
        } catch (SQLException e) {
            Dialogs.error(this, "Таксата не можа да се изчисли.", e);
        }
    }

    // Пълни двете падащи менюта с животните и лекарите от базата.
    private void fillCombos() throws SQLException {
        animalCombo.removeAllItems();
        for (Animal animal : animalDao.getAllAnimals()) {
            animalCombo.addItem(animal);
        }

        doctorCombo.removeAllItems();
        for (Doctor doctor : doctorDao.getAllDoctors()) {
            doctorCombo.addItem(doctor);
        }
    }

    // Пренася данните от обекта в полетата на екрана.
    private void showVisitInFields() {
        selectAnimal(visit.getAnimalId());
        selectDoctor(visit.getDoctorId());

        dateField.setText(visit.getVisitDate());
        timeField.setText(visit.getVisitTime());
        statusCombo.setSelectedItem(visit.getStatus());
        complaintField.setText(visit.getComplaint() == null ? "" : visit.getComplaint());
        diagnosisField.setText(visit.getDiagnosis() == null ? "" : visit.getDiagnosis());
    }

    private void selectAnimal(int animalId) {
        for (int i = 0; i < animalCombo.getItemCount(); i++) {
            if (animalCombo.getItemAt(i).getId() == animalId) {
                animalCombo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selectDoctor(int doctorId) {
        for (int i = 0; i < doctorCombo.getItemCount(); i++) {
            if (doctorCombo.getItemAt(i).getId() == doctorId) {
                doctorCombo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void showItemsInTable() {
        itemsModel.setRowCount(0);
        for (VisitItem item : items) {
            itemsModel.addRow(new Object[] {
                    ItemType.toBulgarian(item.getItemType()),
                    item.getDescription(),
                    String.format("%.1f", item.getQuantity()),
                    String.format("%.2f", item.getUnitPrice()),
                    String.format("%.2f", item.getTotal())
            });
        }
    }

    // Преизчислява сумата и я показва долу вляво.
    private void updateTotal() {
        double procedures = 0;
        for (VisitItem item : items) {
            procedures = procedures + item.getTotal();
        }
        double total = procedures + visit.getConsultationFee();

        totalLabel.setText("Процедури: " + String.format("%.2f", procedures)
                + " €    Такса: " + String.format("%.2f", visit.getConsultationFee())
                + " €    Общо: " + String.format("%.2f", total) + " €");
    }

    // Приключен преглед само се разглежда.
    private void applyReadOnly() {
        boolean editable = !visit.isCompleted();

        animalCombo.setEnabled(editable);
        doctorCombo.setEnabled(editable);
        dateField.setEditable(editable);
        timeField.setEditable(editable);
        statusCombo.setEnabled(editable);
        complaintField.setEditable(editable);
        diagnosisField.setEditable(editable);
        addItemButton.setEnabled(editable);
        removeItemButton.setEnabled(editable);
        saveButton.setEnabled(editable);
    }

    // ---------- запис ----------

    // Пренася попълненото от екрана обратно в обекта.
    private void readFieldsIntoVisit() {
        Animal animal = (Animal) animalCombo.getSelectedItem();
        Doctor doctor = (Doctor) doctorCombo.getSelectedItem();

        if (animal != null) {
            visit.setAnimalId(animal.getId());
        }
        if (doctor != null) {
            visit.setDoctorId(doctor.getId());
        }

        visit.setVisitDateAndTime(dateField.getText().trim(), timeField.getText().trim());
        visit.setStatus((String) statusCombo.getSelectedItem());
        visit.setComplaint(complaintField.getText().trim());
        visit.setDiagnosis(diagnosisField.getText().trim());
    }

    private void saveVisit() {
        readFieldsIntoVisit();

        try {
            // Първо правилата. Ако има нарушено, нищо не се записва.
            List<String> problems = visitService.findProblems(visit);
            if (!problems.isEmpty()) {
                showProblems(problems);
                return;
            }

            if (visit.isNew()) {
                int newId = visitCardDao.insert(visit);
                visitId = newId;
                Dialogs.info(this, "Прегледът е записан под номер " + newId + ".");
            } else {
                visitCardDao.update(visit);
                Dialogs.info(this, "Промените са записани.");
            }
            loadVisit();

        } catch (SQLException e) {
            Dialogs.error(this, "Прегледът не можа да се запише.", e);
        }
    }

    // Показва нарушените правила в едно съобщение, по едно на ред.
    private void showProblems(List<String> problems) {
        StringBuilder text = new StringBuilder("Прегледът не може да се запише:\n\n");
        for (String problem : problems) {
            text.append("•  ").append(problem).append('\n');
        }
        Dialogs.error(this, text.toString());
    }

    // ---------- процедури ----------

    private void addItem() {
        if (visit.isNew()) {
            Dialogs.error(this, "Първо запишете прегледа, после добавяйте процедури.");
            return;
        }

        JComboBox<String> typeCombo = new JComboBox<>(ItemType.all());
        typeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                setText(ItemType.toBulgarian((String) value));
                return this;
            }
        });

        JTextField descriptionField = new JTextField(20);
        JTextField quantityField = new JTextField("1");
        JTextField priceField = new JTextField("0.00");

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 6));
        form.add(new JLabel("Вид:"));
        form.add(typeCombo);
        form.add(new JLabel("Описание:"));
        form.add(descriptionField);
        form.add(new JLabel("Количество:"));
        form.add(quantityField);
        form.add(new JLabel("Единична цена:"));
        form.add(priceField);

        int answer = JOptionPane.showConfirmDialog(this, form, "Нова процедура",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (answer != JOptionPane.OK_OPTION) {
            return;
        }

        String description = descriptionField.getText().trim();
        if (description.isEmpty()) {
            Dialogs.error(this, "Описанието не може да е празно.");
            return;
        }

        double quantity;
        double price;
        try {
            quantity = Double.parseDouble(quantityField.getText().trim().replace(',', '.'));
            price = Double.parseDouble(priceField.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            Dialogs.error(this, "Количеството и цената трябва да са числа.");
            return;
        }

        if (quantity <= 0) {
            Dialogs.error(this, "Количеството трябва да е по-голямо от нула.");
            return;
        }
        if (price < 0) {
            Dialogs.error(this, "Цената не може да е отрицателна.");
            return;
        }

        VisitItem item = new VisitItem();
        item.setVisitId(visit.getId());
        item.setItemType((String) typeCombo.getSelectedItem());
        item.setDescription(description);
        item.setQuantity(quantity);
        item.setUnitPrice(price);

        try {
            visitItemDao.insert(item);
            reloadItems();
        } catch (SQLException e) {
            Dialogs.error(this, "Процедурата не можа да се запише.", e);
        }
    }

    private void removeItem() {
        int row = itemsTable.getSelectedRow();
        if (row == -1) {
            Dialogs.error(this, "Изберете ред от таблицата.");
            return;
        }

        VisitItem item = items.get(row);
        if (!Dialogs.confirm(this, "Да се премахне ли „" + item.getDescription() + "“?")) {
            return;
        }

        try {
            visitItemDao.delete(item.getId());
            reloadItems();
        } catch (SQLException e) {
            Dialogs.error(this, "Процедурата не можа да се премахне.", e);
        }
    }

    // Презарежда процедурите от базата и преизчислява сумата.
    private void reloadItems() throws SQLException {
        items = visitItemDao.findByVisit(visit.getId());
        showItemsInTable();
        updateTotal();
    }

    // ---------- навигация ----------

    private void goToList() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof MainWindow) {
            ((MainWindow) window).showScreen("Прегледи");
        }
    }
}
