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
        setBackground(new Color(245, 247, 246));

        // 1. Заглавна част
        JLabel titleLabel = new JLabel("Картотека", JLabel.LEFT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(new Color(44, 62, 80));
        add(titleLabel, BorderLayout.NORTH);

        // 2. Основен контейнер (разделен на две колони)
        JPanel mainContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        mainContainer.setOpaque(false);

        // Инициализираме празни модели за таблиците
        ownerModel = new DefaultTableModel(new String[]{"ID", "Име", "Телефон"}, 0);
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
        // Изчистваме старите редове преди да заредим новите (предотвратява дублиране)
        ownerModel.setRowCount(0);
        animalModel.setRowCount(0);

        // Зареждане на собственици
        List<Owner> owners = ownerDao.getAllOwners();
        for (Owner o : owners) {
            ownerModel.addRow(new Object[]{o.getId(), o.getIme(), o.getTelefon()});
        }

        // Зареждане на животни
        List<Animal> animals = animalDao.getAllAnimals();
        for (Animal a : animals) {
            animalModel.addRow(new Object[]{a.getId(), a.getIme(), a.getVid(), a.getOwnerIme()});
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
        btnAdd.addActionListener(e -> System.out.println("Action: Добавяне в " + title));
        btnEdit.addActionListener(e -> System.out.println("Action: Редактиране в " + title));
        btnDelete.addActionListener(e -> System.out.println("Action: Изтриване от " + title));

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
}