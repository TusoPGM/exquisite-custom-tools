
package com.e425.exquisitecustomtools.event;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public class TreeProcessing {

    private static final int MAX_LOGS = 128;
    private static final int MAX_RADIUS = 12;
    private static final int MAX_HEIGHT = 40;
    private static final int MIN_NATURAL_LEAVES = 4;
    private static boolean harvesting = false;
    public static boolean isHarvesting() {
        return harvesting;
    }

    public static void harvestTree(
            Level level,
            BlockPos pos,
            ServerPlayer player
    ) {

        if (harvesting) {
            return;
        }

        if (!level.getBlockState(pos).is(BlockTags.LOGS)) {
            return;
        }

        List<BlockPos> logPositions = new ArrayList<>();
        Set<BlockPos> visitedLogs = new HashSet<>();
        Queue<BlockPos> logsToCheck = new ArrayDeque<>();

        logPositions.add(pos);
        visitedLogs.add(pos);
        logsToCheck.add(pos);

        boolean searchLimitReached = false;

        while (!logsToCheck.isEmpty()) {

            BlockPos nextLog = logsToCheck.poll();

            for (int x = -1; x <= 1; x++) {
                for (int y = 0; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {

                        if (x == 0 && y == 0 && z == 0) {
                            continue;
                        }

                        BlockPos nearbyPos = nextLog.offset(x, y, z);
                        BlockState nearbyState = level.getBlockState(nearbyPos);

                        if (!nearbyState.is(BlockTags.LOGS)) {
                            continue;
                        }

                        if (visitedLogs.contains(nearbyPos)) {
                            continue;
                        }

                        if (Math.abs(nearbyPos.getX() - pos.getX()) > MAX_RADIUS
                                || Math.abs(nearbyPos.getZ() - pos.getZ()) > MAX_RADIUS
                                || nearbyPos.getY() - pos.getY() > MAX_HEIGHT) {

                            searchLimitReached = true;
                            continue;
                        }

                        if (logPositions.size() >= MAX_LOGS) {
                            searchLimitReached = true;
                            continue;
                        }

                        visitedLogs.add(nearbyPos);
                        logPositions.add(nearbyPos);
                        logsToCheck.add(nearbyPos);
                    }
                }
            }
        }

        if (searchLimitReached) {
            return;
        }

        int highestLogY = pos.getY();

        for (BlockPos logPos : logPositions) {
            highestLogY = Math.max(highestLogY, logPos.getY());
        }

        int canopyStartY = Math.max(pos.getY(), highestLogY - 3);

        Set<BlockPos> checkedPositions = new HashSet<>();
        int naturalLeafCount = 0;

        for (BlockPos logPos : logPositions) {

            if (logPos.getY() < canopyStartY) {
                continue;
            }

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {

                        BlockPos nearbyPos = logPos.offset(x, y, z);

                        if (!checkedPositions.add(nearbyPos)) {
                            continue;
                        }

                        BlockState nearbyState = level.getBlockState(nearbyPos);

                        if (!nearbyState.is(BlockTags.LEAVES)) {
                            continue;
                        }

                        if (!nearbyState.hasProperty(LeavesBlock.PERSISTENT)) {
                            continue;
                        }

                        boolean persistent = nearbyState.getValue(LeavesBlock.PERSISTENT);

                        if (!persistent) {
                            naturalLeafCount++;
                        }
                    }
                }
            }
        }

        if (naturalLeafCount < MIN_NATURAL_LEAVES) {
            return;
        }

        harvesting = true;

        try {

            for (BlockPos logPos : logPositions) {

                if (logPos.equals(pos)) {
                    continue;
                }

                if (!player.getMainHandItem().is(ItemTags.AXES)) {
                    break;
                }

                if (!level.getBlockState(logPos).is(BlockTags.LOGS)) {
                    continue;
                }

                player.gameMode.destroyBlock(logPos);
            }

        } finally {
            harvesting = false;
        }
    }
}
