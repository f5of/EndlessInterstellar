package packer;

import arc.ApplicationListener;
import arc.Core;
import arc.backend.sdl.SdlApplication;
import arc.backend.sdl.SdlConfig;
import arc.backend.sdl.SdlGL30;
import arc.backend.sdl.jni.SDLGL;
import arc.files.Fi;
import arc.graphics.*;
import arc.graphics.g2d.PixmapPacker;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.io.ByteBufferOutput;
import arc.util.io.Writes;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class AtlasPacker {
    public static int textureAtlasSize = 4096;
    public static Seq<TextureEntry> textures = new Seq<>();
    public static Seq<RegionEntry> regions = new Seq<>();
    public static PixmapIO.PngWriter writer = new PixmapIO.PngWriter();

    public static void main(String[] args) {
        new SdlApplication(new ApplicationListener() {
            @Override
            public void init() {
                Seq<Fi> spritesDirs = null;
                Seq<Fi> sprites = new Seq<>();

                Fi out = null;

                for (String arg : args) {
                    if (arg.contains("sprites=")) {
                        spritesDirs = Seq.with(arg.substring(8).split(";")).map(Fi::new);
                    } else if (arg.contains("outdir=")) {
                        out = new Fi(arg.substring(7)).child("sprites.atlas");
                    }
                }

                if (spritesDirs == null || out == null) return;

                for (Fi f : spritesDirs) {
                    sprites.add(f.findAll());
                }

                Log.info(sprites);

                for (int i = 0; i < sprites.size; i++) {
                    Texture sprite = new Texture(sprites.get(i));
                    if (sprite.width > textureAtlasSize || sprite.height > textureAtlasSize)
                        throw new RuntimeException(new IllegalArgumentException("Sprite size is too big " + sprites.get(i)));
                    int finalI = i;
                    if (textures.find(t -> t.tryPlace(sprite, sprites.get(finalI).name())) == null) {
                        TextureEntry texture = new TextureEntry();
                        textures.add(texture);
                        texture.tryPlace(sprite, sprites.get(finalI).name());
                    }
                }

                boolean flipX = false, flipY = true;

                Writes writes = out.writes();
                writes.bool(flipX);
                writes.bool(flipY);

                writes.i(textures.size);
                writes.i(regions.size);

                textures.each(texture -> {
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    try {
                        writer.write(output, texture.texture.getTextureData().getPixmap());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    writes.i(output.size());
                    writes.b(output.toByteArray());

                    try {
                        writer.write(new Fi("t" + textures.indexOf(texture) + ".png"), texture.texture.getTextureData().getPixmap());
                        texture.drawCells();
                        writer.write(new Fi("t" + textures.indexOf(texture) + "cells.png"), texture.texture.getTextureData().getPixmap());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                regions.each(region -> {
                    writes.i(region.x);
                    writes.i(region.y);
                    writes.i(region.w);
                    writes.i(region.h);
                    writes.i(region.id);
                    writes.str(region.name);
                });

                writes.close();

                Core.app.exit();
            }

            @Override
            public void update() {

            }
        }, new SdlConfig(){{
            gl30 = true;
        }});
    }

    static class TextureEntry {
        public Texture texture;
        public boolean[][] cells;

        public TextureEntry() {
            texture = new Texture(new Pixmap(textureAtlasSize, textureAtlasSize));
            cells = new boolean[textureAtlasSize / 32][textureAtlasSize / 32];
            for (boolean[] cell : cells) {
                Arrays.fill(cell, true);
            }
        }

        public boolean tryPlace(Texture texture, String fileName) {
            int w = Mathf.ceil(texture.width / 32f);
            int h = Mathf.ceil(texture.height / 32f);
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    if (checkRect(i, j, w, h)) {
                        regions.add(new RegionEntry(i * 32, j * 32, texture.width, texture.height,
                                fileName, textures.indexOf(this)));
                        place(texture, i * 32, j * 32);
                        return true;
                    }
                }
            }
            return false;
        }

        boolean checkRect(int x, int y, int w, int h) {
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    if (!at(x + i, y + j)) return false;
                }
            }
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    cells[x + i][y + j] = false;
                }
            }
            return true;
        }

        boolean at(int x, int y) {
            if (textureAtlasSize / 32 <= x || textureAtlasSize / 32 <= y) return false;
            return cells[x][y];
        }

        void place(Texture texture, int x, int y) {
            Log.info("Texture drew at " + x + " " + y + " " + texture.width + " " + texture.height);

            this.texture.getTextureData().getPixmap().draw(texture.getTextureData().getPixmap(), x, y);
        }

        void drawCells() {
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    texture.getTextureData().getPixmap().fillRect(i * 32, j * 32, 32, 32,
                            (cells[i][j] ? Color.green : Color.red).rgba8888());
                }
            }
        }
    }

    static class RegionEntry {
        int x, y, w,h, id;
        String name;

        public RegionEntry(int x, int y, int w, int h, String n, int i) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            name = n;
            id = i;
        }
    }
}
