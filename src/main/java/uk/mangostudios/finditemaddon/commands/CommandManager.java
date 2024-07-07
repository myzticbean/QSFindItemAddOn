package uk.mangostudios.finditemaddon.commands;

import io.leangen.geantyref.TypeToken;
import uk.mangostudios.finditemaddon.commands.impl.AbstractCommand;
import uk.mangostudios.finditemaddon.FindItemAddOn;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.setting.ManagerSetting;

import java.lang.reflect.InvocationTargetException;

public class CommandManager {

    private final LegacyPaperCommandManager<CommandSender> commandManager = new LegacyPaperCommandManager<>(
            FindItemAddOn.getInstance(),
            ExecutionCoordinator.asyncCoordinator(),
            SenderMapper.identity()
    );
    private final AnnotationParser<CommandSender> annotationParser;

    public CommandManager() {
        commandManager.settings().set(ManagerSetting.OVERRIDE_EXISTING_COMMANDS, true);
        commandManager.settings().set(ManagerSetting.ALLOW_UNSAFE_REGISTRATION, true);

        annotationParser = new AnnotationParser<>(
                commandManager,
                CommandSender.class
        );
    }

    public CommandManager registerCommands(Class<? extends AbstractCommand>... commands) {
        for (Class<? extends AbstractCommand> command : commands) {
            FindItemAddOn.getInstance().getLogger().info("Registering command: " + command.getSimpleName());
            try {
                annotationParser.parse(command.getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                FindItemAddOn.getInstance().getLogger().info("Failed to register command: " + command.getSimpleName());
                e.printStackTrace();
            }
        }
        return this;
    }

    public CommandManager registerCommand(AbstractCommand... abstractCommands) {
        for (AbstractCommand command : abstractCommands) {
            FindItemAddOn.getInstance().getLogger().info("Registering command: " + command.getClass().getSimpleName());
            annotationParser.parse(command);
        }
        return this;
    }

    public <T> CommandManager withArgumentType(Class<T> clazz, ArgumentParser<CommandSender, T> parser) {
        commandManager.parserRegistry().registerParserSupplier(TypeToken.get(clazz), params -> parser);
        return this;
    }
}
