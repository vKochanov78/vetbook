package bg.vetbook.ui;

import bg.vetbook.config.AppConfig;
import bg.vetbook.dao.Database;

import java.awt.*;

// Екран „Прегледи“ - списъкът с всички прегледи.
// Прави го Васил.
public class VisitsPanel extends ScreenPanel {

    private AppConfig config;
    private Database database;

    public VisitsPanel(AppConfig config, Database database) {
        this.config = config;
        this.database = database;

        String[] tasks = {
                "Таблица с прегледите: дата и час, животно, собственик, лекар, статус, сума",
                "Филтър по дата и по статус",
                "Търсене по име на животно или на собственик",
                "Бутон за нов преглед - отваря екран „Карта на преглед“",
                "Двойно щракване върху ред - отваря съществуващия преглед"
        };

        setLayout(new BorderLayout());
        add(Placeholder.create("Прегледи", "Васил", tasks), BorderLayout.CENTER);
    }

    public String getTitle() {
        return "Прегледи";
    }
}
