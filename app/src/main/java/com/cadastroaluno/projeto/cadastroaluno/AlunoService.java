package com.cadastroaluno.projeto.cadastroaluno;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.app.PendingIntent.getActivity;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.graphics.BitmapFactory.decodeResource;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static com.cadastroaluno.projeto.cadastroaluno.APIAlunoService.BASE_URL;

/**
 * Created by leobo on 9/18/2016.
 */
public class AlunoService implements AlunoServiceConstants {
    private static final String TAG = "http";

    private AlunoService(){
        //não devo instanciar esta classe
    }

    public static void list(final Context context, final List<Result> results) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIAlunoService service = retrofit.create(APIAlunoService.class);

        Call<Example> call = service.listAlunos();
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                if (response.isSuccessful()) {
                    results.addAll(response.body().getResults());

                    makeText(context, LISTA_DE_ALUNOS_CARREGADA_COM_SUCESSO, LENGTH_LONG).show();
                } else {
                    makeText(context, response.message(), LENGTH_LONG).show();
                    Log.e(TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                makeText(context, OCORREU_UMA_EXCECAO_AO_TENTAR_CARREGAR_A_LISTA_DE_ALUNOS, LENGTH_LONG).show();
                Log.e(TAG, t.getMessage());
            }
        });
    }

    /**
     * Obtém os dados do serviço REST e os armazena no banco de dados
     * @param context Context
     * @param results List<Result> representando os dados dos alunos
     * @param DAOAluno DAOAluno serviço de persistência para dados de um aluno
     */
    public static void sync(final Context context, final List<Result> results, final DAOAluno DAOAluno) {
        Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(BASE_URL)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();

        APIAlunoService service = retrofit.create(APIAlunoService.class);

        Call<Example> call = service.listAlunos();
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                if (response.isSuccessful()) {
                    results.addAll(response.body().getResults());
                    DAOAluno.addAll(response.body().getResults());

                    createNotification(context, SINCRONIZACAO_FEITA_COM_SUCESSO);
                } else {
                    createNotification(context, response.message());
                    Log.e(TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                createNotification(context, DESCULPE_OCORREU_UMA_EXCECAO);
                Log.e(TAG, t.getMessage());
            }
        });
    }

    /**
     * Cria uma notificação com um determinado content text
     * @param context Context
     * @param message String texto para o content text
     */
    private static void createNotification(Context context, String message) {
        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        PendingIntent p = getActivity(context,
                                      0,
                                      new Intent(context, MainActivity.class),
                                      FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context)
                                                          .setContentTitle(TITULO_NOTIFICACAO)
                                                          .setContentText(message)
                                                          .setSmallIcon(R.mipmap.ic_launcher)
                                                          .setLargeIcon(decodeResource(context.getResources(), R.mipmap.ic_launcher))
                                                          .setDefaults(Notification.DEFAULT_ALL)
                                                          .setAutoCancel(true)
                                                          .setContentIntent(p)
                                                          .build();

        nm.notify(1, notification);
    }

    public static void add(final Context context, Result resultToAdd, final List<Result> results) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIAlunoService service = retrofit.create(APIAlunoService.class);

        Call<Result> call = service.addAluno(resultToAdd);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    list(context, results);

                    makeText(context, DADOS_DO_ALUNO_SALVOS_COM_SUCESSO, LENGTH_LONG).show();
                } else {
                    makeText(context, response.message(), LENGTH_LONG).show();
                    Log.e(TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                makeText(context, OCORREU_UMA_EXCECAO_AO_TENTAR_SALVAR_OS_DADOS_DO_ALUNO, LENGTH_LONG).show();
                Log.e(TAG, t.getMessage());
            }
        });
    }

    public static void addAll(final Context context, List<Result> resultsToAdd, final List<Result> results) {
        for (Result result : resultsToAdd) {
            add(context, result, results);
        }
    }

    public static void delete(final Context context, String objectId, final List<Result> results) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIAlunoService service = retrofit.create(APIAlunoService.class);

        Call<ResponseBody> call = service.deleteAluno(objectId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    list(context, results);

                    makeText(context, ALUNO_EXCLUIDO_COM_SUCESSO, LENGTH_LONG).show();
                } else {
                    makeText(context, response.message(), LENGTH_LONG).show();
                    Log.e(TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                makeText(context, OCORREU_UMA_EXCECAO_AO_TENTAR_REMOVER_O_ALUNO, LENGTH_LONG).show();
                Log.e(TAG, t.getMessage());
            }
        });
    }
}
