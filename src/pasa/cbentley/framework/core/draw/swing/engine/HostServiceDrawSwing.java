package pasa.cbentley.framework.core.draw.swing.engine;

import pasa.cbentley.framework.core.draw.j2se.engine.HostServiceDrawJ2se;
import pasa.cbentley.framework.core.draw.swing.ctx.CoreDrawSwingCtx;

public class HostServiceDrawSwing extends HostServiceDrawJ2se {

   public HostServiceDrawSwing(CoreDrawSwingCtx cdc) {
      super(cdc);
   }

   public boolean isHostServiceActive(int serviceID) {
      switch (serviceID) {
         default:
            return super.isHostServiceActive(serviceID);
      }
   }

   public boolean isHostServiceSupported(int serviceID) {
      switch (serviceID) {
         default:
            return isHostServiceSupported(serviceID);
      }
   }

   public boolean setHostServiceActive(int serviceID, boolean isActive) {
      switch (serviceID) {
         default:
            return super.setHostServiceActive(serviceID, isActive);
      }
   }
}
