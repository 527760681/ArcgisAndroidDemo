package com.example.application1.MyOnTouchListener;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.example.application1.DatabaseUtils.ItemPointBean;
import com.example.application1.DatabaseUtils.ItemPointDao;
import com.example.application1.DatabaseUtils.ProjectDao;
import com.example.application1.FIleUtils.FileUtils;
import com.example.application1.ItemEditActivity;

import java.util.List;

public class ItemDelectOnTouchListener extends DefaultMapViewOnTouchListener {
    private MapView mapView;
    private Context context;
    private int projectID;

    public ItemDelectOnTouchListener(Context context, MapView mapView, int projectID) {
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
                            new AlertDialog.Builder(context)
                                    .setTitle("确定要删除" + itemPoint.getItemName() + "吗?")
                                    .setPositiveButton("确定", (dialog, which) -> {
                                        new ItemPointDao(context).delete(itemPoint);
                                        FileUtils.removeFile(itemPoint.getImageDir());
                                        mapView.getGraphicsOverlays().get(0).getGraphics().remove(graphics.get(0));
                                        dialog.dismiss();
                                    })
                                    .setNegativeButton("取消",
                                            ((dialog, which) -> dialog.dismiss()))
                                    .show();
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
