package com.sbm.spotfixrequest;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.sbm.swachh_bharat.R;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SpotfixRequestActivity extends Activity {
	// Intent request codes
	private static final int REQUEST_TAKE_PHOTO = 1;
	
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	private String mCurrentPhotoPath;

	private ImageView mImageView;
	private Button mTakePictureButton;
	private Button mSendImageAndPolicyButton;
	private ToggleButton mTogglePolicyButton;
	
	private boolean policyToAllowPictureTaking;
	private File imageTaken;
	
    private int serverResponseCode = 0;
    private ProgressDialog dialog = null;
        
    private String upLoadServerUri = "http://www.csee.umbc.edu/~prajit1/Cyclops/UploadToServer.php";
     
    /**********  File Path *************/
    private String uploadFilePath = null;
    private String uploadFileName = null;
    
    private String fileNameOnServer = null;
    
    public String fileName = "face.jpg";
    
    
	/**
	 * This method controls what happens when the buttons in the activity are clicked
	 */
	@SuppressWarnings("unused")
	private void addListenerOnButton() {
		//if click on me, then take picture
		mTakePictureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dispatchTakePictureIntent(REQUEST_TAKE_PHOTO);
			}
		});
		mTogglePolicyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        if (policyToAllowPictureTaking) {
		            setPolicySetting(false);
		        } else {
		            setPolicySetting(true);
		        }
			}
		});
		mSendImageAndPolicyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
//				sendImage(imageTaken);
				dialog = ProgressDialog.show(SpotfixRequestActivity.this, "", "Uploading file...", true);
                
                new Thread(new Runnable() {
                	public void run() {
                		uploadFilePath = imageTaken.getAbsolutePath();
                		uploadFileName = imageTaken.getName();
                		uploadFile(uploadFilePath);
                	}
                }).start();        
			}
		});
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

	/**
	 * Returns the policy of the user as a true false value 
	 * @return Returns the policy of the user as a true false value
	 */
	public boolean isPolicyToAllowPictureTaking() {
		return policyToAllowPictureTaking;
	}
 
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_TAKE_PHOTO: {
				if (resultCode == Activity.RESULT_OK) {
					handleCameraPhoto();
				}
				break;
			}
		}
	}
 
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spotfix_request);
		//Set policy to allow picture taking by default
		setPolicySetting(true);
        
        //Get the resources in order
		mImageView = (ImageView) findViewById(R.id.picture_taken_image_view);
		mTakePictureButton = (Button) findViewById(R.id.take_picture_button);
		mSendImageAndPolicyButton = (Button) findViewById(R.id.send_image_policy_button);
		mTogglePolicyButton = (ToggleButton) findViewById(R.id.user_policy_toggle_button);

		mAlbumStorageDirFactory = new BaseAlbumDirFactory();
	}
 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.spotfix_request_options, menu);
		return true;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
	}
	
	@Override
	public synchronized void onResume() {
		super.onResume();
	}

	@SuppressWarnings("unused")
	private void sendImage(File imagePassed) {
//		try {
			
//			File image=new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Download/chuck_norris.jpg");
			Toast.makeText(this, "Sending Image...", Toast.LENGTH_LONG).show();

//			byte[] mybytearray = new byte[(int) image.length()];
//			Log.d(Cyclops.DEBUG_TAG, "file length() =" + (int) image.length());
//
//			FileInputStream fis = new FileInputStream(image);
//			Log.d(Cyclops.DEBUG_TAG, "fis created");
//
//			@SuppressWarnings("resource")
//			BufferedInputStream bis = new BufferedInputStream(fis, (int) image.length());
//			Log.d(Cyclops.DEBUG_TAG, "bis created success");
//
//			bis.read(mybytearray, 0, mybytearray.length);
//
//			Log.d(Cyclops.DEBUG_TAG, "ALL Bytes read from bis");

//			BTManager.mBTService.write(mybytearray);
			

//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
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
	
	public void setPolicySetting(boolean policySetting) {
		this.policyToAllowPictureTaking = policySetting;
	}

	private File setUpPhotoFile() throws IOException {
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		return f;
	}
	
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
                	 Toast.makeText(SpotfixRequestActivity.this, "Source File not exist :"
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
                         	 Toast.makeText(SpotfixRequestActivity.this, msg+fileNameOnServer, 
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
                    	Toast.makeText(SpotfixRequestActivity.this, "MalformedURLException", 
                                                            Toast.LENGTH_SHORT).show();
                    }
                });
                 
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
            } catch (Exception e) {
                 
                dialog.dismiss();  
                e.printStackTrace();
                 
                runOnUiThread(new Runnable() {
                    public void run() {
                    	Toast.makeText(SpotfixRequestActivity.this, "Got Exception : see logcat ", 
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}