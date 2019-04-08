package com.example.application1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.raster.Raster;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.example.application1.DatabaseUtils.ItemPointBean;
import com.example.application1.DatabaseUtils.ItemPointDao;
import com.example.application1.DatabaseUtils.ProjectBean;
import com.example.application1.DatabaseUtils.ProjectDao;
import com.example.application1.MyOnTouchListener.ItemAddOnTouchListener;
import com.example.application1.MyOnTouchListener.ItemDelectOnTouchListener;
import com.example.application1.MyOnTouchListener.ItemSelectOnTouckListener;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MapView mMapView;
    private long exitTime = 0;
    private ArcGISMap map;
    private GraphicsOverlay graphicsOverlay;
    private ImageButton imageButton_add;
    private ImageButton imageButton_save;
    private ImageButton imageButton_details;
    private ImageButton imageButton_delete;
    final private int DEFAULT = 0;
    final private int BUTTON_ADD = 1;
    final private int BUTTON_DETAIL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = findViewById(R.id.mapView);
        imageButton_add = findViewById(R.id.imageButton_add);
        imageButton_save = findViewById(R.id.imageButton_save);
        imageButton_details = findViewById(R.id.imageButton_details);
        imageButton_delete = findViewById(R.id.imageButton_delete);

        map = new ArcGISMap(new Basemap());
        mMapView.setMap(map);

        if (getIntent() != null) {
            Intent intent = getIntent();
            int projectID = intent.getIntExtra("ProjectID", -1);
            if (projectID != -1) {
                changeButtonStatus(DEFAULT);
                initRasterLayerByProjectID(projectID);
                initGraphicsOverlay(projectID);
                imageButton_add.setOnClickListener(v ->{
                    //切换Listener
                    mMapView.setOnTouchListener(new ItemAddOnTouchListener(MainActivity.this, mMapView));
                    changeButtonStatus(BUTTON_ADD);
                }
                );
                imageButton_save.setOnClickListener(v -> {
                    //切换listener为默认
                    mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(MainActivity.this, mMapView));

                    //然后保存结果
                    List<Graphic> graphicList = graphicsOverlay.getGraphics();
                    Graphic newlyAddedItem = graphicList.get(graphicList.size() - 1);
                    Point location = (Point) newlyAddedItem.getGeometry();
                    double x = location.getX();
                    double y = location.getY();

                    //打开新界面并且传递
                    Intent saveIntent = new Intent(MainActivity.this, ItemEditActivity.class);
                    saveIntent.putExtra("type", "save");
                    saveIntent.putExtra("x", x);
                    saveIntent.putExtra("y", y);
                    saveIntent.putExtra("ProjectID", projectID);
                    startActivity(saveIntent);
                    this.finish();
                });
                imageButton_delete.setOnClickListener(v -> {
                    mMapView.setOnTouchListener(new ItemDelectOnTouchListener(this, mMapView, projectID));
                    changeButtonStatus(DEFAULT);
                });
                imageButton_details.setOnClickListener(v -> {
                    mMapView.setOnTouchListener(new ItemSelectOnTouckListener(this, mMapView, projectID));
                    changeButtonStatus(BUTTON_DETAIL);
                });
            }
        }
    }

    private void initGraphicsOverlay(int projectID) {
        //GraphicsOverlay
        graphicsOverlay = new GraphicsOverlay();

        SimpleMarkerSymbol newlyAddedSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, getColor(R.color.itemEdited), 20);

        List<ItemPointBean> queryResult = new ItemPointDao(this).queryByProjectID(projectID);
        for (ItemPointBean result : queryResult) {
            double x = result.getX();
            double y = result.getY();
            Point location = new Point(x, y);
            graphicsOverlay.getGraphics().add(new Graphic(location, newlyAddedSymbol));
        }
        mMapView.getGraphicsOverlays().add(graphicsOverlay);
    }

    private void changeButtonStatus(int statusCode){
        switch (statusCode){
            case DEFAULT://默认状态
                imageButton_save.setEnabled(false);
                imageButton_save.setBackgroundColor(getColor(R.color.buttonDisabled));
                imageButton_add.setEnabled(true);
                imageButton_add.setBackgroundColor(getColor(R.color.buttonEnabled));
                imageButton_details.setEnabled(true);
                imageButton_details.setBackgroundColor(getColor(R.color.buttonEnabled));
                imageButton_delete.setEnabled(true);
                imageButton_delete.setBackgroundColor(getColor(R.color.buttonEnabled));
                break;
            case BUTTON_ADD://点击添加之后
                imageButton_save.setEnabled(true);
                imageButton_save.setBackgroundColor(getColor(R.color.buttonEnabled));
                imageButton_add.setEnabled(false);
                imageButton_add.setBackgroundColor(getColor(R.color.buttonDisabled));
                imageButton_details.setEnabled(false);
                imageButton_details.setBackgroundColor(getColor(R.color.buttonDisabled));
                imageButton_delete.setEnabled(false);
                imageButton_delete.setBackgroundColor(getColor(R.color.buttonDisabled));
                break;
            case BUTTON_DETAIL:
                imageButton_save.setEnabled(false);
                imageButton_save.setBackgroundColor(getColor(R.color.buttonDisabled));
                imageButton_add.setEnabled(false);
                imageButton_add.setBackgroundColor(getColor(R.color.buttonDisabled));
                imageButton_details.setEnabled(false);
                imageButton_details.setBackgroundColor(getColor(R.color.buttonDisabled));
                imageButton_delete.setEnabled(false);
                imageButton_delete.setBackgroundColor(getColor(R.color.buttonDisabled));
        }
    }

    private void initRasterLayerByProjectID(int projectID) {
        //根据ID查询数据
        ProjectBean projectBean = new ProjectDao(getApplicationContext()).queryById(projectID);
        String fileName = new File(projectBean.getBaseMapPath()).getName();

        //添加栅格底图
        String RasterFilePath = projectBean.getBaseMapPath();
        Raster raster = new Raster(RasterFilePath);
        RasterLayer rasterLayer = new RasterLayer(raster);
        map.getOperationalLayers().add(rasterLayer);
        rasterLayer.addDoneLoadingListener(() -> {
            if (rasterLayer.getLoadStatus() == LoadStatus.LOADED) {
                mMapView.setViewpointGeometryAsync(rasterLayer.getFullExtent());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_project:
                Intent intent_add_project = new Intent(MainActivity.this, NewPorjectActivity.class);
                startActivity(intent_add_project);
                this.finish();
                break;
            case R.id.menu_manage_project:
                Intent menu_manage_project = new Intent(MainActivity.this, ProjectManageActivity.class);
                startActivity(menu_manage_project);
                this.finish();
                break;
        }
        return true;
    }

    @Override
    protected void onPause() {
        mMapView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
    }

    @Override
    protected void onDestroy() {
        mMapView.dispose();
        super.onDestroy();
    }
}
