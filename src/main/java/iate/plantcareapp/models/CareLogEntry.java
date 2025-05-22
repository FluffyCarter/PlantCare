package iate.plantcareapp.models;

import java.time.LocalDate;


public class CareLogEntry {
    // Идентификатор растения, к которому относится запись
    private int plantId;

    // Дата выполнения действия (полив, удобрение)
    private LocalDate date;

    // Тип действия (например, "WATER" или "FERTILIZE")
    private String actionType;

    // Название растения
    private String plantName;

    /**
     * Конструктор записи журнала ухода
     *
     * @param plantId    идентификатор растения
     * @param date       дата действия
     * @param actionType тип действия
     * @param plantName  название растения
     */
    public CareLogEntry(int plantId, LocalDate date, String actionType, String plantName) {
        this.plantId = plantId;
        this.date = date;
        this.actionType = actionType;
        this.plantName = plantName;
    }

    // Геттер для ID растения
    public int getPlantId() {
        return plantId;
    }

    // Геттер для даты действия
    public LocalDate getDate() {
        return date;
    }

    // Геттер для типа действия
    public String getActionType() {
        return actionType;
    }

    // Геттер для названия растения
    public String getPlantName() {
        return plantName;
    }
}
