package tgfx.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Predicate;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tgfx.TGConfig;
import tgfx.TGPackets;

import tgfx.TechgunsFX;

import tgfx.client.ClientProxy;

import tgfx.deatheffects.EntityDeathUtils.DeathType;

@Mod.EventBusSubscriber(modid = TechgunsFX.MODID)
public class TGTickHandler {
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void TickParticleSystems(ClientTickEvent event) {
		if(event.phase==Phase.END) {
			ClientProxy.get().particleManager.tickParticles();
			//System.out.println("TGParticleCount:"+ClientProxy.get().particleManager.getList().getSizeDebug()+ " :: "+ClientProxy.get().particleManager.getList().getSize());
		}
	}
}
