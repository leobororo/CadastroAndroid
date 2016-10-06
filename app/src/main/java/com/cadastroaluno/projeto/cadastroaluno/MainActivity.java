package com.cadastroaluno.projeto.cadastroaluno;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static com.cadastroaluno.projeto.cadastroaluno.AlunoService.delete;
import static com.cadastroaluno.projeto.cadastroaluno.AlunoService.add;
import static com.cadastroaluno.projeto.cadastroaluno.AlunoService.list;
import static com.cadastroaluno.projeto.cadastroaluno.AlunoService.sync;
import static com.cadastroaluno.projeto.cadastroaluno.AlunoServiceConstants.DADOS_DO_ALUNO_EXCLUIDOS_DO_BANCO_DE_DADOS_COM_SUCESSO;
import static com.cadastroaluno.projeto.cadastroaluno.AlunoServiceConstants.DADOS_DO_ALUNO_SALVO_NO_BD_COM_SUCESSO;
import static com.cadastroaluno.projeto.cadastroaluno.AlunoServiceConstants.LISTA_DE_ALUNOS_CARREGADA_DO_BD_COM_SUCESSO;
import static com.cadastroaluno.projeto.cadastroaluno.AlunoServiceConstants.VOCE_SERA_NOTIFICADO_QUANDO_A_SINCRONIZACAL_FOR_FINALIZADA;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String PREF_FILE_NAME = "cadastroAlunoPrefs";
    private static final String CARREGAR = "carregar";
    private static int menuItemSelected;

    private final Context context = this;

    private DAOAluno daoAluno;
    private ListView listViewAlunos;
    private AlunoAdapter alunoAdapter;
    private ListAdapter<Result> alunos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instantiateAdapterAndList();
        instantiateDB();
        setUpViews();
        loadInitialData();
    }

    /**
     * Carrega o ListView com os dados de alunos provenientes do serviço REST
     */
    private void loadInitialData() {
        if (getBooleanPrefsByKey(CARREGAR)) {
            loadOnlineData();

            //seleciona o item Carregar dados Online do menu de navegação
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setCheckedItem(R.id.nav_lista_servidor);
        }
    }

    /**
     * Cria uma instância do banco de dados
     */
    private void instantiateDB() {
        daoAluno = new DAOAluno(context);
    }

    /**
     * Configura os dados de algumas views
     */
    private void setUpViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createFloatingActionButton();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        addListViewAlunos();
    }

    /**
     * Cria o adapter para utilização pelo ListView
     */
    private void instantiateAdapterAndList() {
        alunos = new ListAdapter<Result>();
        alunoAdapter = new AlunoAdapter(context, alunos);
        alunos.setAdapter(alunoAdapter);
    }

    /**
     * Cria o botão de ação flutuante
     */
    private void createFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new FABOnClickListener(context));
    }

    /**
     * Salva os dados de um aluno no banco de dados ou no serviço REST dependendo do contexto
     * @param aluno Result dados do aluno a serem salvos
     */
    public void save(Result aluno) {
        if (menuItemSelected == R.id.nav_lista_servidor) {
            add(context, aluno, alunos);
        } else if (menuItemSelected == R.id.nav_lista_db) {
            saveToBdAndUpdateList(aluno);
        }
    }

    /**
     * Remove os dados de um aluno do banco de dador ou do serviço REST dependendo do contexto
     * @param aluno Result dados do aluno a serem removidos
     */
    public void remove(Result aluno) {
        if (menuItemSelected == R.id.nav_lista_servidor) {
            delete(context, aluno.getObjectId(), alunos);
        } else if (menuItemSelected == R.id.nav_lista_db) {
            removeFromBDAndUpdateList(aluno);
        }
    }

    /**
     * Exclui os dados de um aluno do banco de dados e atualiza a lista de alunos
     * @param aluno Result dados do aluno a serem removidos do banco de dados
     */
    private void removeFromBDAndUpdateList(Result aluno) {
        daoAluno.delete(aluno.getId().toString());
        alunos.addAll(daoAluno.list());
        makeText(context, DADOS_DO_ALUNO_EXCLUIDOS_DO_BANCO_DE_DADOS_COM_SUCESSO, LENGTH_LONG).show();
    }

    /**
     * Salva os dados de um aluno do banco de dados e atualiza a lista de alunos
     * @param aluno Result dados do aluno a serem salvos no banco de dados
     */
    private void saveToBdAndUpdateList(Result aluno) {
        daoAluno.add(aluno);
        alunos.addAll(daoAluno.list());
        makeText(context, DADOS_DO_ALUNO_SALVO_NO_BD_COM_SUCESSO, LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            showPrefsAlertDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Exibe um AlertDialog para que o usuário configure se o sistema deverá carregar os dados do serviço REST quando a aplicação voltar para o estado ativo ou não
     */
    private void showPrefsAlertDialog() {
        LayoutInflater inflater = LayoutInflater.from(context);

        final View deleteAlertView = inflater.inflate(R.layout.layout_carregar_dados_alert, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(deleteAlertView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);

        if(getBooleanPrefsByKey(CARREGAR)) {
            ((RadioButton)deleteAlertView.findViewById(R.id.rdSim)).setChecked(true);
        } else {
            ((RadioButton)deleteAlertView.findViewById(R.id.rdNao)).setChecked(true);
        }

        Button btnOk = (Button) deleteAlertView.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((RadioButton)deleteAlertView.findViewById(R.id.rdSim)).isChecked()) {
                    putStringPrefs(CARREGAR, true);
                } else {
                    putStringPrefs(CARREGAR, false);
                }

                alertDialog.dismiss();
            }
        });
    }

    /**
     * Salva uma configuração booleana do usuário utilizando o objeto SharedPreferences
     * @param chave String chave para a configuração
     * @param valor boolean valor a ser salvo no objeto SharedPreferences
     */
    private void putStringPrefs(String chave, boolean valor) {
        SharedPreferences prefs = getSharedPreferences(PREF_FILE_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(chave, valor);
        editor.commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_lista_servidor) {
            loadOnlineData();
        } else if (id == R.id.nav_lista_db) {
            loadOfflineData();
        } else if (id == R.id.nav_sincronizar) {

            //copia dados do serviço REST para o banco de dados local
            copyServerData();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setCheckedItem(menuItemSelected);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Obtém os dados dos alunos através do serviço REST
     */
    private void loadOfflineData() {
        menuItemSelected = R.id.nav_lista_db;
        alunos.addAll(daoAluno.list());
        this.setTitle(R.string.app_name_offline);
        makeText(context, LISTA_DE_ALUNOS_CARREGADA_DO_BD_COM_SUCESSO, LENGTH_LONG).show();
    }

    /**
     * Obtém os dados dos alunos salvos no banco de dados
     */
    private void loadOnlineData() {
        menuItemSelected = R.id.nav_lista_servidor;
        list(context, alunos);
        this.setTitle(R.string.app_name_online);
    }

    /**
     * Copia dadoos do servidor para o banco de dados local
     */
    private void copyServerData() {
        makeText(context, VOCE_SERA_NOTIFICADO_QUANDO_A_SINCRONIZACAL_FOR_FINALIZADA, LENGTH_LONG).show();
        List<Result> alunosWebService = new ArrayList<>();
        sync(context, alunosWebService, daoAluno);
    }

    /**
     * Cria o ListView de alunos
     */
    private void addListViewAlunos() {
        listViewAlunos = (ListView) findViewById(R.id.listViewAlunos);
        listViewAlunos.setAdapter(alunoAdapter);
    }

    /**
     * Obtém o valor de uma configuração booleana armazenada no objeto SharedPreferences
     * @param chave String representando a chave para encontrar a configuração
     * @return boolean valor armazenado para a configuração se ele existir, true caso contrário
     */
    private Boolean getBooleanPrefsByKey(String chave){
        SharedPreferences prefs = getSharedPreferences(PREF_FILE_NAME, 0);
        return prefs.getBoolean(chave, true);
    }
}
