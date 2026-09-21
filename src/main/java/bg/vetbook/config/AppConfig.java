package bg.vetbook.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

// Настройките на програмата: такса за консултация, дневен лимит на лекар,
// път до базата и размери на прозореца.
// Стоят във файл, за да може да се променят, без да се пипа кодът.
public class AppConfig {

    private Properties values = new Properties();
    private String source = "вградени настройки";

    public AppConfig() {
        readBuiltInFile();
        readFileNextToProgram();
    }

    // Файлът vetbook.properties, който е вътре в програмата.
    private void readBuiltInFile() {
        try {
            InputStream in = AppConfig.class.getResourceAsStream("/vetbook.properties");
            InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
            values.load(reader);
            reader.close();
        } catch (Exception e) {
            throw new RuntimeException("Настройките не можаха да се прочетат.", e);
        }
    }

    // Ако до програмата има файл със същото име, стойностите в него са по-силни.
    // Така всеки може да си сложи различна такса или различен път до базата.
    private void readFileNextToProgram() {
        File file = new File("vetbook.properties");
        if (!file.exists()) {
            return;
        }
        try {
            FileInputStream in = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
            values.load(reader);
            reader.close();
            source = "настройки от " + file.getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException("Файлът vetbook.properties не можа да се прочете.", e);
        }
    }

    public String getDatabasePath() {
        return values.getProperty("db.path");
    }

    public double getConsultationFee() {
        return Double.parseDouble(values.getProperty("visit.consultationFee"));
    }

    public int getMaxVisitsPerDay() {
        return Integer.parseInt(values.getProperty("visit.maxPerDoctorPerDay"));
    }

    public int getVisitMinutes() {
        return Integer.parseInt(values.getProperty("visit.durationMinutes"));
    }

    public String getWindowTitle() {
        return values.getProperty("ui.window.title");
    }

    public int getWindowWidth() {
        return Integer.parseInt(values.getProperty("ui.window.width"));
    }

    public int getWindowHeight() {
        return Integer.parseInt(values.getProperty("ui.window.height"));
    }

    // Показва се долу в прозореца, за да се вижда откъде са дошли настройките.
    public String getSource() {
        return source;
    }
}
