import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
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
        Player player = (Player) src;
        if (args.<String>getOne("playerName").isPresent()) {
            String playerReportedName = args.<String>getOne("playerName").get();
            System.out.println("reported player " + playerReportedName);

            ItemStack greenWool = ItemStack.builder()
                    .itemType(ItemTypes.WOOL)
                    .quantity(1)
                    .build();
            Inventory report = Inventory.builder()
                    .of(InventoryArchetypes.ANVIL)
                    .build(Sponge.getPluginManager().getPlugin("spongechatcontrol").get().getInstance().get());
            player.openInventory(report);
            report.set(greenWool);
        }
        return CommandResult.success();
    }
}
