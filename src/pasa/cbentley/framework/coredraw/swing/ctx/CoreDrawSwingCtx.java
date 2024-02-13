/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.coredraw.swing.ctx;

import javax.swing.SwingUtilities;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.BOCtx;
import pasa.cbentley.byteobjects.src4.ctx.IConfigBO;
import pasa.cbentley.core.src4.interfaces.IFeaturable;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.swing.ctx.IFlagsToStringSwingCore;
import pasa.cbentley.core.swing.ctx.SwingCoreCtx;
import pasa.cbentley.framework.coredraw.j2se.ctx.CoreDrawJ2seCtx;
import pasa.cbentley.framework.coredraw.j2se.engine.FontCustomizerJ2SE;
import pasa.cbentley.framework.coredraw.src4.ctx.IFlagToStringCoreDraw;
import pasa.cbentley.framework.coredraw.src4.ctx.IBOCtxSettingsCoreDraw;
import pasa.cbentley.framework.coredraw.src4.interfaces.IFontCustomizer;
import pasa.cbentley.framework.coredraw.src4.interfaces.IFontFactory;
import pasa.cbentley.framework.coredraw.src4.interfaces.IImageFactory;
import pasa.cbentley.framework.coredraw.src4.interfaces.IScaler;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechFeaturesDraw;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechGraphics;
import pasa.cbentley.framework.coredraw.swing.engine.FontCustomizerSwing;
import pasa.cbentley.framework.coredraw.swing.engine.FontFactorySwing;
import pasa.cbentley.framework.coredraw.swing.engine.ImageFactorySwing;
import pasa.cbentley.framework.coredraw.swing.engine.ScalerSwing;

/**
 * This ctx does not require SwingCtx.
 * 
 * We want to be able to use it with minimal dependencies.
 * 
 * @author Charles Bentley
 *
 */
public class CoreDrawSwingCtx extends CoreDrawJ2seCtx implements IFeaturable {

   public static final int      CTX_ID = 4991;

   private IConfigCoreDrawSwing configDrawSwing;

   private FontFactorySwing     factoryFont;

   private ImageFactorySwing    factoryImage;

   private FontCustomizerSwing  fontCustomizerSwing;

   protected final SwingCoreCtx sc;

   private ScalerSwing          scaler;

   /**
    * 
    * @param config when null ConfigCoreDrawSwingDefault is used
    * @param boc
    */
   public CoreDrawSwingCtx(IConfigCoreDrawSwing config, SwingCoreCtx sc, BOCtx boc) {
      super(config == null ? new ConfigCoreDrawSwingDef(boc.getUC()) : config, sc, boc);
      this.sc = sc;
      this.configDrawSwing = (IConfigCoreDrawSwing) getConfig(); //use getter in case null parameter we get the default config
      factoryFont = new FontFactorySwing(this);
      factoryImage = new ImageFactorySwing(this);
      scaler = new ScalerSwing(this);

      if (this.getClass() == CoreDrawSwingCtx.class) {
         a_Init();
      }

      //#debug
      toDLog().pInit("", this, CoreDrawSwingCtx.class, "Created@75", LVL_04_FINER, true);

   }

   /**
    * Uses default {@link ConfigCoreDrawSwingDef} configuration.
    * @param boc
    */
   public CoreDrawSwingCtx(SwingCoreCtx sc, BOCtx boc) {
      this(new ConfigCoreDrawSwingDef(boc.getUCtx()), sc, boc);
   }

   public void callSerially(Runnable run) {
      SwingUtilities.invokeLater(run);
   }

   public boolean featureEnable(int featureID, boolean b) {
      switch (featureID) {
         case ITechFeaturesDraw.SUP_ID_03_OPEN_GL:
            return false;
         case ITechFeaturesDraw.SUP_ID_04_ALIAS:
            //
            int v = ITechGraphics.MODSET_APP_ALIAS_2_OFF;
            if (b) {
               v = ITechGraphics.MODSET_APP_ALIAS_0_BEST;
            }
            getBOCtxSettings().set1(CTX_COREDRAW_OFFSET_02_MODE_ALIAS1, v);
            applySettingsAlias();
            return true;
         case ITechFeaturesDraw.SUP_ID_05_ALIAS_TEXT:
            int va = ITechGraphics.MODSET_APP_ALIAS_2_OFF;
            if (b) {
               va = ITechGraphics.MODSET_APP_ALIAS_0_BEST;
            }
            getBOCtxSettings().set1(CTX_COREDRAW_OFFSET_03_MODE_TEXT_ALIAS1, va);
            //generate event
            applySettingsAlias();
            return true;
         default:
            return super.featureEnable(featureID, b);
      }
   }

   public IConfigCoreDrawSwing getConfigCoreDrawSwing() {
      return configDrawSwing;
   }

   public int getCtxID() {
      return CTX_ID;
   }

   public int getFeatureInt(int featureID) {
      //nothing here right now
      switch (featureID) {
         case SUP_ID_07_IMAGE_SCALING:
            break;
         case SUP_ID_06_CUSTOM_FONTS:
            break;
         default:
            break;
      }
      return 0;
   }

   /**
    * Returns {@link IFontCustomizer} for {@link ITechFeaturesDraw#SUP_ID_06_CUSTOM_FONTS}
    */
   public Object getFeatureObject(int featureID) {
      if (featureID == ITechFeaturesDraw.SUP_ID_06_CUSTOM_FONTS) {
         return getFontCustomizerSwingLazy();
      }
      return null;
   }

   public FontCustomizerJ2SE getFontCustomizerJ2SE() {
      return getFontCustomizerSwingLazy();
   }

   public FontCustomizerSwing getFontCustomizerSwingLazy() {
      if (fontCustomizerSwing == null) {
         fontCustomizerSwing = new FontCustomizerSwing(this, factoryFont);
      }
      return fontCustomizerSwing;
   }

   public IFontFactory getFontFactory() {
      return factoryFont;
   }


   public IImageFactory getImageFactory() {
      return factoryImage;
   }

   public ImageFactorySwing getImageFactorySwing() {
      return factoryImage;
   }

   public IScaler getScaler() {
      return scaler;
   }

   public SwingCoreCtx getSwingCoreCtx() {
      return sc;
   }

   /**
    * <p>
    * 
    * This Swing does support for
    * 
    * <li> {@link ITechFeaturesDraw#SUP_ID_03_OPEN_GL}
    * <li> {@link ITechFeaturesDraw#SUP_ID_04_ALIAS}
    * <li> {@link ITechFeaturesDraw#SUP_ID_05_ALIAS_TEXT}
    * 
    * </p>
    * 
    * <p>
    * See feature support of J2SE for more at {@link CoreDrawJ2seCtx}{@link #hasFeatureSupport(int)}
    * </p>
    */
   public boolean hasFeatureSupport(int featureID) {
      switch (featureID) {
         case ITechFeaturesDraw.SUP_ID_03_OPEN_GL:
            return true;
         case ITechFeaturesDraw.SUP_ID_04_ALIAS:
            return true;
         case ITechFeaturesDraw.SUP_ID_05_ALIAS_TEXT:
            return true;
         default:
            return super.hasFeatureSupport(featureID);
      }
   }

   public boolean isFeatureEnabled(int featureID) {
      switch (featureID) {
         case ITechFeaturesDraw.SUP_ID_03_OPEN_GL:
            return false;
         case ITechFeaturesDraw.SUP_ID_04_ALIAS:
            return getBOCtxSettings().get1(CTX_COREDRAW_OFFSET_02_MODE_ALIAS1) != ITechGraphics.MODSET_APP_ALIAS_2_OFF;
         case ITechFeaturesDraw.SUP_ID_05_ALIAS_TEXT:
            return getBOCtxSettings().get1(CTX_COREDRAW_OFFSET_03_MODE_TEXT_ALIAS1) != ITechGraphics.MODSET_APP_ALIAS_2_OFF;
         default:
            return super.isFeatureEnabled(featureID);
      }
   }

   protected void matchConfig(IConfigBO config, ByteObject settings) {
      super.matchConfig(config, settings);
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, CoreDrawSwingCtx.class);
      toStringPrivate(dc);
      super.toString(dc.sup());
      dc.nlLvl(configDrawSwing, "configDrawSwing");
      dc.nlLvl(fontCustomizerSwing, "fontCustomizerSwing");
      dc.nlLvl(factoryFont, "factoryFont");
      dc.nlLvl(factoryImage, "factoryImage");
      dc.nlLvl(scaler, "scaler");
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, CoreDrawSwingCtx.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());

   }

   public void toStringFlagSetOn(int flag, boolean b, Dctx dctx) {
      if (flag == IFlagToStringCoreDraw.TOSTRING_FLAG_3_IGNORE_FONT_ATTRIBUTES) {
         dctx.setFlagData(sc, IFlagsToStringSwingCore.TOSTRING_FLAG_3_SHOW_FONT_ATTRIBUTES, b);
      }
      if (flag == IFlagToStringCoreDraw.TOSTRING_FLAG_4_SHOW_FONT_ENVIRONEMT) {
         dctx.setFlagData(sc, IFlagsToStringSwingCore.TOSTRING_FLAG_4_SHOW_FONT_ENVIRONEMT, b);
      }
   }

   private void toStringPrivate(Dctx dc) {

   }

   //#enddebug

}
