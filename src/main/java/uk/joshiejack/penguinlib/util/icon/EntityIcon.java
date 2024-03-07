package uk.joshiejack.penguinlib.util.icon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import uk.joshiejack.penguinlib.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntityIcon extends Icon {
    public static final Codec<EntityIcon> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ENTITY_TYPE.holderByNameCodec().fieldOf("id").forGetter(icon -> icon.entityType),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(icon -> icon.count),
            Codec.INT.fieldOf("scale").forGetter(icon -> icon.scale)
    ).apply(instance, EntityIcon::new));

    private static final Vector3f TRANSLATION = new Vector3f();
    private static final Quaternionf ANGLE = new Quaternionf().rotationXYZ(-65F, 0.0F, (float) Math.PI);
    final Holder<EntityType<?>> entityType;
    @OnlyIn(Dist.CLIENT)
    public LivingEntity entity;
    final int scale;
    private int count;

    public EntityIcon(Holder<EntityType<?>> entityType, int count, int scale) {
        super(Type.ENTITY, count);
        this.entityType = entityType;
        this.scale = scale;
    }

    public EntityIcon(FriendlyByteBuf buf) {
        this(Holder.direct(Objects.requireNonNull(buf.readById(BuiltInRegistries.ENTITY_TYPE))), buf.readInt(), buf.readByte());
    }

    @Override
    public Codec<? extends Icon> codec() {
        return CODEC;
    }

    @Override
    public void toNetwork(FriendlyByteBuf pb) {
        pb.writeResourceLocation(BuiltInRegistries.ENTITY_TYPE.getKey(entityType.value()));
        pb.writeInt(super.originalCount);
        pb.writeByte(scale);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(Minecraft mc, GuiGraphics graphics, int x, int y) {
        //TODO? Shadowed?
        if (this.entity == null) {
            assert mc.level != null;
            Entity test = this.entityType.value().create(mc.level);
            if (test instanceof LivingEntity)
                this.entity = (LivingEntity) test;
            else
                return;
        }

        InventoryScreen.renderEntityInInventory(graphics, x + 8, y + 15, scale, TRANSLATION, ANGLE, null, this.entity);
        GuiUtils.renderIconDecorations(graphics, mc.font, x + 8, y + 15, count);
    }

    @Override
    public Icon setCount(int count) {
        this.count = count;
        return this;
    }

    @Override
    public List<Component> getTooltipLines(Player player) {
        List<Component> list = new ArrayList<>();
        list.add(entityType.value().getDescription());
        return list;
    }
}
