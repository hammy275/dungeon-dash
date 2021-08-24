package net.blf02.dungeondash.utils;

import net.blf02.dungeondash.DungeonDash;
import net.blf02.dungeondash.game.BeforeGameState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class PlayerStorage implements Serializable {

    private transient static final long serialVersionUID = 1216119498748966186L;
    public transient int savesSinceLastAccess = 0;
    public transient Player player;

    protected UUID playerUUID;

    protected BeforeGameState preGameState = null;

    private PlayerStorage() {

    }

    public BeforeGameState getBeforeGameState() {
        this.savesSinceLastAccess = 0;
        return this.preGameState;
    }

    public void setBeforeGameState(BeforeGameState state) {
        this.savesSinceLastAccess = 0;
        this.preGameState = state;
    }

    public void save() {
        Bukkit.getScheduler().runTaskAsynchronously(DungeonDash.instance, this::doSave);
    }

    public void doSave() {
        String filePath = DungeonDash.playerDataDir + player.getUniqueId() + ".data";
        try {
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(
                    new GZIPOutputStream(new FileOutputStream(filePath)));
            out.writeObject(this);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to write player data for player " + player.getDisplayName() + "!");
        }

    }

    /**
     * Due to the `Player` type taking up a ton of space, we need to save the UUID, and re-add the `Player`
     * variable to all objects that need it.
     */
    private void setPlayerVars() {
        if (this.preGameState != null) {
            this.preGameState.player = player;
        }
    }

    public static PlayerStorage loadPlayerStorage(Player player) {
        PlayerStorage res;
        res = Tracker.playerStorage.get(player.getUniqueId());
        if (res != null) {
            return res;
        }
        String path = DungeonDash.playerDataDir + player.getUniqueId() + ".data";
        if (!Paths.get(path).toFile().exists()) {
            res = new PlayerStorage();
            res.playerUUID = player.getUniqueId();
        } else {
            try {
                BukkitObjectInputStream in = new BukkitObjectInputStream(
                        new GZIPInputStream(new FileInputStream(path)));
                PlayerStorage ps = (PlayerStorage) in.readObject();
                in.close();
                res = ps;
            } catch (ClassNotFoundException | IOException e) {
                System.out.println("Failed to load PlayerStorage for " + player.getDisplayName());
                e.printStackTrace();
                return null;
            }
        }
        res.player = player;
        Tracker.playerStorage.put(player.getUniqueId(), res);
        res.setPlayerVars();
        return res;
    }

}
