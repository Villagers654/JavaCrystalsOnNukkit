package club.aurorapvp.events.listeners;

import club.aurorapvp.JavaCrystalsOnNukkit;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityEndCrystal;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockIterator;

public class ExplosionEvents implements Listener {
  private static final double HEAD_DAMAGE_MULTIPLIER = 0.17;
  private static final double MAX_DAMAGE = 160.0;
  private static final double MIN_DAMAGE = 1.0;
  private static final double BLOCKING_REDUCTION_FACTOR = 0.66;
  private static final int EXPLOSION_RADIUS = 10;

  @EventHandler
  public void onEntityExplode(EntityExplodeEvent event) {
    long startTime = System.currentTimeMillis();

    Entity explodingEntity = event.getEntity();
    if (!(explodingEntity instanceof EntityEndCrystal)) {
      return;
    }

    event.setCancelled(true);

    Level level = explodingEntity.getLevel();
    Location explosionLocation = explodingEntity.getLocation();

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
        return;
      }

      Location playerLocation = p.getLocation();
      double distance = explosionLocation.distance(playerLocation);

      if (distance > (double) EXPLOSION_RADIUS) {
        continue;
      }

      JavaCrystalsOnNukkit.LOGGER.info("Distance from explostion: " + distance);

      Vector3 playerHead = playerLocation.add(0, 1.62, 0);
      double damage = calculateDamage(distance, p, playerLocation, playerHead, explosionLocation);
      EntityDamageByEntityEvent damageEvent =
          new EntityDamageByEntityEvent(explodingEntity, p,
              EntityDamageByEntityEvent.DamageCause.ENTITY_EXPLOSION, (float) damage);
      level.getServer().getPluginManager().callEvent(damageEvent);
      if (!damageEvent.isCancelled()) {
        JavaCrystalsOnNukkit.LOGGER.info("Final calculated damage: " + damage);

        p.attack(damageEvent);

        JavaCrystalsOnNukkit.LOGGER.info(
            "Explosion calculated in " + (System.currentTimeMillis() - startTime) + "ms");
      }
    }
  }

  public double calculateDamage(double distance, Player p, Vector3 playerFeet, Vector3 playerHead,
                                Vector3 explosionLocation) {
    double damage;
    double distanceFactor = 1 - distance / EXPLOSION_RADIUS;

    if (isExposed(p, playerFeet, explosionLocation)) {
      damage = distanceFactor * (MAX_DAMAGE - MIN_DAMAGE) + MIN_DAMAGE;
      JavaCrystalsOnNukkit.LOGGER.info("Feet are exposed");
    } else if (isExposed(p, playerHead, explosionLocation)) {
      damage = distanceFactor * (MAX_DAMAGE - MIN_DAMAGE) * HEAD_DAMAGE_MULTIPLIER + MIN_DAMAGE;
      JavaCrystalsOnNukkit.LOGGER.info("Head is exposed");
    } else {
      damage = 0;
      JavaCrystalsOnNukkit.LOGGER.info("Not exposed");
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