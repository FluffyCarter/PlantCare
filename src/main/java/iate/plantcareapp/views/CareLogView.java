package iate.plantcareapp.views;

import iate.plantcareapp.dao.CareLogDao;
import iate.plantcareapp.dao.PlantDao;
import iate.plantcareapp.models.CareLogEntry;
import iate.plantcareapp.models.Plant;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class CareLogView {

    private final CareLogDao careLogDao = new CareLogDao();
    private final PlantDao plantDao = new PlantDao(); // Создаем экземпляр PlantDao для получения информации о растении

    public void showCareLog() {
        Stage stage = new Stage();
        BorderPane root = new BorderPane();

        // Заголовок
        Label title = new Label("Журнал ухода за растениями");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        root.setTop(title);
        BorderPane.setMargin(title, new Insets(10));

        // Таблица для отображения записей
        TableView<CareLogEntry> tableView = new TableView<>();

        // Колонки таблицы
        TableColumn<CareLogEntry, String> plantColumn = new TableColumn<>("Растение");
        TableColumn<CareLogEntry, String> actionColumn = new TableColumn<>("Действие");
        TableColumn<CareLogEntry, String> dateColumn = new TableColumn<>("Дата");

        // Настройка столбца с названием растения
        plantColumn.setCellValueFactory(cellData -> {
            CareLogEntry entry = cellData.getValue();
            // Используем метод getPlantById для получения объекта Plant по ID
            Plant plant = plantDao.getPlantById(entry.getPlantId());
            // Возвращаем имя растения (или "Unknown", если не найдено)
            return new SimpleStringProperty(plant != null ? plant.getName() : "Unknown");
        });

        // Настройка столбца с действием
        actionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getActionType()));

        // Настройка столбца с датой
        dateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
        );

        // Добавление колонок в таблицу
        tableView.getColumns().addAll(plantColumn, actionColumn, dateColumn);

        // Получаем все записи из базы данных
        List<CareLogEntry> logs = careLogDao.getAllLogEntries();

        // Добавление записей в таблицу
        tableView.getItems().addAll(logs);

        // Добавляем таблицу на сцену
        root.setCenter(tableView);

        // Создаем сцену
        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Журнал ухода");
        stage.setScene(scene);
        stage.show();
    }
}