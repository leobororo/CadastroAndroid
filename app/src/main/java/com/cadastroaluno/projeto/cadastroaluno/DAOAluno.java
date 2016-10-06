package com.cadastroaluno.projeto.cadastroaluno;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static com.cadastroaluno.projeto.cadastroaluno.Util.getText;

/**
 * Created by leobo on 9/18/2016.
 */
public class DAOAluno extends SQLiteOpenHelper {
    public static String NOME_BD = "ALUNOS_DB";
    public static String NOME_TABELA = "ALUNOS_TBL";
    public static int VERSAO = 1;

    public static final String ID_ALUNO = "id_aluno";
    public static final String OBJECT_ID= "objectId";
    public static final String FOTO_URL = "fotoUrl";
    public static final String IDADE = "idade";
    public static final String NOME = "nome";
    public static final String TELEFONE = "telefone";
    public static final String ENDERECO = "endereco";

    public DAOAluno(Context context) {
        super(context, NOME_BD, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase bd) {
        bd.execSQL("CREATE TABLE IF NOT EXISTS "
                + NOME_TABELA + " ("
                + ID_ALUNO + " INTEGER PRIMARY KEY autoincrement, "
                + OBJECT_ID + " TEXT, "
                + FOTO_URL + " TEXT, "
                + IDADE + " INTEGER, "
                + NOME + " TEXT, "
                + TELEFONE + " TEXT, "
                + ENDERECO + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * Insere os dados de um aluno
     * @param aluno Result
     * @return true caso os dados tenham sido inseridos, false caso contrário
     */
    public boolean add(Result aluno) {
        SQLiteDatabase sqlDB = getWritableDatabase();
        ContentValues valores = createContentValues(aluno);

        long result = sqlDB.insert(NOME_TABELA, null, valores);
        sqlDB.close();

        if (result != -1) {
            return true;
        }

        return false;
    }

    /**
     * Insere os dados de alunos fornecidos em um List
     * @param alunos List<Result>
     * @return true caso os dados tenham sido inseridos, false caso contrário
     */
    public boolean addAll(List<Result> alunos) {
        boolean retorno = true;

        deleteAll();
        for (Result aluno : alunos) {
            retorno = retorno && add(aluno);
        }

        return retorno;
    }

    /**
     * Remove os dados de todos os alunos
     * @return true caso os dados tenham sido removidos, false caso contrário
     */
    public boolean deleteAll(){
        SQLiteDatabase sqlDB = getWritableDatabase();
        int result = sqlDB.delete(NOME_TABELA, "1 = 1", new String[]{});
        sqlDB.close();

        if (result > 0) {
            return true;
        }

        return false;
    }

    /**
     * Remove dados de um aluno com um determinado id
     * @param idAluno
     * @return true caso os dados tenham sido removidos, false caso contrário
     */
    public boolean delete(String idAluno){
        SQLiteDatabase sqlDB = getWritableDatabase();
        int result = sqlDB.delete(NOME_TABELA, ID_ALUNO + " = ?", new String[]{idAluno});
        sqlDB.close();
        if (result > 0) {
            return true;
        }
        return false;
    }

    /**
     * Obtém a lista de dados de alunos
     * @return List<Result>
     */
    public List<Result> list() {
        SQLiteDatabase sqlDB = getReadableDatabase();
        List<Result> alunos = new ArrayList<Result>();
        Cursor cursor = sqlDB.query(NOME_TABELA,
                new String[]{ID_ALUNO, OBJECT_ID, FOTO_URL, IDADE, NOME, TELEFONE, ENDERECO},
                null,
                null,
                null,
                null,
                null);

        while (cursor.moveToNext()) {
            addAlunoData(alunos, cursor);
        }

        cursor.close();
        sqlDB.close();

        return alunos;
    }

    private void addAlunoData(List<Result> alunos, Cursor cursor) {
        Result aluno = new Result();
        aluno.setId(cursor.getInt(0));
        aluno.setObjectId(cursor.getString(1));
        aluno.setFotoUrl(cursor.getString(2));
        aluno.setIdade(Integer.valueOf(cursor.getString(3)));
        aluno.setNome(cursor.getString(4));
        aluno.setTelefone(cursor.getString(5));
        aluno.setEndereco(cursor.getString(6));
        alunos.add(aluno);
    }

    @NonNull
    private ContentValues createContentValues(Result aluno) {
        ContentValues valores = new ContentValues();
        valores.put(OBJECT_ID, getText(aluno.getObjectId()));
        valores.put(FOTO_URL, getText(aluno.getFotoUrl()));
        valores.put(IDADE, getText(aluno.getIdade()));
        valores.put(NOME, getText(aluno.getNome()));
        valores.put(TELEFONE, getText(aluno.getTelefone()));
        valores.put(ENDERECO, getText(aluno.getEndereco()));
        return valores;
    }
}
