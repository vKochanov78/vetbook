package bg.vetbook.dao;

import bg.vetbook.config.AppConfig;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Единствената точка, през която приложението стига до SQLite файла.
 * <p>
 * Всички DAO класове искат връзка оттук — така пътят до базата и настройките
 * на връзката са на едно място, а не разпръснати из целия проект.
 */
public final class Database {

    private final Path file;

    public Database(AppConfig config) {
        this.file = config.databasePath().toAbsolutePath();
    }

    /** Пътят до файла на базата — показва се в лентата долу в главния прозорец. */
    public Path file() {
        return file;
    }

    /**
     * Отваря нова връзка към базата.
     * <p>
     * Викащият я затваря — навсякъде в проекта се ползва
     * {@code try (Connection c = database.open()) { ... }}.
     */
    public Connection open() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + file);
            try (Statement statement = connection.createStatement()) {
                // Без този ред SQLite мълчаливо пренебрегва външните ключове,
                // тоест забраната за триене на собственик с прегледи няма да работи.
                statement.execute("PRAGMA foreign_keys = ON");
            }
            return connection;
        } catch (SQLException e) {
            throw new DataAccessException("Връзката с базата не можа да се отвори.", e);
        }
    }

    /**
     * Подготвя базата за работа: създава файла и таблиците, ако ги няма, и
     * зарежда примерните данни, ако базата е още празна.
     * <p>
     * Заданието иска приложението да тръгва веднага с данни за демонстрация.
     */
    public void initialise() {
        createParentFolder();

        try (Connection connection = open()) {
            runScript(connection, "/db/schema.sql");
            if (isEmpty(connection)) {
                runScript(connection, "/db/seed.sql");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Базата не можа да се подготви за работа.", e);
        }
    }

    private void createParentFolder() {
        Path folder = file.getParent();
        if (folder == null || Files.isDirectory(folder)) {
            return;
        }
        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            throw new DataAccessException("Папката " + folder + " не можа да се създаде.", e);
        }
    }

    /** Базата е празна, ако още няма нито един собственик. */
    private boolean isEmpty(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet rows = statement.executeQuery("SELECT COUNT(*) FROM owners")) {
            return rows.next() && rows.getInt(1) == 0;
        }
    }

    /**
     * Изпълнява .sql файл от ресурсите на проекта, ред по ред.
     * <p>
     * Заявките се разделят по знака „;“. Това работи, защото в нашите скриптове
     * няма точка и запетая вътре в текстова стойност — ако някой добави такава,
     * трябва да се мине към истински парсер.
     */
    private void runScript(Connection connection, String resource) throws SQLException {
        String script = readResource(resource);
        List<String> statements = splitStatements(script);

        boolean previousAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try (Statement statement = connection.createStatement()) {
            for (String sql : statements) {
                statement.execute(sql);
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new DataAccessException("Скриптът " + resource + " не можа да се изпълни.", e);
        } finally {
            connection.setAutoCommit(previousAutoCommit);
        }
    }

    private String readResource(String resource) {
        try (InputStream in = Database.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new DataAccessException("Липсва файлът " + resource + " в приложението.");
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DataAccessException("Файлът " + resource + " не можа да се прочете.", e);
        }
    }

    private List<String> splitStatements(String script) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String line : script.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                continue;
            }
            current.append(line).append('\n');
            if (trimmed.endsWith(";")) {
                String sql = current.toString().trim();
                statements.add(sql.substring(0, sql.length() - 1));
                current.setLength(0);
            }
        }

        String leftover = current.toString().trim();
        if (!leftover.isEmpty()) {
            statements.add(leftover);
        }
        return statements;
    }
}
