package safx.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Predicate;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import safx.SagerFX;

import safx.client.ClientProxy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
@Mod.EventBusSubscriber(modid = SagerFX.MODID)
public class SATickHandler {
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void TickParticleSystems(TickEvent.ClientTickEvent event) {
		if(event.phase==TickEvent.Phase.END) {
			ClientProxy.get().particleManager.tickParticles();
			//System.out.println("SAParticleCount:"+ClientProxy.get().particleManager.getList().getSizeDebug()+ " :: "+ClientProxy.get().particleManager.getList().getSize());
		}
	}
}
