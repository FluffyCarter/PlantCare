package iate.plantcareapp.views;

import iate.plantcareapp.dao.PlantDao;
import iate.plantcareapp.models.Plant;
import iate.plantcareapp.utils.Connector_db;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/*
 * Главный класс приложения, реализующий основной интерфейс
 */
public class MainView extends Application {

    // DAO для работы с растениями
    private final PlantDao plantDao = new PlantDao();
    
    // Основная сетка для отображения карточек растений
    private GridPane grid;
    
    // Менеджер для управления карточками растений
    private PlantCardManager cardManager;

    /*
     * Точка входа в приложение
     */
    public static void main(String[] args) {
        launch(args);
    }

    /*
     * Основной метод запуска JavaFX приложения
     */
    @Override
    public void start(Stage primaryStage) {
        // Инициализация базы данных
        Connector_db.initDatabase();
        
        // Создание корневого контейнера
        BorderPane root = new BorderPane();

        // Создание верхней панели с кнопками
        HBox header = new HBox();
        header.setPadding(new Insets(10));
        header.setSpacing(10);
        header.setAlignment(Pos.CENTER_RIGHT);

        
        // Кнопка добавления нового растения
        Button addButton = new Button("+ Добавить растение");
        // Стилизация кнопки
        addButton.setStyle(
            "-fx-background-color: #4CAF50;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 16;" +
            "-fx-cursor: hand;"
        );
        // Эффекты при наведении
        addButton.setOnMouseEntered(e -> addButton.setStyle(
            "-fx-background-color: #45a049;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 16;" +
            "-fx-cursor: hand;"
        ));
        addButton.setOnMouseExited(e -> addButton.setStyle(
            "-fx-background-color: #4CAF50;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 16;" +
            "-fx-cursor: hand;"
        ));
        // Обработчик нажатия
        addButton.setOnAction(e -> {
            AddPlantView addView = new AddPlantView();
            addView.showAddPlantForm(this::refreshPlants);
        });

        //Кнопка журнала ухода
        Button journalButton = new Button("Журнал ухода");
        // Стилизация кнопки
        journalButton.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 16;" +
            "-fx-cursor: hand;"
        );
        // Эффекты при наведении
        journalButton.setOnMouseEntered(e -> journalButton.setStyle(
            "-fx-background-color: #1976D2;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 16;" +
            "-fx-cursor: hand;"
        ));
        journalButton.setOnMouseExited(e -> journalButton.setStyle(
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 16;" +
            "-fx-cursor: hand;"
        ));
        // Обработчик нажатия
        journalButton.setOnAction(e -> {
            CareLogView careLogView = new CareLogView();
            careLogView.showCareLog();
        });

        // Добавление кнопок на верхнюю панель
        header.getChildren().addAll(journalButton, addButton);
        root.setTop(header);

        // Инициализация сетки для карточек растений
        grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(15);
        grid.setVgap(15);

        // Создание менеджера карточек
        cardManager = new PlantCardManager(grid, this);
        refreshPlants();

        // Добавление прокрутки
        ScrollPane scrollPane = new ScrollPane(grid);
        root.setCenter(scrollPane);

        // Создание и отображение сцены
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Plant Care App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    //Обновление списка растений
    public void refreshPlants() {
        cardManager.refreshPlants();
    }

    //Обновление конкретной карточки растения
    public void updatePlantCard(Plant plant) {
        cardManager.updatePlantCard(plant);
    }
}