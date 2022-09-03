package ru.ffgs.tfchud.event;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.ffgs.tfchud.TerraFirmaHUD;

@Mod.EventBusSubscriber(modid = TerraFirmaHUD.MOD_ID, value = {Side.CLIENT})
public class GuiEventHandler {
	private static final ResourceLocation icons = new ResourceLocation(TerraFirmaHUD.MOD_ID, "textures/gui/icons.png");

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
		int heightLeft = height - GuiIngameForge.left_height;
		int heightRight = height - GuiIngameForge.right_height;

		int xLeft = centerWidth - 90 - 1;
		int xRight = centerWidth + 8 + 2;

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(icons);

		if (mc.playerController.shouldDrawHUD() && mc.getRenderViewEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();

			switch (event.getType()) {
				case ALL:
					GuiIngameForge.renderFood = true;
					break;
				case AIR:
					float air = player.getAir();
					float airFill = Math.min(air / 300, 1);

					if (player.isInsideOfMaterial(Material.WATER) || airFill < 1) {
						GlStateManager.enableBlend();

						ingameGUI.drawTexturedModalRect(xRight, heightRight, 0, 36, 81, 9);
						ingameGUI.drawTexturedModalRect(xRight, heightRight, 0, 45, (int) (airFill * 81), 9);

						GlStateManager.disableBlend();

						GuiIngameForge.left_height += 10;
					}

					event.setCanceled(true);
					break;

				case HEALTH:
					float health = player.getHealth();
					float maxHealth = player.getMaxHealth();

					boolean withered = player.isPotionActive(MobEffects.WITHER); // НАПУГАН :ghost:

					String healthString = String.format("%d/%d", (int) health, (int) maxHealth);

					float healthFill = Math.min(health / maxHealth, 1);

					float absorption = player.getAbsorptionAmount();
					float absorptionFill = Math.min(absorption / maxHealth, 1);

					GlStateManager.enableBlend();

					ingameGUI.drawTexturedModalRect(xLeft, heightLeft, 0, 0, 81, 9);
					ingameGUI.drawTexturedModalRect(xLeft, heightLeft, 0, withered ? 27 : 9, (int) (healthFill * 81), 9);

					if (absorption > 0) {
						ingameGUI.drawTexturedModalRect(xLeft, heightLeft, 0, 18, (int) (absorptionFill * 81), 9);

						healthString = String.format("%d+%d/%d", (int) health, (int) absorption, (int) maxHealth);
					}

					drawStringOutlined(mc.fontRenderer, healthString, xLeft + 40 - (mc.fontRenderer.getStringWidth(healthString) / 2), heightLeft + 1, 0xffffffff, 0x7f000000);

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

					float exhaustion = ObfuscationReflectionHelper.getPrivateValue(FoodStats.class, foodStats, "field_75126_c", "foodExhaustionLevel");
					float exhaustionFill = Math.min(exhaustion / 4, 1);

					GlStateManager.enableBlend();

					if (hasAppleSkin && exhaustionFill > 0) {
						int exhaustionWidth = (int) (exhaustionFill * 81);

						ingameGUI.drawTexturedModalRect(xRight, heightRight - 1, 81, 27, exhaustionWidth, 4);
						ingameGUI.drawTexturedModalRect(xRight, heightRight + 6, 81, 32, exhaustionWidth, 4);
					}

					ingameGUI.drawTexturedModalRect(xRight, heightRight, 81, 0, 81, 9);
					ingameGUI.drawTexturedModalRect(xRight, heightRight, 81, 9, (int) (hungerFill * 81), 9);

					if (hasAppleSkin) {
						ingameGUI.drawTexturedModalRect(xRight, heightRight, 81, 18, (int) (saturationFill * 81), 9);
					}

					ItemStack heldStack = player.getHeldItemMainhand();
					if (heldStack.isEmpty() || !(heldStack.getItem() instanceof ItemFood)) {
						heldStack = player.getHeldItemOffhand();
					}
					if (!heldStack.isEmpty() && heldStack.getItem() instanceof ItemFood) {
						ItemFood food = (ItemFood) heldStack.getItem();

						int healAmount = food.getHealAmount(heldStack);
						float saturationModifier = food.getSaturationModifier(heldStack);

						if (healAmount > 0) {
							float targetFoodLevel = Math.min(healAmount + hunger, 20);
							float targetFoodSaturationLevel = Math.min(saturation + (float)healAmount * saturationModifier * 2.0F, targetFoodLevel);

							float targetFoodFill = Math.min(targetFoodLevel / 20, 1);
							float targetSaturationFill = Math.min(targetFoodSaturationLevel / 20, 1);

							GlStateManager.color(1.0F, 1.0F, 1.0F, MathHelper.abs(MathHelper.sin((float)(Minecraft.getSystemTime() % 1000L) / 1000.0F * (float)Math.PI)));

							ingameGUI.drawTexturedModalRect(xRight + (int) (hungerFill * 81), heightRight, 81 + (int) (hungerFill * 81), 9, (int) ((targetFoodFill - hungerFill) * 81), 9);

							if (hasAppleSkin) {
								ingameGUI.drawTexturedModalRect(xRight + (int) (saturationFill * 81), heightRight, 81 + (int) (saturationFill * 81), 18, (int) ((targetSaturationFill - saturationFill) * 81), 9);
							}

							GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
						}
					}

					GlStateManager.disableBlend();

					GuiIngameForge.right_height += 10;

					event.setCanceled(true);
					break;
				case HEALTHMOUNT:
					Entity tmp = player.getRidingEntity();

					if (tmp instanceof EntityLivingBase) {
						EntityLivingBase mount = (EntityLivingBase) tmp;

						float mountHealth = mount.getHealth();
						float mountMaxHealth = mount.getMaxHealth();
						float mountHealthFill = Math.min(mountHealth / mountMaxHealth, 1);

						GlStateManager.enableBlend();

						ingameGUI.drawTexturedModalRect(xRight, heightRight, 162, 0, 81, 9);
						ingameGUI.drawTexturedModalRect(xRight, heightRight, 162, 9, (int) (mountHealthFill * 81), 9);

						String mountHealthString = String.format("%d/%d", (int) mountHealth, (int) mountMaxHealth);
						drawStringOutlined(mc.fontRenderer, mountHealthString, xRight + 41 - (mc.fontRenderer.getStringWidth(mountHealthString) / 2), heightRight + 1, 0xffffffff, 0x7f000000);

						GlStateManager.disableBlend();

						GuiIngameForge.right_height += 10;
					}

					event.setCanceled(true);
					break;
			}
		}

		mc.getTextureManager().bindTexture(Gui.ICONS);
	}

	private static void drawStringOutlined(FontRenderer fontRenderer, String text, int x, int y, int colorMain, int colorOutline) {
		fontRenderer.drawString(text, x + 1, y, colorOutline);
		fontRenderer.drawString(text, x - 1, y, colorOutline);
		fontRenderer.drawString(text, x, y + 1, colorOutline);
		fontRenderer.drawString(text, x, y - 1, colorOutline);

		fontRenderer.drawString(text, x, y, colorMain);
	}
}
