package uk.joshiejack.penguinlib.client.level.ghost;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public abstract class GhostBlockGetter<D> implements BlockGetter, BlockAndTintGetter {
    protected final D data;
    protected final int xSize;
    protected final int ySize;
    protected final int zSize;

    public GhostBlockGetter(D data, int xSize, int ySize, int zSize) {
        this.data = data;
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(@NotNull BlockPos blockPos) {
        return null;
    }

    @Override
    public int getHeight() {
        return ySize;
    }

    @Nonnull
    public D getData() {
        return data;
    }

    @Override
    public @NotNull FluidState getFluidState(@NotNull BlockPos pPos) {
        return getBlockState(pPos).getFluidState();
    }

    @Override
    public int getMinBuildHeight() {
        return 0;
    }

    @Override
    public float getShade(@NotNull Direction pDirection, boolean pShade) {
        return clientLevel().getShade(pDirection, pShade);
    }

    @Override
    public @NotNull LevelLightEngine getLightEngine() {
        return clientLevel().getLightEngine();
    }

    @Override
    public int getBlockTint(@NotNull BlockPos pBlockPos, @NotNull ColorResolver pColorResolver) {
        return clientLevel().getBlockTint(pBlockPos, pColorResolver);
    }

    @Override
    public int getBrightness(@NotNull LightLayer pLightType, @NotNull BlockPos pBlockPos) {
        return 15;
    }

    @Override
    public int getRawBrightness(@NotNull BlockPos pBlockPos, int sky) {
        return 15;
    }

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    private static ClientLevel clientLevel() {
        return Minecraft.getInstance().level;
    }

    public AABB getAABB() {
        return new AABB(0, getMinBuildHeight(), 0, xSize, getMaxBuildHeight(), zSize);
    }
}
