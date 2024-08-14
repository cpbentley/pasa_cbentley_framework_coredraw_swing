/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.core.draw.swing.ctx;

import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.core.draw.j2se.ctx.ConfigCoreDrawJ2seDef;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechGraphics;

public class ConfigCoreDrawSwingDef extends ConfigCoreDrawJ2seDef implements IConfigCoreDrawSwing {

   public ConfigCoreDrawSwingDef(UCtx uc) {
      super(uc);
   }

   public int getAliasModeText() {
      return ITechGraphics.MODSET_APP_ALIAS_0_BEST;
   }

   public int getAliasMode() {
      return ITechGraphics.MODSET_APP_ALIAS_0_BEST;
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, ConfigCoreDrawSwingDef.class, "@line5");
      toStringPrivate(dc);
      super.toString(dc.sup());
   }

   private void toStringPrivate(Dctx dc) {

   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, ConfigCoreDrawSwingDef.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   //#enddebug

}
