package com.example.exemplocrud;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivityListar extends AppCompatActivity {
    private ListView listView;
    private AlunoDao dao;
    private List<Aluno> alunos;
    private List<Aluno> alunosFiltrados = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_listar);

        listView = findViewById(R.id.lista_alunos);
        dao = new AlunoDao(this);
        alunos = dao.obterTodos();
        alunosFiltrados.addAll(alunos);

        ArrayAdapter<Aluno> adaptador = new ArrayAdapter<Aluno>(this, android.R.layout.simple_list_item_1, alunos);

        listView.setAdapter(adaptador);

        registerForContextMenu(listView);

    }

    public void irParaCadastrar(View view){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    //METODO MENU_CONTEXTO PARA INFLAR O MENU QUANDO ITEM PRESSIONADO
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.menu_contexto, menu); //Aqui coloca o nome do menu que havia sido configurado
    }

    public void excluir(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Aluno alunoExcluir = alunosFiltrados.get(menuInfo.position);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Atenção")
                .setMessage("Realmente deseja excluir o aluno?")
                .setNegativeButton("NÃO",null)
                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alunosFiltrados.remove(alunoExcluir);
                        alunos.remove(alunoExcluir);
                        dao.excluir(alunoExcluir);
                        listView.invalidateViews();
                    }
                } ).create(); //criar a janela
        dialog.show(); //manda mostrar a janela
    }

    public void atualizar(MenuItem item){

        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Aluno alunoAtualizar = alunosFiltrados.get(menuInfo.position);
        Intent it = new Intent(this, MainActivity.class); //Nosso cadastrar se chama 'MainActivity’
        it.putExtra("aluno",alunoAtualizar);
        startActivity(it);
    }
}