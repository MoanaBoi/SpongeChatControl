import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.text.Text;

public class CmdReport implements CommandExecutor {

    public CmdReport() {}

    public static CommandSpec cmdReport = CommandSpec.builder()
            .description(Text.of("Report command"))
            .permission("chatcontrol.report")
            .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("playerName"))))
            .executor(new CmdReport())
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        System.out.println("reported player " + args.<String>getOne("playerName"));
        Inventory reportUi = Inventory.builder()
                .of(InventoryArchetypes.ANVIL);
        return CommandResult.success();
    }
}
