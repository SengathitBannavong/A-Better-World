package game.tile;
import game.graphic.Sprite;
import java.awt.image.BufferedImage;

public class TileSet {
        private final int firstGid;
        private final int lastGid;
        private final String source;
        private final int tileWidth;
        private final int tileHeight;
        private final int tileCount;
        private final int tileColumns;
        private final Sprite sprite;

        public TileSet(int firstGid, String source, int tileWidth, int tileHeight, int tileCount, int tileColumns) {
            this.firstGid = firstGid;
            this.source = source;
            this.tileWidth = tileWidth;
            this.tileHeight = tileHeight;
            this.tileCount = tileCount;
            this.tileColumns = tileColumns;
            this.sprite = new Sprite("tiles/" + source + ".png", tileWidth, tileHeight);
            this.lastGid = firstGid + tileCount - 1;
        }

        public BufferedImage getImage(int i){
            return (i >= firstGid) ? sprite.getSprite((i - firstGid) % tileColumns, (i - firstGid) / tileColumns) : null;
        }

        public int getFirstGid() {
            return firstGid;
        }

        public int getLastGid() {
            return lastGid;
        }

        public Sprite getSprite() {
            return sprite;
        }

        public int getTileColumns() {
            return tileColumns;
        }

        public int getTileCount() {
            return tileCount;
        }

        public int getTileHeight() {
            return tileHeight;
        }

        public int getTileWidth() {
            return tileWidth;
        }

        public String getSource() {
            return source;
        }
}
