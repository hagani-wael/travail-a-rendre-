import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Arbitre {
    public static Data d;
    public static double[][] taux =  {
        {1.0, 0.741, 0.657, 1.061, 1.005},
        {1.349, 1.0, 0.888, 1.433, 1.366},
        {1.521, 1.126, 1.0, 1.614, 1.538},
        {0.942, 0.698, 0.619, 1.0, 0.953},
        {0.995, 0.732, 0.65, 1.049, 1.0}
    };
    public static String[] devise = {"USD", "EUR", "GBP", "CHF", "CAD"};
    public static void main(String[] args) throws IOException{

        ProcessBuilder pb = new ProcessBuilder("java", "-Xmx2048m", Arbitre.class.getName());
        pb.start();

        
        
        if(trouverArbitrage())
            System.out.println("Il y a une opportunité d'arbitrage !");
        else
            System.out.println("Il n'y a pas d'opportunité d'arbitrage.");
    }

    static boolean trouverArbitrage() {
        //ici je veux faire une conversion des taux de change en logarithmes négatifs en utilisant la fonction math 
        double[][] nLogtaux = new double[5][5];

        for(int i = 0 ; i < 5 ; i ++)
            for(int j = 0 ; j < 5 ; j++)
                nLogtaux[i][j] = -Math.log(taux[i][j]);
    
        // ici je vais initialiser les chemins d'arbitrage dans une ArrayList et utiliser add() pour la remplire 
        ArrayList<ArrayList<Integer>> paths = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < nLogtaux.length; i++) {
            paths.add(new ArrayList<Integer>());
        }
    
        // ici c'est l'exécution de l'algorithme de Bellman-Ford pour chaque sommet
        for (int k = 0; k < nLogtaux.length; k++) {
            double[] dist = new double[nLogtaux.length];
            int[] pred = new int[nLogtaux.length];
    
            // ici je vais initialiser encore une fois les distances et les prédécesseurs 
            // qui vont m'aider laur de l'execution de l'algorithme 
            for (int i = 0; i < nLogtaux.length; i++) {
                dist[i] = Double.MAX_VALUE;
                pred[i] = -1;
            }
            dist[k] = 0;
    
            // relaxation des arêtes N-1 fois
            for (int i = 1; i < nLogtaux.length; i++) {
                for (int u = 0; u < nLogtaux.length; u++) {
                    for (int v = 0; v < nLogtaux[u].length; v++) {
                        if (dist[u] != Double.MAX_VALUE && dist[v] > dist[u] + nLogtaux[u][v]) {
                            dist[v] = dist[u] + nLogtaux[u][v];
                            pred[v] = u;
    
                            //ici je vais faire une mise à jour du chemin d'arbitrage si la condition est vérifiée bien sur 
                            if (i == nLogtaux.length - 1 && dist[v] < 0) {
                                ArrayList<Integer> path = paths.get(u);
                                path.add(u);
                                int w = u;
                                
                                  do{
                                    w = pred[w];
                                    path.add(w);
                                }while (w != v);
                                 
    
                                // ici je vais faire un affichage du chemin d'arbitrage en utilisant la fonction get()
                                System.out.print("Arbitrage " + (paths.indexOf(path) + 1) + ": (profit " + String.format("%.2f", -dist[v]) + ""+ devise[v] +")\n");
                                for (int j = 0; j < path.size(); j++) {
                                    int uIndex = path.get(j);
                                    int vIndex = path.get((j + 1) % path.size());
                                    System.out.printf("%.4f %s = %.4f %s\n", Math.exp(-nLogtaux[uIndex][vIndex]), devise[uIndex], Math.exp(nLogtaux[uIndex][vIndex]), devise[vIndex]);
                                }
                                System.out.println("");
                            }
                        }
                    }
                }
            }
        }
    
        return false;
    }
}