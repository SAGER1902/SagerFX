package safx;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;

import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.client.world.ClientWorld;
import safx.util.EntityCondition;

//@Mod.EventBusSubscriber
public class CommonProxy{

	//@Override
	public void preInit() {

	}
	//@Override
	public void init() {

	}
	//@Override
	public void postInit() {
		
	}

	public PlayerEntity getPlayerClient() {
		return null;
	}
    
	public void createFX(String name, ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
		
	}
	public void createFX(String name, ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, float pitch, float yaw) {
		
	}
	public void createFX(String name, ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, float scale) {
		
	}
	public void createFXOnEntity(String name, Entity ent) {

	}
	public void createFXOnEntity(String name, Entity ent, float scale) {

	}
	public void createFXOnEntityWithOffset(String name, Entity ent, float offsetX, float offsetY, float offsetZ, boolean attachToHead, EntityCondition condition) {};
	
    public void setHasStepassist(boolean value){};
    
    public void setHasNightvision(boolean value){};
    
    public void setFlySpeed(float value){};
    
    public boolean getHasStepassist(){
    	return false;
    };
    public boolean getHasNightvision(){
    	return false;
    };
    public boolean isClientPlayerAndIn1stPerson(LivingEntity ent) {
		return false;
	}
    public boolean clientInRangeSquared(double x, double z, double distSq) {
		return false;
	}
    public void registerFluidModelsForFluidBlock(Block b) {};
    
    public void clearItemParticleSystemsHand(LivingEntity entity, Hand hand) {
	}
	public void handlePlayerGliding(PlayerEntity player) {
		//do nothing on server
	}
}
