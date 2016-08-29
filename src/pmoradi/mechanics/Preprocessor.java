package pmoradi.mechanics;

import java.io.File;
import java.io.IOException;

public class Preprocessor {

    private File src;
    private File dest;
    private boolean overwrite;
    private Container container;

    public Preprocessor(File src, File dest) {
        if (src.getAbsolutePath().equals(dest.getAbsolutePath()))
            throw new IllegalArgumentException("The source and destination file point to the same target.");

        this.src = src;
        this.dest = dest;
    }

    public void overwriteDestinationFile(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public void compute() throws IOException {
        container = new Container();
        container.parse(src);

        container.ship(dest);
    }
}
