package com.gamerduck.kitpvp.api.regions;

import lombok.Getter;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;

import java.util.ArrayList;
import java.util.List;

public class Cuboid implements Cloneable {
    @Getter
    final Instance instance;
    @Getter
    final Point lowerPos;
    @Getter
    final int lowerX;
    @Getter
    final int lowerY;
    @Getter
    final int lowerZ;
    @Getter
    final Point higherPos;
    @Getter
    final int higherX;
    @Getter
    final int higherY;
    @Getter
    final int higherZ;
    @Getter
    final List<Point> blocks;

    public Cuboid(Cuboid cube) {
        this.instance = cube.getInstance();
        this.lowerX = cube.getLowerX();
        this.lowerY = cube.getLowerY();
        this.lowerZ = cube.getLowerZ();
        this.lowerPos = cube.getLowerPos();
        this.higherX = cube.getHigherX();
        this.higherY = cube.getHigherY();
        this.higherZ = cube.getHigherZ();
        this.higherPos = cube.getHigherPos();
        blocks = new ArrayList<Point>();
        updateBlocks();
    }

    public Cuboid(Instance instance, Point pointOne, Point pointTwo) {
        this.instance = instance;
        this.lowerX = Math.min(pointOne.blockX(), pointTwo.blockX());
        this.lowerY = Math.min(pointOne.blockY(), pointTwo.blockY());
        this.lowerZ = Math.min(pointOne.blockZ(), pointTwo.blockZ());
        this.lowerPos = new Pos(lowerX, lowerY, lowerZ);
        this.higherX = Math.max(pointOne.blockX(), pointTwo.blockX());
        this.higherY = Math.max(pointOne.blockY(), pointTwo.blockY());
        this.higherZ = Math.max(pointOne.blockZ(), pointTwo.blockZ());
        this.higherPos = new Pos(higherX, higherY, higherZ);
        blocks = new ArrayList<Point>();
        updateBlocks();
    }


    public boolean isInCube(Point pos) {
        if (higherX > pos.x() && lowerX < pos.x()) {
            if (higherY > pos.y() && lowerY < pos.y()) {
                if (higherZ > pos.z() && lowerZ < pos.z()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateBlocks() {
        for (int x = lowerX; x < higherX; x++) {
            for (int y = lowerY; y < higherY; y++) {
                for (int z = lowerZ; z < higherZ; z++) {
                    blocks.add(new Pos(x, y, z));
                }
            }
        }
    }

    @Override
    public Cuboid clone() {
        return new Cuboid(this);
    }

}
