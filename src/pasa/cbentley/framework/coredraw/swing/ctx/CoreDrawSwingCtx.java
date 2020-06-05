/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.coredraw.swing.ctx;

import javax.swing.SwingUtilities;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.BOCtx;
import pasa.cbentley.byteobjects.src4.ctx.IConfigBO;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.coredraw.j2se.ctx.CoreDrawJ2seCtx;
import pasa.cbentley.framework.coredraw.src4.ctx.IConfigCoreDraw;
import pasa.cbentley.framework.coredraw.src4.interfaces.IFontFactory;
import pasa.cbentley.framework.coredraw.src4.interfaces.IImageFactory;
import pasa.cbentley.framework.coredraw.src4.interfaces.IScaler;
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
public class CoreDrawSwingCtx extends CoreDrawJ2seCtx {

   public static final int      CTX_ID = 4991;

   private FontFactorySwing     factoryFont;

   private ImageFactorySwing    factoryImage;

   private ScalerSwing          scaler;

   private IConfigCoreDrawSwing configDrawSwing;

   /**
    * Uses default {@link ConfigCoreDrawSwingDefault} configuration.
    * @param boc
    */
   public CoreDrawSwingCtx(BOCtx boc) {
      this(null, boc);
   }

   /**
    * 
    * @param configDraw
    * @param boc
    */
   public CoreDrawSwingCtx(IConfigCoreDrawSwing configDraw, BOCtx boc) {
      super((configDraw == null) ? new ConfigCoreDrawSwingDefault(boc.getUCtx()) : configDraw, boc);
      this.configDrawSwing = (IConfigCoreDrawSwing) getConfig(); //use getter in case null parameter we get the default config
      factoryFont = new FontFactorySwing(this);
      factoryImage = new ImageFactorySwing(this);
      scaler = new ScalerSwing(this);

      if (this.getClass() == CoreDrawSwingCtx.class) {
         a_Init();
      }
   }

   public IConfigCoreDrawSwing getConfigCoreDrawSwing() {
      return configDrawSwing;
   }

   public FontFactorySwing getFontFactorySwing() {
      return factoryFont;
   }

   public ImageFactorySwing getImageFactorySwing() {
      return factoryImage;
   }

   public IImageFactory getImageFactory() {
      return factoryImage;
   }

   public IFontFactory getFontFactory() {
      return factoryFont;
   }

   public boolean hasFeatureSupport(int supportID) {
      return super.hasFeatureSupport(supportID);
   }

   protected String getDefaultFontNameMonoSub() {
      String[] availableFontFamilyNames = factoryFont.getAvailableFontFamilyNames();

      return availableFontFamilyNames[0];
   }

   protected void matchConfig(IConfigBO config, ByteObject settings) {
      super.matchConfig(config, settings);
   }

   protected String getDefaultFontNameSystemSub() {
      return "Courier New";
   }

   public void callSerially(Runnable run) {
      SwingUtilities.invokeLater(run);
   }

   protected String getDefaultFontNamePropSub() {
      return "Courier New";
   }

   public IScaler getScaler() {
      return scaler;
   }

   public int getCtxID() {
      return CTX_ID;
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, CoreDrawSwingCtx.class);
      toStringPrivate(dc);
      super.toString(dc.sup());
   }

   private void toStringPrivate(Dctx dc) {

   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, CoreDrawSwingCtx.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   //#enddebug

}
