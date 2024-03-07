package uk.joshiejack.penguinlib.client.gui.book;

import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.client.gui.AbstractContainerScreen;
import uk.joshiejack.penguinlib.client.gui.book.page.AbstractPage;
import uk.joshiejack.penguinlib.client.gui.book.tab.Tab;
import uk.joshiejack.penguinlib.world.inventory.AbstractBookMenu;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("ConstantConditions")
public class Book extends AbstractContainerScreen<AbstractBookMenu> {
    private static final Object2ObjectMap<String, Book> BOOK_INSTANCES = new Object2ObjectOpenHashMap<>();
    //protected Map<PageTextField, BookTextField> textFieldCache = new Object2ObjectOpenHashMap<>();
    private final List<Tab> tabs = new ArrayList<>();
    public final ResourceLocation backgroundL;
    public final ResourceLocation backgroundR;
    private int centre, bgLeftOffset;
    private Tab defaultTab = Tab.EMPTY;
    private Tab tab;
    public int fontColor1 = 0x857754;
    public int fontColor2 = 4210752;
    public int lineColor1 = 0xFFB0A483;
    public int lineColor2 = 0xFF9C8C63;
    public int frameTick;

    //private @Nullable BookTextField pageEdit;
    //private @Nullable BookTextField previousEdit;

    public Book(String modid, AbstractBookMenu container, Inventory inv, Component name) {
        super(container, inv, name, null, 360, 230);
        backgroundL = new ResourceLocation(modid, "textures/gui/book_left.png");
        backgroundR = new ResourceLocation(modid, "textures/gui/book_right.png");
    }

    public static Book getInstance(String modid, AbstractBookMenu container, Inventory inv, Component name, Consumer<Book> consumer) {
        if (!BOOK_INSTANCES.containsKey(modid)) {
            Book screen = new Book(modid, container, inv, name);
            consumer.accept(screen); //Apply extra data to this bookscreen
            BOOK_INSTANCES.put(modid, screen);
        }

        return BOOK_INSTANCES.get(modid);
    }

    @Override
    public <T extends GuiEventListener & Renderable & NarratableEntry> @NotNull T addRenderableWidget(@NotNull T widget) {
        return super.addRenderableWidget(widget);
    }

    @Override
    public <T extends Renderable> @NotNull T addRenderableOnly(@NotNull T widget) {
        return super.addRenderableOnly(widget);
    }

    @Override
    public <T extends GuiEventListener & NarratableEntry> @NotNull T addWidget(@NotNull T widget) {
        return super.addWidget(widget);
    }

    public void setTab(Tab tab) {
        this.tab = tab;
        this.markChanged();
    }

    public void bindLeftTexture() {
        //minecraft.getTextureManager().bind(backgroundL);
    }

    /**
     * Add a page to this book, automatically creates a tab for the page on the left side of the book
     **/
    public Tab withTab(Tab tab) {
        if (!tabs.contains(tab))
            tabs.add(tab);
        if (defaultTab == Tab.EMPTY)
            defaultTab = tab;
        return tab;
    }

    public Tab getTab() {
        return tab;
    }

    public Minecraft minecraft() {
        return minecraft;
    }

    public boolean isSelected(AbstractPage page) {
        return this.tab.getPage() == page;
    }

    public boolean isSelected(Tab tab) {
        return this.tab == tab;
    }

    public void markChanged() {
        this.init(minecraft, width, height);
    }

    @Override
    public void initScreen(@Nonnull Minecraft minecraft, @Nonnull Player player) {
        centre = leftPos + (imageWidth / 2);
        bgLeftOffset = centre - 154;
        titleLabelX = (imageWidth / 2) - font.width(title) / 2;
        titleLabelY = -10;
        if (tab == null)
            tab = defaultTab;

        tab.getPage().initLeft(this, bgLeftOffset, 15 + topPos);
        tab.getPage().initRight(this, centre, 15 + topPos);
        tab.addTabs(this, centre + 154, 15 + topPos);
        if (tabs.size() > 1) {
            int y = 0;
            for (Tab tab : tabs)
                addRenderableWidget(tab.create(this, centre - 180, 15 + topPos + (y++ * 36)));
        }
    }

    @Override
    public void containerTick() {
        super.containerTick();
        frameTick++;
    }


    @Override
    protected void renderBg(@Nonnull GuiGraphics matrix, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(matrix, partialTicks, mouseX, mouseY);
        renderTransparentBackground(matrix);
        matrix.blit(backgroundL, bgLeftOffset, topPos, 102, 0, 154, 202);
        matrix.blit(backgroundR, centre, topPos, 0, 0, 154, 202);
    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics matrix, int x, int y) {
        matrix.drawString(font, title, titleLabelX, titleLabelY, 0xFFFFFF);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        InputConstants.Key mouseKey = InputConstants.getKey(pKeyCode, pScanCode);
        return !this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey)
                && super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
}