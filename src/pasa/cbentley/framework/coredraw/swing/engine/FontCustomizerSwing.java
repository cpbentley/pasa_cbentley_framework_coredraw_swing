package pasa.cbentley.framework.coredraw.swing.engine;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

import pasa.cbentley.core.swing.stringables.StringableFontAwt;
import pasa.cbentley.framework.coredraw.j2se.engine.FontCustomizerJ2SE;
import pasa.cbentley.framework.coredraw.swing.ctx.CoreDrawSwingCtx;

public class FontCustomizerSwing extends FontCustomizerJ2SE {

   private final CoreDrawSwingCtx cdwc;

   public FontCustomizerSwing(CoreDrawSwingCtx cdc, FontFactorySwing factory) {
      super(cdc, factory);
      this.cdwc = cdc;
   }

   public void loadFont(String path) {
      InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
      loadFont(is, path);
   }

   public StringableFontAwt getFontD(Font font) {
      return new StringableFontAwt(cdwc.getSwingCoreCtx(), font);
   }

   public void loadFont(InputStream is, String path) {
      if (is == null) {
         //#debug
         toDLog().pNull("Null InputStream for " + path, this, FontCustomizerSwing.class, "loadFont", LVL_05_FINE, true);
         return;
      }
      try {
         Font font = Font.createFont(Font.TRUETYPE_FONT, is);
         if (font == null) {
            //#debug
            toDLog().pNull("awt.Font is null for " + path, this, FontCustomizerSwing.class, "loadFont", LVL_05_FINE, true);
            return;
         }

         boolean registerFont = GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
         //#debug
         toDLog().pInit("" + path + " registerFont=" + registerFont, getFontD(font), FontFactorySwing.class, "loadFont", LVL_05_FINE, true);

      } catch (FontFormatException | IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

}
