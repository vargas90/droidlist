/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.droidlist;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import br.com.droidlist.banco.Banco;
import br.com.droidlist.categoria.Categoria;
import br.com.droidlist.produto.Produto;

/**
 *
 * @author rodrigo.ferreira
 */
public class CustomCursorAdapter extends SimpleCursorAdapter {

    private Cursor c;
    private Context context;
    private List<Integer> checkList = new ArrayList<Integer>();

    public CustomCursorAdapter(Context context, int layout, Cursor c,
            String[] from, int[] to) {
        super(context, layout, c, from, to);
        this.c = c;
        this.context = context;
    }

    @Override
    public View getView(final int pos, View inView, ViewGroup parent) {
        View v = inView;
        Object i = getItem(pos);
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.check_list, null);
        }

        this.c.moveToPosition(pos);
        String produto = this.c.getString(this.c.getColumnIndex("nome"));
        final CheckBox cBox = (CheckBox) v.findViewById(R.id.bcheck);
        cBox.setTag(this.c.getInt(this.c.getColumnIndex("_id")));
        cBox.setText(produto);
        //cBox.setId(this.c.getInt(this.c.getColumnIndex("_id")));
        //cBox.setButtonDrawable(R.drawable.carrinho_unchecked);
        cBox.setTypeface(Typeface.DEFAULT);
        final Banco db = new Banco(context);
        Cursor cur = db.configs();
        Integer fonte = null;
        while (cur.moveToNext()) {
            fonte = (cur.getInt(cur.getColumnIndex("tam_fonte")));
            Log.i("###olha aki###", String.valueOf(fonte));
        }
        if (fonte == null) {
            db.criarConfigs(10);
            fonte = 10;
            Log.i("###nao existia###", String.valueOf(fonte));
        }
        cBox.setTextSize(fonte);

        final TextView tBox = (TextView) v.findViewById(R.id.txcheck);
        tBox.setText(this.c.getString(this.c.getColumnIndex("categoria")));
        tBox.setTag(this.c.getInt(this.c.getColumnIndex("id_categoria")));
        tBox.setTextSize(fonte);
        //tBox.setId(this.c.getInt(this.c.getColumnIndex("_id")));

        DecimalFormat df = new DecimalFormat("0.00");
        final Double preco = (this.c.getDouble(this.c.getColumnIndex("preco")));
        final TextView txtPreco = (TextView) v.findViewById(R.id.txqtde);
        if (this.c.getDouble(this.c.getColumnIndex("preco")) > 0) {
            txtPreco.setText(df.format(preco));
        } else {
            txtPreco.setText(" ");
        }

        final int idProduto = (this.c.getInt(this.c.getColumnIndex("_id")));
        final int idLista = this.c.getInt(this.c.getColumnIndex("id_lista"));

        if (this.c.getString(this.c.getColumnIndex("flag_marcado")).equals("1")) {
            cBox.setChecked(true);
            //cBox.setButtonDrawable(R.drawable.carrinho_checked);
            cBox.setTypeface(Typeface.DEFAULT_BOLD);
            checkList.add((Integer) cBox.getTag());
            //DroidList d = (DroidList) context;
            //d.atualizaMarcado();
        } else {
            cBox.setChecked(false);
        }


        cBox.setOnClickListener(new View.OnClickListener() {

            @SuppressWarnings("element-type-mismatch")
            public void onClick(View arg0) {
                if (cBox.isChecked()) {
                    //cBox.setButtonDrawable(R.drawable.carrinho_checked);
                    cBox.setTypeface(Typeface.DEFAULT_BOLD);
                    checkList.add((Integer) cBox.getTag());
                    //DroidList d = (DroidList) context;
                    //d.marcaProduto(idProduto, idLista, "1");
                    db.marcaProdutoLista(idProduto, idLista, "1");
                    //d.atualizaLista();
                    //marcaProduto(idProduto, idLista, "1");
                    Log.i("### preco ++", String.valueOf(preco));
                } else if (!cBox.isChecked()) {
                    //cBox.setButtonDrawable(R.drawable.carrinho_unchecked);
                    cBox.setTypeface(Typeface.DEFAULT);
                    checkList.remove(cBox.getTag());
                    //DroidList d = (DroidList) context;
                    //d.marcaProduto(idProduto, idLista, "0");
                    db.marcaProdutoLista(idProduto, idLista, "0");
                    //marcaProduto(idProduto, idLista, "0");
                    Log.i("### preco --", String.valueOf(preco));
                }
                //Toast.makeText(context, String.valueOf(cBox.isChecked()), Toast.LENGTH_LONG).show();

                DroidList d = (DroidList) context;
                d.run();
                //d.onCreate(Bundle.EMPTY);
            }
        });

        cBox.setOnLongClickListener(
                new View.OnLongClickListener() {

                    public boolean onLongClick(View arg0) {
                        DroidList d = (DroidList) context;
                        Produto p = new Produto();
                        p.setId((Integer) cBox.getTag());
                        p.setNome((String) cBox.getText());
                        p.setIdCategoria((Integer) tBox.getTag());
                        if (txtPreco.getText().toString().length() > 0) {
                            p.setPreco(preco);
                        }
                        d.opcoesProduto(p);
                        return true;
                    }
                });

        tBox.setOnLongClickListener(new View.OnLongClickListener() {

            public boolean onLongClick(View arg0) {
                DroidList d = (DroidList) context;
                Categoria c = new Categoria();
                c.setId((Integer) tBox.getTag());
                c.setNome((String) tBox.getText());
                d.dialogoAlteraCategoria(c);
                return true;
            }
        });

        return (v);

    }

    public List<Integer> limpaMarcados() {
        return checkList;
    }
}

