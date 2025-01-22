package tgfx;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tgfx.init.ITGInitializer;

@Mod(modid = TechgunsFX.MODID, version = TechgunsFX.VERSION, name=TechgunsFX.NAME, acceptedMinecraftVersions=TechgunsFX.MCVERSION, /*guiFactory=TechgunsFX.GUI_FACTORY, updateJSON=TechgunsFX.UPDATEURL,*/ dependencies=TechgunsFX.DEPENDENCIES)
public class TechgunsFX
{
    public static final String MODID = "tgfx";
    public static final String MCVERSION = "1.12.2";
    public static final String VERSION = "1.0.0.0";
    public static final String NAME = "TechgunsFX";
    //public static final String GUI_FACTORY = "tgfx.gui.config.GuiFactoryTechguns";
    //public static final String UPDATEURL = "https://raw.githubusercontent.com/pWn3d1337/Techguns2/master/update.json";
    public static final String FORGE_BUILD = "14.23.5.2860";
    public static final String DEPENDENCIES = "required:forge@["+FORGE_BUILD+",);after:ftblib;after:chisel;after:patchouli";
    
    @Mod.Instance
    public static TechgunsFX instance;
    
    @SidedProxy(clientSide = "tgfx.client.ClientProxy", serverSide = "tgfx.server.ServerProxy")
    public static CommonProxy proxy;
    
    public TGPackets packets = new TGPackets();
 
    //Mod integration
    public boolean FTBLIB_ENABLED=false;
    public boolean CHISEL_ENABLED=false;
    
    protected ITGInitializer[] initializers = {
    	packets
    };
    
    
	/*public static CreativeTabs tabTechgun = new CreativeTabs(TechgunsFX.MODID) {
		
	    @Override
	    @SideOnly(Side.CLIENT)
	    public ItemStack getTabIconItem() {
	        return TGItems.newStack(TGItems.PISTOL_ROUNDS,1);
	    }

		@Override
		public String getTranslatedTabLabel() {
			return TechgunsFX.MODID+"."+super.getTranslatedTabLabel();
		}

		@Override
		public boolean hasSearchBar() {
			return true;
		}
	};
	static {
		tabTechgun.setBackgroundImageName("item_search.png");
	};*/
	
	public static int modEntityID=-1;
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	/*if(Loader.isModLoaded("ftblib")) {
    		FTBLIB_ENABLED=true;
    	}
    	if(Loader.isModLoaded("chisel")) {
    		CHISEL_ENABLED=true;
    	}*/
    	
    	for (ITGInitializer init : initializers){
    		init.init(event);
    	}
    	proxy.init(event);
    }
    
    @EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
    	TGConfig.init(event);
    	for (ITGInitializer init : initializers){
    		init.preInit(event);
    	}
    	proxy.preInit(event);
    }
    
    @EventHandler
    public void postinit(FMLPostInitializationEvent event)
    {
    	for (ITGInitializer init : initializers){
    		init.postInit(event);
    	}
    	proxy.postInit(event);
    }
}