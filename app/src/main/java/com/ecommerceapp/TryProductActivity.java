package com.ecommerceapp;

import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.PlaneDiscoveryController;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

public class TryProductActivity extends AppCompatActivity {
    private ArFragment arFragment;
    private Uri object;
    private String category, name;
    private Session arSesson;
    private Config arConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try_product);

        category = getIntent().getStringExtra("category");
        name = getIntent().getStringExtra("name");

        if (name.equals("White sofa two seater")) {
            object = Uri.parse("sofa.sfb");

        } else if (name.equals("Silver Frame glasses")) {
            object = Uri.parse("Sunglasses.sfb");

        } else if (name.equals("Red bicycle")) {
            object = Uri.parse("cycle.sfb");

        } else if (name.equals("Small round table")) {
            object = Uri.parse("table.sfb");

        } else if (name.equals("Wooden wardrobe")) {
            object = Uri.parse("43.sfb");

        } else if (name.equals("Plant vase")) {
            object = Uri.parse("vase.sfb");

        } else if (name.equals("Table lamp")) {
            object = Uri.parse("Lamp.sfb");

        } else if (name.equals("Candle stand")) {
            object = Uri.parse("Candelobra.sfb");

        } else if (name.equals("Wooden sofa chair")) {
            object = Uri.parse("43.sfb");

        } else if (name.equals("Wall Painting")) {
            object = Uri.parse("WallPainting_01.sfb");
        }


        arFragment = (ArFragment) getSupportFragmentManager()
                .findFragmentById(R.id.ar_fragment);

        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {

                Anchor anchor = hitResult.createAnchor();

                placeObject(arFragment, anchor, object);
            }
        });

    }

    private void placeObject(ArFragment arFragment, Anchor anchor, Uri model) {

        ModelRenderable.builder().setSource(arFragment.getContext(), model).build()
                .thenAccept(
                        renderable -> addNodeToScene(arFragment, anchor, renderable))
                .exceptionally(throwable -> {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(this);
                    builder.setMessage(throwable.getMessage())
                            .setTitle("Error");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return null;
                });
    }

    private void addNodeToScene(ArFragment arFragment, Anchor anchor, Renderable renderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode transformableNode = new TransformableNode(
                arFragment.getTransformationSystem());

            //transformableNode.getScaleController().setMaxScale(0.09f);
            transformableNode.getScaleController().setMinScale(0.05f);

            transformableNode.setRenderable(renderable);
            transformableNode.setParent(anchorNode);
            arFragment.getArSceneView().getScene().addChild(anchorNode);
            transformableNode.select();

    }
}
