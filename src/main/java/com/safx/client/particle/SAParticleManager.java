package safx.client.particle;

import java.util.Comparator;
import java.util.Iterator;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import safx.client.particle.list.ParticleList;
import safx.client.particle.list.ParticleList.ParticleListIterator;
import safx.client.render.GLStateSnapshot;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.ResourceLocation;

public class SAParticleManager {
    public static double interpPosX;
    public static double interpPosY;
    public static double interpPosZ;
	
    public static double interpPosXs;
    public static double interpPosYs;
    public static double interpPosZs;
	
	protected ParticleList<SAParticleSystem> list_systems = new ParticleList<>();
	protected ParticleList<ISAParticle> list = new ParticleList<>();
	protected ParticleList<ISAParticle> list_nosort = new ParticleList<>();
	protected ComparatorParticleDepth compare = new ComparatorParticleDepth();
	
	public void addEffect(ISAParticle effect)
    {
		String[] parts = Minecraft.getInstance().fpsString.split(" ");
        int fps = Integer.parseInt(parts[0]);
		
        if (effect == null||fps<5) return;
        if(effect instanceof SAParticleSystem) {
        	list_systems.add((SAParticleSystem) effect);
        } else {
        	if (effect.doNotSort()) {
        		list_nosort.add(effect);
        	} else {
        		list.add(effect);
        	}
        }
		//list.debugPrintList();//
    }
	
	public void tickParticles() {
		if(Minecraft.getInstance().isPaused()) return;
		
		Entity viewEnt = Minecraft.getInstance().getCameraEntity();
		
		Iterator<SAParticleSystem> sysit = list_systems.iterator();
		while(sysit.hasNext()) {
			SAParticleSystem p = sysit.next();
			
			p.updateTick();
			if(p.shouldRemove()) {
				sysit.remove();
			}
		}
		
		ParticleListIterator<ISAParticle> it = list.iterator();
		while(it.hasNext()) {
			ISAParticle p = it.next();
			
			p.updateTick();
			if(p.shouldRemove()) {
				it.remove();
			} else {
				if(viewEnt!=null) {
					p.setDepth(this.distanceToPlane(viewEnt, p.getPos()));
				}
			}
		}
		
		Iterator<ISAParticle> it2 = list_nosort.iterator();
		while(it2.hasNext()) {
			ISAParticle p = it2.next();
			
			p.updateTick();
			if(p.shouldRemove()) {
				it2.remove();
			}
		}
		//list.debugPrintList();
	}
	/**
	 * 
	 * @param playerIn renderViewEntity
	 * @param partialTick
	 */
	public void renderParticles(Entity playerIn, float partialTicks)
    {
        float f1 = MathHelper.cos(playerIn.yRot * 0.017453292F);
        float f2 = MathHelper.sin(playerIn.yRot * 0.017453292F);
        float f3 = -f2 * MathHelper.sin(playerIn.xRot * 0.017453292F);
        float f4 = f1 * MathHelper.sin(playerIn.xRot * 0.017453292F);
        float f5 = MathHelper.cos(playerIn.xRot * 0.017453292F);
		//GL11.glPushMatrix();//glstart
		GlStateManager._pushMatrix();
		Minecraft mc = Minecraft.getInstance();
		ActiveRenderInfo activeRenderInfoIn = Minecraft.getInstance().getEntityRenderDispatcher().camera;
		Vector3d camera = activeRenderInfoIn.getPosition();
		interpPosX = camera.x();
		interpPosY = camera.y()-1.5;
		interpPosZ = camera.z();
		
		activeRenderInfoIn.setup(mc.level, (Entity)(mc.getCameraEntity() == null ? mc.player : mc.getCameraEntity()), !mc.options.getCameraType().isFirstPerson(), mc.options.getCameraType().isMirrored(), partialTicks);
		net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup cameraSetup = net.minecraftforge.client.ForgeHooksClient.onCameraSetup(mc.gameRenderer, activeRenderInfoIn, partialTicks);
		activeRenderInfoIn.setAnglesInternal(cameraSetup.getYaw(), cameraSetup.getPitch());
		
		GlStateManager._rotatef(cameraSetup.getRoll(), 0.0F, 0.0F, 1.0F);
		GlStateManager._rotatef(activeRenderInfoIn.getXRot(), 1.0F, 0.0F, 0.0F);
		GlStateManager._rotatef(activeRenderInfoIn.getYRot() + 180.0F, 0.0F, 1.0F, 0.0F);
		
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        
        interpPosXs = playerIn.xOld + (playerIn.getX() - playerIn.xOld) * (double)partialTicks;
        interpPosYs = playerIn.yOld + (playerIn.getY() - playerIn.yOld) * (double)partialTicks;
        interpPosZs = playerIn.zOld + (playerIn.getZ() - playerIn.zOld) * (double)partialTicks;
        
        GlStateManager._disableCull();
        this.list.forEach(p -> {	
        	p.doRender(bufferbuilder, playerIn, partialTicks, f1, f5, f2, f3, f4);
        });
        
        this.list_nosort.forEach(p -> {	
        	p.doRender(bufferbuilder, playerIn, partialTicks, f1, f5, f2, f3, f4);
        });
        GlStateManager._color4f(1f, 1f, 1f, 1f);
		GlStateManager._popMatrix();
		//GL11.glPopMatrix();//glend
    }

	
	public double distanceToPlane(Entity viewEntity, Vector3d pos) {
		//Formula from: http://geomalgorithms.com/a04-_planes.html
		Vector3d n = viewEntity.getLookAngle();//getLookVec
		double dot1 = -n.dot(pos.subtract(viewEntity.getPosition(1F)));//getPositionVector
		double dot2 = n.dot(n);//dotProduct
		double f = dot1/dot2;
		
		Vector3d pos2 = pos.add(n.scale(f));
		return pos.distanceToSqr(pos2);
	}
	
	public static class ComparatorParticleDepth implements Comparator<ISAParticle> {

		@Override
		public int compare(ISAParticle p1, ISAParticle p2) {
			if(p1.doNotSort() && p2.doNotSort()) {
				return 0;
			}
			//Entity view = Minecraft.getInstance().getRenderViewEntity();
			//if(view!=null) {
				double dist1=p1.getDepth();
				double dist2=p2.getDepth();
				//double dist1 = p1.getPos().squareDistanceTo(view.getX(), view.getY(), view.getZ());
				//double dist2 = p2.getPos().squareDistanceTo(view.getX(), view.getY(), view.getZ());
				
				if(dist1<dist2) {
					return 1;
				} else if(dist1>dist2) {
					return -1;
				} else {
					return 0;
				}
			//}
			//return 0;
		}
		

	}
}
