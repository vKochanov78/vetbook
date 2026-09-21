package bg.vetbook.ui;

import bg.vetbook.config.AppConfig;
import bg.vetbook.dao.Database;

import java.awt.BorderLayout;

/**
 * Екран „Карта на преглед“ — т. 4.2 от заданието.
 * Отговорник: Валентин.
 */
public class VisitCardPanel extends ScreenPanel {

    private final AppConfig config;
    private final Database database;

    public VisitCardPanel(AppConfig config, Database database) {
        this.config = config;
        this.database = database;

        setLayout(new BorderLayout());
        add(Placeholder.build(
                "Карта на преглед",
                "Валентин",
                "Горна част: животно, лекуващ лекар, дата и час, оплакване, диагноза, статус",
                "Долна част: таблица с процедурите и вложените медикаменти",
                "Добавяне и премахване на процедура с количество и единична цена",
                "Дължимата сума се преизчислява веднага при всяка промяна",
                "Такса за консултация " + config.consultationFee() + " € — без нея при валиден златен картон",
                "Приключен преглед се отваря само за разглеждане"
        ), BorderLayout.CENTER);
    }

    @Override
    public String title() {
        return "Карта на преглед";
    }
}
