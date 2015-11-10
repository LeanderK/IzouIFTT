package leanderk.izou.iftt.actions.targetActions;

import leanderk.izou.iftt.actions.Action;
import leanderk.izou.iftt.actions.ActionFlow;
import leanderk.izou.iftt.actions.TargetAction;
import org.intellimate.izou.events.EventLifeCycle;
import org.intellimate.izou.identification.Identification;
import org.intellimate.izou.identification.IdentificationManager;
import org.intellimate.izou.sdk.Context;
import org.intellimate.izou.sdk.frameworks.music.events.StartMusicRequest;
import org.intellimate.izou.sdk.frameworks.music.player.Playlist;
import org.intellimate.izou.sdk.frameworks.music.player.TrackInfo;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * the PlayMusic Action
 * @author LeanderK
 * @version 1.0
 */
public class PlayMusic extends Action implements TargetAction, org.intellimate.izou.sdk.util.FireEvent {
    public static final String ID = PlayMusic.class.getCanonicalName();
    public static final String PARAMETER_TRACK = "track";
    public static final String PARAMETER_PLAYLIST = "playlist";
    public static final String PARAMETER_ID = "id";
    public static final String PARAMETER_Repeat = "repeat";
    private String player = null;
    private String type = null;
    private String parameter = null;
    private boolean repeat = false;

    public PlayMusic(String parameter, ActionFlow actionFlow, Context context) throws IllegalArgumentException {
        super(actionFlow, context, ID);
        String[] split = parameter.split(",");
        for (String string : split) {
            if (string.contains("=")) {
                String[] arg = string.split("=");
                if (arg.length != 2)
                    throw new IllegalArgumentException("illegal parameter: " + string);
                String command = arg[0].trim();
                String argument = arg[1].trim();
                if (command.equals(PARAMETER_ID)) {
                    player = argument;
                } else if (command.equals(PARAMETER_TRACK) || command.equals(PARAMETER_PLAYLIST)) {
                    type = command;
                    this.parameter = argument;
                }
            } else {
                String param = string.trim();
                if (param.equals(PARAMETER_Repeat)) {
                    repeat = true;
                } else {
                    throw new IllegalArgumentException("illegal parameter: " + param);
                }
            }

        }
        if (player == null || type == null)
            throw new IllegalArgumentException("to play music you have to specify the player and the track or playlist");
    }

    /**
     * gets called.
     */
    @Override
    public void execute(Consumer<Boolean> callback) {
        if (type.equals(PARAMETER_TRACK)) {
            TrackInfo trackInfo =  new TrackInfo(parameter, null, null);
            Optional<Identification> playerIdOpt = IdentificationManager.getInstance().getIdentification(player);
            if (!playerIdOpt.isPresent()) {
                error("unable to get Identification for: " + player);
                callback.accept(false);
                return;
            }
            Optional<Boolean> optional = IdentificationManager.getInstance()
                    .getIdentification(this)
                    .flatMap(id ->
                            playerIdOpt.flatMap(playerID ->
                                    StartMusicRequest.createStartMusicRequest(id, playerID, trackInfo)))
                    .map(event -> event.addEventLifeCycleListener(EventLifeCycle.APPROVED, eventLifeCycle -> callback.accept(true)))
                    .map(event -> event.addEventLifeCycleListener(EventLifeCycle.CANCELED, eventLifeCycle -> callback.accept(false)))
                    .map(this::fire);
            if (!optional.isPresent() || !optional.get()) {
                callback.accept(false);
            }
        } else if (type.equals(PARAMETER_PLAYLIST)) {
            Playlist playlist = new Playlist(new ArrayList<>(), parameter, new ArrayList<>(), 0);
            Optional<Identification> playerIdOpt = IdentificationManager.getInstance().getIdentification(player);
            if (!playerIdOpt.isPresent()) {
                error("unable to get Identification for: " + player);
                callback.accept(false);
                return;
            }
            Optional<Boolean> optional = IdentificationManager.getInstance()
                    .getIdentification(this)
                    .flatMap(id ->
                            playerIdOpt.flatMap(playerID ->
                                    StartMusicRequest.createStartMusicRequest(id, playerID, playlist)))
                    .map(event -> event.addEventLifeCycleListener(EventLifeCycle.APPROVED, eventLifeCycle -> callback.accept(true)))
                    .map(event -> event.addEventLifeCycleListener(EventLifeCycle.CANCELED, eventLifeCycle -> callback.accept(false)))
                    .map(this::fire);
            if (!optional.isPresent() || !optional.get()) {
                callback.accept(false);
            }
        }
    }
}
