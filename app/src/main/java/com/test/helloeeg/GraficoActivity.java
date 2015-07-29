package com.test.helloeeg;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;

import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class GraficoActivity extends ActionBarActivity {

    static ArrayList<String> datosGrafico=new ArrayList<String>();
    static ArrayList<Integer> meditacion= new ArrayList<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_line_chart);
        Bundle bundle=getIntent().getExtras();
        datosGrafico = bundle.getStringArrayList("listaString");
        meditacion=bundle.getIntegerArrayList("listaInt");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    /**
     * A fragment containing a line chart and preview line chart.
     */
    public static class PlaceholderFragment extends Fragment {

        private LineChartView chart;
        private PreviewLineChartView previewChart;
        private LineChartData data;
        /**
         * Deep copy of data.
         */
        private LineChartData previewData;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_preview_line_chart, container, false);

            chart = (LineChartView) rootView.findViewById(R.id.chart);
            previewChart = (PreviewLineChartView) rootView.findViewById(R.id.chart_preview);

            // Generate data for previewed chart and copy of that data for preview chart.
            generateDefaultData();

            chart.setLineChartData(data);
            // Disable zoom/scroll for previewed chart, visible chart ranges depends on preview chart viewport so
            // zoom/scroll is unnecessary.
            chart.setZoomEnabled(false);
            chart.setScrollEnabled(false);

            previewChart.setLineChartData(previewData);
            previewChart.setViewportChangeListener(new ViewportListener());

            previewX(false);

            return rootView;
        }

        // MENU
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.preview_line_chart, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_reset) {
                generateDefaultData();
                chart.setLineChartData(data);
                previewChart.setLineChartData(previewData);
                previewX(true);
                return true;
            }
            if (id == R.id.action_preview_both) {
                previewXY();
                previewChart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
                return true;
            }
            if (id == R.id.action_preview_horizontal) {
                previewX(true);
                return true;
            }
            if (id == R.id.action_preview_vertical) {
                previewY();
                return true;
            }
            if (id == R.id.action_change_color) {
                int color = ChartUtils.pickColor();
                while (color == previewChart.getPreviewColor()) {
                    color = ChartUtils.pickColor();
                }
                previewChart.setPreviewColor(color);
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        private void generateDefaultData() {
            int numValues = 50;

            List<PointValue> values =  casteo(datosGrafico);


            Line line = new Line(values);
            line.setColor(ChartUtils.COLOR_GREEN);
            line.setHasPoints(false);// too many values so don't draw points.

            List<Line> lines = new ArrayList<Line>();
            lines.add(line);

            data = new LineChartData(lines);
            data.setAxisXBottom(new Axis());
            data.setAxisYLeft(new Axis().setHasLines(true));

            // prepare preview data, is better to use separate deep copy for preview chart.
            // Set color to grey to make preview area more visible.
            previewData = new LineChartData(data);
            previewData.getLines().get(0).setColor(ChartUtils.DEFAULT_DARKEN_COLOR);

        }

        private void previewY() {
            Viewport tempViewport = new Viewport(chart.getMaximumViewport());
            float dy = tempViewport.height() / 4;
            tempViewport.inset(0, dy);
            previewChart.setCurrentViewportWithAnimation(tempViewport);
            previewChart.setZoomType(ZoomType.VERTICAL);
        }

        private void previewX(boolean animate) {
            Viewport tempViewport = new Viewport(chart.getMaximumViewport());
            float dx = tempViewport.width() / 4;
            tempViewport.inset(dx, 0);
            if(animate) {
                previewChart.setCurrentViewportWithAnimation(tempViewport);
            }else{
                previewChart.setCurrentViewport(tempViewport);
            }
            previewChart.setZoomType(ZoomType.HORIZONTAL);
        }

        private void previewXY() {
            // Better to not modify viewport of any chart directly so create a copy.
            Viewport tempViewport = new Viewport(chart.getMaximumViewport());
            // Make temp viewport smaller.
            float dx = tempViewport.width() / 4;
            float dy = tempViewport.height() / 4;
            tempViewport.inset(dx, dy);
            previewChart.setCurrentViewportWithAnimation(tempViewport);
        }

        //convierte la lista de strings a lista de pointValues para efectos del grafico
        private ArrayList<PointValue> casteo(ArrayList<String> lista) {
            ArrayList<PointValue> retorno=new ArrayList<PointValue>();
           for (int i=0; i<lista.size() && i < 100;i++){
               retorno.add(new PointValue( i,Integer.parseInt(lista.get(i))));
           }
            return retorno;
        }
        /**
         * Viewport listener for preview chart(lower one). in {@link #onViewportChanged(Viewport)} method change
         * viewport of upper chart.
         */
        private class ViewportListener implements ViewportChangeListener {

            @Override
            public void onViewportChanged(Viewport newViewport) {
                // don't use animation, it is unnecessary when using preview chart.
                chart.setCurrentViewport(newViewport);
            }

        }

    }
    public void setListBD (View view){
        BD dataBase = new BD(this);
        String valores="";
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = new Date();
        String datetime = dateformat.format(date);
        ArrayList<String> data = datosGrafico;
        for (int i=0; i<data.size();i++){
            valores+=data.get(i)+":";
        }
        // se agregan los valores obtenidos de datos graficos, en formato value0:valu1:value2:value3: ....
        dataBase.agregarElemento(valores,datetime);
        // retorna los datos en un ArrayList<String> en el orden del ultimo hasta el primero (DESC)

         Toast.makeText(this,"Saved", Toast.LENGTH_LONG).show();
        //guardar en txt

        String nomarchivo = "Atencion_"+datetime+".txt";
        File file;
        BufferedWriter write;

        try{
               file = new File(Environment.getExternalStorageDirectory(), nomarchivo);
               file.setReadable(true,false);

               write = new BufferedWriter(new FileWriter(file,true)); // true es para el append
               write.append(valores);
               write.close();



            valores="";
            for (int i=0; i<meditacion.size();i++){
                valores+=meditacion.get(i)+":";
            }
            nomarchivo="Meditacion_"+datetime+".txt";
            file = new File(Environment.getExternalStorageDirectory(), nomarchivo);
            file.setReadable(true,false);
            write = new BufferedWriter(new FileWriter(file,true)); // true es para el append
            write.append(valores);
            write.close();
            Toast.makeText(this,"TXT guardados", Toast.LENGTH_LONG).show();

        }catch (Exception e){
            Toast.makeText(this,"Se guardó en base de datos, pero no se generaron archivos", Toast.LENGTH_LONG).show();
        }
    }
}