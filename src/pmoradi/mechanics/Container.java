package pmoradi.mechanics;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Container {

    private static final String IMPORT = "@import";
    private static final String EXTEND = "@extend";
    private static final String CALC = "@calc";

    private ScriptEngine engine;
    private List<Entity> entities;
    private Map<String, String> variables;
    private Map<String, List<Pair>> customTags;
    private Pattern idPattern;
    private Pattern pairPattern;
    private Pattern varPattern;

    public Container() {
        entities = new ArrayList<>(2000);
        variables = new HashMap<>();
        customTags = new HashMap<>();
        idPattern = Pattern.compile("\\[.*\\].*");
        pairPattern = Pattern.compile(".*\\=.*");
        varPattern = Pattern.compile("\\$[a-zA-Z]+");
        engine = new ScriptEngineManager().getEngineByName("JavaScript");
    }

    public void parse(File src) throws IOException {
        if (!src.exists() || src.isDirectory() || !src.canRead())
            throw new IOException("The source file could not be read or does not exists: " + src.getAbsolutePath());

        try (BufferedReader reader = getReader(src)) {
            String line = null;
            Entity entity = null;
            boolean inComment = false;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#"))
                    inComment = !inComment;

                if (inComment || line.startsWith(";"))
                    continue;

                if (idPattern.matcher(line).matches()) {
                    if (entity != null && !entity.getId().equals("Variables") && !entity.getId().equals("CustomTags")) {
                        entities.add(entity);
                    }
                    entity = new Entity(toId(line));
                } else if (pairPattern.matcher(line).matches()) {
                    Pair pair = toPair(line);

                    if (pair.key.equals(IMPORT)) {
                        parse(new File(src.getParent(), pair.value));
                    } else if (entity.getId().equals("Variables")) {
                        variables.put(pair.key, pair.value);
                    } else if (entity.getId().equals("CustomTags")) {
                        List<Pair> list = Stream.of(pair.value.split(":")).map(this::toPair).collect(Collectors.toList());
                        customTags.put(pair.key, list);
                    } else {
                        entity.add(pair);
                    }
                } else if (!line.isEmpty()) {
                    System.err.println("Unknown line of code: " + line);
                }
            }

            entities.add(entity);
        }
    }

    public void process() throws ScriptException {
        for(Entity entity : entities) {
            List<Pair> copyList = entity.getTags();
            for(int i = 0; i < copyList.size(); i++) {
                Pair tag = copyList.get(i);

                if (tag.value.contains("$")) {
                    tag.value = replaceVars(tag.value);
                }

                if (tag.key.equals(EXTEND)) {
                    merge(findById(tag.value), entity);
                    entity.remove(tag.key);
                }

                if(tag.value.startsWith(CALC)) {
                    String expression = tag.value.replace(CALC, "").trim();
                    tag.value = engine.eval(expression).toString();
                }

                List<Pair> customTags = this.customTags.get(tag.key);
                if (customTags != null && (tag.value.equals("true") || tag.value.equals("yes"))) {
                    copyList.addAll(customTags);
                    entity.add(customTags);
                    entity.remove(tag.key);
                }
            }
        }
    }

    public List<Entity> export() {
        return new ArrayList<>(entities);
    }

    private void merge(Entity source, Entity target) {
        for(Pair tag : source.getTags()) {
            if(!target.containsKey(tag.key))
                target.add(tag);
        }
    }

    private Entity findById(String id) {
        for (Entity entity : entities) {
            if (entity.getId().equals(id))
                return entity;
        }
        throw new IllegalArgumentException("The given ID '" + id + "' could not be found.");
    }

    private String getVariableValue(String key){
        if(key.startsWith("$"))
            key = key.substring(1, key.length());

        return variables.get(key);
    }

    private BufferedReader getReader(File file) throws FileNotFoundException {
        return new BufferedReader(new FileReader(file));
    }

    private Pair toPair(String line) {
        int eqIndex = line.indexOf("=");
        String[] tokens = { line.substring(0, eqIndex), line.substring(eqIndex + 1) };
        Pair pair = new Pair(tokens[0], tokens.length == 1 ? "" : tokens[1]);
        int index = pair.value.indexOf(";");
        pair.value = pair.value.substring(0, index != -1 ? index : pair.value.length()).trim();

        return pair;
    }

    private String toId(String line) {
        return line.substring(1, line.indexOf("]"));
    }

    private String replaceVars(String str) {
        Matcher matcher = varPattern.matcher(str);

        while(matcher.find()) {
            String var = matcher.group();
            str = str.replace(var, getVariableValue(var));
        }

        return str;
    }
}