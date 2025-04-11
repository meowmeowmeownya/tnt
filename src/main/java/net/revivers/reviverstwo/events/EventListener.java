package net.revivers.reviverstwo.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Remove the knockback profile setting since WindSpigot doesn't support it
        // You can add other player join logic here if needed
    }

}