package io.github.thebusybiscuit.slimefun4.implementation.items.food;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemConsumptionHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;

/**
 * {@link Soul} is a food that restores all hunger and has high saturation, but gives Wither II for 10 seconds.
 * 
 * @author B-Megamaths
 * 
 * @see SoulStealingSword
 *
 */
public class Soul extends SimpleSlimefunItem<ItemConsumptionHandler> {

    @ParametersAreNonnullByDefault
    public Soul(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public ItemConsumptionHandler getItemHandler() {
        return (e, p, item) -> Slimefun.runSync(() -> {
            if (p.hasPotionEffect(PotionEffectType.WITHER)) {
                p.removePotionEffect(PotionEffectType.WITHER);
            }
            p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 10, 2));
            p.setFoodLevel(20);
            p.setSaturation(p.getSaturation() + 18);
        }, 1L);
    }

}
