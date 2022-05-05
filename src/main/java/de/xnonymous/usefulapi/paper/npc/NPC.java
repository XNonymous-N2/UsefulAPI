package de.xnonymous.usefulapi.paper.npc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import de.xnonymous.usefulapi.paper.util.PacketUtils;
import de.xnonymous.usefulapi.paper.util.SkinUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class NPC {

    private final UUID uuid;
    @NonNull
    private final Location location;
    @NonNull
    private final String displayName;
    @NonNull
    private final SkinUtils skinUtils;

    private PacketContainer entitySpawn;
    private PacketContainer headRotate;
    private PacketContainer animation;
    private PacketContainer playerInfoAdd;
    private PacketContainer playerInfoRemove;
    private PacketContainer entityMetadata;

    private final ArrayList<UUID> canSee = new ArrayList<>();
    private final ArrayList<UUID> inRange = new ArrayList<>();

    protected void init() {
        createPackets();
        spawn(null);
    }

    public void spawn(Player player) {
        if (player == null) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!Objects.equals(onlinePlayer.getLocation().getWorld(), location.getWorld()))
                    continue;

                PacketUtils.sendPacket(onlinePlayer, playerInfoAdd);
                Bukkit.getScheduler().runTaskLater(NPCManager.getInstance().getPlugin(), () -> PacketUtils.sendPacket(onlinePlayer, entitySpawn), 2);
                Bukkit.getScheduler().runTaskLater(NPCManager.getInstance().getPlugin(), () -> {
                    PacketUtils.sendPacket(onlinePlayer, headRotate, animation, entityMetadata);
                    if (!canSee.contains(onlinePlayer.getUniqueId()))
                        canSee.add(onlinePlayer.getUniqueId());
                }, 20);
            }
            return;
        }

        if (!Objects.equals(player.getLocation().getWorld(), location.getWorld()))
            return;

        PacketUtils.sendPacket(player, playerInfoAdd);
        Bukkit.getScheduler().runTaskLater(NPCManager.getInstance().getPlugin(), () -> PacketUtils.sendPacket(player, entitySpawn), 2);
        Bukkit.getScheduler().runTaskLater(NPCManager.getInstance().getPlugin(), () -> {
            PacketUtils.sendPacket(player, headRotate, animation, entityMetadata);
            if (!canSee.contains(player.getUniqueId()))
                canSee.add(player.getUniqueId());
        }, 20);
    }

    public void destroy() {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        packetContainer.getModifier().writeDefaults();
        packetContainer.getIntegerArrays().write(0, new int[]{uuid.hashCode()});

        PacketUtils.broadcastPacket(packetContainer);
    }

    private void createPackets() {
        PacketContainer entitySpawn = new PacketContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        entitySpawn.getModifier().writeDefaults();
        entitySpawn.getIntegers().write(0, uuid.hashCode());
        entitySpawn.getUUIDs().write(0, uuid);
        entitySpawn.getDoubles().write(0, location.getX());
        entitySpawn.getDoubles().write(1, location.getY());
        entitySpawn.getDoubles().write(2, location.getZ());
        entitySpawn.getBytes().write(0, (byte) (location.getYaw() * 256 / 360));
        entitySpawn.getBytes().write(1, (byte) (location.getPitch() * 256 / 360));
        this.entitySpawn = entitySpawn;

        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, displayName);
        PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, 0, EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromText(displayName));

        skinUtils.search();

        playerInfoData.getProfile().getProperties().put("textures", WrappedSignedProperty.fromValues("textures",
                skinUtils.getValue(),
                skinUtils.getSignature()));

        ArrayList<PlayerInfoData> data = new ArrayList<>();
        data.add(playerInfoData);

        PacketContainer playerInfo = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        playerInfo.getModifier().writeDefaults();
        playerInfo.getPlayerInfoDataLists().write(0, data);
        playerInfo.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);

        this.playerInfoAdd = playerInfo.deepClone();

        playerInfo.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);

        this.playerInfoRemove = playerInfo;

        PacketContainer headRotate = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        headRotate.getModifier().writeDefaults();
        headRotate.getIntegers().write(0, uuid.hashCode());
        headRotate.getBytes().write(0, (byte) (location.getYaw() * 256 / 360));
        this.headRotate = headRotate;

        PacketContainer animation = new PacketContainer(PacketType.Play.Server.ANIMATION);
        animation.getIntegers().write(0, uuid.hashCode());
        animation.getIntegers().write(1, 0);
        this.animation = animation;

        PacketContainer entityMetadata = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        entityMetadata.getModifier().writeDefaults();
        entityMetadata.getIntegers().write(0, uuid.hashCode());

        WrappedDataWatcher.WrappedDataWatcherObject wrappedDataWatcherObject = new WrappedDataWatcher.WrappedDataWatcherObject(16, WrappedDataWatcher.Registry.get(Byte.class));

        ArrayList<WrappedWatchableObject> dataWatchers = new ArrayList<>();
        dataWatchers.add(new WrappedWatchableObject(wrappedDataWatcherObject, (byte) (0x02 + 0x04 + 0x08 + 0x10 + 0x20 + 0x40)));

        entityMetadata.getWatchableCollectionModifier()
                .write(0, dataWatchers);
        this.entityMetadata = entityMetadata;
    }


    public void inRange(Player player) {
        if (!inRange.contains(player.getUniqueId())) {
            spawn(player);
            inRange.add(player.getUniqueId());
        }
    }


}
