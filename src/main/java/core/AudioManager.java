package core;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {

    private static AudioManager instance;

    private MediaPlayer bgmPlayer;
    private final Map<String, AudioClip> sfxCache = new HashMap<>();

    private double masterVolume = 1.0;
    private double bgmVolume = 0.7;
    private double sfxVolume = 0.8;
    private boolean muted = false;

    public AudioManager() {}

    /**
     * Singleton accessor for global audio management across scenes.
     */
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    /**
     * Plays background music from the given resource path.
     */
    public void playBGM(String resourcePath, boolean loop) {
        stopBGM();

        try {
            URL resource = getClass().getResource(resourcePath);
            if (resource == null) {
                System.err.println("AudioManager: BGM file not found at " + resourcePath);
                return;
            }

            Media media = new Media(resource.toExternalForm());
            bgmPlayer = new MediaPlayer(media);

            if (loop) {
                bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            }

            updateBGMVolume();
            bgmPlayer.play();
        } catch (Exception e) {
            System.err.println("AudioManager: Failed to play BGM (" + resourcePath + "): " + e.getMessage());
        }
    }

    public void stopBGM() {
        if (bgmPlayer != null) {
            bgmPlayer.stop();
            bgmPlayer.dispose();
            bgmPlayer = null;
        }
    }

    public void pauseBGM() {
        if (bgmPlayer != null) {
            bgmPlayer.pause();
        }
    }

    public void resumeBGM() {
        if (bgmPlayer != null) {
            bgmPlayer.play();
        }
    }

    public void playSFX(String resourcePath) {
        playSFX(resourcePath, 1.0);
    }

    public void playSFX(String resourcePath, double volumeScale) {
        if (muted) return;

        AudioClip clip = sfxCache.computeIfAbsent(resourcePath, path -> {
            try {
                URL resource = getClass().getResource(path);
                if (resource == null) {
                    System.err.println("AudioManager: SFX file not found at " + path);
                    return null;
                }
                return new AudioClip(resource.toExternalForm());
            } catch (Exception e) {
                System.err.println("AudioManager: Failed to load SFX (" + path + "): " + e.getMessage());
                return null;
            }
        });

        if (clip != null) {
            double finalVolume = masterVolume * sfxVolume * Math.clamp(volumeScale, 0.0, 1.0);
            clip.play(finalVolume);
        }
    }


    public void setMasterVolume(double volume) {
        this.masterVolume = Math.clamp(volume, 0.0, 1.0);
        updateBGMVolume();
    }

    public void setBGMVolume(double volume) {
        this.bgmVolume = Math.clamp(volume, 0.0, 1.0);
        updateBGMVolume();
    }

    public void setSFXVolume(double volume) {
        this.sfxVolume = Math.clamp(volume, 0.0, 1.0);
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
        updateBGMVolume();
    }

    public void toggleMute() {
        setMuted(!muted);
    }

    private void updateBGMVolume() {
        if (bgmPlayer != null) {
            if (muted) {
                bgmPlayer.setVolume(0.0);
            } else {
                bgmPlayer.setVolume(masterVolume * bgmVolume);
            }
        }
    }

    public double getMasterVolume() { return masterVolume; }
    public double getBGMVolume() { return bgmVolume; }
    public double getSFXVolume() { return sfxVolume; }
    public boolean isMuted() { return muted; }
}