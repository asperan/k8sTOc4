# k8sToC4 CLI

CLI tool to convert Kubernetes manifests into C4 diagrams using the LikeC4 DSL.

Overview
- The tool reads Kubernetes manifests (Deployment, Service, Ingress, ConfigMap, Secret, etc.), builds a C4 model, and outputs Structurizr DSL files (.c4).
- Architecture is designed to be extensible via a Visitor pattern to support new Kubernetes resources.
- Automatically maps resources to C4 components and establishes relationships such as:
  - Service -> Pods (via selector)
  - Ingress -> Service (HTTP routes)
  - Pods -> ConfigMap (envFrom, valueFrom, volumes)
  - Pods -> Secret (envFrom, valueFrom, volumes)
  - Pods -> PersistentVolumeClaim (volumes)
- Outputs DSL files suitable for Structurizr visualization.

Supported Kubernetes resources
- Deployment
- StatefulSet
- Service
- Ingress (v1, v1beta1, extensions)
- ConfigMap
- Secret
- PersistentVolumeClaim
- Other generic resources (fallback)

Prerequisites
- Java 21 or newer
- Maven 3.x

Build
- mvn -B -DskipTests=false package
- The resulting executable JAR is generated under target/, e.g. target/k8stoc4-cli-1.0-SNAPSHOT.jar

Usage
- Base command: java -jar target/k8stoc4-cli-1.0-SNAPSHOT.jar parse -i <input-file.yaml>
- Options:
  * -i, --input: Kubernetes YAML input file (required)
  * -o, --output: Output directory for .c4 files (optional)

Examples
- Output to stdout:
  java -jar target/k8stoc4-cli-1.0-SNAPSHOT.jar parse -i src/main/resources/microservice.yaml
- Output to file:
  java -jar target/k8stoc4-cli-1.0-SNAPSHOT.jar parse -i src/main/resources/complex.yaml -o ./output

This will generate:
- output/spec.c4: C4 specifications
- output/model.c4: C4 model with namespace, components, and relations

Architecture (high level)
- The project follows a modular structure under src/main/java:
  - com.k8stoc4.cli: Entry point and commands (Main.java, ParseCommand.java)
  - com.k8stoc4.model: C4Model, C4Namespace, C4Component, C4Relationship, Constants
  - com.k8stoc4.visitor: Visitors to build the model (C4ModelBuilderVisitor, KubernetesResourceVisitor, etc.)
  - com.k8stoc4.render: C4DslRenderer for Structurizr DSL output
- Core ideas: Parser (Fabric8 Kubernetes Client), DSL template rendering (Mustache), and a Visitor-based model builder.

Dependencies
- Picocli 4.7.7: CLI framework
- Fabric8 Kubernetes Client 7.4.0: Kubernetes resource parsing
- Mustache 0.9.10: Template engine for DSL
- Lombok 1.18.42: Boilerplate reduction
- Logback 1.5.20: Logging

Notes
- The CLI jar is produced by the Maven Shade plugin; entry point is com.k8stoc4.cli.Main.
- Version in this README refers to the current release artifact name: k8stoc4-cli-1.0-SNAPSHOT.jar

Contributing
- See CONTRIBUTING.md for details on forking, branching, testing, and submitting PRs.

License
- Apache License 2.0. See LICENSE for details.
