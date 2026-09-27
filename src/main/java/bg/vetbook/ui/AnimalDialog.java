package bg.vetbook.ui;
import bg.vetbook.model.Animal;
import bg.vetbook.model.Owner;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class AnimalDialog extends JDialog {
    private JTextField nameField;
    private JTextField speciesField;
    private JComboBox<Owner> ownerComboBox;
    private boolean isSaved = false;
    private Animal existingAnimal;
    public AnimalDialog(Window parent, Animal animalToEdit, List<Owner> availableOwners) {
        super(parent, animalToEdit == null ? "Добавяне на животно" : "Редакция на животно", ModalityType.APPLICATION_MODAL);
        this.existingAnimal = animalToEdit;
        setSize(350, 250);
        setLocationRelativeTo(parent);
        setResizable(false);
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        //  Форма
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        formPanel.add(new JLabel("Име: *"));
        nameField = new JTextField();
        formPanel.add(nameField);
        formPanel.add(new JLabel("Вид (напр. куче): *"));
        speciesField = new JTextField();
        formPanel.add(speciesField);
        formPanel.add(new JLabel("Собственик: *"));
        // Пълним падащото меню с обектите Owner, които сме извлекли от базата
        ownerComboBox = new JComboBox<>(availableOwners.toArray(new Owner[0]));
        formPanel.add(ownerComboBox);
        // Ако редактираме, попълваме старите данни
        if (existingAnimal != null) {
            nameField.setText(existingAnimal.getName());
            speciesField.setText(existingAnimal.getSpecies());
            // Намираме правилния собственик в менюто и го маркираме
            for (int i = 0; i < ownerComboBox.getItemCount(); i++) {
                Owner o = ownerComboBox.getItemAt(i);
                if (o.getId() == existingAnimal.getOwnerId()) {
                    ownerComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
        mainPanel.add(formPanel, BorderLayout.CENTER);
        // --- БУТОНИ ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Запази");
        JButton cancelBtn = new JButton("Отказ");

        saveBtn.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty() || speciesField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Полетата 'Име' и 'Вид' са задължителни!", "Грешка", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (ownerComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Моля, изберете собственик!", "Грешка", JOptionPane.WARNING_MESSAGE);
                return;
            }
            isSaved = true;
            dispose();
        });
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    public boolean isSaved() {
        return isSaved;
    }
    public Animal getAnimalData() {
        int id = (existingAnimal != null) ? existingAnimal.getId() : 0;
        Owner selectedOwner = (Owner) ownerComboBox.getSelectedItem();
        int ownerId = (selectedOwner != null) ? selectedOwner.getId() : 0;
        Animal animal = new Animal(id, nameField.getText().trim(), speciesField.getText().trim(), ownerId);
        if (selectedOwner != null) {
            animal.setOwnerName(selectedOwner.getName());
        }
        return animal;
    }
}
