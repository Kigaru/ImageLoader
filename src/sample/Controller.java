package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.layout.*;
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

    @FXML
    private Pane imagePane;

    private Image graphics;
    private WritableImage imageWithBoxes;
    private PixelReader graphicsPixelReader;
    private File file; //to get size later
    private boolean isMonochrome = false, isRed = true, isGreen = true, isBlue = true;

    private StackPane birdInts;

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
            long bench = System.nanoTime();


            //for showing it at the end :)
            imageWithBoxes = new WritableImage(graphics.getPixelReader(),(int)graphics.getWidth(),(int)graphics.getHeight());
            birdInts = new StackPane();

            //STEP 1: initialize
            PixelCollection pixelCollection = new PixelCollection((int) graphics.getWidth(), (int) graphics.getHeight());

            System.out.println("Initialization: " + (System.nanoTime() - bench));

            //STEP 2: populate
            for (int i = 0; i < pixelCollection.getPixels().length; i++) {
                pixelCollection.setPixel(i, i);
            }

            System.out.println("Population: " + (System.nanoTime() - bench));

            //STEP 3: saturate image accordingly
            WritableImage saturatedImage = (WritableImage) getModifiedImage(false, false, false, true, true);
            image.setImage(saturatedImage);


            System.out.println("Saturation: " + (System.nanoTime() - bench));

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

            System.out.println("Deleting whites: " + (System.nanoTime() - bench));

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

            System.out.println("Creating roots: " + (System.nanoTime() - bench));

            //STEP 6: noise reduction
            //??

            //STEP 7: count all roots
            LinkedList<Integer> roots = new LinkedList<>();

            for (int pixel = 0; pixel < pixelCollection.getPixels().length; pixel++) { //going thru all pixels
                if(pixelCollection.getPixels()[pixel] >= 0) { //if black pixel
                    boolean isNewRoot = true;
                    for (int i = 0; i < roots.size() ; i++) {
                        if(roots.get(i) == DisjointSet.find(pixelCollection.getPixels(),pixel)) isNewRoot = false;

                    }
                    if(isNewRoot) {
                        roots.insertLastElement(DisjointSet.find(pixelCollection.getPixels(),pixel));
                        //System.out.println("x: "+ roots.getLast()%graphics.getWidth() + ", y: " + (roots.getLast()-roots.getLast()%graphics.getWidth())/graphics.getWidth());
                        //System.out.println(roots.getLast());
                    }
                }
            }

            System.out.println("counting roots: " + (System.nanoTime() - bench));

            //STEP 8: draw boxes
            for(int root = 0; root < roots.size(); root++) {
                int minX, minY, maxX, maxY;
                minX = (int)(roots.get(root)%graphics.getWidth());
                maxX = (int)(roots.get(root)%graphics.getWidth());
                minY = (int)((roots.get(root)-roots.getLast()%graphics.getWidth())/graphics.getWidth());
                maxY = (int)((roots.get(root)-roots.getLast()%graphics.getWidth())/graphics.getWidth());
                for(int pixel = 0; pixel < pixelCollection.getPixels().length; pixel++) {
                    if(pixelCollection.getPixels()[pixel] != -1 && DisjointSet.find(pixelCollection.getPixels(),pixel) == roots.get(root) ) {
                        minX =  pixel%graphics.getWidth() < minX  ? (int)(pixel%graphics.getWidth()) : minX;
                        maxX =  pixel%graphics.getWidth() > maxX  ? (int)(pixel%graphics.getWidth()) : maxX;
                        minY =  ((pixel-pixel%graphics.getWidth())/graphics.getWidth()) < minY  ? (int)((pixel-pixel%graphics.getWidth())/graphics.getWidth()) : minY;
                        maxY =  ((pixel-pixel%graphics.getWidth())/graphics.getWidth()) > maxY  ? (int)((pixel-pixel%graphics.getWidth())/graphics.getWidth()) : maxY;
                    }
                }
                drawBox(minX,minY,maxX,maxY, root + 1);
//                System.out.println("Root #" + (root+1)+", DRAW BOXES AT: ");
//                System.out.println("X: " + minX + ", Y: " + minY);
//                System.out.println("X: " + maxX + ", Y: " + maxY);

            }


            System.out.println("drawing boxes: " + (System.nanoTime() - bench));

            //System.out.println("there are " + totalPixels + " black pixels");
            //System.out.println("there are " + roots.size() + " birds");
            image.setImage(imageWithBoxes);

//            //STEP 4: verify (again)
//            //there should be 788 white pixels in text image
//            int whitePixel = 0;
//            for (int j = 0; j < graphics.getHeight(); j++) {
//                for (int i = 0; i < graphics.getWidth(); i++) {if
//                    if (pixelCollection.getPixels()[i + j*(int)graphics.getWidth()] == -1) {
//                        whitePixel++;
//                    }
//                }
//            }
        }
    }

    private void drawBox(int minX, int minY, int maxX, int maxY, int number){


        PixelWriter imageWithBoxesPW = imageWithBoxes.getPixelWriter();

        Label bird = new Label();
        bird.setText(Integer.toString(number));
        bird.setAlignment(Pos.TOP_RIGHT);
        bird.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        bird.setTextFill(Color.YELLOW);


        //System.out.println("GET FIT WIDTH: " + image.maxWidth(640) + ", GET IMAGE WIDTH: " + graphics.getWidth());
        //System.out.println("GET FIT HEIGHT: " + image.getFitHeight() + ", GET IMAGE HEIGHT: " + graphics.getHeight());

        bird.setLayoutX(minX * (image.maxWidth(image.getFitWidth())/graphics.getWidth()));
        bird.setLayoutY(minY * (image.maxHeight(image.getFitHeight())/graphics.getHeight()));
        imagePane.getChildren().add(bird);



        for (int x = minX; x < maxX; x++) {
            imageWithBoxesPW.setColor(x, minY, Color.RED);
            imageWithBoxesPW.setColor(x, maxY, Color.RED);
        }

        for (int y = minY; y < maxY; y++) {
            imageWithBoxesPW.setColor(minX, y, Color.RED);
            imageWithBoxesPW.setColor(maxX, y, Color.RED);
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
