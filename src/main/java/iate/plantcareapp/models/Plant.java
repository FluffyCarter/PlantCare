package iate.plantcareapp.models;

import java.time.LocalDate;

public class Plant {
    // Уникальный идентификатор растения
    private int id;

    // Название растения
    private String name;

    // Дополнительные заметки о растении
    private String notes;

    // Относительный путь к изображению растения
    private String imagePath;

    // Интервал полива в днях
    private int wateringIntervalDays;

    // Интервал удобрения в днях
    private int fertilizingIntervalDays;

    // Дата последнего полива
    private LocalDate lastWatered;

    // Дата последнего удобрения
    private LocalDate lastFertilized;

    // Конструктор класса, инициализирует основные поля
    public Plant(int id, String name, String notes, String imagePath, 
                 int wateringIntervalDays, int fertilizingIntervalDays) {
        this.id = id;
        this.name = name;
        this.notes = notes;
        this.imagePath = imagePath;
        this.wateringIntervalDays = wateringIntervalDays;
        this.fertilizingIntervalDays = fertilizingIntervalDays;

        // Устанавливаем дату последнего полива и удобрения так,
        // чтобы растение сразу считалось нуждающимся в уходе
        this.lastWatered = LocalDate.now().minusDays(wateringIntervalDays);
        this.lastFertilized = LocalDate.now().minusDays(fertilizingIntervalDays);
    }

    // Геттеры — методы для получения значений полей

    public int getId() { return id; }
    public String getName() { return name; }
    public String getNotes() { return notes; }
    public String getImagePath() { return imagePath; }
    public int getWateringIntervalDays() { return wateringIntervalDays; }
    public int getFertilizingIntervalDays() { return fertilizingIntervalDays; }
    public LocalDate getLastWatered() { return lastWatered; }
    public LocalDate getLastFertilized() { return lastFertilized; }

    // Сеттер для заметок
    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Метод для обновления даты последнего полива на сегодня
    public void water() {
        this.lastWatered = LocalDate.now();
    }

    // Метод для обновления даты последнего удобрения на сегодня
    public void fertilize() {
        this.lastFertilized = LocalDate.now();
    }

    // Метод проверяет, требуется ли полив
    public boolean needsWatering() {
        return LocalDate.now().isAfter(lastWatered.plusDays(wateringIntervalDays));
    }

    // Метод проверяет, требуется ли удобрение
    public boolean needsFertilizing() {
        return LocalDate.now().isAfter(lastFertilized.plusDays(fertilizingIntervalDays));
    }

    // Переопределённый метод toString для отображения растения в списке
    @Override
    public String toString() {
        return name + " (Полив: каждые " + wateringIntervalDays + " дней)";
    }
}
