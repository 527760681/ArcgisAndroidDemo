package com.example.application1.MyOnTouchListener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.example.application1.DatabaseUtils.ItemPointBean;
import com.example.application1.DatabaseUtils.ItemPointDao;
import com.example.application1.DatabaseUtils.ProjectBean;
import com.example.application1.DatabaseUtils.ProjectDao;
import com.example.application1.ItemEditActivity;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ItemSelectOnTouckListener extends DefaultMapViewOnTouchListener {
    private MapView mapView;
    private Context context;
    private int projectID;

    public ItemSelectOnTouckListener(Context context, MapView mapView, int projectID) {
        super(context, mapView);
        this.mapView = mapView;
        this.context = context;
        this.projectID = projectID;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        android.graphics.Point screenPoint = new android.graphics.Point((int) event.getX(), (int) event.getY());
        final ListenableFuture<IdentifyGraphicsOverlayResult> identifyGraphics = mapView.identifyGraphicsOverlayAsync(mapView.getGraphicsOverlays().get(0),
                screenPoint, 10, false, 1);

        identifyGraphics.addDoneListener(() -> {
            try {
                IdentifyGraphicsOverlayResult result = identifyGraphics.get();
                List<Graphic> graphics = result.getGraphics();
                if (!graphics.isEmpty()) {
                    com.esri.arcgisruntime.geometry.Point point = (com.esri.arcgisruntime.geometry.Point) graphics.get(0).getGeometry();

                    double x = point.getX();
                    double y = point.getY();

                    List<ItemPointBean> itemPointBeans = new ItemPointDao(context).queryByProjectID(projectID);
                    for (ItemPointBean itemPoint : itemPointBeans) {
                        double x_target = itemPoint.getX();
                        double y_target = itemPoint.getY();

                        if (x_target == x && y_target == y) {
                            Intent intent = new Intent(context, ItemEditActivity.class);
                            intent.putExtra("type","edit");
                            intent.putExtra("id",itemPoint.getId());
                            context.startActivity(intent);
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return true;
    }

}
