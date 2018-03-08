import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SpamControl {

    private static final Map<PlayerChat, List<String>> spam = new HashMap<>();

    private void applySlowMode(PlayerChat player, int time, String reason) {
        Text msg = Text.builder("Chat timeout pour" + time + "secondes. Raison: " + reason)
                .color(TextColors.RED).build();
        player.getPlayer().sendMessage(msg);
        long epoch = 0;
        try {
            String str = new Date().toString();
            SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            Date date = df.parse(str);
            epoch = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        player.setEpoch(epoch);
        player.setTime(time);
        player.setTimedout(true);
        System.out.println("Timedout player");
        player.getPlayer().setMessageChannel(player.timeOutChannel);
    }

    private void undoSlowMode(PlayerChat player) {
        long epoch = 0;
        try {
            String str = new Date().toString();
            SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            Date date = df.parse(str);
            epoch = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (epoch - player.getEpoch() <= player.getTime()) {
            System.out.println("Untimedout player");
            player.getPlayer().setMessageChannel(player.originalChannel);
        }
        player.setTimedout(false);
        player.setTime(0);
        player.setEpoch(0);
    }

    private Map.Entry<PlayerChat, List<String>> isHere(String pseudo) {
        for (Map.Entry<PlayerChat, List<String>> lastFiveMsg : spam.entrySet()) {
            if (lastFiveMsg.getKey().getPlayer().getName().equals(pseudo))
                return lastFiveMsg;
        }
        return null;
    }

    public void playerMessagePreventSpam(MessageChannelEvent.Chat event) {
        System.out.println("Is it working");
        Optional<Player> optionalPlayer = event.getCause().first(Player.class);
        if (optionalPlayer.isPresent()) {
            String msg = event.getRawMessage().toPlainSingle();
            Map.Entry<PlayerChat, List<String>> lastMsg;
            if ((lastMsg = isHere(optionalPlayer.get().getName())) != null) {
                if (lastMsg.getKey().isTimedout()) {
                    undoSlowMode(lastMsg.getKey());
                    event.setCancelled(true);
                } else {
                    lastMsg.getValue().add(msg);
                    int size = lastMsg.getValue().size();
                    if (size >= 3) {
                        String[] lastThreeMsg = {lastMsg.getValue().get(size - 1), lastMsg.getValue().get(size - 2), lastMsg.getValue().get(size - 3)};
                        if (lastThreeMsg[0].equals(lastThreeMsg[1]) && lastThreeMsg[0].equals(lastThreeMsg[2])) {
                            applySlowMode(lastMsg.getKey(), 5, "Le spam c'est mal !");
                        }
                    }
                }
                System.out.println("Found player and took action on him");
            } else {
                List<String> firstSee = new ArrayList<String>();
                firstSee.add(msg);
                PlayerChat player = new PlayerChat(optionalPlayer.get());
                spam.put(player, firstSee);
                System.out.println("added player to list");
            }
        }
    }
}
