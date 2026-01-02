package cli;

import cli.commands.ParseCommand;
import picocli.CommandLine;

@CommandLine.Command(
        name = "k8stoc4",
        mixinStandardHelpOptions = true,
        version = "1.0",
        description = "Esempio semplice con Picocli",
        subcommands = {ParseCommand.class}
)
public class Main implements Runnable {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        System.out.println("Usa un sottocomando: hello | sum");
    }
}
