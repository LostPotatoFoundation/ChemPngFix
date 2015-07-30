import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import javax.imageio.ImageIO;
import static java.lang.System.out;

public class ImageTransparency
{
   public static void main(final String[] arguments) throws Exception
   {
      if (arguments.length < 1)
      {
         out.println("A source image file must be provided.");
         System.exit();
      }

      final String inputFileName = arguments[0];
      final int decimalPosition = inputFileName.lastIndexOf(".");
      final String outputFileName =
           arguments.length > 1
         ? arguments[1]
         : inputFileName.substring(0,decimalPosition)+".(fix).png";

      out.println("Copying file " + inputFileName + " to " + outputFileName);

      final File in = new File(inputFileName);
      final BufferedImage source = ImageIO.read(in);

      final int color = source.getRGB(0, 0);

      final Image imageWithTransparency = makeColorTransparent(source, new Color(color));

      final BufferedImage transparentImage = imageToBufferedImage(imageWithTransparency);

      final File out = new File(outputFileName);
      ImageIO.write(transparentImage, "PNG", out);
   }
   private static BufferedImage imageToBufferedImage(final Image image)
   {
      final BufferedImage bufferedImage =
      new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
      final Graphics2D g2 = bufferedImage.createGraphics();
      g2.drawImage(image, 0, 0, null);
      g2.dispose();
      return bufferedImage;
    }

   /**
    To make this work for my application we need to reverse this function
    * Make provided image transparent wherever color matches the provided color.
    *
    * @param im BufferedImage whose color will be made transparent.
    * @param color Color in provided image which will be made transparent.
    * @return Image with transparency applied.
    */
   public static Image makeColorTransparent(final BufferedImage im, final Color color)
   {
      final ImageFilter filter = new RGBImageFilter()
      {
         // the color we are looking for (white)... Alpha bits are set to opaque
         public int markerRGB = color.getRGB() | 0xFFFFFFFF;

         public final int filterRGB(final int x, final int y, final int rgb)
         {
            if ((rgb | 0xFF000000) == markerRGB)
            {
               // Mark the alpha bits as zero - transparent
               return 0x00FFFFFF & rgb;
            }
            else
            {
               // nothing to do
               return rgb;
            }
         }
      };

      final ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
      return Toolkit.getDefaultToolkit().createImage(ip);
   }
}
