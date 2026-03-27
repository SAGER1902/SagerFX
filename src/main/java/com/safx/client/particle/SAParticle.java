package safx.client.particle;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.renderer.BufferBuilder;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.Tessellator;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
//import safx.client.models.projectiles.ModelRocket;
import safx.client.particle.SAParticleSystemType.AlphaEntry;
import safx.client.particle.SAParticleSystemType.ColorEntry;
import safx.client.render.SARenderHelper;
import safx.client.render.SARenderHelper.RenderType;
//import safx.client.render.item.RenderItemBase;
import safx.util.MathUtil;
import net.minecraft.client.world.ClientWorld;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import com.mojang.blaze3d.systems.RenderSystem;
/**
 * An actual spawned particle
 */
@OnlyIn(Dist.CLIENT)
public class SAParticle extends Particle implements ISAParticle {
	
	//protected static final VertexFormat VERTEX_FORMAT = (new VertexFormat()).addElement(DefaultVertexFormats.POSITION_3F).addElement(DefaultVertexFormats.TEX_2F).addElement(DefaultVertexFormats.COLOR_4UB).addElement(DefaultVertexFormats.TEX_2S).addElement(DefaultVertexFormats.NORMAL_3B).addElement(DefaultVertexFormats.PADDING_1B);
	protected static final VertexFormat VERTEX_FORMAT = DefaultVertexFormats.POSITION;
	public float particleScale = 10F;
	public final void render(IVertexBuilder p_225606_1_, ActiveRenderInfo p_225606_2_, float p_225606_3_) {
		////System.out.println("1Render");
		//this.renderParticle(buffer, playerIn, partialTickTime, rotX, rotZ, rotYZ, rotXY, rotXZ);
		
	}
	public IParticleRenderType getRenderType() {
	  return IParticleRenderType.NO_RENDER;
	}
	
	/*public double posX;
	public double posX;
	public double posZ;*/
	public double posX() {
		return x;
	}
	
	public double posY() {
		return y;
	}
	
	public double posZ() {
		return z;
	}
	
	int life_time;
	
	float angle;
	float angleRate;
	float angleRateDamping;
	
	float size;
	float sizePrev;
	float sizeRate;
	float sizeRateDamping;
	
	float animationSpeed;
	
	double velX;
	double velY;
	double velZ;
	float velocityDamping;
	float velocityDampingOnGround;
	
	float systemVelocityFactor;
	
	SAParticleSystem particleSystem;
	SAParticleSystemType type;
	
	int variationFrame;
	
	protected double depth;
	
	protected boolean itemAttached=false;
	
	//int angle;

	public SAParticle(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
			double ySpeedIn, double zSpeedIn, SAParticleSystem particleSystem) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn);
		this.xd = xSpeedIn;
		this.yd = ySpeedIn;
		this.zd = zSpeedIn;

		this.particleSystem = particleSystem;
		this.type = particleSystem.type;
		this.init();
		}
		
		private void init() {
			this.life_time = MathUtil.randomInt(random, type.lifetimeMin, type.lifetimeMax);
			this.lifetime = life_time;
			this.size = MathUtil.randomFloat(random, type.sizeMin, type.sizeMax) * this.particleSystem.scale;
			this.size+= (this.particleSystem.startSize);
			this.sizeRate = MathUtil.randomFloat(random, type.sizeRateMin, type.sizeRateMax)  * this.particleSystem.scale;
			this.sizeRateDamping = MathUtil.randomFloat(random, type.sizeRateDampingMin, type.sizeRateDampingMax);
			this.animationSpeed = MathUtil.randomFloat(random, type.animationSpeedMin, type.animationSpeedMax);
			this.velocityDamping = MathUtil.randomFloat(random, type.velocityDampingMin, type.velocityDampingMax);
			this.systemVelocityFactor = MathUtil.randomFloat(random, type.systemVelocityFactorMin, type.systemVelocityFactorMax);
		    this.velocityDampingOnGround = MathUtil.randomFloat(random, type.velocityDampingOnGroundMin, type.velocityDampingOnGroundMax);
			
		    this.angle = MathUtil.randomFloat(random, type.angleMin, type.angleMax);
		    this.angleRate = MathUtil.randomFloat(random, type.angleRateMin, type.angleRateMax);
		    this.angleRateDamping = MathUtil.randomFloat(random, type.angleRateDampingMin, type.angleRateDampingMax);
		    
		    //System.out.printf("###INIT:Motion1=(%.2f / %.2f / %.2f)\n",this.xo, this.yo, this.xo);
		    
			this.xd+=(systemVelocityFactor*particleSystem.motionX());
			this.yd+=(systemVelocityFactor*particleSystem.motionY());
			this.zd+=(systemVelocityFactor*particleSystem.motionZ());
			
			//System.out.printf("###INIT:Motion=(%.2f / %.2f / %.2f)\n",this.xo, this.yo, this.xo);
			////System.out.println("###INIT:VelType="+this.type.velocityType.toString());
			//System.out.printf("###INIT:Type.VelocityData=[%.2f, %.2f, %.2f]\n",this.type.velocityDataMin[0], this.type.velocityDataMin[1], this.type.velocityDataMin[2]);
			
			this.velX = this.xd;
			this.velY = this.yd;
			this.velZ = this.zd;
			
			this.variationFrame = random.nextInt(type.frames);
			
//			if (type.randomRotation) {
//				angle = random.nextInt(4);
//			}
		}
	

    public void onUpdate()
    {

		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		this.sizePrev = this.size;

		life_time--;
		if (this.age++ >= this.lifetime) {
			this.remove();
			return;
		}
		
		/*---
		 * Move with System
		 */
		if (this.type.particlesStickToSystem) {
			
			if (this.particleSystem.entity != null) {
				
				if (!this.particleSystem.entity.isAlive()) {
					this.remove();
					return;
				}
				
				if (this.type.particlesMoveWithSystem && this.particleSystem.attachToHead && this.particleSystem.entity instanceof LivingEntity) {
					LivingEntity ent = (LivingEntity)this.particleSystem.entity;
					
					double p = ent.xRot*MathUtil.D2R;
					double y = ent.yHeadRot*MathUtil.D2R;
					
					double prevP = ent.xRotO * MathUtil.D2R;
					double prevY =ent.yHeadRotO * MathUtil.D2R;
					
					Vector3d offsetBase = this.particleSystem.entityOffset.add(this.particleSystem.type.offset);
					
					//ViewBobbing
					/*if (this.particleSystem.entity == Minecraft.getInstance().player
							&& Minecraft.getInstance().gameSettings.thirdPersonView == 0
							&& Minecraft.getInstance().gameSettings.viewBobbing) {
						Vector3d vec = setupViewBobbing(1.0f).scale(2.0);
						offsetBase = offsetBase.add(vec);
					}
					*/
					
					Vector3d offset = offsetBase.xRot((float)-p);
					offset = offset.yRot((float)-y);
					
					Vector3d offsetP = offsetBase.xRot((float)-prevP);
					offsetP = offsetP.yRot((float)-prevY);

					this.xo = this.particleSystem.entity.xo + offsetP.x;
					this.yo = this.particleSystem.entity.yo + ent.getEyeHeight() + offsetP.y;
					this.zo = this.particleSystem.entity.zo + offsetP.z;
					this.x = this.particleSystem.entity.getX() + offset.x;
					this.y = this.particleSystem.entity.getY() + ent.getEyeHeight() + offset.y;
					this.z = this.particleSystem.entity.getZ() + offset.z;
				}else {
					this.xo = this.particleSystem.entity.xo;
					this.yo = this.particleSystem.entity.yo;
					this.zo = this.particleSystem.entity.zo;
					this.x = this.particleSystem.entity.getX();
					this.y = this.particleSystem.entity.getY();
					this.z = this.particleSystem.entity.getZ();
				}
			}else {
				if (!this.particleSystem.isAlive()) {
					this.remove();
					return;
				}
				this.x = this.particleSystem.getX();
				this.y = this.particleSystem.getY();
				this.z = this.particleSystem.getZ();
			}	
			
		}else if (this.type.particlesMoveWithSystem) {		
			double dP = (this.particleSystem.xRot - this.particleSystem.prevRotationPitch)*MathUtil.D2R;
			double dY = (this.particleSystem.yRot - this.particleSystem.prevRotationYaw)*MathUtil.D2R;
			
			Vector3d pos = new Vector3d(this.x,  this.y, this.z);
			Vector3d sysPos = new Vector3d(this.particleSystem.getX(), this.particleSystem.getY(), this.particleSystem.getZ());
			
			Vector3d offset = sysPos.subtract(pos);
			offset = offset.yRot((float)-dY);
			offset = offset.xRot((float)-dP);
			
			Vector3d motion = new Vector3d (this.xd, this.yd, this.zd);
			motion = motion.yRot((float)-dY);
			motion = motion.xRot((float)-dP);
			
			this.x = sysPos.x+offset.x;
			this.y = sysPos.y+offset.y;
			this.z = sysPos.z+offset.z;
			
			this.xd = motion.x;
			this.yd = motion.y;
			this.zd = motion.z;
		}

		/* -------------
		 * MOTION
		 */

		this.xd = velX;
		this.yd = velY;
		this.zd = velZ;
		this.yd -= type.gravity; //(0.05d * (double) type.gravity * (double) this.ticksExisted);		
		//this.moveEntity(this.xo, this.yo, this.xo);
		//System.out.printf("Velocity=(%.2f / %.2f / %.2f)\n",this.velX, this.velY, this.velZ);
		//System.out.printf("Motion=(%.2f / %.2f / %.2f)\n",this.xo, this.yo, this.xo);
		this.setPos(this.x+this.xd, this.y+this.yd, this.z+this.zd);
		
		this.velX *= velocityDamping;
		this.velY *= velocityDamping;
		this.velZ *= velocityDamping;

		if (this.onGround) {
			this.velX *= velocityDampingOnGround;
			this.velY *= velocityDampingOnGround; // ?
			this.velZ *= velocityDampingOnGround;
			if (type.removeOnGround)
				this.remove();
		}

		/* ------------
		 * SIZE
		 */
		size = Math.max(0.0f, size+sizeRate);
		sizeRate *= sizeRateDamping;
		
		/*
		 * ANGLE
		 */
		angle = (angle + angleRate) % 360.0f;
		angleRate *= angleRateDamping;
    }

	 /**
     * Renders the particle
     */
    public void renderParticle(BufferBuilder buffer, Entity playerIn, float partialTickTime, float rotX, float rotZ, float rotYZ, float rotXY, float rotXZ)
    {
    	float progress = ((float)this.age+partialTickTime) / (/*1.75F**/(float)this.lifetime);
    	
    	preRenderStep(progress);   	
    	
		/*-------------------
		 * ANIMATION
		 */	
		int currentFrame = 0;
        if (type.hasVariations) {
        	currentFrame = variationFrame;
        }else {
        	currentFrame = ((int)((float)type.frames*(progress * this.animationSpeed))) % type.frames;
        }
    	
    	/* -------------
         * RENDER PARTICLE
         */
        this.particleScale = sizePrev + (size-sizePrev)*partialTickTime;
    	
        //Minecraft.getInstance().renderEngine.bindTexture(type.texture);
        Minecraft.getInstance().getTextureManager().bind(type.texture);

//        float f6 = ((float)this.particleTextureIndexX + this.particleTextureJitterX / 4.0F) / 16.0F;
//        float f7 = f6 + 0.015609375F;
//        float f8 = ((float)this.particleTextureIndexY + this.particleTextureJitterY / 4.0F) / 16.0F;
//        float f9 = f8 + 0.015609375F;
        float fscale = 0.1F * this.particleScale;

        float fPosX = (float)(this.xo + (this.x - this.xo) * (double)partialTickTime - (!this.itemAttached ? SAParticleManager.interpPosX :0));
        float fPosY = (float)(this.yo + (this.y - this.yo) * (double)partialTickTime - (!this.itemAttached ? SAParticleManager.interpPosY :0));
        float fPosZ = (float)(this.zo + (this.z - this.zo) * (double)partialTickTime - (!this.itemAttached ? SAParticleManager.interpPosZ :0));
        /*Minecraft mc = Minecraft.getInstance();
		if(!mc.options.getCameraType().isFirstPerson()){
			fPosY = fPosY - 0.5F;
		}*/

        float r = fscale;
        
		int col = currentFrame % type.columns;
		int row = (currentFrame / type.columns);
		
		float u = 1.f/type.columns;
		float v = 1.f/type.columns; 
		float U1 = col*u;
		float V1 = row*v;
		float U2 = (col+1)*u;
		float V2 = (row+1)*v;
		
		float ua, va, ub, vb, uc, vc, ud, vd;
		ua=U2; va=V2; ub = U2; vb= V1; uc = U1; vc = V1; ud=U1; vd = V2;
		
		enableBlendMode();

		//RenderSystem.depthMask(true);

		GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        double a = (angle + (partialTickTime * angleRate)) * MathUtil.D2R;
		Vector3d p1, p2, p3, p4;
		
		if (this.type.groundAligned) {
			float s = fscale;
			p1 = new Vector3d(-s,0,-s);
			p2 = new Vector3d(s,0,-s);
			p3 = new Vector3d(s,0,s);
			p4 = new Vector3d(-s,0,s);
			if (a > 0.0001f) {
				p1 = p1.yRot((float) a);
				p2 = p2.yRot((float) a);
				p3 = p3.yRot((float) a);
				p4 = p4.yRot((float) a);
			}
		}else {
	        p1 = new Vector3d((double)(- rotX * fscale - rotXY * fscale), (double)(- rotZ * fscale), (double)(- rotYZ * fscale - rotXZ * fscale));
	        p2 = new Vector3d((double)(- rotX * fscale + rotXY * fscale), (double)( + rotZ * fscale), (double)( - rotYZ * fscale + rotXZ * fscale));
	        p3 = new Vector3d((double)( rotX * fscale + rotXY * fscale), (double)( + rotZ * fscale), (double)( + rotYZ * fscale + rotXZ * fscale));
	        p4 = new Vector3d((double)( rotX * fscale - rotXY * fscale), (double)( - rotZ * fscale), (double)( + rotYZ * fscale - rotXZ * fscale));        
	        
	        if (a > 0.0001f) {
		        Vector3d axis = p1.normalize().cross(p2.normalize());
				double cosa = Math.cos(a);
				double sina = Math.sin(a);
		        
		        p1 = rotAxis(p1, axis, sina, cosa);
		        p2 = rotAxis(p2, axis, sina, cosa);
		        p3 = rotAxis(p3, axis, sina, cosa);
		        p4 = rotAxis(p4, axis, sina, cosa);     
	        }	        		
		}
		
		/*buffer.pos(p1.x + fPosX, p1.y + fPosY, p1.z + fPosZ).tex((double)ua, (double)va)._color4f(this.rCol, this.gCol, this.bCol, this.alpha).lightmap(0, 240).normal(0.0f, 1.0f, 0.0f).endVertex();
		buffer.pos(p2.x + fPosX, p2.y + fPosY, p2.z + fPosZ).tex((double)ub, (double)vb)._color4f(this.rCol, this.gCol, this.bCol, this.alpha).lightmap(0, 240).normal(0.0f, 1.0f, 0.0f).endVertex();
		buffer.pos(p3.x + fPosX, p3.y + fPosY, p3.z + fPosZ).tex((double)uc, (double)vc)._color4f(this.rCol, this.gCol, this.bCol, this.alpha).lightmap(0, 240).normal(0.0f, 1.0f, 0.0f).endVertex();
		buffer.pos(p4.x + fPosX, p4.y + fPosY, p4.z + fPosZ).tex((double)ud, (double)vd)._color4f(this.rCol, this.gCol, this.bCol, this.alpha).lightmap(0, 240).normal(0.0f, 1.0f, 0.0f).endVertex();*/
		if(type.renderType == RenderType.ALPHA_SHADED){
			buffer.vertex(p1.x + fPosX, p1.y + fPosY-1.65F, p1.z + fPosZ).color(this.rCol, this.gCol, this.bCol, this.alpha)/*.overlayCoords(0, 240)*/.normal(0.0f, 1.0f, 0.0f).uv(ua, va).endVertex();
			buffer.vertex(p2.x + fPosX, p2.y + fPosY-1.65F, p2.z + fPosZ).color(this.rCol, this.gCol, this.bCol, this.alpha)/*.overlayCoords(0, 240)*/.normal(0.0f, 1.0f, 0.0f).uv(ub, vb).endVertex();
			buffer.vertex(p3.x + fPosX, p3.y + fPosY-1.65F, p3.z + fPosZ).color(this.rCol, this.gCol, this.bCol, this.alpha)/*.overlayCoords(0, 240)*/.normal(0.0f, 1.0f, 0.0f).uv(uc, vc).endVertex();
			buffer.vertex(p4.x + fPosX, p4.y + fPosY-1.65F, p4.z + fPosZ).color(this.rCol, this.gCol, this.bCol, this.alpha)/*.overlayCoords(0, 240)*/.normal(0.0f, 1.0f, 0.0f).uv(ud, vd).endVertex();
		}else{
			buffer.vertex(p1.x + fPosX, p1.y + fPosY-1.65F, p1.z + fPosZ).color(this.rCol, this.gCol, this.bCol, this.alpha).overlayCoords(0, 240).normal(0.0f, 1.0f, 0.0f).uv(ua, va).endVertex();
			buffer.vertex(p2.x + fPosX, p2.y + fPosY-1.65F, p2.z + fPosZ).color(this.rCol, this.gCol, this.bCol, this.alpha).overlayCoords(0, 240).normal(0.0f, 1.0f, 0.0f).uv(ub, vb).endVertex();
			buffer.vertex(p3.x + fPosX, p3.y + fPosY-1.65F, p3.z + fPosZ).color(this.rCol, this.gCol, this.bCol, this.alpha).overlayCoords(0, 240).normal(0.0f, 1.0f, 0.0f).uv(uc, vc).endVertex();
			buffer.vertex(p4.x + fPosX, p4.y + fPosY-1.65F, p4.z + fPosZ).color(this.rCol, this.gCol, this.bCol, this.alpha).overlayCoords(0, 240).normal(0.0f, 1.0f, 0.0f).uv(ud, vd).endVertex();
		}
		Tessellator.getInstance().end();
        //new ModelRocket().render(null, 0, 0, 0, 0, 0, 0.625f, 0, 0f, TransformType.GROUND, 0, 0f, 0f);
        ////System.out.println("DoRenderNormal");
        
        disableBlendMode();
    }
    
    /**
     * interpolate colors and alpha values
     */
    protected void preRenderStep(float progress) {
    	
		/* ------------------------
		 * INTERPOLATE COLOR VALUES
		 */
    	
		ColorEntry c1 = null;
		ColorEntry c2 = null;
    	if (type.colorEntries.size()==0) {
    		c1 =new ColorEntry(1.0f,1.0f,1.0f,0);
    		c2 = c1;
    	}else if (type.colorEntries.size() == 1) {
    		c1 = type.colorEntries.get(0);
    		c2 = c1;
    	}else {
    		c1 = type.colorEntries.get(0);
    		for (int i = 1; i < type.colorEntries.size(); i++) {
    			c2 = type.colorEntries.get(i);
				if (progress < c2.time) {
					break;
				}else {
					c1 = c2;
				}
			}
    	}
		float p = (progress-c1.time) / (c2.time-c1.time);		
		if (c1 != c2) {
			
			//RGB to HSB
			float[] hsb1 = Color.RGBtoHSB((int)(c1.r*255), (int)(c1.g*255), (int)(c1.b*255), null);
			float[] hsb2 = Color.RGBtoHSB((int)(c2.r*255), (int)(c2.g*255), (int)(c2.b*255), null);	
			//HSB to RGB;
			Color color = new Color(Color.HSBtoRGB(hsb1[0]*(1f-p) + hsb2[0]*p, hsb1[1]*(1f-p) + hsb2[1]*p, hsb1[2]*(1f-p) + hsb2[2]*p));
			this.rCol = (float)color.getRed() / 255.0f;
			this.gCol = (float)color.getGreen() / 255.0f;
			this.bCol = (float)color.getBlue() / 255.0f;
		}else {
			this.rCol = (float)c1.r;
			this.gCol = (float)c1.g;
			this.bCol = (float)c1.b;
		}
		
//		if (p > 0.99f)
//			//System.out.println(String.format("R=%.3f, G=%.3f, B=%.3f", this.rCol, this.gCol, this.bCol));
		
		/*-------------------------
		 * INTERPOLATE ALPHA VALUES
		 */
		AlphaEntry a1 = null;
		AlphaEntry a2 = null;
		if (type.alphaEntries.size() == 0) {
			this.alpha = 1.0f;
		}else if (type.alphaEntries.size() == 1) {
			a1 = type.alphaEntries.get(0);
			this.alpha = a1.alpha;
		}else {
			a1 = type.alphaEntries.get(0);
    		for (int i = 1; i < type.alphaEntries.size(); i++) {
    			a2 = type.alphaEntries.get(i);
				if (progress < a2.time) {
					break;
				}else {
					a1 = a2;
				}
			}
    		if (a1.time != a2.time) {
    			p = (progress-a1.time) / (a2.time-a1.time);		
    			//interpolate
    			this.alpha = a1.alpha*(1f-p) + a2.alpha * p;
    		}else {
    			this.alpha = a1.alpha;
    		}
		}
//		if (p > 0.99f)
//			//System.out.println(String.format("A=%.3f", this.alpha));
    }
    
    public int getBrightnessForRender(float p_189214_1_)
    {
        return 61680;
    }
	
	protected void enableBlendMode() {
		//GlStateManager.pushAttrib();
    	if (type.renderType != RenderType.SOLID) {
    		GlStateManager._enableBlend();
    		GlStateManager._depthMask(false);
    	}
        if (type.renderType == RenderType.ALPHA||type.renderType == RenderType.ALPHA_SHADED) {
        	//GlStateManager._blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			//RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, 
			GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			//RenderSystem.alphaFunc(516, 0.003921569F);
        } else if (type.renderType == RenderType.ADDITIVE || type.renderType==RenderType.NO_Z_TEST) {
        	//GlStateManager._blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
			//RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, 
			GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			//RenderSystem.alphaFunc(516, 0.003921569F);
        }
        
        if (type.renderType==RenderType.NO_Z_TEST){
        	GlStateManager._depthMask(false);
        	GlStateManager._enableDepthTest();//_disableDepth
        }
        
        /*if (type.renderType != RenderType.ALPHA_SHADED) */SARenderHelper.enableFXLighting();
	}
	
	protected void disableBlendMode() {
		/*if (type.renderType != RenderType.ALPHA_SHADED) */SARenderHelper.disableFXLighting();
		if (type.renderType != RenderType.SOLID) {
    		GlStateManager._disableBlend();
    		GlStateManager._depthMask(true);
    	}
		if (type.renderType == RenderType.ALPHA||type.renderType == RenderType.ALPHA_SHADED) {
        	//GlStateManager._blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			RenderSystem.defaultBlendFunc();
			//RenderSystem.disableBlend();
        } else if (type.renderType == RenderType.ADDITIVE || type.renderType==RenderType.NO_Z_TEST) {
        	//GlStateManager._blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			RenderSystem.defaultBlendFunc();
			//RenderSystem.disableBlend();
        }
		
        if (type.renderType==RenderType.NO_Z_TEST){
        	GlStateManager._depthMask(true);
        	GlStateManager._enableDepthTest();
        }
		
		//GlStateManager.popAttrib();
	}
    
    /**
	 * Retrieve what effect layer (what texture) the particle should be rendered
	 * with. 0 for the particle sprite sheet, 1 for the main Texture atlas, and 3
	 * for a custom texture
	 */
	public int getFXLayer() {
		return 3;
	}
	
	protected Vector3d rotAxis(Vector3d p1, Vector3d axis, double sina, double cosa) {	
		  Vector3d v1 = axis.cross(p1);
		  double d1 = axis.dot(p1);
		  return p1.scale(cosa).add(v1.scale(sina)).add(axis.scale(d1*(1.0 - cosa)));			
		//  return p1.scale(cosa).add(axis.crossProduct(p1).scale(Math.sin(a))).add(axis.scale(axis.dotProduct(p1)*(1.0 - Math.cos(a))));			
	}

	@Override
	public Vector3d getPos() {
		return new Vector3d(this.x, this.y, this.z);
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
	public void doRender(BufferBuilder buffer, Entity playerIn, float partialTickTime, float rotX, float rotZ,
			float rotYZ, float rotXY, float rotXZ) {
			//System.out.println("DoRender001");
		this.renderParticle(buffer, playerIn, partialTickTime, rotX, rotZ, rotYZ, rotXY, rotXZ);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox(float partialTickTime, Entity viewEnt) {
		double fPosX = (this.x-viewEnt.getX());
		double fPosY = (this.y-viewEnt.getY());
		double fPosZ = (this.z-viewEnt.getZ());
	    
		double s = size*0.5;
		return new AxisAlignedBB(fPosX-s, fPosY-s, fPosZ-s, fPosX+s, fPosY+s, fPosZ+s);
	}

	@Override
	public double getDepth() {
		return this.depth;
	}

	@Override
	public void setDepth(double depth) {
		this.depth=depth;
	}
	
	private Vector3d setupViewBobbing(float ptt)
    {
        if (Minecraft.getInstance().getCameraEntity() instanceof PlayerEntity)
        {
            PlayerEntity PlayerEntity = (PlayerEntity)Minecraft.getInstance().getCameraEntity();
            float f1 = PlayerEntity.walkDist - PlayerEntity.walkDistO;
            float f2 = -(PlayerEntity.walkDist + f1 * ptt);
            float f3 = PlayerEntity.oBob + (PlayerEntity.bob - PlayerEntity.oBob) * ptt;
            //float f4 = /*PlayerEntity.prevCameraPitch + (PlayerEntity.cameraPitch - PlayerEntity.prevCameraPitch) * ptt*/PlayerEntity.rotOffs;
			float f4 = 1F;
            float F1 = 1.0f; // (float) Keybinds.X;
            float F2 = 1.0f; //(float) Keybinds.Y;
            
            Vector3d vec = new Vector3d(MathHelper.sin(f2 * (float)Math.PI) * f3 * 0.5F * F1,  -Math.abs(MathHelper.cos(f2 * (float)Math.PI) * f3) * F2, 0.0F);
            vec = MathUtil.rotateVec3dAroundZ(vec, MathHelper.sin(f2 * (float)Math.PI) * f3 * 3.0F * (float)MathUtil.D2R);
            return vec.xRot(Math.abs(MathHelper.cos(f2 * (float)Math.PI - 0.2F) * f3) * 5.0F * (float)MathUtil.D2R).xRot(f4 * (float)MathUtil.D2R);	
        }else {
        	return new Vector3d(0,0,0);
        }
    }

	@Override
	public void setItemAttached() {
		this.itemAttached=true;
	}

	@Override
	public void remove() {
		super.remove();
		this.particleSystem=null;
	}
	
}
