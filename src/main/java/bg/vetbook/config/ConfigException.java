package bg.vetbook.config;

/**
 * Хвърля се, когато настройките липсват или са невалидни.
 * <p>
 * Наследява {@code RuntimeException}, защото при счупени настройки няма смисъл
 * приложението да продължава — показва се съобщение и се спира (виж {@code App}).
 */
public class ConfigException extends RuntimeException {

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
