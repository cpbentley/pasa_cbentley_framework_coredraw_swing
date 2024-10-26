/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.core.draw.swing.engine;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;

import pasa.cbentley.core.src4.ctx.IToStringFlagsUC;
import pasa.cbentley.core.src4.interfaces.IToStringFlags;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.core.draw.j2se.engine.FontJ2se;
import pasa.cbentley.framework.core.draw.swing.ctx.CoreDrawSwingCtx;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechFont;

/**
 * J2SE bridge class for the {@link mordan.bridge.swing.ui.FontSwing} class of MIDP 2.0 <br>
 * 
 * Q:how to provide antialiased font drawing in this bridge?
 * A: Anti-aliasing is done at the Graphics level with Hints.
 * 
 * @author Charles-Philip Bentley
 *
 */
public class FontSwing extends FontJ2se {

   /** 
    * Reference to an AWT Font object, created based on the MIDP font properties requested
    */
   private Font                     fontAwt;

   /** 
    * FontMetrics object does most of the font width and height stuff for us
    */
   private FontMetrics              fontMetrics;

   protected final CoreDrawSwingCtx cdcSwing;

   /**
    * Font face is divided into 2 big families
    * <li> monospaced
    * <li> not monospaced
    * <br>
    * <br>
    * How do you built the system for hot loading of all fonts with a new face and updated base size?
    * <br>
    * On a system that supports font size. negative point size are correctly mapped. on J2ME such sizes
    * must be mapped to small/medium/large.
    * <br>
    * <br>
    * 
    */
   public FontSwing(CoreDrawSwingCtx cdcSwing, int face, int style, int size) {
      super(cdcSwing, face, style, size);
      this.cdcSwing = cdcSwing;

      int fontAwtStyle = getMapStyle(style);
      //points are initialized by upper constructor
      this.fontAwt = new java.awt.Font(fontNameInit, fontAwtStyle, points);
      this.fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(this.fontAwt);
   }

   private int getMapStyle(int style) {
      switch (style) {
         case STYLE_1_BOLD:
            return Font.BOLD;
         case STYLE_2_ITALIC:
            return Font.ITALIC;
         default:
            return Font.PLAIN;
      }
   }

   public FontSwing(CoreDrawSwingCtx cdcSwing, String face, int style, int fontPoints) {
      super(cdcSwing);
      this.cdcSwing = cdcSwing;
      this.fontNameInit = face;

      int fontAwtStyle = getMapStyle(style);
      this.points = fontPoints + cdc.getFontFactory().getFontPointExtraShift();
      this.fontAwt = new java.awt.Font(fontNameInit, fontAwtStyle, points);
      this.fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(this.fontAwt);

      this.style = style;

      int size = ITechFont.SIZE_1_TINY;
      if (fontPoints >= cdc.getFontFactory().getFontPoint(SIZE_5_HUGE)) {
         size = ITechFont.SIZE_5_HUGE;
      } else if (fontPoints >= cdc.getFontFactory().getFontPoint(SIZE_4_LARGE)) {
         size = ITechFont.SIZE_4_LARGE;
      } else if (fontPoints >= cdc.getFontFactory().getFontPoint(SIZE_3_MEDIUM)) {
         size = ITechFont.SIZE_3_MEDIUM;
      } else if (fontPoints >= cdc.getFontFactory().getFontPoint(SIZE_2_SMALL)) {
         size = ITechFont.SIZE_2_SMALL;
      }
      this.size = size;
      this.face = (stringWidth("m") == stringWidth("i")) ? FACE_01_MONOSPACE : FACE_02_PROPORTIONAL;
   }

   public int getAscent() {
      return fontMetrics.getAscent();
   }

   public int getDescent() {
      return fontMetrics.getDescent();
   }

   public String getName() {
      return fontAwt.getFontName();
   }

   public java.awt.Font getFontAWT() {
      return fontAwt;
   }

   public int charWidth(char c) {
      return fontMetrics.charWidth(c);
   }

   /**
    * @throws NullPointerException if c is null
    */
   public int charsWidth(char[] c, int ofs, int len) {
      return fontMetrics.charsWidth(c, ofs, len);
   }

   public int getBaselinePosition() {
      return fontMetrics.getAscent();
   }

   public int getHeight() {
      return fontMetrics.getHeight();
   }

   public int stringWidth(String s) {
      return fontMetrics.stringWidth(s);
   }

   public int substringWidth(String s, int offset, int length) {
      return fontMetrics.stringWidth(s.substring(offset, offset + length));
   }

   public boolean isSupported(int flag) {
      return true;
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, FontSwing.class, toStringGetLine(149));
      toStringPrivate(dc);
      super.toString(dc.sup());

      if (dc.hasFlagToStringUC(IToStringFlagsUC.FLAG_UC_10_HOST_OBJECTS)) {
         //host objects are disabled by master flag
         dc.nl();
         cdcSwing.getSwingCoreCtx().toSCD().d(fontAwt, dc);
      } else {
         //
         dc.append("fontAwt data is not printed. To Print it set IToStringFlagsUC.FLAG_UC_10_HOST_OBJECTS");
      }
   }

   private void toStringPrivate(Dctx dc) {
      dc.appendVarWithSpace("Name", getName());
      dc.appendVarWithSpace("ascent", getAscent());
      dc.appendVarWithSpace("descent", getDescent());
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, FontSwing.class, toStringGetLine(160));
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   //#enddebug

}
