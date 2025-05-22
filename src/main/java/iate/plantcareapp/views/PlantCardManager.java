package iate.plantcareapp.views;

import iate.plantcareapp.dao.PlantDao;
import iate.plantcareapp.models.Plant;
import java.io.File;
import java.io.FileInputStream;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

public class PlantCardManager {

    private final GridPane grid;
    private final MainView mainView;
    private final PlantDao plantDao = new PlantDao();

    public PlantCardManager(GridPane grid, MainView mainView) {
        this.grid = grid;
        this.mainView = mainView;
    }
    
    // Обновлем все карточки
    public void refreshPlants() {
        grid.getChildren().clear();
        List<Plant> plants = plantDao.getAllPlants();
        plants.sort(Comparator.comparing(Plant::getName));

        int col = 0, row = 0;
        for (Plant plant : plants) {
            VBox card = createPlantCard(plant);
            grid.add(card, col, row);
            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }
    
    // Обновляем карточку
    public void updatePlantCard(Plant plant) {
        for (Node node : grid.getChildren()) {
            if (node instanceof VBox card) {
                Label nameLabel = (Label) card.getChildren().get(1);
                if (nameLabel.getText().equals(plant.getName())) {
                    Label wateringLabel = (Label) card.getChildren().get(2);
                    long daysToWater = ChronoUnit.DAYS.between(LocalDate.now(), plant.getLastWatered().plusDays(plant.getWateringIntervalDays()));
                    wateringLabel.setText((daysToWater <= 0) ? "❗ Полейте сейчас" : "💧 Полейте через: " + daysToWater + " дней(-я)");
                    wateringLabel.setTextFill(daysToWater <= 0 ? Color.RED : Color.BLUE);

                    Label fertilizingLabel = (Label) card.getChildren().get(3);
                    long daysToFertilize = ChronoUnit.DAYS.between(LocalDate.now(), plant.getLastFertilized().plusDays(plant.getFertilizingIntervalDays()));
                    fertilizingLabel.setText((daysToFertilize <= 0) ? "❗ Удобрите сейчас" : "🌿 Удобрите через: " + daysToFertilize + " дней(-я)");
                    fertilizingLabel.setTextFill(daysToFertilize <= 0 ? Color.RED : Color.GREEN);

                    break;
                }
            }
        }
    }
    
    // Создаём карточки
    private VBox createPlantCard(Plant plant) {
        VBox box = new VBox();
        box.setSpacing(5);
        box.setPadding(new Insets(10));
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(200, 200);

        String normalStyle = "-fx-border-color: lightgray; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: white;";
        String hoverStyle = "-fx-border-color: #4CAF50; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: #f9fff9; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);";
        box.setStyle(normalStyle);

        box.setOnMouseEntered(e -> box.setStyle(hoverStyle));
        box.setOnMouseExited(e -> box.setStyle(normalStyle));
        box.setOnMouseClicked((MouseEvent e) -> new PlantsDetailView(mainView).showDetails(plant));
        
        /*
        try {
            // Используем getResource для загрузки из JAR или файловой системы
            String imagePath = "/" + plant.getImagePath().replace("\\", "/"); // Добавляем / в начало
            System.out.println("Trying to load image at path: " + imagePath);
            InputStream inputStream = getClass().getResourceAsStream(imagePath);

            if (inputStream != null) {
                Image image = new Image(inputStream, 100, 100, true, true);
                ImageView imageView = new ImageView(image);
                box.getChildren().add(imageView);
            } else {
                box.getChildren().add(new Label("[No image found]"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            box.getChildren().add(new Label("[Error loading image]"));
        }
        */
        
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
                // Если изображение найдено, загружаем его
                InputStream inputStream = new FileInputStream(imageFile);
                Image image = new Image(inputStream, 100, 100, true, true);
                ImageView imageView = new ImageView(image);
                box.getChildren().add(imageView);
                System.out.println("Loaded image from file system: " + imageFile.getAbsolutePath());
            } else {
                // Если изображение не найдено, выводим сообщение
                System.out.println("Image not found in file system: " + imageFile.getAbsolutePath());
                box.getChildren().add(new Label("[No image found]"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            box.getChildren().add(new Label("[Error loading image]"));
        }

        Label nameLabel = new Label(plant.getName());
        nameLabel.setFont(new Font(16));
        box.getChildren().add(nameLabel);

        long daysToWater = ChronoUnit.DAYS.between(LocalDate.now(), plant.getLastWatered().plusDays(plant.getWateringIntervalDays()));
        Label wateringLabel = new Label((daysToWater <= 0) ? "❗ Полейте сейчас" : "💧 Полейте через: " + daysToWater + " дней(-я)");
        wateringLabel.setFont(new Font(12));
        wateringLabel.setTextFill(daysToWater <= 0 ? Color.RED : Color.BLUE);
        box.getChildren().add(wateringLabel);

        long daysToFertilize = ChronoUnit.DAYS.between(LocalDate.now(), plant.getLastFertilized().plusDays(plant.getFertilizingIntervalDays()));
        Label fertilizingLabel = new Label((daysToFertilize <= 0) ? "❗ Удобрите сейчас" : "🌿 Удобрите через: " + daysToFertilize + " дней(-я)");
        fertilizingLabel.setFont(new Font(12));
        fertilizingLabel.setTextFill(daysToFertilize <= 0 ? Color.RED : Color.GREEN);
        box.getChildren().add(fertilizingLabel);

        return box;
    }
}
