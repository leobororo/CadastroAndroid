package com.cadastroaluno.projeto.cadastroaluno;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.cadastroaluno.projeto.cadastroaluno.Util.getInteger;

/**
 * Created by leobo on 9/18/2016.
 */
public class FABOnClickListener implements View.OnClickListener {

    private final Context context;

    public FABOnClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View view) {

        final LayoutInflater inflater = LayoutInflater.from(context);

        final View createAlertView = inflater.inflate(R.layout.layout_create_alert, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(createAlertView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);

        Button btnGerar = (Button) createAlertView.findViewById(R.id.btnGerar);
        btnGerar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Result aluno = new Result();

                EditText txtNome = (EditText) createAlertView.findViewById(R.id.edtNome);
                txtNome.setText("José da Silva");

                EditText txtIdade = (EditText) createAlertView.findViewById(R.id.edtIdade);
                txtIdade.setText("25");

                EditText txtFoto = (EditText) createAlertView.findViewById(R.id.edtFoto);
                txtFoto.setText("https://robohash.org/generate.png");

                EditText txtTelefone = (EditText) createAlertView.findViewById(R.id.edtTelefone);
                txtTelefone.setText("9999-8888");

                EditText txtEndereco = (EditText) createAlertView.findViewById(R.id.edtEndereco);
                txtEndereco.setText("Rua Pium-Í, 100");

            }
        });

        Button btnNao = (Button) createAlertView.findViewById(R.id.btnCancelar);
        btnNao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        Button btnSim = (Button) createAlertView.findViewById(R.id.btnSalvar);
        btnSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Result aluno = new Result();

                EditText txtNome = (EditText) createAlertView.findViewById(R.id.edtNome);
                aluno.setNome(txtNome.getText().toString());

                EditText txtIdade = (EditText) createAlertView.findViewById(R.id.edtIdade);
                aluno.setIdade(getInteger(txtIdade.getText().toString()));

                EditText txtFoto = (EditText) createAlertView.findViewById(R.id.edtFoto);
                aluno.setFotoUrl(txtFoto.getText().toString());

                EditText txtTelefone = (EditText) createAlertView.findViewById(R.id.edtTelefone);
                aluno.setTelefone(txtTelefone.getText().toString());

                EditText txtEndereco = (EditText) createAlertView.findViewById(R.id.edtEndereco);
                aluno.setEndereco(txtEndereco.getText().toString());

                ((MainActivity)context).save(aluno);

                alertDialog.dismiss();
            }
        });
    }
}
