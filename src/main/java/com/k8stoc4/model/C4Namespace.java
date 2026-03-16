package com.k8stoc4.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@Getter
@ToString
public class C4Namespace {
    private final String name;
    private final Set<C4Component> components = new LinkedHashSet<>();
    private final Set<C4LabelGroup> labelGroups = new LinkedHashSet<>();
    private final Set<C4Relationship> relationships = new LinkedHashSet<>();
    private final Map<String, C4LabelGroup> labelGroupIndex = new LinkedHashMap<>();

    public C4Namespace(final String name) { this.name = name; }
    public void addComponents(final C4Component c) { components.add(c); }
    public void removeComponent(final C4Component c) { components.remove(c); }
    public void addLabelGroup(final C4LabelGroup lg) {
        labelGroups.add(lg);
        labelGroupIndex.put(lg.getLabelKey() + ":" + lg.getLabelValue(), lg);
    }
    public void addRelationship(final C4Relationship r) { relationships.add(r); }

    public C4LabelGroup getOrCreateLabelGroup(final String labelKey, final String labelValue) {
        final String key = labelKey + ":" + labelValue;
        final C4LabelGroup group = labelGroupIndex.computeIfAbsent(key,
            k -> new C4LabelGroup(labelKey + "_" + labelValue, labelKey, labelValue));
        labelGroups.add(group);
        return group;
    }
}
