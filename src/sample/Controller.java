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
            graphicsPixelReader = graphics.getPixelReader();
            image.setImage(graphics);
        }
    }

    public void toggleMonochrome() {
        if(graphics!=null) {
            if (!isMonochrome) {                
                isMonochrome = true;
                isRed = false;
                isBlue = false;
                isGreen = false;
                fixCheckBoxes();
                image.setImage(getModifiedImage(isRed,isGreen,isBlue,isMonochrome,false));
            } else {
                isMonochrome = false;
                isRed = true;
                isBlue = true;
                isGreen = true;
                fixCheckBoxes();
                image.setImage(getModifiedImage(isRed,isGreen,isBlue,isMonochrome,false));
            }
        }
    }


    public void toggleColor() {
        isMonochrome = false;
        isRed = redCheck.isSelected();
        isGreen = greenCheck.isSelected();
        isBlue = blueCheck.isSelected();
        fixCheckBoxes();
        image.setImage(getModifiedImage(isRed,isGreen,isBlue,isMonochrome,false));
    }

    private Image getModifiedImage(boolean red, boolean green, boolean blue, boolean mono, boolean explicitMono) {
        if(graphics==null) return new WritableImage(1,1);
        else {
            WritableImage newImage = new WritableImage(graphicsPixelReader, (int) graphics.getWidth(), (int) graphics.getHeight());
            PixelWriter newImagePixelWriter = newImage.getPixelWriter();

            double dred, dgreen, dblue, dmono;

            if(!mono) {
                for (int x = 0; x < graphics.getWidth(); x++) {
                    for (int y = 0; y < graphics.getHeight(); y++) {

                        if (red) dred = graphicsPixelReader.getColor(x, y).getRed();
                        else dred = 0;

                        if (green) dgreen = graphicsPixelReader.getColor(x, y).getGreen();
                        else dgreen = 0;

                        if (blue) dblue = graphicsPixelReader.getColor(x, y).getBlue();
                        else dblue = 0;

                        Color color = new Color(dred, dgreen, dblue, graphicsPixelReader.getColor(x, y).getOpacity());
                        newImagePixelWriter.setColor(x, y, color);
                    }
                }
            }
            else {
                for (int x = 0; x < graphics.getWidth(); x++) {
                    for (int y = 0; y < graphics.getHeight(); y++) {
                        Color color = graphicsPixelReader.getColor(x, y);
                        dmono = (color.getRed()+color.getGreen()+color.getBlue())/3;

                        if(explicitMono) dmono = dmono > 0.5 ? 1 : 0;

                        Color grayscale = new Color(dmono,dmono,dmono,color.getOpacity());
                        newImagePixelWriter.setColor(x, y, grayscale);
                    }
                }
            }



            return newImage;
        }
    }

    public void showAllChannels() {
        if (graphics!=null) {
            Stage channels = new Stage();
            HBox hbox = new HBox();   
            ImageView red = new ImageView(getModifiedImage(true,false,false,false,false));
            ImageView green = new ImageView(getModifiedImage(false,true,false,false,false));
            ImageView blue = new ImageView(getModifiedImage(false,false,true,false,false));


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
            channels.show();
        }
    }


    public void test() {
        if (graphics != null) {

            //STEP 1: initialize
            PixelCollection pixelCollection = new PixelCollection((int) graphics.getWidth(), (int) graphics.getHeight());

            //STEP 2: populate
            for (int i = 0; i < pixelCollection.getPixels().length; i++) {
                pixelCollection.setPixel(i, i);
            }

            //STEP 3: saturate image accordingly
            WritableImage saturatedImage = (WritableImage) getModifiedImage(false, false, false, true, true);
            image.setImage(saturatedImage);



            int totalPixels = pixelCollection.getPixels().length;

            //STEP 4: set all white pixels to -1
            for (int j = 0; j < graphics.getHeight(); j++) {
                for (int i = 0; i < graphics.getWidth(); i++) {
                    if (saturatedImage.getPixelReader().getColor(i, j).equals(Color.WHITE)) {
                        pixelCollection.setPixel(i + j * (int) graphics.getWidth(), -1);
                        totalPixels--;
                    }
                }
            }

            //STEP 5: union all black pixels if necessary
            for (int j = 0; j < graphics.getHeight(); j++) {
                for (int i = 0; i < graphics.getWidth(); i++) {
                    if (pixelCollection.getPixels()[i + j * (int) graphics.getWidth()] != -1) {
                        if ((i + 1 < graphics.getWidth()) && (j + 1 < graphics.getHeight())) {
                            //to the right
                            if (saturatedImage.getPixelReader().getColor(i + 1, j).equals(Color.BLACK)) {
                                DisjointSet.union(pixelCollection.getPixels(), i + j * (int) graphics.getWidth(), i + 1 + j * (int) graphics.getWidth());
                            }
                            //directly under
                            if (saturatedImage.getPixelReader().getColor(i, j + 1).equals(Color.BLACK)) {
                                DisjointSet.union(pixelCollection.getPixels(), i + j * (int) graphics.getWidth(), i + j * (int) graphics.getWidth() + (int) graphics.getWidth());
                            }
                            //under right
                            if (saturatedImage.getPixelReader().getColor(i +1, j + 1).equals(Color.BLACK)) {
                                DisjointSet.union(pixelCollection.getPixels(), i + j * (int) graphics.getWidth(), i +1 + j * (int) graphics.getWidth() + (int) graphics.getWidth());
                            }
                        }
                        if(i-1 >= 0 && (j + 1 < graphics.getHeight())) {

                            //under left
                            if (saturatedImage.getPixelReader().getColor(-1 +i, j +1).equals(Color.BLACK)) {
                                DisjointSet.union(pixelCollection.getPixels(), i + j * (int) graphics.getWidth(), i - 1 + j * (int) graphics.getWidth() + (int) graphics.getWidth());
                            }
                        }
                    }
                }
            }

            //STEP 6: noise reduction
            //??

            //STEP 7: count all roots
            int[] roots = new int[1000];
            for (int i = 0; i < roots.length; i++) {
                roots[i] = -213;
            }
            int counter = 0;
            for (int j = 0; j < pixelCollection.getPixels().length; j++) { //going thru all pixels
                if(pixelCollection.getPixels()[j] >= 0) { //if black pixel
                    boolean isNewRoot = true;
                    for (int i = 0; i < roots.length ; i++) {
                        if(roots[i] == DisjointSet.find(pixelCollection.getPixels(),j)) isNewRoot = false;

                    }
                    if(isNewRoot) {
                        roots[counter] = DisjointSet.find(pixelCollection.getPixels(),j);
                        //System.out.println("x: "+ j%graphics.getWidth() + ", y: " + (j-j%graphics.getWidth())/graphics.getWidth());
                        //System.out.println(j);
                        counter++;
                    }
                }
            }



            //STEP 8: draw boxes

            System.out.println("there are " + totalPixels + " black pixels");
            System.out.println("there are " + counter+ " birds");

//            //STEP 4: verify (again)
//            //there should be 788 white pixels in text image
//            int whitePixel = 0;
//            for (int j = 0; j < graphics.getHeight(); j++) {
//                for (int i = 0; i < graphics.getWidth(); i++) {
//                    if (pixelCollection.getPixels()[i + j*(int)graphics.getWidth()] == -1) {
//                        whitePixel++;
//                    }
//                }
//            }
        }
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
