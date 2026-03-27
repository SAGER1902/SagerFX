package safx.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import net.minecraft.client.Minecraft;

import net.minecraft.client.entity.player.ClientPlayerEntity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import safx.SagerFX;

import safx.client.ClientProxy;
import org.lwjgl.opengl.GL13;
import safx.client.render.GLStateSnapshot;

@Mod.EventBusSubscriber(modid = SagerFX.MODID)
public class SAEventHandler {
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onRenderWorldLast(RenderWorldLastEvent event) {
		
		String[] parts = Minecraft.getInstance().fpsString.split(" ");
        int fps = Integer.parseInt(parts[0]);
		
		if(fps>4){
			ClientProxy.get().particleManager.renderParticles(Minecraft.getInstance().getCameraEntity(), event.getPartialTicks());
		}
		/*GlStateManager._disableBlend();
		GlStateManager._enableDepthTest();
		GlStateManager._depthMask(true);
		GlStateManager._enableCull();
		GlStateManager._disableLighting();
		GlStateManager._glMultiTexCoord2f(GL13.GL_TEXTURE1,0, 240);*/
	}
}
