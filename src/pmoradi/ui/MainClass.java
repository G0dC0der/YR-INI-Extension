package pmoradi.ui;

import pmoradi.mechanics.Preprocessor;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainClass {
    public static void main(String... args) throws IOException, ScriptException {
        File src = new File("ini/rulesmd.ini");
        File dest = new File("ini/rules_out.ini");

        long start = System.currentTimeMillis();
        new Preprocessor(src, dest).compute();
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
