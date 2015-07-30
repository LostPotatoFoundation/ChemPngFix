package org.lpf.chempngfix;

import javafx.event.ActionEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

public class Controller {
    File target;
    public void loadFile(ActionEvent actionEvent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Files", "png");
        fileChooser.addChoosableFileFilter(filter);
        int returnVal = fileChooser.showDialog(fileChooser,"OK");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            target = fileChooser.getSelectedFile();
        } else {
            System.out.println("Operation cancelled by user.");
        }
    }
    public void runProc(ActionEvent actionEvent) throws IOException {
        if (target != null) {
            final BufferedImage source;
            source = ImageIO.read(target);
            final Image whiteImg = makeTransWhite(source);
            final BufferedImage img = imageToBufferedImage(whiteImg);
            final File out = target.getAbsoluteFile();

            try {
                ImageIO.write(img, "PNG", out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static BufferedImage imageToBufferedImage(final Image image) {
        final BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return bufferedImage;
    }

    public static Image makeTransWhite(final BufferedImage im) {
        final ImageFilter filter = new RGBImageFilter() {
            public final int filterRGB(final int x, final int y, final int rgb) {
                if (rgb == 0)
                    return 0xFFFFFFFF;
                else return rgb;
            }
        };

        final ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }
}


