package render;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import model.*;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class C4DslRenderer {

    private static final MustacheFactory MF = new DefaultMustacheFactory();

    // Render principale: workspace
    public String renderModel(C4Model model) {
        StringBuilder sb = new StringBuilder();
        sb.append("model").append("{\n");
        for (C4Namespace namespace : model.getNamespaces().values()) {
            sb.append(renderNamespace(namespace));
        }
        sb.append("}\n");
        return sb.toString();
    }

    private String renderNamespace(C4Namespace namespace) {
        Mustache mustache = MF.compile("templates/namespace.mustache");

        // Mappa i componenti con kindLower
        List<Map<String, String>> comps = namespace.getComponents().stream().map(c -> Map.of(
                "kind", c.getKind().toLowerCase(),
                "id", c.getId(),
                "name", c.getName(),
                "technology", c.getKind(),
                "description", c.getDescription()
        )).toList();
        List<String> relations= new ArrayList<>();
        for (C4Relationship rel: namespace.getRelationships()){
            relations.add(rel.getSource()+" -> "+rel.getTarget());
        }
        Map<String, Object> ctx = Map.of(
                "name", namespace.getName(),
                "components", comps,
                "relations", relations
        );

        StringWriter writer = new StringWriter();
        mustache.execute(writer, ctx);
        return writer.toString();
    }

//    // Render di un container
//    private String renderContainer(C4Container container, int level) {
//        StringBuilder sb = new StringBuilder();
//        String indent = INDENT.repeat(level);
//
//        sb.append(indent).append("container ").append(container.getName()).append(" {\n");
//        sb.append(indent).append(INDENT)
//                .append("technology \"").append(container.getType()).append("\"\n");
//
//        // Aggiunge metadata come description se presente
//        if (!container.getMetadata().isEmpty()) {
//            for (var entry : container.getMetadata().entrySet()) {
//                sb.append(indent).append(INDENT)
//                        .append(entry.getKey()).append(" \"").append(entry.getValue()).append("\"\n");
//            }
//        }
//
//        // Aggiunge componenti
//        for (C4Component comp : container.getComponents()) {
//            sb.append(renderComponent(comp, level + 1));
//        }
//
//        sb.append(indent).append("}\n");
//        return sb.toString();
//    }



    // Render principale: workspace
    public String renderRelations(C4Model model) {
        StringBuilder sb = new StringBuilder();
        for (C4Relationship rel: model.getRelationships()){
            sb.append(rel.getSource()).append(" -> ").append(rel.getTarget()).append("\n");
        }

        return sb.toString();
    }

    public String renderSpec(C4Model model) {
        StringBuilder sb = new StringBuilder();
        sb.append("specification ").append("{").append("\n");
        for (String elementName: model.getSpecifications()){
            sb.append("element ").append(" ").append(elementName).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

}

