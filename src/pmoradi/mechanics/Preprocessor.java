package pmoradi.mechanics;

import javax.script.ScriptException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Preprocessor {

    private File src;
    private File dest;
    private boolean overwrite;

    public Preprocessor(File src, File dest) {
        if (src.getAbsolutePath().equals(dest.getAbsolutePath()))
            throw new IllegalArgumentException("The source and destination file point to the same target.");

        this.src = src;
        this.dest = dest;
        overwrite = true;
    }

    public void overwriteDestinationFile(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public void compute() throws IOException, ScriptException {
        Container container = new Container();
        container.parse(src);
        container.process();

        if(overwrite && dest.exists())
            dest.delete();

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(dest))) { //TODO: Inline
            for(Entity entity : container.export()) {
                writer.write("[" + entity.getId() + "]");
                writer.newLine();
                for(Pair pair : entity.getTags()) {
                    writer.write(pair.toString());
                    writer.newLine();
                }
                writer.newLine();
            }
        }
    }
}
