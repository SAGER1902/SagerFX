package safx.client.particle;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.client.world.ClientWorld;
public abstract class SAFXType {
	public String name;
	boolean isList = false;
	
	public abstract List<SAParticleSystem> createParticleSystems(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ);
	public abstract List<SAParticleSystem> createParticleSystemsOnEntity(Entity ent);
	public abstract List<SAParticleSystem> createParticleSystemsOnParticle(ClientWorld worldIn, SAParticle part);
}
