package com.k8stoc4.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class C4Relationship {
    private final String source;
    private final String target;
    private final String description;
    private final String technology;
    private final String tag;

    public C4Relationship(final String source,
                          final String target,
                          final String description,
                          final String technology) {
        this.source = source;
        this.target = target;
        this.description = description;
        this.technology = technology;
        this.tag = "";
    }

    public C4Relationship(final String source,
                          final String target,
                          final String description,
                          final String technology,
                          final String tag) {
        this.source = source;
        this.target = target;
        this.description = description;
        this.technology = technology;
        this.tag=tag;
    }
}
