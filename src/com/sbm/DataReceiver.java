package com.sbm;

import org.json.JSONException;

public interface DataReceiver {
    public void receive(ServerResponse serverResponse) throws JSONException;
}
