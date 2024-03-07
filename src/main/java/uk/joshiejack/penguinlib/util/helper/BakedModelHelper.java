package uk.joshiejack.penguinlib.util.helper;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;

import java.util.List;
import java.util.Optional;

public class BakedModelHelper {
    public static void cube(TextureAtlasSprite sprite, List<BakedQuad> quads) {
        quads.add(quad(v(0, 1, 1), v(1, 1, 1), v(1, 1, 0), v(0, 1, 0), sprite));
        quads.add(quad(v(0, 0, 0), v(1, 0, 0), v(1, 0, 1), v(0, 0, 1), sprite));
        quads.add(quad(v(1, 0, 0), v(1, 1, 0), v(1, 1, 1), v(1, 0, 1), sprite));
        quads.add(quad(v(0, 0, 1), v(0, 1, 1), v(0, 1, 0), v(0, 0, 0), sprite));
        quads.add(quad(v(0, 1, 0), v(1, 1, 0), v(1, 0, 0), v(0, 0, 0), sprite));
        quads.add(quad(v(0, 0, 1), v(1, 0, 1), v(1, 1, 1), v(0, 1, 1), sprite));
    }

    public static BakedQuad retexture(BakedQuad original, TextureAtlasSprite sprite) {
        int[] vertexData = original.getVertices();
        int[] newVertexData = new int[vertexData.length];
        System.arraycopy(vertexData, 0, newVertexData, 0, newVertexData.length);
        int vertexSizeInts = DefaultVertexFormat.BLOCK.getIntegerSize();
        Optional<VertexFormatElement> positionElement = DefaultVertexFormat.BLOCK.getElements().stream()
                .filter(e -> VertexFormatElement.Usage.UV.equals(e.getUsage())).findFirst();
        int positionOffset = positionElement.get().getIndex();
        for (int i = positionOffset; i < vertexData.length; i += vertexSizeInts) {
            newVertexData[i + 4] = Float.floatToRawIntBits(sprite.getU(unU(original.getSprite(), Float.intBitsToFloat(vertexData[i + 4]))));
            newVertexData[i + 5] = Float.floatToRawIntBits(sprite.getV(unV(original.getSprite(), Float.intBitsToFloat(vertexData[i + 5]))));
        }

        return new BakedQuad(newVertexData, original.getTintIndex(), original.getDirection(), sprite, false);
    }

    private static float unU(TextureAtlasSprite sprite, float u) {
        return (u - sprite.getU0()) / (sprite.getU1() - sprite.getU0());
    }

    private static float unV(TextureAtlasSprite sprite, float v) {
        return (v - sprite.getV0()) / (sprite.getV1() - sprite.getV0());
    }

    public static BakedQuad quad(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, TextureAtlasSprite sprite, int rotation) {
        return switch (rotation) {
            case 0 -> quad(v1, v2, v3, v4, sprite);
            case 1 -> quad(v2, v3, v4, v1, sprite);
            case 2 -> quad(v3, v4, v1, v2, sprite);
            case 3 -> quad(v4, v1, v2, v3, sprite);
            default -> quad(v1, v2, v3, v4, sprite);
        };
    }

    public static BakedQuad quad(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, TextureAtlasSprite sprite) {
        Vec3 normal = v3.subtract(v2).cross(v1.subtract(v2)).normalize();

        BakedQuad[] quad = new BakedQuad[1];
        QuadBakingVertexConsumer builder = new QuadBakingVertexConsumer(q -> quad[0] = q);
        builder.setSprite(sprite);
        builder.setDirection(Direction.getNearest(normal.x, normal.y, normal.z));
        putVertex(builder, normal, v1.x, v1.y, v1.z, 0, 0, sprite);
        putVertex(builder, normal, v2.x, v2.y, v2.z, 0, 1, sprite);
        putVertex(builder, normal, v3.x, v3.y, v3.z, 1, 1, sprite);
        putVertex(builder, normal, v4.x, v4.y, v4.z, 1, 0, sprite);
        return quad[0];
    }

    private static void putVertex(VertexConsumer builder, Position normal, double x, double y, double z, float u, float v, TextureAtlasSprite sprite) {
        float iu = sprite.getU(u);
        float iv = sprite.getV(v);
        builder.vertex(x, y, z)
                .uv(iu, iv)
                .uv2(0, 0)
                .color(1.0f, 1.0f, 1.0f, 1.0f)
                .normal((float) normal.x(), (float) normal.y(), (float) normal.z())
                .endVertex();
    }

    public static Vec3 v(double x, double y, double z) {
        return new Vec3(x, y, z);
    }
}