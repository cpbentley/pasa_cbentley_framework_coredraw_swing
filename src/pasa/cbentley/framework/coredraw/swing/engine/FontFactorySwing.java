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

import pasa.cbentley.core.swing.stringables.StringableFontAwt;
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

      fontPoints = scc.getConfigCoreDrawJ2se().getFontPoints();
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



   public int getFontPointExtraShift() {
      // TODO Auto-generated method stub
      return 0;
   }

}
