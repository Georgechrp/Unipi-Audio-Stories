package com.unipi.george.unipiaudiostories;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

public class PreferencesManager {

    private static final String PREFERENCES_NAME = "MyStories";

    public static void saveStory(Context context, String documentId, String imageUrl, String text, String title, String author, String year) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String storyData = String.format("%s|%s|%s|%s|%s", imageUrl, text, title, author, year);
        editor.putString(documentId, storyData);
        editor.apply();
        Toast.makeText(context, "Story saved with ID: " + documentId, Toast.LENGTH_SHORT).show();
    }

    public static String[] loadStory(Context context, String documentId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String storyData = sharedPreferences.getString(documentId, null);

        if (storyData != null) {
            return storyData.split("\\|"); // Επιστροφή πίνακα με τα δεδομένα
        }
        return null;
    }

    public static Set<String> getAllDocumentIds(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet("documentIds", new HashSet<>());
    }

    public static void addDocumentId(Context context, String documentId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Set<String> documentIds = sharedPreferences.getStringSet("documentIds", new HashSet<>());
        documentIds.add(documentId);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("documentIds", documentIds);
        editor.apply();
    }
}
