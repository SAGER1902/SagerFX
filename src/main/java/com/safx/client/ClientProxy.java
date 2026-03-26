package safx.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import safx.CommonProxy;
import safx.SagerFX;

import safx.client.particle.SAFX;
import safx.client.particle.SAParticleManager;
import safx.client.particle.SAParticleSystem;

import safx.util.EntityCondition;
import safx.util.MathUtil;

import net.minecraft.client.multiplayer.ClientLevel;

import safx.FXConfig;
@Mod.EventBusSubscriber(Dist.CLIENT)
//@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {
	
	public SAParticleManager particleManager = new SAParticleManager();
	/**
	 * if local player has pressed the fire key this tick
	 */
	public boolean keyFirePressedMainhand;
	public boolean keyFirePressedOffhand;

	@OnlyIn(Dist.CLIENT)
	public float player_zoom=1.0f;
	
	//client side safeguard to prevent multiple reloadsounds overlapping caused by deyncs
	public long lastReloadsoundPlayed=0L;
	
	//local player First person muzzle flash timing
	protected long player_muzzleFlashtime_right=0;
	protected long player_muzzleFlashtime_total_right=0;
	protected long player_muzzleFlashtime_left=0;
	protected long player_muzzleFlashtime_total_left=0;
	
	public float PARTIAL_TICK_TIME;
	//local muzzle flash jitter offsets
	public float muzzleFlashJitterX = 0; //-1.0 to 1.0
	public float muzzleFlashJitterY = 0; //-1.0 to 1.0
	public float muzzleFlashJitterAngle = 0; //-1.0 to 1.0
	public float muzzleFlashJitterScale = 0; //-1.0 to 1.0
	
	public boolean hasStepassist=false;
	
	public boolean hasNightvision=false;

	private boolean isLeft(InteractionHand handIn){
		if(this.getPlayerClient().getMainArm()==HumanoidArm.RIGHT){
			return handIn == InteractionHand.OFF_HAND;
		} else {
			return handIn == InteractionHand.MAIN_HAND;
		}
	}
	
	private boolean isLeft(boolean left){
		if(this.getPlayerClient().getMainArm()==HumanoidArm.RIGHT){
			return left;
		} else {
			return !left;
		}
	}
	@Override
	public void preInit(){
		SAFX.loadFXList();
	}
	@Override
	public void init(){
	}
	@Override
	public void postInit() {
	}

	public static ClientProxy get(){
		return (ClientProxy) SagerFX.proxy;
	}

	@Override
	public Player getPlayerClient() {
		return Minecraft.getInstance().player;
	}
	
	@Override
	public void createFX(String name, ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ){	
		Minecraft mc = Minecraft.getInstance();
		if(mc.getFps()<FXConfig.render_fps)return;
		if (mc.player == null) {
			return;
		}
		double deltaX = x - mc.player.getX();
		double deltaY = y - mc.player.getY();
		double deltaZ = z - mc.player.getZ();
		double distanceSq = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
		if (distanceSq > FXConfig.render_range*FXConfig.render_range) {
			return;
		}
		List<SAParticleSystem> systems = SAFX.createFX(world, name, x, y, z, motionX, motionY, motionZ);
		if (systems!=null) {
			systems.forEach(s -> particleManager.addEffect(s));//Minecraft.getInstance().effectRenderer.addEffect(s));
		}
	}
	
	@Override
	public void createFX(String name, ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, float pitch, float yaw){
		Minecraft mc = Minecraft.getInstance();
		if(mc.getFps()<FXConfig.render_fps)return;
		if (mc.player == null) {
			return;
		}
		double deltaX = x - mc.player.getX();
		double deltaY = y - mc.player.getY();
		double deltaZ = z - mc.player.getZ();
		double distanceSq = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
		if (distanceSq > FXConfig.render_range*FXConfig.render_range) {
			return;
		}
		List<SAParticleSystem> systems = SAFX.createFX(world, name, x, y, z, motionX, motionY, motionZ);
		if (systems!=null) {
			for (SAParticleSystem s : systems) {
				s.xRot = pitch;
				s.yRot = yaw;
				particleManager.addEffect(s);
			}
		}
	}
	
	@Override
	public void createFX(String name, ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, float scale){
		Minecraft mc = Minecraft.getInstance();
		if(mc.getFps()<FXConfig.render_fps)return;
		if (mc.player == null) {
			return;
		}
		double deltaX = x - mc.player.getX();
		double deltaY = y - mc.player.getY();
		double deltaZ = z - mc.player.getZ();
		double distanceSq = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
		if (distanceSq > FXConfig.render_range*FXConfig.render_range) {
			return;
		}
		List<SAParticleSystem> systems = SAFX.createFX(world, name, x, y, z, motionX, motionY, motionZ);
		if (systems!=null) {
			for (SAParticleSystem s : systems) {
				s.scale = scale;
				particleManager.addEffect(s);
			}
		}
	}
	
	@Override
	public void createFXOnEntity(String name, Entity ent) {
		Minecraft mc = Minecraft.getInstance();
		if(mc.getFps()<FXConfig.render_fps)return;
		if (mc.player == null) {
			return;
		}
		/*double deltaX = ent.getX() - mc.player.getX();
		double deltaY = ent.getY() - mc.player.getY();
		double deltaZ = ent.getZ() - mc.player.getZ();
		double distanceSq = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
		if (distanceSq > FXConfig.render_range*FXConfig.render_range) {
			return;
		}*/
		List<SAParticleSystem> systems = SAFX.createFXOnEntity(ent, name);
		if (systems!=null) {
			systems.forEach(s -> {
				s.condition=EntityCondition.ENTITY_ALIVE;
				particleManager.addEffect(s);
			}); //Minecraft.getInstance().effectRenderer.addEffect(s));
		}
	}
	
	@Override
	public void createFXOnEntity(String name, Entity ent, float scale) {
		Minecraft mc = Minecraft.getInstance();
		if(mc.getFps()<FXConfig.render_fps)return;
		if (mc.player == null) {
			return;
		}
		/*double deltaX = ent.getX() - mc.player.getX();
		double deltaY = ent.getY() - mc.player.getY();
		double deltaZ = ent.getZ() - mc.player.getZ();
		double distanceSq = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
		if (distanceSq > FXConfig.render_range*FXConfig.render_range) {
			return;
		}*/
		List<SAParticleSystem> systems = SAFX.createFXOnEntity(ent, name);
		if (systems!=null) {
			for (SAParticleSystem s : systems) {
				s.scale = scale;
				s.condition=EntityCondition.ENTITY_ALIVE;
				particleManager.addEffect(s);
			}
		}
	}
	
	@Override
	public void createFXOnEntityWithOffset(String name, Entity ent, float offsetX, float offsetY, float offsetZ, boolean attachToHead, EntityCondition condition) {
		Minecraft mc = Minecraft.getInstance();
		if(mc.getFps()<FXConfig.render_fps)return;
		if (mc.player == null) {
			return;
		}
		/*double deltaX = ent.getX() - mc.player.getX();
		double deltaY = ent.getY() - mc.player.getY();
		double deltaZ = ent.getZ() - mc.player.getZ();
		double distanceSq = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
		if (distanceSq > FXConfig.render_range*FXConfig.render_range) {
			return;
		}*/
		List<SAParticleSystem> systems = SAFX.createFXOnEntity(ent, name);
		if (systems!=null) {
			for (SAParticleSystem s : systems) {
				s.entityOffset = new Vec3(offsetX, offsetY, offsetZ);
				s.attachToHead = attachToHead;
				s.condition = condition;
				particleManager.addEffect(s);
			}
		}
	}
	
	@Override
	public void setHasStepassist(boolean value) {
		this.hasStepassist=value;
	}

	@Override
	public void setHasNightvision(boolean value) {
		this.hasNightvision=value;
	}

	@Override
	public boolean getHasStepassist() {
		return this.hasStepassist;
	}

	@Override
	public boolean getHasNightvision() {
		return this.hasNightvision;
	}
	
	/*@Override
	public boolean isClientPlayerAndIn1stPerson(LivingEntity ent) {
		return ent == this.getPlayerClient() && Minecraft.getInstance().gameSettings.thirdPersonView == 0;
	}*/

	public String resolvePlayerNameFromUUID(UUID uuid){
		String name = UsernameCache.getLastKnownUsername(uuid);
		if (name!=null){
			return name;
		} else {
			return "UNKNOW_PLAYERNAME";
		}
	}
	
	/*@Override
	public boolean clientInRangeSquared(double x, double z, double distSq) {
		Player localPly = this.getPlayerClient();
		
		MathUtil.Vec2 posPly = new MathUtil.Vec2(localPly.x, localPly.z);
		MathUtil.Vec2 pos = new MathUtil.Vec2(x, z);
		
		return posPly.getVecTo(pos).lenSquared() <= distSq;
	}*/
	
	protected static ResourceLocation[] getTextures(String name, String ...suffixes) {
		ResourceLocation[] tex = new ResourceLocation[suffixes.length+1];
		tex[0]=ResourceLocation.tryParse(SagerFX.MODID+":"+name+".png");
		
		for(int i=1; i<=suffixes.length; i++) {
			tex[i] = ResourceLocation.tryParse(SagerFX.MODID+":"+name+"_"+suffixes[i-1]+".png");
		}
		return tex;
	}
}
