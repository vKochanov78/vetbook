package bg.vetbook.ui;
import bg.vetbook.model.Owner;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
public class OwnerDialog extends JDialog {
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    // Флаг, който ни казва дали потребителят е натиснал "Запази" (true) или "Отказ"/"Х"
    private boolean isSaved = false;
    // Пазим стария собственик, ако сме в режим на редакция
    private Owner existingOwner;
    // Конструкторът приема родителския прозорец и обект Owner (ако е null, значи добавяме нов)
    public OwnerDialog(Window parent,Owner ownertoEdit ) {
        super(parent, ownertoEdit == null ? "Добавяне на собственик" : "Редакция на собственик", ModalityType.APPLICATION_MODAL);
        this.existingOwner = ownertoEdit;

        //Основни параметри на прозореца
        setSize(350, 350);
        setLocationRelativeTo(parent); // в центъра на екрана
        setResizable(false);
        // Основен контейнер с отстояния
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        // форма за въвеждане
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));

        formPanel.add(new JLabel("Име: "));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Телефон: "));
        phoneField = new JTextField();
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Имейл: "));
        emailField = new JTextField();
        formPanel.add(emailField);

        // Ако редактираме съществуващ собственик, зареждаме данните му в полетата
        if (existingOwner != null) {
            nameField.setText(existingOwner.getName());
            phoneField.setText(existingOwner.getPhone());
            emailField.setText(existingOwner.getEmail());
        }
        mainPanel.add(formPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Запази");
        JButton cancelBtn = new JButton("Откажи");
        saveBtn.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Полето Име е задължително!",
                        "Грешка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            isSaved = true; //Отбелязва се, че прозореца е затворен с успех
            dispose(); //Затваря се прозореца
        });
        // Логиката за Отказ бутона
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

    }
        //Връща true, ако потребителят е натиснал бутона "Запази" и е попълнил валидни данни.
        public boolean isSaved(){
            return isSaved;
        }

        public Owner getOwnerData(){
            int id = (existingOwner!=null)?existingOwner.getId():0 ;
            return new Owner(id,
                    nameField.getText().trim(),
                    phoneField.getText().trim(),
                    emailField.getText().trim()
        );
        }
    }
