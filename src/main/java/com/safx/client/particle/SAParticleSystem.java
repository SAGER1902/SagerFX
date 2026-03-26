package safx.client.particle;
import java.util.Random;
import java.util.List;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import safx.client.ClientProxy;
import safx.client.particle.SAParticleSystemType.DirResult;
import safx.util.EntityCondition;
import safx.util.MathUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.Minecraft;

import net.minecraft.client.Camera;

import wmlib.util.Vec3dr;
import wmlib.common.living.EntityWMVehicleBase;
/**
 * A particle system which spawns particles
 */
@OnlyIn(Dist.CLIENT)
public class SAParticleSystem extends Particle implements ISAParticle {
	
	public SAParticleSystemType type;
	protected final Random randomfx = new Random();
	public final void render(VertexConsumer p_225606_1_, Camera p_225606_2_, float p_225606_3_) {
	}

	public ParticleRenderType getRenderType() {
	  return ParticleRenderType.NO_RENDER;
	}
	//public abstract ParticleRenderType getRenderType();
	
	public EntityCondition condition = EntityCondition.NONE;
	
	/*public double posX;
	public double posX;
	public double posZ;*/

	/*public double motionX;
	public double motionY;
	public double motionZ;*/
	
	int systemLifetime;
	int spawnDelay = 0;
	
	public float scale =1.0f; //global scale
	public float yRot;
	public float xRot;
	
	public float prevRotationYaw;
	public float prevRotationPitch;
	
	public int initialDelay;
	public int ticksExisted = 0;
	
	public float startSizeRate;
	public float startSizeRateDamping;
	public float startSize = 0.0f;
	
	protected SAParticleStreak prevParticle = null;
	
//	private long timediff = 0;
	
	Entity entity; //parent entity (if attached to an entity)
	public boolean attachToHead = false;
	public Vec3 entityOffset = null;
	SAParticle parent; //parent particle (if attached to a particle)
	
	protected boolean itemAttached=false;
	
	public SAParticleSystem(ClientLevel worldIn, SAParticleSystemType type, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
		//super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
		super(worldIn, xCoordIn + type.offset.x, yCoordIn+type.offset.y, zCoordIn+type.offset.z);
		this.xd = xSpeedIn;
		this.yd = ySpeedIn;
		this.zd = zSpeedIn;
		this.type = type;
		this.init();
	}
	
	public SAParticleSystem(/*ClientLevel worldIn, */Entity entity, SAParticleSystemType type) {
		this(/*Minecraft.level*/null, type, entity.getX(),entity.getY(),entity.getZ(),0,0,0);
		this.entity = entity;
	}
		

	public SAParticleSystem(ClientLevel worldIn, SAParticle part, SAParticleSystemType type) {
		this(worldIn, type, part.posX(), part.posY(), part.posZ(),0,0,0);
		this.parent = part;
		//////System.out.println("Spawn attached system : " + type.name);
	}

	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		//This is a ParticleSystem which spawns the actual particles. Don't render anything
	}
	
	protected void init() {
		this.systemLifetime = MathUtil.randomInt(this.randomfx, type.systemLifetimeMin, type.systemLifetimeMax);
		this.initialDelay = MathUtil.randomInt(this.randomfx, type.initialDelayMin, type.initialDelayMax);
		this.startSizeRate = MathUtil.randomFloat(this.randomfx, type.startSizeRateMin, type.startSizeRateMax) * this.scale;
		this.startSizeRateDamping = MathUtil.randomFloat(this.randomfx, type.startSizeRateDampingMin, type.startSizeRateDampingMax);
		//Minecraft.getInstance().effectRenderer.addEffect(this);
		//timediff = System.currentTimeMillis();
	}
	
	public void onUpdate() {	
//		if (this.ticksExisted == 0) {
//			////System.out.println("Timediff: "+(System.currentTimeMillis()-timediff));
//		}
		this.prevRotationPitch = this.xRot;
		this.prevRotationYaw = this.yRot;
		
		this.xo = this.getX();
		this.yo = this.getY();
		this.zo = this.getZ();
		
		if (type == null) {
			this.remove();
			return;
		}

		if (this.entity!=null && !this.itemAttached){		
	    	if (!condition.evaluate(entity)) {
	    		this.remove();
	    		return;
	    	}			
			
			if (this.attachToHead && entity instanceof LivingEntity) {
				LivingEntity elb = (LivingEntity) entity;
				this.xRot=elb.getXRot();
				this.yRot=elb.yHeadRot;
				Vec3dr offsetr = type.offsetr;
				if (this.entityOffset != null) offsetr = offsetr.add1(this.entityOffset);
				if(entity instanceof EntityWMVehicleBase){
					EntityWMVehicleBase plane = (EntityWMVehicleBase)entity;
					offsetr = offsetr.rotateRote((float) ((-plane.flyRoll)*MathUtil.D2R));	
				}
				offsetr = offsetr.rotatePitch((float) (-elb.getXRot()*MathUtil.D2R));
				offsetr = offsetr.rotateYaw((float) ((-elb.getYRot())*MathUtil.D2R));		
				
				this.x = elb.xo + offsetr.x;
				this.y = elb.yo + elb.getEyeHeight() + offsetr.y;
				this.z = elb.zo + offsetr.z;
			}else {
				this.xRot=entity.getXRot();
				this.yRot=entity.getYRot();
						
				Vec3 offset = type.offset;
				if (this.entityOffset != null) offset = offset.add(this.entityOffset);
				
				offset = offset.xRot((float) (-entity.getXRot()*MathUtil.D2R));
				offset = offset.yRot((float) ((-entity.getYRot())*MathUtil.D2R));		
				
				this.x = entity.xo + offset.x;
				this.y = entity.yo + offset.y;
				this.z = entity.zo + offset.z;
			}
			this.xd = entity.getDeltaMovement().x;
			this.yd = entity.getDeltaMovement().y;
			this.zd = entity.getDeltaMovement().z;
			
		} else if (this.parent != null) {
			this.x=parent.posX() + type.offset.x;
			this.y=parent.posY() + type.offset.y;
			this.z=parent.posZ() + type.offset.z;
		} else {
			this.x+=xd;
			this.y+=yd;
			this.z+=zd;
		}
		
		if (initialDelay-- > 0) {
			return;
		}
		
		this.startSize = this.startSize+this.startSizeRate;
		this.startSizeRate = (this.startSizeRate * this.startSizeRateDamping);
		
		
		if (spawnDelay-- <= 0) {
			int count = MathUtil.randomInt(randomfx, type.particleCountMin, type.particleCountMax);

			for (int i = 0; i < count; i++) {
				//Get position and motion data
				DirResult dir = this.type.new DirResult();
				Vec3 position = type.volumeType.getPosition(this, dir, i, count);
				//System.out.printf("Dir: %.3f,  %.3f,  %.3f\n", dir[0], dir[1], dir[2]);
				//position = position.addVector(type.offset.x, type.offset.y, type.offset.z);
				Vec3 motion = type.velocityType.getVelocity(this, dir.values);
				if(this.scale>1){
					motion = motion.scale(0.9F+this.scale*0.1F);
				}else{
					motion = motion.scale(this.scale);
				}
				
				position = position.scale(this.scale);
				
				//apply ParticleSystem's entity rotation
				
				//////System.out.println("ParticleSystemRotation - Pitch: "+this.xRot+" - Yaw: "+this.yRot);
				
				//////System.out.println(String.format("Entity pitch : %.3f,  yaw : %.3f", this.xRot, this.yRot));				
				
				motion = motion.xRot((float) (-this.xRot*MathUtil.D2R));
				motion = motion.yRot((float) ((-this.yRot)*MathUtil.D2R));
				if (type.volumeType != SAParticleSystemType.VOL_TRAIL) {
					position = position.xRot((float) (-this.xRot*MathUtil.D2R));
					position = position.yRot((float) ((-this.yRot)*MathUtil.D2R));		
				}
				//////System.out.println("Motion: ("+motion.x+ ", "+motion.y + ", "+ motion.z+")");
				//////System.out.println("Position: ("+position.x+ ", "+position.y + ", "+ position.z+")");
				
				//Spawn particle
				double mf = 0.05D; //Per Second instead of Per Tick
				SAParticle particle=null;
				if (this.type.streak) {
					SAParticleStreak particleStreak = new SAParticleStreak(this.level, this.getX()+position.x, this.getY()+position.y, this.getZ()+position.z, motion.x*mf, motion.y*mf, motion.z*mf, this);
					if (prevParticle != null) {
						particleStreak.setPrev(prevParticle);
						prevParticle.setNext(particleStreak);
					}
					addEffect(particleStreak);
					prevParticle = particleStreak;
					particle = particleStreak;
				}else {
					if(this.itemAttached) {
						//particle = new SAParticleItemAttached(this.level, this.getX()+position.x, this.getY()+position.y, this.getZ()+position.z, motion.x*mf, motion.y*mf, motion.z*mf, this, this.entity);
					} else {
						particle = new SAParticle(this.level, this.getX()+position.x, this.getY()+position.y, this.getZ()+position.z, motion.x*mf, motion.y*mf, motion.z*mf, this);
					}
					addEffect(particle);
				}
				if (this.type.attachedSystem != null && !this.type.attachedSystem.equals("")) {
					List<SAParticleSystem> systems = SAFX.createFXOnParticle(this.level, particle, this.type.attachedSystem);
					if (systems!=null) {
						for (SAParticleSystem s : systems) {
							s.scale = this.scale;
							addEffect(s);
						}
					}
				}
			}			
			spawnDelay = MathUtil.randomInt(this.randomfx, type.spawnDelayMin, type.spawnDelayMax);
		}
		
		if (this.entity!=null){
			if (this.entity.isRemoved()){
				this.remove();
			} else if ( this.systemLifetime>0 && ticksExisted>= this.systemLifetime){
				this.remove();
			}
			
		} else if (this.parent != null) {
			if (!this.parent.isAlive()) {
				this.remove();
			}
		}else {
			if (ticksExisted >= this.systemLifetime) {
				this.remove();
			}
		}
		ticksExisted++;
		

	}

	protected void addEffect(ISAParticle s) {
		ClientProxy.get().particleManager.addEffect(s);
	}
	
	/**
	 * Retrieve what effect layer (what texture) the particle should be rendered
	 * with. 0 for the particle sprite sheet, 1 for the main Texture atlas, and 3
	 * for a custom texture
	 */
	public int getFXLayer() {
		return 1;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public double motionX() {
		return xd;
	}
	
	public double motionY() {
		return yd;
	}
	
	public double motionZ() {
		return zd;
	}

	@Override
	public Vec3 getPos() {
		return new Vec3(this.getX(), this.getY(), this.getZ());
	}

	@Override
	public boolean shouldRemove() {
		return !this.isAlive();
	}

	@Override
	public void updateTick() {
		this.onUpdate();
	}

	@Override
	public void doRender(BufferBuilder buffer, Entity entityIn, float partialTickTime, float rotX, float rotZ,
			float rotYZ, float rotXY, float rotXZ) {
		this.renderParticle(buffer, entityIn, partialTickTime, rotX, rotZ, rotYZ, rotXY, rotXZ);
	}

	@Override
	public AABB getRenderBoundingBox(float partialTickTime, Entity viewEntity) {
		//DOESN'T MATTER, SYSTEM DOES NOT RENDER STUFF ANYWAY
	    float s = 0.5f; 
		return new AABB(this.getX()-s, this.getY()-s, this.getZ()-s, this.getX()+s, this.getY()+s, this.getZ()+s);
	}

	@Override
	public double getDepth() {
		return 0;
	}

	@Override
	public void setDepth(double depth) {
	}

	@Override
	public void setItemAttached() {
	}

	//@Override
	public void remove() {
		super.remove();
		this.entity=null;
	}
	
//ACTUALLY WE DON'T NEED THIS	
//	@OnlyIn(Dist.CLIENT)
//	public static class Factory implements IParticleFactory {
//		public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... parameters) {
//        	if (parameters.length <= 0) return null;
//        	SAParticleSystemType type = SAParticleList.getType(parameters[0]);
//			return new SAParticleSystem(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, type);
//		}
//	}
}