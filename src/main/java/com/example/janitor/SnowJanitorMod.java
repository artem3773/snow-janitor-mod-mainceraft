package com.example.janitor;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.List;

public class SnowJanitorMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // Каждую секунду проверяем жителей в мире и даем им логику уборки
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            for (net.minecraft.entity.Entity entity : world.iterateEntities()) {
                if (entity instanceof VillagerEntity villager) {
                    // Проверяем, есть ли лопата в руках
                    if (villager.getMainHandStack().isOf(Items.IRON_SHOVEL) || 
                        villager.getMainHandStack().isOf(Items.WOODEN_SHOVEL)) {
                        
                        // Ищем снег рядом и убираем
                        BlockPos snowPos = findNearbySnow(villager);
                        if (snowPos != null && villager.getBlockPos().isWithinDistance(snowPos, 4.0)) {
                            villager.swingHand(Hand.MAIN_HAND);
                            world.playSound(null, snowPos, SoundEvents.BLOCK_SNOW_BREAK, SoundCategory.BLOCKS, 0.5F, 1.0F);
                            world.setBlockState(snowPos, Blocks.AIR.getDefaultState());
                        }
                    }
                }
            }
        });
    }

    private BlockPos findNearbySnow(VillagerEntity villager) {
        World world = villager.getWorld();
        BlockPos pos = villager.getBlockPos();
        for (int x = -3; x <= 3; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos checkPos = pos.add(x, y, z);
                    BlockState state = world.getBlockState(checkPos);
                    if (state.isOf(Blocks.SNOW) || state.isOf(Blocks.SNOW_BLOCK)) {
                        return checkPos;
                    }
                }
            }
        }
        return null;
    }
}
