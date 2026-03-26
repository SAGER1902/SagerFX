package safx;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = SagerFX.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FXConfig
{
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	
    public static final ForgeConfigSpec.BooleanValue USE_OPT = BUILDER
            .comment("是否启动以下这些优化,优化要用到的检测距离这些可能也是一种消耗,你可以关闭它们然后像以前的版本一样渲染")
            .define("use_optimistic", true);
	
    public static final ForgeConfigSpec.IntValue RENDER_RANGE = BUILDER.comment("粒子特效的最大渲染距离,超过该距离后,不会渲染和创建新的粒子").defineInRange("render_range", 120, 1, 1000);
    public static final ForgeConfigSpec.IntValue RENDER_FPS = BUILDER.comment("粒子特效的帧数限制值,当游戏帧数低于这个值时,不会渲染和创建新的粒子").defineInRange("render_fps", 5, 0, 1000);
	

    static final ForgeConfigSpec SPEC = BUILDER.build();

    // 获取所有配置类别（用于GUI）
    public static ForgeConfigSpec getSpec() {
        return SPEC;
    }

	public static int render_range;
	public static int render_fps;
	
    public static void syncConfig() {
		render_range = RENDER_RANGE.get();
		render_fps = RENDER_FPS.get();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
		syncConfig();
    }
}
