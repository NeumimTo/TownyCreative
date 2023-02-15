package cz.neumimto.townycreative;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ManagedCreativeCache {

    private static Set<UUID> players = new HashSet<>();

    public static void put(Player player) {
        players.add(player.getUniqueId());
    }

    public static void pop(Player player) {
        players.remove(player.getUniqueId());
    }

    public static boolean isManaged(Player player) {
        return players.contains(player.getUniqueId());
    }
}
