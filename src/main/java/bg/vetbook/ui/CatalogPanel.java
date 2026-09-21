package bg.vetbook.ui;

import bg.vetbook.config.AppConfig;
import bg.vetbook.dao.Database;

import java.awt.BorderLayout;

/**
 * Екран „Картотека“ — т. 4.3 от заданието.
 * Отговорник: Стоян.
 */
public class CatalogPanel extends ScreenPanel {

    private final AppConfig config;
    private final Database database;

    public CatalogPanel(AppConfig config, Database database) {
        this.config = config;
        this.database = database;

        setLayout(new BorderLayout());
        add(Placeholder.build(
                "Картотека",
                "Стоян",
                "Списък със собственици и животните на избрания собственик",
                "Добавяне, редактиране и изтриване на собственик",
                "Добавяне, редактиране и изтриване на животно",
                "Валидация на телефон и e-mail при въвеждане",
                "Забрана за изтриване на собственик или животно, за което има прегледи"
        ), BorderLayout.CENTER);
    }

    @Override
    public String title() {
        return "Картотека";
    }
}
