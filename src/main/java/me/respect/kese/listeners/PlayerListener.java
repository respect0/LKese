package me.respect.kese.listeners;

import me.respect.kese.Kese;
import me.respect.kese.utils.UpdateChecker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private final Kese plugin = Kese.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().isOp()) {
            UpdateChecker.sendToPlayer(plugin, event.getPlayer());
        }
    }

}
