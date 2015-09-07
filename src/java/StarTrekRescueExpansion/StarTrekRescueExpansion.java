package StarTrekRescueExpansion;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.Serializable;
import java.net.URL;
import java.util.Random;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.HttpServlet;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Vinicius Meca
 */
@ManagedBean(name = "StarTrekRescueExpansion")
@SessionScoped
public class StarTrekRescueExpansion extends HttpServlet implements Serializable {

    public int linhas = 10;
    public int colunas = 10;
    public Integer num_tripulantes = 3;
    public int[] acertou = new int[num_tripulantes];
    public int[] adjacente = new int[2];

    public int[][] planeta = new int[colunas][linhas];
    public int[][] tripulantes = new int[num_tripulantes][2];
    public int[] sinalizador = new int[2];
    public int tentativas;
    public int acertos;

    public int dificuldadeSelected;

    public void startSearch() {

        init();
        initPlaneta(getPlaneta());
        initTripulantes(getTripulantes());

    }

    //Metodo para iniciar do zero tentativas e acertos
    @Override
    public void init() {

        setTentativas(0);
        setAcertos(0);

    }
    //Fim Metodo para iniciar do zero tentativas e acertos    

    //Metodo para tocar som
    public void playSound(String nomeArquivo){
        URL url = getClass().getResource(nomeArquivo+".wav");
        AudioClip audio = Applet.newAudioClip(url);
        audio.play();
    }
    
    //Metodo Realiza busca enquanto nao encontrar todos os tripulantes perdidos
    public void searchTripulantes(int linha, int coluna) {
       
        playSound("sinalizador");

        int[] sinalizador_ = {linha, coluna};

        if (getDificuldadeSelected() == 0) {
            if (acertou(sinalizador_, getTripulantes())) {
                planeta[linha][coluna] = 1;
                acertos++;
                playSound("encontrado");
                if (getAcertos() == 3) {
                    RequestContext.getCurrentInstance().execute("PF('dialogFim').show()");
                    playSound("win");
                }
            } else if (verificaAdjacentesEasy(sinalizador_, getTripulantes(), getPlaneta())) {
                planeta[linha][coluna] = 2;
                setAdjascentesEasy(sinalizador_, getPlaneta(), 2);
                playSound("alerta");
            } else {
                planeta[linha][coluna] = 0;
                setAdjascentes(sinalizador_, getPlaneta(), 0);
            }
        } else {
            if (acertou(sinalizador_, getTripulantes())) {
                planeta[linha][coluna] = 1;
                acertos++;
                playSound("encontrado");
                if (getAcertos() == 3) {
                    RequestContext.getCurrentInstance().execute("PF('dialogFim').show()");
                    playSound("win");
                }
            } else if (verificaAdjacentes(sinalizador_, getTripulantes(), getPlaneta())) {
                planeta[linha][coluna] = 2;
                setAdjascentes(sinalizador_, getPlaneta(), 2);
                playSound("alerta");
            } else {
                planeta[linha][coluna] = 0;
                setAdjascentes(sinalizador_, getPlaneta(), 0);
            }
        }
        tentativas++;
    }
    //Fim metodo realiza busca enquanto nao encontrar todos os tripulantes perdidos

    //Metodo para iniciar planeta
    public void initPlaneta(int[][] planeta) {
        for (int linha = 0; linha < getColunas(); linha++) {
            for (int coluna = 0; coluna < getColunas(); coluna++) {
                planeta[linha][coluna] = -1;
            }
        }
    }
    //Fim metodo para iniciar planeta    

    //Metodo para iniciar tripulantes
    public void initTripulantes(int[][] tripulantes) {
        Random sorteio = new Random();

        //Sorteia as posices dos tripulantes
        for (int i = 0; i < getNum_tripulantes(); i++) {
            tripulantes[i][0] = sorteio.nextInt(getLinhas());
            tripulantes[i][1] = sorteio.nextInt(getColunas());

            getAcertou()[i] = 0;

            //Não deixa sortear um tripulantes na mesma posíção duas vezes
            for (int antes = 0; antes < i; antes++) {
                if ((tripulantes[i][0] == tripulantes[antes][0]) && (tripulantes[i][1] == tripulantes[antes][1])) {
                    do {
                        tripulantes[i][0] = sorteio.nextInt(getLinhas());
                        tripulantes[i][1] = sorteio.nextInt(getColunas());
                    } while ((tripulantes[i][0] == tripulantes[antes][0]) && (tripulantes[i][1] == tripulantes[antes][1]));
                }
            }
        }
    }
    //Fim Metodo para iniciar tripulantes

    //Metodo que verifica se encontrou um tripulante
    public boolean acertou(int[] sinalizador, int[][] tripulantes) {

        for (int i = 0; i < getNum_tripulantes(); i++) {
            if (sinalizador[0] == tripulantes[i][0] && sinalizador[1] == tripulantes[i][1]) {
                return true;
            }
        }
        return false;

    }
    //Fim Metodo que verifica se encontrou um tripulante

    //Metodo que verifica se ha tripulante nas adjacentes
    public boolean verificaAdjacentes(int[] sinalizador, int[][] tripulantes, int[][] planeta) {

        int linha = sinalizador[0];
        int coluna = sinalizador[1];

        for (int i = 0; i < getNum_tripulantes(); i++) {

            int user_linha = tripulantes[i][0];
            int user_coluna = tripulantes[i][1];

            if ((linha + 1 == user_linha && coluna + 1 == user_coluna) && planeta[linha + 1][coluna + 1] != 1
                    || (linha - 1 == user_linha && coluna - 1 == user_coluna) && planeta[linha - 1][coluna - 1] != 1
                    || (linha + 1 == user_linha && coluna - 1 == user_coluna) && planeta[linha + 1][coluna - 1] != 1
                    || (linha - 1 == user_linha && coluna + 1 == user_coluna) && planeta[linha - 1][coluna + 1] != 1
                    || (linha + 1 == user_linha && coluna == user_coluna) && planeta[linha + 1][coluna] != 1
                    || (linha == user_linha && coluna + 1 == user_coluna) && planeta[linha][coluna + 1] != 1
                    || (linha - 1 == user_linha && coluna == user_coluna) && planeta[linha - 1][coluna] != 1
                    || (linha == user_linha && coluna - 1 == user_coluna) && planeta[linha][coluna - 1] != 1) {
                return true;
            }
        }
        return false;
    }
     //Fim Metodo que verifica se ha tripulante nas adjacentes

    //Metodo que verifica se ha tripulante nas adjacentes easy
    public boolean verificaAdjacentesEasy(int[] sinalizador, int[][] tripulantes, int[][] planeta) {

        int linha = sinalizador[0];
        int coluna = sinalizador[1];

        for (int i = 0; i < getNum_tripulantes(); i++) {

            int user_linha = tripulantes[i][0];
            int user_coluna = tripulantes[i][1];

            if ((linha + 1 == user_linha && coluna + 1 == user_coluna) && planeta[linha + 1][coluna + 1] != 1) {
                adjacente[0] = user_linha;
                adjacente[1] = user_coluna;
            }
            if ((linha - 1 == user_linha && coluna - 1 == user_coluna) && planeta[linha - 1][coluna - 1] != 1) {
                adjacente[0] = user_linha;
                adjacente[1] = user_coluna;
            }
            if ((linha + 1 == user_linha && coluna - 1 == user_coluna) && planeta[linha + 1][coluna - 1] != 1) {
                adjacente[0] = user_linha;
                adjacente[1] = user_coluna;
            }
            if ((linha - 1 == user_linha && coluna + 1 == user_coluna) && planeta[linha - 1][coluna + 1] != 1) {
                adjacente[0] = user_linha;
                adjacente[1] = user_coluna;
            }
            if ((linha + 1 == user_linha && coluna == user_coluna) && planeta[linha + 1][coluna] != 1) {
                adjacente[0] = user_linha;
                adjacente[1] = user_coluna;
            }
            if ((linha == user_linha && coluna + 1 == user_coluna) && planeta[linha][coluna + 1] != 1) {
                adjacente[0] = user_linha;
                adjacente[1] = user_coluna;
            }
            if ((linha - 1 == user_linha && coluna == user_coluna) && planeta[linha - 1][coluna] != 1) {
                adjacente[0] = user_linha;
                adjacente[1] = user_coluna;
            }
            if ((linha == user_linha && coluna - 1 == user_coluna) && planeta[linha][coluna - 1] != 1) {
                adjacente[0] = user_linha;
                adjacente[1] = user_coluna;
            }

            if ((linha + 1 == user_linha && coluna + 1 == user_coluna) && planeta[linha + 1][coluna + 1] != 1
                    || (linha - 1 == user_linha && coluna - 1 == user_coluna) && planeta[linha - 1][coluna - 1] != 1
                    || (linha + 1 == user_linha && coluna - 1 == user_coluna) && planeta[linha + 1][coluna - 1] != 1
                    || (linha - 1 == user_linha && coluna + 1 == user_coluna) && planeta[linha - 1][coluna + 1] != 1
                    || (linha + 1 == user_linha && coluna == user_coluna) && planeta[linha + 1][coluna] != 1
                    || (linha == user_linha && coluna + 1 == user_coluna) && planeta[linha][coluna + 1] != 1
                    || (linha - 1 == user_linha && coluna == user_coluna) && planeta[linha - 1][coluna] != 1
                    || (linha == user_linha && coluna - 1 == user_coluna) && planeta[linha][coluna - 1] != 1) {
                return true;
            }

        }
        return false;
    }
     //Fim Metodo que verifica se ha tripulante nas adjacentes easy

    //Metodo que seta as adjacentes easy
    public void setAdjascentesEasy(int[] sinalizador, int[][] planeta, int type) {
        int linha = sinalizador[0];
        int coluna = sinalizador[1];
        int linhaAdjacente = adjacente[0];
        int colunaAdjacente = adjacente[1];

        //1
        if (linha > 0 && linha < linhas - 1
                && coluna > 0 && coluna < colunas - 1) {

            if (planeta[linha + 1][coluna + 1] != 1) {
                planeta[linha + 1][coluna + 1] = type;
            }
            if (planeta[linha - 1][coluna - 1] != 1) {
                planeta[linha - 1][coluna - 1] = type;
            }
            if (planeta[linha + 1][coluna] != 1) {
                planeta[linha + 1][coluna] = type;
            }
            if (planeta[linha][coluna + 1] != 1) {
                planeta[linha][coluna + 1] = type;
            }
            if (planeta[linha - 1][coluna] != 1) {
                planeta[linha - 1][coluna] = type;
            }
            if (planeta[linha][coluna - 1] != 1) {
                planeta[linha][coluna - 1] = type;
            }
            if (planeta[linha + 1][coluna - 1] != 1) {
                planeta[linha + 1][coluna - 1] = type;
            }
            if (planeta[linha - 1][coluna + 1] != 1) {
                planeta[linha - 1][coluna + 1] = type;
            }
        }

        //2
        if (linha == 0
                && coluna > 0 && coluna < colunas - 1) {

            if (planeta[linha + 1][coluna] != 1) {
                planeta[linha + 1][coluna] = type;
            }
            if (planeta[linha + 1][coluna + 1] != 1) {
                planeta[linha + 1][coluna + 1] = type;
            }
            if (planeta[linha][coluna + 1] != 1) {
                planeta[linha][coluna + 1] = type;
            }
            if (planeta[linha + 1][coluna - 1] != 1) {
                planeta[linha + 1][coluna - 1] = type;
            }
            if (planeta[linha][coluna - 1] != 1) {
                planeta[linha][coluna - 1] = type;
            }

        }

        //3
        if (coluna == 0
                && linha > 0 && linha < linhas - 1) {

            if (planeta[linha - 1][coluna] != 1) {
                planeta[linha - 1][coluna] = type;
            }
            if (planeta[linha - 1][coluna + 1] != 1) {
                planeta[linha - 1][coluna + 1] = type;
            }
            if (planeta[linha][coluna + 1] != 1) {
                planeta[linha][coluna + 1] = type;
            }
            if (planeta[linha + 1][coluna + 1] != 1) {
                planeta[linha + 1][coluna + 1] = type;
            }
            if (planeta[linha + 1][coluna] != 1) {
                planeta[linha + 1][coluna] = type;
            }

        }

        //4
        if (linha == linhas - 1
                && coluna > 0 && coluna < colunas - 1) {

            if (planeta[linha][coluna - 1] != 1) {
                planeta[linha][coluna - 1] = type;
            }
            if (planeta[linha - 1][coluna - 1] != 1) {
                planeta[linha - 1][coluna - 1] = type;
            }
            if (planeta[linha - 1][coluna] != 1) {
                planeta[linha - 1][coluna] = type;
            }
            if (planeta[linha - 1][coluna + 1] != 1) {
                planeta[linha - 1][coluna + 1] = type;
            }
            if (planeta[linha][coluna + 1] != 1) {
                planeta[linha][coluna + 1] = type;
            }

        }

        //5
        if (coluna == colunas - 1
                && linha > 0 && linha < linhas - 1) {

            if (planeta[linha - 1][coluna] != 1) {
                planeta[linha - 1][coluna] = type;
            }
            if (planeta[linha - 1][coluna - 1] != 1) {
                planeta[linha - 1][coluna - 1] = type;
            }
            if (planeta[linha][coluna - 1] != 1) {
                planeta[linha][coluna - 1] = type;
            }
            if (planeta[linha + 1][coluna - 1] != 1) {
                planeta[linha + 1][coluna - 1] = type;
            }
            if (planeta[linha + 1][coluna] != 1) {
                planeta[linha + 1][coluna] = type;
            }

        }

        //6
        if (linha == 0 && coluna == 0) {

            if (planeta[linha][coluna + 1] != 1) {
                planeta[linha][coluna + 1] = type;
            }
            if (planeta[linha + 1][coluna + 1] != 1) {
                planeta[linha + 1][coluna + 1] = type;
            }
            if (planeta[linha + 1][coluna] != 1) {
                planeta[linha + 1][coluna] = type;
            }
        }
        //7
        if (linha == linhas - 1 && coluna == 0) {

            if (planeta[linha - 1][coluna] != 1) {
                planeta[linha - 1][coluna] = type;
            }
            if (planeta[linha - 1][coluna + 1] != 1) {
                planeta[linha - 1][coluna + 1] = type;
            }
            if (planeta[linha][coluna + 1] != 1) {
                planeta[linha][coluna + 1] = type;
            }

        }

        //8
        if (linha == linhas - 1 && coluna == colunas - 1) {

            if (planeta[linha - 1][coluna] != 1) {
                planeta[linha - 1][coluna] = type;
            }
            if (planeta[linha - 1][coluna - 1] != 1) {
                planeta[linha - 1][coluna - 1] = type;
            }
            if (planeta[linha][coluna - 1] != 1) {
                planeta[linha][coluna - 1] = type;
            }
        }

        //9
        if (linha == 0 && coluna == colunas - 1) {

            if (planeta[linha][coluna - 1] != 1) {
                planeta[linha][coluna - 1] = type;
            }
            if (planeta[linha + 1][coluna - 1] != 1) {
                planeta[linha + 1][coluna - 1] = type;
            }
            if (planeta[linha + 1][coluna] != 1) {
                planeta[linha + 1][coluna] = type;
            }
        }
        //1
        ///////////////////////////////////////////////
        if (linhaAdjacente > 0 && linhaAdjacente < linhas - 1
                && colunaAdjacente > 0 && colunaAdjacente < colunas - 1) {

            if (planeta[linha + 1][coluna + 1] == type) {
                planeta[linha + 1][coluna + 1] = 3;
            }
            if (planeta[linha - 1][coluna - 1] == type) {
                planeta[linha - 1][coluna - 1] = 3;
            }
            if (planeta[linha + 1][coluna] == type) {
                planeta[linha + 1][coluna] = 3;
            }
            if (planeta[linha][coluna + 1] == type) {
                planeta[linha][coluna + 1] = 3;
            }
            if (planeta[linha - 1][coluna] == type) {
                planeta[linha - 1][coluna] = 3;
            }
            if (planeta[linha][coluna - 1] == type) {
                planeta[linha][coluna - 1] = 3;
            }
            if (planeta[linha + 1][coluna - 1] == type) {
                planeta[linha + 1][coluna - 1] = 3;
            }
            if (planeta[linha - 1][coluna + 1] == type) {
                planeta[linha - 1][coluna + 1] = 3;
            }
            //*
            if (planeta[linhaAdjacente + 1][colunaAdjacente + 1] == 3) {
                planeta[linhaAdjacente + 1][colunaAdjacente + 1] = type;
            }
            if (planeta[linhaAdjacente - 1][colunaAdjacente - 1] == 3) {
                planeta[linhaAdjacente - 1][colunaAdjacente - 1] = type;
            }
            if (planeta[linhaAdjacente + 1][colunaAdjacente] == 3) {
                planeta[linhaAdjacente + 1][colunaAdjacente] = type;
            }
            if (planeta[linhaAdjacente][colunaAdjacente + 1] == 3) {
                planeta[linhaAdjacente][colunaAdjacente + 1] = type;
            }
            if (planeta[linhaAdjacente - 1][colunaAdjacente] == 3) {
                planeta[linhaAdjacente - 1][colunaAdjacente] = type;
            }
            if (planeta[linhaAdjacente][colunaAdjacente - 1] == 3) {
                planeta[linhaAdjacente][colunaAdjacente - 1] = type;
            }
            if (planeta[linhaAdjacente + 1][colunaAdjacente - 1] == 3) {
                planeta[linhaAdjacente + 1][colunaAdjacente - 1] = type;
            }
            if (planeta[linhaAdjacente - 1][colunaAdjacente + 1] == 3) {
                planeta[linhaAdjacente - 1][colunaAdjacente + 1] = type;
            }
            planeta[linhaAdjacente][colunaAdjacente] = type;
        }
        ///////////////////////////////////////////////
        //2
        ///////////////////////////////////////////////
        if (linhaAdjacente == 0
                && colunaAdjacente > 0 && colunaAdjacente < colunas - 1) {

            if (planeta[linha + 1][coluna] == type) {
                planeta[linha + 1][coluna] = 3;
            }
            if (coluna != colunas - 1) {
                if (planeta[linha + 1][coluna + 1] == type) {
                    planeta[linha + 1][coluna + 1] = 3;
                }
                if (planeta[linha][coluna + 1] == type) {
                    planeta[linha][coluna + 1] = 3;
                }
            }
            if (!(linha == 0 || coluna == 0) || coluna != 0) {
                if (planeta[linha + 1][coluna - 1] == type) {
                    planeta[linha + 1][coluna - 1] = 3;
                }
                if (planeta[linha][coluna - 1] == type) {
                    planeta[linha][coluna - 1] = 3;
                }
            }
            if (linha != 0 && (coluna > 0 && coluna < colunas - 1)) {
                if (planeta[linha - 1][coluna + 1] == type) {
                    planeta[linha - 1][coluna + 1] = 3;
                }
                if (planeta[linha - 1][coluna - 1] == type) {
                    planeta[linha - 1][coluna - 1] = 3;
                }
            }
            //*
            if (planeta[linhaAdjacente + 1][colunaAdjacente] == 3) {
                planeta[linhaAdjacente + 1][colunaAdjacente] = type;
            }
            if (planeta[linhaAdjacente + 1][colunaAdjacente + 1] == 3) {
                planeta[linhaAdjacente + 1][colunaAdjacente + 1] = type;
            }
            if (planeta[linhaAdjacente][colunaAdjacente + 1] == 3) {
                planeta[linhaAdjacente][colunaAdjacente + 1] = type;
            }
            if (planeta[linhaAdjacente + 1][colunaAdjacente - 1] == 3) {
                planeta[linhaAdjacente + 1][colunaAdjacente - 1] = type;
            }
            if (planeta[linhaAdjacente][colunaAdjacente - 1] == 3) {
                planeta[linhaAdjacente][colunaAdjacente - 1] = type;
            }
            planeta[linhaAdjacente][colunaAdjacente] = type;

        }
        //////////////////////////////////////////////
        //3
        ////////////////////////////////////////////
        if (colunaAdjacente == 0
                && linhaAdjacente > 0 && linhaAdjacente < linhas - 1) {

            if (linha != 0) {
                if (planeta[linha - 1][coluna] == type) {
                    planeta[linha - 1][coluna] = 3;
                }
                if (planeta[linha - 1][coluna + 1] == type) {
                    planeta[linha - 1][coluna + 1] = 3;
                }

            }
            if (coluna != 0) {
                if (planeta[linha + 1][coluna - 1] == type) {
                    planeta[linha + 1][coluna - 1] = 3;
                }
            }
            if (planeta[linha][coluna + 1] == type) {
                planeta[linha][coluna + 1] = 3;
            }
            if (planeta[linha + 1][coluna + 1] == type) {
                planeta[linha + 1][coluna + 1] = 3;
            }
            if (planeta[linha + 1][coluna] == type) {
                planeta[linha + 1][coluna] = 3;
            }
            if (linha != 0 && coluna != 0) {
                if (planeta[linha - 1][coluna - 1] == type) {
                    planeta[linha - 1][coluna - 1] = 3;
                }
            }
            //*
            if (planeta[linhaAdjacente - 1][colunaAdjacente] == 3) {
                planeta[linhaAdjacente - 1][colunaAdjacente] = type;
            }
            if (planeta[linhaAdjacente - 1][colunaAdjacente + 1] == 3) {
                planeta[linhaAdjacente - 1][colunaAdjacente + 1] = type;
            }
            if (planeta[linhaAdjacente][colunaAdjacente + 1] == 3) {
                planeta[linhaAdjacente][colunaAdjacente + 1] = type;
            }
            if (planeta[linhaAdjacente + 1][colunaAdjacente + 1] == 3) {
                planeta[linhaAdjacente + 1][colunaAdjacente + 1] = type;
            }
            if (planeta[linhaAdjacente + 1][colunaAdjacente] == 3) {
                planeta[linhaAdjacente + 1][colunaAdjacente] = type;
            }
            planeta[linhaAdjacente][colunaAdjacente] = type;

        }
        ////////////////////////////////////////////
        //4
        ///////////////////////////////////////////
        if (linhaAdjacente == linhas - 1
                && colunaAdjacente > 0 && colunaAdjacente < colunas - 1) {

            if (coluna != 0) {
                if (planeta[linha][coluna - 1] == type) {
                    planeta[linha][coluna - 1] = 3;
                }
                if (planeta[linha - 1][coluna - 1] == type) {
                    planeta[linha - 1][coluna - 1] = 3;
                }
            }
            if (planeta[linha - 1][coluna] == type) {
                planeta[linha - 1][coluna] = 3;
            }
            if (linha != linhas - 1 || (coluna > 0 && coluna < colunas - 1)) {

                if (planeta[linha - 1][coluna + 1] == type) {
                    planeta[linha - 1][coluna + 1] = 3;
                }
                if (planeta[linha][coluna + 1] == type) {
                    planeta[linha][coluna + 1] = 3;
                }
            }
            if (linha != linhas - 1 && coluna != 0) {
                if (planeta[linha + 1][coluna + 1] == type) {
                    planeta[linha + 1][coluna + 1] = 3;
                }
                if (planeta[linha + 1][coluna - 1] == type) {
                    planeta[linha + 1][coluna - 1] = 3;
                }
            }

            //*
            if (planeta[linhaAdjacente][colunaAdjacente - 1] == 3) {
                planeta[linhaAdjacente][colunaAdjacente - 1] = type;
            }
            if (planeta[linhaAdjacente - 1][colunaAdjacente - 1] == 3) {
                planeta[linhaAdjacente - 1][colunaAdjacente - 1] = type;
            }
            if (planeta[linhaAdjacente - 1][colunaAdjacente] == 3) {
                planeta[linhaAdjacente - 1][colunaAdjacente] = type;
            }
            if (planeta[linhaAdjacente - 1][colunaAdjacente + 1] == 3) {
                planeta[linhaAdjacente - 1][colunaAdjacente + 1] = type;
            }
            if (planeta[linhaAdjacente][colunaAdjacente + 1] == 3) {
                planeta[linhaAdjacente][colunaAdjacente + 1] = type;
            }
            planeta[linhaAdjacente][colunaAdjacente] = type;

        }
        //////////////////////////////////////////
        //5
        //////////////////////////////////////////
        if (colunaAdjacente == colunas - 1
                && linhaAdjacente > 0 && linhaAdjacente < linhas - 1) {

            if (planeta[linha - 1][coluna] == type) {
                planeta[linha - 1][coluna] = 3;
            }
            if (planeta[linha - 1][coluna - 1] == type) {
                planeta[linha - 1][coluna - 1] = 3;
            }
            if (planeta[linha][coluna - 1] == type) {
                planeta[linha][coluna - 1] = 3;
            }
            if (linha != linhas - 1 && coluna == colunas - 1) {
                if (planeta[linha + 1][coluna - 1] == type) {
                    planeta[linha + 1][coluna - 1] = 3;
                }
                if (planeta[linha + 1][coluna] == type) {
                    planeta[linha + 1][coluna] = 3;
                }
            }
            if (linha != linhas - 1 && coluna != colunas - 1) {
                if (planeta[linha + 1][coluna - 1] == type) {
                    planeta[linha + 1][coluna - 1] = 3;
                }
                if (planeta[linha + 1][coluna] == type) {
                    planeta[linha + 1][coluna] = 3;
                }
                if (planeta[linha - 1][coluna + 1] == type) {
                    planeta[linha - 1][coluna + 1] = 3;
                }
                if (planeta[linha + 1][coluna + 1] == type) {
                    planeta[linha + 1][coluna + 1] = 3;
                }
            }
            //*
            if (planeta[linhaAdjacente - 1][colunaAdjacente] == 3) {
                planeta[linhaAdjacente - 1][colunaAdjacente] = type;
            }
            if (planeta[linhaAdjacente - 1][colunaAdjacente - 1] == 3) {
                planeta[linhaAdjacente - 1][colunaAdjacente - 1] = type;
            }
            if (planeta[linhaAdjacente][colunaAdjacente - 1] == 3) {
                planeta[linhaAdjacente][colunaAdjacente - 1] = type;
            }
            if (planeta[linhaAdjacente + 1][colunaAdjacente - 1] == 3) {
                planeta[linhaAdjacente + 1][colunaAdjacente - 1] = type;
            }
            if (planeta[linhaAdjacente + 1][colunaAdjacente] == 3) {
                planeta[linhaAdjacente + 1][colunaAdjacente] = type;
            }
            planeta[linhaAdjacente][colunaAdjacente] = type;

        }
        /////////////////////////////////////////
        //6
        ////////////////////////////////////////
        if (linhaAdjacente == 0 && colunaAdjacente == 0) {

            if (planeta[linha][coluna + 1] == type) {
                planeta[linha][coluna + 1] = 3;
            }
            if (planeta[linha + 1][coluna + 1] == type) {
                planeta[linha + 1][coluna + 1] = 3;
            }
            if (planeta[linha + 1][coluna] == type) {
                planeta[linha + 1][coluna] = 3;
            }
            if (!(linha == 0 || coluna == 0)) {
                if (planeta[linha + 1][coluna - 1] == type) {
                    planeta[linha + 1][coluna - 1] = 3;
                }
                if (planeta[linha - 1][coluna + 1] == type) {
                    planeta[linha - 1][coluna + 1] = 3;
                }
            }
            //*
            if (planeta[linhaAdjacente][colunaAdjacente + 1] == 3) {
                planeta[linhaAdjacente][colunaAdjacente + 1] = type;
            }
            if (planeta[linhaAdjacente + 1][colunaAdjacente + 1] == 3) {
                planeta[linhaAdjacente + 1][colunaAdjacente + 1] = type;
            }
            if (planeta[linhaAdjacente + 1][colunaAdjacente] == 3) {
                planeta[linhaAdjacente + 1][colunaAdjacente] = type;
            }
            planeta[linhaAdjacente][colunaAdjacente] = type;

        }
        /////////////////////////////////////////
        //7
        /////////////////////////////////////////
        if (linhaAdjacente == linhas - 1 && colunaAdjacente == 0) {

            if (planeta[linha - 1][coluna] == type) {
                planeta[linha - 1][coluna] = 3;
            }
            if (planeta[linha - 1][coluna + 1] == type) {
                planeta[linha - 1][coluna + 1] = 3;
            }
            if (planeta[linha][coluna + 1] == type) {
                planeta[linha][coluna + 1] = 3;
            }
            if (!(linha == linhas - 1 || coluna == 0)) {
                if (planeta[linha - 1][coluna - 1] == type) {
                    planeta[linha - 1][coluna - 1] = 3;
                }
                if (planeta[linha + 1][coluna + 1] == type) {
                    planeta[linha + 1][coluna + 1] = 3;
                }
            }
            //*
            if (planeta[linhaAdjacente - 1][colunaAdjacente] == 3) {
                planeta[linhaAdjacente - 1][colunaAdjacente] = type;
            }
            if (planeta[linhaAdjacente - 1][colunaAdjacente + 1] == 3) {
                planeta[linhaAdjacente - 1][colunaAdjacente + 1] = type;
            }
            if (planeta[linhaAdjacente][colunaAdjacente + 1] == 3) {
                planeta[linhaAdjacente][colunaAdjacente + 1] = type;
            }
            planeta[linhaAdjacente][colunaAdjacente] = type;

        }
        /////////////////////////////////////////
        //8
        /////////////////////////////////////////
        if (linhaAdjacente == linhas - 1 && colunaAdjacente == colunas - 1) {

            if (planeta[linha - 1][coluna] == type) {
                planeta[linha - 1][coluna] = 3;
            }
            if (planeta[linha - 1][coluna - 1] == type) {
                planeta[linha - 1][coluna - 1] = 3;
            }
            if (planeta[linha][coluna - 1] == type) {
                planeta[linha][coluna - 1] = 3;
            }
            if (!(linha == linhas - 1 || coluna == colunas - 1)) {
                if (planeta[linha - 1][coluna + 1] == type) {
                    planeta[linha - 1][coluna + 1] = 3;
                }
                if (planeta[linha + 1][coluna - 1] == type) {
                    planeta[linha + 1][coluna - 1] = 3;
                }
            }
            //*
            if (planeta[linhaAdjacente - 1][colunaAdjacente] == 3) {
                planeta[linhaAdjacente - 1][colunaAdjacente] = type;
            }
            if (planeta[linhaAdjacente - 1][colunaAdjacente - 1] == 3) {
                planeta[linhaAdjacente - 1][colunaAdjacente - 1] = type;
            }
            if (planeta[linhaAdjacente][colunaAdjacente - 1] == 3) {
                planeta[linhaAdjacente][colunaAdjacente - 1] = type;
            }
            planeta[linhaAdjacente][colunaAdjacente] = type;
        }
        ////////////////////////////////////////
        //9
        /////////////////////////////////////////
        if (linhaAdjacente == 0 && colunaAdjacente == colunas - 1) {

            if (planeta[linha][coluna - 1] == type) {
                planeta[linha][coluna - 1] = 3;
            }
            if (planeta[linha + 1][coluna - 1] == type) {
                planeta[linha + 1][coluna - 1] = 3;
            }
            if (planeta[linha + 1][coluna] == type) {
                planeta[linha + 1][coluna] = 3;
            }
            if (!(linha == 0 || coluna == colunas - 1)) {
                if (planeta[linha - 1][coluna - 1] == type) {
                    planeta[linha - 1][coluna - 1] = 3;
                }
                if (planeta[linha + 1][coluna + 1] == type) {
                    planeta[linha + 1][coluna + 1] = 3;
                }
            }
            //*
            if (planeta[linhaAdjacente][colunaAdjacente - 1] == 3) {
                planeta[linhaAdjacente][colunaAdjacente - 1] = type;
            }
            if (planeta[linhaAdjacente + 1][colunaAdjacente - 1] == 3) {
                planeta[linhaAdjacente + 1][colunaAdjacente - 1] = type;
            }
            if (planeta[linhaAdjacente + 1][colunaAdjacente] == 3) {
                planeta[linhaAdjacente + 1][colunaAdjacente] = type;
            }
            planeta[linhaAdjacente][colunaAdjacente] = type;

        }
        //////////////////////////////////////////

    }
    //Fim Metodo que seta as adjacentes easy

    //Metodo que seta as adjacentes
    public void setAdjascentes(int[] sinalizador, int[][] planeta, int type) {

        int linha = sinalizador[0];
        int coluna = sinalizador[1];

        //1
        if (linha > 0 && linha < linhas - 1
                && coluna > 0 && coluna < colunas - 1) {

            if (planeta[linha + 1][coluna + 1] != 1) {
                planeta[linha + 1][coluna + 1] = type;
            }
            if (planeta[linha - 1][coluna - 1] != 1) {
                planeta[linha - 1][coluna - 1] = type;
            }
            if (planeta[linha + 1][coluna] != 1) {
                planeta[linha + 1][coluna] = type;
            }
            if (planeta[linha][coluna + 1] != 1) {
                planeta[linha][coluna + 1] = type;
            }
            if (planeta[linha - 1][coluna] != 1) {
                planeta[linha - 1][coluna] = type;
            }
            if (planeta[linha][coluna - 1] != 1) {
                planeta[linha][coluna - 1] = type;
            }
            if (planeta[linha + 1][coluna - 1] != 1) {
                planeta[linha + 1][coluna - 1] = type;
            }
            if (planeta[linha - 1][coluna + 1] != 1) {
                planeta[linha - 1][coluna + 1] = type;
            }

            //2
        } else if (linha == 0
                && coluna > 0 && coluna < colunas - 1) {

            if (planeta[linha + 1][coluna] != 1) {
                planeta[linha + 1][coluna] = type;
            }
            if (planeta[linha + 1][coluna + 1] != 1) {
                planeta[linha + 1][coluna + 1] = type;
            }
            if (planeta[linha][coluna + 1] != 1) {
                planeta[linha][coluna + 1] = type;
            }
            if (planeta[linha + 1][coluna - 1] != 1) {
                planeta[linha + 1][coluna - 1] = type;
            }
            if (planeta[linha][coluna - 1] != 1) {
                planeta[linha][coluna - 1] = type;
            }

            //3
        } else if (coluna == 0
                && linha > 0 && linha < linhas - 1) {

            if (planeta[linha - 1][coluna] != 1) {
                planeta[linha - 1][coluna] = type;
            }
            if (planeta[linha - 1][coluna + 1] != 1) {
                planeta[linha - 1][coluna + 1] = type;
            }
            if (planeta[linha][coluna + 1] != 1) {
                planeta[linha][coluna + 1] = type;
            }
            if (planeta[linha + 1][coluna + 1] != 1) {
                planeta[linha + 1][coluna + 1] = type;
            }
            if (planeta[linha + 1][coluna] != 1) {
                planeta[linha + 1][coluna] = type;
            }

            //4
        } else if (linha == linhas - 1
                && coluna > 0 && coluna < colunas - 1) {

            if (planeta[linha][coluna - 1] != 1) {
                planeta[linha][coluna - 1] = type;
            }
            if (planeta[linha - 1][coluna - 1] != 1) {
                planeta[linha - 1][coluna - 1] = type;
            }
            if (planeta[linha - 1][coluna] != 1) {
                planeta[linha - 1][coluna] = type;
            }
            if (planeta[linha - 1][coluna + 1] != 1) {
                planeta[linha - 1][coluna + 1] = type;
            }
            if (planeta[linha][coluna + 1] != 1) {
                planeta[linha][coluna + 1] = type;
            }

            //5
        } else if (coluna == colunas - 1
                && linha > 0 && linha < linhas - 1) {

            if (planeta[linha - 1][coluna] != 1) {
                planeta[linha - 1][coluna] = type;
            }
            if (planeta[linha - 1][coluna - 1] != 1) {
                planeta[linha - 1][coluna - 1] = type;
            }
            if (planeta[linha][coluna - 1] != 1) {
                planeta[linha][coluna - 1] = type;
            }
            if (planeta[linha + 1][coluna - 1] != 1) {
                planeta[linha + 1][coluna - 1] = type;
            }
            if (planeta[linha + 1][coluna] != 1) {
                planeta[linha + 1][coluna] = type;
            }

            //6
        } else if (linha == 0 && coluna == 0) {

            if (planeta[linha][coluna + 1] != 1) {
                planeta[linha][coluna + 1] = type;
            }
            if (planeta[linha + 1][coluna + 1] != 1) {
                planeta[linha + 1][coluna + 1] = type;
            }
            if (planeta[linha + 1][coluna] != 1) {
                planeta[linha + 1][coluna] = type;
            }

            //7
        } else if (linha == linhas - 1 && coluna == 0) {

            if (planeta[linha - 1][coluna] != 1) {
                planeta[linha - 1][coluna] = type;
            }
            if (planeta[linha - 1][coluna + 1] != 1) {
                planeta[linha - 1][coluna + 1] = type;
            }
            if (planeta[linha][coluna + 1] != 1) {
                planeta[linha][coluna + 1] = type;
            }

            //8
        } else if (linha == linhas - 1 && coluna == colunas - 1) {

            if (planeta[linha - 1][coluna] != 1) {
                planeta[linha - 1][coluna] = type;
            }
            if (planeta[linha - 1][coluna - 1] != 1) {
                planeta[linha - 1][coluna - 1] = type;
            }
            if (planeta[linha][coluna - 1] != 1) {
                planeta[linha][coluna - 1] = type;
            }

            //9
        } else if (linha == 0 && coluna == colunas - 1) {

            if (planeta[linha][coluna - 1] != 1) {
                planeta[linha][coluna - 1] = type;
            }
            if (planeta[linha + 1][coluna - 1] != 1) {
                planeta[linha + 1][coluna - 1] = type;
            }
            if (planeta[linha + 1][coluna] != 1) {
                planeta[linha + 1][coluna] = type;
            }

        }
    }
    //Fim Metodo que seta as adjacentes

    //Getters e Seters    
    /**
     * @return the linhas
     */
    public int getLinhas() {
        return linhas;
    }

    /**
     * @param linhas the linhas to set
     */
    public void setLinhas(int linhas) {
        this.linhas = linhas;
    }

    /**
     * @return the colunas
     */
    public int getColunas() {
        return colunas;
    }

    /**
     * @param colunas the colunas to set
     */
    public void setColunas(int colunas) {
        this.colunas = colunas;
    }

    /**
     * @return the num_tripulantes
     */
    public Integer getNum_tripulantes() {
        return num_tripulantes;
    }

    /**
     * @param num_tripulantes the num_tripulantes to set
     */
    public void setNum_tripulantes(Integer num_tripulantes) {
        this.num_tripulantes = num_tripulantes;
    }

    /**
     * @return the acertou
     */
    public int[] getAcertou() {
        return acertou;
    }

    /**
     * @param acertou the acertou to set
     */
    public void setAcertou(int[] acertou) {
        this.acertou = acertou;
    }

    /**
     * @return the adjacente
     */
    public int[] getAdjacente() {
        return adjacente;
    }

    /**
     * @param adjacente the adjacente to set
     */
    public void setAdjacente(int[] adjacente) {
        this.adjacente = adjacente;
    }

    /**
     * @return the planeta
     */
    public int[][] getPlaneta() {
        return planeta;
    }

    /**
     * @param planeta the planeta to set
     */
    public void setPlaneta(int[][] planeta) {
        this.planeta = planeta;
    }

    /**
     * @return the tripulantes
     */
    public int[][] getTripulantes() {
        return tripulantes;
    }

    /**
     * @param tripulantes the tripulantes to set
     */
    public void setTripulantes(int[][] tripulantes) {
        this.tripulantes = tripulantes;
    }

    /**
     * @return the sinalizador
     */
    public int[] getSinalizador() {
        return sinalizador;
    }

    /**
     * @param sinalizador the sinalizador to set
     */
    public void setSinalizador(int[] sinalizador) {
        this.sinalizador = sinalizador;
    }

    /**
     * @return the tentativas
     */
    public int getTentativas() {
        return tentativas;
    }

    /**
     * @param tentativas the tentativas to set
     */
    public void setTentativas(int tentativas) {
        this.tentativas = tentativas;
    }

    /**
     * @return the acertos
     */
    public int getAcertos() {
        return acertos;
    }

    /**
     * @param acertos the acertos to set
     */
    public void setAcertos(int acertos) {
        this.acertos = acertos;
    }

    /**
     * @return the dificuldadeSelected
     */
    public int getDificuldadeSelected() {
        return dificuldadeSelected;
    }

    /**
     * @param dificuldadeSelected the dificuldadeSelected to set
     */
    public void setDificuldadeSelected(int dificuldadeSelected) {
        this.dificuldadeSelected = dificuldadeSelected;
    }

}
