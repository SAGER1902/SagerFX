package safx.client.particle;

import java.util.Comparator;
import java.util.Iterator;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.world.entity.Entity;
import com.mojang.math.Axis;
import net.minecraft.world.phys.Vec3;
import safx.client.particle.list.ParticleList;
import safx.client.particle.list.ParticleList.ParticleListIterator;
import net.minecraft.client.Camera;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.GuiGraphics;

import net.minecraft.world.phys.AABB;
import net.minecraft.client.renderer.culling.Frustum;

import safx.FXConfig;
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
        if (effect == null) return;
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
	public void renderParticles(Entity playerIn, float partialTicks, Camera activeRenderInfoIn, PoseStack stack/*, Frustum frustum*/)
    {
        float f1 = (float)Math.cos(activeRenderInfoIn.getYRot() * 0.017453292F);
        float f2 = (float)Math.sin(activeRenderInfoIn.getYRot() * 0.017453292F);
        float f3 = -f2 * (float)Math.sin(activeRenderInfoIn.getXRot() * 0.017453292F);
        float f4 = f1 * (float)Math.sin(activeRenderInfoIn.getXRot() * 0.017453292F);
        float f5 = (float)Math.cos(activeRenderInfoIn.getXRot() * 0.017453292F);
		
		Vec3 came_vec = activeRenderInfoIn.getPosition();
		interpPosX = came_vec.x();
		interpPosY = came_vec.y();
		interpPosZ = came_vec.z();
		
        interpPosXs = playerIn.xOld + (playerIn.getX() - playerIn.xOld) * (double)partialTicks;
        interpPosYs = playerIn.yOld + (playerIn.getY() - playerIn.yOld) * (double)partialTicks;
        interpPosZs = playerIn.zOld + (playerIn.getZ() - playerIn.zOld) * (double)partialTicks;
		//RenderSystem.depthMask(true);
        RenderSystem.disableCull();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
		
		//float range = 0.5F;
        this.list.forEach(p -> {
			/*AABB aabb = new AABB(p.getPos().x-range, p.getPos().y-range, p.getPos().z-range, 
			p.getPos().x+range, p.getPos().y+range, p.getPos().z+range).inflate(1.0);*/
			// 视锥体剔除
			/*if (frustum.isVisible(p.getRenderBoundingBox(partialTicks,playerIn)))*/
			if(came_vec.distanceToSqr(p.getPos())<FXConfig.render_range*FXConfig.render_range){
				p.doRender(bufferbuilder, playerIn, partialTicks, f1, f5, f2, f3, f4);
			}
        });
        this.list_nosort.forEach(p -> {	
			/*if (frustum.isVisible(p.getRenderBoundingBox(partialTicks,playerIn)))*/
			if(came_vec.distanceToSqr(p.getPos())<FXConfig.render_range*FXConfig.render_range){
				p.doRender(bufferbuilder, playerIn, partialTicks, f1, f5, f2, f3, f4);
			}
        });//streak
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		//stack.popPose();
		//GL11.glPopMatrix();//glend
    }

	
	public double distanceToPlane(Entity viewEntity, Vec3 pos) {
		Vec3 n = viewEntity.getLookAngle();//getLookVec
		double dot1 = -n.dot(pos.subtract(viewEntity.getPosition(1F)));//getPositionVector
		double dot2 = n.dot(n);//dotProduct
		double f = dot1/dot2;
		Vec3 pos2 = pos.add(n.scale(f));
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
