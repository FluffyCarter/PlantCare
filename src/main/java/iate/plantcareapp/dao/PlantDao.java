package iate.plantcareapp.dao;

import iate.plantcareapp.models.Plant;
import iate.plantcareapp.utils.Connector_db;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс Data Access Object (DAO) для работы с растениями в базе данных.
 * Обеспечивает CRUD-операции для сущности Plant.
 */
public class PlantDao {

    /**
     * Добавляет новое растение в базу данных.
     * @param plant Объект растения для добавления
     */
    public void addPlant(Plant plant) {
        String sql = "INSERT INTO plants (name, notes, image_path, watering_interval_days, fertilizing_interval_days, last_watered, last_fertilized) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Connector_db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, plant.getName());
            stmt.setString(2, plant.getNotes());
            stmt.setString(3, plant.getImagePath());
            stmt.setInt(4, plant.getWateringIntervalDays());
            stmt.setInt(5, plant.getFertilizingIntervalDays());

            LocalDate watered = plant.getLastWatered();
            if (watered != null) {
                stmt.setDate(6, Date.valueOf(watered));
            } else {
                stmt.setNull(6, Types.DATE);
            }

            LocalDate fertilized = plant.getLastFertilized();
            if (fertilized != null) {
                stmt.setDate(7, Date.valueOf(fertilized));
            } else {
                stmt.setNull(7, Types.DATE);
            }

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Получает список всех растений из базы данных.
     * @return Список объектов Plant
     */
    public List<Plant> getAllPlants() {
        List<Plant> plants = new ArrayList<>();
        String sql = "SELECT * FROM plants";

        try (Connection conn = Connector_db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Plant plant = new Plant(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("notes"),
                    rs.getString("image_path"),
                    rs.getInt("watering_interval_days"),
                    rs.getInt("fertilizing_interval_days")
                );

                // Дата последнего полива
                long wateredTimestampMillis = rs.getLong("last_watered");
                if (wateredTimestampMillis > 0) {
                    LocalDate wateredDate = LocalDateTime.ofEpochSecond(wateredTimestampMillis / 1000, 0, ZoneOffset.UTC).toLocalDate();
                    setPrivateField(plant, "lastWatered", wateredDate);
                }

                // Дата последнего удобрения
                long fertilizedTimestampMillis = rs.getLong("last_fertilized");
                if (fertilizedTimestampMillis > 0) {
                    LocalDate fertilizedDate = LocalDateTime.ofEpochSecond(fertilizedTimestampMillis / 1000, 0, ZoneOffset.UTC).toLocalDate();
                    setPrivateField(plant, "lastFertilized", fertilizedDate);
                }

                plants.add(plant);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return plants;
    }


    /**
     * Устанавливает значение приватного поля объекта Plant через reflection [ИСПРАВИТЬ].
     * @param plant Объект растения
     * @param fieldName Название поля
     * @param value Значение для установки
     */
    private void setPrivateField(Plant plant, String fieldName, LocalDate value) {
        try {
            // Получаем доступ к приватному полю через reflection
            var field = Plant.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(plant, value);
        } catch (Exception e) {
            // Логируем ошибки доступа к полю
            e.printStackTrace();
        }
    }

    /**
     * Обновляет дату последнего полива для указанного растения.
     * @param id ID растения
     * @param date Новая дата полива
     */
    public void updatePlantWateringDate(int id, LocalDate date) {
        String sql = "UPDATE plants SET last_watered = ? WHERE id = ?";

        try (Connection conn = Connector_db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {


            // Устанавливаем параметры запроса
            pstmt.setDate(1, Date.valueOf(date));
            pstmt.setInt(2, id);
            
            // Выполняем обновление
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error getting watering date: " + e.getMessage());
        }
    }

    /**
     * Обновляет дату последнего удобрения для указанного растения.
     * @param id ID растения
     * @param date Новая дата удобрения
     */
    public void updatePlantFertilizingDate(int id, LocalDate date) {
        String sql = "UPDATE plants SET last_fertilized = ? WHERE id = ?";

        try (Connection conn = Connector_db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Устанавливаем параметры запроса
            pstmt.setDate(1, Date.valueOf(date));
            pstmt.setInt(2, id);
            
            // Выполняем обновление
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error getting fert. date: " + e.getMessage());
        }
    }

    /**
     * Обновляет заметки для указанного растения.
     * @param plantId ID растения
     * @param newNotes Новый текст заметки
     */
    public void updateNotes(int plantId, String newNotes) {
        String sql = "UPDATE plants SET notes = ? WHERE id = ?";

        try (Connection conn = Connector_db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Устанавливаем параметры запроса
            stmt.setString(1, newNotes);
            stmt.setInt(2, plantId);
            
            // Выполняем обновление
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Notes update error: " + e.getMessage());
        }
    }
    
    /**
     * Получает растение по его ID из базы данных.
     * @param id ID растения
     * @return Объект Plant или null, если растение не найдено
     */
    public Plant getPlantById(int id) {
        Plant plant = null;
        String sql = "SELECT * FROM plants WHERE id = ?";

        try (Connection conn = Connector_db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    plant = new Plant(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("notes"),
                        rs.getString("image_path"),
                        rs.getInt("watering_interval_days"),
                        rs.getInt("fertilizing_interval_days")
                    );

                    // Безопасная установка даты последнего полива
                    java.sql.Date lastWateredDate = rs.getDate("last_watered");
                    if (lastWateredDate != null) {
                        setPrivateField(plant, "lastWatered", lastWateredDate.toLocalDate());
                    }

                    // Безопасная установка даты последней подкормки
                    java.sql.Date lastFertilizedDate = rs.getDate("last_fertilized");
                    if (lastFertilizedDate != null) {
                        setPrivateField(plant, "lastFertilized", lastFertilizedDate.toLocalDate());
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error during getting plant by ID: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Reflection error when setting dates: " + e.getMessage());
        }

        return plant;
    }

    
    // Метод для удаления растения
    public void deletePlant(int id) {
        String sql = "DELETE FROM plants WHERE id = ?";
        try (Connection conn = Connector_db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
