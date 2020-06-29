package com.testninja.seleniumar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    private ArFragment arFragment;
    public FloatingActionButton addARViewBtn;
    private static boolean isARViewLoaded = false;
    private static final String serverUrl = "http://IP:9093"; //replace the IP with the server's ip address

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    private void initialize() {
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneForm);

        addARViewBtn = (FloatingActionButton) findViewById(R.id.add_ar_frame_bth);
        addARViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isARViewLoaded && addStreamingFrameToView()) {
                    isARViewLoaded = !isARViewLoaded;
                    addARViewBtn.hide();
                }
            }
        });
    }

    private boolean addStreamingFrameToView() {
        Anchor anchor = getAnchorPoint();
        if (anchor != null) {
            arFragment.getArSceneView()
                    .getPlaneRenderer().setVisible(false);

            ViewRenderable.builder()
                    .setView(arFragment.getContext(), R.layout.image_streaming_view).build()
                    .thenAccept(viewRenderable -> {
                        try {
                            ImageView view = (ImageView) viewRenderable.getView().findViewById(R.id.img);
                            AnchorNode anchorNode = new AnchorNode(anchor);
                            TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
                            node.setRenderable(viewRenderable);
                            node.setParent(anchorNode);
                            arFragment.getArSceneView().getScene().addChild(anchorNode);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    connectToSocket(view);
                                }
                            }).start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
            return true;
        }
        return false;
    }

    private Anchor getAnchorPoint() {
        Frame frame = arFragment.getArSceneView().getArFrame();
        Point point = getScreenCenter();

        if (frame != null) {
            List<HitResult> hits = frame.hitTest((float) point.x, (float) point.y);

            for (int i = 0; i < hits.size(); i++) {
                Trackable trackable = hits.get(i).getTrackable();
                if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hits.get(i).getHitPose())) {
                    return hits.get(i).createAnchor();
                }
            }
        }

        return null;
    }

    private Point getScreenCenter() {
        View vw = findViewById(android.R.id.content);
        return new Point(vw.getWidth() / 2, vw.getHeight() / 2);
    }

    private void connectToSocket(ImageView view) {
        try {
            Socket socket = IO.socket(serverUrl);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    /* start the automation script execution in sever and waits for the live stream */
                    socket.emit("start", "");
                }

            }).on("data", new Emitter.Listener() { //When new browser screenshot is sent from server

                @Override
                public void call(Object... args) {
                    Bitmap image = convertBase64ToBitMap(args[0].toString()); //args[0] holds the base64 encoded screenshot of the browser
                    /* To avoid screen freeze, render the image after a delay*/
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    view.setImageBitmap(image);
                                }
                            });
                        }
                    }, 100);

                }

            });
            socket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap convertBase64ToBitMap(String base64) {
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
