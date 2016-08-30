package pmoradi.mechanics;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Container {

    private enum TechnoType {
        VEHICLE,
        INFANTRY,
        BUILDING,
        AIRCRAFT,
        OTHER
    }

    private static final String IMPORT = "@import";
    private static final String EXTEND = "@extend";
    private static final String CALC = "@calc";

    private ScriptEngine engine;
    private List<Entity> entities;
    private List<Pair> variables;
    private Pattern idPattern;
    private Pattern pairPattern;

    public Container() {
        entities = new LinkedList<>();
        variables = new ArrayList<>();
        idPattern = Pattern.compile("\\[.*\\].*");
        pairPattern = Pattern.compile(".*\\=.*");
        engine = new ScriptEngineManager().getEngineByName("JavaScript");
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
                        if (entity.getId().equals("Variables"))
                            variables.addAll(entity.getTags());
                        else
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
    }

    public void process() throws ScriptException {
        Entity vehicleTypes = findById("VehicleTypes");
        Entity infantryTypes = findById("InfantryTypes");
        Entity buildingsTypes = findById("BuildingTypes");
        Entity aircraftTypes = findById("AircraftTypes");

        for(Entity entity : entities) {
            List<Pair> tags = entity.getTags();

            for(Pair tag : tags) {
                if (tag.key.equals(EXTEND)) {
                    Entity parent = findById(tag.value);

                    //Copy tags. Do not override subclass
                }
                if (tag.key.contains("$")) {

                }
                if(tag.value.contains("$")) {

                }

                if(tag.value.startsWith(CALC)) {
                    String expression = tag.value.replace(CALC, "").trim();
                    String evaluated = (String) engine.eval(expression);
                }

                TechnoType type = guessType(entity);
                if(type != TechnoType.OTHER) {
                    //Auto add
                }
            }
        }
    }

    public List<Entity> export() {
        return new ArrayList<>(entities);
    }

    private TechnoType guessType(Entity entity) {
        return null;
    }

    private Entity findById(String id) {
        for(Entity entity : entities) {
            if(id.equals(entity.getId()))
                return entity;
        }
        return null;
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