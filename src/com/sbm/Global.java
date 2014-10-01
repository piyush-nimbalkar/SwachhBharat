package com.sbm;

import android.content.Context;
import android.content.Intent;

/* All the constants are here
 */
public final class Global {

    static final String SENDER_ID = "83386399536";
    static final String REGISTER_URL = "http://mpss.csce.uark.edu/~devan/register.php";
    static final String LOGIN_URL = "http://swachh-bharat.herokuapp.com/login";
    static final String UPDATE_URL = "http://mpss.csce.uark.edu/~devan/update.php";
    static final String DISPLAY_MESSAGE_ACTION = "com.example.friendfinder.DISPLAY_MESSAGE";
    static final String MESSAGE = "message";
    static final String USERNAME = "username";
    static final String LATITUDE = "latitude";
    static final String LONGITUDE = "longitude";

    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(MESSAGE, message);
        context.sendBroadcast(intent);
    }

}