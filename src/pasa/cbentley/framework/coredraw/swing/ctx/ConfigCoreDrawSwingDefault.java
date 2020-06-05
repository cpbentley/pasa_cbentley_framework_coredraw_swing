/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.coredraw.swing.ctx;

import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.framework.coredraw.j2se.ctx.ConfigCoreDrawJ2se;
import pasa.cbentley.framework.coredraw.src4.ctx.IConfigCoreDraw;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechDrawer;

public class ConfigCoreDrawSwingDefault extends ConfigCoreDrawJ2se implements IConfigCoreDrawSwing {



   public ConfigCoreDrawSwingDefault(UCtx uc) {
      super(uc);
   }
   

   public int getAliasMode() {
      return ITechDrawer.MODSET_APP_ALIAS_0_BEST;
   }

}
