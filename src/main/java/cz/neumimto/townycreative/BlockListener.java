package cz.neumimto.townycreative;

import com.palmergames.bukkit.towny.event.nation.NationTownLeaveEvent;
import com.palmergames.bukkit.towny.event.player.PlayerExitsFromTownBorderEvent;
import com.palmergames.bukkit.towny.event.town.TownLeaveEvent;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.Bukkit;
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

import java.util.Collection;
import java.util.List;

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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("townycreative.player.toggle")
                && !player.hasPermission("townycreative.player.bypasslogin")) {
            if (player.getGameMode() == GameMode.CREATIVE) {
                gamemodeService.disableForPlayer(player);
            }
        }
    }

    @EventHandler
    public void townDisbandEvent(TownLeaveEvent event) {
        List<Resident> residents = event.getTown().getResidents();
        removeForResidents(residents);
    }

    @EventHandler
    public void nationDisbandedEvent(NationTownLeaveEvent event) {
        List<Resident> residents = event.getTown().getResidents();
        removeForResidents(residents);
    }

    public void removeForResidents(Collection<Resident> res) {
        for (Resident resident : res) {
            Player player = Bukkit.getPlayer(resident.getUUID());
            if (player == null || ignore(player)) {
                continue;
            }
            gamemodeService.disableForPlayer(player);
        }
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
