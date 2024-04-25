package br.com.mvbos.lgj;

import java.awt.*;
import java.io.File;
import java.util.Random;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

import br.com.mvbos.lgj.base.CenarioPadrao;
import br.com.mvbos.lgj.base.Texto;

public class JogoCenario extends CenarioPadrao {

	enum Estado {
		JOGANDO, GANHOU, PERDEU
	}

	private static final int ESPACAMENTO = 2;

	private static final int ESPACO_VAZIO = -1;

	private static final int LINHA_COMPLETA = -2;

	private int largBloco, altBloco; // largura bloco e altura bloco

	private int ppx, ppy; // Posicao peca x e y

	private final int[][] grade = new int[10][16];

	private int temporizador = 0;

	private int contador_feitas = 0;

	private Texto texto = new Texto(20);

	private Random rand = new Random();

	private int idPeca = -1;
	private int idPrxPeca = -1;
	private int idPrxPeca2 = -1;

	private int idPrxPeca3 = -1;
	private Color corPeca;
	private int[][] peca;

	private int nivel = Jogo.nivel;
	private int pontos;
	private int linhasFeistas;

	private int contador = 0;

	private boolean animar;
	private boolean depurar;

	private Estado estado = Estado.JOGANDO;


	// Som
	private AudioInputStream as;

	private Clip clipEliminacaoLinha;
	private Clip clipGameOver;
	private Clip clipMoviPecas;
	private Clip clipRotacao;
	private Clip clipTravamentoDePeca;
	private Sequencer seqTema;

	private int ajuste_de_tela = 250;
	private int L_esquerda = 0;
	private int L_direita = 1;
	private int T = 2;
	private int Raio_esquerda = 3;
	private int Raio_direita = 4;
	private int quadrado = 5;
	private int reta = 6;
	private int cont_L_esquerda = 0;
	private int cont_L_direita = 0;
	private int cont_T = 0;
	private int cont_Raio_esquerda = 0;
	private int cont_Raio_direita = 0;
	private int cont_quadrado = 0;
	private int cont_reta = 0;
	private String nome;

	public JogoCenario(int largura, int altura) {
		super(largura, altura);

	}

	@Override
	public void carregar() {
		largBloco = largura / grade.length;
		altBloco = altura / grade[0].length;

		for (int i = 0; i < grade.length; i++) {
			for (int j = 0; j < grade[0].length; j++) {
				grade[i][j] = ESPACO_VAZIO;

			}
		}


		//AUDIOS
		Type[] audioFileTypes = AudioSystem.getAudioFileTypes();
		for (Type t : audioFileTypes) {
			//System.out.println(t.getExtension());

		}

		try {
			as = AudioSystem.getAudioInputStream(new File("som\\EliminacaoLinha.wav"));
			clipEliminacaoLinha = AudioSystem.getClip();
			clipEliminacaoLinha.open(as);

			as = AudioSystem.getAudioInputStream(new File("som\\GameOver.wav"));
			clipGameOver = AudioSystem.getClip();
			clipGameOver.open(as);

			as = AudioSystem.getAudioInputStream(new File("som\\MoviPecas.mid"));
			clipMoviPecas = AudioSystem.getClip();
			clipMoviPecas.open(as);

			as = AudioSystem.getAudioInputStream(new File("som\\RotacaoPecas.mid"));
			clipRotacao = AudioSystem.getClip();
			clipRotacao.open(as);

			as = AudioSystem.getAudioInputStream(new File("som\\TravamentoDePeca.wav"));
			clipTravamentoDePeca = AudioSystem.getClip();
			clipTravamentoDePeca.open(as);

			seqTema = MidiSystem.getSequencer();
			seqTema.setSequence(MidiSystem.getSequence(new File("som\\Tema.mid")));
			seqTema.open();

			seqTema.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);

			seqTema.start();

		} catch (Exception e) {
			e.printStackTrace();

		}

		adicionaPeca();
	}

	@Override
	public void descarregar() {

		if (clipEliminacaoLinha != null) {
			clipEliminacaoLinha.stop();
			clipEliminacaoLinha.close();

		}
		if (clipGameOver != null) {
			clipGameOver.stop();
			clipGameOver.close();

		}
		if (clipMoviPecas != null) {
			clipMoviPecas.stop();
			clipMoviPecas.close();

		}
		if (clipRotacao != null) {
			clipRotacao.stop();
			clipRotacao.close();

		}
		if (clipTravamentoDePeca != null) {
			clipTravamentoDePeca.stop();
			clipTravamentoDePeca.close();

		}
		if (seqTema != null) {
			seqTema.stop();
			seqTema.close();

		}
	}

	@Override
	public void atualizar() {

		if (estado != Estado.JOGANDO) {
			return;

		}

		if (Jogo.controleTecla[Jogo.Tecla.ESQUERDA.ordinal()]) {
			if (validaMovimento(peca, ppx - 1, ppy)){
				ppx--;

			}

			if (clipMoviPecas != null) {
				clipMoviPecas.setFramePosition(0);
				clipMoviPecas.start();

			}
		}

		else if (Jogo.controleTecla[Jogo.Tecla.DIREITA.ordinal()]) {
			if (validaMovimento(peca, ppx + 1, ppy)){
				ppx++;

			}

			if (clipMoviPecas != null) {
				clipMoviPecas.setFramePosition(0);
				clipMoviPecas.start();

			}
		}

		// CIMA = GIRAR PEÇA SENTIDO HORARIO
		if (Jogo.controleTecla[Jogo.Tecla.CIMA.ordinal()]) {
			girarReposicionarPeca(false);

		}
		else if (Jogo.controleTecla[Jogo.Tecla.BAIXO.ordinal()]) {
			if (validaMovimento(peca, ppx, ppy + 1)){
				ppy++;
				pontos+=1;
			}

			if (clipMoviPecas != null) {
				clipMoviPecas.setFramePosition(0);
				clipMoviPecas.start();

			}
		}

		// Z = GIRAR PEÇA ANTI - HORARIO
		if (Jogo.controleTecla[Jogo.Tecla.Z.ordinal()]) {
			girarReposicionarPeca2(false);

		}

		if (depurar && Jogo.controleTecla[Jogo.Tecla.BC.ordinal()]) {
			if (++idPeca == Peca.PECAS.length)
				idPeca = 0;

			peca = Peca.PECAS[idPeca];
			corPeca = Peca.Cores[idPeca];

		}

		// SPACE = QUEDA FORTE
		if (Jogo.controleTecla[Jogo.Tecla.SPACE.ordinal()]) {
			while (validaMovimento(peca, ppx, ppy + 1)) {
				ppy++;
				pontos+=1;

			}

			if (clipTravamentoDePeca != null) {
				clipTravamentoDePeca.setFramePosition(0);
				clipTravamentoDePeca.start();

			}
		}

		Jogo.liberaTeclas();

		if (animar && temporizador >= 5) {
			animar = false;

			descerColunas();
			adicionaPeca();

		} else if (temporizador >= 20) {
			temporizador = 0;

			if (colidiu(ppx, ppy + 1)) {

				if (clipTravamentoDePeca != null) {
					clipTravamentoDePeca.setFramePosition(0);
					clipTravamentoDePeca.start();

				}

				if (!parouForaDaGrade()) {
					adicionarPecaNaGrade();
					animar = marcarLinha();

					peca = null;

					if (!animar)
						adicionaPeca();

				} else {
					estado = Estado.PERDEU;

					if (clipGameOver != null) {
						clipGameOver.setFramePosition(0);
						clipGameOver.start();
						seqTema.stop();

					}
					rankJogadores();

				}

			} else
				ppy++;

		} else
			temporizador += nivel;

	}

	public void rankJogadores(){
		Jogador J = new Jogador();
		Rank Rank = new Rank();

		String N = JOptionPane.showInputDialog(null, "Digite seu nome:");

		J.setPontuacao(pontos);

		J.setNome(N);

		Rank.criarRank(J.getNome(), Integer.toString(J.getPontuacao()));

		Rank.setRanking(J);

		Rank.organizarTamanho();

		int posicao_Ultimo_Jogador = Rank.ranking.indexOf(J) + 1;

		JOptionPane.showMessageDialog(null,  posicao_Ultimo_Jogador+" no Ranking / Nome: " + J.getNome() + " / Pontos: " + J.getPontuacao());

		JOptionPane.showMessageDialog(null, "Nome: " + J.getNome() + " " + "Pontos: " + J.getPontuacao(), "Ranking", JOptionPane.PLAIN_MESSAGE);

		Rank.salvarArquivo();

		Rank.exibirRanking();


	}

	public void adicionaPeca() {

		ppy = -2;
		ppx = grade.length / 2 - 1;

		// Primeira Chamada
		if (idPeca == -1) {
			idPeca = rand.nextInt(Peca.PECAS.length);
			idPrxPeca = rand.nextInt(Peca.PECAS.length);

			if (idPeca == idPrxPeca) {
				idPrxPeca = rand.nextInt(Peca.PECAS.length);

			}
		}

		else{
			idPeca = idPrxPeca;

		}

		// Segunda Chamada
		if (idPrxPeca2 == -1){
			idPrxPeca2 = rand.nextInt(Peca.PECAS.length);

			}

		else{
			idPrxPeca = idPrxPeca2;
			idPrxPeca2 = idPrxPeca3;

		}

		//Terceira Chamada
		idPrxPeca3 = rand.nextInt(Peca.PECAS.length);

		if(idPrxPeca3 == idPrxPeca2){
			idPrxPeca3 = rand.nextInt(Peca.PECAS.length);

		}

		System.out.println(idPeca);

		peca = Peca.PECAS[idPeca];
		corPeca = Peca.Cores[idPeca];

		if (idPeca == 0){
			cont_L_esquerda+=1;

		} else if (idPeca == 1) {
			cont_L_direita+=1;

		}else if (idPeca == 2) {
			cont_T+=1;

		}else if (idPeca == 3) {
			cont_Raio_esquerda+=1;

		}else if (idPeca == 4) {
			cont_Raio_direita+=1;

		}else if (idPeca == 5) {
			cont_quadrado+=1;

		}else if (idPeca == 6) {
			cont_reta+=1;

		}

	}

	private void adicionarPecaNaGrade() {

		for (int col = 0; col < peca.length; col++) {
			for (int lin = 0; lin < peca[col].length; lin++) {

				if (peca[lin][col] != 0) {

					grade[col + ppx][lin + ppy] = idPeca;

				}
			}
		}
	}

	private boolean validaMovimento(int[][] peca, int px, int py) {

		if (peca == null)
			return false;

		for (int col = 0; col < peca.length; col++) {
			for (int lin = 0; lin < peca[col].length; lin++) {
				if (peca[lin][col] == 0)
					continue;

				int prxPx = col + px; // Proxima posicao peca x
				int prxPy = lin + py; // Proxima posicao peca y


				if (prxPx < 0 || prxPx >= grade.length)
					return false;

				if (prxPy >= grade[0].length)
					return false;

				if (prxPy < 0)
					continue;

				// Colidiu com uma peca na grade
				if (grade[prxPx][prxPy] > ESPACO_VAZIO)
					return false;

			}
		}

		return true;
	}

	private boolean parouForaDaGrade() {

		if (peca == null)
			return false;

		for (int lin = 0; lin < peca.length; lin++) {
			for (int col = 0; col < peca[lin].length; col++) {
				if (peca[lin][col] == 0)
					continue;
				// Fora da grade
				if (lin + ppy < 0)
					return true;

			}
		}

		return false;
	}

	private boolean colidiu(int px, int py) {

		if (peca == null)
			return false;

		for (int col = 0; col < peca.length; col++) {
			for (int lin = 0; lin < peca[col].length; lin++) {
				if (peca[lin][col] == 0)
					continue;

				int prxPx = col + px;
				int prxPy = lin + py;

				if (depurar) {
					if (prxPx < 0 || prxPx >= grade.length)
						return false;

				}
				// Chegou na base da grade
				if (prxPy == grade[0].length)
					return true;

				// Fora da grade
				if (prxPy < 0)
					continue;

				// Colidiu com uma peca na grade
				if (grade[prxPx][prxPy] > ESPACO_VAZIO)
					return true;
			}
		}

		return false;
	}

	private boolean marcarLinha() {
		int multPontos = 0;

		for (int lin = grade[0].length - 1; lin >= 0; lin--) {
			boolean linhaCompleta = true;

			for (int col = grade.length - 1; col >= 0; col--) {
				if (grade[col][lin] == ESPACO_VAZIO) {
					linhaCompleta = false;
					break;
				}
			}

			if (linhaCompleta) {

				for (int col = grade.length - 1; col >= 0; col--) {
					grade[col][lin] = LINHA_COMPLETA;
					contador = contador + 1;
					System.out.println(contador);

				}
			}
		}

		if (contador == 10){
			multPontos = 100 * nivel;
			contador_feitas = contador_feitas + 1;
			linhasFeistas = linhasFeistas + 1;

		} else if (contador == 20) {
			multPontos = 300 * nivel;
			contador_feitas = contador_feitas + 2;
			linhasFeistas = linhasFeistas + 2;

		}else if (contador == 30) {
			multPontos = 500 * nivel;
			contador_feitas = contador_feitas + 3;
			linhasFeistas = linhasFeistas + 3;

		}else if (contador >= 40) {
			multPontos = 800 * nivel;
			contador_feitas = contador_feitas + 4;
			linhasFeistas = linhasFeistas + 4;

		}

		contador = 0;

		pontos += multPontos;


		//INFINITOS LEVELS E 10 LINHAS DE PONTUAÇÃO

		if (linhasFeistas == 10) {
			nivel++;
			linhasFeistas = 0;
			contador_feitas = 0;

		}else if (linhasFeistas > 10) {
			nivel++;
			linhasFeistas = 0;
			contador_feitas -= 10;

		}

		return multPontos > 0;
	}

	private void descerColunas() {
		for (int col = 0; col < grade.length; col++) {
			for (int lin = grade[0].length - 1; lin >= 0; lin--) {

				if (grade[col][lin] == LINHA_COMPLETA) {
					int moverPara = lin;
					int prxLinha = lin - 1;

					for (; prxLinha > -1; prxLinha--) {
						if (grade[col][prxLinha] == LINHA_COMPLETA)
							continue;
						else
							break;

					}

					for (; moverPara > -1; moverPara--, prxLinha--) {

						if (prxLinha > -1)
							grade[col][moverPara] = grade[col][prxLinha];

						else
							grade[col][moverPara] = ESPACO_VAZIO;

					}
				}
			}
		}

		if (clipEliminacaoLinha != null) {
			clipEliminacaoLinha.setFramePosition(0);
			clipEliminacaoLinha.start();

		}

	}

	protected void girarPeca(boolean sentidoHorario) {
		if (peca == null)
			return;

		final int[][] temp = new int[peca.length][peca.length];

		for (int i = 0; i < peca.length; i++) {
			for (int j = 0; j < peca.length; j++) {
				if (sentidoHorario)
					temp[j][peca.length - i - 1] = peca[i][j];

				else
					temp[peca.length - j - 1][i] = peca[i][j];

			}
		}

		System.out.println("Antes:");
		imprimirArray(peca);
		System.out.println("Depois:");
		imprimirArray(temp);

		if (validaMovimento(temp, ppx, ppy)) {
			peca = temp;

		}
	}

	private void imprimirArray(int[][] arr) {
		for (int lin = 0; lin < arr.length; lin++) {
			for (int col = 0; col < arr[lin].length; col++) {
				System.out.print(arr[lin][col] + "\t");

			}

			System.out.println();
		}
	}


	//GIRAR PEÇA SENTIDO HORÁRIO
	private void girarReposicionarPeca(boolean sentidoHorario) {
		if (peca == null)
			return;

		int tempPx = ppx;
		final int[][] tempPeca = new int[peca.length][peca.length];

		for (int i = 0; i < peca.length; i++) {
			for (int j = 0; j < peca.length; j++) {
				if (sentidoHorario)
					tempPeca[j][peca.length - i - 1] = peca[i][j];

				else
					tempPeca[peca.length - j - 1][i] = peca[i][j];

			}
		}


		// Reposiciona peca na tela
		for (int i = 0; i < tempPeca.length; i++) {
			for (int j = 0; j < tempPeca.length; j++) {
				if (tempPeca[j][i] == 0) {
					continue;

				}

				int prxPx = i + tempPx;

				if (prxPx < 0)
					tempPx = tempPx - prxPx;

				else if (prxPx == grade.length)
					tempPx = tempPx - 1;

			}
		}


		if (validaMovimento(tempPeca, tempPx, ppy)) {
			peca = tempPeca;
			ppx = tempPx;

			if (clipRotacao != null) {
				clipRotacao.setFramePosition(0);
				clipRotacao.start();

			}
		}
	}

	//GIRAR PEÇA SENTIDO ANTI - HORÁRIO
	private void girarReposicionarPeca2(boolean sentidoAntihorario) {
		if (peca == null)
			return;

		int tempPx = ppx;
		final int[][] tempPeca = new int[peca.length][peca.length];

		for (int i = 0; i < peca.length; i++) {
			for (int j = 0; j < peca.length; j++) {
				if (sentidoAntihorario)
					tempPeca[peca.length - j - 1][i] = peca[i][j];

				else
					tempPeca[j][peca.length - i - 1] = peca[i][j];

			}
		}

		// Reposiciona peca na tela
		for (int i = 0; i < tempPeca.length; i++) {
			for (int j = 0; j < tempPeca.length; j++) {
				if (tempPeca[j][i] == 0) {
					continue;

				}

				int prxPx = i + tempPx;

				if (prxPx < 0)
					tempPx = tempPx - prxPx;

				else if (prxPx == grade.length)
					tempPx = tempPx - 1;

			}
		}

		if (validaMovimento(tempPeca, tempPx, ppy)) {
			peca = tempPeca;
			ppx = tempPx;

			if (clipRotacao != null) {
				clipRotacao.setFramePosition(0);
				clipRotacao.start();

			}
		}
	}

	@Override
	public void desenhar(Graphics2D g) {

		// RESPONSÁVEL PELO AJUSTE DA TELA
		for (int col = 0; col < grade.length; col++) {
			for (int lin = 0; lin < grade[0].length; lin++) {
				int valor = grade[col][lin];

				if (valor == ESPACO_VAZIO)
					continue;

				if (valor == LINHA_COMPLETA)
					g.setColor(Color.RED);

				else
					g.setColor(Peca.Cores[valor]);

				int x = ajuste_de_tela + col * largBloco + ESPACAMENTO; //ajuste de tela para enquadrar corretamente as peças
				int y = lin * altBloco + ESPACAMENTO;

				g.fillRect(x, y, largBloco - ESPACAMENTO, altBloco - ESPACAMENTO);

			}
		}

		if (peca != null) {
			g.setColor(corPeca);

			for (int col = 0; col < peca.length; col++) {
				for (int lin = 0; lin < peca[col].length; lin++) {
					if (peca[lin][col] != 0) {
						int x = ajuste_de_tela + (col + ppx) * largBloco + ESPACAMENTO; //ajuste de tela para enquadrar corretamente as peças
						int y = (lin + ppy) * altBloco + ESPACAMENTO;

						g.fillRect(x, y, largBloco - ESPACAMENTO, altBloco - ESPACAMENTO);

					} else if (depurar) {
						g.setColor(Color.PINK);
						int x = ajuste_de_tela + (col + ppx) * largBloco + ESPACAMENTO; //ajuste de tela para enquadrar corretamente as peças
						int y = (lin + ppy) * altBloco + ESPACAMENTO;

						g.fillRect(x, y, largBloco - ESPACAMENTO, altBloco - ESPACAMENTO);

						g.setColor(corPeca);

					}
				}
			}
		}

		//MINIATURA DAS PEÇAS
		int miniatura = largBloco / 2; //TAMANHO DA MINIATURA
		int[][] prxPeca = Peca.PECAS[idPrxPeca];
		g.setColor(Peca.Cores[idPrxPeca]);

		for (int col = 0; col < prxPeca.length; col++) {
			for (int lin = 0; lin < prxPeca[col].length; lin++) {
				if (prxPeca[lin][col] == 0)
					continue;

				int x = 757 + col * miniatura + ESPACAMENTO; //POSIÇÃO DA MINIATURA
				int y = 70 + lin * miniatura + ESPACAMENTO; // POSIÇÃO DA MINIATURA

				g.fillRect(x, y, miniatura - ESPACAMENTO, miniatura - ESPACAMENTO);

			}
		}

		int[][] prxPeca2 = Peca.PECAS[idPrxPeca2];
		g.setColor(Peca.Cores[idPrxPeca2]);

		for (int col = 0; col < prxPeca2.length; col++) {
			for (int lin = 0; lin < prxPeca2[col].length; lin++) {
				if (prxPeca2[lin][col] == 0)
					continue;

				int x = 842 + col * miniatura + ESPACAMENTO; //POSIÇÃO DA MINIATURA
				int y = 70 + lin * miniatura + ESPACAMENTO; // POSIÇÃO DA MINIATURA

				g.fillRect(x, y, miniatura - ESPACAMENTO, miniatura - ESPACAMENTO);

			}
		}

		int[][] prxPeca3 = Peca.PECAS[idPrxPeca3];
		g.setColor(Peca.Cores[idPrxPeca3]);

		for (int col = 0; col < prxPeca3.length; col++) {
			for (int lin = 0; lin < prxPeca3[col].length; lin++) {
				if (prxPeca3[lin][col] == 0)
					continue;

				int x = 923 + col * miniatura + ESPACAMENTO; //POSIÇÃO DA MINIATURA
				int y = 70 + lin * miniatura + ESPACAMENTO; // POSIÇÃO DA MINIATURA

				g.fillRect(x, y, miniatura - ESPACAMENTO, miniatura - ESPACAMENTO);

			}
		}

		texto.setCor(Color.WHITE);
		texto.desenha(g, "Level:" + nivel, 20, 35); //CONTABILIZA O NÚMERO DO LEVEL/NIVEL
		texto.desenha(g, "Linhas Eliminadas:" + contador_feitas, 20, 60); //CONTABILIZA O NÚMERO DE LINHAS ELIMINADAS
		texto.desenha(g, "Pontos:"+ pontos, 20, 85); // CONTABILIZA A PONTUAÇÃO
		texto.desenha(g, "Status: ", 20, 142); // Status do Jogo
		texto.desenha(g, "ESTRUTURA SEGUINTE", 775, 35); // TEXTO PARA PROXIMA PEÇA

		//PAINEL ESQUERDO
		g.drawLine(250,0,250,672);//RESPONSAVEL POR "CRIAR" A LINHA DA ESQUERDA
		g.drawLine(0,108,250,108);//RESPONSAVEL POR "CRIAR" A LINHA DA APÓS OS PONTOS
		g.drawLine(0,160,250,160);//RESPONSAVEL POR "CRIAR" A LINHA DA DIREITA

		texto.setCor(Color.WHITE);
		texto.desenha(g, ": " + cont_L_esquerda, 100, 215); //Peça 0
		texto.desenha(g, ": " + cont_L_direita, 100, 285); //Peça 1
		texto.desenha(g, ": " + cont_T, 100, 345); //Peça 2
		texto.desenha(g, ": " + cont_Raio_esquerda, 100, 410); //Peça 3
		texto.desenha(g, ": " + cont_Raio_direita, 100, 480); //Peça 4
		texto.desenha(g, ": " + cont_quadrado, 100, 542); //Peça 5
		texto.desenha(g, ": " + cont_reta, 100, 610); //Peça 6


		//PAINEL DIREITO
		g.drawLine(750,0,750,672);//RESPONSAVEL POR "CRIAR" A LINHA DA DIREITA
		g.drawLine(750,50,1000,50);//RESPONSAVEL POR "CRIAR" A LINHA DA APÓS OS PONTOS
		g.drawLine(750,175,1000,175);//RESPONSAVEL POR "CRIAR" A LINHA DA APÓS OS PONTOS
		g.drawLine(834,50,834,175);//LINHA DAS MINIATURAS
		g.drawLine(918,50,918,175);//LINHA DAS MINIATURAS
		texto.desenha(g, "Tetris by Braule and Lucas", 757, 650); //CONTABILIZA O NÚMERO DO LEVEL/NIVEL


		//MINIATURA DAS PEÇAS
		int miniatura_fixada = largBloco / 3; //TAMANHO DA MINIATURA
		int[][] prxPeca_fixada0 = Peca.PECAS[0]; //L_esquerda
		g.setColor(Peca.Cores[0]);

		for (int col = 0; col < prxPeca_fixada0.length; col++) {
			for (int lin = 0; lin < prxPeca_fixada0[col].length; lin++) {
				if (prxPeca_fixada0[lin][col] == 0)
					continue;

				int x = 40 + col * miniatura_fixada + ESPACAMENTO; //POSIÇÃO DA MINIATURA
				int y = 185 + lin * miniatura_fixada + ESPACAMENTO; // POSIÇÃO DA MINIATURA

				g.fillRect(x, y, miniatura_fixada - ESPACAMENTO, miniatura_fixada - ESPACAMENTO);

			}
		}

		int[][] prxPeca_fixada1 = Peca.PECAS[1]; //L_direita
		g.setColor(Peca.Cores[1]);

		for (int col = 0; col < prxPeca_fixada1.length; col++) {
			for (int lin = 0; lin < prxPeca_fixada1[col].length; lin++) {
				if (prxPeca_fixada1[lin][col] == 0)
					continue;

				int x = 40 + col * miniatura_fixada + ESPACAMENTO; //POSIÇÃO DA MINIATURA
				int y = 255 + lin * miniatura_fixada + ESPACAMENTO; // POSIÇÃO DA MINIATURA

				g.fillRect(x, y, miniatura_fixada - ESPACAMENTO, miniatura_fixada - ESPACAMENTO);

			}
		}

		int[][] prxPeca_fixada2 = Peca.PECAS[2]; //T
		g.setColor(Peca.Cores[2]);

		for (int col = 0; col < prxPeca_fixada2.length; col++) {
			for (int lin = 0; lin < prxPeca_fixada2[col].length; lin++) {
				if (prxPeca_fixada2[lin][col] == 0)
					continue;

				int x = 40 + col * miniatura_fixada + ESPACAMENTO; //POSIÇÃO DA MINIATURA
				int y = 325 + lin * miniatura_fixada + ESPACAMENTO; // POSIÇÃO DA MINIATURA

				g.fillRect(x, y, miniatura_fixada - ESPACAMENTO, miniatura_fixada - ESPACAMENTO);

			}
		}

		int[][] prxPeca_fixada3 = Peca.PECAS[3]; //Raio_Esquerda
		g.setColor(Peca.Cores[3]);

		for (int col = 0; col < prxPeca_fixada3.length; col++) {
			for (int lin = 0; lin < prxPeca_fixada3[col].length; lin++) {
				if (prxPeca_fixada3[lin][col] == 0)
					continue;

				int x = 40 + col * miniatura_fixada + ESPACAMENTO; //POSIÇÃO DA MINIATURA
				int y = 380 + lin * miniatura_fixada + ESPACAMENTO; // POSIÇÃO DA MINIATURA

				g.fillRect(x, y, miniatura_fixada - ESPACAMENTO, miniatura_fixada - ESPACAMENTO);

			}
		}

		int[][] prxPeca_fixada4 = Peca.PECAS[4]; //Raio_Direita
		g.setColor(Peca.Cores[4]);

		for (int col = 0; col < prxPeca_fixada4.length; col++) {
			for (int lin = 0; lin < prxPeca_fixada4[col].length; lin++) {
				if (prxPeca_fixada4[lin][col] == 0)
					continue;

				int x = 40 + col * miniatura_fixada + ESPACAMENTO; //POSIÇÃO DA MINIATURA
				int y = 450 + lin * miniatura_fixada + ESPACAMENTO; // POSIÇÃO DA MINIATURA

				g.fillRect(x, y, miniatura_fixada - ESPACAMENTO, miniatura_fixada - ESPACAMENTO);

			}
		}

		int[][] prxPeca_fixada5 = Peca.PECAS[5]; //Quadrado
		g.setColor(Peca.Cores[5]);

		for (int col = 0; col < prxPeca_fixada5.length; col++) {
			for (int lin = 0; lin < prxPeca_fixada5[col].length; lin++) {
				if (prxPeca_fixada5[lin][col] == 0)
					continue;

				int x = 40 + col * miniatura_fixada + ESPACAMENTO; //POSIÇÃO DA MINIATURA
				int y = 520 + lin * miniatura_fixada + ESPACAMENTO; // POSIÇÃO DA MINIATURA

				g.fillRect(x, y, miniatura_fixada - ESPACAMENTO, miniatura_fixada - ESPACAMENTO);

			}
		}

		int[][] prxPeca_fixada6 = Peca.PECAS[6]; //reta
		g.setColor(Peca.Cores[6]);

		for (int col = 0; col < prxPeca_fixada6.length; col++) {
			for (int lin = 0; lin < prxPeca_fixada6[col].length; lin++) {
				if (prxPeca_fixada6[lin][col] == 0)
					continue;

				int x = 40 + col * miniatura_fixada + ESPACAMENTO; //POSIÇÃO DA MINIATURA
				int y = 575 + lin * miniatura_fixada + ESPACAMENTO; // POSIÇÃO DA MINIATURA

				g.fillRect(x, y, miniatura_fixada - ESPACAMENTO, miniatura_fixada - ESPACAMENTO);

			}
		}

		if (estado == Estado.JOGANDO && Jogo.pausado == false){
			texto.desenha(g, "Jogando! ", 87, 142); // JOGANDO
		}


		if (estado != Estado.JOGANDO) {
			texto.setCor(Color.WHITE);
			if (estado == Estado.GANHOU)
				texto.desenha(g, "Finalmente!", 180, 180);
			else
				texto.desenha(g, "Deu Ruim! ", 87, 142); // GAME OVER

				//String nome = JOptionPane.showInputDialog("Digite seu nome?");
				//JOptionPane.showMessageDialog(null, "Seu nome é: " + nome, "Mensagem", JOptionPane.PLAIN_MESSAGE);
				//System.out.println(nome);
		}
	}
}