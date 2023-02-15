package cz.neumimto.townycreative;

import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.WorldCoord;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GamemodeService {

    private TConfig tConfig;

    private File cfg;

    public void initConfig(File file) {
        cfg = file;
        if (!file.exists()) {
            TownyCreative.log("No config found");

            tConfig = new TConfig();
            tConfig.TOGGLE_COOLDOWN = 20000;
            tConfig.DAMAGE_PLAYER_WHEN_CROSSING_TOWNBORDER_IN_CREATIVE = false;
            tConfig.DESTROY_ALLOWED_BLOCKS_ONLY = true;
            tConfig.ALLOWED_BLOCKS = new HashMap<>();
            TownyCreative.log("Saving default config");
            save();
            TownyCreative.log("Default config saved");
        }
        reload();
        TownyCreative.log("Config Loaded");
    }

    public void reload() {
        try (FileConfig c = FileConfig.of(cfg)) {
            c.load();
            tConfig = new ObjectConverter().toObject(c, TConfig::new);
        }
    }

    public void save() {
        if (!cfg.exists()) {
            try {
                cfg.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (FileConfig c = FileConfig.of(cfg)) {
            new ObjectConverter().toConfig(tConfig, c);
            c.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Set<Material> getMaterials(Player player, Block block) {
        Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
        Nation nation = resident.getNationOrNull();
        if (nation == null) {
            return Collections.emptySet();
        }
        TownBlock townBlock = TownyUniverse.getInstance().getTownBlockOrNull(WorldCoord.parseWorldCoord(block));
        if (townBlock == null || !townBlock.hasTown()) {
            return Collections.emptySet();
        }
        if (!townBlock.getTownOrNull().equals(resident.getTownOrNull())) {
            return Collections.emptySet();
        }
        return tConfig.ALLOWED_BLOCKS.get(nation.getName().toLowerCase());
    }

    public Set<Material> getMaterials(Player player) {
        Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
        Nation nation = resident.getNationOrNull();
        if (nation == null) {
            return Collections.emptySet();
        }
        return tConfig.ALLOWED_BLOCKS.getOrDefault(nation.getName().toLowerCase(), Collections.emptySet());
    }

    public boolean canBreak(Player player, Block block) {
        return tConfig.DESTROY_ALLOWED_BLOCKS_ONLY || getMaterials(player, block).contains(block.getType());
    }

    public boolean canPlace(Player player, Block blockPlaced) {
        return getMaterials(player, blockPlaced).contains(blockPlaced.getType());
    }

    public void setMaterials(Nation nation, MergeFn fn, Set<Material> materials) {
        if (materials.isEmpty() && fn == MergeFn.SET) {
            tConfig.ALLOWED_BLOCKS.remove(nation.getName().toLowerCase());

            Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
            for (Player onlinePlayer : onlinePlayers) {
                if (ManagedCreativeCache.isManaged(onlinePlayer)) {
                    disableForPlayer(onlinePlayer);
                }
            }

        } else {
            if (fn == MergeFn.SET) {
                tConfig.ALLOWED_BLOCKS.put(nation.getName().toLowerCase(), materials);
            } else if (fn == MergeFn.ADD) {
                Set<Material> materials1 = tConfig.ALLOWED_BLOCKS.get(nation.getName().toLowerCase());
                if (materials1 != null) {
                    materials.addAll(materials1);
                }
                tConfig.ALLOWED_BLOCKS.put(nation.getName().toLowerCase(), materials);
            }
        }
        save();
    }

    public void disableForPlayer(Player player) {
        player.getInventory().clear();
        player.setGameMode(player.getPreviousGameMode() == null ? GameMode.SURVIVAL : player.getPreviousGameMode());
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 100, 1));
        ManagedCreativeCache.pop(player);
    }

}
