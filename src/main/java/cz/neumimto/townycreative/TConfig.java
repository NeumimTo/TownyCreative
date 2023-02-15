package cz.neumimto.townycreative;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Conversion;
import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.yaml.YamlFormat;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TConfig {

    @Path("Destroy_allowed_blocks_only")
    public boolean DESTROY_ALLOWED_BLOCKS_ONLY;

    @Path("Nations")
    @Conversion(NationsConverter.class)
    public Map<String, Set<Material>> ALLOWED_BLOCKS = new HashMap<>();

    @Path("Damage_player_when_crossing_townborder_in_creative")
    public boolean DAMAGE_PLAYER_WHEN_CROSSING_TOWNBORDER_IN_CREATIVE;

    @Path("Toggle_cooldown")
    public long TOGGLE_COOLDOWN;

    public static class NationsConverter implements Converter<Map<String, Set<Material>>, Config> {

        @Override
        public Map<String, Set<Material>> convertToField(Config value) {
            Map<String, Set<Material>> map = new HashMap<>();

            Map<String, Object> m = value.valueMap();
            for (Map.Entry<String, Object> entry : m.entrySet()) {
                List<String> v = (List<String>) entry.getValue();
                String nationName = entry.getKey();
                map.put(nationName.toLowerCase(), v.stream().map(Material::matchMaterial).collect(Collectors.toSet()));
            }

            return map;
        }

        @Override
        public Config convertFromField(Map<String, Set<Material>> value) {
            Config config = Config.inMemory();
            for (Map.Entry<String, Set<Material>> entry : value.entrySet()) {
                config.set(entry.getKey(), entry.getValue().stream().map(a->a.key().asString()).collect(Collectors.toList()));;
            }
            return config;
        }
    }

}
