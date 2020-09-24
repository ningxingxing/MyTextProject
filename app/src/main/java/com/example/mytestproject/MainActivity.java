package com.example.mytestproject;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.DragAndDropPermissionsCompat;

public class MainActivity extends AppCompatActivity {
    private static final String IMAGEVIEW_TAG = "icon bitmap";
    private final String TAG = "MainActivity";
    private static final String IMAGE_URI = "IMAGE_URI";
    private Uri mImageUri;
    private Button btnBrush;
    private Button btnRecycler;
    private DrawView mDrawView;
    private ImageView ivDrag;
    private Button btnBlur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView imageView = findViewById(R.id.iv_show);

        // Sets the bitmap for the ImageView from an icon bit map (defined elsewhere)
       // imageView.setImageBitmap(iconBitmap);
        imageView.setImageResource(R.mipmap.ic_launcher);
        // Sets the tag
        imageView.setTag(IMAGEVIEW_TAG);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                ClipData.Item item = new ClipData.Item((String) v.getTag());
                ClipData data = new ClipData(IMAGEVIEW_TAG,
                        new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN },
                        item);
                View.DragShadowBuilder myShadow = new MyDragShadowBuilder(v);
                //调用startDrag方法，第二个参数为创建拖放阴影
                v.startDrag(data, new View.DragShadowBuilder(v), null, 0);
                return false;
            }
        });
        imageView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                final int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        //拖拽开始事件
                        if (event.getClipDescription().hasMimeType(
                                ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                            return true;
                        }
                        return false;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        //被拖放View进入目标View
                        imageView.setBackgroundColor(Color.YELLOW);
                        Log.e(TAG,"onDrag ACTION_DRAG_ENTERED");
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        Log.e(TAG,"onDrag ACTION_DRAG_LOCATION");
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        //被拖放View离开目标View
                        Log.e(TAG,"onDrag ACTION_DRAG_EXITED");
                        imageView.setBackgroundColor(Color.BLUE);
                        return true;
                    case DragEvent.ACTION_DROP:
                        //释放拖放阴影，并获取移动数据
                        Log.e(TAG,"onDrag ACTION_DROP");
                        ClipData.Item item = event.getClipData().getItemAt(0);
                        String dragData = item.getText().toString();

                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        Log.e(TAG,"onDrag ACTION_DRAG_ENDED");
                        //拖放事件完成
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
        mDrawView = findViewById(R.id.draw_view);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawView.getShape()==1){
                    mDrawView.setShape(2);
                }else {
                    mDrawView.setShape(1);
                }

            }
        });

        btnBrush = findViewById(R.id.btn_brush);
        btnBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, OldDemoActivity.class);
                startActivity(intent);
            }
        });

        btnRecycler = findViewById(R.id.btn_recycler);
        btnRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RecyclerActivity.class);
                startActivity(intent);

            }
        });

        btnBlur = findViewById(R.id.btn_blur);
        btnBlur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,BlurActivity.class);
                startActivity(intent);
            }
        });


        ivDrag = findViewById(R.id.iv_drag);

    }
    private float lastX;
    private float lastY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getRawX();
                lastY = event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                //计算移动的距离
                int dx = (int) (event.getRawX() - lastX);//相对坐标
                int dy = (int) (event.getRawY() - lastY);//相对坐标
                ivDrag.layout(ivDrag.getLeft() + dx, ivDrag.getTop() + dy, ivDrag.getRight() + dx, ivDrag.getBottom() + dy);//设置按钮位置
                lastX = event.getRawX();
                lastY = event.getRawY();
                break;
        }

        return true;

    //    return super.onTouchEvent(event);
    }

    private class PermissionAwareImageDragListener extends ImageDragListener {

        @Override
        protected void processLocation(float x, float y) {
            // Callback is received when the dragged image enters the drop area.
        }

        @Override
        protected boolean setImageUri(View view, DragEvent event, Uri uri) {
            // Read the string from the clip description extras.
           // Log.d(TAG, "ClipDescription extra: " + getExtra(event));

            Log.d(TAG, "Setting image source to: " + uri.toString());
            mImageUri = uri;

            if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
                // Accessing a "content" scheme Uri requires a permission grant.
                DragAndDropPermissionsCompat dropPermissions = ActivityCompat
                        .requestDragAndDropPermissions(MainActivity.this, event);
                Log.d(TAG, "Requesting permissions.");

                if (dropPermissions == null) {
                    // Permission could not be obtained.
                    Log.d(TAG, "Drop permission request failed.");
                    return false;
                }

                final boolean result = super.setImageUri(view, event, uri);

//                if (mReleasePermissionCheckBox.isChecked()) {
//                    /* Release the permissions if you are done with the URI.
//                     Note that you may need to hold onto the permission until later if other
//                     operations are performed on the content. For instance, releasing the
//                     permissions here will prevent onCreateView from properly restoring the
//                     ImageView state.
//                     If permissions are not explicitly released, the permission grant will be
//                     revoked when the activity is destroyed.
//                     */
//                    dropPermissions.release();
//                    Log.d(TAG, "Permissions released.");
//                }

                return result;
            } else {
                // Other schemes (such as "android.resource") do not require a permission grant.
                return super.setImageUri(view, event, uri);
            }
        }

        @Override
        public boolean onDrag(View view, DragEvent event) {
            // DragTarget is peeking into the MIME types of the dragged event in order to ignore
            // non-image drags completely.
            // DragSource does not do that but rejects non-image content once a drop has happened.
            ClipDescription clipDescription = event.getClipDescription();
            if (clipDescription != null && !clipDescription.hasMimeType("image/*")) {
                return false;
            }
            // Callback received when image is being dragged.
            return super.onDrag(view, event);
        }
    }

}
