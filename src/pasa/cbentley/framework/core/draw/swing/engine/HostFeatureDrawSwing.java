package pasa.cbentley.framework.core.draw.swing.engine;

import pasa.cbentley.core.src4.interfaces.IHostFeature;
import pasa.cbentley.framework.core.draw.j2se.engine.HostFeatureDrawJ2se;
import pasa.cbentley.framework.core.draw.swing.ctx.CoreDrawSwingCtx;
import pasa.cbentley.framework.coredraw.src4.ctx.IBOCtxSettingsCoreDraw;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechGraphics;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechHostFeatureDraw;

/**
 * This class deals with all HostData, from Draw, UI and Core.
 * 
 * If you want a class limited to Draw, you 
 * @author Charles Bentley
 *
 */
public class HostFeatureDrawSwing extends HostFeatureDrawJ2se implements IHostFeature, ITechHostFeatureDraw, IBOCtxSettingsCoreDraw {

   public HostFeatureDrawSwing(CoreDrawSwingCtx cdc) {
      super(cdc);
   }

   public boolean setHostFeatureEnabled(int featureID, boolean b) {
      switch (featureID) {
         default:
            return super.setHostFeatureEnabled(featureID, b);
      }
   }

   public boolean setHostFeatureEnabledFactory(int featureID, boolean b) {
      switch (featureID) {
         default:
            return super.setHostFeatureEnabledFactory(featureID, b);
      }
   }

   public boolean isHostFeatureSupported(int featureID) {
      switch (featureID) {
         default:
            return super.isHostFeatureSupported(featureID);
      }
   }

   public boolean isHostFeatureEnabled(int featureID) {
      switch (featureID) {
         default:
            return super.isHostFeatureEnabled(featureID);
      }
   }

   public boolean isHostFeatureFactoryEnabled(int featureID) {
      switch (featureID) {
         default:
            return super.isHostFeatureFactoryEnabled(featureID);
      }
   }

}
