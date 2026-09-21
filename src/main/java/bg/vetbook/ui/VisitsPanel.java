package bg.vetbook.ui;

import bg.vetbook.config.AppConfig;
import bg.vetbook.dao.Database;

import java.awt.BorderLayout;

/**
 * Екран „Прегледи“ — т. 4.1 от заданието.
 * Отговорник: Васил.
 */
public class VisitsPanel extends ScreenPanel {

    private final AppConfig config;
    private final Database database;

    public VisitsPanel(AppConfig config, Database database) {
        this.config = config;
        this.database = database;

        setLayout(new BorderLayout());
        add(Placeholder.build(
                "Прегледи",
                "Васил",
                "Таблица с всички прегледи: дата и час, животно, собственик, лекар, статус, сума",
                "Филтър по дата и по статус",
                "Търсене по име на животно или на собственик",
                "Бутон за нов преглед — отваря екран „Карта на преглед“",
                "Двойно щракване върху ред — отваря съществуващия преглед"
        ), BorderLayout.CENTER);
    }

    @Override
    public String title() {
        return "Прегледи";
    }
}
