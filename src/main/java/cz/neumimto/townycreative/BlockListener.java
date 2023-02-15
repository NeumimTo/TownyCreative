package cz.neumimto.townycreative;


import com.palmergames.bukkit.towny.event.player.PlayerExitsFromTownBorderEvent;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;

public class BlockListener implements Listener {

    private GamemodeService gamemodeService;

    public BlockListener(GamemodeService gamemodeService) {
        this.gamemodeService = gamemodeService;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (ignore(player)){
            return;
        }

        boolean allowed = gamemodeService.canBreak(player, event.getBlock());

        event.setCancelled(!allowed);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockBreak(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (ignore(player)){
            return;
        }

        boolean allowed = gamemodeService.canPlace(player, event.getBlockPlaced());

        event.setCancelled(!allowed);
    }

    @EventHandler
    public void onMove(PlayerExitsFromTownBorderEvent event) {
        Player player = event.getPlayer();
        if (ignore(player)) {
            return;
        }

        gamemodeService.disableForPlayer(event.getPlayer());
        player.sendMessage("You crossed town border, removing creative mode");
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (ignore(player)) {
            return;
        }
        player.sendMessage("You cannot drop items from inventory while in creative mode");
        event.setCancelled(true);
        gamemodeService.disableForPlayer(player);
    }

    @EventHandler
    public void onItemDrop(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (ignore(player)) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (block.getState() instanceof Container) {
            player.sendMessage("You cannot interact with any blocks while in creative mode");
            event.setCancelled(true);
            gamemodeService.disableForPlayer(player);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (ignore(player)) {
            return;
        }

        player.sendMessage("You cannot interact with entities while in creative mode");
        event.setCancelled(true);
        gamemodeService.disableForPlayer(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (ignore(player)) {
            return;
        }
        gamemodeService.disableForPlayer(player);
    }


    public boolean ignore(Player player) {

        if (!ManagedCreativeCache.isManaged(player)) {
            return true;
        }
        if (player.getGameMode() != GameMode.CREATIVE) {
            return true;
        }
        return false;
    }

}
