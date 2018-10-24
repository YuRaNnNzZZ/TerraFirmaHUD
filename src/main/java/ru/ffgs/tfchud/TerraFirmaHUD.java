package ru.ffgs.tfchud;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.ffgs.tfchud.event.GuiEventHandler;

@Mod(
	modid = TerraFirmaHUD.MOD_ID,
	name = TerraFirmaHUD.MOD_NAME,
	version = TerraFirmaHUD.MOD_VERSION,
	acceptableRemoteVersions = "*",
	dependencies = "after:appleskin;",
	clientSideOnly = true
)
public class TerraFirmaHUD {
	public static final String MOD_ID = "terrafirmahud";
	public static final String MOD_NAME = "TerraFirmaHUD";
	public static final String MOD_VERSION = "@VERSION@";

	@SideOnly(Side.CLIENT)
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		if (Loader.isModLoaded("appleskin"))
			GuiEventHandler.hasAppleSkin = true;
	}
}
