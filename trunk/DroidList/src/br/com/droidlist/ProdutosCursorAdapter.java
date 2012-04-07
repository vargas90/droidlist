/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.droidlist;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SimpleCursorAdapter;
import java.util.ArrayList;

/**
 *
 * @author rodrigo.ferreira
 */
public class ProdutosCursorAdapter extends SimpleCursorAdapter {

    private Cursor c;
    private Context context;
    private ArrayList<Integer> checkList = new ArrayList<Integer>();

    public ProdutosCursorAdapter(Context context, int layout, Cursor c,
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
            v = inflater.inflate(R.layout.esc_produto_check, null);
        }
        this.c.moveToPosition(pos);
        String produto = this.c.getString(this.c.getColumnIndex("nome"));
        final CheckBox cBox = (CheckBox) v.findViewById(R.esc_produto_check.bcheck);
        cBox.setTag(this.c.getInt(this.c.getColumnIndex("_id")));
        //cBox.setButtonDrawable(R.drawable.carrinho_unchecked);
        cBox.setText(produto);
        cBox.setTextColor(Color.WHITE);

        //TextView tBox = (TextView) v.findViewById(R.esc_produto_check.txcheck);
        //tBox.setText(this.c.getString(this.c.getColumnIndex("categoria")));

        cBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @SuppressWarnings("element-type-mismatch")
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (arg1) {
                    //cBox.setButtonDrawable(R.drawable.carrinho_checked);
                    checkList.add((Integer) cBox.getTag());
                } else {
                    //cBox.setButtonDrawable(R.drawable.carrinho_unchecked);
                    checkList.remove(cBox.getTag());
                }
            }
        });
        return (v);
    }

    public ArrayList<Integer> produtosMarcados() {
        return checkList;
    }
}

