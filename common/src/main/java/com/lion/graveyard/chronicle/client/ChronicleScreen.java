package com.lion.graveyard.chronicle.client;

import com.lion.graveyard.Graveyard;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/// Chapters down the left, the open book on the right. Unfound chapters are listed but dimmed,
/// so the player sees how much of the story is still out there.
@Environment(EnvType.CLIENT)
public class ChronicleScreen extends Screen {
    public static final Identifier BUTTON_TEXTURE = Graveyard.id("textures/gui/chronicle_button.png");
    private static final Identifier BOOK_TEXTURE = Identifier.withDefaultNamespace("textures/gui/book.png");

    private static final int BOOK_WIDTH = 192;
    private static final int BOOK_HEIGHT = 192;
    private static final int TEXT_WIDTH = 114;
    private static final int TEXT_TOP = 38;
    private static final int LINES_PER_PAGE = 13;
    private static final int LIST_WIDTH = 120;
    private static final int ROW_HEIGHT = 11;

    @Nullable private final Screen parent;
    private final List<ChronicleChapters.Chapter> chapters = ChronicleChapters.load();

    @Nullable private ChronicleChapters.Chapter selected;
    private List<List<FormattedCharSequence>> bookPages = List.of();
    private int pageIndex;
    private int listScroll;

    private int listX, listY, bookX, bookY;
    private PageButton nextButton;
    private PageButton previousButton;

    public ChronicleScreen(@Nullable Screen parent) {
        this(parent, null);
    }

    public ChronicleScreen(@Nullable Screen parent, @Nullable String openPage) {
        super(Component.translatable("chronicle." + Graveyard.MOD_ID + ".title"));
        this.parent = parent;

        if (openPage != null) {
            this.selected = this.chapters.stream()
                    .filter(c -> c.id().equals(openPage))
                    .findFirst()
                    .orElse(null);
        }
    }

    @Override
    protected void init() {
        int totalWidth = LIST_WIDTH + 8 + BOOK_WIDTH;
        this.listX = (this.width - totalWidth) / 2;
        this.bookX = this.listX + LIST_WIDTH + 8;
        this.bookY = Math.max(2, (this.height - BOOK_HEIGHT) / 2 - 10);
        this.listY = this.bookY + 2;

        this.nextButton = this.addRenderableWidget(new PageButton(this.bookX + 116, this.bookY + 159, true, b -> this.turnPage(1), true));
        this.previousButton = this.addRenderableWidget(new PageButton(this.bookX + 43, this.bookY + 159, false, b -> this.turnPage(-1), true));
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose())
                .bounds(this.bookX + 48, this.bookY + BOOK_HEIGHT - 4, 98, 20).build());

        if (this.selected == null) {
            this.chapters.stream()
                    .filter(c -> ClientChronicle.isFound(c.id()))
                    .findFirst()
                    .ifPresent(this::select);
        } else {
            this.select(this.selected);
        }

        this.updateButtons();
    }

    private void select(ChronicleChapters.Chapter chapter) {
        this.selected = chapter;
        this.pageIndex = 0;
        this.bookPages = this.paginate(chapter);
        this.updateButtons();
    }

    private List<List<FormattedCharSequence>> paginate(ChronicleChapters.Chapter chapter) {
        List<FormattedCharSequence> lines = new ArrayList<>();
        for (String paragraph : chapter.pages()) {
            if (!lines.isEmpty()) {
                lines.add(FormattedCharSequence.EMPTY);
            }

            lines.addAll(this.font.split(Component.literal(paragraph), TEXT_WIDTH));
        }
        List<List<FormattedCharSequence>> pages = new ArrayList<>();
        for (int i = 0; i < lines.size(); i += LINES_PER_PAGE) {
            pages.add(lines.subList(i, Math.min(lines.size(), i + LINES_PER_PAGE)));
        }

        if (pages.isEmpty()) {
            pages.add(List.of());
        }

        return pages;
    }

    private void turnPage(int delta) {
        this.pageIndex = Mth.clamp(this.pageIndex + delta, 0, this.bookPages.size() - 1);
        this.updateButtons();
    }

    private void updateButtons() {
        this.nextButton.visible = this.pageIndex < this.bookPages.size() - 1;
        this.previousButton.visible = this.pageIndex > 0;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        this.extractTransparentBackground(context);
        this.extractList(context, mouseX, mouseY);
        this.extractBook(context);
    }

    private void extractList(GuiGraphicsExtractor context, int mouseX, int mouseY) {
        Font font = this.font;
        int y = this.listY;
        context.text(font, this.title, this.listX, y, 0xFFFFFFFF, true);
        y += 14;
        int lastAct = -1;
        int rows = 0;
        for (ChronicleChapters.Chapter chapter : this.chapters) {
            if (chapter.act() != lastAct) {
                lastAct = chapter.act();

                if (rows++ >= this.listScroll) {
                    context.text(font, Component.translatable("chronicle." + Graveyard.MOD_ID + ".act." + lastAct).withStyle(ChatFormatting.GOLD), this.listX, y, 0xFFFFFFFF, true);
                    y += ROW_HEIGHT;
                }
            }

            if (rows++ < this.listScroll) {
                continue;
            }

            if (y > this.progressY() - ROW_HEIGHT) {
                break;
            }

            boolean found = ClientChronicle.isFound(chapter.id());
            boolean hovered = found && mouseX >= this.listX && mouseX < this.listX + LIST_WIDTH && mouseY >= y && mouseY < y + ROW_HEIGHT;
            Component label = found ? Component.translatable(chapter.titleKey()) : Component.literal("? ? ?");
            int color = chapter == this.selected ? 0xFFFFE0A0 : hovered ? 0xFFFFFFFF : found ? 0xFFC0C0C0 : 0xFF505050;
            context.text(font, Component.literal("  ").append(label), this.listX, y, color, true);
            y += ROW_HEIGHT;
        }
        int foundCount = (int) this.chapters.stream()
                .filter(c -> ClientChronicle.isFound(c.id()))
                .count();
        context.text(
                font,
                Component.translatable("chronicle." + Graveyard.MOD_ID + ".progress", foundCount, this.chapters.size()).withStyle(ChatFormatting.DARK_GRAY),
                this.listX,
                this.progressY(),
                0xFFFFFFFF,
                true
        );
    }

    private int progressY() {
        return Math.min(this.height - ROW_HEIGHT, this.bookY + BOOK_HEIGHT + 2);
    }

    private void extractBook(GuiGraphicsExtractor context) {
        context.blit(RenderPipelines.GUI_TEXTURED, BOOK_TEXTURE, this.bookX, this.bookY, 0.0F, 0.0F, BOOK_WIDTH, BOOK_HEIGHT, 256, 256);

        if (this.selected == null) {
            List<FormattedCharSequence> hint = this.font.split(Component.translatable("chronicle." + Graveyard.MOD_ID + ".empty"), TEXT_WIDTH);
            int y = this.bookY + 40;
            for (FormattedCharSequence line : hint) {
                context.text(this.font, line, this.bookX + 36, y, 0xFF404040, false);
                y += 9;
            }
            return;
        }

        Component header = Component.translatable(this.selected.titleKey()).withStyle(ChatFormatting.BOLD);
        context.text(this.font, header, this.bookX + 36, this.bookY + 14, 0xFF202020, false);
        Component counter = Component.translatable("book.pageIndicator", this.pageIndex + 1, this.bookPages.size());
        context.text(this.font, counter, this.bookX + 36 + TEXT_WIDTH - this.font.width(counter), this.bookY + 25, 0xFF606060, false);

        int y = this.bookY + TEXT_TOP;
        for (FormattedCharSequence line : this.bookPages.get(this.pageIndex)) {
            context.text(this.font, line, this.bookX + 36, y, 0xFF202020, false);
            y += 9;
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();

        if (event.button() == 0 && mouseX >= this.listX && mouseX < this.listX + LIST_WIDTH) {
            int y = this.listY + 14;
            int lastAct = -1;
            int rows = 0;
            for (ChronicleChapters.Chapter chapter : this.chapters) {
                if (chapter.act() != lastAct) {
                    lastAct = chapter.act();

                    if (rows++ >= this.listScroll) {
                        y += ROW_HEIGHT;
                    }
                }

                if (rows++ < this.listScroll) {
                    continue;
                }

                if (y > this.progressY() - ROW_HEIGHT) {
                    break;
                }

                if (mouseY >= y && mouseY < y + ROW_HEIGHT) {
                    if (ClientChronicle.isFound(chapter.id())) {
                        this.select(chapter);
                    }

                    return true;
                }

                y += ROW_HEIGHT;
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double amount) {
        if (mouseX < this.bookX) {
            int rows = this.chapters.size() + (int) this.chapters.stream()
                    .mapToInt(ChronicleChapters.Chapter::act)
                    .distinct()
                    .count();
            this.listScroll = Mth.clamp(this.listScroll - (int) Math.signum(amount), 0, Math.max(0, rows - 8));
            return true;
        }

        this.turnPage(amount < 0 ? 1 : -1);
        return true;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
