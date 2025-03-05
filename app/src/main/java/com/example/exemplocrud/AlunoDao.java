package com.example.exemplocrud;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AlunoDao {

    private Conexao conexao;
    private SQLiteDatabase banco;

    public AlunoDao(Context context) {
        this.conexao = new Conexao(context);
        banco = conexao.getWritableDatabase();
    }

    public long inserir(Aluno aluno){
        if(!cpfExistente(aluno.getCpf())) {

            ContentValues values = new ContentValues();
            values.put("nome", aluno.getNome());
            values.put("cpf", aluno.getCpf());
            values.put("telefone", aluno.getTelefone());
            return banco.insert("aluno", null, values);
        }else{
            return -1;
        }
    }

    public List<Aluno> obterTodos(){
        List<Aluno> alunos = new ArrayList<>();
//cursor aponta para as linhas retornadas
        Cursor cursor = banco.query("aluno", new String[]{"id", "nome", "cpf", "telefone"},
                null, null,null,null,null); //nome da tabela, nome das colunas, completa com null o método
//que por padrão pede esse número de colunas obrigatórias
        while(cursor.moveToNext()){ //verifica se consegue mover para o próximo ponteiro ou linha
            Aluno a = new Aluno();
            a.setId(cursor.getInt(0)); // new String[]{"id", "nome", "cpf", "telefone"}, id é coluna '0'
            a.setNome(cursor.getString(1)); // new String[]{"id", "nome", "cpf", "telefone"}, nome é coluna '1'
            a.setCpf(cursor.getString(2)); // new String[]{"id", "nome", "cpf", "telefone"}, cpf é coluna '2'
            a.setTelefone(cursor.getString(3)); // new String[]{"id", "nome", "cpf", "telefone"}, telefone é coluna '3'
            alunos.add(a);
        }
        return alunos;
    }

    public boolean cpfExistente(String cpf){
        Cursor cursor = banco.query("aluno", new String[]{"id"}, "cpf = ?", new String[]{cpf}, null, null,null);
        boolean cpfExiste = cursor.getCount() > 0;
        cursor.close();
        return cpfExiste;
    }

    public boolean validaCpf(String cpf) {
        cpf = cpf.replaceAll("[^0-9]", ""); // Remove caracteres não numéricos

        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) { // Verifica tamanho e CPFs inválidos
            return false;
        }

        try {
            int soma = 0;
            int peso = 10;

            // Cálculo do primeiro dígito verificador
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * peso--;
            }

            int primeiroDigito = (soma * 10) % 11;
            if (primeiroDigito == 10) primeiroDigito = 0;

            if (primeiroDigito != Character.getNumericValue(cpf.charAt(9))) {
                return false;
            }

            // Cálculo do segundo dígito verificador
            soma = 0;
            peso = 11;

            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * peso--;
            }

            int segundoDigito = (soma * 10) % 11;
            if (segundoDigito == 10) segundoDigito = 0;

            return segundoDigito == Character.getNumericValue(cpf.charAt(10));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validaTelefone(String telefone){
        String regex = "^\\(\\d{2}\\) 9\\d{4}-\\d{4}$";
        return Pattern.matches(regex, telefone);
    }
}
