package org.example;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import lombok.extern.java.Log;
import render.C4DslRenderer;
import visitor.C4ModelBuilderVisitor;
import visitor.VisitorUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.List;

@Log
public class KubernetesC4FromYamlVisitor {

    public static void main(String[] args) throws Exception {

        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            FileInputStream fis=new FileInputStream(new File("src/main/resources/complex.yaml"));

            List<HasMetadata> resources = client.load(fis).items();
            C4ModelBuilderVisitor visitor = new C4ModelBuilderVisitor();
            for (HasMetadata r : resources) {
                VisitorUtils.accept(r, visitor);
            }

            C4DslRenderer renderer=new C4DslRenderer();
            //System.out.println(renderer.renderSpec(visitor.getModel()));
            //System.out.println(renderer.renderModel(visitor.getModel()));

            try (FileWriter f = new FileWriter("spec.c4", false)) {
                f.write(renderer.renderSpec(visitor.getModel()));
            }
            try (FileWriter f = new FileWriter("model.c4", false)) {
                f.write(renderer.renderModel(visitor.getModel()));
            }

        }
    }
}
