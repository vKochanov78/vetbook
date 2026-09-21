package bg.vetbook.dao;

import bg.vetbook.config.AppConfig;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// Всичко около файла на базата: отваряне на връзка, създаване на таблиците
// и зареждане на примерните данни при първо пускане.
public class Database {

    private String path;

    public Database(AppConfig config) {
        File file = new File(config.getDatabasePath());
        this.path = file.getAbsolutePath();
    }

    public String getPath() {
        return path;
    }

    // Отваря нова връзка към базата. Който я отвори, той я затваря.
    public Connection connect() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path);

        // Без този ред SQLite не проверява външните ключове и изтриването
        // на собственик, който има прегледи, би минало.
        Statement statement = connection.createStatement();
        statement.execute("PRAGMA foreign_keys = ON");
        statement.close();

        return connection;
    }

    // Подготвя базата: създава таблиците, ако ги няма, и слага
    // примерните данни, ако базата е още празна.
    public void setup() throws SQLException {
        createFolder();

        Connection connection = connect();
        try {
            runScript(connection, "/db/schema.sql");
            if (isEmpty(connection)) {
                runScript(connection, "/db/seed.sql");
            }
        } finally {
            connection.close();
        }
    }

    // Създава папката, в която ще стои файлът на базата.
    private void createFolder() {
        File folder = new File(path).getParentFile();
        if (folder != null && !folder.exists()) {
            folder.mkdirs();
        }
    }

    // Базата е празна, ако няма нито един собственик.
    private boolean isEmpty(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet rows = statement.executeQuery("SELECT COUNT(*) FROM owners");
        rows.next();
        int count = rows.getInt(1);
        rows.close();
        statement.close();
        return count == 0;
    }

    // Изпълнява .sql файл, който е сложен в ресурсите на проекта.
    private void runScript(Connection connection, String fileName) throws SQLException {
        String script = removeComments(readFile(fileName));
        String[] commands = script.split(";");

        Statement statement = connection.createStatement();
        for (int i = 0; i < commands.length; i++) {
            String sql = commands[i].trim();
            if (!sql.isEmpty()) {
                statement.execute(sql);
            }
        }
        statement.close();
    }

    private String readFile(String fileName) {
        try {
            InputStream in = Database.class.getResourceAsStream(fileName);
            byte[] bytes = in.readAllBytes();
            in.close();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Файлът " + fileName + " не можа да се прочете.", e);
        }
    }

    // Маха редовете с коментари, за да останат само заявките.
    private String removeComments(String script) {
        StringBuilder result = new StringBuilder();
        String[] lines = script.split("\n");

        for (int i = 0; i < lines.length; i++) {
            if (!lines[i].trim().startsWith("--")) {
                result.append(lines[i]);
                result.append("\n");
            }
        }
        return result.toString();
    }
}
