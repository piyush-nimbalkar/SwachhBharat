package com.sbm.spotfixrequest;

import static com.sbm.Global.HTTP_CREATED;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import com.sbm.DataReceiver;
import com.sbm.Global;
import com.sbm.R;
import com.sbm.ServerResponse;
import com.sbm.model.Spotfix;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SpotfixRequestActivity extends Activity implements DatePickerFragment.TheListener, TimePickerFragment.TheListener, DataReceiver, OnClickListener {
    private static final String TAG = "SPOTFIX_REQUEST_ACTIVITY";
    private static final int REQUEST_TAKE_PHOTO = 1;

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private String mCurrentPhotoPath;

    private ImageView mImageView;

    private EditText mSpotfixTitleEditText;
    private EditText mSpotfixDescEditText;
    private EditText mSpotfixEstimatedHoursEditText;
    private EditText mSpotfixEstimatedPeopleEditText;

    private TextView mSpotfixFixingDateSelecteTextView;

    private Button mSpotfixFixingTimeSelectButton;

    private File imageTaken;

//    private Spotfix spotfixToStore;
    private Context context;
    private long userID;
    private String spotfixFixingDateAndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotfix_request);

        context = this;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        userID = preferences.getLong(Global.CURRENT_USER_ID, -1);

        mSpotfixTitleEditText = (EditText) findViewById(R.id.editTextSpotfixTitle);
        mSpotfixDescEditText = (EditText) findViewById(R.id.editTextSpotfixDesc);
        mSpotfixEstimatedHoursEditText = (EditText) findViewById(R.id.editTextSpotfixEstimatedHours);
        mSpotfixEstimatedPeopleEditText = (EditText) findViewById(R.id.editTextSpotfixEstimatedPeople);
        mSpotfixFixingDateSelecteTextView = (TextView) findViewById(R.id.textViewSpotfixFixingDateSelected);

        Button mSpotfixFixingDateSelectButton = (Button) findViewById(R.id.buttonSpotfixFixingDate);
        mSpotfixFixingDateSelectButton.setOnClickListener(this);

        mSpotfixFixingTimeSelectButton = (Button) findViewById(R.id.buttonSpotfixFixingTime);
        mSpotfixFixingTimeSelectButton.setOnClickListener(this);
        mSpotfixFixingTimeSelectButton.setEnabled(false);

        Button mSpotfixFixingDateSelectedButton = (Button) findViewById(R.id.buttonSpotfixFixingDate);
        mSpotfixFixingDateSelectedButton.setOnClickListener(this);

        Button mSpotfixTakePictureButton = (Button) findViewById(R.id.buttonSpotfixTakePicture);
        mSpotfixTakePictureButton.setOnClickListener(this);

        Button mSpotfixRequestSubmitButton = (Button) findViewById(R.id.buttonSpotfixSubmitRequest);
        mSpotfixRequestSubmitButton.setOnClickListener(this);

        mImageView = (ImageView) findViewById(R.id.imageViewDisplayTakenPicture);
        mImageView.setVisibility(View.GONE);

        mAlbumStorageDirFactory = new BaseAlbumDirFactory();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSpotfixFixingDate:
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getFragmentManager(), "datePickerFragment");
                mSpotfixFixingTimeSelectButton.setEnabled(true);
                break;
            case R.id.buttonSpotfixFixingTime:
                DialogFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getFragmentManager(), "timePickerFragment");
                break;
            case R.id.buttonSpotfixTakePicture:
                dispatchTakePictureIntent(REQUEST_TAKE_PHOTO);
                break;
            case R.id.buttonSpotfixSubmitRequest:
                submitRequest();
                break;
        }
    }

    private void submitRequest() {
        SimpleDateFormat sdf = new SimpleDateFormat(Spotfix.DATE_FORMAT, Locale.US);
        Date dSelected = new Date();
        try {
            dSelected = sdf.parse(mSpotfixFixingDateSelecteTextView.getText().toString());
        } catch (ParseException aParseException) {
            // TODO Auto-generated catch block
            aParseException.printStackTrace();
        }

        String[] params = new String[9];
        params[0] = String.valueOf(userID);
        params[1] = mSpotfixTitleEditText.getText().toString();
        params[2] = mSpotfixDescEditText.getText().toString();
        params[3] = mSpotfixEstimatedHoursEditText.getText().toString();
        params[4] = mSpotfixEstimatedPeopleEditText.getText().toString();
        params[5] = "39.253701";
        params[6] = "-76.714585";
        params[7] = dSelected.toString();
        params[8] = "pending";

        for (String param : params) {
            Log.v(TAG, param);
        }

        SubmitSpotfixTask spotfixRequestSubmit = new SubmitSpotfixTask(context);
        spotfixRequestSubmit.delegate = (DataReceiver) context;
        spotfixRequestSubmit.execute(params);
    }

    @Override
    public void receive(ServerResponse response) throws JSONException {
        if (response != null) {
            if (response.getStatusCode() == HTTP_CREATED) {

                Toast.makeText(context, "Successfully submitted the request", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(context, "Submission failure: " + response.getMessage() + " please enter all the required values", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * This method creates the file where the image will be stored
     *
     * @return Returns the file object for storing the image
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        Date dNow = new Date();
        File albumDir = getAlbumDir();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
        return File.createTempFile(fmt.format(dNow), ".jpg", albumDir);
    }

    /**
     * Takes an actionCode to determine the choice in the activity results.
     * Method to send the intent to capture image
     *
     * @param actionCode Defines what action to be taken
     */
    private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file;

        try {
            file = setUpPhotoFile();
            mCurrentPhotoPath = file.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        } catch (IOException e) {
            e.printStackTrace();
            mCurrentPhotoPath = null;
        }
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
    }

    /**
     * Takes the image file and displays to the user for accepting
     *
     * @param image
     */
    public void displayImage(File image) {
        Uri path = Uri.fromFile(image);
        Intent jpgOpenintent = new Intent(Intent.ACTION_VIEW);
        jpgOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        jpgOpenintent.setDataAndType(path, "image/jpg");
        try {
            startActivity(jpgOpenintent);
        } catch (ActivityNotFoundException e) {
            //TODO
        }
    }

    /**
     * Method to add image that was taken using the camera intent into the gallery
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        imageTaken = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(imageTaken);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    /**
     * Gets the album location and creates the folder if necessary
     *
     * @return Returns reference to directory where the image will be stored
     */
    private File getAlbumDir() {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }
        } else {
            Log.d(TAG, "External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }

    /**
     * Returns Photo album for this application
     *
     * @return Returns the name of the album for the application
     */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    /**
     * Method that adds the image into the gallery
     */
    private void handleCameraPhoto() {
        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();
            //Image was taken, time to send it now
            mCurrentPhotoPath = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO)
            if (resultCode == Activity.RESULT_OK)
                handleCameraPhoto();
    }

    /**
     * Sets the image properties for the picture being taken
     */
    private void setPic() {
        /* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
        mImageView.setImageBitmap(bitmap);
        mImageView.setVisibility(View.VISIBLE);
    }

    private File setUpPhotoFile() throws IOException {
        File imageFile = createImageFile();
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.spotfix_request_options, menu);
        return true;
    }

    @Override
    public void returnDate(String date) {
        String[] parts = date.split(" ");
        spotfixFixingDateAndTime = parts[0];
        mSpotfixFixingDateSelecteTextView.setText(spotfixFixingDateAndTime);
    }

    @Override
    public void returnTime(String time) {
        String date = mSpotfixFixingDateSelecteTextView.getText().toString();
        String[] dateParts = date.split(" ");
        String[] timeParts = time.split(" ");
        spotfixFixingDateAndTime = dateParts[0]+" "+timeParts[1];
        mSpotfixFixingDateSelecteTextView.setText(spotfixFixingDateAndTime);
    }
}
