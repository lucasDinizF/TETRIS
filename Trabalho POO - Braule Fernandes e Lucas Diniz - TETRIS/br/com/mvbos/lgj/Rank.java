package br.com.mvbos.lgj;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;


public class Rank {
    public ArrayList<Jogador> ranking = new ArrayList<Jogador>();


    public ArrayList<Jogador> getRanking() {
        return ranking;

    }

    public void setRanking(Jogador ranking) {
        this.ranking.add(ranking);

    }

    public void criarRank(String A, String B) {
        Path Ranking = Paths.get("C:\\Users\\lucas\\OneDrive\\Área de Trabalho\\Programacao\\Ranking.txt");

        try (BufferedReader L = new BufferedReader(new FileReader(Ranking.toFile()))) {

            if (!Files.exists(Ranking)) {
                Files.createFile(Ranking);

            }

            String P;

            while ((P = L.readLine()) != null) {
                String[] Atributos = P.split(",");

                Jogador J = new Jogador();
                J.setNome(Atributos[0]);
                J.setPontuacao(Integer.parseInt(Atributos[1]));

                ranking.add(J);

            }


        } catch (Exception e) {
            throw new RuntimeException(e);

        }


    }

    public void organizarTamanho() {
        Collections.sort(ranking);

        while (ranking.size() > 10) {
            ranking.remove(ranking.size() - 1);

        }

    }

    public void salvarArquivo() {
        Path Ranking = Paths.get("C:\\Users\\lucas\\OneDrive\\Área de Trabalho\\Programacao\\Ranking.txt");

        try (BufferedWriter E = new BufferedWriter(new FileWriter(Ranking.toFile()))) {

            for (Jogador jogador : ranking) {
                E.write(jogador.getNome() + "," + jogador.getPontuacao() + "\n");

            }

        } catch (IOException e) {
            throw new RuntimeException(e);

        }

    }

    public  void exibirRanking() {
        JFrame mostrar_ranking = new JFrame("Ranking");
        mostrar_ranking.setSize(225,280);

        JTextArea Mostrar_ranking = new JTextArea();

        for (int i = 0; i < ranking.size(); i++) {
            Jogador jogador = ranking.get(i);

            Mostrar_ranking.append((i + 1) + ") Nome: " + jogador.getNome() + " | Pontos: " + jogador.getPontuacao() + " |" + "\n");
            Mostrar_ranking.append("");

        }

        mostrar_ranking.add(Mostrar_ranking);

        mostrar_ranking.setVisible(true);

    }


}
