/* ***************************************************************
* Autor............: Isis Caroline Lima Viana
* Matricula........: 202410016
* Inicio...........: 16/08/2025
* Ultima alteracao.: 29/09/2025
* Nome.............: CamadaFisicaReceptora.java
* Funcao...........: Essa camada eh responsavel por decodificar o 
sinal recebido em binario comum a partir da codificacao escolhida 
(binario, manchester ou manchester diferencial) e enviar a mensagem 
escolhida para a CamadaAplicacaoReceptora
****************************************************************/
package modelo;

public class CamadaFisicaReceptora {

  private CamadaEnlaceDadosReceptora camada_Enlace_Dados_Receptora;
  private int tipoDeDecodificacao;
  private int tipoDeEnquadramento;

  // Construtor
  public CamadaFisicaReceptora(CamadaEnlaceDadosReceptora camada_Enlace_Dados_Receptora, int tipoDeDecodificacao,
      int tipoDeEnquadramento) {
    this.camada_Enlace_Dados_Receptora = camada_Enlace_Dados_Receptora;
    this.tipoDeDecodificacao = tipoDeDecodificacao;
    this.tipoDeEnquadramento = tipoDeEnquadramento;
    // fazer animacao da impressora
  }

  /* ***************************************************************
   * Metodo: camadaFisicaReceptora
   * Funcao: verifica qual a codificacao escolhida e chama o metodo
   * equivalente a essa escolha, por fim envia o fluxoBrutoDeBits
   * codificados para a camadaEnlaceDadosReceptora
   * Parametros: int[] (mensagem codificada no tipo escolhido)
   * Retorno: vazio
   ****************************************************************/
  public void camadaFisicaReceptora(int quadro[]) {
    int fluxoBrutoDeBits[] = new int[(int) quadro.length / 2];
    switch (tipoDeDecodificacao) {
      case 0: // codificao binaria
        fluxoBrutoDeBits = camadaFisicaReceptoraDecodificacaoBinaria(quadro);
        break;
      case 1: // codificacao manchester
        if (tipoDeEnquadramento != 3) {
          fluxoBrutoDeBits = camadaFisicaReceptoraDecodificacaoManchester(quadro);
        } else {
          fluxoBrutoDeBits = violacaoDaCamadaFisica(quadro);
        }
        break;
      case 2: // codificacao manchester diferencial
        if (tipoDeEnquadramento != 3) {
          fluxoBrutoDeBits = camadaFisicaReceptoraDecodificacaoManchesterDiferencial(quadro);
        } else {
          fluxoBrutoDeBits = violacaoDaCamadaFisica(quadro);
        }
        break;
    }// fim do switch/case
     // chama proxima camada
    System.out.println("\nCamada Fisica Receptora:\n"+imprimirVetor(fluxoBrutoDeBits));
    camada_Enlace_Dados_Receptora.camadaEnlaceDadosReceptora(fluxoBrutoDeBits);
  }// fim do metodo CamadaFisicaTransmissora

  /* ***************************************************************
   * Metodo: camadaFisicaReceptoraDecodificacaoBinaria
   * Funcao: a mensagem ja esta em AscII binario, entao apenas se
   * retorna o quadro enviado como parametro
   * Parametros: int[] (mensagem codificada em binario)
   * Retorno: int[] (mensagem codificada em binario)
   ****************************************************************/
  public int[] camadaFisicaReceptoraDecodificacaoBinaria(int quadro[]) {
    return quadro;
  }// fim do metodo CamadaFisicaReceptoraDecodificacaoBinaria

  /* ***************************************************************
   * Metodo: camadaFisicaReceptoraDecodificacaoManchester
   * Funcao: verifica os bits de 2 em 2, se achar a sequencia 10
   * escreve 1 na posicao (iteracao) em que ele foi achado no Array
   * quadroDecodificado
   * Parametros: int[] (mensagem codificada em manchester)
   * Retorno: int[] (mensagem codificada em binario)
   ****************************************************************/
  public int[] camadaFisicaReceptoraDecodificacaoManchester(int quadro[]) {
    int indexQuadro = 0, k;
    int tam = (quadro.length + 2 - 1) / 2; // arredonda pra cima
    int[] quadroDecodificado = new int[tam];
    //System.out.println(imprimirBinario(quadro[0]));
    for (int i = 0; i < quadroDecodificado.length; i++) {
      k = 31;
      quadroDecodificado[i] = 0; // zera todas as posicoes
      for (int j = 31; j >= 0; j--) {

        if (j == 15) {
          indexQuadro++;
          k = 31;
        } // cada quadroDecodificado[] guarda 4 caracteres
        // entao na metade k volta pro final
        if (indexQuadro >= quadro.length)
          break;
        // se o par for 10 entao escreve um 1
        if (((quadro[indexQuadro] & 1 << k) != 0) && ((quadro[indexQuadro] & 1 << (k - 1)) == 0))
          quadroDecodificado[i] = (quadroDecodificado[i] | 1 << j);
        else if (((quadro[indexQuadro] & 1 << k) != 0) && ((quadro[indexQuadro] & 1 << (k - 1)) != 0))
          quadroDecodificado[i] = (quadroDecodificado[i] | 1 << j); // esse da erro, mas nao sei como sinalizar
        k -= 2; // verifica o proximo par
      }
      indexQuadro++;
    }
    return quadroDecodificado;
  }// fim do metodo CamadaFisicaReceptoraDecodificacaoManchester

  /* ***************************************************************
   * Metodo: CamadaFisicaReceptoraDecodificacaoManchesterDiferencial
   * Funcao: verifica os bits de 2 em 2, se ouve mudanca de um bit
   * para o outro escreve 0, se nao escreve 1
   * Parametros: int[] (mensagem codificada em manchester diferencial)
   * Retorno: int[] (mensagem codificada em binario)
   ****************************************************************/
  public int[] camadaFisicaReceptoraDecodificacaoManchesterDiferencial(int quadro[]) {

    boolean estadoAnterior = true; // to usando boolean pq int ia embolar tudo 0=false e 1=true
    int indexQuadro = 0, k = 31;
    int tam = (quadro.length + 2 - 1) / 2; // arredonda pra cima
    int[] quadroDecodificado = new int[tam];

    for (int i = 0; i < quadro.length; i++) {

      for (int j = 31; j >= 0; j -= 2) {
        int a = (quadro[i] & 1 << j);
        int b = (quadro[i] & 1 << (j - 1));
        if ((a == 0) && (b == 0)) {
          break;
        }
        int mascara = 1 << j;
        boolean bitNovo = (quadro[i] & mascara) != 0;

        if (estadoAnterior == bitNovo) {
          quadroDecodificado[indexQuadro] |= 1 << k;
        }
        estadoAnterior = bitNovo;
        k--;
        if (k < 0) {
          k = 31;
          indexQuadro++;
        }
      }
    }
    return quadroDecodificado;
  }// fim do CamadaFisicaReceptoraDecodificacaoManchesterDiferencial

  /* ***************************************************************
   * Metodo: violacaoDaCamadaFisica
   * Funcao: metodo de desenquadramento que envolve identificar os quadros
   * com dois pares altos (11) no inicio e no fim. Vai copiando os bits do 
   * quadro enquadrado ate terminar todo o fluxo de bits
   * Parametros: int[] quadro (enquadrado)
   * Retorno: int[] quadroDesenquadrado
   ****************************************************************/
  public int[] violacaoDaCamadaFisica(int[] quadro) {
    //System.out.println("ORIGINAL:"+imprimirVetor(quadro));
    int num=7;  //tamanho do quadro
    int totalBytesCodificados = quadro.length*2-bytesVaziosManchester(quadro[quadro.length-1]);
    int totalPares = totalBytesCodificados*8;
    int tam = (totalBytesCodificados + 1) / 2; // arredonda pra cima
    int[] quadroDecodificado = new int[tam];
    int k=32, contDecofificado=0; //auxiliares do quadro decofificado
    int ponteiro=29, contQuadro=0;  //auxiliares do quadro original 
    int d=31, contD=0;  //auxiliares dos quadros individuais
    int[] quadroD = new int[(num+3)/4];
    quadroD[0]=0;

    totalPares--; //tira o primeiro 11

    while(totalPares>0){
      int a = (quadro[contQuadro] & 1 << ponteiro)==0?0:1;
      int b = (quadro[contQuadro] & 1 << (ponteiro - 1))==0?0:1;
      if(a==1 && b==1){
        //insere o quadro decodificado num unico bloco:
        for(int i=0; i<quadroD.length; i++){
          for(int j=31; j>=0; j-=2){
            int bit1 = (quadroD[i] >> j) & 1;
            int bit2 = (quadroD[i] >> (j-1)) & 1;
            k--;
            if(k<0){
              k=31;
              contDecofificado++;
              if(contDecofificado<quadroDecodificado.length)
                quadroDecodificado[contDecofificado]=0;
            }
            if (bit1!=0)
              quadroDecodificado[contDecofificado] |= 1<<k;
            k--;
            if (bit2!=0)
              quadroDecodificado[contDecofificado] |= 1<<k;
            if (bit1 == 0 && bit2 == 0) {k+=2;break;}
          }
        } //fim do inserir quadroD em quadroDecodificado
        quadroD = new int[(num+3)/4];
        quadroD[0]=0;
        d=31;
      } //fim do if fechou o quadro
      else{
        if(a==1)
          quadroD[0] |= 1<<d;
        if(b==1)
          quadroD[0] |= 1<<(d-1);
        d-=2;
        if(d<0){
          d=31;
          contD++;
          if(contD<quadroD.length)
            quadroD[contD]=0;
        }
      }
      ponteiro-=2;
      if(ponteiro<0) {
        ponteiro=31;
        contQuadro++;
      }
      totalPares--;
    } //fim do loop

    if (d != 31 || contD > 0) { // Se há dados no último quadroD
      for (int i = 0; i <= contD; i++) {
        for (int j = 31; j > 0; j -= 2) {
          if (k < 0) break;
          
          int bit1 = (quadroD[i] >> j) & 1;
          int bit2 = (quadroD[i] >> (j-1)) & 1;
          
          // Para no 00 legítimo
          if (bit1 == 0 && bit2 == 0) break;
          
          // Adiciona bit1
          k--;
          if (bit1 == 1) {
            quadroDecodificado[contDecofificado] |= (1 << k);
          }
          
          // Adiciona bit2  
          k--;
          if (bit2 == 1) {
            quadroDecodificado[contDecofificado] |= (1 << k);
          }
          
          if (k < 0) {
            k = 31;
            contDecofificado++;
            if (contDecofificado >= quadroDecodificado.length) break;
            quadroDecodificado[contDecofificado] = 0;
          }
        }
      }
    }
   
    /*System.out.println(("Antes da traducao:"));
    System.out.println(imprimirVetor(quadroDecodificado));
    System.out.println("AAA");*/
    if(tipoDeDecodificacao==1)
      quadroDecodificado = camadaFisicaReceptoraDecodificacaoManchester(quadroDecodificado);
    else if(tipoDeDecodificacao==2)
      quadroDecodificado = camadaFisicaReceptoraDecodificacaoManchesterDiferencial(quadroDecodificado);
    return quadroDecodificado;
  }

  /* ****************** METODOS AUXILIARES ****************** */
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
  
  public void transportaErro(boolean erros) {
    camada_Enlace_Dados_Receptora.transportaErro(erros);
  }
  
  /* ****************** METODOS DE IMPRESSAO ****************** */

  public String imprimirBinario(int teste) {
    String a = "";
    a += String.format("%32s", Integer.toBinaryString(teste)).replace(' ', '0');
    a += "\n";
    return a;
  }

  public String imprimirVetor(int[] vetor) {
    String a = "";
    for (int i : vetor) {
      a += imprimirBinario(i);
    }
    return a;
  }


} // fim da classe CamadaFisicaReceptora