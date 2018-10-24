package ru.ffgs.tfchud.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.ffgs.tfchud.TerraFirmaHUD;

@Mod.EventBusSubscriber(modid = TerraFirmaHUD.MOD_ID, value = {Side.CLIENT})
public class GuiEventHandler {
	private static ResourceLocation icons = new ResourceLocation(TerraFirmaHUD.MOD_ID, "textures/gui/icons.png");

	public static boolean hasAppleSkin = false;

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onRenderGameOverlay(RenderGameOverlayEvent event) {
		ScaledResolution res = event.getResolution();
		Minecraft mc = Minecraft.getMinecraft();
		GuiIngame ingameGUI = mc.ingameGUI; // drawTexturedModalRect is not a static so the instance of overlay gui will do

		int width = res.getScaledWidth();
		int height = res.getScaledHeight();

		int centerWidth = width / 2;
		int baseHeight = height - 39;

		int xLeft = centerWidth - 90 - 1;
		int xRight = centerWidth + 8 + 1;

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(icons);

		if (mc.playerController.shouldDrawHUD() && mc.getRenderViewEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();

			switch (event.getType()) {
				case ALL:
					GuiIngameForge.renderFood = true;
					break;
				case HEALTH:
					float health = player.getHealth();
					float maxHealth = player.getMaxHealth();
					float healthFill = Math.min(health / maxHealth, 1);

					GlStateManager.enableBlend();

					ingameGUI.drawTexturedModalRect(xLeft, baseHeight, 0, 0, 81, 9);
					ingameGUI.drawTexturedModalRect(xLeft, baseHeight, 0, 9, (int) (healthFill * 81), 9);

					String healthString = String.format("%d/%d", (int) health, (int) maxHealth);
					mc.fontRenderer.drawString(healthString, xLeft + 40 - (mc.fontRenderer.getStringWidth(healthString) / 2), baseHeight + 1, 0xffffffff);

					GlStateManager.disableBlend();

					GuiIngameForge.left_height += 10;

					event.setCanceled(true);
					break;
				case FOOD:
					FoodStats foodStats = player.getFoodStats();

					float hunger = foodStats.getFoodLevel();
					float hungerFill = Math.min(hunger / 20, 1);

					float saturation = foodStats.getSaturationLevel();
					float saturationFill = Math.min(saturation / 20, 1);

					float exhaustion = ReflectionHelper.getPrivateValue(FoodStats.class, foodStats, "foodExhaustionLevel");
					float exhaustionFill = Math.min(exhaustion / 4, 1);

					GlStateManager.enableBlend();

					if (hasAppleSkin && exhaustionFill > 0) {
						int exhaustionWidth = (int) (exhaustionFill * 81);

						ingameGUI.drawTexturedModalRect(xRight, baseHeight - 1, 81, 27, exhaustionWidth, 4);
						ingameGUI.drawTexturedModalRect(xRight, baseHeight + 6, 81, 32, exhaustionWidth, 4);
					}

					ingameGUI.drawTexturedModalRect(xRight, baseHeight, 81, 0, 81, 9);
					ingameGUI.drawTexturedModalRect(xRight, baseHeight, 81, 9, (int) (hungerFill * 81), 9);

					if (hasAppleSkin) {
						ingameGUI.drawTexturedModalRect(xRight, baseHeight, 81, 18, (int) (saturationFill * 81), 9);
					}

					GlStateManager.disableBlend();

					GuiIngameForge.right_height += 10;

					event.setCanceled(true);
					break;
				case HEALTHMOUNT:
					Entity tmp = player.getRidingEntity();
					if (!(tmp instanceof EntityLivingBase)) break;

					EntityLivingBase mount = (EntityLivingBase) tmp;

					float mountHealth = mount.getHealth();
					float mountMaxHealth = mount.getMaxHealth();
					float mountHealthFill = Math.min(mountHealth / mountMaxHealth, 1);

					GlStateManager.enableBlend();

					ingameGUI.drawTexturedModalRect(xRight, baseHeight - 10, 162, 0, 81, 9);
					ingameGUI.drawTexturedModalRect(xRight, baseHeight - 10, 162, 9, (int) (mountHealthFill * 81), 9);

					String mountHealthString = String.format("%d/%d", (int) mountHealth, (int) mountMaxHealth);
					mc.fontRenderer.drawString(mountHealthString, xRight + 41 - (mc.fontRenderer.getStringWidth(mountHealthString) / 2), baseHeight - 9, 0xffffffff);

					GlStateManager.disableBlend();

					GuiIngameForge.right_height += 10;

					event.setCanceled(true);
					break;
			}
		}

		mc.getTextureManager().bindTexture(Gui.ICONS);
	}
}
