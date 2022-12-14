package ru.ffgs.tfchud;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.ffgs.tfchud.event.GuiEventHandler;

@Mod(
		modid = TerraFirmaHUD.MOD_ID,
		acceptableRemoteVersions = "*",
		dependencies = "after:appleskin;",
		clientSideOnly = true,
		useMetadata = true
)
public class TerraFirmaHUD {
	public static final String MOD_ID = "terrafirmahud";

	@SideOnly(Side.CLIENT)
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		if (Loader.isModLoaded("appleskin"))
			GuiEventHandler.hasAppleSkin = true;
	}
}
