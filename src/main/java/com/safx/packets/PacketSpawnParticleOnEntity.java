package safx.packets;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import safx.client.ClientProxy;
import safx.util.EntityCondition;
import safx.util.SALogger;

public class PacketSpawnParticleOnEntity implements IMessage {

	String name;
	int entityID;
	
	float offsetX = 0.0f;
	float offsetY = 0.0f;
	float offsetZ = 0.0f;
	
	float scale = 1.0f;
	
	boolean attachToHead = false;
	
	EntityCondition condition;
	
	public PacketSpawnParticleOnEntity() {};
	
	public PacketSpawnParticleOnEntity(String name, Entity ent) {
		this(name, ent, 0.0f, 0.0f, 0.0f, false, EntityCondition.NONE, 1.0f);
	}
	
	public PacketSpawnParticleOnEntity(String name, Entity ent, float offsetX, float offsetY, float offsetZ, boolean attachToHead) {
		this(name, ent, offsetX, offsetY, offsetZ, attachToHead, EntityCondition.NONE, 1.0f);
	}
	
	public PacketSpawnParticleOnEntity(String name, Entity ent, float offsetX, float offsetY, float offsetZ, boolean attachToHead, EntityCondition condition) {
		this(name, ent, offsetX, offsetY, offsetZ, attachToHead, condition, 1.0f);
	}
	
	public PacketSpawnParticleOnEntity(String name, Entity ent, float offsetX, float offsetY, float offsetZ, boolean attachToHead, EntityCondition condition, float scale) {
		super();
		this.name=name;
		this.entityID=ent.getEntityId();
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		this.attachToHead = attachToHead;
		this.condition = condition;
		this.scale = scale;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int len = buf.readShort();
		this.name = buf.readCharSequence(len, StandardCharsets.UTF_8).toString();
		
		this.entityID=buf.readInt();
		
		this.offsetX=buf.readFloat();
		this.offsetY=buf.readFloat();
		this.offsetZ=buf.readFloat();
		
		this.attachToHead = buf.readBoolean();
		
		this.condition = EntityCondition.fromByte(buf.readByte());
		this.scale = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		CharSequence cs = this.name;
		buf.writeShort(cs.length());
		buf.writeCharSequence(name, StandardCharsets.UTF_8);
		
		buf.writeInt(entityID);
		
		buf.writeFloat(this.offsetX);
		buf.writeFloat(this.offsetY);
		buf.writeFloat(this.offsetZ);
		
		buf.writeBoolean(this.attachToHead);
		
		if (condition == null) {
			buf.writeByte(EntityCondition.NONE.id);
		}
		else {
			buf.writeByte(condition.id);
		}
		buf.writeFloat(this.scale);
	}
	
	public static class Handler implements IMessageHandler<PacketSpawnParticleOnEntity, IMessage> {
		@Override
		public IMessage onMessage(PacketSpawnParticleOnEntity message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(PacketSpawnParticleOnEntity m, MessageContext ctx) {
			Entity ent = Minecraft.getMinecraft().player.world.getEntityByID(m.entityID);
			if (ent!=null){
//				if (!m.attachToHead && m.offsetX == 0 && m.offsetY == 0 && m.offsetZ ==0) {
//					ClientProxy.get().createFXOnEntity(m.name, ent);
//				}else {
					ClientProxy.get().createFXOnEntityWithOffset(m.name, ent, m.offsetX, m.offsetY, m.offsetZ, m.attachToHead, m.condition, m.scale);
//				}
			} else {
				SALogger.logger_client.warning("Got Packet for FX "+m.name+" on Entity, but ent was null");
			}
		}
	}
}
