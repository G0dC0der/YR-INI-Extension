package pmoradi.mechanics;

public class EntityExporter {

    public static String export(Entity entity) {
        StringBuilder bu = new StringBuilder(1024 * 1024 * 5);

        bu.append("[").append(entity.getId()).append("]\n");
        for(Pair pair : entity.export())
            bu.append(pair.key).append("=").append(pair.value).append("\n");

        return bu.append("\n").toString();
    }
}
