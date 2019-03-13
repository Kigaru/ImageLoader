package utilities;

public class PixelCollection {

    private int[] pixels;

    public PixelCollection(int width, int height) {
        pixels = new int[width*height];
    }

    public int[] getPixels() {
        return pixels;
    }

    public void setPixel(int index, int parent) {
        pixels[index] = parent;
    }
}
