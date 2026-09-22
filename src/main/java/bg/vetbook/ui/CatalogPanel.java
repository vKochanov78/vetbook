package bg.vetbook.ui;

import bg.vetbook.config.AppConfig;
import bg.vetbook.dao.Database;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


// Екран „Картотека“ - собствениците и техните животни.
// Прави го Стоян.
public class CatalogPanel extends ScreenPanel {

    private AppConfig config;
    private Database database;

    public CatalogPanel(AppConfig config, Database database) {
        this.config = config;
        this.database = database;
          setLayout(new BorderLayout(15,15));
          setBorder(new EmptyBorder(20,20,20,20));
          setBackground(new Color(245,247,246));
          // 1 Title Section
        JLabel tittlelabel= new JLabel("Картотека",JLabel.LEFT);
        tittlelabel.setFont(new Font("SansSerif",Font.BOLD,26));
        tittlelabel.setForeground(new Color(44,62,80));
        add(tittlelabel,BorderLayout.NORTH);

        // 2 Main Екрана
        JPanel mainekran = new JPanel(new GridLayout(1,2,20,0));
        mainekran.setOpaque(false);
        //  Собственика
        mainekran.add(createCatalogSection("Собственици",new String[]{"ID","Име","Телефон"}));
        // Животни
        mainekran.add(createCatalogSection("Животни",new String[]{"ID","Име","Вид","Собственик"}));
        add(mainekran,BorderLayout.CENTER);

    }
    private JPanel createCatalogSection(String title, String[] columnNames) {

        JPanel sectionPanel = new JPanel(new BorderLayout(10, 10));

        sectionPanel.setOpaque(false);

        // Section Title

        JLabel sectionLabel = new JLabel(title);

        sectionLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        sectionLabel.setForeground(new Color(52, 73, 94));

        sectionPanel.add(sectionLabel, BorderLayout.NORTH);

        // Table Implementation

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        JTable table = new JTable(model);

        table.setRowHeight(25);

        table.getTableHeader().setReorderingAllowed(false);



        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.getViewport().setBackground(Color.WHITE);

        sectionPanel.add(scrollPane, BorderLayout.CENTER);

        // Button Panel

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        buttonPanel.setOpaque(false);

        JButton btnAdd = createStyledButton("Добави", new Color(0, 0, 0));

        JButton btnEdit = createStyledButton("Редактирай", new Color(0, 0, 0));

        JButton btnDelete = createStyledButton("Изтрий", new Color(0, 0, 0));

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
// Бутони за UI
    private JButton createStyledButton(String text,Color baseColor){
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        return button;
    }

    public String getTitle() {
        return "Картотека";
    }
}
