package safx.client.render;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
//import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL13;

import com.mojang.blaze3d.systems.RenderSystem;
public class SARenderHelper {//this from techguns

	protected static float lastBrightnessX=0;
	protected static float lastBrightnessY=0;
	
	protected static int lastBlendFuncSrc=0;
	protected static int lastBlendFuncDest=0;

	public static void enableFXLighting()
    {
    	lastBrightnessX= GlStateManager.lastBrightnessX;
		lastBrightnessY= GlStateManager.lastBrightnessY;

		//GlStateManager._disableLighting();
		GlStateManager._glMultiTexCoord2f(GL13.GL_TEXTURE1, 240f, 240f);
    }
	
    public static void disableFXLighting()
    {
    	//GlStateManager._enableLighting();
    	GlStateManager._glMultiTexCoord2f(GL13.GL_TEXTURE1, lastBrightnessX, lastBrightnessY);
    }
    
    public static void enableFluidGlow(int luminosity) {
    	lastBrightnessX= GlStateManager.lastBrightnessX;
		lastBrightnessY= GlStateManager.lastBrightnessY;
		
		float newLightX = Math.min((luminosity/15.0f)*240.0f + lastBrightnessX, 240.0f);
		float newLightY = Math.min((luminosity/15.0f)*240.0f + lastBrightnessY, 240.0f);
		
    	GlStateManager._glMultiTexCoord2f(GL13.GL_TEXTURE1, newLightX, newLightY);
    }
    
    public static void disableFluidGlow() {
    	GlStateManager._glMultiTexCoord2f(GL13.GL_TEXTURE1, lastBrightnessX, lastBrightnessY);
    }

    public enum RenderType {
    	ALPHA, ADDITIVE, SOLID, ALPHA_SHADED, NO_Z_TEST;
    }
	
    /**
     * This includes FXLighting!
     */
    public static void enableBlendMode(RenderType type) {
		GL11.glDisable(GL11.GL_LIGHTING);//关闭阴影
    	if (type != RenderType.SOLID) {
    		GlStateManager._enableBlend();
    		GlStateManager._depthMask(false);
    	}
        if (type == RenderType.ALPHA) {
        	//GlStateManager._blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			//RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, 
			GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			//RenderSystem.alphaFunc(516, 0.003921569F);
        } else if (type == RenderType.ADDITIVE || type==RenderType.NO_Z_TEST) {
        	//GlStateManager._blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
			//RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, 
			GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			//RenderSystem.alphaFunc(516, 0.003921569F);
        }
        
        if (type==RenderType.NO_Z_TEST){
        	GlStateManager._depthMask(false);
        	GlStateManager._enableDepthTest();//_disableDepth
        }
        if (type != RenderType.ALPHA_SHADED) SARenderHelper.enableFXLighting();
	}
	
    /**
     * This includes FXLighting!
     */
	public static void disableBlendMode(RenderType type) {
		if (type != RenderType.ALPHA_SHADED) SARenderHelper.disableFXLighting();
		if (type != RenderType.SOLID) {
    		GlStateManager._disableBlend();
    		GlStateManager._depthMask(true);
    	}
		if (type == RenderType.ALPHA) {
        	//GlStateManager._blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			RenderSystem.defaultBlendFunc();
			//RenderSystem.disableBlend();
        } else if (type == RenderType.ADDITIVE || type==RenderType.NO_Z_TEST) {
        	//GlStateManager._blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			RenderSystem.defaultBlendFunc();
			//RenderSystem.disableBlend();
        }
		
        if (type==RenderType.NO_Z_TEST){
        	GlStateManager._depthMask(true);
        	GlStateManager._enableDepthTest();
        }
		//GL11.glEnable(GL11.GL_LIGHTING);
	}
}
