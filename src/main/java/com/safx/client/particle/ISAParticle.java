package safx.client.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface ISAParticle {

	public Vec3 getPos();
	
	public boolean shouldRemove();
	public void updateTick();
	
	public void doRender(BufferBuilder buffer, Entity entityIn, float partialTickTime, float rotX, float rotZ, float rotYZ, float rotXY, float rotXZ);
	   
	public AABB getRenderBoundingBox(float ptt, Entity viewEnt);
	
	public default boolean doNotSort() {
		return false;
	}
	public double getDepth();
	public void setDepth(double depth);
	public void setItemAttached();
}
