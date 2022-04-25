package io.github.thebusybiscuit.slimefun4.implementation.items.weapons;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.DamageableItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.WorldUtils;

/**
 * The {@link ZeusLightning} is one interesting magical item. It holds the purpose of launching
 * every mob in the vicinty several blocks away, while shaking the ground beneath it
 * very much like an actual lightning, however, this is the almighty Zeus' lightning,
 * so it is even more powerful.
 * @author gmartini2019
 *
 */

/* Declaration of the class as a Slime fun item, which is damageable */
public class ZeusLightning extends SimpleSlimefunItem<ItemUseHandler> implements NotPlaceable, DamageableItem {

    /**
    * This parameters are in place to determine the characteristics of the item, such as the effect it will have on its sorrounding
    * and on the enemies (STRENGTH), its vertical range (HEIGHT), the damage done to each individual enemy (DAMAGE), the minimum
    * distance and the max distance that it can affect entities (MIN_PLAYER_DISTANCE and MAX_GROUND_DISTANCE) and its overall 
    * radius of range (RANGE).
    */
    private static final float STRENGTH = 2.0F;
    private static final float HEIGHT = 2.0F;
    private static final float DAMAGE = 18;
    private static final float MIN_PLAYER_DISTANCE = 0.1F;
    private static final float MAX_GROUND_DISTANCE = 5F;
    private static final int RANGE = 50;

    @ParametersAreNonnullByDefault
    public ZeusLightning(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    /*
    * This method is used to call upon the item's handler, which is an entierely different class.
    * It details the ways the item interacts with the world around it.
    */
    
    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            Player p = e.getPlayer();
            List<Block> blocks = p.getLineOfSight(null, RANGE);
            Set<UUID> pushedEntities = new HashSet<>();

            // Skip the first two, too close to the player.
            for (int i = 2; i < blocks.size(); i++) {
                Block ground = findGround(blocks.get(i));
                Location groundLocation = ground.getLocation();

                ground.getWorld().playEffect(groundLocation, Effect.STEP_SOUND, ground.getType());

                // Check if they have room above.
                Block blockAbove = ground.getRelative(BlockFace.UP);

                if (blockAbove.getType().isAir()) {
                    createJumpingBlock(ground, blockAbove, i);
                }

                for (Entity n : ground.getChunk().getEntities()) {
                    // @formatter:off
                    if (
                        n instanceof LivingEntity && n.getType() != EntityType.ARMOR_STAND
                        && !n.getUniqueId().equals(p.getUniqueId())
                        && canReach(p.getLocation(), n.getLocation(), groundLocation)
                        && pushedEntities.add(n.getUniqueId())
                    ) {
                        pushEntity(p, n);
                    }
                    // @formatter:on
                }
            }

            for (int i = 0; i < 4; i++) {
                damageItem(p, e.getItem());
            }
        };
    }

    /* 'createJumpingBlock' is a method used to make the ground beneath, ahead, on the side and behind the player
    * shake and rumble. This is done by making the affected blocks jump up and down, making any entity follow.
    */
    @ParametersAreNonnullByDefault
    private void createJumpingBlock(Block ground, Block blockAbove, int index) {
        Location loc = ground.getRelative(BlockFace.UP).getLocation().add(0.5, 0.0, 0.5);
        FallingBlock block = ground.getWorld().spawnFallingBlock(loc, ground.getBlockData());
        block.setDropItem(false);
        block.setVelocity(new Vector(0, 0.4 + index * 0.01, 0));
        block.setMetadata("ZeusLightning", new FixedMetadataValue(Slimefun.instance(), "fake_block"));
    }

    /* Calculates if the block or entity the player is trying to effect is in range. */
    
    @ParametersAreNonnullByDefault
    private boolean canReach(Location playerLocation, Location entityLocation, Location groundLocation) {
        // Too far away from ground
        double maxGroundDistanceSquared = MAX_GROUND_DISTANCE * MAX_GROUND_DISTANCE;

        // Fixes #3086 - Too close to Player, knockback may be NaN.
        double minPlayerDistanceSquared = MIN_PLAYER_DISTANCE * MIN_PLAYER_DISTANCE;

        // @formatter:off
        return entityLocation.distanceSquared(groundLocation) < maxGroundDistanceSquared 
            && playerLocation.distanceSquared(entityLocation) > minPlayerDistanceSquared;
        // @formatter:on
    }

    /* If the object, or entity, is in range, it will create a pushing effect by virtue of an invisible
    * vector between the player and the entity
    */
    @ParametersAreNonnullByDefault
    private void pushEntity(Player p, Entity entity) {
       
            EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(p, entity, DamageCause.ENTITY_ATTACK, DAMAGE);
            Bukkit.getPluginManager().callEvent(event);

            // Fixes #2207 - Only apply Vector if the Player is able to damage the entity
            if (!event.isCancelled()) {
                Vector vector = entity.getLocation().toVector().subtract(p.getLocation().toVector()).normalize();
                vector.multiply(STRENGTH);
                vector.setY(HEIGHT);

                try {
                    entity.setVelocity(vector);
                } catch (IllegalArgumentException x) {
                    /*
                     * Printing the actual vector here is much more verbose than just
                     * getting "x is not finite". See #3086
                     */
                    error("Exception while trying to set velocity: " + vector, x);
                }

                ((LivingEntity) entity).damage(event.getDamage());
            }
        
    }
/* finds the group upon which to strike on. */
    private @Nonnull Block findGround(@Nonnull Block b) {
        if (b.getType() == Material.AIR) {
            int minHeight = WorldUtils.getMinHeight(b.getWorld());
            for (int y = 0; b.getY() - y > minHeight; y++) {
                Block block = b.getRelative(0, -y, 0);

                if (block.getType() != Material.AIR) {
                    return block;
                }
            }
        }

        return b;
    }

/* 'isDamageable' determines whether an object is damageable either by explosion, or by wear and tear.
* In this case, it is.
*/
    @Override
    public boolean isDamageable() {
        return true;
    }

}
