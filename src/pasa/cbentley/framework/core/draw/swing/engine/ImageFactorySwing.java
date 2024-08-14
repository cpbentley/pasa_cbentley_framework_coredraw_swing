/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.core.draw.swing.engine;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import pasa.cbentley.framework.core.draw.j2se.engine.ImageFactoryJ2se;
import pasa.cbentley.framework.core.draw.swing.ctx.CoreDrawSwingCtx;
import pasa.cbentley.framework.coredraw.src4.interfaces.IGraphics;
import pasa.cbentley.framework.coredraw.src4.interfaces.IImage;

public class ImageFactorySwing extends ImageFactoryJ2se {

   protected final CoreDrawSwingCtx scc;

   public ImageFactorySwing(CoreDrawSwingCtx scc) {
      super(scc);
      this.scc = scc;
   }

   public IGraphics createGraphics(Object g) {
      GraphicsSwing gw = new GraphicsSwing(scc, (Graphics2D) g);
      //#debug
      gw.toStringSetNameDebug("ImageFactorySwing");
      return gw;
   }

   @Override
   public IImage createImage(InputStream is) {
      return new ImageSwing(scc, is);
   }

   @Override
   public IImage createImage(int w, int h) {
      return new ImageSwing(scc, w, h);
   }

   /**
    * When {@link ITechHost#HOST_FLAGX_1_ANTI_ALIAS} is false, the alpha value is ignored.
    * @param w
    * @param h
    * @param color
    * @return
    */
   public IImage createImage(int w, int h, int color) {
      return new ImageSwing(scc, w, h, color);
   }

   @Override
   public IImage createImage(byte[] data, int start, int len) {
      return new ImageSwing(scc, new ByteArrayInputStream(data, start, len));
   }

   public IImage createRGBImage(int[] rgb, int width, int height, boolean processAlpha) {
      int imageType = BufferedImage.TYPE_INT_RGB;
      if (processAlpha) {
         imageType = BufferedImage.TYPE_INT_ARGB;
      }
      //IndexColorModel icm = new IndexColorModel(0, 0, rgb, 0, processAlpha, 0, 0);
      BufferedImage bi = new BufferedImage(width, height, imageType);
      bi.setRGB(0, 0, width, height, rgb, 0, width);
      return new ImageSwing(scc, bi);
   }

   /**
    * Convenience method that returns a scaled instance of the
    * provided {@code BufferedImage}.
    *
    * @param img the original image to be scaled
    * @param targetWidth the desired width of the scaled instance,
    *    in pixels
    * @param targetHeight the desired height of the scaled instance,
    *    in pixels
    * @param hint one of the rendering hints that corresponds to
    *    {@code RenderingHints.KEY_INTERPOLATION} (e.g.
    *    {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
    *    {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
    *    {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
    * @param higherQuality if true, this method will use a multi-step
    *    scaling technique that provides higher quality than the usual
    *    one-step technique (only useful in downscaling cases, where
    *    {@code targetWidth} or {@code targetHeight} is
    *    smaller than the original dimensions, and generally only when
    *    the {@code BILINEAR} hint is specified)
    * @return a scaled version of the original {@code BufferedImage}
    */
   public BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint, boolean higherQuality) {
      int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
      BufferedImage ret = (BufferedImage) img;
      int w, h;
      if (higherQuality) {
         // Use multi-step technique: start with original size, then
         // scale down in multiple passes with drawImage()
         // until the target size is reached
         w = img.getWidth();
         h = img.getHeight();
      } else {
         // Use one-step technique: scale directly from original
         // size to target size with a single drawImage() call
         w = targetWidth;
         h = targetHeight;
      }

      do {
         if (higherQuality && w > targetWidth) {
            w /= 2;
            if (w < targetWidth) {
               w = targetWidth;
            }
         }

         if (higherQuality && h > targetHeight) {
            h /= 2;
            if (h < targetHeight) {
               h = targetHeight;
            }
         }

         BufferedImage tmp = new BufferedImage(w, h, type);
         Graphics2D g2 = tmp.createGraphics();
         g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
         g2.drawImage(ret, 0, 0, w, h, null);
         g2.dispose();

         ret = tmp;
      } while (w != targetWidth || h != targetHeight);

      return ret;
   }

   public IImage createImage(IImage source) {
      // TODO Auto-generated method stub
      return null;
   }

   public IImage createImage(IImage image, int x, int y, int width, int height, int transform) {
      // TODO Auto-generated method stub
      return null;
   }

   public IImage createImage(String name) throws IOException {
      // TODO Auto-generated method stub
      return null;
   }


}
