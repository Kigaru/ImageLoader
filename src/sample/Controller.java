package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;



public class Controller {

    @FXML
    private ImageView image;

    @FXML
    private CheckMenuItem redCheck, greenCheck, blueCheck, monoCheck;

    private Image graphics;
    private PixelReader graphicsPixelReader;
    private File file; //to get size later
    private boolean isMonochrome = false, isRed = true, isGreen = true, isBlue = true;


    public void openImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an image...");
        file = fileChooser.showOpenDialog(image.getScene().getWindow());

        if(file != null) {

            isMonochrome=false;
            isRed=true;
            isGreen=true;
            isBlue=true;
            fixCheckBoxes();

            graphics = new Image(file.toURI().toString());
            System.out.println(file.toURI().toString());
            graphicsPixelReader = graphics.getPixelReader();
            image.setImage(graphics);
        }
    }

    public void toggleMonochrome() {
        long before = System.nanoTime();
        if(graphics!=null) {
            if (!isMonochrome) {                
                isMonochrome = true;
                isRed = false;
                isBlue = false;
                isGreen = false;
                fixCheckBoxes();
                image.setImage(getModifiedImage(isRed,isGreen,isBlue,isMonochrome));
            } else {
                isMonochrome = false;
                isRed = true;
                isBlue = true;
                isGreen = true;
                fixCheckBoxes();
                image.setImage(getModifiedImage(isRed,isGreen,isBlue,isMonochrome));
            }
        }
        long after = System.nanoTime();
        System.out.println("The time it took to toggle monochrome = " + (after - before) + " nanoseconds");
    }


    public void toggleColor() {
        long before = System.nanoTime();
        isMonochrome = false;
        isRed = redCheck.isSelected();
        isGreen = greenCheck.isSelected();
        isBlue = blueCheck.isSelected();
        fixCheckBoxes();
        image.setImage(getModifiedImage(isRed,isGreen,isBlue,isMonochrome));
        long after = System.nanoTime();
        System.out.println("The time it took to toggle colors = " + (after - before) + " nanoseconds");
    }

    private Image getModifiedImage(boolean red, boolean green, boolean blue, boolean mono) {
        if(graphics==null) return new WritableImage(1,1);
        else {
            WritableImage newImage = new WritableImage(graphicsPixelReader, (int) graphics.getWidth(), (int) graphics.getHeight());
            PixelWriter newImagePixelWriter = newImage.getPixelWriter();

            double dred, dgreen, dblue, dmono;

            if(!mono) {
                for (int y = 0; y < graphics.getWidth(); y++) {
                    for (int x = 0; x < graphics.getHeight(); x++) {

                        if (red) dred = graphicsPixelReader.getColor(y, x).getRed();
                        else dred = 0;

                        if (green) dgreen = graphicsPixelReader.getColor(y, x).getGreen();
                        else dgreen = 0;

                        if (blue) dblue = graphicsPixelReader.getColor(y, x).getBlue();
                        else dblue = 0;

                        Color color = new Color(dred, dgreen, dblue, graphicsPixelReader.getColor(y, x).getOpacity());
                        newImagePixelWriter.setColor(y, x, color);
                    }
                }
            }
            else {
                for (int y = 0; y < graphics.getWidth(); y++) {
                    for (int x = 0; x < graphics.getHeight(); x++) {
                        Color color = graphicsPixelReader.getColor(y, x);
                        dmono = (color.getRed()+color.getGreen()+color.getBlue())/3;
                        Color grayscale = new Color(dmono,dmono,dmono,color.getOpacity());
                        newImagePixelWriter.setColor(y, x, grayscale);
                    }
                }
                
            }
            return newImage;
        }
    }
    
    public void showAllChannels() {
        long timeStart = System.currentTimeMillis();
        if (graphics!=null) {
            Stage channels = new Stage();
            HBox hbox = new HBox();   
            ImageView red = new ImageView(getModifiedImage(true,false,false,false));
            ImageView green = new ImageView(getModifiedImage(false,true,false,false));
            ImageView blue = new ImageView(getModifiedImage(false,false,true,false));


            //screen-size Screen.getPrimary().getBounds().getMaxX();
            double x = graphics.getWidth();
            double y = graphics.getHeight();
            double width, height;

            //if(graphics.getWidth()*3  >Screen.getPrimary().getBounds().getMaxX()) {
            red.setFitWidth((int) (Screen.getPrimary().getBounds().getMaxX()*0.9/3));
            red.setFitHeight((int) (red.getFitWidth()*(y/x)));

            green.setFitWidth((int)(Screen.getPrimary().getBounds().getMaxX()*0.9/3));
            green.setFitHeight((int)(green.getFitWidth()*(y/x)));

            blue.setFitWidth((int)(Screen.getPrimary().getBounds().getMaxX()*0.9/3));
            blue.setFitHeight((int)(blue.getFitWidth()*(y/x)));

            width = red.getFitWidth()*3;
            height = red.getFitHeight();


//            else {
//                red.setFitHeight((int) (Screen.getPrimary().getBounds().getMaxY()*0.8));
//                red.setFitWidth((int) (red.getFitHeight()*(x/y)));
//
//                green.setFitHeight((int) (Screen.getPrimary().getBounds().getMaxY()*0.8));
//                green.setFitWidth((int) (green.getFitHeight()*(x/y)));;
//
//                blue.setFitHeight((int) (Screen.getPrimary().getBounds().getMaxY()*0.8));
//                blue.setFitWidth((int) (blue.getFitHeight()*(x/y)));
//
//                width = red.getFitWidth()*3;
//                height = red.getFitHeight();
//            }



            hbox.getChildren().addAll(red,green,blue);
            channels.setScene(new Scene(hbox, width, height));
            channels.setResizable(false);
            long a = System.currentTimeMillis();
            channels.show();
            System.out.println("aaaaaaa: " + ((System.currentTimeMillis() - a)/1000.0) + " bbb");
        }
        System.out.println("It took: " + ((System.currentTimeMillis() - timeStart)/1000.0) + " seconds to process the image");
    }


    public void test() {

        showAllChannels();
    }

    public void fileInfo() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("info.fxml"));
        Parent root = loader.load();
        InfoController infoCtrl = loader.getController();

        if (graphics!=null) {
            infoCtrl.setFileName("File Name: " + file.getName());
            infoCtrl.setFileSize("File Size: " + file.length()+ " bytes");
            infoCtrl.setResolution("Resolution: " + (int) graphics.getWidth()+"x"+(int) graphics.getHeight()+" pixels");
        }

        stage.setTitle("File Info...");
        stage.setScene(new Scene(root, 300, 200));
        stage.setResizable(false);
        stage.show();
    }

    private void fixCheckBoxes() {
        redCheck.setSelected(isRed);
        greenCheck.setSelected(isGreen);
        blueCheck.setSelected(isBlue);
        monoCheck.setSelected(isMonochrome);
    }

    public void close() {
        System.exit(0);
    }
}
