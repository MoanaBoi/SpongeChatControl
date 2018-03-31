import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CmdReportList implements CommandExecutor {

    public CmdReportList() {

    }

    public static CommandSpec cmdReportList = CommandSpec.builder()
            .description(Text.of("Get a list of all the reports"))
            .permission("chatcontrol.report.list")
            .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("playerName"))))
            .executor(new CmdReportList())
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player) src;
        if (args.<String>getOne("playerName").isPresent()) {
            String playerReportedName = args.<String>getOne("playerName").get();
            for (String reasons : CmdReport.reportList.get(playerReportedName)) {
                String good = reasons.replace("-", ", ");
                Text msg = Text.builder(good).color(TextColors.AQUA).build();
                player.sendMessage(msg);
            }
        }
        return CommandResult.success();
    }
}
