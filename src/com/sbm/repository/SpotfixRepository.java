package com.sbm.repository;

import android.content.Context;
import com.sbm.model.Spotfix;
import com.sbm.storage.DataStorage;

public class SpotfixRepository {

    private final Context context;
    private static DataStorage dataStorage;

    public SpotfixRepository(Context context) {
        this.context = context;
        dataStorage = new DataStorage(context);
    }

    public void createSpotfix(Spotfix spotfix) {
        dataStorage.createSpotfix(spotfix);
    }

}
