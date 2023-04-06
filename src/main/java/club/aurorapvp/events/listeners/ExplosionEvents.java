package club.aurorapvp.events.listeners;

import club.aurorapvp.configs.CustomConfig;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityEndCrystal;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockIterator;
import java.util.List;

public class ExplosionEvents implements Listener {
  private static final double HEAD_DAMAGE_MULTIPLIER = CustomConfig.getConfig().getDouble("damage.head-damage-multiplier");
  private static final double MAX_DAMAGE = CustomConfig.getConfig().getDouble("damage.max-damage");
  private static final double MIN_DAMAGE = CustomConfig.getConfig().getDouble("damage.min-damage");;
  private static final double BLOCKING_REDUCTION_FACTOR = CustomConfig.getConfig().getDouble("damage.blocking-reduction-multiplier");
  private static final int EXPLOSION_RADIUS = CustomConfig.getConfig().getInt("radius");

  @EventHandler
  public void onEntityExplode(EntityExplodeEvent event) {
    Entity explodingEntity = event.getEntity();
    if (!(explodingEntity instanceof EntityEndCrystal)) {
      return;
    }

    event.setYield(0);

    Level level = explodingEntity.getLevel();
    Location explosionLocation = explodingEntity.getLocation();

    breakBlocks(event.getBlockList(), level);

    for (Entity e : level.getEntities()) {
      if (e instanceof EntityEndCrystal endCrystal) {
        Location entityLocation = e.getLocation();
        double distance = explosionLocation.distance(entityLocation);

        if (distance > (double) EXPLOSION_RADIUS) {
          continue;
        }

        endCrystal.explode();
        continue;
      }

      if (!(e instanceof Player p)) {
        continue;
      }

      Location playerLocation = p.getLocation();
      double distance = explosionLocation.distance(playerLocation);

      if (distance > (double) EXPLOSION_RADIUS) {
        continue;
      }

      Vector3 playerHead = playerLocation.add(0, 1.62, 0);

      double damage = calculateDamage(distance, p, playerLocation, playerHead, explosionLocation);

      EntityDamageByEntityEvent damageEvent =
          new EntityDamageByEntityEvent(explodingEntity, p,
              EntityDamageByEntityEvent.DamageCause.ENTITY_EXPLOSION, (float) damage);
      level.getServer().getPluginManager().callEvent(damageEvent);

      if (!damageEvent.isCancelled()) {
        p.attack(damageEvent);
      }
    }
  }

  public void breakBlocks(List<Block> blocks, Level level) {
    for (Block block : blocks) {
      Location blockLocation = block.getLocation();

      Item[] drops = block.getDrops(Item.getCreativeItem(Item.AIR));
      level.setBlock(blockLocation, Block.get(BlockID.AIR));
      for (Item drop : drops) {
        level.dropItem(blockLocation, drop);
      }
    }
  }

  public double calculateDamage(double distance, Player p, Vector3 playerFeet, Vector3 playerHead,
                                Vector3 explosionLocation) {
    double damage;
    double distanceFactor = 1 - distance / EXPLOSION_RADIUS;

    if (isExposed(p, playerFeet, explosionLocation)) {
      damage = distanceFactor * (MAX_DAMAGE - MIN_DAMAGE) + MIN_DAMAGE;
    } else if (isExposed(p, playerHead, explosionLocation)) {
      damage = distanceFactor * (MAX_DAMAGE - MIN_DAMAGE) * HEAD_DAMAGE_MULTIPLIER + MIN_DAMAGE;
    } else {
      damage = 0;
    }

    if (p.isSneaking()) {
      damage *= BLOCKING_REDUCTION_FACTOR;
    }

    return damage;
  }

  public boolean isExposed(Player p, Vector3 position, Vector3 explosionLocation) {
    Vector3 direction = explosionLocation.subtract(position);
    BlockIterator iterator =
        new BlockIterator(p.getLevel(), position.add(0.5, 0.5, 0.5), direction.normalize(), 0,
            (int) Math.ceil(direction.length()));
    while (iterator.hasNext()) {
      Block block = iterator.next();
      if (block.isSolid()) {
        if (block.getLocation().distanceSquared(explosionLocation) <
            position.distanceSquared(explosionLocation)) {
          return false;
        }
      }
    }
    return true;
  }
}