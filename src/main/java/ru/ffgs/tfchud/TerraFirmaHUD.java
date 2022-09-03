package ru.ffgs.tfchud;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ru.ffgs.tfchud.proxy.CommonProxy;

@Mod(
		modid = TerraFirmaHUD.MOD_ID,
		acceptableRemoteVersions = "*",
		dependencies = "after:applecore;",
		useMetadata = true
)
public class TerraFirmaHUD {
	public static final String MOD_ID = "terrafirmahud";

	@SidedProxy(serverSide = "ru.ffgs.tfchud.proxy.CommonProxy", clientSide = "ru.ffgs.tfchud.proxy.ClientProxy")
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}
}
