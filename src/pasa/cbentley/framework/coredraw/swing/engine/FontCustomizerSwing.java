package pasa.cbentley.framework.coredraw.swing.engine;

import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.coredraw.j2se.engine.FontCustomizerJ2SE;
import pasa.cbentley.framework.coredraw.swing.ctx.CoreDrawSwingCtx;

public class FontCustomizerSwing extends FontCustomizerJ2SE {

   private final CoreDrawSwingCtx cdwc;

   public FontCustomizerSwing(CoreDrawSwingCtx cdc, FontFactorySwing factory) {
      super(cdc, factory);
      this.cdwc = cdc;
   }

   public void loadFont(String path) {
      factory.loadFont(path);
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, FontCustomizerSwing.class, "@line5");
      toStringPrivate(dc);
      super.toString(dc.sup());
   }

   private void toStringPrivate(Dctx dc) {

   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, FontCustomizerSwing.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   //#enddebug

}
