package com.sbm.repository;

import android.content.Context;
import android.util.Log;
import com.sbm.model.Spotfix;
import com.sbm.storage.DataStorage;

import java.text.ParseException;
import java.util.ArrayList;

public class SpotfixRepository {
    private static final String TAG = "SPOTFIX_REPOSITORY";

    private final Context context;
    private static DataStorage dataStorage;

    public SpotfixRepository(Context context) {
        this.context = context;
        dataStorage = new DataStorage(context);
    }

    public void createSpotfix(Spotfix spotfix) {
        dataStorage.createSpotfix(spotfix);
    }

    public void createSpotfixes(ArrayList<Spotfix> spotfixes) {
        deleteSpotfixes();
        for (Spotfix spotfix : spotfixes)
            createSpotfix(spotfix);
    }

    private void deleteSpotfixes() {
        dataStorage.deleteSpotfixes();
    }

    public ArrayList<Spotfix> getSpotfixes() {
        ArrayList<Spotfix> spotfixes = new ArrayList<Spotfix>();
        try {
            spotfixes = dataStorage.getSpotfixes();
        } catch (ParseException e) {
            Log.d(TAG, "A parse error occurred while loading records from the database.");
            e.printStackTrace();
        }
        return spotfixes;
    }

}
