package org.wtfTeam.engine.graph;

import org.wtfTeam.engine.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class PicTexture {

    private static final String IMAGE_FORMAT = "png";

    private String imgPath;
    private String allChars;

    private final Map<Character, CharInfo> charMap;

    private Texture texture;

    private int height;//总高 = 单个图片的高，因为是一行排列

    private int width;//这是总长

    private int one_width;//这是单个图片的长度

    public PicTexture(String imgPath, String allChars, int width, int height) throws Exception {
        //imgPath为图片的本地地址，allChars为映射到的字符串

        this.allChars = allChars;
        //这个allChars对应块的种类，即用字符串映射图片
        this.imgPath = imgPath;
        this.one_width = width;
        this.height = height;
        charMap = new HashMap<>();

        buildTexture();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Texture getTexture() {
        return texture;
    }

    public CharInfo getCharInfo(char c) {
        return charMap.get(c);
    }


    private void buildTexture() throws Exception {
        // Get the font metrics for each character for the selected font by using image
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2D = img.createGraphics();


        this.width = 0;
        for (char c : allChars.toCharArray()) {
            // Get the size for each character and update global image size
            CharInfo charInfo = new CharInfo(width, one_width);
            charMap.put(c, charInfo);
            width += charInfo.getWidth();
        }
        g2D.dispose();

        //这里加载本地图片，横着排列
        img = ImageIO.read(Class.forName(Utils.class.getName()).getResourceAsStream(imgPath));


        ByteBuffer buf = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(img, IMAGE_FORMAT, out);
            out.flush();
            byte[] data = out.toByteArray();
            buf = ByteBuffer.allocateDirect(data.length);
            buf.put(data, 0, data.length);
            buf.flip();
        }


        texture = new Texture(buf);
    }

    public static class CharInfo {

        private final int startX;

        private final int width;

        public CharInfo(int startX, int width) {
            this.startX = startX;
            this.width = width;
        }

        public int getStartX() {
            return startX;
        }

        public int getWidth() {
            return width;
        }
    }
}
