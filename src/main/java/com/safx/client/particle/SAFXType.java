package safx.client.particle;

import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.client.multiplayer.ClientLevel;
public abstract class SAFXType {
	public String name;
	boolean isList = false;
	
	public abstract List<SAParticleSystem> createParticleSystems(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ);
	public abstract List<SAParticleSystem> createParticleSystemsOnEntity(Entity ent);
	public abstract List<SAParticleSystem> createParticleSystemsOnParticle(ClientLevel worldIn, SAParticle part);
}
