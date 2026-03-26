package safx.client.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class SARenderHelper {//this from techguns
	protected static Uniform lastBrightnessX;
	protected static Uniform lastBrightnessY;
	public static void enableFXLighting() {
		/*// 获取当前 Shader 的光照参数（需根据实际 Shader 调整）
		ShaderInstance shader = RenderSystem.getShader();
		if (shader != null) {
			lastBrightnessX = shader.getUniform("Light0_Direction");
			lastBrightnessY = shader.getUniform("Light1_Direction");
		}
		// 设置高亮度光照参数（通过 Shader Uniform）
		//RenderSystem.setShader(GameRenderer::getRenderTypeEntityTranslucentEmissiveShader());
		RenderSystem.setShaderTexture(1, ResourceLocation.tryParse("textures/block/white_concrete.png")); // 示例纹理
		RenderSystem.setShaderColor(240f / 255f, 240f / 255f, 240f / 255f, 1.0f);*/
	}
	public static void disableFXLighting() {
		//RenderSystem.setShader(GameRenderer::getRenderTypeEntityTranslucentShader());
		/*RenderSystem.setShaderTexture(1, ResourceLocation.tryParse("textures/block/stone.png")); // 恢复默认纹理
		RenderSystem.setShaderColor(
			lastBrightnessX.getCount() / 240f,
			lastBrightnessY.getCount() / 240f,
			1.0f,
			1.0f
		);*/
	}
    public enum RenderTypeSA {
    	ALPHA, ADDITIVE, SOLID, ALPHA_SHADED, NO_Z_TEST;
    }
	public static void enableBlendMode(RenderTypeSA type) {
		//RenderSystem.disableLighting(); // 替代 GL11.glDisable(GL11.GL_LIGHTING)
		if (type != RenderTypeSA.SOLID) {
			RenderSystem.enableBlend();
			RenderSystem.depthMask(false);
		}
		
		if (type == RenderTypeSA.ALPHA) { // 替代 RenderTypeSA.ALPHA
			RenderSystem.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, 
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, 
				GlStateManager.SourceFactor.ONE, 
				GlStateManager.DestFactor.ZERO
			);
		} else if (type == RenderTypeSA.ADDITIVE || type == RenderTypeSA.NO_Z_TEST) { 
			RenderSystem.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, 
				GlStateManager.DestFactor.ONE, 
				GlStateManager.SourceFactor.ONE, 
				GlStateManager.DestFactor.ZERO
			);
		}
		
		if (type == RenderTypeSA.NO_Z_TEST) { // 替代 NO_Z_TEST
			RenderSystem.depthMask(false);
			RenderSystem.enableDepthTest();
		}
		
		if (type != RenderTypeSA.ALPHA_SHADED) { // 替代 ALPHA_SHADED
			SARenderHelper.enableFXLighting();
		}
	}

	public static void disableBlendMode(RenderTypeSA type) {
		if (type != RenderTypeSA.ALPHA_SHADED) { // 替代 ALPHA_SHADED
			SARenderHelper.disableFXLighting();
		}
		
		if (type != RenderTypeSA.SOLID) {
			RenderSystem.disableBlend();
			RenderSystem.depthMask(true);
		}
		
		if (type == RenderTypeSA.ADDITIVE || type == RenderTypeSA.NO_Z_TEST) {
			RenderSystem.defaultBlendFunc();
		}
		
		if (type == RenderTypeSA.NO_Z_TEST) { // 替代 NO_Z_TEST
			RenderSystem.depthMask(true);
			RenderSystem.enableDepthTest();
		}
	}
}
