package com.unipi.george.unipiaudiostories;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.Locale;

public class MyTts {
    private TextToSpeech textToSpeech;
    private boolean isInitialized = false;

    public interface CompletionListener {
        void onComplete();
    }

    private CompletionListener completionListener;

    public MyTts(Context context) {
        textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
                isInitialized = true; // Σημειώνουμε ότι η αρχικοποίηση ολοκληρώθηκε
                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        // Ανάγνωση ξεκίνησε
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        // Ανάγνωση ολοκληρώθηκε
                        if (completionListener != null) {
                            completionListener.onComplete();
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {
                        // Σφάλμα κατά την ανάγνωση
                    }
                });
            }
        });
    }

    public void speak(String message) {
        if (isInitialized) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null, "UniqueUtteranceId");
        } else {
            throw new IllegalStateException("TextToSpeech is not initialized yet.");
        }
    }

    public void stopSpeaking() {
        if (isInitialized) {
            textToSpeech.stop();
        }
    }

    public boolean isSpeaking() {
        return isInitialized && textToSpeech.isSpeaking();
    }

    public void setCompletionListener(CompletionListener listener) {
        this.completionListener = listener;
    }

    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
    }
}
