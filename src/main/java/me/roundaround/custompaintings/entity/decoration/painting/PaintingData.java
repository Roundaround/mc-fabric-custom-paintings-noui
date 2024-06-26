package me.roundaround.custompaintings.entity.decoration.painting;

import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public record PaintingData(Identifier id, int index, int width, int height, String name, String artist,
                           boolean isVanilla) {
  public static final PaintingData EMPTY = new PaintingData(null, 0, 0, 0);
  public static final PacketCodec<PacketByteBuf, PaintingData> PACKET_CODEC = PacketCodec.of(
      PaintingData::writeToPacketByteBuf, PaintingData::fromPacketByteBuf);

  public PaintingData(Identifier id, int index, int width, int height) {
    this(id, index, width, height, "", "");
  }

  public PaintingData(Identifier id, int index, int width, int height, String name, String artist) {
    this(id, index, width, height, name, artist, false);
  }

  public PaintingData(PaintingVariant vanillaVariant, int index) {
    this(vanillaVariant.assetId(), index, vanillaVariant.width(), vanillaVariant.height(), vanillaVariant.toString(),
        "", true
    );
  }

  public int getScaledWidth() {
    return this.width() * 16;
  }

  public int getScaledHeight() {
    return this.height() * 16;
  }

  public int getScaledWidth(int maxWidth, int maxHeight) {
    float scale = Math.min((float) maxWidth / this.getScaledWidth(), (float) maxHeight / this.getScaledHeight());
    return Math.round(scale * this.getScaledWidth());
  }

  public int getScaledHeight(int maxWidth, int maxHeight) {
    float scale = Math.min((float) maxWidth / this.getScaledWidth(), (float) maxHeight / this.getScaledHeight());
    return Math.round(scale * this.getScaledHeight());
  }

  public boolean isEmpty() {
    return this.id == null;
  }

  public boolean hasName() {
    return this.isVanilla() || this.name != null && !this.name.isEmpty();
  }

  public boolean hasArtist() {
    return this.isVanilla() || this.artist != null && !this.artist.isEmpty();
  }

  public boolean hasLabel() {
    return this.hasName() || this.hasArtist();
  }

  public MutableText getNameText() {
    if (!this.hasName()) {
      return Text.empty();
    }

    if (this.isVanilla()) {
      return Text.translatable(this.id().toTranslationKey("painting", "title")).formatted(Formatting.YELLOW);
    }

    return Text.literal(this.name).formatted(Formatting.LIGHT_PURPLE);
  }

  public MutableText getArtistText() {
    if (!this.hasArtist()) {
      return Text.empty();
    }

    if (this.isVanilla()) {
      return Text.translatable(this.id().toTranslationKey("painting", "author")).formatted(Formatting.ITALIC);
    }

    return Text.literal(this.artist).formatted(Formatting.ITALIC);
  }

  public Text getLabel() {
    if (!this.hasLabel()) {
      return Text.empty();
    }

    if (!this.hasArtist()) {
      return this.getNameText();
    }

    if (!this.hasName()) {
      return this.getArtistText();
    }

    return Text.empty().append(this.getNameText()).append(" - ").append(this.getArtistText());
  }

  public NbtCompound writeToNbt() {
    NbtCompound nbt = new NbtCompound();
    if (this.isEmpty()) {
      return nbt;
    }

    nbt.putString("Id", this.id.toString());
    nbt.putInt("Index", this.index);
    nbt.putInt("Width", this.width);
    nbt.putInt("Height", this.height);
    nbt.putString("Name", this.name);
    nbt.putString("Artist", this.artist);
    nbt.putBoolean("Vanilla", this.isVanilla);
    return nbt;
  }

  public static PaintingData fromNbt(NbtCompound nbt) {
    if (!nbt.contains("Id")) {
      return EMPTY;
    }

    Identifier id = Identifier.tryParse(nbt.getString("Id"));
    int index = nbt.getInt("Index");
    int width = nbt.getInt("Width");
    int height = nbt.getInt("Height");
    String name = nbt.getString("Name");
    String artist = nbt.getString("Artist");
    boolean isVanilla = nbt.getBoolean("Vanilla");
    return new PaintingData(id, index, width, height, name, artist, isVanilla);
  }

  public void writeToPacketByteBuf(PacketByteBuf buf) {
    if (this.isEmpty()) {
      buf.writeBoolean(false);
      return;
    }
    buf.writeBoolean(true);
    buf.writeIdentifier(this.id);
    buf.writeInt(this.index);
    buf.writeInt(this.width);
    buf.writeInt(this.height);
    buf.writeString(this.name);
    buf.writeString(this.artist);
    buf.writeBoolean(this.isVanilla);
  }

  public static PaintingData fromPacketByteBuf(PacketByteBuf buf) {
    if (!buf.readBoolean()) {
      return EMPTY;
    }
    Identifier id = buf.readIdentifier();
    int index = buf.readInt();
    int width = buf.readInt();
    int height = buf.readInt();
    String name = buf.readString();
    String artist = buf.readString();
    boolean isVanilla = buf.readBoolean();
    return new PaintingData(id, index, width, height, name, artist, isVanilla);
  }

  public boolean isMismatched(PaintingData knownData) {
    return this.isMismatched(knownData, MismatchedCategory.EVERYTHING);
  }

  public boolean isMismatched(PaintingData knownData, MismatchedCategory category) {
    switch (category) {
      case SIZE:
        return this.width() != knownData.width() || this.height() != knownData.height();
      case INFO:
        return !this.name().equals(knownData.name()) || !this.artist().equals(knownData.artist()) ||
            this.isVanilla() != knownData.isVanilla();
      case EVERYTHING:
        return this.width() != knownData.width() || this.height() != knownData.height() ||
            !this.name().equals(knownData.name()) || !this.artist().equals(knownData.artist());
      default:
        return false;
    }
  }

  public enum MismatchedCategory {
    SIZE, INFO, EVERYTHING
  }
}
