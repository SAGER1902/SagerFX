package safx.client.particle;

import java.util.Iterator;
import java.util.List;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Implemented by capabilities that store entityAttachedParticle systems
 *
 */
public interface ISAParticleAttachments {
	
	public default void tickParticles() {
		if(this.getEntityParticles()!=null) {
			Iterator<ISAParticle> it = getEntityParticles().iterator();
			while(it.hasNext()) {
				ISAParticle p = it.next();
				
				p.updateTick();
				if(p.shouldRemove()) {
					it.remove();
				}
			}
		}
		
		if(this.getEntityParticlesMH()!=null) {
			Iterator<ISAParticle> it = getEntityParticlesMH().iterator();
			while(it.hasNext()) {
				ISAParticle p = it.next();
				
				p.updateTick();
				if(p.shouldRemove()) {
					it.remove();
				}
			}
		}
		
		if(this.getEntityParticlesOH()!=null) {
			Iterator<ISAParticle> it = getEntityParticlesOH().iterator();
			while(it.hasNext()) {
				ISAParticle p = it.next();
				
				p.updateTick();
				if(p.shouldRemove()) {
					it.remove();
				}
			}
		}
		
		if(this.getParticleSysMainhand()!=null) {
			Iterator<SAParticleSystem> it = getParticleSysMainhand().iterator();
			while(it.hasNext()) {
				SAParticleSystem p = it.next();
				
				p.updateTick();
				if(p.shouldRemove()) {
					it.remove();
				}
			}
		}
		if(this.getParticleSysOffhand()!=null) {
			Iterator<SAParticleSystem> it = getParticleSysOffhand().iterator();
			while(it.hasNext()) {
				SAParticleSystem p = it.next();
				
				p.updateTick();
				if(p.shouldRemove()) {
					it.remove();
				}
			}
		}
	}
	
	public default void clearAttachedSystemsHand(InteractionHand hand) {
		if(hand==InteractionHand.MAIN_HAND) {
			if (this.getParticleSysMainhand()!=null) {
				this.getParticleSysMainhand().clear();
			}
		} else {
			if (this.getParticleSysOffhand()!=null) {
				this.getParticleSysOffhand().clear();
			}
		}
	}
	
	public default void addSystemsHand(InteractionHand hand, List<SAParticleSystem> systems) {
		
		if(hand==InteractionHand.MAIN_HAND) {
			this.getOrInitParticleSysMainhand().clear();
			this.getParticleSysMainhand().addAll(systems);
		} else {
			this.getOrInitParticleSysOffhand().clear();
			this.getParticleSysOffhand().addAll(systems);
		}
	}
	
	public default void addEffectHand(InteractionHand hand, List<ISAParticle> effects) {
		
		if(hand==InteractionHand.MAIN_HAND) {
			this.getOrInitEntityParticlesMH().addAll(effects);
		} else {
			this.getOrInitEntityParticlesOH().addAll(effects);
		}
	}
	
	public List<ISAParticle> getEntityParticles();
	
	public List<ISAParticle> getEntityParticlesMH();
	
	public List<ISAParticle> getEntityParticlesOH();
	
	public List<SAParticleSystem> getParticleSysMainhand();
	
	public List<SAParticleSystem> getParticleSysOffhand();
	
	public List<ISAParticle> getOrInitEntityParticles();

	public List<ISAParticle> getOrInitEntityParticlesOH();

	public List<ISAParticle> getOrInitEntityParticlesMH();
	
	public List<SAParticleSystem> getOrInitParticleSysMainhand();
	
	public List<SAParticleSystem> getOrInitParticleSysOffhand();

}
