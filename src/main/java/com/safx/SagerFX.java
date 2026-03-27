package safx;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.DistExecutor;
import safx.client.ClientProxy;
import safx.events.SAEventHandler;
import safx.events.SATickHandler;
import safx.CommonProxy;
@Mod(SagerFX.MODID)
public class SagerFX
{
	public static final String MODID = "safx";
	public static CommonProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    public SagerFX() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        MinecraftForge.EVENT_BUS.register(this);
		modEventBus.addListener(this::onCommonSetup);
        if (FMLLoader.getDist().isClient()) {
			MinecraftForge.EVENT_BUS.register(new SATickHandler());
			MinecraftForge.EVENT_BUS.register(new SAEventHandler());
		}
    }
	private void onCommonSetup(FMLCommonSetupEvent event)
    {
		proxy.preInit();
    }	
}