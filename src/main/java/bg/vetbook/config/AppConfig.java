package bg.vetbook.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Настройките на приложението — такси, лимити и път до базата.
 * <p>
 * Заданието (т. 6) изисква тези неща да не се пишат направо в кода. Затова
 * стойностите по подразбиране идват от {@code vetbook.properties} вътре в
 * приложението, а ако до стартирания .jar има файл със същото име, той
 * надделява. Така всеки от отбора може да си сложи различен път до базата,
 * без да променя нито един ред Java.
 */
public final class AppConfig {

    /** Файлът с настройките по подразбиране, вграден в приложението. */
    private static final String BUNDLED_FILE = "/vetbook.properties";

    /** Незадължителен файл до приложението, който надделява над вградения. */
    private static final String EXTERNAL_FILE = "vetbook.properties";

    private final Properties values;
    private final Path externalFile;

    private AppConfig(Properties values, Path externalFile) {
        this.values = values;
        this.externalFile = externalFile;
    }

    /**
     * Зарежда настройките: първо вградените, после външните върху тях.
     *
     * @throws ConfigException ако вграденият файл липсва или не може да се прочете
     */
    public static AppConfig load() {
        Properties values = new Properties();

        try (InputStream in = AppConfig.class.getResourceAsStream(BUNDLED_FILE)) {
            if (in == null) {
                throw new ConfigException(
                        "Липсва вграденият файл с настройки " + BUNDLED_FILE + ".");
            }
            values.load(new java.io.InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ConfigException("Вградените настройки не можаха да се прочетат.", e);
        }

        Path external = Path.of(EXTERNAL_FILE);
        if (!Files.isRegularFile(external)) {
            return new AppConfig(values, null);
        }

        try (Reader reader = Files.newBufferedReader(external, StandardCharsets.UTF_8)) {
            values.load(reader);
        } catch (IOException e) {
            throw new ConfigException(
                    "Файлът " + external.toAbsolutePath() + " не можа да се прочете.", e);
        }
        return new AppConfig(values, external);
    }

    /** Път до файла на базата. Папката му се създава при нужда от {@code Database}. */
    public Path databasePath() {
        return Path.of(text("db.path"));
    }

    /** Такса за консултация в евро (т. 5 от заданието). */
    public BigDecimal consultationFee() {
        return money("visit.consultationFee");
    }

    /** Максимален брой прегледи на един лекар за един ден. */
    public int maxVisitsPerDoctorPerDay() {
        return positiveNumber("visit.maxPerDoctorPerDay");
    }

    /** Продължителност на един преглед в минути — за проверката за застъпване. */
    public int visitDurationMinutes() {
        return positiveNumber("visit.durationMinutes");
    }

    public String windowTitle() {
        return text("ui.window.title");
    }

    public int windowWidth() {
        return positiveNumber("ui.window.width");
    }

    public int windowHeight() {
        return positiveNumber("ui.window.height");
    }

    /** Откъде идват настройките — показва се в лентата долу, полезно при проблем. */
    public String sourceDescription() {
        return externalFile == null
                ? "настройки по подразбиране"
                : "настройки от " + externalFile.toAbsolutePath();
    }

    // ---------- помощни ----------

    private String text(String key) {
        String value = values.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new ConfigException("В настройките липсва стойност за „" + key + "“.");
        }
        return value.trim();
    }

    private int positiveNumber(String key) {
        String value = text(key);
        int number;
        try {
            number = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ConfigException(
                    "Настройката „" + key + "“ трябва да е цяло число, а е „" + value + "“.", e);
        }
        if (number <= 0) {
            throw new ConfigException(
                    "Настройката „" + key + "“ трябва да е по-голяма от нула, а е " + number + ".");
        }
        return number;
    }

    private BigDecimal money(String key) {
        String value = text(key);
        try {
            BigDecimal amount = new BigDecimal(value);
            if (amount.signum() < 0) {
                throw new ConfigException(
                        "Настройката „" + key + "“ не може да е отрицателна сума.");
            }
            return amount.setScale(2, java.math.RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            throw new ConfigException(
                    "Настройката „" + key + "“ трябва да е сума, а е „" + value + "“.", e);
        }
    }
}
