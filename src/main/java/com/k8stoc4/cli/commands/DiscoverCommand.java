package com.k8stoc4.cli.commands;

import com.k8stoc4.controller.RenderOutputWriter;
import com.k8stoc4.controller.DiscoverController;
import com.k8stoc4.controller.writer.FileWriter;
import com.k8stoc4.controller.writer.SystemOutWriter;
import com.k8stoc4.render.C4DslRenderer;
import picocli.CommandLine;

import java.util.Optional;

@CommandLine.Command(
        name = "discover",
        description = "discover the cluster status"
)
public class DiscoverCommand implements Runnable {

    @CommandLine.Option(
            names = {"-o", "--output"},
            description = "output dir",
            required = false
    )
    private Optional<String> output;

    @CommandLine.Option(
            names = {"-n", "--namespace"},
            description = "force namespace",
            required = false
    )
    private Optional<String> defaultNs;

    @CommandLine.Option(
            names = {"-g","--group-by-label"},
            description = "label key for grouping (e.g. app.kubernetes.io/name, app)",
            required = false
    )
    private Optional<String> groupByLabel;

    @Override
    public void run() {
        final C4DslRenderer.Output renderOutput = new DiscoverController(defaultNs, groupByLabel).execute();
        final RenderOutputWriter writer = output.isPresent() ? new FileWriter(output.get()) : new SystemOutWriter();
        writer.write(renderOutput);
    }
}
