package crypto.lab1.task3;

public class Startup {
    public static void main(String[] args) {
        final String ciphertext = "Rm nb blfmtvi zmw nliv efomvizyov bvzih nb uzgsvi tzev nv hlnv zwerxv gszg R'ev yvvm gfimrmt levi rm nb nrmw vevi hrmxv. Dsvmvevi blf uvvo orpv xirgrxrarmt zmblmv, sv glow nv, qfhg ivnvnyvi gszg zoo gsv kvlkov rm gsrh dliow szevm'g szw gsv zwezmgztvh gszg blf'ev szw.";
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
        geneticAlgorithm.run(ciphertext);
    }
}
