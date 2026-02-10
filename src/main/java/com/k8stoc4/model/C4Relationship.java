package com.k8stoc4.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class C4Relationship {
    public String source;      
    public String target;      
    public String description; 
    public String technology;
    public String tag="";

    public C4Relationship(String source,
                          String target,
                          String description,
                          String technology) {
        this.source = source;
        this.target = target;
        this.description = description;
        this.technology = technology;
    }

    public C4Relationship(String source,
                          String target,
                          String description,
                          String technology,
                          String tag) {
        this.source = source;
        this.target = target;
        this.description = description;
        this.technology = technology;
        this.tag=tag;
    }
}
