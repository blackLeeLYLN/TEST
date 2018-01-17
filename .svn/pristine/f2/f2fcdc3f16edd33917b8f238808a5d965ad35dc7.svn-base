package af.boyuan.com.rrraf;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.boyuan.identifier.BaseActivity;
import com.boyuan.identifier.system.common.Device;
import com.boyuan.identifier.system.io.Schema;
import com.boyuan.identifier.system.mission.AutoFocusing;
import com.boyuan.identifier.system.mission.Mission;
import com.boyuan.identifier.system.mission.MotorAction;
import com.boyuan.identifier.widget.CameraPreview;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by admin on 2016/7/5.自定义相机
 */
public class Camera extends BaseActivity
{
    android.hardware.Camera camera = null;
    CameraPreview preview = null;
    android.hardware.Camera.Parameters parameters = null;
    int frameCount = 0;
    int previewWidth = 0;
    int previewHeight = 0;
    String ratio = null;
    Camera instance = null;
    // long workId = 0;
    // int originalWidth = 0;
    // int originalHeight = 0;
    // String imageSrc = null;
    boolean ratio5X = false;
    boolean ratio15X = false;
    boolean ratio40X = false;

    boolean motorMoving = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_camera;
    }

    private void setExposure(int exp)
    {
        Device.setExposure(this.exposure = exp);
        ((TextView)findViewById(R.id.xxfuck)).setText(String.valueOf(this.exposure));
    }

    @Override
    protected void initListener()
    {
        findViewById(R.id.btn_af_15x).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new AutoFocusing(Camera.this, "af-15x", MotorAction.ratio_15).go();
                setExposure(45);
            }
        });
        findViewById(R.id.btn_af_40x).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                new AutoFocusing(Camera.this, "af-40x", MotorAction.ratio_40).go();
                setExposure(70);
            }
        });
        bindEvent();
    }

    int steps = 0;
    private int nextStep()
    {
        return steps++;
    }

    @Override
    protected boolean fullscreen()
    {
        return true;
    }

    @Override
    protected void initialized()
    {
        Bundle bundle = this.getIntent().getExtras();
        // workId = bundle.getLong("work_id");
        // imageSrc = bundle.getString("image_src");
        // originalWidth = bundle.getInt("original_width");
        // originalHeight = bundle.getInt("original_height");
        motorMoving = true;
        new AutoFocusing(Camera.this, "af-reset", MotorAction.reset).go();
        Device.setExposure(0x20);

        tooltip("序列号: " + Device.getSerialNumber());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        initCamera();
        Device.lightUp();
    }

    protected void initCamera()
    {
        instance = this;
        new Mission("camera", this)
        {
            @Override
            public Object handle() throws Exception
            {
                return android.hardware.Camera.open();
            }
        }.go();
    }

    private byte[] cameraData;
    private int frameWidth;
    private int frameHeight;
    private void saveCameraData(final byte[] data, final int width, final int height)
    {
        this.frameCount += 1;
        if (this.frameCount > 0)
        {
            this.frameWidth = width;
            this.frameHeight = height;
            this.cameraData = data;
        }

        ((Button)findViewById(R.id.xxsteps)).setText(String.valueOf(this.frameCount));
        /*
        if (frameCount % 4 != 3) return;
        new Mission("xxoo", this)
        {
            @Override
            public Object handle() throws Exception
            {
                final YuvImage image = new YuvImage(data, ImageFormat.NV21, width, height, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
                if(!image.compressToJpeg(new Rect(0, 0, width, height), 100, os))
                {
                    return null;
                }
                byte[] tmp = os.toByteArray();
                Bitmap bmp = BitmapFactory.decodeByteArray(tmp, 0,tmp.length);
                return bmp;
            }
        }.go();
        */
    }

    public int getFrameWidth()
    {
        return this.frameWidth;
    }

    public int getFrameHeight()
    {
        return this.frameHeight;
    }

    public byte[] getCameraData()
    {
        /*
        if (null == this.cameraData) return null;
        byte[] buff = this.cameraData.clone();
        this.cameraData = null;
        return buff;
        */
        return this.cameraData;
    }

    public byte[] obtainCameraData()
    {
        if (null == this.cameraData) return null;
        byte[] buff = this.cameraData.clone();
        this.cameraData = null;
        return buff;
    }

    @Override
    public void complete(String mission, Object data) throws Exception
    {
        if ("test".equals(mission))
        {
            // ((TextView)findViewById(R.id.xxfuck)).setText(String.valueOf(data));
        }

        if ("camera".equals(mission))
        {
            try
            {
                camera = (android.hardware.Camera) data;
                parameters = camera.getParameters();
                parameters.set("redgain", "32");
                parameters.set("bluegain", "32");
                parameters.set("hand_exposure", "100");
                // 通过这里设置摄像头分辨率
                // parameters.setPreviewSize(320, 180);
                // parameters.setPreviewSize(640, 480);
                camera.setParameters(parameters);
                camera.setDisplayOrientation(90);
                preview = new CameraPreview(instance, camera);
                preview.setPreviewListener(new android.hardware.Camera.PreviewCallback()
                {
                    @Override
                    public void onPreviewFrame(byte[] data, final android.hardware.Camera cameraX)
                    {
                        saveCameraData(data, cameraX.getParameters().getPreviewSize().width, cameraX.getParameters().getPreviewSize().height);
                        new Mission("test", Camera.this)
                        {
                            @Override
                            public Object handle() throws Exception
                            {
                                /*
                                List<android.hardware.Camera.Size> sizes = cameraX.getParameters().getSupportedPreviewSizes();
                                String xx = "";
                                for (int i = 0; i < sizes.size(); i++)
                                {
                                    android.hardware.Camera.Size oo = sizes.get(i);
                                    xx += oo.width + "x" + oo.height + ",";
                                }
                                */

                                List<int[]> ranges = cameraX.getParameters().getSupportedPreviewFpsRange();
                                String xx = "";
                                for (int i = 0; i < ranges.size(); i++)
                                {
                                    int[] oo = ranges.get(i);
                                    for (int k = 0; k < oo.length; k++)
                                    {
                                        xx += oo[k] + ":";
                                    }
                                    xx += ",";
                                }
                                return xx;
                            }
                        }.go();
                    }
                });
                ((FrameLayout)findViewById(R.id.camera_container)).addView(preview, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                Log.d("houcheng", ex.toString());
            }
        }
        if ("af-reset".equals(mission))
        {
            // new AutoFocusing(Camera.this, "af-15x", MotorAction.ratio_15).go();
        }
        if ("af-15x".equals(mission))
        {
            motorMoving = false;
            ratio = "15x";
            // tooltip("停留在: " + data);
            tooltip("序号：" + String.valueOf(data));
        }
        if ("af-40x".equals(mission))
        {
            motorMoving = false;
            ratio = "40x";
            tooltip("序号：" + String.valueOf(data));
        }
        if ("af-zoom-in".equals(mission))
        {
            motorMoving = false;
            // ((TextView)findViewById(R.id.steps)).setText(String.valueOf(data));
        }
        if ("af-zoom-out".equals(mission))
        {
            motorMoving = false;
            // ((TextView)findViewById(R.id.steps)).setText(String.valueOf(data));
        }
        if (mission.startsWith("progress:"))
        {
            // ((TextView)findViewById(R.id.steps)).setText(String.valueOf(data));
        }
        if ("takephoto".equals(mission))
        {
            tooltip("照片已保存到: liyanan.jpg");
        }
    }

    @Override
    public void error(String mission, Exception issue) throws Exception
    {
        if ("submit".equals(mission))
        {
            tooltip("出错啦：" + issue.toString());
            uploading = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        try { camera.setPreviewCallback(null); } catch(Exception e) { }
        try { camera.stopPreview(); } catch(Exception e) { }
        try { camera.release(); } catch(Exception e) { }
        Device.lightOff();
    }

    private boolean uploading = false;

    // 点叉叉时触发
    private void tryToExit()
    {
        if (motorMoving)
        {
            tooltip("正在对焦");
            return;
        }
        if (uploading)
        {
            return;
        }
        this.finish();
    }

    // 点拍照按钮时触发
    private void takePhoto()
    {
        tooltip("拍什么拍？");
    }

    // 单步缩小按钮
    private void zoomOut()
    {
        // motorMoving = true;
        // new AutoFocusing(this, "af-zoom-out", MotorAction.step_zoom_out).go();

        setExposure(this.exposure -= 1);
    }

    private int exposure = 0;

    // 单步放大按钮
    private void zoomIn()
    {
        // motorMoving = true;
        // new AutoFocusing(this, "af-zoom-in", MotorAction.step_zoom_in).go();
        setExposure(this.exposure += 1);
    }

    // 提交到云端按钮
    private void submit()
    {
        tooltip("点这个小云朵干什么？");
    }

    private void bindEvent()
    {
        findViewById(R.id.iv_left_decrease).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomOut();
            }
        });

        findViewById(R.id.iv_right_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                zoomIn();
            }
        });

        findViewById(R.id.xxsteps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                camera.takePicture(new android.hardware.Camera.ShutterCallback()
                {
                    @Override
                    public void onShutter()
                    {
                        tooltip("拍个照...");
                    }
                }, null, new android.hardware.Camera.PictureCallback() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onPictureTaken(final byte[] data, android.hardware.Camera camera) {
                        try {

                            (new Mission("takephoto", instance) {
                                @Override
                                public Object handle() throws Exception {
                                    FileOutputStream fos = null;
                                    Matrix m = new Matrix();
                                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    // bmp.getPixels(null, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
                                    m.setRotate(90, (float) bmp.getWidth() / 2, (float) bmp.getHeight() / 2);
                                    bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
                                    fos = new FileOutputStream(Schema.parse("file://liyanan.jpg").getFile(instance));

                                    int destWidth = bmp.getWidth();
                                    int destHeight = bmp.getHeight();

                                    if (destWidth > 2000 || destHeight > 2000)
                                    {
                                        if (destWidth > destHeight)
                                        {
                                            destHeight = (int)((float)destHeight / ((float)destWidth / 2000));
                                            destWidth = 2000;
                                        }
                                        else
                                        {
                                            destWidth = (int)((float)destWidth / ((float)destHeight / 2000));
                                            destHeight = 2000;
                                        }
                                    }

                                    bmp = Bitmap.createScaledBitmap(bmp, destWidth, destHeight, true);

                                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                    return null;
                                }
                            }).go();

                            // 需要跳转到下一步，设置位置去了
                            // 那这里就可以不需要startPreview了
                            // camera.startPreview();
                            preview.startPreview();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            tooltip(ex.toString());
                        }
                    }
                });
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (uploading) return true;
        }
        if (keyCode == KeyEvent.KEYCODE_9)
        {
            tooltip("吃饱了没事干啊?");
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // result code: 1 成功, 2 失败, 3 取消
        if (resultCode != 1)
        {
            tooltip("位置设定己取消");
            return;
        }
        Bundle bundle = data.getExtras();
        offsetLeft = bundle.getInt("offset_left");
        offsetTop = bundle.getInt("offset_top");
    }

    private int offsetLeft = -1;
    private int offsetTop = -1;
}
