package uk.joshiejack.penguinlib.util.icon;

import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.data.PenguinRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class Icon {
    public static final ResourceLocation DEFAULT_LOCATION = new ResourceLocation(PenguinLib.MODID, "textures/gui/icons.png");
    // Simplified codec that defaults to SpriteIcon to avoid registry initialization issues
    public static final Codec<Icon> CODEC = SpriteIcon.CODEC.xmap(icon -> (Icon) icon, icon -> (SpriteIcon) icon);

    public abstract Codec<? extends Icon> codec();

    protected boolean shadowed;
    private final Type type;
    protected final int originalCount;

    public Icon(Type type, int originalCount) {
        this.type = type;
        this.originalCount = originalCount;
    }

    public Icon shadowed () {
        this.shadowed = true;
        return this;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        ITEM (ItemIcon::new) , 
        TEXTURE(TextureIcon::new), 
        ENTITY(EntityIcon::new), 
        TAG(TagIcon::new), 
        LIST(ListIcon::new), 
        SPRITE(SpriteIcon::new);

        private final Function<FriendlyByteBuf, Icon> icon;

        Type(Function<FriendlyByteBuf, Icon> icon) {
            this.icon = icon;
        }

        public Icon apply(FriendlyByteBuf buf) {
            return icon.apply(buf);
        }
    }



//    public static Icon fromJson(JsonObject json) {
//        //Icon List
//        if (json.has("icon_list")) {
//            JsonArray array = json.getAsJsonArray("icon_list");
//            List<Icon> icons = new ArrayList<>();
//            for (int i = 0; i < array.size(); i++) {
//                icons.add(fromJson(array.get(i).getAsJsonObject()));
//            }
//
//            return new ListIcon(icons);
//        }
//
//        //List
//        if (json.has("list")) {
//            JsonArray array = json.getAsJsonArray("list");
//            List<ItemStack> items = new ArrayList<>();
//            for (int i = 0; i < array.size(); i++) {
//                Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(array.get(i).getAsString()));
//                if (item != BuiltInRegistries.ITEM.get(BuiltInRegistries.ITEM.getDefaultKey()))
//                    items.add(new ItemStack(item));
//            }
//
//            return new ItemListIcon(items);
//        }
//
//        //Rest
//        return json.has("item") ? new ItemIcon(new ItemStack(GsonHelper.getAsItem(json, "item")))
//                : json.has("tag") ? new TagIcon(ItemTags.create(new ResourceLocation(GsonHelper.getAsString(json, "tag"))))
//                : json.has("entity") ? new EntityIcon(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(JSONUtils.getAsString(json, "entity"))), JSONUtils.getAsInt(json, "scale"))
//                : new TextureIcon(json.has("texture") ? new ResourceLocation(JSONUtils.getAsString(json, "texture")) : DEFAULT_LOCATION,
//                json.has("x") ? JSONUtils.getAsInt(json, "x") : 0,
//                json.has("y") ? JSONUtils.getAsInt(json, "y") : 0);
//    }
//
//    public abstract JsonElement toJson(JsonObject json);

    public static Icon fromNetwork(FriendlyByteBuf pb) {
        Type type = Type.values()[pb.readByte()];
        switch (type) {
            case ITEM:
                return new ItemIcon(pb);
            case TEXTURE:
                return new TextureIcon(pb.readBoolean() ? pb.readResourceLocation() : DEFAULT_LOCATION, pb.readInt(), pb.readInt(), pb.readInt());
            case ENTITY:
                return new EntityIcon(Holder.direct(BuiltInRegistries.ENTITY_TYPE.get(pb.readResourceLocation())), pb.readInt(), pb.readByte());
            case TAG:
                return new TagIcon(ItemTags.create(pb.readResourceLocation()), pb.readInt());
            case LIST: {
                int count = pb.readInt();
                List<Icon> icons = new ArrayList<>();
                int size = pb.readInt();
                for (int i = 0; i < size; i++) {
                    icons.add(fromNetwork(pb)); //Hmm
                }

                return new ListIcon(icons, count);
            }
        }

        //Unreachable
        return null;
    }

    public abstract void toNetwork(FriendlyByteBuf pb);

    @OnlyIn(Dist.CLIENT)
    public abstract void render(Minecraft mc, GuiGraphics graphics, int x, int y);

    public abstract Icon setCount(int count);


    /**@OnlyIn(Dist.CLIENT)
    public void renderWithCount(Minecraft mc, GuiGraphics graphics, PoseStack matrix, int x, int y, int count) {
        render(mc, graphics, matrix, x, y);
        if (count != 1) {
            PoseStack matrixstack = new PoseStack();
            String s = String.valueOf(count);
            matrixstack.translate(0.0D, 0.0D, /*mc.gui.getBlitOffset()/?TODO/ + 200.0F);
            IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.immediate(Tesselator.getInstance().getBuilder());
            mc.font.drawInBatch(s, (float) (x + 19 - 2 - mc.font.width(s)), (float) (y + 6 + 3), 16777215, true, matrixstack.last().pose(), irendertypebuffer$impl, false, 0, 15728880);
            irendertypebuffer$impl.endBatch();
        }
    } */ //TODO

    public abstract List<Component> getTooltipLines(Player player);
}