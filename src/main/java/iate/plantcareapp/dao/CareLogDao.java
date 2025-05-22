package iate.plantcareapp.dao;

import iate.plantcareapp.utils.Connector_db;
import iate.plantcareapp.models.CareLogEntry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Date;
import java.time.Instant;
import java.time.ZoneId;

/**
 * DAO-класс для работы с журналом ухода за растениями.
 */
public class CareLogDao {

    /**
     * Добавляет новую запись в журнал ухода.
     * @param entry Объект CareLogEntry для вставки
     */
    public void addLogEntry(CareLogEntry entry) {
        String sql = "INSERT INTO care_log (plant_id, date, action_type) VALUES (?, ?, ?)";

        try (Connection conn = Connector_db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, entry.getPlantId());
            pstmt.setDate(2, Date.valueOf(entry.getDate()));
            pstmt.setString(3, entry.getActionType());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error adding entry to journal: " + e.getMessage());
        }
    }

    /**
     * Получает все записи журнала ухода, отсортированные по дате (от новых к старым).
     * @return Список CareLogEntry
     */
    
    public List<CareLogEntry> getAllLogEntries() {
        List<CareLogEntry> entries = new ArrayList<>();

        String sql = """
            SELECT cl.plant_id, cl.date, cl.action_type, p.name AS plant_name
            FROM care_log cl
            JOIN plants p ON cl.plant_id = p.id
            ORDER BY cl.date DESC
        """;

        try (Connection conn = Connector_db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int plantId = rs.getInt("plant_id");

                // Логируем метку времени
                long timestamp = rs.getLong("date");
                System.out.println("Timestamp: " + timestamp);

                LocalDate date = null;
                if (timestamp > 0) {
                    date = Instant.ofEpochMilli(timestamp)
                                  .atZone(ZoneId.systemDefault())
                                  .toLocalDate();
                } else {
                    System.out.println("Invalid timestamp detected for plant ID " + plantId);
                }

                String actionType = rs.getString("action_type");

                // Логируем имя растения
                String plantName = rs.getString("plant_name");
                if (plantName == null) {
                    System.out.println("Plant name is null for plant ID: " + plantId);
                    plantName = "Unknown";  // Можно заменить на "Unknown", если имя не найдено
                }

                entries.add(new CareLogEntry(plantId, date, actionType, plantName));
            }

        } catch (SQLException e) {
            System.err.println("Error getting journal: " + e.getMessage());
        }

        return entries;
    }

}