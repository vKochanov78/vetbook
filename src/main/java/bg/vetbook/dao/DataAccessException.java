package bg.vetbook.dao;

/**
 * Обвива техническите грешки от базата ({@code SQLException}) в едно понятие,
 * което горните слоеве могат да хващат.
 * <p>
 * Смисълът: интерфейсът не трябва да знае нищо за JDBC. Хваща се тази грешка,
 * показва се съобщение на потребителя и приложението продължава да работи —
 * заданието изрично иска грешка да не води до срив (т. 6).
 */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
