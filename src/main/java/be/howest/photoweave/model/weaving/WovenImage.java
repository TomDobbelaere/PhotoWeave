package be.howest.photoweave.model.weaving;

import be.howest.photoweave.model.binding.Binding;
import be.howest.photoweave.model.binding.BindingPalette;
import be.howest.photoweave.model.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class WovenImage {
    private BufferedImage sourceImage;
    private BufferedImage resultImage;

    private BindingPalette bindingPalette;

    private Integer markedBinding;
    private boolean showMarkedBinding;
    private boolean inverted;
    private boolean showFloaters;

    private int floaterTresholdX = 5;
    private int floaterTresholdY = 5;

    public WovenImage(BufferedImage sourceImage) {
        this.sourceImage = ImageUtil.convertImageToRGBInt(sourceImage);

        this.resultImage = ImageUtil.createBlankCopy(this.sourceImage);
        this.bindingPalette = new BindingPalette(this.sourceImage);

        this.inverted = false;
        this.showFloaters = false;
    }

    public void redraw() {
        Graphics gr = this.resultImage.getGraphics();
        gr.clearRect(0, 0, this.resultImage.getWidth(), this.resultImage.getHeight());
        gr.dispose();

        int[] imageData = ImageUtil.getDataBufferIntData(this.sourceImage);
        int[] targetData = ImageUtil.getDataBufferIntData(this.resultImage);

        tilingAlgorithm(imageData,targetData);

        //if (showFloaters) drawFloaters();
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public void setShowFloaters(boolean showFloaters) {
        this.showFloaters = showFloaters;
    }

    public void setFloaterTresholdX(int floaterTresholdX) {
        this.floaterTresholdX = floaterTresholdX;
    }

    public void setFloaterTresholdY(int floaterTresholdY) {
        this.floaterTresholdY = floaterTresholdY;
    }

    private void tilingAlgorithm(int[] imageData, int[] targetData){
        int threadCount = 8;
        Thread[] threads = new Thread[threadCount];

        for (int k = 0; k < threadCount; k++) {
            int start = ((imageData.length - 1) / threadCount) * k;
            int end = ((imageData.length - 1) / threadCount) * (k + 1);

            threads[k] = new Thread(() -> apply(imageData, targetData, start, end));
            threads[k].start();
        }

        for (int k = 0; k < threadCount; k++)
            try {
                threads[k].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        System.out.println("Tiling finished");
    }

    private void apply(int[] imageData, int[] targetData, int start, int end) {
        for (int i = start; i <= end; i++) {
            Binding binding = bindingPalette.getBindingPalette().get(imageData[i]);
            BufferedImage pattern = binding.getBindingImage();

            int x = (i % this.sourceImage.getWidth()) % pattern.getWidth();
            int y = ((int) Math.floor(i / this.sourceImage.getWidth())) % pattern.getHeight();
            int color = pattern.getRGB(x, y);

            /*if (inverted) {
                if (color == Color.BLACK.getRGB()) color = Color.WHITE.getRGB();
                else color = Color.BLACK.getRGB();
            }

            if (markedBinding != null && showMarkedBinding && imageData[i] == markedBinding) {
                if (color == Color.BLACK.getRGB()) color = Color.YELLOW.getRGB();
                else color = Color.LIGHT_GRAY.getRGB();
            }*/

            targetData[i] = color;
        }

        //System.out.println("Thread " + String.valueOf(start) + "/" + String.valueOf(end));
    }


    private void drawFloaters() {
        int lastColor = 0;
        int colorCount = 0;

        for (int y = 0; y < this.resultImage.getHeight() - 1; y++) {
            colorCount = 0;

            for (int x = 0; x < this.resultImage.getWidth() -1; x++) {
                int color = this.resultImage.getRGB(x, y);

                if (color == lastColor) colorCount++;
                else colorCount = 0;


                if (colorCount > floaterTresholdX - 1) {
                    colorCount = 0;

                    color = Color.RED.getRGB();
                    this.resultImage.setRGB(x, y, color);
                }

                lastColor = color;
            }
        }

        lastColor = 0;

        for (int x = 0; x < this.resultImage.getWidth() -1; x++) {
            colorCount = 0;

            for (int y = 0; y < this.resultImage.getHeight() - 1; y++) {
                int color = this.resultImage.getRGB(x, y);

                if (color == lastColor) colorCount++;
                else colorCount = 0;

                if (colorCount > floaterTresholdY - 1) {
                    colorCount = 0;

                    color = Color.orange.getRGB();
                    this.resultImage.setRGB(x, y, color);
                }

                lastColor = color;
            }
        }
    }

    /* Getter */
    public BufferedImage getResultImage() {
        return resultImage;
    }

    public void setMarkedBinding(Integer markedBinding) {
        this.markedBinding = markedBinding;
    }

    public void setShowMarkedBinding(boolean showMarkedBinding) {
        this.showMarkedBinding = showMarkedBinding;
    }

    public BindingPalette getBindingPalette() {
        return bindingPalette;
    }
}