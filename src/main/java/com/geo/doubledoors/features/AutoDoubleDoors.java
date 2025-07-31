package com.geo.doubledoors.features;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class AutoDoubleDoors {

    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient || hand != Hand.MAIN_HAND) return ActionResult.PASS;

            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if (!(block instanceof DoorBlock)) return ActionResult.PASS;

            boolean isOpen = state.get(DoorBlock.OPEN);
            boolean newState = !isOpen;

            // Update current door
            world.setBlockState(pos, state.with(DoorBlock.OPEN, newState), 10);

            // Sync the door next to it
            syncOtherDoor(world, pos, state, newState);

            return ActionResult.SUCCESS;
        });
    }

    private static void syncOtherDoor(World world, BlockPos pos, BlockState originalState, boolean open) {
        Direction facing = originalState.get(DoorBlock.FACING);

        Direction left = facing.rotateYCounterclockwise();
        Direction right = facing.rotateYClockwise();

        trySetDoorState(world, pos.offset(left), originalState, open);
        trySetDoorState(world, pos.offset(right), originalState, open);
    }

    private static void trySetDoorState(World world, BlockPos pos, BlockState originalState, boolean open) {
        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof DoorBlock)) return;

        // Only affect if it's same direction and not already matching the open state
        if (state.get(DoorBlock.FACING) == originalState.get(DoorBlock.FACING) &&
                state.get(DoorBlock.OPEN) != open) {

            world.setBlockState(pos, state.with(DoorBlock.OPEN, open), 10);
        }
    }
}
