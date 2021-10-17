package io.github.Calcilore.MusicPlayer;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Language;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MusicPlayer implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "musicplayer";
    public static final String MOD_NAME = "MusicPlayer";

    public static final Random random = new Random();

    private PositionedSoundInstance sound = null;
    private boolean isRandomLoop = false;

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");

        KeyBinding playSongk       = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.musicplayer.playsong", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, "category.musicplayer.musicplayer"));
        KeyBinding stopSongk       = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.musicplayer.stopsong", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "category.musicplayer.musicplayer"));
        KeyBinding playLoopk       = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.musicplayer.playloop", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_BRACKET, "category.musicplayer.musicplayer"));
        KeyBinding playRandomLoopk = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.musicplayer.playrandomloop", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET, "category.musicplayer.musicplayer"));
        KeyBinding getSongk        = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.musicplayer.getsong", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, "category.musicplayer.musicplayer"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) { // if not in world
                isRandomLoop = false;
            }

            while (playSongk.wasPressed()) {
                isRandomLoop = false;
                playSong(getRandomSong(), false, client);
            }

            while (playLoopk.wasPressed()) {
                isRandomLoop = true;
                playSong(getRandomSong(), true, client);
            }

            while (playRandomLoopk.wasPressed() || (!client.getSoundManager().isPlaying(sound) && isRandomLoop)) {
                isRandomLoop = true;
                playSong(getRandomSong(), false, client);
            }

            while (stopSongk.wasPressed()) {
                if (sound != null)
                    client.getSoundManager().stop(sound);

                sound = null;
                isRandomLoop = false;
            }

            while (getSongk.wasPressed()) {
                tellSong(client);
            }
        });
    }

    public SoundEvent getRandomSong() {
        return songs[random.nextInt(songs.length)];
    }

    public void playSong(SoundEvent soundEvent, boolean loop, MinecraftClient client) {
        if (soundEvent != null) {
            if (sound != null)
                client.getSoundManager().stop(sound);

            sound = new PositionedSoundInstance(soundEvent.getId(), SoundCategory.RECORDS, 0.25f, 1f, loop, 0, SoundInstance.AttenuationType.NONE, 0.0D, 0.0D, 0.0D, true);

            client.getSoundManager().play(sound);

            tellSong(client);
        }
    }

    public void tellSong(MinecraftClient client) {

        Language lan = TranslationStorage.getInstance();
        String soundString;

        if (sound == null) {
            soundString = lan.get("musicplayer.nothing");
        } else {

            // convert "minecraft:music_disk.<disc>" to "item.minecraft.music_disc_<disc>.desc"
            soundString = String.valueOf(sound.getId());
            soundString = soundString.substring(soundString.lastIndexOf(".") + 1);
            //soundString = soundString.substring(0,1).toUpperCase() + soundString.substring(1).toLowerCase(); // No language
            soundString = lan.get("item.minecraft.music_disc_" + soundString + ".desc"); // With language
        }

        client.inGameHud.setOverlayMessage(Text.Serializer.fromJson("[\"\",{\"text\":\"" + lan.get("musicplayer.playingsong") + "\",\"color\":\"green\"},{\"text\":\"" + soundString + "\",\"bold\":true,\"color\":\"gold\"}]"), false);
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }

    private static final SoundEvent[] songs = new SoundEvent[] {
        //SoundEvents.MUSIC_DISC_11,
        //SoundEvents.MUSIC_DISC_13,
        SoundEvents.MUSIC_DISC_BLOCKS,
        SoundEvents.MUSIC_DISC_CAT,
        SoundEvents.MUSIC_DISC_CHIRP,
        SoundEvents.MUSIC_DISC_FAR,
        SoundEvents.MUSIC_DISC_MALL,
        SoundEvents.MUSIC_DISC_MELLOHI,
        SoundEvents.MUSIC_DISC_PIGSTEP,
        SoundEvents.MUSIC_DISC_STAL,
        SoundEvents.MUSIC_DISC_STRAD,
        SoundEvents.MUSIC_DISC_WAIT,
        SoundEvents.MUSIC_DISC_WARD
    };
}