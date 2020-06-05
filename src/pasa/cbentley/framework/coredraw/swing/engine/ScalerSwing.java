/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.coredraw.swing.engine;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.coredraw.src4.engine.ScalerAbstract;
import pasa.cbentley.framework.coredraw.src4.interfaces.IScaler;
import pasa.cbentley.framework.coredraw.swing.ctx.CoreDrawSwingCtx;
import pasa.cbentley.layouter.src4.tech.ITechLayout;

public class ScalerSwing extends ScalerAbstract implements IScaler, ITechLayout {

   protected final CoreDrawSwingCtx cdsc;

   /**
    * <li> {@link ITechLayout#SIZE_0_NONE} = 0
    * <li> {@link ITechLayout#SIZE_1_SMALLEST} = 1
    * <li> {@link ITechLayout#SIZE_2_SMALL} = 2
    * <li> {@link ITechLayout#SIZE_3_MEDIUM} = 3
    * <li> {@link ITechLayout#SIZE_4_BIG} = 5
    * <li> {@link ITechLayout#SIZE_5_BIGGEST} = 10
    * 
    */
   int[]                            scalePadding = new int[] { 0, 2, 4, 5, 6, 8, 10 };

   public ScalerSwing(CoreDrawSwingCtx cdc) {
      super(cdc);
      this.cdsc = cdc;
      scalePadding = new int[6];
      scalePadding[SIZE_0_NONE]=0;
      scalePadding[SIZE_1_SMALLEST]=2;
      scalePadding[SIZE_2_SMALL]=4;
      scalePadding[SIZE_3_MEDIUM]=5;
      scalePadding[SIZE_4_BIG]=8;
      scalePadding[SIZE_5_BIGGEST]=10;
   }

   @Override
   public int getScalePixel(int valu, int fun) {
      if (fun == SCALE_0_PADDING) {
         return scalePadding[fun];
      } else if (fun == SCALE_1_EXPO) {
         return scalePadding[fun];
      } else {
         throw new IllegalArgumentException();
      }
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
   
   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, "ScalerSwing");
      toStringPrivate(dc);
      super.toString(dc.sup());
   }

   private void toStringPrivate(Dctx dc) {
      
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, "ScalerSwing");
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   //#enddebug
   

}
