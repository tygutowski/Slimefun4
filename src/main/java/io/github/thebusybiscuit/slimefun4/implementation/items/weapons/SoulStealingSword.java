package io.github.thebusybiscuit.slimefun4.implementation.items.weapons;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.EntityKillHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;

/**
 * The {@link SoulStealingSword} is a special kind of sword which allows you to obtain
 * the {@link Soul} item by killing any {@link LivingEntity}.
 * Souls can be eaten or provide an easy way to craft Soul Sticks.
 * The odds for dropping a Soul are managed by an ItemSetting and can be customised.
 * 
 * @author B-Megamaths
 *
 * @see SwordOfBeheading
 * @see Soul
 *
 */
public class SoulStealingSword extends SimpleSlimefunItem<EntityKillHandler> {

    private final ItemSetting<Integer> chanceDropSoul = new IntRangeSetting(this, "chance.DROP_SOUL", 0, 100, 100);

    @ParametersAreNonnullByDefault
    public SoulStealingSword(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        addItemSetting(chanceDropSoul);
    }

    @Override
    public EntityKillHandler getItemHandler() {
        return (e, entity, killer, item) -> {
			Random random = ThreadLocalRandom.current();
            switch (e.getEntityType()) {
                case AXOLOTL:
                case BAT:
                case BEE:
                case BLAZE:
                case CAT:
                case CAVE_SPIDER:
                case CHICKEN:
                case COD:
                case COW:
                case CREEPER:
                case DOLPHIN:
                case DONKEY:
                case DROWNED:
                case ELDER_GUARDIAN:
                case ENDERMAN:
                case ENDERMITE:
                case EVOKER:
                case FOX:
                case GHAST:
                case GIANT:
                case GLOW_SQUID:
                case GOAT:
                case GUARDIAN:
                case HOGLIN:
                case HORSE:
                case HUSK:
                case ILLUSIONER:
                case IRON_GOLEM:
                case LLAMA:
                case MAGMA_CUBE:
                case MULE:
                case MUSHROOM_COW:
                case OCELOT:
                case PANDA:
                case PARROT:
                case PHANTOM:
                case PIG:
                case PIGLIN:
                case PIGLIN_BRUTE:
                case PILLAGER:
                case POLAR_BEAR:
                case PUFFERFISH:
                case RABBIT:
                case RAVAGER:
                case SALMON:
                case SHEEP:
                case SHULKER:
                case SILVERFISH:
                case SKELETON:
                case SKELETON_HORSE:
                case SLIME:
                case SNOWMAN:
                case SPIDER:
                case SQUID:
                case STRAY:
                case TRADER_LLAMA:
                case TROPICAL_FISH:
                case TURTLE:
                case VILLAGER:
                case VINDICATOR:
                case WANDERING_TRADER:
                case WITCH:
                case WITHER:
                case WITHER_SKELETON:
                case WOLF:
                case ZOGLIN:
                case ZOMBIE:
                case ZOMBIE_HORSE:
                case ZOMBIE_VILLAGER:
                case ZOMBIFIED_PIGLIN:
                    if (random.nextInt(100) < chanceDropSoul.getValue()) {
                        e.getDrops().add(SlimefunItems.SOUL);
                    }
                    break;
                default:
                    break;
            }
        };
    }

}
