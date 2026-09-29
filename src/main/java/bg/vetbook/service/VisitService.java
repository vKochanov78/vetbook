package bg.vetbook.service;

import bg.vetbook.config.AppConfig;
import bg.vetbook.dao.Database;
import bg.vetbook.dao.VisitCardDao;
import bg.vetbook.dao.VisitItemDao;
import bg.vetbook.model.Visit;
import bg.vetbook.model.VisitItem;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Правилата на амбулаторията.
// Този клас не пипа прозорци и не пише SQL - само проверява и смята.
// Затова може да се пробва, без да се отваря приложението.
public class VisitService {

    private AppConfig config;
    private VisitCardDao visitCardDao;
    private VisitItemDao visitItemDao;

    public VisitService(AppConfig config, Database database) {
        this.config = config;
        this.visitCardDao = new VisitCardDao(database);
        this.visitItemDao = new VisitItemDao(database);
    }

    // Такса за консултация.
    // Животно с валиден златен картон не плаща такса.
    public double feeFor(int animalId) throws SQLException {
        if (visitCardDao.hasValidGoldCard(animalId)) {
            return 0;
        }
        return config.getConsultationFee();
    }

    // Проверява прегледа срещу всички правила.
    // Връща списък със съобщения. Празен списък значи, че всичко е наред.
    public List<String> findProblems(Visit visit) throws SQLException {
        List<String> problems = new ArrayList<>();

        if (visit.getAnimalId() <= 0) {
            problems.add("Изберете животно.");
        }
        if (visit.getDoctorId() <= 0) {
            problems.add("Изберете лекуващ лекар.");
        }

        boolean dateOk = isDateValid(visit.getVisitDate());
        boolean timeOk = isTimeValid(visit.getVisitTime());

        if (!dateOk) {
            problems.add("Датата трябва да е във вид ГГГГ-ММ-ДД, например 2026-09-30.");
        }
        if (!timeOk) {
            problems.add("Часът трябва да е във вид ЧЧ:ММ, например 14:30.");
        }

        if (dateOk && timeOk && visit.getDoctorId() > 0) {
            checkDoctorsDay(visit, problems);
        }

        if (visit.isCompleted()) {
            checkCanBeCompleted(visit, problems);
        }

        return problems;
    }

    // Правило: най-много 8 прегледа на един лекар за един ден.
    // Правило: два прегледа на един лекар не могат да се застъпват по час.
    private void checkDoctorsDay(Visit visit, List<String> problems) throws SQLException {
        List<Visit> sameDay = visitCardDao.findByDoctorAndDate(
                visit.getDoctorId(), visit.getVisitDate());

        int others = 0;
        int newStart = minutesOfDay(visit.getVisitTime());

        for (Visit other : sameDay) {
            // Самият преглед не се брои срещу себе си при редактиране.
            if (other.getId() == visit.getId()) {
                continue;
            }
            others = others + 1;

            int otherStart = minutesOfDay(other.getVisitTime());
            int difference = Math.abs(newStart - otherStart);

            if (difference < config.getVisitMinutes()) {
                problems.add("Лекарят вече има преглед в " + other.getVisitTime()
                        + " ч. Прегледите се застъпват - един преглед заема "
                        + config.getVisitMinutes() + " минути.");
            }
        }

        int limit = config.getMaxVisitsPerDay();
        if (others >= limit) {
            problems.add("Лекарят вече има " + others + " прегледа за "
                    + visit.getVisitDate() + ". Повече от " + limit + " за един ден не се записват.");
        }
    }

    // Правило: преглед не се приключва без диагноза и поне една процедура.
    private void checkCanBeCompleted(Visit visit, List<String> problems) throws SQLException {
        String diagnosis = visit.getDiagnosis();
        if (diagnosis == null || diagnosis.trim().isEmpty()) {
            problems.add("Прегледът не може да се приключи без въведена диагноза.");
        }

        if (visit.isNew()) {
            problems.add("Прегледът не може да се приключи, преди да е записан и да има поне една процедура.");
            return;
        }

        List<VisitItem> items = visitItemDao.findByVisit(visit.getId());
        if (items.isEmpty()) {
            problems.add("Прегледът не може да се приключи без поне една процедура.");
        }
    }

    // ---------- проверки на дата и час ----------

    // LocalDate.parse отказва несъществуващи дати като 2026-13-45.
    private boolean isDateValid(String date) {
        if (date == null || date.length() != 10) {
            return false;
        }
        try {
            LocalDate.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTimeValid(String time) {
        if (time == null || time.length() != 5 || time.charAt(2) != ':') {
            return false;
        }
        int hours = numberOrMinusOne(time.substring(0, 2));
        int minutes = numberOrMinusOne(time.substring(3, 5));
        return hours >= 0 && hours <= 23 && minutes >= 0 && minutes <= 59;
    }

    // Превръща "14:30" в 870 - броя минути от началото на деня.
    // Така два часа се сравняват с просто изваждане.
    private int minutesOfDay(String time) {
        if (!isTimeValid(time)) {
            return -1;
        }
        int hours = Integer.parseInt(time.substring(0, 2));
        int minutes = Integer.parseInt(time.substring(3, 5));
        return hours * 60 + minutes;
    }

    private int numberOrMinusOne(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
