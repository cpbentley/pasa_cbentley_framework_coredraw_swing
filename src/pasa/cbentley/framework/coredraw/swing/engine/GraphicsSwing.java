/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.coredraw.swing.engine;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IEventsBO;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.event.BusEvent;
import pasa.cbentley.core.src4.event.IEventConsumer;
import pasa.cbentley.core.src4.interfaces.ITechThread;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.framework.coredraw.j2se.engine.GraphicsJ2SE;
import pasa.cbentley.framework.coredraw.src4.ctx.IEventsCoreDraw;
import pasa.cbentley.framework.coredraw.src4.ctx.ITechCtxSettingsCoreDraw;
import pasa.cbentley.framework.coredraw.src4.interfaces.IGraphics;
import pasa.cbentley.framework.coredraw.src4.interfaces.IImage;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechDrawer;
import pasa.cbentley.framework.coredraw.swing.ctx.CoreDrawSwingCtx;

public class GraphicsSwing extends GraphicsJ2SE implements IGraphics, IEventConsumer {

   public static final int          BASELINE  = 64;

   public static final int          BOTTOM    = 32;

   public static final int          DOTTED    = 1;

   public static final int          HCENTER   = 1;

   public static final int          LEFT      = 4;

   public static final int          RIGHT     = 8;

   public static final int          SOLID     = 0;

   public static final int          TOP       = 16;

   public static final int          VCENTER   = 2;

   /** 
    * Rectangle for getting the AWT clip information into 
    */
   private java.awt.Rectangle       clip      = new java.awt.Rectangle();

   private int                      color;

   /** 
    */
   private FontSwing                fontSwing = null;

   private int                      fwFlags;

   /** 
    * AWT Graphics context.  
    * <br>
    * This is passed in the constructor, but will be created from the getGraphics call on an AWT Image
    */
   private java.awt.Graphics2D      graphics;

   private Stroke                   gStroke;

   protected final CoreDrawSwingCtx scc;

   private int                      stroke;

   private int                      translate_x;

   private int                      translate_y;

   /**
    * When use, a call to {@link GraphicsSwing#setGraphics2D(Graphics2D)} must be made
    * before using the object
    * @param scc
    */
   public GraphicsSwing(CoreDrawSwingCtx scc) {
      super(scc);
      this.scc = scc;
      aInit();
   }

   /**
    * Create a graphics object based on Appli settings. 
    * {@link IModSetAppli}
    * <br>
    * Or Specific Settings decided by the user that wants to override appli wide settings
    * for a specific purpose.
    * <br>
    * <br>
    * 
    * @param g
    * @param scc
    */
   public GraphicsSwing(CoreDrawSwingCtx scc, java.awt.Graphics2D g) {
      super(scc);
      this.scc = scc;
      setGraphics2D(g);
      aInit();
   }

   /**
    * 
    * @param scc
    * @param g
    * @param renderingFlags Swing rendering hints
    */
   public GraphicsSwing(CoreDrawSwingCtx scc, java.awt.Graphics2D g, int renderingFlags) {
      super(scc);
      //register for ctx events
      this.scc = scc;
      setGraphics2D(g);
      if (renderingFlags == 0) {
         //flags.. not need for a special tech params
      } else {
         createTech();
         tech.set1(DRAWER_OFFSET_02_MODE_ALIAS1, MODSET_APP_ALIAS_1_ON);
      }
      aInit();
   }

   private void aInit() {
      fontSwing = (FontSwing) scc.getFontFactorySwing().getDefaultFont();
   }

   /**
    * This method pins the {@link GraphicsSwing} into memory.
    */
   public void registerForChanges() {
      int pid = IEventsBO.PID_1_CTX;
      int eid = IEventsBO.PID_1_CTX_1_SETTINGS_CHANGE;
      scc.getBOC().getEventBus().addConsumer(this, pid, eid, ITechThread.THREAD_MODE_1_MAIN_NOW);
   }

   /**
    * Apply ctx wide settings from {@link ITechCtxSettingsCoreDraw}
    */
   public void applySettings() {
      applySettingsAlias();
   }

   public void applySettingsAlias() {
      ByteObject tech = scc.getSettingsBO();
      Object hints = null;
      int mode = tech.get1(ITechCtxSettingsCoreDraw.CTX_COREDRAW_OFFSET_02_MODE_ALIAS1);
      if (mode == ITechDrawer.MODSET_APP_ALIAS_0_BEST || mode == ITechDrawer.MODSET_APP_ALIAS_1_ON) {
         hints = RenderingHints.VALUE_ANTIALIAS_ON;
      } else {
         hints = RenderingHints.VALUE_ANTIALIAS_OFF;
      }
      RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, hints);
      graphics.addRenderingHints(rh);

      int modeText = tech.get1(ITechCtxSettingsCoreDraw.CTX_COREDRAW_OFFSET_03_MODE_TEXT_ALIAS1);
      if (modeText == ITechDrawer.MODSET_APP_ALIAS_0_BEST || modeText == ITechDrawer.MODSET_APP_ALIAS_1_ON) {
         hints = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
      } else {
         hints = RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
      }
      RenderingHints textRendering = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, hints);
      graphics.addRenderingHints(textRendering);

   }

   public void clipRect(int x, int y, int width, int height) {
      graphics.clipRect(x, y, width, height);
   }

   public void consumeEvent(BusEvent e) {
      if (e.getPID() == IEventsBO.PID_1_CTX) {
         if (e.getEventID() == IEventsBO.PID_1_CTX_1_SETTINGS_CHANGE) {
            int param1 = e.getParam1();
            if (param1 == IEventsCoreDraw.SETTINGS_0_ALL) {
               applySettings();
               e.setActed();
            } else if (param1 == IEventsCoreDraw.SETTINGS_1_ALIAS) {
               applySettingsAlias();
               e.setActed();
            }
         }
      }
   }

   /**
    * Called when {@link GraphicsSwing} is not used anymore
    */
   public void destroy() {
      scc.getBOC().getEventBus().removeConsumer(this);
   }

   /**
    * Copies the contents of a rectangular area (x_src, y_src, width, height) to a destination area, 
    * whose anchor point identified by anchor is located at (x_dest, y_dest).
    * Method is usefull to create repetitive background cheaply
    * @param x_src
    * @param y_src
    * @param width
    * @param height
    * @param x_dest
    * @param y_dest
    * @param anchor
    * GraphicsX: In All Cases Requires a Merge
    * @throws:
    * IllegalStateException - if the destination of this Graphics object is the display device 
    * IllegalArgumentException - if the region to be copied exceeds the bounds of the source image
    */
   public void copyArea(int x_src, int y_src, int width, int height, int x_dest, int y_dest, int anchor) {
      //TODO do other anchors
      if (anchor != (TOP | LEFT)) {
         throw new RuntimeException("Anchor not implemented yet");
      }
      int dx = x_dest - x_src;
      int dy = y_dest - y_src;
      graphics.copyArea(x_src, y_src, width, height, dx, dy);
   }

   /**
    * TODO
    * 
    * @param image
    */
   public void drawAntiAliasedImage(BufferedImage image) {
      Graphics2D g = graphics;
      // Clear the background to black
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, 50, 50);

      // Create the source image
      BufferedImage srcImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
      Graphics2D g2 = srcImage.createGraphics();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setColor(Color.WHITE);
      g2.fillRect(0, 0, 50, 50);
      g2.setColor(Color.YELLOW);
      g2.fillOval(5, 5, 40, 40);
      g2.setColor(Color.BLACK);
      g2.fillOval(15, 15, 5, 5);
      g2.fillOval(30, 15, 5, 5);
      g2.drawOval(20, 30, 10, 7);
      g2.dispose();

      // Render the image untransformed
      g.drawImage(srcImage, 15, 7, null);
      g.setColor(Color.WHITE);
      g.drawString("Untransformed", 80, 25);

      // Render the image rotated (with NEAREST_NEIGHBOR)
      AffineTransform xform = new AffineTransform();
      xform.setToIdentity();
      xform.translate(15, 70);
      xform.rotate(Math.PI / 8, 25, 25);
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
      g.drawImage(srcImage, xform, null);
      g.setColor(Color.WHITE);
      g.drawString("Nearest Neighbor", 80, 85);

      // Render the image rotated (with BILINEAR)
      xform.setToIdentity();
      xform.translate(15, 130);
      xform.rotate(Math.PI / 8, 25, 25);
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g.drawImage(srcImage, xform, null);
      g.setColor(Color.WHITE);
      g.drawString("Bilinear", 80, 145);

      // Render the image rotated (with BILINEAR and antialiased edges)
      g.setColor(Color.WHITE);
      g.drawString("Bilinear, Antialiased", 80, 205);
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setPaint(new TexturePaint(srcImage, new Rectangle2D.Float(0, 0, srcImage.getWidth(), srcImage.getHeight())));
      xform.setToIdentity();
      xform.translate(15, 190);
      xform.rotate(Math.PI / 8, srcImage.getWidth() / 2, srcImage.getHeight() / 2);
      g.transform(xform);
      g.fillRect(0, 0, srcImage.getWidth(), srcImage.getHeight());
   }

   public void drawArc(int x, int y, int w, int h, int sa, int aa) {
      graphics.drawArc(x, y, w, h, sa, aa);
   }

   public void drawChar(char character, int x, int y, int anchor) {
      drawString("" + character, x, y, anchor);
   }

   public void drawChars(char[] data, int offset, int length, int x, int y, int anchor) {
      drawString(new String(data, offset, length), x, y, anchor);
   }

   public void drawImage(IImage imgx, int x, int y, int anchor) {
      ImageSwing img = (ImageSwing) imgx;
      // default anchor
      if (anchor == 0) {
         anchor = TOP | LEFT;
      }

      switch (anchor & (TOP | BOTTOM | BASELINE | VCENTER)) {
         case BASELINE:
         case BOTTOM:
            y -= img.getHeight();
            break;
         case VCENTER:
            y -= img.getHeight() >> 1;
            break;
         case TOP:
         default:
            break;

      }

      switch (anchor & (LEFT | RIGHT | HCENTER)) {
         case RIGHT:
            x -= img.getWidth();
            break;
         case HCENTER:
            x -= img.getWidth() >> 1;
            break;
         case LEFT:
         default:
            break;
      }

      graphics.drawImage(img.getImageSwing(), x, y, null);
   }

   public void drawLine(int x1, int y1, int x2, int y2) {
      graphics.drawLine(x1, y1, x2, y2);
   }

   public void drawRect(int x, int y, int width, int height) {
      graphics.drawRect(x, y, width, height);
   }

   public void drawRegion(IImage srcx, int x_src, int y_src, int width, int height, int transform, int x_dst, int y_dst, int anchor) {
      ImageSwing src = (ImageSwing) srcx;
      // may throw NullPointerException, this is ok
      if (x_src + width > src.getWidth() || y_src + height > src.getHeight() || width < 0 || height < 0 || x_src < 0 || y_src < 0) {
         throw new IllegalArgumentException("Area out of Image");
      }

      java.awt.Image img = src.getImageSwing();

      java.awt.geom.AffineTransform t = new java.awt.geom.AffineTransform();

      int dW = width, dH = height;
      switch (transform) {
         case IImage.TRANSFORM_0_NONE: {
            break;
         }
         case IImage.TRANSFORM_5_ROT_90: {
            t.translate((double) height, 0);
            t.rotate(Math.PI / 2);
            dW = height;
            dH = width;
            break;
         }
         case IImage.TRANSFORM_3_ROT_180: {
            t.translate(width, height);
            t.rotate(Math.PI);
            break;
         }
         case IImage.TRANSFORM_6_ROT_270: {
            t.translate(0, width);
            t.rotate(Math.PI * 3 / 2);
            dW = height;
            dH = width;
            break;
         }
         case IImage.TRANSFORM_2_FLIP_V_MIRROR: {
            t.translate(width, 0);
            t.scale(-1, 1);
            break;
         }
         case IImage.TRANSFORM_7_MIRROR_ROT90: {
            t.translate((double) height, 0);
            t.rotate(Math.PI / 2);
            t.translate((double) width, 0);
            t.scale(-1, 1);
            dW = height;
            dH = width;
            break;
         }
         case IImage.TRANSFORM_1_FLIP_H_MIRROR_ROT180: {
            t.translate(width, 0);
            t.scale(-1, 1);
            t.translate(width, height);
            t.rotate(Math.PI);
            break;
         }
         case IImage.TRANSFORM_4_MIRROR_ROT270: {
            t.rotate(Math.PI * 3 / 2);
            t.scale(-1, 1);
            dW = height;
            dH = width;
            break;
         }
         default:
            throw new IllegalArgumentException("Bad transform");
      }

      // process anchor and correct x and y _dest
      // vertical
      boolean badAnchor = false;

      if (anchor == 0) {
         anchor = TOP | LEFT;
      }

      if ((anchor & 0x7f) != anchor || (anchor & BASELINE) != 0)
         badAnchor = true;

      if ((anchor & TOP) != 0) {
         if ((anchor & (VCENTER | BOTTOM)) != 0)
            badAnchor = true;
      } else if ((anchor & BOTTOM) != 0) {
         if ((anchor & VCENTER) != 0)
            badAnchor = true;
         else {
            y_dst -= dH - 1;
         }
      } else if ((anchor & VCENTER) != 0) {
         y_dst -= (dH - 1) >>> 1;
      } else {
         // no vertical anchor
         badAnchor = true;
      }

      // horizontal
      if ((anchor & LEFT) != 0) {
         if ((anchor & (HCENTER | RIGHT)) != 0)
            badAnchor = true;
      } else if ((anchor & RIGHT) != 0) {
         if ((anchor & HCENTER) != 0)
            badAnchor = true;
         else {
            x_dst -= dW - 1;
         }
      } else if ((anchor & HCENTER) != 0) {
         x_dst -= (dW - 1) >>> 1;
      } else {
         // no horizontal anchor
         badAnchor = true;
      }

      if (badAnchor)
         throw new IllegalArgumentException("Bad Anchor");

      java.awt.geom.AffineTransform savedT = graphics.getTransform();

      graphics.translate(x_dst, y_dst);
      graphics.transform(t);

      graphics.drawImage(img, 0, 0, width, height, x_src, y_src, x_src + width, y_src + height, null);

      // return to saved
      graphics.setTransform(savedT);
   }

   public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {
      if (rgbData == null)
         throw new NullPointerException();
      if (width == 0 || height == 0) {
         return;
      }
      int l = rgbData.length;
      if (width < 0 || height < 0 || offset < 0 || offset >= l || (scanlength < 0 && scanlength * (height - 1) < 0) || (scanlength >= 0 && scanlength * (height - 1) + width - 1 >= l))
         throw new ArrayIndexOutOfBoundsException();
      int imageType = BufferedImage.TYPE_INT_ARGB;
      if (!processAlpha) {
         imageType = BufferedImage.TYPE_INT_RGB;
      }
      BufferedImage targetImage = new BufferedImage(width, height, imageType);
      targetImage.setRGB(0, 0, width, height, rgbData, offset, scanlength);
      graphics.drawImage(targetImage, x, y, null);
   }

   public void drawRoundRect(int x, int y, int w, int h, int r1, int r2) {
      graphics.drawRoundRect(x, y, w, h, r1, r2);
   }

   public void drawString(String str, int x, int y, int anchor) {
      // default anchor
      if (anchor == 0) {
         anchor = TOP | LEFT;
      }

      switch (anchor & (TOP | BOTTOM | BASELINE | VCENTER)) {
         case TOP:
            y += fontSwing.getAscent();
            break;
         case BOTTOM:
            y -= fontSwing.getDescent();
            break;
         case VCENTER:
            y += (fontSwing.getHeight() / 2);
      }

      switch (anchor & (LEFT | RIGHT | HCENTER)) {
         case RIGHT:
            x -= fontSwing.stringWidth(str);
            break;
         case HCENTER:
            x -= (fontSwing.stringWidth(str) / 2);
            break;
      }

      graphics.drawString(str, x, y);
   }

   public void drawSubstring(String str, int offset, int len, int x, int y, int anchor) {
      drawString(str.substring(offset, offset + len), x, y, anchor);
   }

   /**
    * Is the feature enable for this specific {@link IGraphics} instance
    * 
    * <li> {@link ITechDrawer#SUP_ID_03_OPEN_GL}
    * <li> {@link ITechDrawer#SUP_ID_04_ALIAS}
    * <li> {@link ITechDrawer#SUP_ID_10_TRANSPARENT_BACKGROUND}
    * 
    * @param featureID
    * @return true if feature could be enabled/disabled
    */
   public boolean featureEnable(int featureID, boolean enable) {
      if (featureID == SUP_ID_04_ALIAS) {
         if (tech == null) {
            //return the default settings
            createTech();
            if (enable) {
               tech.set1(DRAWER_OFFSET_02_MODE_ALIAS1, MODSET_APP_ALIAS_2_OFF);
            } else {
               tech.set1(DRAWER_OFFSET_02_MODE_ALIAS1, MODSET_APP_ALIAS_1_ON);
            }
         } else {
            if (enable) {
               tech.set1(DRAWER_OFFSET_02_MODE_ALIAS1, MODSET_APP_ALIAS_2_OFF);
            } else {
               tech.set1(DRAWER_OFFSET_02_MODE_ALIAS1, MODSET_APP_ALIAS_1_ON);
            }
         }
         return true;
      }
      return false;
   }

   public void fillArc(int x, int y, int w, int h, int sa, int aa) {
      graphics.fillArc(x, y, w, h, sa, aa);
   }

   public void fillRect(int x, int y, int width, int height) {
      graphics.fillRect(x, y, width, height);
   }

   public void fillRoundRect(int x, int y, int w, int h, int r1, int r2) {
      graphics.fillRoundRect(x, y, w, h, r1, r2);
   }

   public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
      int[] xPoints = new int[] { x1, x2, x3 };
      int[] yPoints = new int[] { y1, y2, y3 };
      graphics.fillPolygon(xPoints, yPoints, 3);
   }

   public int getBlueComponent() {
      return color & 0xFF;
   }

   public int getClipHeight() {
      graphics.getClipBounds(clip);
      return clip.height;
   }

   public int getClipWidth() {
      graphics.getClipBounds(clip);
      return clip.width;
   }

   public int getClipX() {
      graphics.getClipBounds(clip);
      return clip.x;
   }

   public int getClipY() {
      graphics.getClipBounds(clip);
      return clip.y;
   }

   public int getColor() {
      return graphics.getColor().getRGB();
   }

   public IMFont getDefaultFont() {
      return scc.getFontFactory().getDefaultFont();
   }

   /**
    * Gets the color that will be displayed if the specified color is requested. <br>
    * This method enables the developer to check the manner in which RGB values are mapped 
    * to the set of distinct colors that the device can actually display. 
    * <br>
    * <br>
    * For example, with a monochrome device, 
    * this method will return either 0xFFFFFF (white) or 0x000000 (black) depending on the brightness of the specified color.
    * @param color the desired color (in 0x00RRGGBB format, the high-order byte is ignored) 
    * @return the corresponding color that will be displayed on the device's screen (in 0x00RRGGBB format)
    */
   public int getDisplayColor(int color) {
      //in the 
      return color;
   }

   public IMFont getFont() {
      return fontSwing;
   }

   /**
    * 
    */
   public IMFont getFont(int face, int style, int size) {
      return scc.getFontFactory().getFont(face, style, size);
   }

   public int getGrayScale() {
      //TODO implement grayscaling
      return color;
   }

   public int getGreenComponent() {
      return (color >> 8) & 0xFF;
   }

   public int getRedComponent() {
      return (color >> 16) & 0xFF;
   }

   public int getStrokeStyle() {
      return stroke;
   }

   public int getTranslateX() {
      return translate_x;
   }

   public int getTranslateY() {
      return translate_y;
   }

   /**
    * Is the feature enable for this specific {@link IGraphics} instance
    * 
    * <li> {@link ITechDrawer#SUP_ID_03_OPEN_GL}
    * <li> {@link ITechDrawer#SUP_ID_04_ALIAS}
    * <li> {@link ITechDrawer#SUP_ID_10_TRANSPARENT_BACKGROUND}
    * 
    * @param featureID
    * @return
    */
   public boolean hasFeatureEnabled(int featureID) {
      if (featureID == SUP_ID_04_ALIAS) {
         if (tech == null) {
            //return the default settings
         } else {
            int mode = tech.get1(DRAWER_OFFSET_02_MODE_ALIAS1);
            if (mode == MODSET_APP_ALIAS_2_OFF) {
               return false;
            } else {
               return true;
            }
         }
      }
      return false;
   }

   /**
    * <li> {@link IGraphics#IMPL_FLAG_1_ANTI_ALIAS}
    * 
    * Link to the IMo
    */
   public boolean hasImplementationFlag(int flag) {
      return BitUtils.hasFlag(fwFlags, flag);
   }

   public void setClip(int x, int y, int width, int height) {
      graphics.setClip(x, y, width, height);
   }

   public void setColor(int RGB) {
      color = RGB;
      graphics.setColor(new java.awt.Color(RGB));
   }

   public void setColor(int red, int green, int blue) {
      color = ColorUtils.getRGBInt(red, green, blue);
      graphics.setColor(new java.awt.Color(red, green, blue));
   }

   public void setComposite(AlphaComposite instance) {
      graphics.setComposite(instance);
   }

   public void setFont(IMFont font) {
      this.fontSwing = (FontSwing) font;
      if (this.fontSwing == null) {
         this.fontSwing = (FontSwing) scc.getFontFactory().getDefaultFont();
      }
      graphics.setFont(this.fontSwing.getFontAWT());
   }

   /**
    * Sets the {@link Graphics2D} for this class and apply settings to it 
    * @param g
    */
   public void setGraphics2D(java.awt.Graphics2D g) {
      graphics = g;
      applySettingsAlias();
   }

   public void setGrayScale(int v) {

   }

   /**
    * Stroke style for line drawing isn't something AWT supports.
    * 
    */
   public void setStrokeStyle(int style) {
      Stroke str = graphics.getStroke();
      //System.out.println(((BasicStroke) str).getLineWidth());
      if (style == IGraphics.DOTTED) {
         gStroke = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 1.0f, new float[] { 2.0f }, 1.0f);
      } else {
         gStroke = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
      }
      graphics.setStroke(gStroke);
      stroke = style;
   }

   /**
    * translate needs to remember the current translation, since AWT doesn't provide getTranslate methods.
    */
   public void translate(int x, int y) {
      graphics.translate(-translate_x, -translate_y);
      translate_x += x;
      translate_y += y;
      graphics.translate(translate_x, translate_y);
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, GraphicsSwing.class, "@line5");
      toStringPrivate(dc);
      super.toString(dc.sup());
      dc.appendColorWithSpace("color", color);
      dc.appendVarWithSpace("translate_x", translate_x);
      dc.appendVarWithSpace("translate_y", translate_y);
   }

   private void toStringPrivate(Dctx dc) {

   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, GraphicsSwing.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   //#enddebug

}
