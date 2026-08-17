package ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DialogueSystem {

    private String speakerName = "";
    private String fullText = "";
    private String currentDisplayText = "";
    private Image speakerPortrait = null;
    private AudioClip textSound = null;

    private double displayTimer = 0.0;
    private double duration = 0.0;
    private boolean active = false;

    // Typewriter effect speed control
    private double charTimer = 0.0;
    private final double timePerChar = 0.025; // Speed per letter in seconds
    private int visibleCharCount = 0;

    public DialogueSystem() {
        try {
            // Optional: Load your text blip sound file here
            // textSound = new AudioClip(getClass().getResource("/sounds/text_blip.wav").toExternalForm());
        } catch (Exception e) {
            System.err.println("Text sound effect could not be loaded.");
        }
    }

    public void startDialogue(String speaker, String text, Image portrait, double durationSeconds) {
        showMessage(speaker, text, portrait, durationSeconds);
    }

    /**
     * Displays dialogue with a speaker name, text body, portrait image, and duration.
     */
    public void showMessage(String speaker, String text, Image portrait, double durationSeconds) {
        this.speakerName = speaker != null ? speaker : "";
        this.fullText = text != null ? text : "";
        this.speakerPortrait = portrait;
        this.currentDisplayText = "";
        this.duration = durationSeconds;
        this.displayTimer = 0.0;
        this.charTimer = 0.0;
        this.visibleCharCount = 0;
        this.active = true;
    }

    public void showMessage(String speaker, String text, double durationSeconds) {
        showMessage(speaker, text, null, durationSeconds);
    }

    public void update(double delta) {
        if (!active) return;

        // Typewriter Effect
        if (visibleCharCount < fullText.length()) {
            charTimer += delta;
            while (charTimer >= timePerChar && visibleCharCount < fullText.length()) {
                charTimer -= timePerChar;

                char nextChar = fullText.charAt(visibleCharCount);
                if (nextChar != ' ' && textSound != null) {
                    textSound.play(0.3); // Play at 30% volume
                }

                visibleCharCount++;
                currentDisplayText = fullText.substring(0, visibleCharCount);
            }
        } else {
            // Only count down reading duration AFTER typing finishes
            displayTimer += delta;
            if (displayTimer >= duration) {
                clear();
            }
        }
    }

    public void render(GraphicsContext gc, double panelWidth, double panelHeight) {
        if (!active) return;

        gc.save();

        double boxWidth = panelWidth * 0.65;
        double boxHeight = 100;
        double boxX = (panelWidth - boxWidth) / 2.0;
        double boxY = panelHeight - boxHeight - 35;

        gc.setFill(Color.rgb(10, 10, 25, 0.90));
        gc.setStroke(Color.web("#00ffcc"));
        gc.setLineWidth(2.0);
        gc.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 10, 10);
        gc.strokeRoundRect(boxX, boxY, boxWidth, boxHeight, 10, 10);

        double textStartX = boxX + 25;

        if (speakerPortrait != null) {
            double portraitSize = 74;
            double portraitX = boxX + 13;
            double portraitY = boxY + (boxHeight - portraitSize) / 2.0;

            gc.setFill(Color.rgb(25, 25, 45));
            gc.fillRect(portraitX, portraitY, portraitSize, portraitSize);
            gc.drawImage(speakerPortrait, portraitX, portraitY, portraitSize, portraitSize);
            gc.setStroke(Color.web("#00ffcc"));
            gc.strokeRect(portraitX, portraitY, portraitSize, portraitSize);

            textStartX = portraitX + portraitSize + 20;
        }

        if (!speakerName.isEmpty()) {
            gc.setFont(Font.font("Consolas", FontWeight.BOLD, 15));
            gc.setFill(Color.web("#00ffcc"));
            gc.fillText(speakerName.toUpperCase(), textStartX, boxY + 28);
        }

        gc.setFont(Font.font("Consolas", FontWeight.NORMAL, 14));
        gc.setFill(Color.WHITE);
        double textY = speakerName.isEmpty() ? boxY + 50 : boxY + 52;
        gc.fillText(currentDisplayText, textStartX, textY);

        gc.restore();
    }

    public void clear() {
        this.active = false;
        this.speakerName = "";
        this.fullText = "";
        this.currentDisplayText = "";
        this.speakerPortrait = null;
    }

    public boolean isActive() {
        return active;
    }

    public void setTextSound(AudioClip textSound) {
        this.textSound = textSound;
    }
}