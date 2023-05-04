package content;

import mindustry.ctype.Content;

public class EIContentLoader {
    public static void load() {
        EIItems.load();
        EIBlocks.load();
    }

    public <T extends Content> T handleContent(T content) {
        return content;
    }
}
