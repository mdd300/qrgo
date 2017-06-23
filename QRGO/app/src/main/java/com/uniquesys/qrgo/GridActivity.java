package com.uniquesys.qrgo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GridActivity extends AppCompatActivity implements myInterface {

     private Bitmap bitmap;
    private ImageView image;
    List<Bitmap> splittedBitmaps = new ArrayList<>();
    List<String> splittedid = new ArrayList<>();
    JSONArray JASresult;
    String resultado = null;
    Bitmap result = null;
    String img_test = null;
    int pagina = 1;
    ProgressDialog pd;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);
        pd = ProgressDialog.show(GridActivity.this, "Carregando", "Aguarde...", true, false);
        this.getGridImage();


    }

    public void itemClicked( String id) {
        Log.e("Imagem", id);
        String codigo = id;
        String method = "https://www.uniquesys.com.br/qrgo/pedidos/readqrcodepedido_app";
        String function = "produto";
        Model prodTask = new Model(this);
        prodTask.execute(function,method, codigo);
        String resultado = null;
        try {
            resultado = prodTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(GridActivity.this, ProdutoActivity.class);
        Log.e("Imagem", resultado);
        Bundle bundle = new Bundle();
        bundle.putString("resultado", resultado);
        intent.putExtras(bundle);
        startActivity(intent);

    }
    @Override
    public void startMyIntent(Intent i) {
        startActivity(i);
    }


public void getGridImage(){

    Model prodTask = new Model();
    String method = "https://www.uniquesys.com.br/qrgo/pedidos/grid_listagem";
    String function = "listagem";
    prodTask.execute(function, method, String.valueOf(pagina));

    try {

        resultado = prodTask.get();
        Log.e("Imagem",resultado.toString());
        JASresult = new JSONArray(resultado.toString());
        for (int i = 0; i < JASresult.length(); i++) {
            JSONObject obj = JASresult.getJSONObject(i);
            String img = obj.getString("img_nome");
            String id = obj.getString("prod_uniq");

            try {

                if (!img.equals("null")) {
                    String urlOfImage = "https://www.uniquesys.com.br/qrgo/uploads/produtos/img/" + img;
                    method = urlOfImage;
                    function = "imagem";
                    Imagem imgTask = new Imagem();
                    imgTask.execute(function, method);
                    result = imgTask.get();
                    splittedid.add(id);

                    splittedBitmaps.add(result);

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            img_test = obj.getString("img_nome");
        }
    } catch (InterruptedException e) {
        e.printStackTrace();
    } catch (ExecutionException e) {
        e.printStackTrace();
    } catch (JSONException e) {
        e.printStackTrace();
    }

    this.RefreshGrid(splittedid,splittedBitmaps);
}
public void RefreshGrid(final List<String> splittedid, final List<Bitmap> splittedBitmaps){

    new Thread()
    {

        public void run()
        {

            try
            {
                sleep(1500);
                Fragment fragment = new GridFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("lista", (ArrayList<? extends Parcelable>) splittedBitmaps);
                bundle.putStringArrayList("id", (ArrayList<String>) splittedid);
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();


            }
            catch (Exception e)
            {
                Log.e("tag",e.getMessage());
            }
            pd.dismiss();
        }
    }.start();


}

    public void Pagination(View v) throws ExecutionException, InterruptedException {
        pd = ProgressDialog.show(GridActivity.this, "Carregando", "Aguarde...", true, false);
        pagina++;
        Log.e("Imagem", String.valueOf(pagina));
        this.getGridImage();


    }


}