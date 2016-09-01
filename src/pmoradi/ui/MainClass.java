package pmoradi.ui;

import pmoradi.mechanics.Preprocessor;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainClass {

    public static void main(String... args) throws IOException, ScriptException {
        String src = args[0];
        String dest = args[1];
        boolean overwrite = Boolean.parseBoolean(args.length >= 3 ? args[2] : "true");

        Preprocessor preprocessor = new Preprocessor(new File(src), new File(dest));
        preprocessor.overwriteDestinationFile(overwrite);
        long start = System.currentTimeMillis();
        preprocessor.compute();
        long end = System.currentTimeMillis();
        System.out.println("Success! It took " + (end - start) + " milliseconds to compile the file.");
    }
}
