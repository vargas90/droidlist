package br.com.droidlist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 *
 * @author Dil
 */
public class Banco {

    private Context context;
    private SQLiteDatabase db2 = null;
    private static String BANCO = "DroidListDB";
    private static String TABELA_PRODUTO = "CREATE TABLE IF NOT EXISTS produto (_id INTEGER NOT NULL PRIMARY KEY autoincrement , "
            + "nome varchar2(30), preco real, id_categoria INTEGER);";
    private static String TABELA_CATEGORIA = "create table if not exists categoria (_id INTEGER NOT NULL PRIMARY KEY autoincrement , "
            + "nome varchar2(30));";
    private static String TABELA_LISTA = "create table if not exists lista (_id integer not null primary key autoincrement,"
            + "nome varchar2(30));";
    private static String TABELA_PROD_LISTA = "create table if not exists prod_lista (_id_prod integer not null,"
            + "id_lista integer not null, qtde integer, flag_marcado varchar2(1));";
    private static String TABELA_CONFIGS = "create table if not exists configs (tam_fonte integer);";

    public Banco(Context context) {
        this.context = context;

    }

    public void fechaBanco() {
        db2.close();
    }

    public void criarPesistencia() {
        criarBanco();
        criarTabela(TABELA_PRODUTO);
        criarTabela(TABELA_CATEGORIA);
        criarTabela(TABELA_LISTA);
        criarTabela(TABELA_PROD_LISTA);
        criarTabela(TABELA_CONFIGS);
    }

    public void criarBanco() {
        try {
            this.db2 = context.openOrCreateDatabase(BANCO, 0, null);
            Log.i("Criar Banco", "Banco " + BANCO + " Criado com Sucesso");
        } catch (Exception e) {
            Log.e("Criar Banco", e.toString());
        }
    }

    public void criarTabela(String sql) {
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            db2.execSQL(sql);
            Log.i("Criar Tabela", "Criou a tabela " + sql);
        } catch (Exception e) {
            Log.e("Criar Tabela", "Nao criou a tabela " + sql);
        }
    }

    public void apagaTudo() {
        try {
            String sql = "drop table prod_lista;";
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            db2.execSQL(sql);
            sql = "drop table categoria";
            db2.execSQL(sql);
            sql = "drop table produto";
            db2.execSQL(sql);
            sql = "drop table lista";
            db2.execSQL(sql);
            Log.i("Apaga Tudo", "OK");
        } catch (Exception e) {
            Log.e("Apaga Tudo", "Erro: " + e.toString());
        }
    }

    public void criarCategoria(String nome) {
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            String sql = "insert into categoria (nome) values ("
                    + "'" + nome + "')";
            db2.execSQL(sql);
            main dl = (main) context;
            dl.msgCriarCategoria();
            Log.i("Crira Categoria", "Inseriu Categoria com sucesso " + sql);
        } catch (Exception e) {
            Log.e("Criar Categoria", "Erro ao inserir categoria " + e.toString());
        }
    }

    public void criarConfigs(int fonte) {
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            String sql = "insert into configs (tam_fonte) values ("
                    + fonte + ")";
            db2.execSQL(sql);
            Log.i("Crira Configs", "Criou com sucesso " + sql);
        } catch (Exception e) {
            Log.e("Criar Configs", "Erro" + e.toString());
        }
    }

    public void criarLista(String nome) {
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            String sql = "insert into lista (nome) values ("
                    + "'" + nome + "')";
            db2.execSQL(sql);
            main dl = (main) context;
            dl.msgCriarLista();
            Log.i("Criar Lista", "Criou Lista com sucesso " + sql);
        } catch (Exception e) {
            Log.e("Criar Lista", "Erro ao criar Lista " + e.toString());
        }
    }

    public void insereProduto(Produto produto) {
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            String sql = "insert into produto (nome, id_categoria, preco) values ("
                    + "'" + produto.getNome() + "'," + produto.getIdCategoria() + ", " + produto.getPreco() + ")";
            db2.execSQL(sql);
            main dl = (main) context;
            dl.msgInsereProduto();
            Log.i("Insere Produto", "Inseriu Produto com sucesso " + sql);
        } catch (Exception e) {
            Log.e("Insere Produto", "Erro ao inserir produto " + e.toString());
        }
    }

    public void apagaProduto(int id) {
        try {
            removeProdutoLista(id);
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            String sql = "delete from produto where _id = " + id;
            db2.execSQL(sql);
            main dl = (main) context;
            dl.msgApagaProduto();
            Log.i("Apaga Produto", "Excluiu Produto com sucesso " + sql);
        } catch (Exception e) {
            Log.e("Apaga Produto", "Erro ao excluir produto " + e.toString());
        }
    }

    public void adicionaProdutoLista(int idProduto, int idLista) {
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            String sql = "insert into prod_lista (_id_prod, id_lista, flag_marcado) values ("
                    + idProduto + "," + idLista + ", '0')";
            db2.execSQL(sql);
            Log.i("Adiciona Prod. Lista", "Criou Lista com sucesso " + sql);
        } catch (Exception e) {
            Log.e("Adiciona Prod. Lista", "Erro ao criar Lista " + e.toString());
        }
    }

    public void apagaLista(int id) {
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            String sql = "delete from prod_lista where id_Lista =" + id;
            db2.execSQL(sql);
            sql = "delete from lista where _id = " + id;
            db2.execSQL(sql);
            main dl = (main) context;
            dl.msgApagaLista();
            Log.i("Apaga Lista", "Lista apagada " + id);
        } catch (Exception e) {
            Log.e("Apaga Lista", "Erro: " + id + " - " + e.toString());
        }
    }

    public void apagaCategoria(int id) {
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            String sql = "delete from categoria where _id = " + id;
            db2.execSQL(sql);
            main dl = (main) context;
            dl.msgApagaCategoria();
            Log.i("Apaga Categoria", "Categoria apagada " + id);
        } catch (Exception e) {
            Log.e("Apaga Categoria", "Erro: " + id + " - " + e.toString());
        }
    }

    public void removeProdutoLista(int idProduto, int idLista) {
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            String sql = "delete from prod_lista where id_Lista =" + idLista + " and _id_prod = " + idProduto;
            db2.execSQL(sql);
            Log.i("Remove Produto Lista", "Produto: " + idProduto + " removido da lista: " + idLista);
        } catch (Exception e) {
            Log.e("Remove Produto Lista", "Erro: " + idProduto + " - " + idLista + " - " + e.toString());
        }
    }

    public void removeProdutoLista(int idProduto) {
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            String sql = "delete from prod_lista where _id_prod = " + idProduto;
            db2.execSQL(sql);
            Log.i("Remove Produto Lista", "Produto removido de todas as listas: " + idProduto);
        } catch (Exception e) {
            Log.e("Remove Produto Lista", "Erro: " + idProduto + " - " + e.toString());
        }
    }

    public void marcaProdutoLista(int idProduto, int idLista, String flag) {
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            String sql = "update prod_lista set flag_marcado = '" + flag + "' where _id_prod = " + idProduto + " and id_lista = " + idLista;
            db2.execSQL(sql);
            Log.i("Marca Produto Lista", "Produto Marcado: " + idProduto + " - Lista " + idLista + " - flag: " + flag);
        } catch (Exception e) {
            Log.e("Marca Produto Lista", "Erro: " + e.toString());
        }
    }

    public void desmarcaProdutoLista(int idProduto, int idLista) {
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            String sql = "update prod_lista set flag_marcado = '0' where _id_prod = " + idProduto + " and id_lista = " + idLista;
            db2.execSQL(sql);
            Log.i("Desmarca Produto Lista", "Produto Desmarcado: " + idProduto + " - Lista " + idLista);
        } catch (Exception e) {
            Log.e("Desmarca Produto Lista", "Erro: " + e.toString());
        }
    }

    public void alteraProduto(Produto produto) {
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            String sql = " update produto "
                    + " set nome = '" + produto.getNome()
                    + "', id_categoria = " + produto.getIdCategoria()
                    + " , preco = " + produto.getPreco()
                    + " where _id = " + produto.getId();
            db2.execSQL(sql);
            main dl = (main) context;
            dl.msgAlteraProduto();
            Log.i("Altera Produto", "Produto alterado com sucesso: " + sql);
        } catch (Exception e) {
            Log.e("Altera Produto", "Erro " + e.toString());
        }
    }

    public void alteraCategoria(int id, String nome) {
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            String sql = " update categoria "
                    + " set nome = '" + nome
                    + "' where _id = " + id;
            db2.execSQL(sql);
            main dl = (main) context;
            dl.msgAlteraCategoria();
            Log.i("Altera Categoria", "Categoria alterada id: " + id + " - nome: " + nome);
        } catch (Exception e) {
            Log.e("Altera Categoria", "Erro " + e.toString());
        }
    }

    public void alteraFonte(int tamanho) {
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            String sql = " update configs "
                    + " set tam_fonte = " + tamanho;
            db2.execSQL(sql);
            Log.i("Altera Fonte", "Fonte alterada Tamanho: " + tamanho);
        } catch (Exception e) {
            Log.e("Altera Fonte", "Erro " + e.toString());
        }
    }

    public Cursor listarProdutos(int idLista) {
        String[] colunas = {"produto._id as _id", "produto.preco as preco", "prod_lista._id_prod,"
            + "prod_lista.id_lista as id_lista", "produto.nome nome", "lista.nome as lista,"
            + "categoria.nome as categoria, categoria._id as id_categoria, prod_lista.flag_marcado as flag_marcado"};
        String where = " prod_lista._id_prod = produto._id"
                + " and produto.id_categoria = categoria._id"
                + " and prod_lista.id_lista = lista._id"
                + " and prod_lista.id_lista = " + idLista;
        Cursor c = null;
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            c = db2.query(true, "prod_lista, produto, lista, categoria", colunas, where, null, null, null, "produto.nome", null);
            Log.i("Listar Produtos(idLista)", "Ok");
        } catch (Exception e) {
            Log.e("Listar Produtos(idLista)", "Erro no cursor: " + e.toString());
        }
        return c;
    }

    public Cursor listarProdutosNaoEscolhidos(int idLista) {
        String[] colunas = {"produto._id as _id", "produto.preco as preco", "prod_lista._id_prod,"
            + "prod_lista.id_lista as id_lista", "produto.nome nome", "lista.nome as lista,"
            + "categoria.nome as categoria, categoria._id as id_categoria, prod_lista.flag_marcado as flag_marcado"};
        String where = " prod_lista._id_prod = produto._id"
                + " and produto.id_categoria = categoria._id"
                + " and prod_lista.id_lista = lista._id"
                + " and prod_lista.id_lista = " + idLista;
        Cursor c = null;
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            c = db2.query(true, "prod_lista, produto, lista, categoria", colunas, where, null, null, null, "produto.nome", null);
            Log.i("Listar Produtos(idLista)", "Ok");
        } catch (Exception e) {
            Log.e("Listar Produtos(idLista)", "Erro no cursor: " + e.toString());
        }
        return c;
    }

    public Cursor configs(){
      String[] colunas = {"tam_fonte"};
      Cursor c = null;
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            c = db2.query(true, "configs", colunas, null, null, null, null, null, null);
            Log.i("Configs", "Ok");
        } catch (Exception e) {
            Log.e("Configs", "Erro no cursor: " + e.toString());
        }
        return c;
    }
    public Cursor sumProdutosLista(int idLista) {
        String[] colunas = {"sum(produto.preco) as preco"};
        String where = " prod_lista._id_prod = produto._id"
                + " and produto.id_categoria = categoria._id"
                + " and prod_lista.id_lista = lista._id"
                + " and prod_lista.id_lista = " + idLista;
        Cursor c = null;
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            c = db2.query(true, "prod_lista, produto, lista, categoria", colunas, where, null, null, null, null, null);
            Log.i("Sum Produtos Lista", "Ok");
        } catch (Exception e) {
            Log.e("Sum Produtos Lista", "Erro no cursor: " + e.toString());
        }
        return c;
    }

    public Cursor sumProdutosMarcadosLista(int idLista) {
        String[] colunas = {"sum(produto.preco) as preco"};
        String where = " prod_lista._id_prod = produto._id"
                + " and produto.id_categoria = categoria._id"
                + " and prod_lista.id_lista = lista._id"
                + " and prod_lista.id_lista = " + idLista
                + " and prod_lista.flag_marcado = '1'";
        Cursor c = null;
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            c = db2.query(true, "prod_lista, produto, lista, categoria", colunas, where, null, null, null, null, null);
            Log.i("Sum Produtos Marcados Lista", "Ok");
        } catch (Exception e) {
            Log.e("Sum Produtos Marcados Lista", "Erro no cursor: " + e.toString());
        }
        return c;
    }

    public Cursor listarProdutos() {
        String[] colunas = {"produto._id as _id", "produto.nome as nome", "produto.id_categoria", "produto.preco as preco", "categoria.nome as categoria"};
        Cursor c = null;
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            c = db2.query("produto, categoria", colunas, "produto.id_categoria = categoria._id", null, null, null, "produto.nome", null);
            Log.i("Lstar Produtos()", "Ok");
        } catch (Exception e) {
            Log.e("Listar Produtos()", "Erro no cursor: " + e.toString());
        }
        return c;
    }

    public Cursor listarProdutos(int idCateg, boolean categoria) {
        String[] colunas = {"produto._id as _id", "produto.nome as nome", "produto.id_categoria as id_categoria", "categoria.nome as categoria"};
        String where = " produto.id_categoria = categoria._id"
                + " and categoria._id = " + idCateg;
        Cursor c = null;
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            c = db2.query("produto, categoria", colunas, where, null, null, null, "produto.nome", null);
            Log.i("Lstar Produtos()", "Ok");
        } catch (Exception e) {
            Log.e("Listar Produtos(categ, boolean)", "Erro no cursor: " + e.toString());
        }
        return c;
    }

    public Cursor listarProdutos(int idCateg, int idLista) {
        String[] colunas = {"produto._id as _id", "produto.nome as nome", "produto.id_categoria as id_categoria", "categoria.nome as categoria"};
        String where = " produto.id_categoria = categoria._id"
                + " and categoria._id = " + idCateg
                + " and produto._id not in( select _id_prod" +
                "                           from   prod_lista" +
                "                           where  id_lista = " + idLista + ")";
        Cursor c = null;
        // SELECT produto._id as _id, produto.nome as nome, produto.id_categoria as id_categoria, categoria.nome as categoria
        // FROM produto, categoria
        // WHERE  produto.id_categoria = categoria._id
        // and categoria._id = 1
        // and prod._id not in( select _id_prod
        //                         from   produto_lista
        //                         where  id_lista = 1
        //                         and    flag_marcado = 1 )
        // ORDER BY produto.nome
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            c = db2.query("produto, categoria", colunas, where, null, null, null, "produto.nome", null);
            Log.i("Lstar Produtos()", "Ok");
        } catch (Exception e) {
            Log.e("Listar Produtos(categ, lista)", "Erro no cursor: " + e.toString());
        }
        return c;
    }

    public Cursor ConsultaUltimoProduto() {
        String[] colunas = {"max(_id) as _id"};
        Cursor c = null;
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            c = db2.query("produto", colunas, null, null, null, null, null, null);
            Log.i("ConsultaUltimoProduto", "Ok");
        } catch (Exception e) {
            Log.e("ConsultaUltimoProduto", "Erro no cursor: " + e.toString());
        }
        return c;
    }

    public Cursor listarCategorias() {
        String[] colunas = {"_id", "nome"};
        Cursor c = null;
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            c = db2.query(true, "categoria", colunas, null, null, null, null, "nome", null);
            Log.i("Listar Categorias", "Ok");
        } catch (Exception e) {
            Log.e("Listar Categorias", "Erro no cursor: " + e.toString());
        }
        return c;
    }

    public Cursor listarListas() {
        String[] colunas = new String[]{"_id", "nome"};
        Cursor c = null;
        try {
            db2 = context.openOrCreateDatabase(BANCO, 0, null);
            c = db2.query("lista", colunas, null, null, null, null, "nome");
            Log.i("Listar Listas", "OK");
        } catch (Exception e) {
            Log.e("Listar Listas", "Erro no cursor: " + e.toString());
        }
        return c;
    }
}
