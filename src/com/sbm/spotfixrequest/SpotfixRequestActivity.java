package com.sbm.spotfixrequest;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.sbm.R;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class SpotfixRequestActivity extends Activity implements DatePickerFragment.TheListener {
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spotfix_request);
		
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
				verifyInput();
				submitRequest();
			}
		});
	}

	private void submitRequest() {
		// TODO Auto-generated method stub
		
	}

	private boolean verifyInput() {
		if(mSpotfixTitleEditText.getText().length()==0)
			return false;
		if(mSpotfixDescEditText.getText().length()==0)
			return false;
		if(mSpotfixEstimatedHoursEditText.getText().length()==0)
			return false;
		if(mSpotfixEstimatedPeopleEditText.getText().length()==0)
			return false;
		return true;
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
	
/**
    public int uploadFile(String sourceFileUri) {
        HttpURLConnection conn = null;
        DataOutputStream dos = null;  
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024; 
        File sourceFile = new File(sourceFileUri); 
         
        if (!sourceFile.isFile()) {
             
             dialog.dismiss(); 
              
             Log.e("uploadFile", "Source File not exist :"
                                 +uploadFilePath + "" + uploadFileName);
              
             runOnUiThread(new Runnable() {
                 public void run() {
                	 Toast.makeText(TakePictureActivity.this, "Source File not exist :"
                             +uploadFilePath + "" + uploadFileName, Toast.LENGTH_SHORT).show();
                 }
             }); 
              
             return 0;
          
        }
        else
        {
             try { 
                  
                   // open a URL connection to the Servlet
                 FileInputStream fileInputStream = new FileInputStream(sourceFile);
                 URL url = new URL(upLoadServerUri);
                  
                 // Open a HTTP  connection to  the URL
                 conn = (HttpURLConnection) url.openConnection(); 
                 conn.setDoInput(true); // Allow Inputs
                 conn.setDoOutput(true); // Allow Outputs
                 conn.setUseCaches(false); // Don't use a Cached Copy
                 conn.setRequestMethod("POST");
                 conn.setRequestProperty("Connection", "Keep-Alive");
                 conn.setRequestProperty("Connection", "close"); 
                 conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                 conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                 conn.setRequestProperty("uploaded_file", fileName); 
                  
                 dos = new DataOutputStream(conn.getOutputStream());
        
                 dos.writeBytes(twoHyphens + boundary + lineEnd); 
                 dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
                  
                 dos.writeBytes(lineEnd);
        
                 // create a buffer of  maximum size
                 bytesAvailable = fileInputStream.available(); 
        
                 bufferSize = Math.min(bytesAvailable, maxBufferSize);
                 buffer = new byte[bufferSize];
        
                 // read file and write it into form...
                 bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
                    
                 while (bytesRead > 0) {
                      
                   dos.write(buffer, 0, bufferSize);
                   bytesAvailable = fileInputStream.available();
                   bufferSize = Math.min(bytesAvailable, maxBufferSize);
                   bytesRead = fileInputStream.read(buffer, 0, bufferSize);   
                    
                  }
        
                 // send multipart form data necesssary after file data...
                 dos.writeBytes(lineEnd);
                 dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
        
                 // Responses from the server (code and message)
                 serverResponseCode = conn.getResponseCode();
                 String serverResponseMessage = conn.getResponseMessage();
                   
                 Log.i("uploadFile", "HTTP Response is : "
                         + serverResponseMessage + ": " + serverResponseCode);
                  
                 if(serverResponseCode == 200){
                      
                     runOnUiThread(new Runnable() {
                          public void run() {
                               
                              String msg = "File Upload Completed.\n\n See uploaded file here : \n\n";
                             fileNameOnServer = "http://www.csee.umbc.edu/~prajit1/Cyclops/uploads/"+uploadFileName;  
                         	 Toast.makeText(TakePictureActivity.this, msg+fileNameOnServer, 
                                           Toast.LENGTH_SHORT).show();
                          }
                      });                
                 }    
                  
                 //close the streams //
                 fileInputStream.close();
                 dos.flush();
                 dos.close();
                   
            } catch (MalformedURLException ex) {
                 
                dialog.dismiss();  
                ex.printStackTrace();
                 
                runOnUiThread(new Runnable() {
                    public void run() {
                    	Toast.makeText(TakePictureActivity.this, "MalformedURLException", 
                                                            Toast.LENGTH_SHORT).show();
                    }
                });
                 
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
            } catch (Exception e) {
                 
                dialog.dismiss();  
                e.printStackTrace();
                 
                runOnUiThread(new Runnable() {
                    public void run() {
                    	Toast.makeText(TakePictureActivity.this, "Got Exception : see logcat ", 
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server Exception", "Exception : "
                                                 + e.getMessage(), e);  
            }
            dialog.dismiss();       
            return serverResponseCode; 
             
         } // End else block 
    } 
 */

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