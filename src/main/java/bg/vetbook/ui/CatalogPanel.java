package bg.vetbook.ui;
import bg.vetbook.config.AppConfig;
import bg.vetbook.dao.AnimalDao;
import bg.vetbook.dao.Database;
import bg.vetbook.dao.OwnerDao;
import bg.vetbook.model.Animal;
import bg.vetbook.model.Owner;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * CatalogPanel представлява екрана 'Картотека' за приложението VetBook.
 */
public class CatalogPanel extends ScreenPanel {

    // Пазим моделите на таблиците, за да можем да ги пълним и обновяваме с данни
    private final DefaultTableModel ownerModel;
    private final DefaultTableModel animalModel;

    // Връзките към базата данни
    private final OwnerDao ownerDao;
    private final AnimalDao animalDao;

    // Конструкторът приема конфигурацията и базата данни, както очаква главното приложение
    public CatalogPanel(AppConfig config, Database database) {
        // Инициализираме DAO обектите с подадената база данни
        this.ownerDao = new OwnerDao(database);
        this.animalDao = new AnimalDao(database);

        // Настройка на основния изглед на панела
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        // 1. Заглавна част
        JLabel titleLabel = new JLabel("Картотека", JLabel.LEFT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(new Color(44, 62, 80));
        add(titleLabel, BorderLayout.NORTH);

        // 2. Основен контейнер (разделен на две колони)
        JPanel mainContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        mainContainer.setOpaque(false);

        // Инициализираме празни модели за таблиците
        ownerModel = new DefaultTableModel(new String[]{"ID", "Име", "Телефон","Имейл"}, 0);
        animalModel = new DefaultTableModel(new String[]{"ID", "Име", "Вид", "Собственик"}, 0);

        // Създаваме и добавяме двете секции към екрана
        mainContainer.add(createRegistrySection("Собственици", ownerModel));
        mainContainer.add(createRegistrySection("Животни", animalModel));

        add(mainContainer, BorderLayout.CENTER);

        // 3. Зареждаме данните веднага при отваряне на екрана
        loadData();
    }

    /**
     * Изтегля данните от базата данни чрез DAO класовете и ги налива в таблиците.
     */
    public void loadData() {
        try {
            // Изчистваме старите редове преди да заредим новите (предотвратява дублиране)
            ownerModel.setRowCount(0);
            animalModel.setRowCount(0);
            // Зареждане на собственици
            List<Owner> owners = ownerDao.getAllOwners();
            for (Owner o : owners) {
                ownerModel.addRow(new Object[]{o.getId(), o.getName(), o.getPhone(), o.getEmail()});
            }
            // Зареждане на животни
            List<Animal> animals = animalDao.getAllAnimals();
            for (Animal a : animals) {
                animalModel.addRow(new Object[]{a.getId(), a.getName(), a.getSpecies(), a.getOwnerName()});
            }
            }
            catch(SQLException e){
            Dialogs.error(this, "Данните не можаха да се заредят.", e);
        }
    }

    /**
     * Помощен метод, който създава едната половина на екрана (заглавие, таблица и бутони).
     */
    private JPanel createRegistrySection(String title, DefaultTableModel model) {
        JPanel sectionPanel = new JPanel(new BorderLayout(10, 10));
        sectionPanel.setOpaque(false);

        // Заглавие на секцията
        JLabel sectionLabel = new JLabel(title);
        sectionLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        sectionLabel.setForeground(new Color(52, 73, 94));
        sectionPanel.add(sectionLabel, BorderLayout.NORTH);

        // Настройка на таблицата
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);

        // Добавяме скрол към таблицата
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        sectionPanel.add(scrollPane, BorderLayout.CENTER);

        // Панел за бутоните отдолу
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton btnAdd = createStyledButton("Добави");
        JButton btnEdit = createStyledButton("Редактирай");
        JButton btnDelete = createStyledButton("Изтрий");

        // Action Listeners (Placeholders)
        btnAdd.addActionListener(e -> {
            if (title.equals("Собственици")) {
                addOwnerDialog();
            } else {
                addAnimalDialog();
            }
        });
        btnEdit.addActionListener(e ->{if(title.equals("Собственици")){
            editOwnerDialog(table);

         }else{
            editAnimalDialog(table);
        }
        } );
        btnDelete.addActionListener(e -> {if(title.equals("Собственици")){
            deleteSelectedOwner(table);
        }else{
            deleteSelectedAnimal(table);
        }
        });

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        sectionPanel.add(buttonPanel, BorderLayout.SOUTH);

        return sectionPanel;
    }

    /**
     * Помощен метод за създаване на бутони.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        return button;
    }
    public String getTitle(){
        return "Картотека";
    }
    private void addOwnerDialog(){
        Window parentWindow =SwingUtilities.getWindowAncestor(this);
        OwnerDialog dialog = new OwnerDialog(parentWindow,null);
        dialog.setVisible(true);

        if(dialog.isSaved())
        {
         try {
             Owner newOwner= dialog.getOwnerData();
             ownerDao.addOwners(newOwner);
             loadData();
         } catch (SQLException ex ){
             Dialogs.error(this,"Грешка при запазването на собственик",ex);
         }
        }
    }
    private void editOwnerDialog(JTable table){
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Моля, изберете собственик от таблицата!", "Внимание", JOptionPane.WARNING_MESSAGE);
            return; // Спираме, ако няма избран ред
        }

        // Взимаме данните от избрания ред (Колона 0 е ID, Колона 1 е Име, Колона 2 е Телефон)
        int id = (int) table.getValueAt(selectedRow, 0);
        String name = (String) table.getValueAt(selectedRow, 1);
        String phone = (String) table.getValueAt(selectedRow, 2);

        // Създаваме обект със старите данни (имейлът го няма в таблицата, затова го оставяме празен засега)
        Owner ownerToEdit = new Owner(id, name, phone, "");

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        OwnerDialog dialog = new OwnerDialog(parentWindow, ownerToEdit);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            try {
                Owner updatedOwner = dialog.getOwnerData();
                ownerDao.updateOwner(updatedOwner);
                loadData(); // Презареждаме таблицата
            } catch (SQLException ex) {
                Dialogs.error(this, "Грешка при редакция на собственик.", ex);
            }
        }
    }
    private void deleteSelectedOwner(JTable table){
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Моля, изберете собственик за изтриване!", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) table.getValueAt(selectedRow, 0);
        String name = (String) table.getValueAt(selectedRow, 1);

        // Питаме за потвърждение (добра практика преди изтриване)
        int confirm = JOptionPane.showConfirmDialog(this,
                "Сигурни ли сте, че искате да изтриете собственик: " + name + "?",
                "Потвърждение за изтриване",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ownerDao.deleteOwner(id);
                loadData();
            } catch (SQLException ex) {
                // Проверяваме дали грешката е заради свързани животни (Foreign Key)
                if (ex.getMessage().contains("FOREIGN KEY constraint failed")) {
                    JOptionPane.showMessageDialog(this,
                            "Не можете да изтриете този собственик, защото към него има записани животни или прегледи!\n" +
                                    "Моля, първо изтрийте неговите животни.",
                            "Забранено изтриване",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    // Ако е друга грешка, показваме стандартното съобщение
                    Dialogs.error(this, "Грешка при изтриване на собственик.", ex);
                }
            }
        }
    }
    private void addAnimalDialog(){
        try {
            List<Owner> availableOwners = ownerDao.getAllOwners(); // Трябват ни за падащото меню
            Window parentWindow = SwingUtilities.getWindowAncestor(this);

            AnimalDialog dialog = new AnimalDialog(parentWindow, null, availableOwners);
            dialog.setVisible(true);

            if (dialog.isSaved()) {
                animalDao.addAnimal(dialog.getAnimalData());
                loadData();
            }
        } catch (SQLException ex) {
            Dialogs.error(this, "Грешка при отваряне или запис на животно.", ex);
        }
    }
    private void editAnimalDialog(JTable table){
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Моля, изберете животно!", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) table.getValueAt(selectedRow, 0);
        try {
            List<Owner> availableOwners = ownerDao.getAllOwners();
            // Намираме пълните данни за избраното животно от базата (заради ownerId)
            Animal animalToEdit = null;
            for (Animal a : animalDao.getAllAnimals()) {
                if (a.getId() == id) {
                    animalToEdit = a;
                    break;
                }
            }
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            AnimalDialog dialog = new AnimalDialog(parentWindow, animalToEdit, availableOwners);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                animalDao.updateAnimal(dialog.getAnimalData());
                loadData();
            }
        } catch (SQLException ex) {
            Dialogs.error(this, "Грешка при редакция.", ex);
        }
    }
    private void deleteSelectedAnimal(JTable table){
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Моля, изберете животно!", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) table.getValueAt(selectedRow, 0);
        String name = (String) table.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Изтриване на животно: " + name + "?",
                "Потвърждение", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                animalDao.deleteAnimal(id);
                loadData();
            } catch (SQLException ex) {
                Dialogs.error(this, "Грешка при изтриване.", ex);
            }
        }
    }
}