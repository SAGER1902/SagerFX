package safx.client.particle;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.client.world.ClientWorld;
public class SAParticleListType extends SAFXType {
	ArrayList<ParticleSystemEntry> particleSystems = new ArrayList<ParticleSystemEntry>();

	
	
	public SAParticleListType() {
		isList = true;
	}

	public void addParticleSystem(String particleSystem) {
		particleSystems.add(new ParticleSystemEntry(particleSystem));
	}
	
	class ParticleSystemEntry {
		//ParticleSystemType type; //Actually is just a string
		String particleSystem;
		//TODO: Delay, Offset, Scale, etc.

		public ParticleSystemEntry(String particleSystem) {
			super();
			this.particleSystem = particleSystem;
		}
	}

	
	@Override
	public List<SAParticleSystem> createParticleSystems(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
		ArrayList<SAParticleSystem> list = new ArrayList<SAParticleSystem>();
		for (ParticleSystemEntry system : particleSystems) {		
			if (SAFX.FXList.containsKey(system.particleSystem)) {
				SAFXType fxtype = SAFX.FXList.get(system.particleSystem);
				list.addAll(fxtype.createParticleSystems(world, x, y, z, motionX, motionY, motionZ));
			}
		}
		return list;
	}

	@Override
	public List<SAParticleSystem> createParticleSystemsOnEntity(Entity ent) {
		ArrayList<SAParticleSystem> list = new ArrayList<SAParticleSystem>();
		for (ParticleSystemEntry system : particleSystems) {		
			if (SAFX.FXList.containsKey(system.particleSystem)) {
				SAFXType fxtype = SAFX.FXList.get(system.particleSystem);
				list.addAll(fxtype.createParticleSystemsOnEntity(ent));
			}
		}
		return list;
	}

	@Override
	public List<SAParticleSystem> createParticleSystemsOnParticle(ClientWorld worldIn, SAParticle ent) {
		ArrayList<SAParticleSystem> list = new ArrayList<SAParticleSystem>();
		for (ParticleSystemEntry system : particleSystems) {		
			if (SAFX.FXList.containsKey(system.particleSystem)) {
				SAFXType fxtype = SAFX.FXList.get(system.particleSystem);
				list.addAll(fxtype.createParticleSystemsOnParticle(worldIn, ent));
			}
		}
		return list;
	}
}
