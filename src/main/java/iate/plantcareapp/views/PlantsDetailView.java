package iate.plantcareapp.views;

import iate.plantcareapp.dao.PlantDao;
import iate.plantcareapp.dao.CareLogDao;
import iate.plantcareapp.models.CareLogEntry;
import iate.plantcareapp.models.Plant;
import java.io.File;
import java.io.FileInputStream;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.io.InputStream;

public class PlantsDetailView {

    // DAO для работы с БД
    private final PlantDao plantDao = new PlantDao();
    private final CareLogDao careLogDao = new CareLogDao();
    private final MainView mainView;

    // Конструктор принимает ссылку на главное окно
    public PlantsDetailView(MainView mainView) {
        this.mainView = mainView;
    }

    // Метод отображения окна с деталями растения
    public void showDetails(Plant plant) {
        Stage detailStage = new Stage();
        detailStage.initModality(Modality.APPLICATION_MODAL);
        detailStage.setTitle("Карточка растения");

        // Основной контейнер окна
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(25));
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f7fa, #e4e8f0);");
        mainContainer.setAlignment(Pos.TOP_CENTER);

        // Название растения
        Label nameLabel = new Label(plant.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        nameLabel.setTextFill(Color.web("#2c3e50"));
        
        // Контейнер изображения
        StackPane imageContainer = new StackPane();
        imageContainer.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        imageContainer.setPadding(new Insets(10));
        
        // Загрузка изображения растения или установка изображения по умолчанию
        ImageView imageView;
        
        /*
        try {
            File imageFile = new File("src/main/resources/" + plant.getImagePath());
            Image image = new Image(imageFile.toURI().toString(), 100, 100, true, true);
            imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
        } catch (Exception e) {
            imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/default-plant.png")));
            imageView.setFitWidth(180);
            imageView.setFitHeight(180);
            imageView.setOpacity(0.7);
        }
        */
        /*
        try {
            // Пытаемся загрузить изображение через ClassLoader (работает и в JAR, и в IDE)
            String imagePath = "/" + plant.getImagePath().replace("\\", "/"); // Добавляем / в начало
            
            InputStream imageStream = getClass().getResourceAsStream(imagePath);

            if (imageStream != null) {
                Image image = new Image(imageStream, 100, 100, true, true);
                imageView = new ImageView(image);
                imageView.setPreserveRatio(true);
            } else {
                // Если основное изображение не найдено, загружаем дефолтное
                throw new RuntimeException("Main image not found");
            }
        } catch (Exception e) {
            // Загружаем дефолтное изображение
            try {
                imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/default-plant.png")));
                imageView.setFitWidth(180);
                imageView.setFitHeight(180);
                imageView.setOpacity(0.7);
            } catch (Exception ex) {
                // Если даже дефолтное изображение не загрузилось
                imageView = new ImageView();
                System.err.println("Failed to load default image: " + ex.getMessage());
            }
        }*/
        
        try {
            // Берем путь изображения, убираем возможную лишнюю папку 'images' в начале пути
            String imagePath = plant.getImagePath().replace("\\", "/"); // Преобразуем слэши

            // Если в пути уже есть лишняя папка images, убираем ее
            if (imagePath.startsWith("images/")) {
                imagePath = imagePath.substring("images/".length());
            }

            // Формируем путь относительно папки src/main/resources/images
            File imageFile = new File("src/main/resources/images/" + imagePath); // Путь к файлу
            System.out.println("Trying to load image at path: " + imageFile.getAbsolutePath());

            if (imageFile.exists()) {
                // Если изображение существует, загружаем его
                InputStream imageStream = new FileInputStream(imageFile);
                Image image = new Image(imageStream, 100, 100, true, true);
                imageView = new ImageView(image);
                imageView.setPreserveRatio(true);
            } else {
                // Если изображение не найдено, загружаем дефолтное изображение
                throw new RuntimeException("Main image not found");
            }
        } catch (Exception e) {
            // Загружаем дефолтное изображение
            try {
                imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/default-plant.png")));
                imageView.setFitWidth(180);
                imageView.setFitHeight(180);
                imageView.setOpacity(0.7);
            } catch (Exception ex) {
                // Если даже дефолтное изображение не загрузилось
                imageView = new ImageView();
                System.err.println("Failed to load default image: " + ex.getMessage());
            }
        }


        imageContainer.getChildren().add(imageView);

        // Панель ухода за растением
        VBox carePanel = new VBox(10);
        carePanel.setPadding(new Insets(15));
        carePanel.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        
        Label careTitle = new Label("Уход за растением");
        careTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        careTitle.setTextFill(Color.web("#3498db"));

        // Информация о частоте полива и удобрения
        Label wateringInfo = createInfoLabel("💧 Полив", "каждые " + plant.getWateringIntervalDays() + " дней");
        Label fertilizingInfo = createInfoLabel("🌿 Удобрение", "каждые " + plant.getFertilizingIntervalDays() + " дней");
        
        // Последние даты ухода
        HBox waterStatus = createStatusBox("Последний полив:", plant.getLastWatered(), "#3498db");
        HBox fertilizeStatus = createStatusBox("Последнее удобрение:", plant.getLastFertilized(), "#2ecc71");
        
        // Прогресс-бары по уходу
        ProgressBar waterProgress = createProgressBar(plant.getLastWatered(), plant.getWateringIntervalDays(), "#3498db");
        ProgressBar fertilizeProgress = createProgressBar(plant.getLastFertilized(), plant.getFertilizingIntervalDays(), "#2ecc71");

        // Добавление компонентов в панель ухода
        carePanel.getChildren().addAll(
            careTitle, new Separator(),
            wateringInfo, waterStatus, waterProgress,
            new Separator(),
            fertilizingInfo, fertilizeStatus, fertilizeProgress
        );

        // Панель заметок
        VBox notesPanel = new VBox(10);
        notesPanel.setPadding(new Insets(15));
        notesPanel.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        
        Label notesTitle = new Label("Мои заметки");
        notesTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        notesTitle.setTextFill(Color.web("black"));

        // Текстовое поле для заметок
        TextArea notesArea = new TextArea(plant.getNotes());
        notesArea.setPromptText("Добавьте заметки о растении...");
        notesArea.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-radius: 5;");
        notesArea.setPrefRowCount(3);

        // Кнопки действия: сохранить, полить, удобрить
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button saveButton = createActionButton("Сохранить", "black");
        saveButton.setOnAction(e -> {
            plant.setNotes(notesArea.getText());
            plantDao.updateNotes(plant.getId(), plant.getNotes());
            mainView.updatePlantCard(plant);
            detailStage.close();
        });

        Button waterButton = createActionButton("Полить сегодня", "#3498db");
        waterButton.setOnAction(e -> {
            updateCareDate(plant, true);
            detailStage.close();
        });

        Button fertilizeButton = createActionButton("Удобрить сегодня", "#2ecc71");
        fertilizeButton.setOnAction(e -> {
            updateCareDate(plant, false);
            detailStage.close();
        });

        // Добавление кнопок в панель
        buttonBox.getChildren().addAll(saveButton, waterButton, fertilizeButton);
        
        // Небольшая серая кнопка удаления
        Button deleteButton = new Button("Удалить растение");
        deleteButton.setFont(Font.font("Arial", 10));
        deleteButton.setTextFill(Color.GRAY);
        deleteButton.setStyle("-fx-background-color: transparent;");
        deleteButton.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Удаление растения");
            confirm.setHeaderText("Вы уверены, что хотите удалить \"" + plant.getName() + "\"?");
            confirm.setContentText("Это действие необратимо.");

            if (confirm.showAndWait().get() == ButtonType.OK) {
                plantDao.deletePlant(plant.getId());
                mainView.refreshPlants();
                detailStage.close();
            }
        });
        
        // Устанавливаем базовый стиль один раз
        deleteButton.setStyle("-fx-background-color: transparent;");

        // Наведение: добавляем только подчёркивание
        deleteButton.setOnMouseEntered(e ->
            deleteButton.setStyle("-fx-background-color: transparent; -fx-underline: true;")
        );

        // Уход мыши: убираем подчёркивание
        deleteButton.setOnMouseExited(e ->
            deleteButton.setStyle("-fx-background-color: transparent; -fx-underline: false;")
        );

        
        // Оборачиваем кнопку в контейнер для центрирования
        HBox deleteButtonContainer = new HBox(deleteButton);
        deleteButtonContainer.setAlignment(Pos.CENTER);

        
        // Добавляем кнопки в панель заметок
        notesPanel.getChildren().addAll(notesTitle, notesArea, buttonBox, deleteButtonContainer);

        // Сборка всех панелей в основном контейнере
        mainContainer.getChildren().addAll(
            nameLabel, imageContainer, carePanel, notesPanel
        );

        // Установка сцены и отображение окна
        Scene scene = new Scene(mainContainer, 500, 700);
        detailStage.setScene(scene);
        detailStage.show();
    }

    // Метод для обновления даты ухода (полив или удобрение)
    private void updateCareDate(Plant plant, boolean isWatering) {
        LocalDate today = LocalDate.now();
        String plantName = plant.getName();
        CareLogEntry newEntry;

        if (isWatering) {
            plant.water();
            plantDao.updatePlantWateringDate(plant.getId(), today);
            newEntry = new CareLogEntry(plant.getId(), today, "WATER", plantName);
        } else {
            plant.fertilize();
            plantDao.updatePlantFertilizingDate(plant.getId(), today);
            newEntry = new CareLogEntry(plant.getId(), today, "FERTILIZE", plantName);
        }

        careLogDao.addLogEntry(newEntry);
        mainView.updatePlantCard(plant);
    }

    // Создание метки с информацией
    private Label createInfoLabel(String icon, String text) {
        Label label = new Label(icon + " " + text);
        label.setFont(Font.font("Arial", 14));
        return label;
    }

    // Создание строки с последней датой ухода
    private HBox createStatusBox(String title, LocalDate date, String color) {
        HBox box = new HBox(5);
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        titleLabel.setTextFill(Color.web(color));
        
        Label dateLabel = new Label(date.toString());
        dateLabel.setFont(Font.font("Arial", 12));
        
        box.getChildren().addAll(titleLabel, dateLabel);
        return box;
    }

    // Создание прогресс-бара ухода
    private ProgressBar createProgressBar(LocalDate lastDate, int interval, String color) {
        long daysPassed = ChronoUnit.DAYS.between(lastDate, LocalDate.now());
        double progress = Math.min(1.0, (double) daysPassed / interval);
        
        ProgressBar progressBar = new ProgressBar(progress);
        progressBar.setPrefWidth(250);
        progressBar.setStyle("-fx-accent: " + color + "; -fx-control-inner-background: #f1f1f1;");
        
        return progressBar;
    }

    // Создание кнопки действия
    private Button createActionButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; " +
                       "-fx-background-radius: 20; -fx-padding: 8 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");
        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() + "-fx-cursor: hand;"));
        return button;
    }
}
