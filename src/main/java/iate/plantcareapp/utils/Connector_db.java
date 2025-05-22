package iate.plantcareapp.utils;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class Connector_db {

    private static final String URL = "jdbc:sqlite:plantcare.db";

    public static Connection getConnection() throws SQLException {
        try {
            // Явная загрузка драйвера SQLite
            Class.forName("org.sqlite.SQLiteJDBCLoader");
            return java.sql.DriverManager.getConnection(URL);
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found", e);
        }
    }

    public static void initDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // Включаем поддержку внешних ключей
            stmt.execute("PRAGMA foreign_keys = ON;");

            // Создание таблицы plants
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS plants (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    notes TEXT,
                    image_path TEXT,
                    watering_interval_days INTEGER NOT NULL,
                    fertilizing_interval_days INTEGER NOT NULL,
                    last_watered TEXT,
                    last_fertilized TEXT
                );
            """);

            // Создание таблицы care_log
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS care_log (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    plant_id INTEGER NOT NULL,
                    date TEXT NOT NULL,
                    action_type TEXT NOT NULL CHECK (action_type IN ('WATER', 'FERTILIZE')),
                    FOREIGN KEY (plant_id) REFERENCES plants(id) ON DELETE CASCADE
                );
            """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

