/* ***************************************************************
* Autor............: Isis Caroline Lima Viana
* Matricula........: 202410016
* Inicio...........: 16/09/2025
* Ultima alteracao.: 29/09/2025
* Nome.............: CamadaEnlaceDadosReceptora.java
* Funcao...........: Essa camada eh responsavel por ...
*************************************************************** */
package modelo;

//importando as bibliotecas necessarias
import java.util.ArrayDeque;  //usada para procurar sequencia de 11111


public class CamadaEnlaceDadosReceptora {
  private CamadaAplicacaoReceptora camada_Aplicacao_Receptora;
  private int tipoDeEnquadramento;
  private final int contraBarra = 0b01011100, flag = 0b01111110;  //constantes

  // Construtor:
  public CamadaEnlaceDadosReceptora(CamadaAplicacaoReceptora camada_Aplicacao_Receptora, int tipoDeEnquadramento) {
    this.camada_Aplicacao_Receptora = camada_Aplicacao_Receptora;
    this.tipoDeEnquadramento = tipoDeEnquadramento;
    // camada fisica como parametro
  }

  /* ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraReceptora
   * Funcao: recebe o quadro enquadrado, chama o metodo de desenquadramento
   * pra ele e o encaminha para a camada de Aplicação Receptora
   * Parametros: int[] quadro enquadrado
   * Retorno: vazio
   ****************************************************************/
  public void camadaEnlaceDadosReceptora(int quadro[]) {
    int[] quadroDesenquadrado = camadaEnlaceDadosReceptoraEnquadramento(quadro);
    // camadaDeEnlaceReceptoraControleDeErro(quadro);
    // camadaDeEnlaceReceptoraControleDeFluxo(quadro);

    //envia para a proxima camada:
    camada_Aplicacao_Receptora.camadaAplicacaoReceptora(quadroDesenquadrado);

  }// fim do metodo CamadaEnlaceDadosReceptora

  /* ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraReceptoraEnquadramento
   * Funcao: recebe o quadro enquadrado, chama o metodo de desenquadramento
   * equivalente ao tipoDeEnquadramento escolhido
   * Parametros: int[] quadro enquadrado
   * Retorno: quadroDesenquadrado
   ****************************************************************/
  public int[] camadaEnlaceDadosReceptoraEnquadramento(int quadro[]) {
    int quadroDesenquadrado[];
    switch (tipoDeEnquadramento) {
      case 0: // contagem de caracteres
        quadroDesenquadrado = camadaEnlaceDadosReceptoraEnquadramentoContagemDeCaracteres(quadro);
        break;
      case 1: // insercao de bytes
        quadroDesenquadrado = camadaEnlaceDadosReceptoraEnquadramentoInsercaoDeBytes(quadro);
        break;
      case 2: // insercao de bits
        quadroDesenquadrado = camadaEnlaceDadosReceptoraEnquadramentoInsercaoDeBits(quadro);
        break;
      case 3: // violacao da camada fisica
        quadroDesenquadrado = camadaEnlaceDadosReceptoraEnquadramentoViolacaoDaCamadaFisica(quadro);
        break;
      default:
        quadroDesenquadrado = null;
    }// fim do switch/case
    System.out.println("\nCamada de Enlace de Dados Receptora:");
    imprimir(quadroDesenquadrado);
    return quadroDesenquadrado;

  }// fim do metodo CamadaEnlaceReceptoraEnquadramento

  /* ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraContagemDeCaracteres
   * Funcao: recebe o quadro enquadrado, le o numero guardado no primeiro
   * byte e a partir desse valor n copia para o array quadroDesenquadrado
   * n os bits seguintes. Feito isso le-se o o proximo byte para se saber
   * quantos n bits ler no proximo bloco    
   * Parametros: int[] quadro enquadrado
   * Retorno: quadroDesenquadrado
   ****************************************************************/
  public int[] camadaEnlaceDadosReceptoraEnquadramentoContagemDeCaracteres(int quadro[]) {
    int num = lerNumero(quadro[0]);
    System.out.println("Contagem de caracteres: " + num);
    //imprimir(quadro);
    int bytesUteis = quadro.length * 4 - ((quadro.length * 4 + num - 1) / num);

    // Calcula quantos inteiros são necessários para os bytes uteis
    int tamanhoOriginal = (bytesUteis + 3) / 4; // Arredonda para cima
    int[] quadroDesenquadrado = new int[tamanhoOriginal];

    int contInt = 0, contNovoQuadro = 0, contQuadro = 0, k = 32; 
    int contBytesDesenquadrados = num - 1;
    int byteLido=0;
    try{
    quadroDesenquadrado[0] = 0;
    
      while (bytesUteis != 0) {
        if (contBytesDesenquadrados == (num - 1)) {
          contBytesDesenquadrados = 0;
          byteLido = 0;
          for (int i = 31; i >= 24; i--) {
            int masc = 0 | 1 << (i - contInt * 8);
            if ((masc & quadro[contQuadro]) != 0){
              byteLido |= 1<<(i-24);
            }
          }
          if(byteLido>0)
            num=byteLido;
        } else {
          // ler byte:
          for (int i = 31; i >= 24; i--) {
            k--;
            int masc = 0 | 1 << (i - contInt * 8);
            if ((masc & quadro[contQuadro]) != 0){
              quadroDesenquadrado[contNovoQuadro] |= 1 << k;
            }
          }
          // verificacoes:
          if (k == 0) {
            k = 32;
            contNovoQuadro++;
            if (contNovoQuadro < quadroDesenquadrado.length)
              quadroDesenquadrado[contNovoQuadro] = 0;
          }
          contBytesDesenquadrados++;
          bytesUteis--;
        }
        contInt = (contInt + 1) % 4;
        if (contInt == 0) {
          contQuadro++;
        }
      }
    }catch(Exception a){
      System.out.println("Erro intransponivel");
      return new int[]{0b01001101011001010110111001110011,
                      0b01100001011001110110010101101101,
                      0b00100000011011101110001101101111,
                      0b00100000011001010110111001110110,
                      0b01101001011000010110010001100001,
                      0b00100001001000000100010101110010,
                      0b01110010011011110010000001101110,
                      0b01101111011100110010000001100010,
                      0b01101001011100110111010000100000,
                      0b01100100011001010010000001100011,
                      0b01101111011011100111010001110010,
                      0b01101111011011000110010100000000};
    }
    
    /*System.out.println("\nDESENQUADRADO:");
    imprimir(quadroDesenquadrado);
    System.out.println();*/
    return quadroDesenquadrado;
  }// fim do metodo CamadaEnlaceDadosReceptoraContagemDeCaracteres
  
  /* ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraInsercaoDeBytes
   * Funcao: recebe o quadro enquadrado, procura a flag, enquanto nao
   * a encontra vai juntando os demais bytes no caminho.
   * Parametros: int[] quadro enquadrado
   * Retorno: quadroDesenquadrado
   ****************************************************************/
  public int[] camadaEnlaceDadosReceptoraEnquadramentoInsercaoDeBytes(int quadro[]) {
    System.out.println("Insercao de Bytes");
    int totalBytes = (quadro.length*4)-bytesVazios(quadro[quadro.length-1]);
    int tam = (totalBytes - contarPadrao(contraBarra, quadro)+3)/4;
    int[] novoQuadro= new int[tam];
    boolean garanteByte = false;
    int contNovoQuadro=0, k=32, contInt=0, contQuadro=0;
    novoQuadro[0]= 0;

    while(totalBytes>1){
      for(int bytes=0; bytes<4; bytes++){
        int byteReconhecido=0;
        for(int j=31; j>=24; j--){
          int masc = 0| 1<<(j-contInt*8);
          if((masc & quadro[contQuadro])!=0)
            byteReconhecido |= 1<<(j-24);
        }
        if(byteReconhecido == 0b00000000){
          garanteByte = false;
        } else if(garanteByte || byteReconhecido!=contraBarra){
          k-=8;
          if(contNovoQuadro<tam)
            novoQuadro[contNovoQuadro] |= byteReconhecido<<k;
          garanteByte = false;
          if(k==0) {
            k=32;
            contNovoQuadro++;
            if(contNovoQuadro<tam)
              novoQuadro[contNovoQuadro]=0;
          }
        }else{
          garanteByte = true;
        }
        contInt = (contInt+1)%4;
        totalBytes--;
      }
      contQuadro++;
    }
    /*System.out.println("\nDESENQUADRADO:");
    imprimir(novoQuadro);
    System.out.println();*/
    return novoQuadro;
  }// fim do metodo CamadaEnlaceDadosReceptoraInsercaoDeBytes

  /* ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraInsercaoDeBits
   * Funcao: recebe o quadro enquadrado, retira todas as flags e une
   * os quadros num unico bloco e, por fim chama-se o metodo tirarBits0
   * para tirar os 0 extras incluidos para proteger a mensagem  
   * Parametros: int[] quadro enquadrado
   * Retorno: quadroDesenquadrado
   ****************************************************************/
  public int[] camadaEnlaceDadosReceptoraEnquadramentoInsercaoDeBits(int quadro[]) {
    // implementacao do algoritmo
    //imprimir(quadro);
    System.out.println("Insercao de Bits");
    int totalBytes = (quadro.length*4)-bytesVazios(quadro[quadro.length-1]);
    int tam = (totalBytes - contarPadrao(flag, quadro)+3)/4;
    int[] novoQuadro= new int[tam];
    int contNovoQuadro=0, k=32, contInt=0, contQuadro=0;
    novoQuadro[0]= 0;

    while(totalBytes>0){
      for(int bytes=0; bytes<4; bytes++){
        int byteReconhecido=0;
        for(int j=31; j>=24; j--){
          int masc = 0| 1<<(j-contInt*8);
          if((masc & quadro[contQuadro])!=0)
            byteReconhecido |= 1<<(j-24);
        }
        if(byteReconhecido!=0b00000000 && byteReconhecido!=flag){
          k-=8;
          if(contNovoQuadro<tam)
            novoQuadro[contNovoQuadro] |= byteReconhecido<<k;
          
          if(k==0) {
            k=32;
            contNovoQuadro++;
            if(contNovoQuadro<tam)
              novoQuadro[contNovoQuadro]=0;
          }
        }
        contInt = (contInt+1)%4;
        totalBytes--;
      }
      contQuadro++;
    }

    novoQuadro = tirarBits0(novoQuadro);

    //System.out.println("\nDESENQUADRADO:");
    //imprimir(novoQuadro);
    //System.out.println();

    return novoQuadro;
  }// fim do metodo CamadaEnlaceDadosReceptoraInsercaoDeBits

  /* ***************************************************************
   * Metodo: CamadaEnlaceDadosReceptoraViolacaoDaCamadaFisica
   * Funcao: nao ha muito o que fazer com o quadro nessa camada entao
   * apenas retorna o quadro do jeito que ele veio, pois vai ser trabalho
   * da camada fisica lidar com o enquadramento.   
   * Parametros: int[] quadro enquadrado
   * Retorno: quadroDesenquadrado
   ****************************************************************/
  public int[] camadaEnlaceDadosReceptoraEnquadramentoViolacaoDaCamadaFisica(int quadro[]) {
    // implementacao do algoritmo
    System.out.println("Violacao da camada fisica");
    return quadro;
  }// fim do metodo CamadaEnlaceDadosReceptoraViolacaoDaCamadaFisica

  public void camadaEnlaceDadosReceptoraControleDeErro(int quadro[]) {
    // algum codigo aqui
  }// fim do metodo CamadaEnlaceDadosReceptoraControleDeErro

  public void camadaEnlaceDadosReceptoraControleDeFluxo(int quadro[]) {
    // algum codigo aqui
  }// fim do metodo CamadaEnlaceDadosReceptoraControleDeFluxo

  /* ****************** METODOS AUXILIARES ****************** */
  //conta quantos bytes vazios tem no int
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

  //le os primeiros 8 bits do int e retorna o numero 
  //que aquele byte diz respeito
  public int lerNumero(int primeiroInt) {
    int num = 0;
    for (int i = 31; i >= 24; i--) {
      int masc = 0 | 1 << i;
      if ((masc & primeiroInt) != 0)
        num |= 1 << (i - 24);
    }
    return num;
  }

  //conta quantas vezes o padrao apareceu no quadro
  public int contarPadrao (int padrao, int[] quadro){
    int cont=0;
    for(int i=0; i<quadro.length; i++){
      for(int j=0; j<4; j++){
        int byteReconhecido =0;
        for(int k=31; k>=24; k--){
          int masc = 1 << (k-(j*8));
          if ((masc & quadro[i])!=0)
            byteReconhecido |= 1<<(k-24);
        }
        if(byteReconhecido==padrao) {cont++; j++;}
      }
    }
    //System.out.println("Contador de flags:"+cont);
    return cont;
  }

  //conta quantas sequencias de 11111 tivemos no quadro
  public int buscarUns (int[] quadro){
    int cont=0;
    ArrayDeque<Boolean> sequenciaBits = new ArrayDeque<>(5);
    for(int i=0; i<quadro.length; i++){
      for(int j=31; j>=0; j--){
        if (sequenciaBits.size() == 5) sequenciaBits.removeFirst();
        int masc = 1<<j;
        if((masc&quadro[i])!=0){
          sequenciaBits.addLast(true);
        }else{
          sequenciaBits.addLast(false);
        }
        if (sequenciaBits.size() == 5 && sequenciaBits.stream().allMatch(b -> b)) {
          cont++;
          sequenciaBits.clear(); // ou continue monitorando
        }
      }
    }
    return cont;
  }

  //quando ve 111110 tira esse 0 para retornar a mensagem original
  public int[] tirarBits0(int[] quadro){
    int tamEmBits = (quadro.length*32)-buscarUns(quadro);
    int tam = (tamEmBits +31)/32; //arredonda pra cima
    int[] quadroSemZero = new int[tam];
    int k=32, contQ=0;

    quadroSemZero[0]=0;

    ArrayDeque<Boolean> sequenciaBits = new ArrayDeque<>(5);
    for(int i=0; i<quadro.length; i++){
      for(int j=31; j>=0; j--){
        k--;
        if(k<0){ 
          k=31;
          contQ++;
          if(contQ<tam)
            quadroSemZero[contQ]=0;
          else
            break;
        }
        if (sequenciaBits.size() == 5) sequenciaBits.removeFirst();
        int masc = 1<<j;
        if((masc&quadro[i])!=0){
          sequenciaBits.addLast(true);
          quadroSemZero[contQ] |= 1<<k;
        }else{
          sequenciaBits.addLast(false);
        }
        if (sequenciaBits.size() == 5 && sequenciaBits.stream().allMatch(b -> b)) {
          sequenciaBits.clear(); // ou continue monitorando
          j--;
        }
      }
    }
    return quadroSemZero;
  }

  //chama o metodo transportaErro da camada_Aplicacao_Receptora
  public void transportaErro(boolean erros){
    camada_Aplicacao_Receptora.transportaErro(erros);
  }

  /* ****************** METODOS DE IMPRESSAO ****************** */
  public void imprimir(int[] vetor) {
    for (int a : vetor) {
      String bits32 = String.format("%32s", Integer.toBinaryString(a)).replace(' ', '0');
      System.out.println(bits32);
    }
  }

} // fim da classe CamadaEnlaceDadosReceptora