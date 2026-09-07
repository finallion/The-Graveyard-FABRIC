package com.lion.graveyard.chronicle.client;

import com.lion.graveyard.Graveyard;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/// Chapters down the left, the open book on the right. Unfound chapters are listed but dimmed,
/// so the player sees how much of the story is still out there.
@Environment(EnvType.CLIENT)
public class ChronicleScreen extends Screen {
    public static final Identifier BUTTON_TEXTURE = new Identifier(Graveyard.MOD_ID, "textures/gui/chronicle_button.png");
    private static final Identifier BOOK_TEXTURE = new Identifier("textures/gui/book.png");

    private static final int BOOK_WIDTH = 192;
    private static final int BOOK_HEIGHT = 192;
    private static final int TEXT_WIDTH = 114;
    private static final int LINES_PER_PAGE = 14;
    private static final int LIST_WIDTH = 120;
    private static final int ROW_HEIGHT = 11;

    @Nullable private final Screen parent;
    private final List<ChronicleChapters.Chapter> chapters = ChronicleChapters.load();

    @Nullable private ChronicleChapters.Chapter selected;
    private List<List<OrderedText>> bookPages = List.of();
    private int pageIndex;
    private int listScroll;

    private int listX, listY, bookX, bookY;
    private PageTurnWidget nextButton;
    private PageTurnWidget previousButton;

    public ChronicleScreen(@Nullable Screen parent) {
        super(Text.translatable("chronicle." + Graveyard.MOD_ID + ".title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int totalWidth = LIST_WIDTH + 8 + BOOK_WIDTH;
        this.listX = (this.width - totalWidth) / 2;
        this.bookX = this.listX + LIST_WIDTH + 8;
        this.bookY = Math.max(2, (this.height - BOOK_HEIGHT) / 2 - 10);
        this.listY = this.bookY + 2;

        this.nextButton = this.addDrawableChild(new PageTurnWidget(this.bookX + 116, this.bookY + 159, true, b -> this.turnPage(1), true));
        this.previousButton = this.addDrawableChild(new PageTurnWidget(this.bookX + 43, this.bookY + 159, false, b -> this.turnPage(-1), true));
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, b -> this.close())
                .dimensions(this.bookX + 48, this.bookY + BOOK_HEIGHT - 4, 98, 20).build());

        if (this.selected == null) {
            this.chapters.stream().filter(c -> ClientChronicle.isFound(c.id())).findFirst().ifPresent(this::select);
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

    /// Paragraphs are wrapped to the book column and cut into pages; a blank line separates paragraphs.
    private List<List<OrderedText>> paginate(ChronicleChapters.Chapter chapter) {
        List<OrderedText> lines = new ArrayList<>();
        for (String paragraph : chapter.pages()) {
            lines.addAll(this.textRenderer.wrapLines(Text.literal(paragraph), TEXT_WIDTH));
            lines.add(OrderedText.EMPTY);
        }
        List<List<OrderedText>> pages = new ArrayList<>();
        for (int i = 0; i < lines.size(); i += LINES_PER_PAGE) {
            pages.add(lines.subList(i, Math.min(lines.size(), i + LINES_PER_PAGE)));
        }
        if (pages.isEmpty()) pages.add(List.of());
        return pages;
    }

    private void turnPage(int delta) {
        this.pageIndex = MathHelper.clamp(this.pageIndex + delta, 0, this.bookPages.size() - 1);
        this.updateButtons();
    }

    private void updateButtons() {
        this.nextButton.visible = this.pageIndex < this.bookPages.size() - 1;
        this.previousButton.visible = this.pageIndex > 0;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        this.renderList(context, mouseX, mouseY);
        this.renderBook(context);
        super.render(context, mouseX, mouseY, delta);
    }

    private void renderList(DrawContext context, int mouseX, int mouseY) {
        TextRenderer font = this.textRenderer;
        int y = this.listY;
        context.drawText(font, this.title, this.listX, y, 0xFFFFFF, true);
        y += 14;
        int lastAct = -1;
        int rows = 0;
        for (ChronicleChapters.Chapter chapter : this.chapters) {
            if (chapter.act() != lastAct) {
                lastAct = chapter.act();
                if (rows++ >= this.listScroll) {
                    context.drawText(font, Text.translatable("chronicle." + Graveyard.MOD_ID + ".act." + lastAct).formatted(Formatting.GOLD), this.listX, y, 0xFFFFFF, true);
                    y += ROW_HEIGHT;
                }
            }
            if (rows++ < this.listScroll) continue;
            if (y > this.height - ROW_HEIGHT) break;
            boolean found = ClientChronicle.isFound(chapter.id());
            boolean hovered = found && mouseX >= this.listX && mouseX < this.listX + LIST_WIDTH && mouseY >= y && mouseY < y + ROW_HEIGHT;
            Text label = found ? Text.translatable(chapter.titleKey()) : Text.literal("? ? ?");
            int color = chapter == this.selected ? 0xFFE0A0 : hovered ? 0xFFFFFF : found ? 0xC0C0C0 : 0x505050;
            context.drawText(font, Text.literal("  ").append(label), this.listX, y, color, true);
            y += ROW_HEIGHT;
        }
        int foundCount = (int) this.chapters.stream().filter(c -> ClientChronicle.isFound(c.id())).count();
        context.drawText(font, Text.translatable("chronicle." + Graveyard.MOD_ID + ".progress", foundCount, this.chapters.size()).formatted(Formatting.DARK_GRAY),
                this.listX, this.bookY + BOOK_HEIGHT - 4 + 6, 0xFFFFFF, true);
    }

    private void renderBook(DrawContext context) {
        context.drawTexture(BOOK_TEXTURE, this.bookX, this.bookY, 0, 0, BOOK_WIDTH, BOOK_HEIGHT);
        if (this.selected == null) {
            List<OrderedText> hint = this.textRenderer.wrapLines(Text.translatable("chronicle." + Graveyard.MOD_ID + ".empty"), TEXT_WIDTH);
            int y = this.bookY + 40;
            for (OrderedText line : hint) {
                context.drawText(this.textRenderer, line, this.bookX + 36, y, 0x404040, false);
                y += 9;
            }
            return;
        }
        Text header = Text.translatable(this.selected.titleKey()).formatted(Formatting.BOLD);
        context.drawText(this.textRenderer, header, this.bookX + 36, this.bookY + 16, 0x202020, false);
        if (!this.selected.source().isEmpty()) {
            context.drawText(this.textRenderer, Text.literal(this.selected.source()).formatted(Formatting.ITALIC), this.bookX + 36, this.bookY + 26, 0x606060, false);
        }
        Text counter = Text.translatable("book.pageIndicator", this.pageIndex + 1, this.bookPages.size());
        context.drawText(this.textRenderer, counter, this.bookX + 36 + TEXT_WIDTH - this.textRenderer.getWidth(counter), this.bookY + 16, 0x606060, false);

        int y = this.bookY + 40;
        for (OrderedText line : this.bookPages.get(this.pageIndex)) {
            context.drawText(this.textRenderer, line, this.bookX + 36, y, 0x202020, false);
            y += 9;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= this.listX && mouseX < this.listX + LIST_WIDTH) {
            int y = this.listY + 14;
            int lastAct = -1;
            int rows = 0;
            for (ChronicleChapters.Chapter chapter : this.chapters) {
                if (chapter.act() != lastAct) {
                    lastAct = chapter.act();
                    if (rows++ >= this.listScroll) y += ROW_HEIGHT;
                }
                if (rows++ < this.listScroll) continue;
                if (mouseY >= y && mouseY < y + ROW_HEIGHT) {
                    if (ClientChronicle.isFound(chapter.id())) this.select(chapter);
                    return true;
                }
                y += ROW_HEIGHT;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (mouseX < this.bookX) {
            int rows = this.chapters.size() + (int) this.chapters.stream().mapToInt(ChronicleChapters.Chapter::act).distinct().count();
            this.listScroll = MathHelper.clamp(this.listScroll - (int) Math.signum(amount), 0, Math.max(0, rows - 8));
            return true;
        }
        this.turnPage(amount < 0 ? 1 : -1);
        return true;
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
