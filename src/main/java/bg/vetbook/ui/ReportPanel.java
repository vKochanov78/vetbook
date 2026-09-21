package bg.vetbook.ui;

import bg.vetbook.config.AppConfig;
import bg.vetbook.dao.Database;

import java.awt.BorderLayout;

/**
 * Екран „Справка“ — т. 4.4 от заданието.
 * Отговорник: Стоян.
 */
public class ReportPanel extends ScreenPanel {

    private final AppConfig config;
    private final Database database;

    public ReportPanel(AppConfig config, Database database) {
        this.config = config;
        this.database = database;

        setLayout(new BorderLayout());
        add(Placeholder.build(
                "Справка",
                "Стоян",
                "Избор на период — от дата и до дата",
                "Брой прегледи по статус за периода",
                "Приход по лекуващ лекар за периода",
                "Експорт на резултата в CSV файл"
        ), BorderLayout.CENTER);
    }

    @Override
    public String title() {
        return "Справка";
    }
}
