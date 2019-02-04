package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class InfoController {
    @FXML
    private Text fileName, fileSize, resolution;

    public void setFileName(String fileName) {
        this.fileName.setText(fileName);
    }

    public void setFileSize(String fileSize) {
        this.fileSize.setText(fileSize);
    }

    public void setResolution(String resolution) {
        this.resolution.setText(resolution);
    }

    public void clickOk() {
        Stage stage = (Stage) fileName.getScene().getWindow();
        stage.close();
    }

}
