package visitor;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.fabric8.kubernetes.api.model.networking.v1.IngressRule;
import lombok.Getter;
import lombok.Setter;
import model.*;

import java.util.*;

import static visitor.VisitorUtils.containerMatchesSelector;

@Getter
@Setter
public class C4ModelBuilderVisitor implements KubernetesResourceVisitor {

    private final C4Model model = new C4Model();

    private C4Namespace getOrCreateSystem(String ns) {
        return model.getNamespaces().computeIfAbsent(ns, C4Namespace::new);
    }

    @Override
    public void visit(StatefulSet statefulSet) {
        model.getSpecifications().add(statefulSet.getKind().toLowerCase());
        String ns = Optional.ofNullable(statefulSet.getMetadata().getNamespace()).orElse("default");
        C4Namespace namespace = getOrCreateSystem(ns);

        C4Component component = new C4Component(
                statefulSet.getMetadata().getNamespace(),
                statefulSet.getMetadata().getName(),
                statefulSet.getKind());
        PodSpec podSpec = statefulSet.getSpec().getTemplate().getSpec();

        if (podSpec != null && podSpec.getContainers() != null) {
            Container c = podSpec.getContainers().get(0);
            component.setImage(c.getImage());
            //Add Metadata from pod labels
            component.setMetadata(
                    statefulSet.getSpec().getTemplate().getMetadata().getLabels());


            if (c.getEnvFrom()!=null){
                for(EnvFromSource envFrom: c.getEnvFrom()){
                    addValueFromRelationship(namespace, component, envFrom);
                }
            }
            if (c.getEnv() != null) {
                for (EnvVar e : c.getEnv()) {
                    // Copia env
                    if (e.getValueFrom() != null) {
                        addValueFromKeyRelationship(namespace, component, e.getValueFrom());
                    }else{
                        component.getEnv().put(e.getName(), e.getValue());
                    }

                }
            }
        }

        if (podSpec != null && podSpec.getVolumes() != null) {
            for (Volume volume:podSpec.getVolumes()){
                addVolumeRelationship(namespace,component,volume);
            }
        }
        namespace.addCompoments(component);
    }

    @Override
    public void visit(Deployment deployment) {
        model.getSpecifications().add(deployment.getKind().toLowerCase());
        String ns = Optional.ofNullable(deployment.getMetadata().getNamespace()).orElse("default");
        C4Namespace namespace = getOrCreateSystem(ns);

        C4Component component = new C4Component(
                deployment.getMetadata().getNamespace(),
                deployment.getMetadata().getName(),
                deployment.getKind());
        PodSpec podSpec = deployment.getSpec().getTemplate().getSpec();

        if (podSpec != null && podSpec.getContainers() != null) {
            Container c = podSpec.getContainers().get(0);
            component.setImage(c.getImage());
            //Add Metadata from pod labels
            component.setMetadata(
                    deployment.getSpec().getTemplate().getMetadata().getLabels());

            if (c.getEnvFrom()!=null){
                for(EnvFromSource envFrom: c.getEnvFrom()){
                    addValueFromRelationship(namespace, component, envFrom);
                }
            }
            if (c.getEnv() != null) {
                for (EnvVar e : c.getEnv()) {
                    // Copia env
                    if (e.getValueFrom() != null) {
                        addValueFromKeyRelationship(namespace, component, e.getValueFrom());
                    }else{
                        component.getEnv().put(e.getName(), e.getValue());
                    }

                }
            }
        }


        if (podSpec != null && podSpec.getVolumes() != null) {
            for (Volume volume:podSpec.getVolumes()){
                addVolumeRelationship(namespace,component,volume);
            }
        }

        namespace.addCompoments(component);
    }

    private void addVolumeRelationship(C4Namespace namespace,C4Component component,Volume volume) {
        String source = component.getNamespace() + "." + component.getId();
        String target="";
        if ( volume.getPersistentVolumeClaim()!=null && volume.getPersistentVolumeClaim().getClaimName()!=null){
            target = component.getNamespace() + ".volume_" + volume.getPersistentVolumeClaim().getClaimName();
            namespace.addRelationship(new C4Relationship(source,target,"mount","volume"));
        }

    }

    private void addValueFromRelationship(C4Namespace namespace,
                                          C4Component component,
                                          EnvFromSource valueFrom) {
        String source = component.getNamespace() + "." + component.getId();
        if (valueFrom.getConfigMapRef() != null) {
            String target = component.getNamespace() + ".configmap_" + valueFrom.getConfigMapRef().getName();
            namespace.addRelationship(new C4Relationship(source, target, "mount", "configmap"));
        }
        if (valueFrom.getSecretRef() != null) {
            String target = component.getNamespace() + ".secret_" + valueFrom.getSecretRef().getName();
            namespace.addRelationship(new C4Relationship(source, target, "mount", "secret"));
        }

    }

    private void addValueFromKeyRelationship(C4Namespace namespace,
                                          C4Component component,
                                          EnvVarSource valueFrom) {

        String source = component.getNamespace() + "." + component.getId();
        if (valueFrom.getConfigMapKeyRef() != null) {
            String target = component.getNamespace() + ".configmap_" + valueFrom.getConfigMapKeyRef().getName();
            namespace.addRelationship(new C4Relationship(source, target, "mount", "configmap"));
        }
        if (valueFrom.getSecretKeyRef() != null) {
            String target = component.getNamespace() + ".secret_" + valueFrom.getSecretKeyRef().getName();
            namespace.addRelationship(new C4Relationship(source, target, "mount", "secret"));
        }
    }

    @Override
    public void visit(Service svc) {
        model.getSpecifications().add(svc.getKind().toLowerCase());
        String ns = Optional.ofNullable(svc.getMetadata().getNamespace()).orElse("default");
        C4Namespace namespace = getOrCreateSystem(ns);

        C4Component service = new C4Component(svc.getMetadata().getNamespace(),
                svc.getMetadata().getName(), svc.getKind());
        namespace.addCompoments(service);

        Map<String, String> selector = svc.getSpec().getSelector();
        if (selector != null && !selector.isEmpty()) {
            for (C4Namespace currNamespace : model.getNamespaces().values()) {
                for (C4Component component : currNamespace.getComponents()) {
                    if (containerMatchesSelector(component, selector)) {
                        namespace.addRelationship(new C4Relationship(
                                //TODO Refactor
                               service.getNamespace()+".service_"+service.getName(),
                                component.getNamespace()+"."+component.getId(),
                                "routes to",
                                "TCP/HTTP"
                        ));
                    }
                }
            }
        }
    }

    @Override
    public void visit(Ingress ing) {
        model.getSpecifications().add(ing.getKind().toLowerCase());
        String ns = Optional.ofNullable(ing.getMetadata().getNamespace()).orElse("default");
        C4Namespace namespace = getOrCreateSystem(ns);
        C4Component ingress = new C4Component(ing.getMetadata().getNamespace(),
                ing.getMetadata().getName(), ing.getKind());
        namespace.addCompoments(ingress);

        for (IngressRule rule : ing.getSpec().getRules()) {
            rule.getHttp().getPaths().forEach(path -> {
                String svcName = path.getBackend().getService().getName();
                namespace.addRelationship(new C4Relationship(
                        ingress.getNamespace()+"."+ingress.getId(),
                        ingress.getNamespace()+".service_"+svcName,
                        "routes HTTP traffic",
                        "HTTP"
                ));
            });
        }
    }

    @Override
    public void visit(HasMetadata resource) {
        model.getSpecifications().add(resource.getKind().toLowerCase());
        String ns = Optional.ofNullable(resource.getMetadata().getNamespace()).orElse("default");
        C4Namespace system = getOrCreateSystem(ns);

        C4Component container = new C4Component(resource.getMetadata().getNamespace(), resource.getMetadata().getName(), resource.getKind());
        system.addCompoments(container);
    }

}
