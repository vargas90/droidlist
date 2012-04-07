/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.droidlist;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import br.com.droidlist.banco.Banco;
import br.com.droidlist.categoria.Categoria;
import br.com.droidlist.produto.Produto;

/**
 * 
 * @author Dil
 */
public class DroidList extends Activity implements Runnable {

	Banco db = new Banco(this);
	Integer lista = null;
	CustomCursorAdapter cc;
	ListView princ;
	TextView tLista;
	TextView tCarrinho;
	Double soma;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// princ = (ListView) findViewById(R.main.lista);

		db.criarPesistencia();
		db.fechaBanco();
		// AdManager.setTestDevices(new String[]{
		// AdManager.TEST_EMULATOR, // Android emulator
		// "E83D20734F72FB3108F104ABC0FFC738", // My T-Mobile G1 Test Phone
		// });
		// AdView adView = (AdView) findViewById(R.id.ad);
		// adView.requestFreshAd();
		carregaPrincipal();
		// codigo para recuperar informaÃ§Ãµes da versao do androidmainfest
		// PackageInfo packageInfo = getPackageManager().getPackageInfo("nome da package",0);
		// packageInfo.versionCode (cÃ³digo da versÃ£o)
		// packageInfo.versionName (nome da versÃ£o)
	}

	void carregaPrincipal() {
		setContentView(R.layout.main);
		try {
			final Spinner splista = (Spinner) findViewById(R.main.splista);
			final ArrayList<Lista> strLista = new ArrayList<Lista>();
			Cursor c = db.listarListas();
			if (c.getCount() == 0) {
				db.criarLista(getString(R.string.label_minhalista));
			}
			c = db.listarListas();
			while (c.moveToNext()) {
				Lista l = new Lista();
				l.setId(c.getInt(c.getColumnIndex("_id")));
				l.setNome(c.getString(c.getColumnIndex("nome")));
				strLista.add(l);
			}

			ArrayAdapter<Lista> apLista = new ArrayAdapter<Lista>(DroidList.this, android.R.layout.simple_spinner_item, strLista);
			apLista.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			splista.setAdapter(apLista);

			Lista l = strLista.get(0);
			lista = l.getId();

			splista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {

					Lista l = strLista.get(pos);
					lista = l.getId();
					atualizaLista();
				}

				public void onNothingSelected(AdapterView<?> arg0) {
					throw new UnsupportedOperationException("Not supported yet.");
				}
			});

			Button btProd = (Button) findViewById(R.main.btprod);
			btProd.setOnClickListener(new View.OnClickListener() {

				public void onClick(View arg0) {
					dialogoCadastraProduto(true);
				}
			});

			Button btLimpar = (Button) findViewById(R.main.btlimpar);
			btLimpar.setOnClickListener(new View.OnClickListener() {

				public void onClick(View arg0) {
					limparLista();
				}
			});

			db.fechaBanco();

		} catch (Exception e) {
			Log.e("Carrega Principal", e.toString());
		}
	}

	void dialogoCadastraProduto(boolean botao) {
		try {
			if (botao) {
				final Dialog produto = new Dialog(DroidList.this);
				produto.setTitle(R.string.label_produtoCadastrar);
				produto.setContentView(R.layout.add_produto);

				Spinner spnCategorias = (Spinner) produto.findViewById(R.addproduto.spncategoria);
				final ArrayList<Categoria> strCategoria = new ArrayList<Categoria>();
				Cursor categ = db.listarCategorias();
				final Categoria c = new Categoria();
				while (categ.moveToNext()) {
					Categoria categoria = new Categoria();
					categoria.setId(categ.getInt(categ.getColumnIndex("_id")));
					categoria.setNome(categ.getString(categ.getColumnIndex("nome")));
					strCategoria.add(categoria);
				}
				Categoria cat = new Categoria();
				cat.setId(9999);
				cat.setNome(getString(R.string.label_nova));
				strCategoria.add(cat);
				ArrayAdapter<Categoria> adpCategorias = new ArrayAdapter<Categoria>(DroidList.this, android.R.layout.simple_spinner_item, strCategoria);
				adpCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spnCategorias.setAdapter(adpCategorias);
				spnCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						Categoria c2 = strCategoria.get(arg2);
						if (c2.getId() == 9999) {
							dialogoCadastraCategoria("L");
							produto.dismiss();
						}
						c.setId(c2.getId());
						c.setNome(c2.getNome());
						// Toast.makeText(DroidList.this, "id categoria "+ c.getId() + " - " + c.getNome(),
						// Toast.LENGTH_SHORT).show();
					}

					public void onNothingSelected(AdapterView<?> arg0) {
						throw new UnsupportedOperationException("Not supported yet.");
					}
				});

				Button btcadastrar = (Button) produto.findViewById(R.addproduto.btcadproduto);
				btcadastrar.setOnClickListener(new View.OnClickListener() {

					public void onClick(View arg0) {
						EditText edproduto = (EditText) produto.findViewById(R.addproduto.edproduto);
						EditText edPreco = (EditText) produto.findViewById(R.addproduto.edpreco);
						// EditText edQtde = (EditText) produto.findViewById(R.addproduto.edqtde);
						if (!strCategoria.isEmpty() & edproduto.length() > 0) {
							Produto p = new Produto();
							p.setNome(edproduto.getText().toString().toUpperCase());
							p.setIdCategoria(c.getId());
							if (edPreco.length() > 0) {
								p.setPreco(Double.parseDouble(edPreco.getText().toString()));
							}
							db.insereProduto(p);
							Cursor prods = db.ConsultaUltimoProduto();
							ArrayList<Integer> prod = new ArrayList<Integer>();
							while (prods.moveToNext()) {
								prod.add(prods.getInt(prods.getColumnIndex("_id")));
							}
							db.adicionaProdutoLista(prod.get(0), lista);
							atualizaLista();
							produto.dismiss();
						} else {
							Toast.makeText(DroidList.this, R.string.mensagem_informa_nomeProduto, Toast.LENGTH_SHORT).show();
						}
					}
				});

				Button btcancelar = (Button) produto.findViewById(R.addproduto.btcancelar);
				btcancelar.setOnClickListener(new View.OnClickListener() {

					public void onClick(View arg0) {
						produto.dismiss();
					}
				});

				produto.show();
			} else {
				final Dialog produto = new Dialog(DroidList.this);
				produto.setTitle(R.string.label_produtoCadastrar);
				produto.setContentView(R.layout.add_produto);

				Spinner spnCategorias = (Spinner) produto.findViewById(R.addproduto.spncategoria);
				final ArrayList<Categoria> strCategoria = new ArrayList<Categoria>();
				Cursor categ = db.listarCategorias();
				final Categoria c = new Categoria();
				while (categ.moveToNext()) {
					Categoria categoria = new Categoria();
					categoria.setId(categ.getInt(categ.getColumnIndex("_id")));
					categoria.setNome(categ.getString(categ.getColumnIndex("nome")));
					strCategoria.add(categoria);
				}
				Categoria cat = new Categoria();
				cat.setId(9999);
				cat.setNome(getString(R.string.label_nova));
				strCategoria.add(cat);
				ArrayAdapter<Categoria> adpCategorias = new ArrayAdapter<Categoria>(DroidList.this, android.R.layout.simple_spinner_item, strCategoria);
				adpCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spnCategorias.setAdapter(adpCategorias);
				spnCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						Categoria c2 = strCategoria.get(arg2);
						if (c2.getId() == 9999) {
							dialogoCadastraCategoria("B");
							produto.dismiss();
						}
						c.setId(c2.getId());
						c.setNome(c2.getNome());
						// Toast.makeText(DroidList.this, "id categoria "+ c.getId() + " - " + c.getNome(),
						// Toast.LENGTH_SHORT).show();
					}

					public void onNothingSelected(AdapterView<?> arg0) {
						throw new UnsupportedOperationException("Not supported yet.");
					}
				});

				Button btcadastrar = (Button) produto.findViewById(R.addproduto.btcadproduto);
				btcadastrar.setOnClickListener(new View.OnClickListener() {

					public void onClick(View arg0) {
						EditText edproduto = (EditText) produto.findViewById(R.addproduto.edproduto);
						EditText edPreco = (EditText) produto.findViewById(R.addproduto.edpreco);
						// EditText edQtde = (EditText) produto.findViewById(R.addproduto.edqtde);

						if (!strCategoria.isEmpty() & edproduto.length() > 0) {
							Produto p = new Produto();
							p.setNome(edproduto.getText().toString().toUpperCase());
							p.setIdCategoria(c.getId());
							if (edPreco.length() > 0) {
								p.setPreco(Double.parseDouble(edPreco.getText().toString()));
							}
							db.insereProduto(p);
							produto.dismiss();
						} else {
							Toast.makeText(DroidList.this, R.string.mensagem_informa_nomeProduto, Toast.LENGTH_SHORT).show();
						}
					}
				});

				Button btcancelar = (Button) produto.findViewById(R.addproduto.btcancelar);
				btcancelar.setOnClickListener(new View.OnClickListener() {

					public void onClick(View arg0) {
						produto.dismiss();
					}
				});

				produto.show();
				db.fechaBanco();
			}
		} catch (Exception e) {
			Log.e("Dialogo Cadastra Produto", e.toString());
		}
	}

	void dialogoAlteraProduto(final Produto p) {
		try {
			final Dialog produto = new Dialog(DroidList.this);
			produto.setTitle(R.string.label_produtoAlterar);
			produto.setContentView(R.layout.add_produto);
			final EditText edproduto = (EditText) produto.findViewById(R.addproduto.edproduto);
			edproduto.setText(p.getNome());
			final EditText edpreco = (EditText) produto.findViewById(R.addproduto.edpreco);
			edpreco.setText(String.valueOf(p.getPreco()));

			Spinner spnCategorias = (Spinner) produto.findViewById(R.addproduto.spncategoria);
			final ArrayList<Categoria> strCategoria = new ArrayList<Categoria>();
			Cursor categ = db.listarCategorias();
			final Categoria c = new Categoria();
			while (categ.moveToNext()) {
				Categoria categoria = new Categoria();
				categoria.setId(categ.getInt(categ.getColumnIndex("_id")));
				categoria.setNome(categ.getString(categ.getColumnIndex("nome")));
				strCategoria.add(categoria);
			}

			final ArrayAdapter<Categoria> adpCategorias = new ArrayAdapter<Categoria>(DroidList.this, android.R.layout.simple_spinner_item, strCategoria);
			adpCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spnCategorias.setAdapter(adpCategorias);
			spnCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Categoria c2 = strCategoria.get(arg2);
					c.setId(c2.getId());
					c.setNome(c2.getNome());
				}

				public void onNothingSelected(AdapterView<?> arg0) {
					throw new UnsupportedOperationException("Not supported yet.");
				}
			});

			Button btcadastrar = (Button) produto.findViewById(R.addproduto.btcadproduto);
			btcadastrar.setText(R.string.label_alterar);
			btcadastrar.setOnClickListener(new View.OnClickListener() {

				public void onClick(View arg0) {
					// EditText edproduto = (EditText) produto.findViewById(R.addproduto.edproduto);
					if (!adpCategorias.isEmpty() & edproduto.length() > 0) {
						if (edpreco.getText().toString().length() > 0) {
							p.setPreco(Double.parseDouble(edpreco.getText().toString()));
						}
						p.setNome(edproduto.getText().toString().toUpperCase());
						p.setIdCategoria(c.getId());
						db.alteraProduto(p);
						atualizaLista();
						produto.dismiss();
					} else {
						Toast.makeText(DroidList.this, R.string.mensagem_informa_nomeProduto, Toast.LENGTH_SHORT).show();
					}
				}
			});

			Button btcancelar = (Button) produto.findViewById(R.addproduto.btcancelar);
			btcancelar.setOnClickListener(new View.OnClickListener() {

				public void onClick(View arg0) {
					produto.dismiss();
				}
			});

			produto.show();
			db.fechaBanco();
		} catch (Exception e) {
			Log.e("Dialogo Altera Produto", e.toString());
		}
	}

	void dialogoCadastraCategoria(final String fonte) {
		try {
			final Dialog dialogo = new Dialog(DroidList.this);
			dialogo.setTitle(R.string.label_categoriaCadastrar);
			dialogo.setContentView(R.layout.add_categ);
			Button btcancelar = (Button) dialogo.findViewById(R.addcateg.btcancelar);
			btcancelar.setOnClickListener(new View.OnClickListener() {

				public void onClick(View arg0) {
					dialogo.dismiss();
				}
			});

			Button btcadastrar = (Button) dialogo.findViewById(R.addcateg.btcadcateg);
			btcadastrar.setOnClickListener(new View.OnClickListener() {

				public void onClick(View arg0) {
					EditText edcateg = (EditText) dialogo.findViewById(R.addcateg.edcateg);
					if (edcateg.length() > 0) {
						db.criarCategoria(edcateg.getText().toString().toUpperCase());
						db.fechaBanco();
						dialogo.dismiss();

						// fonte L = adiciona ao banco e na lista, B = adiciona no banco para escolha futura, M =
						// adiciona a lista
						if (!fonte.equals("M")) {
							if (fonte.equals("L")) {
								dialogoCadastraProduto(true);
							} else if (fonte.equals("B")) {
								dialogoCadastraProduto(false);
							}
						}

					} else {
						Toast.makeText(DroidList.this, R.string.mensagem_informa_campoVazio, Toast.LENGTH_SHORT).show();
					}
				}
			});
			dialogo.show();
		} catch (Exception e) {
			Log.e("Dialogo Adiciona Categoria", e.toString());
		}
	}

	void dialogoAlteraCategoria(final Categoria categoria) {
		try {
			final Dialog dialogo = new Dialog(DroidList.this);
			dialogo.setTitle(R.string.label_categoriaAlterar);
			dialogo.setContentView(R.layout.add_categ);
			Button btcancelar = (Button) dialogo.findViewById(R.addcateg.btcancelar);
			final EditText edcateg = (EditText) dialogo.findViewById(R.addcateg.edcateg);
			edcateg.setText(categoria.getNome());

			btcancelar.setOnClickListener(new View.OnClickListener() {

				public void onClick(View arg0) {
					dialogo.dismiss();
				}
			});

			Button btcadastrar = (Button) dialogo.findViewById(R.addcateg.btcadcateg);
			btcadastrar.setText(R.string.label_alterar);
			btcadastrar.setOnClickListener(new View.OnClickListener() {

				public void onClick(View arg0) {
					if (edcateg.length() > 0) {
						db.alteraCategoria(categoria.getId(), edcateg.getText().toString().toUpperCase());
						atualizaLista();
						dialogo.dismiss();
					} else {
						Toast.makeText(DroidList.this, R.string.mensagem_informa_campoVazio, Toast.LENGTH_SHORT).show();
					}
				}
			});
			dialogo.show();
			db.fechaBanco();
		} catch (Exception e) {
			Log.e("Dialogo Altera Categoria", e.toString());
		}
	}

	void dialogoCadastraLista() {
		try {
			final Dialog dialogo = new Dialog(DroidList.this);
			dialogo.setTitle(R.string.label_adicionaLista);
			dialogo.setContentView(R.layout.add_lista);
			Button btcancelar = (Button) dialogo.findViewById(R.addlista.btcancelar);
			btcancelar.setOnClickListener(new View.OnClickListener() {

				public void onClick(View arg0) {
					dialogo.dismiss();
				}
			});

			Button btcadastrar = (Button) dialogo.findViewById(R.addlista.btcadlista);
			btcadastrar.setOnClickListener(new View.OnClickListener() {

				public void onClick(View arg0) {
					EditText ed = (EditText) dialogo.findViewById(R.addlista.edlista);
					if (ed.length() > 0) {
						db.criarLista(ed.getText().toString().toUpperCase());
						dialogo.dismiss();
						carregaPrincipal();
					} else {
						Toast.makeText(DroidList.this, R.string.erro_cadlista, Toast.LENGTH_SHORT).show();
					}
				}
			});

			dialogo.show();
			db.fechaBanco();

		} catch (Exception e) {
			Log.e("Dialogo Adiciona Lista", e.toString());
		}
	}

	void dialogoEscolheProduto() {
		try {

			final Dialog dialogo = new Dialog(DroidList.this);
			dialogo.setTitle(R.string.label_produtos);
			dialogo.setContentView(R.layout.esc_produto);
			// cursor para preencher a lista
			Cursor c = db.listarProdutos();
			final ArrayList<Produto> lstProd = new ArrayList<Produto>();
			while (c.moveToNext()) {
				Produto p = new Produto();
				p.setId(c.getInt(c.getColumnIndex("_id")));
				p.setIdCategoria(c.getInt(c.getColumnIndex("id_categoria")));
				p.setNome(c.getString(c.getColumnIndex("nome")));
				lstProd.add(p);
			}
			final ListAdapter la = new ArrayAdapter(DroidList.this, android.R.layout.simple_list_item_checked, lstProd);
			final ListView lst = (ListView) dialogo.findViewById(R.esc_produto.lstproduto);

			// Cursor para preencher o spinner
			Cursor categorias = db.listarCategorias();
			final ArrayList<Categoria> lstCateg = new ArrayList<Categoria>();
			while (categorias.moveToNext()) {
				Categoria categ = new Categoria();
				categ.setId(categorias.getInt(categorias.getColumnIndex("_id")));
				Cursor qtde = db.listarProdutos(categorias.getInt(categorias.getColumnIndex("_id")), lista);
				categ.setNome(categorias.getString(categorias.getColumnIndex("nome")) + " (" + qtde.getCount() + ")");
				lstCateg.add(categ);
			}
			final Spinner sp = (Spinner) dialogo.findViewById(R.esc_produto.splista);
			ArrayAdapter<Categoria> spla = new ArrayAdapter<Categoria>(DroidList.this, android.R.layout.simple_spinner_item, lstCateg);
			spla.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp.setAdapter(spla);

			String[] campos = new String[] { "_id" };
			int[] xml = new int[] { R.esc_produto_check.bcheck };
			final ProdutosCursorAdapter prodcur = new ProdutosCursorAdapter(this, R.layout.esc_produto_check, db.listarProdutos(), campos, xml);
			lst.setAdapter(prodcur);

			sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
					Categoria categ = lstCateg.get(pos);
					String[] campos = new String[] { "_id" };
					int[] xml = new int[] { R.esc_produto_check.bcheck };
					lst.setAdapter(new ProdutosCursorAdapter(DroidList.this, R.layout.esc_produto_check, db.listarProdutos(categ.getId(), lista), campos, xml));
				}

				public void onNothingSelected(AdapterView<?> arg0) {
					throw new UnsupportedOperationException("Not supported yet.");
				}
			});

			Button btadd = (Button) dialogo.findViewById(R.esc_produto.btproduto);
			btadd.setOnClickListener(new View.OnClickListener() {

				public void onClick(View arg0) {
					try {
						ProdutosCursorAdapter prods = (ProdutosCursorAdapter) lst.getAdapter();
						if (!prods.produtosMarcados().isEmpty()) {
							for (Integer p : prods.produtosMarcados()) {
								db.adicionaProdutoLista(p, lista);
								// Toast.makeText(DroidList.this, "produto:" + p, Toast.LENGTH_SHORT).show();
							}
							Toast.makeText(DroidList.this, R.string.mensagem_informa_produtoAdicionado, Toast.LENGTH_SHORT).show();
							atualizaLista();
							dialogo.dismiss();
						} else {
							Toast.makeText(DroidList.this, R.string.mensagem_informa_selecionarProduto, Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						Log.e("add produto", "Erro: " + e.toString());
					}
				}
			});

			dialogo.show();
			db.fechaBanco();

		} catch (Exception e) {
			Log.e("Dialogo Escolhe Produto", e.toString());
		}
	}

	public void limparLista() {
		List<Integer> prods = cc.limpaMarcados();
		if (!prods.isEmpty() && prods != null) {
			try {
				for (Integer p : prods) {
					db.removeProdutoLista(p, lista);
					db.fechaBanco();
				}
				atualizaLista();
				Log.i("Limpar Lista", "Produtos removidos da lista!");
			} catch (Exception e) {
				Log.e("Limpar Lista", "Erro: " + e.toString());
			}
		} else {
			Toast.makeText(this, R.string.mensagem_informa_itemMarcado, Toast.LENGTH_SHORT).show();
		}
	}

	public void resetaBanco() {
		try {
			AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
			dialogo.setTitle(R.string.label_aviso);
			dialogo.setMessage(R.string.mensagem_informa_confirmaResetBanco);
			dialogo.setPositiveButton(R.string.label_sim, new OnClickListener() {

				public void onClick(DialogInterface arg0, int arg1) {
					db.apagaTudo();
					db.criarPesistencia();
					db.fechaBanco();
					// atualizaLista();
					Toast.makeText(DroidList.this, R.string.mensagem_informa_bancoResetado, Toast.LENGTH_SHORT).show();
					carregaPrincipal();
				}
			});
			dialogo.setNegativeButton(R.string.label_nao, null);
			dialogo.show();
			Log.i("Reseta Banco", "OK");
		} catch (Exception e) {
			Log.e("Reseta Banco", "Erro: " + e.toString());
		}
	}

	public void dialogoApagaCategoria() {
		try {
			final Dialog dialogo = new Dialog(this);
			dialogo.setContentView(R.layout.exc_categ);
			dialogo.setTitle(R.string.label_categoriaApagar);
			final ArrayList<Categoria> categorias = new ArrayList<Categoria>();
			Cursor c = db.listarCategorias();
			while (c.moveToNext()) {
				Categoria categ = new Categoria();
				Cursor count = db.listarProdutos(c.getInt(c.getColumnIndex("_id")), true);
				categ.setId(c.getInt(c.getColumnIndex("_id")));
				categ.setNome(c.getString(c.getColumnIndex("nome")) + " (" + count.getCount() + ")");
				categorias.add(categ);
			}
			final Categoria categoria = new Categoria();
			ArrayAdapter<Categoria> adp = new ArrayAdapter<Categoria>(DroidList.this, android.R.layout.simple_spinner_item, categorias);
			adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			Spinner spCateg = (Spinner) dialogo.findViewById(R.exccateg.spcateg);
			spCateg.setAdapter(adp);
			spCateg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
					Categoria c = categorias.get(pos);
					categoria.setId(c.getId());
					categoria.setNome(c.getNome());
				}

				public void onNothingSelected(AdapterView<?> arg0) {
					throw new UnsupportedOperationException("Not supported yet.");
				}
			});

			Button btExcluir = (Button) dialogo.findViewById(R.exccateg.btexccateg);
			btExcluir.setOnClickListener(new View.OnClickListener() {

				public void onClick(View arg0) {
					Cursor c = db.listarProdutos(categoria.getId(), true);
					if (c.getCount() == 0) {
						db.apagaCategoria(categoria.getId());
						dialogo.dismiss();
					} else {
						Toast.makeText(DroidList.this, R.string.mensagem_informa_categoriaNaoExcluida, Toast.LENGTH_SHORT).show();
					}
				}
			});

			Button btCancelar = (Button) dialogo.findViewById(R.exccateg.btcancelar);
			btCancelar.setOnClickListener(new View.OnClickListener() {

				public void onClick(View arg0) {
					dialogo.dismiss();
				}
			});
			dialogo.show();
			db.fechaBanco();
		} catch (Exception e) {
		}
	}

	public void dialogoTamanhoFont() {
		try {
			AlertDialog.Builder dialogo = new AlertDialog.Builder(DroidList.this);
			List<String> opcao = new ArrayList<String>();
			opcao.add("Pequena");
			opcao.add("Media");
			opcao.add("Grande");
			ArrayAdapter<String> aStr = new ArrayAdapter<String>(DroidList.this, android.R.layout.select_dialog_singlechoice, opcao);
			final CheckBox cBox = (CheckBox) findViewById(R.id.bcheck);
			dialogo.setAdapter(aStr, new OnClickListener() {

				public void onClick(DialogInterface arg0, int pos) {
					switch (pos) {
					case 0:
						db.alteraFonte(10);
						db.fechaBanco();
						atualizaLista();
						break;
					case 1:
						db.alteraFonte(15);
						db.fechaBanco();
						atualizaLista();
						break;
					case 2:
						db.alteraFonte(20);
						db.fechaBanco();
						atualizaLista();
						break;
					default:
						break;
					}

				}
			});

			dialogo.show();
		} catch (Exception e) {
		}
	}

	public void apagaLista() {
		AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
		dialogo.setTitle(R.string.label_aviso);
		dialogo.setMessage(R.string.mensagem_confirma_exclusaoLista);
		dialogo.setPositiveButton(R.string.label_sim, new OnClickListener() {

			public void onClick(DialogInterface arg0, int arg1) {
				db.apagaLista(lista);
				carregaPrincipal();
			}
		});
		dialogo.setNegativeButton(R.string.label_nao, null);

		dialogo.show();
	}

	public void opcoesProduto(final Produto produto) {
		try {
			AlertDialog.Builder dialogo = new AlertDialog.Builder(DroidList.this);
			dialogo.setTitle(R.string.label_acoes);
			String[] itens = new String[] { this.getString(R.string.label_editarItem), this.getString(R.string.label_removerLista), this.getString(R.string.label_eliminaItem), this.getString(R.string.label_marcaItem),
					this.getString(R.string.label_desmarcaItem) };

			dialogo.setItems(itens, new OnClickListener() {

				public void onClick(DialogInterface arg0, int pos) {
					switch (pos) {
					case 0:
						dialogoAlteraProduto(produto);
						break;
					case 1:
						db.removeProdutoLista(produto.getId(), lista);
						atualizaLista();
						break;
					case 2:
						AlertDialog.Builder dialogo = new AlertDialog.Builder(DroidList.this);
						dialogo.setTitle(R.string.label_aviso);
						dialogo.setMessage(R.string.mensagem_confirma_exclusaoBanco);
						dialogo.setPositiveButton(R.string.label_sim, new OnClickListener() {

							public void onClick(DialogInterface arg0, int arg1) {
								db.apagaProduto(produto.getId());
								atualizaLista();
							}
						});
						dialogo.setNegativeButton(R.string.label_nao, null);
						dialogo.show();
						break;
					case 3:
						marcaProduto(produto.getId(), lista, "1");
						break;
					case 4:
						marcaProduto(produto.getId(), lista, "0");
						break;
					default:
						break;
					}
				}
			}).show();
		} catch (Exception e) {
			Log.e("Opções Produto", "Erro: " + e.toString());
		}

	}

	// mensagens pÃ³s banco de dados
	public void msgCriarCategoria() {
		Toast.makeText(this, R.string.mensagem_informa_categoriaCriada, Toast.LENGTH_SHORT).show();
	}

	public void msgCriarLista() {
		Toast.makeText(this, R.string.mensagem_informa_listaCriada, Toast.LENGTH_SHORT).show();
	}

	public void msgInsereProduto() {
		Toast.makeText(this, R.string.mensagem_informa_produtoInserido, Toast.LENGTH_SHORT).show();
	}

	public void msgApagaProduto() {
		Toast.makeText(this, R.string.mensagem_informa_produtoExcluido, Toast.LENGTH_SHORT).show();
	}

	public void msgAdicionaProdutoLista() {
		Toast.makeText(this, R.string.mensagem_informa_produtoAdicionado, Toast.LENGTH_SHORT).show();
	}

	public void msgApagaLista() {
		Toast.makeText(this, R.string.mensagem_informa_listaExcluida, Toast.LENGTH_LONG).show();
	}

	public void msgApagaCategoria() {
		Toast.makeText(this, R.string.mensagem_informa_categoriaExcluida, Toast.LENGTH_SHORT).show();
	}

	public void msgRemoveProdutoLista() {
		Toast.makeText(this, R.string.mensagem_informa_listaProdutoRemovido, Toast.LENGTH_SHORT).show();
	}

	public void msgAlteraProduto() {
		Toast.makeText(this, R.string.mensagem_informa_produtoAlterado, Toast.LENGTH_SHORT).show();
	}

	public void msgAlteraCategoria() {
		Toast.makeText(this, R.string.mensagem_informa_categoriaAlterada, Toast.LENGTH_SHORT).show();
	}

	public void atualizaLista() {
		String[] campos = new String[] { "_id", "nome" };
		int[] xml = new int[] { R.id.bcheck, R.id.txcheck };
		cc = new CustomCursorAdapter(DroidList.this, R.layout.check_list, db.listarProdutos(lista), campos, xml);
		// setListAdapter(cc);
		// princ.setItemsCanFocus(true);
		// princ.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		princ = (ListView) findViewById(R.main.lista);
		princ.setAdapter(cc);
		db.fechaBanco();
		tLista = (TextView) findViewById(R.main.txlista);
		tCarrinho = (TextView) findViewById(R.main.txcarrinho);

		Cursor sum = db.sumProdutosLista(lista);
		double preco = 0;
		while (sum.moveToNext()) {
			preco = sum.getDouble(sum.getColumnIndex("preco"));
		}
		db.fechaBanco();
		if (preco > 0) {
			DecimalFormat df = new DecimalFormat("#.00");
			tLista.setText("  " + this.getString(R.string.label_totalLista) + " " + df.format(preco));
		} else {
			tLista.setText("");
		}

		atualizaMarcado();
	}

	public void atualizaMarcado() {
		Cursor sumMarcados = db.sumProdutosMarcadosLista(lista);
		while (sumMarcados.moveToNext()) {
			soma = sumMarcados.getDouble(sumMarcados.getColumnIndex("preco"));
		}
		db.fechaBanco();

		if (soma > 0) {
			DecimalFormat df = new DecimalFormat("#.00");
			Log.i("### soma", String.valueOf(soma));
			tCarrinho.setText("  " + this.getString(R.string.label_totalCarrinho) + " " + df.format(soma));
		} else {
			Log.i("### soma", String.valueOf(soma));
			tCarrinho.setText("");
		}
	}

	public void totalMarcado(Double preco, String operacao) {
		if (preco != null) {
			DecimalFormat df = new DecimalFormat("0.00");
			try {
				tCarrinho = (TextView) findViewById(R.main.txcarrinho);
				if (operacao.equals("+")) {
					soma += preco;
				} else if (operacao.equals("-")) {
					soma -= preco;
				}

				if (soma > 0) {
					// DecimalFormat df = new DecimalFormat("#.00");
					Log.i("### soma", String.valueOf(soma));
					tCarrinho.setText("  " + this.getString(R.string.label_totalCarrinho) + " " + df.format(soma));
				} else {
					Log.i("### soma", String.valueOf(soma));
					tCarrinho.setText("");
				}

			} catch (Exception e) {
				Log.e("Add Total Marcado", "Erro: " + e.toString());
			}
		}

	}

	public void marcaProduto(int idProduto, int idLista, String flag) {
		try {
			db.marcaProdutoLista(idProduto, idLista, flag);
			Log.i("Marca Produto", "Ok");
			atualizaLista();
		} catch (Exception e) {
			Log.e("Marca Produto", "Erro: " + e.toString());
		}
	}

	public void desmarcaProduto(int idProduto, int idLista) {
		try {
			db.desmarcaProdutoLista(idProduto, idLista);
			Log.i("Desmarca Produto", "Ok");
		} catch (Exception e) {
			Log.e("Desmarca Produto", "Erro: " + e.toString());
		}

	}

	public void enviarEmail() {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("text/html");

		Cursor c = db.listarProdutos(lista);
		StringBuffer produtos = new StringBuffer();
		String sLista = "";
		DecimalFormat df = new DecimalFormat(getString(R.string.label_modeda) + "0.00");

		produtos.append("Produto - Preço" + "\n");
		while (c.moveToNext()) {
			produtos.append((c.getString(c.getColumnIndex("nome")) + " - " + df.format(c.getDouble(c.getColumnIndex("preco")))) + "\n");
			sLista = "DroidList - " + c.getString(c.getColumnIndex("lista"));

		}

		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, sLista);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, produtos.toString());
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "" });

		DroidList.this.startActivity(Intent.createChooser(emailIntent, getString(R.string.mensagem_enviar_email)));
	}

	public void sobre() {
		AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
		dialogo.setTitle(getString(R.string.app_name));
		dialogo.setIcon(R.drawable.icon);
		dialogo.setMessage(getString(R.string.mensagem_sobre));
		dialogo.setNeutralButton("OK", null);
		dialogo.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		db.fechaBanco();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_addprod:
			dialogoCadastraProduto(false);
			return true;
			// startActivity(new Intent(DroidList.this, Categoria.class));
		case R.id.menu_addcateg:
			dialogoCadastraCategoria("M");
			return true;
		case R.id.menu_addlista:
			dialogoCadastraLista();
			return true;
		case R.id.menu_addprodlista:
			dialogoEscolheProduto();
			return true;
		case R.id.menu_sub_apagaLista:
			apagaLista();
			return true;
		case R.id.menu_sub_resetaBanco:
			resetaBanco();
			return true;
		case R.id.menu_sub_apagaCatgoria:
			dialogoApagaCategoria();
			return true;
		case R.id.menu_sub_tamanhoLetra:
			dialogoTamanhoFont();
			return true;
		case R.id.menu_sub_email:
			enviarEmail();
			return true;
		case R.id.menu_sub_sobre:
			sobre();
			return true;
		default:
			return true;
		}

	}

//	@Override
//	public void onBackPressed() {
//		AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
//		dialogo.setIcon(R.drawable.interruptor);
//		dialogo.setTitle(R.string.label_aviso);
//		dialogo.setMessage(R.string.mensagem_confirma_sair);
//		dialogo.setPositiveButton(R.string.label_sim, new OnClickListener() {
//
//			public void onClick(DialogInterface arg0, int arg1) {
//				finish();
//			}
//		});
//		dialogo.setNegativeButton(R.string.label_nao, null);
//
//		dialogo.show();
//	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
			dialogo.setIcon(R.drawable.interruptor);
			dialogo.setTitle(R.string.label_aviso);
			dialogo.setMessage(R.string.mensagem_confirma_sair);
			dialogo.setPositiveButton(R.string.label_sim, new OnClickListener() {

				public void onClick(DialogInterface arg0, int arg1) {
					finish();
				}
			});
			dialogo.setNegativeButton(R.string.label_nao, null);

			dialogo.show();

		}
		//return super.onKeyDown(keyCode, event);
		return false;
	}

	public void run() {
		String[] campos = new String[] { "_id", "nome" };
		int[] xml = new int[] { R.id.bcheck, R.id.txcheck };
		cc = new CustomCursorAdapter(DroidList.this, R.layout.check_list, db.listarProdutos(lista), campos, xml);
		// setListAdapter(cc);
		// princ.setItemsCanFocus(true);
		// princ.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		princ = (ListView) findViewById(R.main.lista);
		princ.setAdapter(cc);
		db.fechaBanco();
		tLista = (TextView) findViewById(R.main.txlista);
		tCarrinho = (TextView) findViewById(R.main.txcarrinho);

		Cursor sum = db.sumProdutosLista(lista);
		double preco = 0;
		while (sum.moveToNext()) {
			preco = sum.getDouble(sum.getColumnIndex("preco"));
		}
		db.fechaBanco();
		if (preco > 0) {
			DecimalFormat df = new DecimalFormat("#.00");
			tLista.setText("  " + this.getString(R.string.label_totalLista) + " " + df.format(preco));
		} else {
			tLista.setText("");
		}

		atualizaMarcado();
	}
}
