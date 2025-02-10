package com.unipi.george.unipiaudiostories.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.Locale;

/**
 * Κλάση για τη μηχανή TextToSpeech του Android.
 * Αυτή η κλάση χειρίζεται την αρχικοποίηση, τη σύνθεση φωνής και τις ειδοποιήσεις ολοκλήρωσης
 */
public class MyTts {
    private TextToSpeech textToSpeech; // για σύνθεση φωνής
    private boolean isInitialized = false; // έχει ολοκληρωθεί η αρχικοποίηση του TextToSpeech;

    // Διεπαφή για την ειδοποίηση όταν ολοκληρώνεται η σύνθεση φωνής
    public interface CompletionListener {
        void onComplete();
    }

    private CompletionListener completionListener;

    /**
     * Κατασκευαστής της κλάσης MyTts.
     * Αρχικοποιεί τη μηχανή TextToSpeech και ορίζει ακροατή για την παρακολούθηση της κατάστασής της.
     *
     * @param context Το context της εφαρμογής που χρησιμοποιείται για την αρχικοποίηση του TextToSpeech.
     */
    public MyTts(Context context) {
        textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                // Ορίζει τη γλώσσα του TextToSpeech στα Αγγλικά
                textToSpeech.setLanguage(Locale.ENGLISH);
                isInitialized = true;

                // Ορίζει ακροατή για την παρακολούθηση της προόδου των φράσεων
                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        // Κλήση όταν ξεκινά η σύνθεση φωνής
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        // Κλήση όταν ολοκληρώνεται η σύνθεση φωνής
                        if (completionListener != null) {
                            completionListener.onComplete(); // Ειδοποιεί τον ακροατή
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {
                        // Κλήση όταν σφάλμα κατά τη σύνθεση φωνής
                    }
                });
            }
        });
    }

    /**
     * Αναπαράγει το μήνυμα που δίνεται μέσω του TextToSpeech.
     *
     * @param message Το κείμενο που θα αναπαραχθεί.
     * @throws IllegalStateException αν το TextToSpeech δεν έχει ακόμη αρχικοποιηθεί.
     */
    public void speak(String message) {
        if (isInitialized) {
            // Προσθέτει το μήνυμα στην ουρά ομιλίας με μοναδικό ID φράσης
            textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null, "UniqueUtteranceId");
        } else {
            throw new IllegalStateException("TextToSpeech is not initialized yet.");
        }
    }


    public void stopSpeaking() {
        if (isInitialized) {
            textToSpeech.stop(); // Διακόπτει την τρέχουσα αναπαραγωγή
        }
    }



     //True αν αναπαράγει, false διαφορετικά.
    public boolean isSpeaking() {
        return isInitialized && textToSpeech.isSpeaking();
    }


    public void setCompletionListener(CompletionListener listener) {
        this.completionListener = listener;
    }

    /**
     * Αποδεσμεύει τους πόρους που σχετίζονται με τη μηχανή TextToSpeech
     * Πρέπει να καλείται όταν η κλάση δεν χρειάζεται πλέον για την αποφυγή διαρροών μνήμης
     */
    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.shutdown(); // Τερματίζει σωστά τη μηχανή TextToSpeech
        }
    }
}
