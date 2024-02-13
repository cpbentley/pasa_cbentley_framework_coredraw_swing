/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.coredraw.swing.engine;

import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.InputStream;

import javax.imageio.ImageIO;

import pasa.cbentley.framework.coredraw.j2se.engine.ImageJ2SE;
import pasa.cbentley.framework.coredraw.src4.interfaces.IGraphics;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechGraphics;
import pasa.cbentley.framework.coredraw.swing.ctx.CoreDrawSwingCtx;

/**
 * J2SE bridge class for the {@link mordan.bridge.swing.ui.ImageSwing} class of MIDP 2.0.
 * <br>
 * <br>
 * @author Charles-Philip Bentley
 *
 */
public class ImageSwing extends ImageJ2SE {

   protected final CoreDrawSwingCtx cdcSwing;

   /** 
    * Each image object has an associated Graphics object. 
    */
   public GraphicsSwing             graphics;

   /** 
    * We are lucky in Swing. Its always possible to create a {@link BufferedImage} and draw on it
    * <br>
    * This field is never null once the constructor finishes. 
    */
   private BufferedImage            image;

  
   /**
    * Creates relevant {@link IGraphics} object.
    * @param bi
    * @param cdc
    */
   public ImageSwing(CoreDrawSwingCtx cdc, BufferedImage bi) {
      super(cdc);
      this.cdcSwing = cdc;
      this.image = bi;
      this.isMutable = true;
      //#debug
      toStringNameDebug = "ImageSwing BufferedImage";
   }

   /**
    * Constructor for creating an Image from an InputStream of image data.  
    * <br>
    * <br>
    * Creates relevant {@link IGraphics} object.
    * 
    * Supports formats by {@link ImageIO}
    */
   public ImageSwing(CoreDrawSwingCtx cdc, InputStream is) {
      super(cdc);
      this.cdcSwing = cdc;
      try {
         this.image = ImageIO.read(is);
      } catch (Exception ex) {
         //#debug
         toDLog().pEx("Exception loading image from InputStream.", null, ImageSwing.class, "", ex);
         ex.printStackTrace();
      }
      this.isMutable = true;
      //#debug
      toStringNameDebug = "ImageSwing InputStream";
   }

   /**
    * Constructor for creating a new Image with a given width and height.
    * <br>
    * Background is filled with opaque white pixels.
    * <br>
    * <br>
    * Creates relevant {@link IGraphics} object.
    * @param w
    * @param h
    */
   public ImageSwing(CoreDrawSwingCtx cdc, int w, int h) {
      this(cdc, w, h, cdc.getColorImageBackgroundDefault(), 0);
   }

   /**
    * Constructor for creating a new Image with a given width and height.
    * <br>
    * Creates relevant {@link IGraphics} object.
    * @param w
    * @param h
    * @param cdc
    * @param color
    */
   public ImageSwing(CoreDrawSwingCtx cdc, int w, int h, int color) {
      this(cdc, w, h, color, 0);
   }

   /**
    * 
    * {@link ITechGraphics#IMPL_FLAG_3_TRANS_BACKGROUND}
    * is supported anyways and will be.
    * 
    * Creates relevant {@link IGraphics} object.
    * @param w
    * @param h
    * @param cdc
    * @param color when alpha is 0
    * @param renderingFlags
    */
   public ImageSwing(CoreDrawSwingCtx cdc, int w, int h, int color, int renderingFlags) {
      super(cdc);
      this.cdcSwing = cdc;
      this.image = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
      this.isMutable = true;
      this.isEmpty = true;
      this.colorBackground = color;
      this.renderingFlags = renderingFlags;
      //#debug
      toStringNameDebug = "ImageSwing  w" + w + " h" + h + " color=" + color;
   }

   /**
    * Returns the {@link IGraphics} object
    */
   public GraphicsSwing getGraphics() {
      if (!isMutable)
         throw new IllegalStateException();
      if (graphics == null) {
         graphics = new GraphicsSwing(cdcSwing, image.createGraphics());
         if (isEmpty) {
            //we have to initialize the background
            int w = image.getWidth();
            int h = image.getHeight();
            int alpha = (colorBackground >> 24) & 0xFF;
            if (alpha == 0) {
               //fill graphics with clear pixels
               graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
               graphics.fillRect(0, 0, w, h);
               //reset composite
               graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            } else {
               graphics.setColor(colorBackground);
               graphics.fillRect(0, 0, w, h);
            }
         }
         graphics.toStringSetNameDebug(toStringNameDebug);
         graphics.setClip(0, 0, image.getWidth(), image.getHeight());
      }
      return graphics;
   }

   public int getHeight() {
      return image.getHeight(null);
   }

   /**
    * 
    * @return
    */
   public BufferedImage getImageSwing() {
      return image;
   }

   public void getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height) {
      if (width <= 0 || height <= 0) {
         return;
      }
      if (x < 0 || y < 0 || x + width > getWidth() || y + height > getHeight()) {
         throw new IllegalArgumentException("Specified area exceeds bounds of image");
      }
      if ((scanlength < 0 ? -scanlength : scanlength) < width) {
         throw new IllegalArgumentException("abs value of scanlength is less than width");
      }
      if (rgbData == null) {
         throw new NullPointerException("null rgbData");
      }
      if (offset < 0 || offset + width > rgbData.length) {
         throw new ArrayIndexOutOfBoundsException();
      }
      if (scanlength < 0) {
         if (offset + scanlength * (height - 1) < 0) {
            throw new ArrayIndexOutOfBoundsException();

         }
      } else {
         if (offset + scanlength * (height - 1) + width > rgbData.length) {
            throw new ArrayIndexOutOfBoundsException();
         }
      }

      try {
         (new PixelGrabber(image, x, y, width, height, rgbData, offset, scanlength)).grabPixels();
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

   public int getWidth() {
      return image.getWidth(null);
   }

}
