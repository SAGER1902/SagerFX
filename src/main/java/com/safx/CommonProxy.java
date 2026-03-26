package safx;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.client.multiplayer.ClientLevel;
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

	public Player getPlayerClient() {
		return null;
	}
    
	public void createFX(String name, ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
		
	}
	public void createFX(String name, ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, float pitch, float yaw) {
		
	}
	public void createFX(String name, ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, float scale) {
		
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
    public void clearItemParticleSystemsHand(LivingEntity entity, InteractionHand hand) {
	}
	public void handlePlayerGliding(Player player) {
		//do nothing on server
	}
}
