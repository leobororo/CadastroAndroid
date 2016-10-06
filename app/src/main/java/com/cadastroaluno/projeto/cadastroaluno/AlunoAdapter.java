package com.cadastroaluno.projeto.cadastroaluno;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static com.cadastroaluno.projeto.cadastroaluno.AlunoServiceConstants.NAO_FOI_POSSIVEL_CARREGAR_A_IMAGEM_DE_URL;
import static com.cadastroaluno.projeto.cadastroaluno.AlunoServiceConstants.NAO_FOI_POSSIVEL_ENCONTRAR_O_APLICATIVO_GOOGLE_MAPS;
import static com.cadastroaluno.projeto.cadastroaluno.Util.getText;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;

/**
 * Created by leobo on 8/29/2016.
 */
public class AlunoAdapter extends BaseAdapter {

    private final Context context;
    private final List<Result> alunos;

    public AlunoAdapter(Context context, List<Result> alunos) {
        this.context = context;
        this.alunos = alunos;
    }

    @Override
    public int getCount() {
        return alunos != null ? alunos.size() : 0;
    }

    @Override
    public Object getItem(int index) {
        return alunos.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {

        View viewAluno = LayoutInflater.from(context).inflate(R.layout.layout_item_aluno, viewGroup, false);

        TextView txtNome = (TextView) viewAluno.findViewById(R.id.txtNome);
        TextView txtIdade = (TextView) viewAluno.findViewById(R.id.txtIdade);
        TextView txtEndereco = (TextView) viewAluno.findViewById(R.id.txtEndereco);


        final Result aluno = alunos.get(index);
        txtNome.setText(getText(aluno.getNome()));
        txtIdade.setText(getText(aluno.getIdade()));
        txtEndereco.setText(getText(aluno.getEndereco()));

        ImageButton btnRemover = (ImageButton) viewAluno.findViewById(R.id.btnRemove);
        btnRemover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteAlertDialog(aluno);
            }
        });

        viewAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirecionarParaGoogleMaps(aluno);
            }
        });

        downPicasso(aluno.getFotoUrl(), viewAluno);

        return viewAluno;
    }

    private void redirecionarParaGoogleMaps(Result aluno) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + aluno.getEndereco());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        } else {
            makeText(context, NAO_FOI_POSSIVEL_ENCONTRAR_O_APLICATIVO_GOOGLE_MAPS, LENGTH_LONG).show();
        }
    }

    private void showDeleteAlertDialog(final Result aluno) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View deleteAlertView = inflater.inflate(R.layout.layout_delete_alert, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(deleteAlertView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);

        TextView txtMensagem = (TextView) deleteAlertView.findViewById(R.id.txtMensagemDelete);
        txtMensagem.setText("Deseja remover " + aluno.getNome() + "?");

        Button btnNao = (Button) deleteAlertView.findViewById(R.id.btnNao);
        btnNao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        Button btnSim = (Button) deleteAlertView.findViewById(R.id.btnSim);
        btnSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)context).remove(aluno);

                alertDialog.dismiss();
            }
        });
    }

    private void downPicasso(final String url, View viewAluno) {
        ImageView imagemAluno = (ImageView) viewAluno.findViewById(R.id.imagemAluno);

        if (!isNullOrEmpty(url)) {
            Picasso.with(context)
                    .load(url)
                    .into(imagemAluno, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            //nenhuma ação adicional caso a imagem tenha sido carregada com sucesso
                        }

                        @Override
                        public void onError() {
                            makeText(context, format(NAO_FOI_POSSIVEL_CARREGAR_A_IMAGEM_DE_URL, url), LENGTH_LONG).show();
                        }
                    });
        }

    }
}
