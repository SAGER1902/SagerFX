package safx.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;
/*import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;*/
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import safx.SagerFX;

import net.minecraft.network.chat.Component;
import net.minecraftforge.event.TickEvent;

import java.util.Arrays;

import safx.client.ClientProxy;
import org.lwjgl.opengl.GL13;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.culling.Frustum;
import org.joml.Matrix4f;

import safx.FXConfig;
@Mod.EventBusSubscriber(modid = SagerFX.MODID)
public class SAEventHandler {
    /*private static final List<RenderLevelStageEvent.Stage> STAGES = Arrays.asList(
        RenderLevelStageEvent.Stage.AFTER_SKY,
        RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS,
        RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS,
        RenderLevelStageEvent.Stage.AFTER_WEATHER,
        RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS,
        RenderLevelStageEvent.Stage.AFTER_PARTICLES,
        RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS
    );
	
    private static int currentStageIndex = 0;
    private static int timer = 0;
    private static final int SWITCH_INTERVAL = 100; // 切换间隔（ ticks，20 ticks = 1秒）
    private static boolean isTesting = true;
	
    public static void toggleTesting() {
        isTesting = !isTesting;
        Minecraft.getInstance().player.displayClientMessage(
            Component.literal("渲染阶段测试: " + (isTesting ? "开启" : "关闭")),
            false
        );
        
        if (isTesting) {
            announceCurrentStage();
        }
    }
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !isTesting) return;
        
        timer++;
        if (timer >= SWITCH_INTERVAL) {
            timer = 0;
            switchToNextStage();
        }
    }
	
    // 切换到下一个阶段
    private static void switchToNextStage() {
        currentStageIndex = (currentStageIndex + 1) % STAGES.size();
        announceCurrentStage();
    }
	
    // 向玩家发送当前阶段信息
    private static void announceCurrentStage() {
        if (Minecraft.getInstance().player == null) return;
        
        RenderLevelStageEvent.Stage currentStage = STAGES.get(currentStageIndex);
        Minecraft.getInstance().player.displayClientMessage(
            Component.literal("测试渲染阶段: " + getStageName(currentStage)),
            false
        );
    }
    
    // 获取阶段名称
    private static String getStageName(RenderLevelStageEvent.Stage stage) {
        // 这里需要根据你的Minecraft版本调整阶段名称
        // 以下是1.20.1版本的阶段名称
        if (stage == RenderLevelStageEvent.Stage.AFTER_SKY) return "AFTER_SKY";
        if (stage == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) return "AFTER_SOLID_BLOCKS";
        if (stage == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) return "AFTER_CUTOUT_BLOCKS";
        if (stage == RenderLevelStageEvent.Stage.AFTER_WEATHER) return "AFTER_WEATHER";
        if (stage == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) return "AFTER_TRIPWIRE_BLOCKS";
        if (stage == RenderLevelStageEvent.Stage.AFTER_PARTICLES) return "AFTER_PARTICLES";
        if (stage == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return "AFTER_TRANSLUCENT_BLOCKS";
        return "UNKNOWN";
    }*/
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onRenderWorldLast(RenderLevelStageEvent event) {
        /*if (!isTesting) return;
        RenderLevelStageEvent.Stage currentStage = STAGES.get(currentStageIndex);
        if (event.getStage() != currentStage) {
            return;
        }*/
		
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) {
			return;
		}
		Minecraft mc = Minecraft.getInstance();
		if(mc.getFps()<FXConfig.render_fps)return;
		
		float partialTicks = mc.getFrameTime();
		Camera camera = event.getCamera();
		PoseStack stack = event.getPoseStack();
		//PoseStack stack1 = event.getPoseStack().last().pose();
		
        /*Matrix4f projectionMatrix = event.getProjectionMatrix();  // 或从 GameRenderer 获取
        //Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        // 构造视锥体
        Frustum frustum = new Frustum(projectionMatrix, stack.last().pose());
        frustum.prepare(cameraPos.x, cameraPos.y, cameraPos.z);*/
		
		ClientProxy.get().particleManager.renderParticles(mc.getCameraEntity(), partialTicks, camera, stack/*, frustum*/);
		RenderSystem.disableBlend();
		RenderSystem.enableDepthTest();
		RenderSystem.depthMask(true);
		RenderSystem.enableCull();
	}
}
