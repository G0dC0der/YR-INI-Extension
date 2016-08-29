package pmoradi.mechanics;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Container {

    private static final String IMPORT = "@import";

    private List<Entity> entities;
    private List<Pair> variables;
    private Pattern idPattern;
    private Pattern pairPattern;

    public Container() {
        entities = new LinkedList<>();
        variables = new ArrayList<>();
        idPattern = Pattern.compile("\\[.*\\].*");
        pairPattern = Pattern.compile(".*\\=.*");
    }

    public void parse(File src) throws IOException {
        try (BufferedReader reader = getReader(src)) {
            String line = null;
            Entity entity = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith(";"))
                    continue;

                if (idPattern.matcher(line).matches()) {
                    if (entity != null) {
                        entities.add(entity);
                    }

                    entity = new Entity(toId(line));
                } else if (pairPattern.matcher(line).matches()) {
                    Pair pair = toPair(line);

                    if (pair.key.equals(IMPORT)) {
                        parse(new File(pair.value));
                    } else {
                        entity.add(pair);
                    }
                }
            }

            entities.add(entity);
        }


        for(Iterator<Entity> iterator = entities.iterator(); iterator.hasNext();){
            Entity entity = iterator.next();
            if(entity.getId().equals("Variables")) {
                iterator.remove();
                variables.addAll(Arrays.asList(entity.export()));
            }
        }
    }

    public void process() {

    }

    public void ship(File dest) throws IOException {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(dest))) {
            for(Entity entity : entities)
                writer.write(EntityExporter.export(entity));
        }
    }

    private BufferedReader getReader(File file) throws FileNotFoundException {
        return new BufferedReader(new FileReader(file));
    }

    private Pair toPair(String line) {
        String[] tokens = line.split("=");
        Pair pair = new Pair(tokens[0], tokens.length == 1 ? "" : tokens[1]);
        int index = pair.value.indexOf(";");
        pair.value = pair.value.substring(0, index != -1 ? index : pair.value.length()).trim();

        return pair;
    }

    private String toId(String line) {
        return line.substring(1, line.indexOf("]"));
    }
}