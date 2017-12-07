package be.howest.photoweave.model;


import com.google.gson.Gson;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CustomFile {
    private ImageData image;
    private Mutation mutation;
    private UserInterface userInterface;

    public CustomFile(ImageData image, Mutation mutation, UserInterface userInterface) {
        this.image = image;
        this.mutation = mutation;
        this.userInterface = userInterface;
    }

    public class ImageData{
        private String base64;
        private int width;
        private int height;

        public ImageData(String base64, int width, int height) {
            this.base64 = base64;
            this.width = width;
            this.height = height;
        }
    }

    public class Mutation{
        private Scale scale;
        private int posterization;
        private List<BindingData> bindingpalette;

        private Mutation(Scale scale, int posterization, List<BindingData> bindingpalette) {
            this.scale = scale;
            this.posterization = posterization;
            this.bindingpalette = bindingpalette;
        }
    }

    public class Scale{
        private int width;
        private int height;
        private double ratio;

        public Scale(int width, int height) {
            this.width = width;
            this.height = height;
            this.ratio = width/height;
        }
    }

    public class BindingData{
        private String cname;
        private String bname;
        private int rgbInt;
        private String base64;

        public BindingData(String cname, String bname, int rgbInt, String base64) {
            this.cname = cname;
            this.bname = bname;
            this.rgbInt = rgbInt;
            this.base64 = base64;
        }
    }


    public class UserInterface{
        private ImageViewParams imageview;
        private BindingParams binding;

        private UserInterface(ImageViewParams imageview, BindingParams binding) {
            this.imageview = imageview;
            this.binding = binding;
        }
    }

    public class ImageViewParams{
        private double zoom;
        private int scrollX;
        private int scrollY;

        public ImageViewParams(double zoom, int scrollX, int scrollY) {
            this.zoom = zoom;
            this.scrollX = scrollX;
            this.scrollY = scrollY;
        }
    }

    public class BindingParams{
        private boolean inverted;
        private selectedBindingsParams selectedbinding;

        public BindingParams(boolean inverted, selectedBindingsParams selectedbinding) {
            this.inverted = inverted;
            this.selectedbinding = selectedbinding;
        }
    }

    public class selectedBindingsParams{
        private int index;
        private boolean marked;
        private BindingParams item;
        private floatersParams floaters;

        public selectedBindingsParams(int index, boolean marked, BindingParams item, floatersParams floaters) {
            this.index = index;
            this.marked = marked;
            this.item = item;
            this.floaters = floaters;
        }
    }

    public class floatersParams{
        private boolean marked;
        private int x;
        private int y;

        public floatersParams(boolean marked, int x, int y) {
            this.marked = marked;
            this.x = x;
            this.y = y;
        }
    }


    /* IMAGE TO BASE64
        Image image = ImageIO.read(new URL("https://i.imgur.com/05BNKi9.jpg"));
        BufferedImage bi = new BufferedImage(image.getWidth(null),image.getHeight(null),BufferedImage.TYPE_INT_RGB);

        Graphics graphics = bi.getGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(bi,"bmp",output);
        String test = DatatypeConverter.printBase64Binary(output.toByteArray());
    */
    /*
    public static void main(String [] args) throws IOException {
        CustomFile cf = new CustomFile();
    }
    */
    public static void main(String [] args){
        String json = null;
        try {
            json = new String(Files.readAllBytes(Paths.get("C:\\Users\\Quinten\\OneDrive\\Documenten\\verver.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson g = new Gson();

        CustomFile cf = g.fromJson(json, CustomFile.class);

        System.out.println(cf);
    }
}
