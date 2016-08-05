package info.competitiveprogramming.transparentcapture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    CameraListener cameraListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraListener = new CameraListener();
        SurfaceView surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(cameraListener);
        cameraListener.setImageView((ImageView) findViewById(R.id.imageView));

        final View contentView = findViewById(android.R.id.content);
        contentView.setOnClickListener(this);
    }

    private static class CameraListener implements
            SurfaceHolder.Callback,
            Camera.AutoFocusCallback,
            Camera.PictureCallback,
            Camera.PreviewCallback,
            Camera.ShutterCallback {

        private Camera camera;
        private Bitmap bitmap;
        private ImageView imageView;

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            openCamera(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (camera != null) {
                try {
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setPreviewSize(720, 480);
                    camera.setParameters(parameters);
                } catch (Exception e) {
                }
                startPreview();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                camera.release();
                camera = null;
            }
        }

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
        }

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Matrix m = new Matrix();
            m.postRotate(90);
            Bitmap rawBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            bitmap = Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.getWidth(), rawBitmap.getHeight(), m, false);
            imageView.setImageBitmap(bitmap);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    startPreview();
                }
            }, 100);
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
        }

        @Override
        public void onShutter() {
        }

        void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }

        boolean openCamera(SurfaceHolder holder) {
            boolean result = false;
            try {
                camera = Camera.open();
                camera.setPreviewDisplay(holder);
                camera.setDisplayOrientation(90);
                result = true;
            } catch (Exception e) {
            }
            return result;
        }

        void takePicture() {
            if (camera != null) {
                camera.takePicture(this, null, this);
            }
        }

        void startPreview() {
            if (camera != null) {
                try {
                    camera.startPreview();
                    camera.autoFocus(this);
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (cameraListener != null) {
            cameraListener.takePicture();
        }
    }
}
