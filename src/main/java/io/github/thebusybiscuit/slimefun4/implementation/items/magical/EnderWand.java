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
	 * The {@link EnderWand} is a very interesting magical weapon. It stems from the an Ender eye, which
	 * has the ability to teleport the user anywhere they wish to throw it, or a mob, if the user uses it 
	 * against it. Once a right click is made, such entity will vanish and appear at a random location.
	 * 
	 * @author Giulio Martini
	 *
	 */
	/* The following parameters are used to describe the effect the ender wand will have on its sorroundings. More specifically, the physical impact
	* of its effects (STRENGTH), its vertical range (HEIGHT), the damage caused by the item (DAMAGE), the minimum and maximum range it has to impact
	other entities (MIN_PLAYER_DISTANCE and MAX_GROUND_DISTANCE) and its radial range (RANGE). */
	public class EnderWand extends SimpleSlimefunItem<ItemUseHandler> implements NotPlaceable, DamageableItem {
	/* It can be observed that the Ender wand does not damage any entity, but only affects them in a way that will be described later. 
	* It possesses a wide radial range.
	*/
	    private static final float STRENGTH = 0F;
	    private static final float HEIGHT = 0.3F;
	    private static final float DAMAGE = 0;
	    private static final float MIN_PLAYER_DISTANCE = 0.5F;
	    private static final float MAX_GROUND_DISTANCE = 3F;
	    private static final int RANGE = 50;

	    @ParametersAreNonnullByDefault
	    public EnderWand(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
	        super(itemGroup, item, recipeType, recipe);
	    }
	/* Method that recalls the handler for this specific object. */
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

	    @ParametersAreNonnullByDefault
		private void createJumpingBlock(Block ground, Block blockAbove, int index) {
			Location loc = ground.getRelative(BlockFace.UP).getLocation().add(0.5, 0.0, 0.5);
			FallingBlock block = ground.getWorld().spawnFallingBlock(loc, ground.getBlockData());
			block.setDropItem(false);
			block.setVelocity(new Vector(0, 0.4 + index * 0.01, 0));
			block.setMetadata("ZeusLightning", new FixedMetadataValue(Slimefun.instance(), "fake_block"));
		}
	   
	    @ParametersAreNonnullByDefault
	    private boolean canReach(Location playerLocation, Location entityLocation, Location groundLocation) {
	        double maxGroundDistanceSquared = MAX_GROUND_DISTANCE * MAX_GROUND_DISTANCE;
	        double minPlayerDistanceSquared = MIN_PLAYER_DISTANCE * MIN_PLAYER_DISTANCE;
	        return entityLocation.distanceSquared(groundLocation) < maxGroundDistanceSquared 
	            && playerLocation.distanceSquared(entityLocation) > minPlayerDistanceSquared;
	    }

	    @ParametersAreNonnullByDefault
	    private void pushEntity(Player p, Entity entity) {
	            EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(p, entity, DamageCause.ENTITY_ATTACK, DAMAGE);
	            Bukkit.getPluginManager().callEvent(event);
	            if (!event.isCancelled()) {
	                Vector vector = entity.getLocation().toVector().subtract(p.getLocation().toVector()).normalize();
	                vector.multiply(STRENGTH);
	                vector.setY(HEIGHT);
	                try {
	                    entity.setVelocity(vector);
	                } catch (IllegalArgumentException x) {
	                   
	                    error("Exception while trying to set velocity: " + vector, x);
	                }
	                ((LivingEntity) entity).damage(event.getDamage());
	            }
	        
	    }

	   
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

	/* Method that informs the game that this item is damageable, either by explosion, lava or wear and tear. */
		@Override
	    public boolean isDamageable() {
	        return false;
	    }
	}
