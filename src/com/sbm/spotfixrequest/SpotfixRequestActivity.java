package com.sbm.spotfixrequest;

import static com.sbm.Global.HTTP_CREATED;
import static com.sbm.Global.HTTP_SUCCESS;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import com.sbm.DataReceiver;
import com.sbm.Global;
import com.sbm.R;
import com.sbm.ServerResponse;
import com.sbm.model.Spotfix;
import com.sbm.model.SpotfixBuilder;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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

public class SpotfixRequestActivity extends Activity implements DatePickerFragment.TheListener, DataReceiver {
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
	
	private Button mSpotfixFixingDateSelectedButton;
	private Button mSpotfixTakePictureButton;
	private Button mSpotfixRequestSubmitButton;

	private File imageTaken;

	private Spotfix spotfixToStore;
	private Context context;
    private SharedPreferences preferences;
    private long userID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spotfix_request);
		
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        userID = preferences.getLong(Global.CURRENT_USER_ID, -1);
		
		mSpotfixTitleEditText = (EditText) findViewById(R.id.editTextSpotfixTitle); 
		mSpotfixDescEditText = (EditText) findViewById(R.id.editTextSpotfixDesc); 
		mSpotfixEstimatedHoursEditText = (EditText) findViewById(R.id.editTextSpotfixEstimatedHours); 
		mSpotfixEstimatedPeopleEditText = (EditText) findViewById(R.id.editTextSpotfixEstimatedPeople); 
		mSpotfixFixingDateSelecteTextView = (TextView) findViewById(R.id.textViewSpotfixFixingDateSelected);
		
		mSpotfixFixingDateSelectedButton = (Button) findViewById(R.id.buttonSpotfixFixingDate);
		mSpotfixTakePictureButton = (Button) findViewById(R.id.buttonSpotfixTakePicture);
		mSpotfixRequestSubmitButton = (Button) findViewById(R.id.buttonSpotfixSubmitRequest);
		
		mImageView = (ImageView) findViewById(R.id.imageViewDisplayTakenPicture);
		mImageView.setVisibility(View.GONE);
		
		mAlbumStorageDirFactory = new BaseAlbumDirFactory();

		addOnClickListener();
    }

	private void addOnClickListener() {
		mSpotfixFixingDateSelectedButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogFragment picker = new DatePickerFragment();
                picker.show(getFragmentManager(), "datePicker");
			}
		});
		
		mSpotfixTakePictureButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dispatchTakePictureIntent(REQUEST_TAKE_PHOTO);
			}
		});
		
		mSpotfixRequestSubmitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                submitRequest();
            }
        });
	}

	private String[] submitRequest() {
        SimpleDateFormat sdf = new SimpleDateFormat(Spotfix.DATE_FORMAT);
        Date dSelected = new Date();
        try {
            dSelected = sdf.parse(mSpotfixFixingDateSelecteTextView.getText().toString());
        } catch (ParseException aParseException) {
            // TODO Auto-generated catch block
            aParseException.printStackTrace();
        }

		String[] params = new String[9];

//		try {
//			spotfixToStore = SpotfixBuilder.spotfix()
//			        .setOwnerId(userID)
//			        .setTitle(mSpotfixTitleEditText.getText().toString())
//			        .setDescription(mSpotfixDescEditText.getText().toString())
//			        .setEstimatedHours(Long.parseLong(mSpotfixEstimatedHoursEditText.getText().toString()))
//			        .setEstimatedPeople(Long.parseLong(mSpotfixEstimatedPeopleEditText.getText().toString()))
//                    .setStatus("pending")
//			        //Ebiquity Lab LAT LONG approx
//			        .setLatitude(39.253701)
//			        .setLongitude(-76.714585)
//			        .setFixDate(dSelected).build();
//		} catch (NumberFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		spotfixToStore.valid();
		
		params[0] = String.valueOf(userID);
		params[1] = mSpotfixTitleEditText.getText().toString();
		params[2] = mSpotfixDescEditText.getText().toString();
		params[3] = mSpotfixEstimatedHoursEditText.getText().toString();
		params[4] = mSpotfixEstimatedPeopleEditText.getText().toString();
        //Ebiquity Lab LAT LONG approx
		params[5] = "39.253701";
		params[6] = "-76.714585";
		params[7] = dSelected.toString();
        params[8] = "pending";

        for(String param : params) {
            Log.v(TAG, param);
        }

		SpotfixRequestSubmit spotfixRequestSubmit = new SpotfixRequestSubmit(context);
		spotfixRequestSubmit.delegate = (DataReceiver) context;
		spotfixRequestSubmit.execute(params);

        String[] dummy = new String[1];
		return dummy;
	}

    @Override
    public void receive(ServerResponse response) throws JSONException {
        if (response != null) {
            if (response.getStatusCode() == HTTP_CREATED) {
                Toast.makeText(context, "Successfully submitted the request", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(context, "Submission failure: "+response.getMessage()+" please enter all the required values", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private static class SpotfixRequestSubmit extends AsyncTask<String, Integer, ServerResponse> {
        private final Context spotfixRequestSubmitContext;
        private ProgressDialog dialog;
        public DataReceiver delegate;

        public SpotfixRequestSubmit(Context context) {
        	spotfixRequestSubmitContext = context;
            dialog = new ProgressDialog(spotfixRequestSubmitContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Submitting request...");
            this.dialog.show();
        }

        @Override
        protected ServerResponse doInBackground(String... params) {
            ServerResponse serverResponse = null;

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(Global.CREATE_SPOTFIXES_URL);

            List<NameValuePair> value = new LinkedList<NameValuePair>();
            value.add(new BasicNameValuePair(Global.SPOTFIX_OWNER_ID, params[0]));
            value.add(new BasicNameValuePair(Global.SPOTFIX_TITLE, params[1]));
            value.add(new BasicNameValuePair(Global.SPOTFIX_DESC, params[2]));
            value.add(new BasicNameValuePair(Global.SPOTFIX_ESTIMATED_HOURS, params[3]));
            value.add(new BasicNameValuePair(Global.SPOTFIX_ESTIMATED_PEOPLE, params[4]));
            value.add(new BasicNameValuePair(Global.SPOTFIX_LAT, params[5]));
            value.add(new BasicNameValuePair(Global.SPOTFIX_LONG, params[6]));
            value.add(new BasicNameValuePair(Global.SPOTFIX_FIX_DATE, params[7]));
            value.add(new BasicNameValuePair(Global.SPOTFIX_STATUS, params[8]));

            try {
                post.setEntity(new UrlEncodedFormEntity(value));
//                post.setHeader("Content-type: application/JSON", "");
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                HttpResponse httpResponse = client.execute(post);
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                String responseString = "";
                String temp;
                while((temp = reader.readLine()) != null)
                    responseString += temp;

//                Log.d("----------", String.valueOf(post.getParams()));
                Log.d("----------", responseString);
//                Log.d("----------", String.valueOf(httpResponse.getStatusLine().getStatusCode()));
                serverResponse = new ServerResponse(httpResponse.getStatusLine().getStatusCode(), responseString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return serverResponse;
        }

        @Override
        protected void onPostExecute(ServerResponse response) {
            super.onPostExecute(response);
            try {
                delegate.receive(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (dialog.isShowing())
                dialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

    }

    /**
	 * This method creates the file where the image will be stored
	 * @return Returns the file object for storing the image
	 * @throws IOException
	 */
	private File createImageFile() throws IOException {
		Date dNow = new Date();
		SimpleDateFormat fmt = new SimpleDateFormat ("yyyyMMdd_HHmmss", Locale.US);
		String timeStamp = fmt.format(dNow);
		String imageFileName = timeStamp;
		File albumF = getAlbumDir();
		//Creates a jpeg file
		File imageF = File.createTempFile(imageFileName, ".jpg", albumF);
		return imageF;
	}
	
	/**
	 * Takes an actionCode to determine the choice in the activity results. 
	 * Method to send the intent to capture image
	 * @param actionCode
	 */
	private void dispatchTakePictureIntent(int actionCode) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File f = null;
			
			try {
				f = setUpPhotoFile();
				mCurrentPhotoPath = f.getAbsolutePath();
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			} catch (IOException e) {
				e.printStackTrace();
				f = null;
				mCurrentPhotoPath = null;
			}
		startActivityForResult(takePictureIntent,1);
	}
	
	/**
	 * Takes the image file and displays to the user for accepting 
	 * @param image
	 */
	public void displayImage(File image){
       Uri path = Uri.fromFile(image);
       Intent jpgOpenintent = new Intent(Intent.ACTION_VIEW);
       jpgOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       jpgOpenintent.setDataAndType(path, "image/jpg");
       try {
           startActivity(jpgOpenintent);
       }
       catch (ActivityNotFoundException e) {
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
	 * @return Returns reference to directory where the image will be stored
	 */
	private File getAlbumDir() {
		File storageDir = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
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
	 *  Returns Photo album for this application
	 *  @return Returns the name of the album for the application
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
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
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
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		return f;
	}
	
    public static String getTag() {
		return TAG;
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.spotfix_request_options, menu);
        return true;
    }

	@Override
	public void returnDate(String date) {
		mSpotfixFixingDateSelecteTextView.setText(date);		
	}
}