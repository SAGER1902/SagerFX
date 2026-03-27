package safx.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import net.minecraft.block.Block;

import net.minecraft.client.Minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
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

import net.minecraft.client.world.ClientWorld;
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
	
	//client side safeguard to prevent multiple reloadSounds overlapping caused by deyncs
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

	private boolean isLeft(Hand handIn){
		if(this.getPlayerClient().getMainArm()==HandSide.RIGHT){
			return handIn == Hand.OFF_HAND;
		} else {
			return handIn == Hand.MAIN_HAND;
		}
	}
	
	private boolean isLeft(boolean left){
		if(this.getPlayerClient().getMainArm()==HandSide.RIGHT){
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
	public PlayerEntity getPlayerClient() {
		return Minecraft.getInstance().player;
	}
	
	@Override
	public void createFX(String name, ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) {
			return;
		}
		double deltaX = x - mc.player.getX();
		double deltaY = y - mc.player.getY();
		double deltaZ = z - mc.player.getZ();
		double distanceSq = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ; // 使用距离平方避免开方运算
		if (distanceSq > 6400.0) {// 如果距离超过80格（80^2 = 6400），直接返回
			return;
		}
		/*// 3. （可选）简易的“前方视野”判断
		// 如果距离已经很近（例如20格内），无论如何都创建，保证体验
		if (distanceSq > 400.0) { // 20格平方 = 400
			// 计算从玩家指向特效位置的方向向量，并归一化（计算长度）
			double dirLength = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ); // 水平距离
			if (dirLength > 0.001) { // 避免除零
				// 获取玩家的水平旋转（偏航角）弧度值
				float yawRad = mc.player.yRot * ((float) Math.PI / 180F);
				// 计算玩家的前方视线向量
				double lookX = -Math.sin(yawRad);
				double lookZ = Math.cos(yawRad);
				// 计算玩家到特效的水平方向向量，并归一化
				double dirToEffectX = deltaX / dirLength;
				double dirToEffectZ = deltaZ / dirLength;
				// 计算点积：值在[-1, 1]之间，1表示完全同向
				double dotProduct = lookX * dirToEffectX + lookZ * dirToEffectZ;
				// 如果点积小于cos(100°)≈ -0.173，说明特效在玩家后方100度开外，则不创建
				// 这个阈值可以调整：值越小，视角范围越大；值越大，视角范围越窄
				if (dotProduct < -0.173) {
					return;
				}
			}
		}*/
		List<SAParticleSystem> systems = SAFX.createFX(world, name, x, y, z, motionX, motionY, motionZ);
		if (systems != null) {
			systems.forEach(s -> particleManager.addEffect(s));
		}
	}
	
	
	/*@Override
	public void createFX(String name, ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ){	
		List<SAParticleSystem> systems = SAFX.createFX(world, name, x, y, z, motionX, motionY, motionZ);
		if (systems!=null) {
			systems.forEach(s -> particleManager.addEffect(s));//Minecraft.getInstance().effectRenderer.addEffect(s));
		}
	}*/
	
	@Override
	public void createFX(String name, ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, float pitch, float yaw){	
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
	public void createFX(String name, ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, float scale){	
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
		List<SAParticleSystem> systems = SAFX.createFXOnEntity(ent, name);
		if (systems!=null) {
			for (SAParticleSystem s : systems) {
				s.entityOffset = new Vector3d(offsetX, offsetY, offsetZ);
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
		PlayerEntity localPly = this.getPlayerClient();
		
		MathUtil.Vec2 posPly = new MathUtil.Vec2(localPly.x, localPly.z);
		MathUtil.Vec2 pos = new MathUtil.Vec2(x, z);
		
		return posPly.getVecTo(pos).lenSquared() <= distSq;
	}*/
	
	protected static ResourceLocation[] getTextures(String name, String ...suffixes) {
		ResourceLocation[] tex = new ResourceLocation[suffixes.length+1];
		tex[0]=new ResourceLocation(SagerFX.MODID, name+".png");
		
		for(int i=1; i<=suffixes.length; i++) {
			tex[i] = new ResourceLocation(SagerFX.MODID, name+"_"+suffixes[i-1]+".png");
		}
		return tex;
	}
}
