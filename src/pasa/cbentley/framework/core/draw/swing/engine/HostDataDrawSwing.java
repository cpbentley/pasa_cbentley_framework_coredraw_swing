package pasa.cbentley.framework.core.draw.swing.engine;

import pasa.cbentley.core.src4.interfaces.IHostData;
import pasa.cbentley.framework.core.draw.j2se.engine.HostDataDrawJ2se;
import pasa.cbentley.framework.core.draw.swing.ctx.CoreDrawSwingCtx;
import pasa.cbentley.framework.coredraw.src4.ctx.ObjectCDC;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechHostDataDraw;

/**
 * This class deals with all HostData, from Draw, UI and Core.
 * 
 * If you want a class limited to Draw, you 
 * @author Charles Bentley
 *
 */
public class HostDataDrawSwing extends HostDataDrawJ2se implements IHostData, ITechHostDataDraw {

   public HostDataDrawSwing(CoreDrawSwingCtx cdc) {
      super(cdc);
   }

   public float getHostDataFloat(int dataID) {
      switch (dataID) {
         default:
            return super.getHostDataFloat(dataID);
      }
   }

   public int getHostDataInt(int dataID) {
      switch (dataID) {
         default:
            return super.getHostDataInt(dataID);
      }
   }

   public Object getHostDataObject(int dataID) {
      switch (dataID) {
         default:
            return super.getHostDataObject(dataID);
      }
   }

   public String getHostDataString(int dataID) {
      switch (dataID) {
         case DATA_ID_STR_05_HOSTNAME:
            return "Swing";
         default:
            return super.getHostDataString(dataID);
      }
   }

}
