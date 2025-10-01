/* ***************************************************************
* Autor............: Isis Caroline Lima Viana
* Matricula........: 202410016
* Inicio...........: 16/08/2025
* Ultima alteracao.: 23/09/2025
* Nome.............: CamadaFisicaTransmissora.java
* Funcao...........: Essa camada eh responsavel por codificar o 
sinal na codificacao escolhida (binario, manchester ou manchester
diferencial) e enviar a mensagem escolhida para o meio de comunicacao
*************************************************************** */
package modelo;

public class CamadaFisicaTransmissora {

  private MeioDeComunicacao meioDeComunicacao;
  private int tipoDeCodificacao;
  private int tipoDeEnquadramento;
  private MeioDeComunicacao copiaMeioDeComunicacao;

  // Construtor
  public CamadaFisicaTransmissora(MeioDeComunicacao meioDeComunicacao, int tipoDeCodificacao, int tipoDeEnquadramento) {
    this.meioDeComunicacao = meioDeComunicacao;
    this.tipoDeCodificacao = tipoDeCodificacao;
    this.tipoDeEnquadramento = tipoDeEnquadramento;
  }

  /* ***************************************************************
   * Metodo: camadaFisicaTransmissora
   * Funcao: verifica qual a codificacao escolhida e chama o metodo
   * equivalente a essa escolha, por fim envia o fluxoBrutoDeBits
   * codificados para o meioDeComunicacao
   * Parametros: int[] (mensagem em AscII binario)
   * Retorno: vazio
   ****************************************************************/
  public void camadaFisicaTransmissora(int quadro[]) {
    // tipoDeCodificacao = 2; // alterar de acordo o teste
    int fluxoBrutoDeBits[] = new int[quadro.length];
    switch (tipoDeCodificacao) {
      case 0: // codificao binaria
        fluxoBrutoDeBits = camadaFisicaTransmissoraCodificacaoBinaria(quadro);
        break;
      case 1: // codificacao manchester
        if(tipoDeEnquadramento!=3){
          fluxoBrutoDeBits = camadaFisicaTransmissoraCodificacaoManchester(quadro);
        }else{
        fluxoBrutoDeBits = violacaoDaCamadaFisica(
            camadaFisicaTransmissoraCodificacaoManchester(quadro));
        }
        break;
      case 2: // codificacao manchester diferencial
        if(tipoDeEnquadramento!=3){
          fluxoBrutoDeBits = camadaFisicaTransmissoraCodificacaoManchesterDiferencial(quadro);
        }else{
        fluxoBrutoDeBits = violacaoDaCamadaFisica(
            camadaFisicaTransmissoraCodificacaoManchesterDiferencial(quadro));
        }
        break;
    }// fim do switch/case
    System.out.println("\nCamada Fisica Transmissora:");
    imprimirVetor(fluxoBrutoDeBits);
    copiaMeioDeComunicacao = meioDeComunicacao.clone();
    copiaMeioDeComunicacao.comunicar(fluxoBrutoDeBits);
  }// fim do metodo CamadaFisicaTransmissora

  /* ***************************************************************
   * Metodo: camadaFisicaTransmissoraCodificacaoBinaria
   * Funcao: nao muda nada no quadro original entao apenas o retorna
   * Parametros: int[] (mensagem em AscII binario)
   * Retorno: int[] (quadro codificado)
   ****************************************************************/
  public int[] camadaFisicaTransmissoraCodificacaoBinaria(int quadro[]) {
    //System.out.println("Original:\n");
    //imprimirVetor(quadro);
    return quadro;
  }// fim do metodo CamadaFisicaTransmissoraCodificacaoBinaria

  /* ***************************************************************
   * Metodo: camadaFisicaTransmissoraCodificacaoManchester
   * Funcao: para cada 0 escreve 01 e para cada 1 escreve 10
   * Parametros: int[] (mensagem em AscII binario)
   * Retorno: int[] (quadro codificado em Manchester)
   ****************************************************************/
  public int[] camadaFisicaTransmissoraCodificacaoManchester(int quadro[]) {
    int bytesValidos = 4 - bytesVazios(quadro[quadro.length - 1]);
    int[] quadroManchester;
    if (bytesValidos > 2)
      quadroManchester = new int[quadro.length * 2];
    else
      quadroManchester = new int[(quadro.length * 2) - 1];
    int cont = 0, k = 31, contBit = 0;
    int bitsValidos = 32 * (quadro.length - 1) + 8 * bytesValidos;

    for (int i = 0; i < quadro.length; i++) {
      k = 31;
      quadroManchester[cont] = 0;
      for (int j = 31; j >= 0; j--) {
        contBit++;

        if (j == 15) {
          cont++;
          quadroManchester[cont] = 0;
        }
        int mascara = 1 << j;
        if ((mascara & quadro[i]) != 0) { // se for 1 escreve 10
          quadroManchester[cont] = (quadroManchester[cont] | 1 << k);
          k -= 2; // pula a posicao do '1' e do '0'

        } else { // se for 0 escreve 01
          k--; // pula a posicao do '0'
          quadroManchester[cont] = (quadroManchester[cont] | 1 << k);
          k--; // pula a posicao do '1'
        }
        if (contBit == bitsValidos)
          break;
      }
      cont++;
    }

    /*impressao para eu acompanhar: (PARTE EXCLUIVEL)
    System.out.println("Original/Meio Termo:\n"); imprimirVetor(quadro);
    System.out.println("Codificado:\n"); imprimirVetor(quadroManchester);*/
    // PARTE EXCLUIVEL

    return quadroManchester;
  }// fim do metodo CamadaFisicaTransmissoraCodificacaoManchester

  /* ***************************************************************
   * Metodo: camadaFisicaTransmissoraCodificacaoManchesterDiferencial
   * Funcao: a variavel booleana sinalAtual representa a polaridade
   * do sinal, quando ele eh verdadeiro escreve 10, quando ele eh falso
   * escreve 01. Antes de escrever esses valores no novo array, porem,
   * caso ele veja um 0 o valor verdade de sinalAtual eh invertido e
   * caso ele veja 1, o sinalAtual permanece o mesmo
   * Parametros: int[] (mensagem em AscII binario)
   * Retorno: int[] (quadro codificado em Manchester Diferencial)
   ****************************************************************/
  public int[] camadaFisicaTransmissoraCodificacaoManchesterDiferencial(int quadro[]) {
    // impressao para eu acompanhar: (PARTE EXCLUIVEL)
    //System.out.println("Original:\n"); imprimirVetor(quadro);
    // PARTE EXCLUIVEL
    int bytesValidos = 4 - bytesVazios(quadro[quadro.length - 1]);
    int[] quadroManchesterDiferencial;
    if (bytesValidos > 2)
      quadroManchesterDiferencial = new int[quadro.length * 2];
    else
      quadroManchesterDiferencial = new int[(quadro.length * 2) - 1];
    int bitsValidos = 32 * (quadro.length - 1) + 8 * bytesValidos;
    boolean sinalAtual = true; // Começa com nível alto
    int k, indexQuadroMD = 0, contBits = 0;
    for (int i = 0; i < quadro.length; i++) {
      quadroManchesterDiferencial[indexQuadroMD] = 0;
      k = 31;
      for (int j = 31; j >= 0; j--) {
        contBits++;
        if (j == 15) {
          indexQuadroMD++;
          k = 31;
        }
        int mascara = 1 << j;
        boolean novoBit = (mascara & quadro[i]) != 0; // eh 1

        /*
         * se foi zero trocou vai ter mudado entao ele vai imprimir o
         * oposto do que estava antes. Ja se nao foi 0, entao trocou segue
         * com o mesmo valor verdade, portanto o sinal mantem seu fluxo:
         */
        // Manchester Diferencial: bit 0 = transição, bit 1 = mantém
        if (!novoBit) { // Se for 0, faz transição
          sinalAtual = !sinalAtual;
        }
        if (!sinalAtual) { // se for 0 escreve 01
          k--; // pula a posicao do '0'
          quadroManchesterDiferencial[indexQuadroMD] |= 1 << k;
          k--; // pula a posicao do '1'

        } else { // se for 1 escreve 10
          quadroManchesterDiferencial[indexQuadroMD] |= 1 << k;
          k -= 2; // pula a posicao do '1' e do '0'
        }
        if (contBits == bitsValidos)
          break;
      } // fim do for interno (passando pelos 31 bits do inteiro
      indexQuadroMD++;
    } // fim do for externo (passando por cada inteiro do array)

    //System.out.println("Codificado: " ); imprimirVetor(quadroManchesterDiferencial);
    // transforma o sinal codificado para codificacao manchester
    return quadroManchesterDiferencial;
  }// fim do CamadaFisicaTransmissoraCodificacaoManchesterDiferencial

  /* ***************************************************************
   * Metodo: parar
   * Funcao: chama o metodo para matar a Copia do MeioDeComunicacao
   * Parametros: nenhum
   * Retorno: vazio
   ****************************************************************/
  public void parar() {
    if (copiaMeioDeComunicacao != null && copiaMeioDeComunicacao.isAlive())
      copiaMeioDeComunicacao.matarThread();
  } // fim do metodo parar

  /* ***************************************************************
   * Metodo: setTaxaDeErro
   * Funcao: chama o metodo setTaxaDeErro do meio de comunicacao.
   * Serve para mudar a probabilidade de um problema acontecer na
   * transmissao da mensagem.
   * Parametros: int taxa (nova probabilidade)
   * Retorno: vazio
   ****************************************************************/
  public void setTaxaDeErro(int taxa) {
    if (copiaMeioDeComunicacao != null)
      copiaMeioDeComunicacao.setTaxaDeErro(taxa);
    meioDeComunicacao.setTaxaDeErro(taxa);
  }

  /* ***************************************************************
   * Metodo: violacaoDaCamadaFisica
   * Funcao: metodo de enquadramento que envolve dividir os quadros
   * com dois pares altos (11). Vai copiando os bits do quadro enviado 
   * como parametro e, a cada n par de bits, ele insere o par 11.
   * Parametros: int[] quadro (quadro sem enquadramento)
   * Retorno: int[] quadroEnquadrado
   ****************************************************************/
  public int[] violacaoDaCamadaFisica(int[] quadro) {
    //System.out.println("Imprimindo original:");
    //imprimirVetor(quadro);
    int num = 7;  //tamanho do quadro
    num *= 2;
    int totalBitsOriginais = quadro.length * 32 - bytesVaziosManchester(quadro[quadro.length - 1]);
    int totalBits = totalBitsOriginais + 2*(totalBitsOriginais + num - 3) / (num - 2);
    int sinalAnt=1, sinalAntAnt =1;
    int tam = (totalBits + 31) / 32;
    int[] quadroEnquadrado = new int[tam];
    int contQuadroEnquadrado = 0, k = 30; //contador de posicao e deslocador do quadroEnquadrado
    int contQuadro = 0, j = 31; //contador de posicao e deslocador do quadro original
    int contBit=0;  //conta os bits que foram transferidos

    //coloca a primeira flag 11
    quadroEnquadrado[0] = 0 | 0b11 << k;
    totalBits--;
    
    //loop
    while (totalBits != 0) {
      //decrementa o deslocador do quadroEnquadrado
      k--;
      if (k < 0) {
        k=31;
        contQuadroEnquadrado++;
        if (contQuadroEnquadrado < quadroEnquadrado.length)
          quadroEnquadrado[contQuadroEnquadrado] = 0;
        else
          break;
      }
      //insere bit no quadroEnquadrado
      int masc = 0 | 1 << j;
      if ((masc & quadro[contQuadro]) != 0){
        quadroEnquadrado[contQuadroEnquadrado] |= 1 << k;
        sinalAntAnt=sinalAnt;
        sinalAnt=1;
      }else{
        //se ver 00 para de transferir
        if(sinalAnt==0 && sinalAntAnt==0) break;
        else 
          sinalAntAnt=sinalAnt;
          sinalAnt=0;
      }
      //decrementa o deslocador do quadro original
      j--;
      if (j <0) {
        j = 31;
        contQuadro++;
        if(contQuadro==quadro.length) break;
      }
      totalBits--;
      contBit++;
      //se foram todos os n bits do quadro insere-se 11 para
      //sinalizar que o quadro acabou
      if(contBit==num-2){
        for(int i=0; i<2; i++){
          k--;
          if (k < 0) {
          k=31;
          contQuadroEnquadrado++;
          if (contQuadroEnquadrado < quadroEnquadrado.length)
            quadroEnquadrado[contQuadroEnquadrado] = 0;
          }
          quadroEnquadrado[contQuadroEnquadrado] |= 1 << k;
        }
      contBit=0;
      }
    } //fim do while
    
    return quadroEnquadrado;
  } //fim da violacaoDaCamadaFisica

  /* ****************** METODOS AUXILIARES ****************** */

   public int bytesVazios(int ultimoInt) {
    int i = 0;
    for (i = 0; i < 4; i++) {
      int mascara = 0;
      mascara = 0b11111111 << (i * 8);
      if ((mascara & ultimoInt) != 0)
        break;
    }
    return i;
  }

  public int bytesVaziosManchester(int ultimoInt) {
    int i = 0;
    for (i = 0; i < 2; i++) {
      int mascara = 0;
      mascara = 0b1111111111111111 << (i * 16);
      if ((mascara & ultimoInt) != 0)
        break;
    }
    return i;
  }

  /* ****************** METODOS DE IMPRESSAO ****************** */
  public String imprimirBinario(int teste) {
    String a = "";
    a += String.format("%32s", Integer.toBinaryString(teste)).replace(' ', '0');
    a += "\n";
    return a;
  }

  public void imprimirVetor(int[] vetor) {
    for (int a : vetor) {
      String bits32 = String.format("%32s", Integer.toBinaryString(a)).replace(' ', '0');
      System.out.println(bits32);
    }
  }

} // fim da classe camadaFisicaTransmissora