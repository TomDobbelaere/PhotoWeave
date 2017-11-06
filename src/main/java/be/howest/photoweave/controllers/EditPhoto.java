package be.howest.photoweave.controllers;

import be.howest.photoweave.components.SelectBinding;
import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.imaging.MonochromeImage;
import be.howest.photoweave.model.weaving.WovenImage;
import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class EditPhoto {
    @FXML
    private JFXCheckBox markBindings;
    @FXML
    private SelectBinding selectBinding;
    @FXML
    private ImageView photoview;
    @FXML
    private Slider slider;
    @FXML
    private VBox imagevbox;
    @FXML
    private AnchorPane anchorpane;
    @FXML
    private AnchorPane window;
    @FXML
    private Label fileNameId;
    @FXML
    private Label imageSizeId;
    @FXML
    private TextField widthinputtextfield;
    @FXML
    private TextField heightinputtextfield;
    @FXML
    private Label amountColorsLabel;

    //Image parameters
    private String path;
    private int imageWidth;
    private int imageHeight;
    private String filename;
    private BufferedImage img;
    private BufferedImage originalImg;
    private MonochromeImage monochromeImg;
    private WovenImage wovenImage;
    private Image endImage;
    private Stage stage;

    //Edit parameters
    private int posterizeScale = 10;
    private boolean imgChanged = true;

    public void initData(String path) throws IOException {
        //init parameters
        this.path = path;
        this.img = ImageIO.read(new File(path));
        this.originalImg = img;
        this.monochromeImg = new MonochromeImage(img);
        this.imageWidth = img.getWidth();
        this.imageHeight = img.getHeight();
        this.filename = path.substring(path.lastIndexOf("/") + 1);
        this.posterizeScale = 10;
        this.stage = (Stage) window.getScene().getWindow();

        selectBinding.getComboBox().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Binding>() {
            @Override
            public void changed(ObservableValue<? extends Binding> observable, Binding oldValue, Binding newValue) {
                wovenImage.redraw();
                photoview.setImage(SwingFXUtils.toFXImage(wovenImage.getResultImage(),null));
            }
        });

        selectBinding.getComboBoxColors().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                wovenImage.setMarkedBinding(selectBinding.getComboBoxColors().getSelectionModel().getSelectedItem());
                wovenImage.setShowMarkedBinding(markBindings.isSelected());
                wovenImage.redraw();
                photoview.setImage(SwingFXUtils.toFXImage(wovenImage.getResultImage(),null));
            }
        });

        markBindings.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                wovenImage.setMarkedBinding(selectBinding.getComboBoxColors().getSelectionModel().getSelectedItem());
                wovenImage.setShowMarkedBinding(markBindings.isSelected());
                wovenImage.redraw();
                photoview.setImage(SwingFXUtils.toFXImage(wovenImage.getResultImage(),null));
            }
        });

        //set properties
        updateTexts();

        slider.setOnMouseReleased((MouseEvent event) -> {
            posterizeScale = slider.valueProperty().intValue();
            amountColorsLabel.setText("Amount of colors: " + posterizeScale);
            updateImage();
        });

        widthinputtextfield.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("widthinputtextfield.textProperty()");
            if (!newValue.matches("\\d*")) {
                widthinputtextfield.setText(newValue.replaceAll("\\D", ""));
            } else {
                if (!widthinputtextfield.getText().trim().isEmpty() && Integer.parseInt(newValue) != 0) {
                    imageWidth = Integer.parseInt(newValue);
                }
            }
            resizeImage();
        });
        heightinputtextfield.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("heightinputtextfield.textProperty()");
            if (!newValue.matches("\\d*")) {
                heightinputtextfield.setText(newValue.replaceAll("\\D", ""));
            } else {
                if (!heightinputtextfield.getText().trim().isEmpty() && Integer.parseInt(newValue) != 0) {
                    imageHeight = Integer.parseInt(newValue);
                }
            }
            resizeImage();
        });

        window.heightProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("window.heightProperty()");
            imagevbox.setMinHeight((Double) newVal - 50);
        });
        window.widthProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("window.widthProperty()");
            imagevbox.setMinWidth((Double) newVal - 200);
        });
    }

    public void zoomin() {
        System.out.println("zoomin()");
        photoview.setFitWidth(photoview.getFitWidth() * 1.3);
        photoview.setFitHeight(photoview.getFitHeight() * 1.3);
    }

    public void zoomout() {
        System.out.println("zoomout()");
        photoview.setFitWidth(photoview.getFitWidth() / 1.3);
        photoview.setFitHeight(photoview.getFitHeight() / 1.3);
    }

    public void updateImage() {
        System.out.println("updateImage()");
        if (imgChanged) {
            monochromeImg = new MonochromeImage(img);
            imgChanged = !imgChanged;
        }

        monochromeImg.setLevels(posterizeScale);
        monochromeImg.redraw();
        /* OLD WARD CODE
        endImage = SwingFXUtils.toFXImage(monochromeImg.getModifiedImage(), null);
        photoview.setImage(endImage);
        */

        //NEW TEMP CODE
        wovenImage = new WovenImage(monochromeImg.getModifiedImage());
        wovenImage.redraw();
        photoview.setImage(SwingFXUtils.toFXImage(wovenImage.getResultImage(),null));

        selectBinding.setBindingPalette(wovenImage.getBindingPalette());

        updateTexts();
    }

    private void resizeImage() {
        System.out.println("resizeImage()");
        BufferedImage newImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

        Graphics g = newImage.createGraphics();
        g.drawImage(originalImg, 0, 0, imageWidth, imageHeight, null);
        g.dispose();

        img = newImage;
        imgChanged = true;

        updateImage();
    }

    public void zoomPhoto() {
        System.out.println("zoomPhoto()");
        if (img.getHeight() <= img.getWidth()) {
            photoview.setFitWidth(anchorpane.getWidth());
        } else {
            photoview.setFitHeight(anchorpane.getHeight());
        }
    }

    public void export(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", ".png"),
                new FileChooser.ExtensionFilter("JPG", ".jpg"),
                new FileChooser.ExtensionFilter("JPEG", ".jpeg")
        );
        fileChooser.setTitle("PhotoWeave | Save Image");
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                //Kan hier een confict zijn.
                WovenImage wovenImage = new WovenImage(monochromeImg.getModifiedImage());
                wovenImage.redraw();

                ImageIO.write(wovenImage.getResultImage(), "png", file);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void updateTexts() {
        System.out.println("updateTexts()");
        fileNameId.setText("File: " + filename);
        imageSizeId.setText("Width: " + imageWidth + "px; Height: " + imageHeight + "px;");
        widthinputtextfield.setText(String.valueOf(imageWidth));
        heightinputtextfield.setText(String.valueOf(imageHeight));
        amountColorsLabel.setText("Amount of colors: " + posterizeScale);
    }


    /* TEMP EVENTS
    public void applyBindings(MouseDragEvent mouseDragEvent) {
        System.out.println("Apply BINDING");
        WovenImage wovenImage = new WovenImage(monochromeImg.getModifiedImage());
        wovenImage.redraw();


        photoview.setImage(SwingFXUtils.toFXImage(wovenImage.getResultImage(),null));
    }

    public void applyBindings2(DragEvent dragEvent) {
        System.out.println("APPLY BINDING");

    }*/
}