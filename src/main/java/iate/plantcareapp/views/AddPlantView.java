package iate.plantcareapp.views;

import iate.plantcareapp.dao.PlantDao;
import iate.plantcareapp.models.Plant;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class AddPlantView {

    // DAO для взаимодействия с базой данных
    private final PlantDao plantDao = new PlantDao();

    // Метод отображения формы добавления нового растения
    public void showAddPlantForm(Runnable onPlantAdded) {
        Stage stage = new Stage();
        stage.setTitle("Добавить растение");

        // Создание сетки для размещения компонентов
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(15);
        grid.setVgap(15);

        // Настройка ширины колонок
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(30);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(20);
        grid.getColumnConstraints().addAll(col1, col2, col3);

        // Поля ввода
        TextField nameField = new TextField(); // Название растения
        TextArea notesArea = new TextArea();   // Заметки
        notesArea.setPrefRowCount(4);
        TextField wateringField = new TextField();     // Интервал полива
        TextField fertilizingField = new TextField();  // Интервал удобрения
        TextField imagePathField = new TextField();    // Путь к изображению
        imagePathField.setEditable(false);             // Запрет редактирования поля вручную

        // Кнопка для выбора изображения
        Button browseButton = new Button("📷");
        browseButton.setTooltip(new Tooltip("Выбрать изображение"));

        // Обработчик выбора изображения
        /*
        browseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите изображение");

            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                try {
                    String imageName = selectedFile.getName();

                    // Путь до папки изображений проекта
                    File projectRoot = new File(System.getProperty("user.dir"));
                    File imagesDir = new File(projectRoot, "src/main/resources/images");
                    if (!imagesDir.exists()) {
                        imagesDir.mkdirs(); // Создание папки, если её нет
                    }

                    File destFile = new File(imagesDir, imageName);

                    // Копирование изображения, если его ещё нет
                    if (!destFile.exists()) {
                        java.nio.file.Files.copy(
                                selectedFile.toPath(),
                                destFile.toPath(),
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING
                        );
                    }

                    // Установка относительного пути
                    String relativePath = "images\\" + imageName;
                    imagePathField.setText(relativePath);

                } catch (Exception ex) {
                    showAlert("Ошибка при копировании изображения: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        */
        
    browseButton.setOnAction(e -> {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите изображение");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Изображения", "*.jpg", "*.jpeg", "*.png")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                // Определяем путь к папке resources в исходниках
                File resourcesDir = new File("src/main/resources/images");
                if (!resourcesDir.exists()) {
                    resourcesDir.mkdirs();
                }

                // Копируем файл в src/main/resources/images/
                String imageName = selectedFile.getName();
                File destFile = new File(resourcesDir, imageName);
                Files.copy(selectedFile.toPath(), destFile.toPath(), 
                          StandardCopyOption.REPLACE_EXISTING);

                // Сохраняем путь в формате images/имя_файла
                imagePathField.setText("images/" + imageName);

                // System.out.println("Изображение скопировано в: " + destFile.getAbsolutePath());

            } catch (Exception ex) {
                showAlert("Error during copying image: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    });
        
        // Кнопки сохранения и отмены
        Button saveButton = new Button("💾 Сохранить");
        Button cancelButton = new Button("❌ Отмена");

        // Стилизация кнопок
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        // Обработчик сохранения данных
        saveButton.setOnAction(e -> {
            try {
                // Считывание данных из формы
                String name = nameField.getText();
                String notes = notesArea.getText();
                int wateringDays = Integer.parseInt(wateringField.getText());
                int fertilizingDays = Integer.parseInt(fertilizingField.getText());
                String imagePath = imagePathField.getText();

                // Проверка на пустое имя
                if (name.isBlank()) {
                    showAlert("Имя не может быть пустым.");
                    return;
                }

                // Создание и сохранение объекта растения
                Plant newPlant = new Plant(
                        0, name, notes, imagePath,
                        wateringDays, fertilizingDays
                );

                plantDao.addPlant(newPlant); // Добавление в БД

                if (onPlantAdded != null) onPlantAdded.run(); // Обновление UI
                stage.close(); // Закрытие окна

            } catch (NumberFormatException ex) {
                // Обработка ошибки при неверном вводе чисел
                showAlert("Периоды полива и удобрения должны быть целыми числами.");
            }
        });

        // Обработчик кнопки отмены
        cancelButton.setOnAction(e -> stage.close());

        // Размещение компонентов в сетке
        grid.add(new Label("Название:"), 0, 0);
        grid.add(nameField, 1, 0, 2, 1);

        grid.add(new Label("Заметки:"), 0, 1);
        grid.add(notesArea, 1, 1, 2, 1);

        grid.add(new Label("Интервал полива (дней):"), 0, 2);
        grid.add(wateringField, 1, 2);

        grid.add(new Label("Интервал удобрения (дней):"), 0, 3);
        grid.add(fertilizingField, 1, 3);

        grid.add(new Label("Изображение:"), 0, 4);
        grid.add(imagePathField, 1, 4);
        grid.add(browseButton, 2, 4);

        // Контейнер для кнопок
        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        grid.add(buttonBox, 1, 5, 2, 1);

        // Оборачивание в BorderPane с фоном
        BorderPane root = new BorderPane();
        root.setCenter(grid);
        root.setStyle("-fx-background-color: #f0f8ff;");

        // Создание и отображение сцены
        Scene scene = new Scene(root, 640, 400);
        stage.setScene(scene);
        stage.show();
    }

    // Вспомогательный метод для отображения ошибок
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
