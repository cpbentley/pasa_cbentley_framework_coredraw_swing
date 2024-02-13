/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.coredraw.swing.engine;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.swing.stringables.StringableFontAwt;
import pasa.cbentley.framework.coredraw.j2se.engine.FontFactoryJ2SE;
import pasa.cbentley.framework.coredraw.src4.ctx.ToStringStaticCoreDraw;
import pasa.cbentley.framework.coredraw.src4.engine.VisualState;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechFont;
import pasa.cbentley.framework.coredraw.swing.ctx.CoreDrawSwingCtx;

/**
 * 
 * Provides the Swing mapping for  {@link ITechFont#SIZE_3_MEDIUM} into Swing fontPoints
 * 
 * <li> {@link FontFactorySwing#getFontPoint(int)}
 * 
 * <li> {@link FontFactorySwing#getAvailableFontFamilyNames()}
 * 
 * 
 * @author Charles Bentley
 *
 */
public class FontFactorySwing extends FontFactoryJ2SE {

   protected final CoreDrawSwingCtx scc;

   public FontFactorySwing(CoreDrawSwingCtx scc) {
      super(scc);
      this.scc = scc;
      //this is parametrize by launch values
     
      fontPointsExtraShift = scc.getConfigCoreDrawJ2se().getFontPointsExtraShift();
   }

   public int getFontPoint(int size) {
      //#debug
      toDLog().pFlow("size=" + ToStringStaticCoreDraw.fontSize(size), null, FontFactorySwing.class, "getFontPoint", LVL_04_FINER, true);
      if (size < -1) {
         return fontPoints[SIZE_0_DEFAULT];
      } else if (size > ITechFont.SIZE_X_NUM) {
         return size;
      } else {
         int points = fontPoints[size];
         return points;
      }
   }

   public void loadFont(String path) {
      InputStream is = this.getClass().getResourceAsStream(path);
      loadFont(is, path);
   }

   public StringableFontAwt getFontD(Font font) {
      return new StringableFontAwt(scc.getSwingCoreCtx(), font);
   }

   public void loadFont(InputStream is, String path) {
      if (is == null) {
         //#debug
         toDLog().pNull("Null InputStream for " + path, this, FontFactoryJ2SE.class, "loadFont", LVL_05_FINE, true);
         return;
      }
      try {
         Font font = Font.createFont(Font.TRUETYPE_FONT, is);
         if (font == null) {
            //#debug
            toDLog().pNull("awt.Font is null for " + path, this, FontFactoryJ2SE.class, "loadFont", LVL_05_FINE, true);
            return;
         }

         boolean registerFont = GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
         //#debug
         toDLog().pInit("" + path + " registerFont=" + registerFont, getFontD(font), FontFactoryJ2SE.class, "loadFont", LVL_05_FINE, true);

      } catch (FontFormatException | IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public String[] getAvailableFontFamilyNames() {
      return GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
   }

   public void setFontRatio(int ratio, int etalon) {
      IMFont f = getDefaultFont();
      int h = f.getHeight();
      int div = etalon / h;
      int fontPoints = 0;
      VisualState vs = null;
      if (div > ratio) {
         //font is too small
         vs = fontSizeIncrease();
      } else {
         vs = fontSizeDecrease();
      }
      fontPoints = vs.fontPoints[0];
   }

   public String[] getMonoSpaceFonts() {
      Font fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
      List monoFonts1 = new ArrayList();

      FontRenderContext frc = new FontRenderContext(null, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
      for (Font font : fonts) {
         Rectangle2D iBounds = font.getStringBounds("i", frc);
         Rectangle2D mBounds = font.getStringBounds("m", frc);
         if (iBounds.getWidth() == mBounds.getWidth()) {
            monoFonts1.add(font);
         }
      }
      String[] ar = new String[monoFonts1.size()];
      for (int i = 0; i < ar.length; i++) {
         ar[i] = ((Font) monoFonts1.get(i)).getFontName();
      }
      return ar;
   }

   public IMFont getFont(int face, int style, int size) {
      IMFont f = getFontCached(face, style, size);
      if (f == null) {
         f = new FontSwing(scc, face, style, size);
         setFontFromCache(face, style, size, f);
      }
      return f;
   }

   protected IMFont createFont(int face, int style, int size) {
      return new FontSwing(scc, face, style, size);
   }

   public float getFontScale(int size) {
      return 1;
   }

   public IMFont getFont(String face, int style, int fontPoint) {
      return new FontSwing(scc, face, style, fontPoint);
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, FontFactorySwing.class, "@line5");
      toStringPrivate(dc);
      super.toString(dc.sup());
   }

   private void toStringPrivate(Dctx dc) {

   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, FontFactorySwing.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   //#enddebug

}
