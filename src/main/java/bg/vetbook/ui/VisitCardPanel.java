package bg.vetbook.ui;

import bg.vetbook.config.AppConfig;
import bg.vetbook.dao.Database;

import java.awt.*;

// Екран „Карта на преглед“ - въвеждане и редактиране на един преглед.
// Прави го Валентин.
public class VisitCardPanel extends ScreenPanel {

    private AppConfig config;
    private Database database;

    public VisitCardPanel(AppConfig config, Database database) {
        this.config = config;
        this.database = database;

        String[] tasks = {
                "Горе: животно, лекуващ лекар, дата и час, оплакване, диагноза, статус",
                "Долу: таблица с процедурите и вложените медикаменти",
                "Добавяне и премахване на процедура с количество и единична цена",
                "Сумата се преизчислява веднага при всяка промяна",
                "Такса за консултация " + config.getConsultationFee() + " € - без нея при валиден златен картон",
                "Приключен преглед се отваря само за разглеждане"
        };

        setLayout(new BorderLayout());
        add(Placeholder.create("Карта на преглед", "Валентин", tasks), BorderLayout.CENTER);
    }

    public String getTitle() {
        return "Карта на преглед";
    }
}
