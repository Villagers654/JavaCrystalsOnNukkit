package club.aurorapvp.events.listeners;

import cn.nukkit.entity.item.EntityEndCrystal;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEntityEvent;

public class InteractEvents implements Listener {
  @EventHandler
  public void onInteract(PlayerInteractEntityEvent event) {
    if (event.getEntity() instanceof EntityEndCrystal) {
      if (event.getPlayer().hasEffect(18)) {
        event.setCancelled(true);
      }
    }
  }
}
