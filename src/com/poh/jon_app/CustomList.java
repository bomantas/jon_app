package com.poh.jon_app;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import com.example.com.poh.pecs_plus.R;
import com.example.com.poh.pecs_plus.R.id;
import com.example.com.poh.pecs_plus.R.layout;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomList extends ArrayAdapter<String>{
	private final Activity context;
	//private List<String> web;
	//private List<String> filename;
	private final String[] web;
	private final String[] imageId;	
	
	public CustomList(Activity context,
			String[] web, String[] imageId2) {
			super(context, R.layout.list_single, web);
			this.context = context;
			this.web = web;
			this.imageId = imageId2;
			}	
	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView= inflater.inflate(R.layout.list_single, null, true);
		
		TextView txtTitle = (TextView) rowView.findViewById(R.id.listtv);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.listimg);
		txtTitle.setVisibility(View.VISIBLE);
		txtTitle.setText(web[position]);
		txtTitle.setTextSize(32);

		if(imageId.length > 0)
		{
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);		
			float width = size.x;
			float height = size.y;
			imageView.setVisibility(View.VISIBLE);
			String filepath = "poh_pecs/" + imageId[position];
			File image = new File(Environment.getExternalStorageDirectory(), filepath);
			Uri uri = Uri.fromFile(image);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(uri.getPath(), options);
			float imageHeight = options.outHeight;
			float imageWidth = options.outWidth;
			
	        try {
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.context.getContentResolver(), uri);
				//bitmap = getResizedBitmap(bitmap, width, height);
				if(txtTitle.getText().length() < 1)
				{
					float scale = (float)(width-100) / imageWidth;

					imageView.getLayoutParams().width = (int) (imageWidth * scale);
					imageView.getLayoutParams().height = (int) (imageHeight * scale);
				}
				else
				{
					// Logo
					float scale = (float)256 / imageHeight;
					imageView.getLayoutParams().width = (int) (imageWidth * scale);
					imageView.getLayoutParams().height = (int) (imageHeight * scale);
				}
				imageView.setImageBitmap(bitmap);		
				//bitmap.recycle();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		else
			imageView.setVisibility(View.GONE);
	
		//imageView.setImageResource(R.drawable.image1);
		return rowView;	
	}
	
	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
	    int width = bm.getWidth();
	    int height = bm.getHeight();
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    // CREATE A MATRIX FOR THE MANIPULATION
	    Matrix matrix = new Matrix();
	    // RESIZE THE BIT MAP
	    matrix.postScale(scaleWidth, scaleHeight);

	    // "RECREATE" THE NEW BITMAP
	    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	    return resizedBitmap;
	}
}