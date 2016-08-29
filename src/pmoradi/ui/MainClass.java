package pmoradi.ui;

import pmoradi.mechanics.Preprocessor;

import java.io.File;
import java.io.IOException;

public class MainClass {
    public static void main(String... args) throws IOException {
        File src = new File("ini/rulesmd.ini");
        File dest = new File("ini/rules_out.ini");

        new Preprocessor(src, dest).compute();
    }
}
