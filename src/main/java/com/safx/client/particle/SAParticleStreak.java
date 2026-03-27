package safx.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import safx.client.ClientProxy;
import safx.util.MathUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
public class SAParticleStreak extends SAParticle{

	protected SAParticleStreak prev;
	protected SAParticleStreak next;
	
	protected Vector3d pos1; //This streak segment's vertices
	protected Vector3d pos2; 
	
	public SAParticleStreak(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
			double ySpeedIn, double zSpeedIn, SAParticleSystem particleSystem) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, particleSystem);
	}

	 /**
     * Renders the particle
     */
	@Override
    public void renderParticle(BufferBuilder buffer, Entity playerIn, float partialTickTime, float rotX, float rotZ, float rotYZ, float rotXY, float rotXZ)
    {
    	//float progress = ((float)this.age+partialTickTime) / (float)this.lifetime;
    	float progress = ((float)this.age+partialTickTime) / (1.75F*(float)this.lifetime);
//    	System.out.printf("renderParticle: rotX = %.4f,  rotZ = %.4f,  rotYZ = %.4f,  rotXY = %.4f,  rotXZ = %.4f\n", rotX, rotZ, rotYZ, rotXY, rotXZ);
//    	Vector3d View = ClientProxy.get().getPlayerClient().getLook(partialTickTime);
//    	System.out.printf("PlayerView: X = %.4f,  Y = %.4f,  Z = %.4f\n---\n", View.x, View.y, View.z);
    	
    	preRenderStep(progress);
    	
    	if (this.next == null) {
    		this.alpha = 0.0f;
    	}
    	
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

        float fscale = 0.1F * this.particleScale;

        float fPosX = (float)(this.xo + (this.x - this.xo) * (double)partialTickTime - SAParticleManager.interpPosXs);
        float fPosY = (float)(this.yo + (this.y - this.yo) * (double)partialTickTime - SAParticleManager.interpPosYs);
        float fPosZ = (float)(this.zo + (this.z - this.zo) * (double)partialTickTime - SAParticleManager.interpPosZs);
		float fixY = -1.65F;
		if(this.particleSystem.entity!=null && this.particleSystem.entity instanceof LivingEntity){
			//fixY = 0;
			fPosX = (float)(this.xo + (this.x - this.xo) * partialTickTime - SAParticleManager.interpPosX);
			fPosY = (float)(this.yo + (this.y - this.yo) * partialTickTime - SAParticleManager.interpPosY);
			fPosZ = (float)(this.zo + (this.z - this.zo) * partialTickTime - SAParticleManager.interpPosZ);
		}else{
			if(!Minecraft.getInstance().options.getCameraType().isFirstPerson()){
				fixY = - 1F;
				if(playerIn.getVehicle()!=null)fixY = - 6F;
			}
		}
		
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

		GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);

        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        double a = (angle + (partialTickTime * angleRate)) * MathUtil.D2R;
		Vector3d p1, p2, p3, p4;
		
		////System.out.println("streak");
		
        if (prev == null) {
            this.pos1 = null; //new Vector3d(fPosX, fPosY, fPosZ);
            this.pos2 = null; //new Vector3d(fPosX, fPosY, fPosZ);
        }else {        	
    		Vector3d v_view = ClientProxy.get().getPlayerClient().getLookAngle();//getLook(partialTickTime);
    		
    		//Vector3d v_prev = new Vector3d(prev.getX(), prev.getY(), prev.getZ()).subtract(ClientProxy.get().getPlayerClient().getPositionEyes(partialTickTime));
    		Vector3d v_prev = new Vector3d(prev.x, prev.y, prev.z).subtract(ClientProxy.get().getPlayerClient().getPosition(partialTickTime));
    		
            /*System.out.printf("fPos: X=%.3f,  Y=%.3f, Z=%.3f\n", fPosX, fPosY, fPosZ);
            System.out.printf("PrevPos: X=%.3f,  Y=%.3f, Z=%.3f\n", v_prev.x, v_prev.y, v_prev.z);
            System.out.printf("PrevP1: X=%.3f,  Y=%.3f, Z=%.3f\n", prev.pos1.x, prev.pos1.y, prev.pos1.z);*/
    		
    		Vector3d v_dir = v_prev.subtract(fPosX, fPosY, fPosZ).normalize();
            
    		Vector3d v_cross = v_view.cross(v_dir).normalize();
    		
            p1 = new Vector3d(v_cross.x*fscale + fPosX, v_cross.y*fscale  + fPosY, v_cross.z*fscale  + fPosZ);
            p2 = new Vector3d(v_cross.x* -fscale + fPosX, v_cross.y* -fscale  + fPosY, v_cross.z* -fscale  + fPosZ);
        	
            this.pos1 = p1;
            this.pos2 = p2;
            
            
            float fscaleP = prev.particleScale *0.1f;
            
            
            if (prev.pos1 != null && prev.pos2 != null) {
            	p3 = prev.pos2;
            	p4 = prev.pos1;
            }else {
            	p4 = new Vector3d(v_cross.x*fscaleP + v_prev.x, v_cross.y*fscaleP  + v_prev.y, v_cross.z*fscaleP  + v_prev.z);
                p3 = new Vector3d(v_cross.x* -fscaleP + v_prev.x, v_cross.y* -fscaleP  + v_prev.y, v_cross.z* -fscaleP  + v_prev.z);
                
                prev.pos1 = p4;
                prev.pos2 = p3;
            }			

			
	        buffer.vertex(p1.x, p1.y+fixY, p1.z).color(this.rCol, this.gCol, this.bCol, this.alpha).overlayCoords(0, 240).normal(0.0f, 1.0f, 0.0f).uv(ua, va).endVertex();
			buffer.vertex(p2.x, p2.y+fixY, p2.z).color(this.rCol, this.gCol, this.bCol, this.alpha).overlayCoords(0, 240).normal(0.0f, 1.0f, 0.0f).uv(ub, vb).endVertex();
			buffer.vertex(p3.x, p3.y+fixY, p3.z).color(prev.rCol, prev.gCol, prev.bCol, prev.alpha).overlayCoords(0, 240).normal(0.0f, 1.0f, 0.0f).uv(uc, vc).endVertex();
			buffer.vertex(p4.x, p4.y+fixY, p4.z).color(prev.rCol, prev.gCol, prev.bCol, prev.alpha).overlayCoords(0, 240).normal(0.0f, 1.0f, 0.0f).uv(ud, vd).endVertex();
			/*buffer.pos(p1.x, p1.y, p1.z).tex((double)ua, (double)va).color(this.rCol, this.gCol, this.bCol, this.alpha).lightmap(0, 240).normal(0.0f, 1.0f, 0.0f).endVertex();
			buffer.pos(p2.x, p2.y, p2.z).tex((double)ub, (double)vb).color(this.rCol, this.gCol, this.bCol, this.alpha).lightmap(0, 240).normal(0.0f, 1.0f, 0.0f).endVertex();
			buffer.pos(p3.x, p3.y, p3.z).tex((double)uc, (double)vc).color(prev.rCol, prev.gCol, prev.bCol, prev.alpha).lightmap(0, 240).normal(0.0f, 1.0f, 0.0f).endVertex();
			buffer.pos(p4.x, p4.y, p4.z).tex((double)ud, (double)vd).color(prev.rCol, prev.gCol, prev.bCol, prev.alpha).lightmap(0, 240).normal(0.0f, 1.0f, 0.0f).endVertex();*/
        }
        Tessellator.getInstance().end();
        disableBlendMode();
		
		//System.out.println("DoRenderStreak");
    }

	public SAParticleStreak getPrev() {
		return prev;
	}

	public void setPrev(SAParticleStreak prev) {
		this.prev = prev;
	}

	public SAParticleStreak getNext() {
		return next;
	}

	public void setNext(SAParticleStreak next) {
		this.next = next;
	}

	@Override
	public boolean doNotSort() {
		return true;
	}
	
}
