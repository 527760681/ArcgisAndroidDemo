package com.example.application1.MyOnTouchListener;

import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;


import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.example.application1.R;

import java.util.List;


public class ItemAddOnTouchListener extends DefaultMapViewOnTouchListener {
    private Context context;
    private MapView mapView;
    private Graphic graphic;

    public ItemAddOnTouchListener(Context context, MapView mapView){
        super(context,mapView);
        this.context = context;
        this.mapView = mapView;
    }

    @Override
     public boolean onSingleTapConfirmed(MotionEvent event){
        com.esri.arcgisruntime.geometry.Point location = mapView.screenToLocation(new Point((int)event.getX(),(int)event.getY()));
        GraphicsOverlay graphicsOverlay = mapView.getGraphicsOverlays().get(0);
        SimpleMarkerSymbol newlyAddedSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, context.getColor(R.color.itemNewlyAdded),20);
        List<Graphic> graphicList =  graphicsOverlay.getGraphics();
        if (graphic == null){
            graphic = new Graphic(location,newlyAddedSymbol);
            graphicList.add(graphic);
        }else {
            graphic = new Graphic(location,newlyAddedSymbol);
            graphicList.remove(graphicList.size()-1);
            graphicList.add(graphic);
        }
        return true;
    }
}
