/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.coredraw.swing.engine;

import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator.Attribute;

import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.coredraw.j2se.engine.FontJ2SE;
import pasa.cbentley.framework.coredraw.swing.ctx.CoreDrawSwingCtx;

/**
 * J2SE bridge class for the {@link mordan.bridge.swing.ui.FontSwing} class of MIDP 2.0 <br>
 * 
 * Q:how to provide antialiased font drawing in this bridge?
 * A: Anti-aliasing is done at the Graphics level with Hints.
 * 
 * @author Charles-Philip Bentley
 *
 */
public class FontSwing extends FontJ2SE {

   /** 
    * Reference to an AWT Font object, created based on the MIDP font properties requested
    */
   private java.awt.Font            font;

   /** 
    * FontMetrics object does most of the font width and height stuff for us
    */
   private FontMetrics              fontMetrics;

   protected final CoreDrawSwingCtx cdcSwing;

   /**
    * Constructor.  Take the MIDP font parameters, and determine an appropriate AWT font to use.
    * <br>
    * <br>
    * Font face is divided into 2 big families
    * <li> monospaced
    * <li> not monospaced
    * <br>
    * <br>
    * How do you built the system for hot loading of all fonts with a new face and updated base size?
    * <br>
    * This here is Swing (platform specific). The user action must invalidate all layouts. 
    * All StringDrawable Stringer are accessed and font resetted.
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
      int awt_style = java.awt.Font.PLAIN;

      switch (style) {
         case STYLE_BOLD:
            awt_style = java.awt.Font.BOLD;
            break;
         case STYLE_ITALIC:
            awt_style = java.awt.Font.ITALIC;
            break;
         //TODO underline as text attribute
      }

      this.font = new java.awt.Font(fontName, awt_style, points);
      this.fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(this.font);
   }

   public int getAscent() {
      return fontMetrics.getAscent();
   }

   public int getDescent() {
      return fontMetrics.getDescent();
   }

   public String getName() {
      return font.getFontName();
   }

   public java.awt.Font getFontAWT() {
      return font;
   }

   public int charWidth(char c) {
      return fontMetrics.charWidth(c);
   }

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
      dc.root(this, FontSwing.class, "@line5");
      toStringPrivate(dc);
      super.toString(dc.sup());
      dc.appendVarWithNewLine("ascent", getAscent());
      dc.appendVarWithSpace("descent", getDescent());

      dc.appendVarWithNewLine("height", getHeight());
      dc.appendVarWithSpace("Widtheight", getWidthWeigh());

      dc.nl();
      dc.append("Width of chars : ");
      dc.appendVarWithSpace("space", charWidth(' '));
      dc.appendVarWithSpace("m", charWidth('m'));
      dc.appendVarWithSpace("i", charWidth('i'));
      dc.appendVarWithSpace("_", charWidth('_'));
      dc.nl();
      dc.append("Width of strings : ");
      dc.appendVarWithSpace("mi", stringWidth("mi"));
      dc.appendVarWithSpace("m i", stringWidth("m i"));

      dc.nl();

      dc.root(font, "java.awt.Font");
      dc.appendVarWithSpace("name", font.getName());
      dc.appendVarWithSpace("fontName", font.getFontName());
      dc.appendVarWithSpace("Family", font.getFamily());
      dc.appendVarWithSpace("PostScriptName", font.getPSName());
      dc.nl();
      dc.appendVarWithSpace("Size", font.getSize());
      dc.appendVarWithSpace("Style", font.getStyle());
      dc.appendVarWithSpace("NumGlyphs", font.getNumGlyphs());
      dc.nl();
      dc.appendVarWithSpace("ItalicAngle", font.getItalicAngle());

      dc.nl();
      Attribute[] availableAttributes = font.getAvailableAttributes();
      dc.appendVar("AvailableAttributes", availableAttributes.length);
      for (int i = 0; i < availableAttributes.length; i++) {
         dc.nl();
         Attribute at = availableAttributes[i];
         if (at instanceof TextAttribute) {
            TextAttribute tat = (TextAttribute) at;
            dc.appendVarWithSpace("TextAttribute toString", tat.toString());

         } else {
            dc.appendVarWithSpace("Attribute toString", at.toString());
         }

      }
   }

   private void toStringPrivate(Dctx dc) {

   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, FontSwing.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   //#enddebug

}
