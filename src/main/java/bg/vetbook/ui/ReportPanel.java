package bg.vetbook.ui;

import bg.vetbook.config.AppConfig;
import bg.vetbook.dao.Database;

import java.awt.*;

// Екран „Справка“ - обобщение за избран период.
// Прави го Стоян.
public class ReportPanel extends ScreenPanel {

    private AppConfig config;
    private Database database;

    public ReportPanel(AppConfig config, Database database) {
        this.config = config;
        this.database = database;

        String[] tasks = {
                "Избор на период - от дата и до дата",
                "Брой прегледи по статус за периода",
                "Приход по лекуващ лекар за периода",
                "Запис на резултата в CSV файл"
        };

        setLayout(new BorderLayout());
        add(Placeholder.create("Справка", "Стоян", tasks), BorderLayout.CENTER);
    }

    public String getTitle() {
        return "Справка";
    }
}
