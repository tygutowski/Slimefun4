package io.github.thebusybiscuit.slimefun4.implementation.items.food;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.bakedlibs.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;

/**
 * {@link Soul} is a food that restores all hunger and has high saturation, but gives Wither II for 10 seconds.
 * 
 * @author B-Megamaths
 * 
 * @see SoulStealingSword
 *
 */
public class Soul extends SimpleSlimefunItem<ItemUseHandler>{

	private final ItemSetting<Integer> saturation = new IntRangeSetting(this, "saturation-level", 0, 6, Integer.MAX_VALUE);
	
    @ParametersAreNonnullByDefault
    public Soul(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
		
		
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            // Check if it is being placed into an ancient altar.
            if (e.getClickedBlock().isPresent()) {
                Material block = e.getClickedBlock().get().getType();

                if (block == Material.DISPENSER || block == Material.ENCHANTING_TABLE) {
                    return;
                }
            }

            Player p = e.getPlayer();

            if (p.getGameMode() != GameMode.CREATIVE) {
                ItemUtils.consumeItem(e.getItem(), false);
            }
			
			if (p.getFoodLevel() <= 19) {
				p.setFoodLevel(p.getFoodLevel() + 2);
				p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EAT, 1, 1);
				p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 120, 2));
				p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 30, 2));
			}
			else {
				ItemUtils.consumeItem(e.getItem(), false);
			}
			
        };
    }

}
