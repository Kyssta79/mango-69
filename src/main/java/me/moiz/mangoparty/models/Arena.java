package me.moiz.mangoparty.models;

import org.bukkit.Location;
import org.bukkit.World;

public class Arena {
    private String name;
    private Location spawn1;
    private Location spawn2;
    private Location center;
    private Location corner1;
    private Location corner2;
    private String schematicName;
    private boolean inUse;

    public Arena(String name) {
        this.name = name;
        this.inUse = false;
    }

    public Arena(String name, Location spawn1, Location spawn2, Location center, Location corner1, Location corner2, String schematicName) {
        this.name = name;
        this.spawn1 = spawn1;
        this.spawn2 = spawn2;
        this.center = center;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.schematicName = schematicName;
        this.inUse = false;
    }

    public boolean isInBounds(Location location) {
        if (corner1 == null || corner2 == null || location == null) {
            return false;
        }

        if (!location.getWorld().equals(corner1.getWorld())) {
            return false;
        }

        double minX = Math.min(corner1.getX(), corner2.getX());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        return location.getX() >= minX && location.getX() <= maxX &&
               location.getY() >= minY && location.getY() <= maxY &&
               location.getZ() >= minZ && location.getZ() <= maxZ;
    }

    // Getters
    public String getName() {
        return name;
    }

    public Location getSpawn1() {
        return spawn1;
    }

    public Location getSpawn2() {
        return spawn2;
    }

    public Location getCenter() {
        return center;
    }

    public Location getCorner1() {
        return corner1;
    }

    public Location getCorner2() {
        return corner2;
    }

    public String getSchematicName() {
        return schematicName;
    }

    public boolean isInUse() {
        return inUse;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setSpawn1(Location spawn1) {
        this.spawn1 = spawn1;
    }

    public void setSpawn2(Location spawn2) {
        this.spawn2 = spawn2;
    }

    public void setCenter(Location center) {
        this.center = center;
    }

    public void setCorner1(Location corner1) {
        this.corner1 = corner1;
    }

    public void setCorner2(Location corner2) {
        this.corner2 = corner2;
    }

    public void setSchematicName(String schematicName) {
        this.schematicName = schematicName;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    public boolean isComplete() {
        return spawn1 != null && spawn2 != null && center != null && 
               corner1 != null && corner2 != null && schematicName != null;
    }

    @Override
    public String toString() {
        return "Arena{" +
                "name='" + name + '\'' +
                ", spawn1=" + spawn1 +
                ", spawn2=" + spawn2 +
                ", center=" + center +
                ", corner1=" + corner1 +
                ", corner2=" + corner2 +
                ", schematicName='" + schematicName + '\'' +
                ", inUse=" + inUse +
                '}';
    }
}
