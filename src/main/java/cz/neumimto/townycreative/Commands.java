package cz.neumimto.townycreative;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.WorldCoord;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

@CommandAlias("townycreative")
public class Commands extends BaseCommand {

    private GamemodeService gamemodeService;

    public Commands(GamemodeService gamemodeService) {
        this.gamemodeService = gamemodeService;
    }

    @Default
    @CommandPermission("townycreative.player.toggle")
    public void toggle(Player player) {
        Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
        Nation nationOrNull = resident.getNationOrNull();
        if (nationOrNull == null) {
            player.sendMessage("No nation");
            return;
        }

        if (gamemodeService.getMaterials(player).isEmpty()) {
            player.sendMessage("Action not possible for your nation");
            return;
        }

        if (!player.getInventory().isEmpty()) {
            player.sendMessage("Before accessing creative mode empty your inventory first");
            return;
        }

        TownBlock townBlockOrNull = TownyUniverse.getInstance().getTownBlockOrNull(WorldCoord.parseWorldCoord(player.getLocation()));
        if (townBlockOrNull == null || townBlockOrNull.getTownOrNull() != resident.getTownOrNull()) {
            player.sendMessage("Before accessing creative mode empty your inventory first");
            return;
        }

        if (player.getGameMode() != GameMode.CREATIVE && !ManagedCreativeCache.isManaged(player)) {
            player.setGameMode(GameMode.CREATIVE);
            ManagedCreativeCache.put(player);
            player.sendMessage("Gamemode updated");
        } else {
            gamemodeService.disableForPlayer(player);
            player.sendMessage("Gamemode updated");
        }
    }

    @Subcommand("set-build-blocks")
    @CommandCompletion("@nation")
    @CommandPermission("townycreative.admin")
    public void setBlocks(Player executor, Nation nation, MergeFn fn) {
        Block bellow = executor.getLocation().getBlock();
        if (bellow.getType() != Material.CHEST) {
            executor.sendMessage("You must be standing on a chest");
            return;
        }

        Container container = (Container) bellow.getState();
        ItemStack[] toAdd = container.getInventory().getContents();
        Set<Material> materialSet = new HashSet<>();
        for (ItemStack itemStack : toAdd) {
            if (itemStack == null || itemStack.getType().isAir()) {
                continue;
            }
            if (!itemStack.getType().isBlock()) {
                executor.sendMessage(">" + bellow.getType() + "! is not a block - skipped" );
            } else {
                materialSet.add(itemStack.getType());
            }
        }

        gamemodeService.setMaterials(nation, fn, materialSet);
        executor.sendMessage("Configuration updated");
    }

}
