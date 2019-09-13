package View;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MainGUIController {
    @FXML
    ListView<String> listview1;
    @FXML
    ListView<String> listview2;
    @FXML
    Label place1;
    @FXML
    Label place2;
    @FXML
    Button quitButton;
    @FXML
    Button optButton;

    AtomicInteger taskExecution = new AtomicInteger(0);

    @FXML
    public void initialize() {
        Random rand = new Random();
        ObservableList<String> problems = FXCollections.observableArrayList("Programs slowing computer startup",
                "Invalid registry entries", rand.nextInt(11) + " trojan viruses found " +
                        "on computer", rand.nextInt(11) + "spyware found on computer", "Hard Disk fragmented slowing down computer");
        listview1.setItems(problems);
        place1.setText("Threat Level: " + (30 + rand.nextInt(70) + "%"));
        place2.setText("Optimization: Recommended");

        listview1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!listview2.getItems().contains(listview1.getSelectionModel().getSelectedItem()))
                listview2.getItems().add(listview1.getSelectionModel().getSelectedItem());
        });
    }

    @FXML
    public void quitHandler() {
        Stage stage = (Stage) quitButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void optimizeHandler() {
        Stage stage = (Stage) quitButton.getScene().getWindow();
        Label processResult = new Label();
        Alert alert = new Alert(
                Alert.AlertType.INFORMATION,
                "Operation in progress",
                ButtonType.CANCEL
        );
        alert.setTitle("Running Operation");
        alert.setHeaderText("Please wait... ");
        ProgressIndicator progressIndicator = new ProgressIndicator();
        alert.setGraphic(progressIndicator);
        Task<Void> task = new Task<Void>() {
            final int N_ITERATIONS = 50;
            {
                setOnFailed(a -> {
                    alert.close();
                    updateMessage("Failed");
                });
                setOnSucceeded(a -> {
                    alert.close();
                    updateMessage("Succeeded");
                });
                setOnCancelled(a -> {
                    alert.close();
                    updateMessage("Cancelled");
                });
            }
            @Override
            protected Void call() throws Exception {
                updateMessage("Processing");
                int i;
                for (i = 0; i < N_ITERATIONS; i++) {
                    if (isCancelled()) {
                        break;
                    }
                    updateProgress(i, N_ITERATIONS);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.interrupted();
                    }
                }
                if (!isCancelled()) {
                    updateProgress(i, N_ITERATIONS);
                }
                return null;
            }
        };

        progressIndicator.progressProperty().bind(task.progressProperty());
        processResult.textProperty().unbind();
        processResult.textProperty().bind(task.messageProperty());

        Thread taskThread = new Thread(
                task,
                "task-thread-" + taskExecution.getAndIncrement()
        );
        taskThread.start();

        alert.initOwner(stage);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.CANCEL && task.isRunning()) {
            task.cancel();
        }
        listview1.setItems(FXCollections.observableArrayList());
        listview2.setItems(FXCollections.observableArrayList());
        place1.setText("Threat Level: 0%");
        place2.setText("Optimization: Done");
    }
}
