/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.coredraw.swing.engine;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import pasa.cbentley.framework.coredraw.j2se.engine.FontFactoryJ2SE;
import pasa.cbentley.framework.coredraw.src4.ctx.ToStringStaticCoreDraw;
import pasa.cbentley.framework.coredraw.src4.engine.VisualState;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechFont;
import pasa.cbentley.framework.coredraw.swing.ctx.CoreDrawSwingCtx;

public class FontFactorySwing extends FontFactoryJ2SE {

   protected final CoreDrawSwingCtx scc;

   public FontFactorySwing(CoreDrawSwingCtx scc) {
      super(scc);
      this.scc = scc;
      //this is parametrize by launch values

      fontPoints = new int[SIZE_X_NUM];
      fontPoints[SIZE_0_DEFAULT] = 12;
      fontPoints[SIZE_1_TINY] = 8;
      fontPoints[SIZE_2_SMALL] = 10;
      fontPoints[SIZE_3_MEDIUM] = 12;
      fontPoints[SIZE_4_LARGE] = 16;
      fontPoints[SIZE_5_HUGE] = 22;
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

   public String[] getAvailableFontFamilyNames() {
      return GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
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

   
   public IMFont getFont(int face, int style, int size) {
      IMFont f = getFontCached(face, style, size);
      if (f == null) {
         f = new FontSwing(scc, face, style, size);
         setFontFromCache(face, style, size, f);
      }
      return f;
   }

   
   public float getFontScale(int size) {
      return 1;
   }

   public IMFont getFont(String face, int style, int fontPoint) {
      // TODO Auto-generated method stub
      return null;
   }

   public int getScalePixel(int valu, int fun) {
      // TODO Auto-generated method stub
      return 0;
   }

   public void setFontName(String name) {
      // TODO Auto-generated method stub

   }

   public IMFont getDefaultFontMono() {
      // TODO Auto-generated method stub
      return null;
   }

   public IMFont getDefaultFontProportional() {
      // TODO Auto-generated method stub
      return null;
   }

   public int getFontPointExtraShift() {
      // TODO Auto-generated method stub
      return 0;
   }

}
