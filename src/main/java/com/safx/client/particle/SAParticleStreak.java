package safx.client.particle;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import safx.client.ClientProxy;
import safx.util.MathUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.BufferUploader;
import net.minecraft.client.renderer.GameRenderer;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import safx.client.render.SARenderHelper;
import safx.client.render.SARenderHelper.RenderTypeSA;
public class SAParticleStreak extends SAParticle{

	protected SAParticleStreak prev;
	protected SAParticleStreak next;
	
	protected Vec3 pos1; //This streak segment's vertices
	protected Vec3 pos2; 
	
	public SAParticleStreak(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
			double ySpeedIn, double zSpeedIn, SAParticleSystem particleSystem) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, particleSystem);
	}

	 /**
     * Renders the particle
     */
	 int time = 0;
	@Override
    public void renderParticle(BufferBuilder buffer, Entity playerIn, float partialTickTime, float rotX, float rotZ, float rotYZ, float rotXY, float rotXZ)
    {
		//float progress = ((float)this.age+partialTickTime) / (float)this.lifetime;
		float progress = ((float)this.age+partialTickTime) / (1.75F*(float)this.lifetime);
//    	System.out.printf("renderParticle: rotX = %.4f,  rotZ = %.4f,  rotYZ = %.4f,  rotXY = %.4f,  rotXZ = %.4f\n", rotX, rotZ, rotYZ, rotXY, rotXZ);
//    	Vec3 View = ClientProxy.get().getPlayerClient().getLook(partialTickTime);
//    	System.out.printf("PlayerView: X = %.4f,  Y = %.4f,  Z = %.4f\n---\n", View.x, View.y, View.z);
    	preRenderStep(progress);
    	if (this.next == null) {
    		this.alpha = 0.0f;
    	}
		int currentFrame = 0;
        if (type.hasVariations) {
        	currentFrame = variationFrame;
        }else {
        	currentFrame = ((int)((float)type.frames*(progress * this.animationSpeed))) % type.frames;
        }
        this.particleScale = sizePrev + (size-sizePrev)*partialTickTime;
        float fscale = 0.1F * this.particleScale;
        float fPosX = (float)(this.xo + (this.x - this.xo) * partialTickTime - SAParticleManager.interpPosXs);
        float fPosY = (float)(this.yo + (this.y - this.yo) * partialTickTime - SAParticleManager.interpPosYs);
        float fPosZ = (float)(this.zo + (this.z - this.zo) * partialTickTime - SAParticleManager.interpPosZs);
		Minecraft mc = Minecraft.getInstance();
		float fixY = -1.65F;

		if(this.particleSystem.entity!=null && this.particleSystem.entity instanceof LivingEntity){
			fixY = 0;
			fPosX = (float)(this.xo + (this.x - this.xo) * partialTickTime - SAParticleManager.interpPosX);
			fPosY = (float)(this.yo + (this.y - this.yo) * partialTickTime - SAParticleManager.interpPosY);
			fPosZ = (float)(this.zo + (this.z - this.zo) * partialTickTime - SAParticleManager.interpPosZ);
		}else{
			if(!mc.options.getCameraType().isFirstPerson()){
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
		
		RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
		RenderSystem.setShaderTexture(0, type.texture);
		enableBlendMode();
		int light=LightTexture.pack(15, 15);
		if(type.renderType == RenderTypeSA.ALPHA_SHADED){
			Level level = mc.level;
			BlockPos renderPos = new BlockPos((int)this.x, (int)this.y, (int)this.z);
			int blockLight = level.getBrightness(LightLayer.BLOCK, renderPos);
			int skyLight = level.getBrightness(LightLayer.SKY, renderPos);
			light = LightTexture.pack(blockLight, skyLight);
		}
		//RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);//POSITION_COLOR_TEX
        double a = (angle + (partialTickTime * angleRate)) * MathUtil.D2R;
		Vec3 p1, p2, p3, p4;
        if (prev == null) {
            this.pos1 = null; //new Vec3(fPosX, fPosY, fPosZ);
            this.pos2 = null; //new Vec3(fPosX, fPosY, fPosZ);
        }else {        	
    		Vec3 v_view = ClientProxy.get().getPlayerClient().getLookAngle();//getLook(partialTickTime);
    		//Vec3 v_prev = new Vec3(prev.getX(), prev.getY(), prev.getZ()).subtract(ClientProxy.get().getPlayerClient().getPositionEyes(partialTickTime));
    		Vec3 v_prev = new Vec3(prev.x, prev.y, prev.z).subtract(ClientProxy.get().getPlayerClient().getPosition(partialTickTime));
            /*System.out.printf("fPos: X=%.3f,  Y=%.3f, Z=%.3f\n", fPosX, fPosY, fPosZ);
            System.out.printf("PrevPos: X=%.3f,  Y=%.3f, Z=%.3f\n", v_prev.x, v_prev.y, v_prev.z);
            System.out.printf("PrevP1: X=%.3f,  Y=%.3f, Z=%.3f\n", prev.pos1.x, prev.pos1.y, prev.pos1.z);*/
    		Vec3 v_dir = v_prev.subtract(fPosX, fPosY, fPosZ).normalize();
    		Vec3 v_cross = v_view.cross(v_dir).normalize();
            p1 = new Vec3(v_cross.x*fscale + fPosX, v_cross.y*fscale  + fPosY, v_cross.z*fscale  + fPosZ);
            p2 = new Vec3(v_cross.x* -fscale + fPosX, v_cross.y* -fscale  + fPosY, v_cross.z* -fscale  + fPosZ);
            this.pos1 = p1;
            this.pos2 = p2;
            float fscaleP = prev.particleScale *0.1f;
            if (prev.pos1 != null && prev.pos2 != null) {
            	p3 = prev.pos2;
            	p4 = prev.pos1;
            }else {
            	p4 = new Vec3(v_cross.x*fscaleP + v_prev.x, v_cross.y*fscaleP  + v_prev.y, v_cross.z*fscaleP  + v_prev.z);
                p3 = new Vec3(v_cross.x* -fscaleP + v_prev.x, v_cross.y* -fscaleP  + v_prev.y, v_cross.z* -fscaleP  + v_prev.z);
                
                prev.pos1 = p4;
                prev.pos2 = p3;
            }
	        buffer.vertex(p1.x, p1.y+fixY, p1.z).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(ua, va).uv2(light).endVertex();
			buffer.vertex(p2.x, p2.y+fixY, p2.z).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(ub, vb).uv2(light).endVertex();
			buffer.vertex(p3.x, p3.y+fixY, p3.z).color(prev.rCol, prev.gCol, prev.bCol, prev.alpha).uv(uc, vc).uv2(light).endVertex();
			buffer.vertex(p4.x, p4.y+fixY, p4.z).color(prev.rCol, prev.gCol, prev.bCol, prev.alpha).uv(ud, vd).uv2(light).endVertex();
		}
        //Tesselator.getInstance().end();
		BufferUploader.drawWithShader(buffer.end());
        disableBlendMode();
		////System.out.println("DoRenderStreak");
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
