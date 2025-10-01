/* ***************************************************************
* Autor............: Isis Caroline Lima Viana
* Matricula........: 202410016
* Inicio...........: 16/09/2025
* Ultima alteracao.: 25/09/2025
* Nome.............: CamadaEnlaceDadosTransmissora.java
* Funcao...........: Essa camada eh responsavel por ...
*************************************************************** */
package modelo;

//importando as bibliotecas necessarias
import java.util.ArrayDeque;

public class CamadaEnlaceDadosTransmissora {

  private CamadaFisicaTransmissora camada_Fisica_Transmissora;
  private int tipoDeEnquadramento;
  private final int contraBarra = 0b01011100, flag = 0b01111110;

  // Construtor:
  public CamadaEnlaceDadosTransmissora(CamadaFisicaTransmissora camada_Fisica_Transmissora, int tipoDeEnquadramento) {
    // camada fisica como parametro
    this.camada_Fisica_Transmissora = camada_Fisica_Transmissora;
    this.tipoDeEnquadramento = tipoDeEnquadramento;
  }

  /* ***************************************************************
   * Metodo: CamadaEnlaceDadosTransmissora
   * Funcao: chama o metodo de enquadramento e instancia seu retorno 
   * no array quadroEnquadrado. Por fim envia o quadroEnquadrado para 
   * a camada fisica transmissora
   * Parametros: int[] quadro (quadro sem enquadramento)
   * Retorno: int[] quadroEnquadrado
   ****************************************************************/
  public void camadaEnlaceDadosTransmissora(int quadro[]) {
    int[] quadroEnquadrado = camadaEnlaceDadosTransmissoraEnquadramento(quadro);
    // camadaDeEnlaceTransmissoraControleDeErro(quadro);
    // camadaDeEnlaceTransmissoraControleDeFluxo(quadro);
    // chama proxima camada
    camada_Fisica_Transmissora.camadaFisicaTransmissora(quadroEnquadrado);
  }// fim do metodo CamadaEnlaceDadosTransmissora

  /* ***************************************************************
   * Metodo: camadaEnlaceDadosTransmissoraEnquadramento
   * Funcao: chama o metodo de enquadramento equivalente ao tipo de
   * enquadramento selecionado no menu.
   * Parametros: int[] quadro (quadro sem enquadramento)
   * Retorno: int[] quadroEnquadrado
   ****************************************************************/
  public int[] camadaEnlaceDadosTransmissoraEnquadramento(int quadro[]) {
    int quadroEnquadrado[];
    switch (tipoDeEnquadramento) {
      case 0: // contagem de caracteres
        quadroEnquadrado = camadaEnlaceDadosTransmissoraEnquadramentoContagemDeCaracteres(quadro);
        break;
      case 1: // insercao de bytes
        quadroEnquadrado = camadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBytes(quadro);
        break;
      case 2: // insercao de bits
        quadroEnquadrado = camadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBits(quadro);
        break;
      case 3: // violacao da camada fisica
        quadroEnquadrado = camadaEnlaceDadosTransmissoraEnquadramentoViolacaoDaCamadaFisica(quadro);
        break;
      default:
        quadroEnquadrado = quadro;
    }// fim do switch/case
    System.out.println("\nCamada de Enlace de Dados:");
    imprimir(quadroEnquadrado);
    return quadroEnquadrado;

  }// fim do metodo CamadaEnlaceTransmissoraEnquadramento

  /* ***************************************************************
   * Metodo: CamadaEnlaceDadosTransmissoraContagemDeCaracteres
   * Funcao: Identifica qual numero foi informado em num (mudanca 
   * possivel apenas no codigo para fins de escalabilidade). Coloca 
   * esse numero no array quadroEnquadrado e inicia um loop onde a 
   * cada n bytes insere esse numero n de novo ate que todos os bytes
   * do quadro original estejam no quadroEnquadrado
   * Parametros: int[] quadro (quadro sem enquadramento)
   * Retorno: int[] quadroEnquadrado
   ****************************************************************/
  public int[] camadaEnlaceDadosTransmissoraEnquadramentoContagemDeCaracteres(int quadro[]) {
    // implementacao do algoritmo
    int num = 0b00000011; //3
    System.out.println("Contagem de caracteres: " + num);
    int quantosBytes = (quadro.length - 1) * 4 + (4 - bytesVazios(quadro[quadro.length - 1]));
    int quantosCabecalhos = (quantosBytes + (num - 2)) / (num-1);
    int totalBytes = quantosCabecalhos + quantosBytes;
    int tamanhoNovoQuadro =  (totalBytes+ 3) / 4;
    int[] quadroEnquadrado = new int[tamanhoNovoQuadro];

    int contQuadroEnquadrado = 0, contQuadro = 0, k = 32, contInt = 0, contBytesEnquadrados = 0;
    quadroEnquadrado[0]=0;
    while (quantosCabecalhos != 0) {
      k-=8;
      quadroEnquadrado[contQuadroEnquadrado] |= num << k;
      contBytesEnquadrados++;
      quantosCabecalhos--;
      for (int i = num; i >1; i--) { // coloca caracter por caracter no novo quadro
        if (contBytesEnquadrados % 4 == 0) {
          contQuadroEnquadrado++;
          if(contQuadroEnquadrado<quadroEnquadrado.length)
          quadroEnquadrado[contQuadroEnquadrado]=0;
          else break;
          k = 32;
        }
        for (int j = 31; j >= 24; j--) {
          k--;
          int masc = 1 << (j - (contInt * 8));
          if ((masc & quadro[contQuadro]) != 0) {
            quadroEnquadrado[contQuadroEnquadrado] |= 1 << k;
          }
        }

        contBytesEnquadrados++;
        contInt = (contInt + 1) % 4;
        if (contInt == 0){
          contQuadro++;
        }
        if(contBytesEnquadrados==totalBytes) break;

      } // fim do for de contagem de caracteres
      if (contBytesEnquadrados % 4 == 0) {
          k = 32;
          contQuadroEnquadrado++;
          if(contQuadroEnquadrado<quadroEnquadrado.length)
            quadroEnquadrado[contQuadroEnquadrado]=0;
      }
    } // fim do while
    //imprimir(quadroEnquadrado);
    return quadroEnquadrado;
  }// fim do metodo CamadaEnlaceDadosTransmissoraContagemDeCaracteres

  /* ***************************************************************
   * Metodo: camadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBytes
   * Funcao: vai copiando os bytes do quadro original para o quadroEnquadrado
   * e entre os quadros insere o caracter de contrabarra(\). Caso a mensagem
   * original possua uma contrabarra insere-se uma outra contrabarra antes
   * daquela na mensagem.
   * Parametros: int[] quadro (quadro sem enquadramento)
   * Retorno: int[] quadroEnquadrado
   ****************************************************************/
  public int[] camadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBytes(int quadro[]) {
    // implementacao do algoritmo
    int num = 3;  //segui usando o num para definir o tamanho do quadro por
                  //que meu quadro precisa de um tamanho
    System.out.println("Insercao de Bytes: " + num);
    int quantosBytes = (quadro.length - 1) * 4 + (4 - bytesVazios(quadro[quadro.length - 1]));
    int quantosCabecalhos = (quantosBytes + (num - 2)) / (num-1)+1;
    int totalBytes = quantosCabecalhos + quantosBytes+bucarPadrao(contraBarra, quadro);
    int tamanhoNovoQuadro =  (totalBytes+ 3) / 4;
    int[] quadroEnquadrado = new int[tamanhoNovoQuadro];

    int contQuadroEnquadrado = 0, contQuadro = 0, k = 32, contInt = 0, contBytesEnquadrados = 0;
    quadroEnquadrado[0]=0;
    while (quantosCabecalhos != 1) {
      k-=8;
      quadroEnquadrado[contQuadroEnquadrado] |= contraBarra << k;
      contBytesEnquadrados++;
      quantosCabecalhos--;
      for (int i = num; i >1; i--) { // coloca caracter por caracter no novo quadro
        if (contBytesEnquadrados % 4 == 0) {
          contQuadroEnquadrado++;
          if(contQuadroEnquadrado<quadroEnquadrado.length)
            quadroEnquadrado[contQuadroEnquadrado]=0;
          else break;
          k = 32;
        }
        int byteReconhecido=0;
        for (int j = 31; j >= 24; j--) {
          k--;
          int masc = 1 << (j - (contInt * 8));
          if ((masc & quadro[contQuadro]) != 0) {
            quadroEnquadrado[contQuadroEnquadrado] |= 1 << k;
            byteReconhecido |= 1<<(j-24);
          }
        }
        contBytesEnquadrados++;
        if(byteReconhecido==contraBarra){
          if (contBytesEnquadrados % 4 == 0) {
            k = 32;
            contQuadroEnquadrado++;
            quadroEnquadrado[contQuadroEnquadrado]=0;
          }
          k-=8;
          quadroEnquadrado[contQuadroEnquadrado] |= contraBarra << k;
          contBytesEnquadrados++;
        }

        contInt = (contInt + 1) % 4;
        if (contInt == 0){
          contQuadro++;
        }
        if(contBytesEnquadrados==totalBytes-1) break;

      } // fim do for de contagem de caracteres
      if (contBytesEnquadrados % 4 == 0) {
          k = 32;
          contQuadroEnquadrado++;
          if(contQuadroEnquadrado<quadroEnquadrado.length)
            quadroEnquadrado[contQuadroEnquadrado]=0;
      }
    } // fim do while
    k-=8;
    quadroEnquadrado[contQuadroEnquadrado] |= contraBarra << k;
    imprimir(quadroEnquadrado);
    return quadroEnquadrado;
  }// fim do metodo CamadaEnlaceDadosTransmissoraInsercaoDeBytes

  //viu 5 1's coloca um 0
  
  /* ***************************************************************
   * Metodo: camadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBytes
   * Funcao: No inicio chama a funcao insereBits0 que vai inserir os 0 
   * extras. E, depois disso, vai copiando os bites do quadro com zeros 
   * extras para o quadroEnquadrado e, a cada byte insere a flag 01111110.
   * Parametros: int[] quadro (quadro sem enquadramento)
   * Retorno: int[] quadroEnquadrado
   ****************************************************************/
  public int[] camadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBits(int quadro[]) {
    // implementacao do algoritmo
    int num = 3;
    System.out.println("Insercao de Bytes: " + num);
    int quantosBytes = (quadro.length - 1) * 4 + (4 - bytesVazios(quadro[quadro.length - 1]));
    
    int zerosInseridos = buscarUns(quadro);
    int[] totalBits = new int[]{0};

    quadro = insereBits0(quadro, ((quantosBytes*8)+zerosInseridos+31)/32, totalBits);
    //System.out.println("Com os 0 inseridos:");
    //imprimir(quadro);
    quantosBytes = (quadro.length - 1) * 4 + (4 - bytesVazios(quadro[quadro.length - 1]));
    int quantosCabecalhos = (quantosBytes + (num - 2)) / (num-1)+1;
    int totalBytes = quantosCabecalhos + quantosBytes+bucarPadrao(flag, quadro);
    int tamanhoNovoQuadro =  (totalBytes+ 3) / 4;
    int[] quadroEnquadrado = new int[tamanhoNovoQuadro];

    int contQuadroEnquadrado = 0, contQuadro = 0, k = 32, contInt = 0, contBytesEnquadrados = 0;
    quadroEnquadrado[0]=0;
    while (quantosCabecalhos != 1) {
      k-=8;
      quadroEnquadrado[contQuadroEnquadrado] |= flag << k;
      contBytesEnquadrados++;
      quantosCabecalhos--;
      for (int i = num; i >1; i--) { // coloca caracter por caracter no novo quadro
        if (contBytesEnquadrados % 4 == 0) {
          contQuadroEnquadrado++;
          if(contQuadroEnquadrado<quadroEnquadrado.length)
            quadroEnquadrado[contQuadroEnquadrado]=0;
          else break;
          k = 32;
        }
        int byteReconhecido=0;
        for (int j = 31; j >= 24; j--) {
          k--;
          int masc = 1 << (j - (contInt * 8));
          if ((masc & quadro[contQuadro]) != 0) {
            quadroEnquadrado[contQuadroEnquadrado] |= 1 << k;
            byteReconhecido |= 1<<(j-24);
          }
        }
        contBytesEnquadrados++;
        if(byteReconhecido==contraBarra){
          if (contBytesEnquadrados % 4 == 0) {
            k = 32;
            contQuadroEnquadrado++;
            quadroEnquadrado[contQuadroEnquadrado]=0;
          }
          k-=8;
          quadroEnquadrado[contQuadroEnquadrado] |= flag << k;
          contBytesEnquadrados++;
        }

        contInt = (contInt + 1) % 4;
        if (contInt == 0){
          contQuadro++;
        }
        if(contBytesEnquadrados==totalBytes-1) break;

      } // fim do for de contagem de caracteres
      if (contBytesEnquadrados % 4 == 0) {
          k = 32;
          contQuadroEnquadrado++;
          if(contQuadroEnquadrado<quadroEnquadrado.length)
            quadroEnquadrado[contQuadroEnquadrado]=0;
      }
    } // fim do while
    k-=8;
    quadroEnquadrado[contQuadroEnquadrado] |= flag << k;
    //System.out.println("Enquadrado:");
    //imprimir(quadroEnquadrado);
    return quadroEnquadrado;
    //return new int[]{0b01111110011000010111110101111110, 0b00110000101111101000000001111110};
  }// fim do metodo CamadaEnlaceDadosTransmissoraInsercaoDeBits

  /* ***************************************************************
   * Metodo: camadaEnlaceDadosTransmissoraEnquadramentoViolacaoDaCamadaFisica
   * Funcao: Esse metodo de enquadramento delega o enquadramento para a camada
   * fisica, portanto retorna a mesma coisa
   * Parametros: int[] quadro (quadro sem enquadramento)
   * Retorno: int[] quadro (ainda sem enquadramento)
   ****************************************************************/
  public int[] camadaEnlaceDadosTransmissoraEnquadramentoViolacaoDaCamadaFisica(int quadro[]) {
    // implementacao do algoritmo
    System.out.println("Violacao da camada fisica");
    return quadro;
  }// fim do metodo CamadaEnlaceDadosTransmissoraViolacaoDaCamadaFisica

  public void CamadaEnlaceDadosTransmissoraControleDeErro(int quadro[]) {
    // algum codigo aqui
  }// fim do metodo CamadaEnlaceDadosTransmissoraControleDeErro

  public void CamadaEnlaceDadosTransmissoraControleDeFluxo(int quadro[]) {
    // algum codigo aqui
  }// fim do metodo CamadaEnlaceDadosTransmissoraControleDeFluxo

  /* ****************** METODOS AUXILIARES ****************** */
  //conta quantos bytes vazios tem num int
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

  //conta quantas vezes um padrao apareceu no quadro
  public int bucarPadrao (int padrao, int[] quadro){
    int cont=0;
    for(int i=0; i<quadro.length; i++){
      for(int j=0; j<4; j++){
        int byteReconhecido =0;
        for(int k=31; k>=24; k--){
          int masc = 1 << (k-(j*8));
          if ((masc & quadro[i])!=0)
            byteReconhecido |= 1<<(k-24);
        }
        if(byteReconhecido==padrao) cont++;
      }
    }
    return cont;
  }

  //conta quantos 0 deverao ser inseridos
  //(quantas sequencias 11111 a mensagem tem)
  public int buscarUns (int[] quadro){
    int cont=0;
    ArrayDeque<Boolean> sequenciaBits = new ArrayDeque<>(5);
    for(int i=0; i<quadro.length; i++){
      for(int j=31; j>0; j--){
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

  /* ***************************************************************
   * Metodo: insereBits0
   * Funcao: Transfere bit a bit do quadro original para o novoQuadro
   * ao mesmo tempo em que vai preenchendo um deque circular de 5 
   * posições com dados booleanos (true se inseriu 1, false se inseriu 0)
   * se todas as posicoes do deque forem true significa que a mensagem teve
   * 11111 e, por isso insere um 0 no novoQuadro.
   * Parametros: int[] quadro (quadro sem enquadramento)
   * Retorno: int[] quadro com 0s
   ****************************************************************/
  public int[] insereBits0(int[] quadro, int tamanho, int[] cont){
    int[] novoQuadro = new int[tamanho];
    int contNovoQuadro=0, k=32;
    novoQuadro[0]=0;
    ArrayDeque<Boolean> sequenciaBits = new ArrayDeque<>(5);
    for(int i=0; i<quadro.length; i++){
      for(int j=31; j>=0; j--){
        if (sequenciaBits.size() == 5) sequenciaBits.removeFirst();
        int masc = 0| 1<<j;
        k--;
        if((masc&quadro[i])!=0){
          sequenciaBits.addLast(true);
          novoQuadro[contNovoQuadro] |= 1<<k;
        }else{
          sequenciaBits.addLast(false);
        }
        cont[0]++;
        if (sequenciaBits.size() == 5 && sequenciaBits.stream().allMatch(b -> b)) {
          k--; //adiciona 0
          sequenciaBits.clear(); // ou continue monitorando
          cont[0]++;
        }
        if(k==0){
          contNovoQuadro++;
          if(contNovoQuadro<novoQuadro.length){
            novoQuadro[contNovoQuadro] = 0;
            k=32;
          } //fim do if
        } // fim do if k=0
      } //fim do for j (loop int do quadro)
    } //fim do for i (loop das posicoes do quadro)
    return novoQuadro;
  } //fim do metodo

  //chama o metodo parar da camada fisica
  public void parar() {
    camada_Fisica_Transmissora.parar();
  }
  //transfere a nova taxa de erro para a camada fisica
  public void setTaxaDeErro(int taxa) {
    camada_Fisica_Transmissora.setTaxaDeErro(taxa);
  }

  /* ****************** METODOS DE IMPRESSAO ****************** */
  public void imprimir(int[] vetor) {
    for (int a : vetor) {
      String bits32 = String.format("%32s", Integer.toBinaryString(a)).replace(' ', '0');
      System.out.println(bits32);
    }
  }

} // fim da classe CamadaEnlaceDadosTransmissora