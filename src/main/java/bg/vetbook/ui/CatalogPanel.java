package bg.vetbook.ui;

import bg.vetbook.config.AppConfig;
import bg.vetbook.dao.Database;

import java.awt.*;

// Екран „Картотека“ - собствениците и техните животни.
// Прави го Стоян.
public class CatalogPanel extends ScreenPanel {

    private AppConfig config;
    private Database database;

    public CatalogPanel(AppConfig config, Database database) {
        this.config = config;
        this.database = database;

        String[] tasks = {
                "Списък със собственици и животните на избрания собственик",
                "Добавяне, редактиране и изтриване на собственик",
                "Добавяне, редактиране и изтриване на животно",
                "Проверка на телефон и e-mail при въвеждане",
                "Забрана за изтриване на собственик или животно, за което има прегледи"
        };

        setLayout(new BorderLayout());
        add(Placeholder.create("Картотека", "Стоян", tasks), BorderLayout.CENTER);
    }

    public String getTitle() {
        return "Картотека";
    }
}
