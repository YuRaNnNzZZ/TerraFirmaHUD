package ru.ffgs.tfchud.proxy;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import ru.ffgs.tfchud.event.GuiEventHandler;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        if (Loader.isModLoaded("applecore"))
            GuiEventHandler.hasAppleCore = true;

        MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
    }
}
