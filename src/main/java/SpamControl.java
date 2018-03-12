import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.message.MessageChannelEvent;

import java.util.*;

public class SpamControl {

    private static final Map<PlayerChat, List<String>> spam = new HashMap<>();

    private Map.Entry<PlayerChat, List<String>> isHere(String pseudo) {
        for (Map.Entry<PlayerChat, List<String>> lastFiveMsg : spam.entrySet()) {
            if (lastFiveMsg.getKey().getPlayer().getName().equals(pseudo))
                return lastFiveMsg;
        }
        return null;
    }

    public void playerMessagePreventSpam(MessageChannelEvent.Chat event) {
        Optional<Player> optionalPlayer = event.getCause().first(Player.class);
        if (optionalPlayer.isPresent()) {
            String msg = event.getRawMessage().toPlainSingle();
            Map.Entry<PlayerChat, List<String>> lastMsg;
            if ((lastMsg = isHere(optionalPlayer.get().getName())) != null) {
                if (lastMsg.getKey().isTimedout()) {
                    if (lastMsg.getKey().undoSlowMode())
                        event.setCancelled(true);
                } else {
                    lastMsg.getValue().add(msg);
                    int size = lastMsg.getValue().size();
                    if (size >= 3) {
                        String[] lastThreeMsg = {lastMsg.getValue().get(size - 1), lastMsg.getValue().get(size - 2), lastMsg.getValue().get(size - 3)};
                        if (lastThreeMsg[0].equals(lastThreeMsg[1]) && lastThreeMsg[0].equals(lastThreeMsg[2])) {
                            lastMsg.getKey().applySlowMode(5, "Le spam c'est mal !");
                            event.setCancelled(true);
                        }
                    }
                }
            } else {
                List<String> firstSee = new ArrayList<String>();
                firstSee.add(msg);
                PlayerChat player = new PlayerChat(optionalPlayer.get());
                spam.put(player, firstSee);
            }
        }
    }
}
