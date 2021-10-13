package io.github.Calcilore.MusicPlayer;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
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

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");

        KeyBinding playSong = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.musicplayer.playsong", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, "category.musicplayer.musicplayer"));
        KeyBinding stopSong = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.musicplayer.stopsong", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "category.musicplayer.musicplayer"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (playSong.wasPressed()) {
                SoundEvent soundEvent = songs[random.nextInt(songs.length)];
                if (soundEvent != null) {
                    if (sound != null)
                        client.getSoundManager().stop(sound);

                    sound = new PositionedSoundInstance(soundEvent.getId(), SoundCategory.RECORDS, 0.25f, 1f, false, 0, SoundInstance.AttenuationType.NONE, 0.0D, 0.0D, 0.0D, true);

                    client.getSoundManager().play(sound);

                    String soundString = String.valueOf(sound.getId());
                    soundString = soundString.substring(soundString.lastIndexOf(".") + 1);
                    soundString = soundString.substring(0,1).toUpperCase() + soundString.substring(1).toLowerCase();

                    client.inGameHud.setOverlayMessage(Text.Serializer.fromJson("[\"\",{\"text\":\"Playing Song: \",\"color\":\"green\"},{\"text\":\"" + soundString + "\",\"bold\":true,\"color\":\"gold\"},{\"text\":\"!\",\"bold\":true,\"color\":\"green\"}]"), false);
                }
            }

            while (stopSong.wasPressed()) {
                if (sound != null)
                    client.getSoundManager().stop(sound);
            }
        });
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }

    private static final SoundEvent[] songs = new SoundEvent[] {
        SoundEvents.MUSIC_DISC_11,
        SoundEvents.MUSIC_DISC_13,
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