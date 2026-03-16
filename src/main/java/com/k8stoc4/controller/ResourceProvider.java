package com.k8stoc4.controller;

import io.fabric8.kubernetes.api.model.HasMetadata;

import java.util.List;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface ResourceProvider {
    List<HasMetadata> resources();
}
