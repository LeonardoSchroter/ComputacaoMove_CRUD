package com.example.exemplocrud;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {

    private EditText nome;
    private EditText cpf;
    private EditText telefone;
    private AlunoDao dao;

    private Aluno aluno = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nome = findViewById(R.id.editNome);
        cpf = findViewById(R.id.editCPF);
        telefone = findViewById(R.id.editTelefone);

        dao = new AlunoDao(this);

        Intent it = getIntent(); //pega intenção
        if(it.hasExtra("aluno")){
            aluno = (Aluno) it.getSerializableExtra("aluno");
            nome.setText(aluno.getNome().toString());
            cpf.setText(aluno.getCpf());
            telefone.setText(aluno.getTelefone());
        }

    }

    public void salvar(View view){
        String nomeDigitado = nome.getText().toString().trim();
        String cpfDigitado = cpf.getText().toString().trim();
        String telefoneDigitado = telefone.getText().toString().trim();

        if(nomeDigitado.isEmpty() || cpfDigitado.isEmpty() || telefoneDigitado.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!dao.validaCpf(cpfDigitado)){
            Toast.makeText(this, "CPF invalido!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dao.cpfExistente(cpfDigitado)){
            Toast.makeText(this, "CPF ja existe no banco de dados!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!dao.validaTelefone(telefoneDigitado)){
            Toast.makeText(this, "Telefone invalido! Use o formato (XX) 9XXXX-XXXX", Toast.LENGTH_SHORT).show();
            return;
        }

        if(aluno==null) {
            Aluno a = new Aluno();
            a.setNome(nome.getText().toString());
            a.setCpf(cpf.getText().toString());
            a.setTelefone(telefone.getText().toString());
            long id = dao.inserir(a);
            Toast.makeText(this,"Aluno inserido com id: "+id,Toast.LENGTH_SHORT).show();
        }
        else {
            aluno.setNome(nome.getText().toString());
            aluno.setCpf(cpf.getText().toString());
            aluno.setTelefone(telefone.getText().toString());
            dao.atualizar(aluno); //inserir o aluno
            Toast.makeText(this,"Aluno Atualizado!! com id: ", Toast.LENGTH_SHORT).show();
        }



    }

    public void irParaListar(View view){
        Intent intent = new Intent(this,MainActivityListar.class);
        startActivity(intent);
    }
}