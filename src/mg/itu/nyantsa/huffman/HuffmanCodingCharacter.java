package huffman;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class HuffmanCodingCharacter {
    /**
     * Classe pour représenter un nœud dans l'arbre de Huffman
     */
    static class HuffmanNode implements Comparable<HuffmanNode> {
        char caractere;
        int frequence;
        HuffmanNode gauche, droite;
        
        public HuffmanNode(char caractere, int frequence) {
            this.caractere = caractere;
            this.frequence = frequence;
            this.gauche = null;
            this.droite = null;
        }
        
        public HuffmanNode(int frequence, HuffmanNode gauche, HuffmanNode droite) {
            this.caractere = '\0';
            this.frequence = frequence;
            this.gauche = gauche;
            this.droite = droite;
        }
        
        @Override
        public int compareTo(HuffmanNode autre) 
        {    return this.frequence - autre.frequence; }
        
        public boolean estFeuille() 
        {   return gauche == null && droite == null; }
    }
    
    /**
     * Normalise le texte : convertit en minuscules et ne garde que les lettres et espaces
     * 
     * @param texte Le texte à normaliser
     * @return Le texte normalisé (minuscules, lettres et espaces seulement)
     */
    private static String normaliserTexte(String texte) {
        if (texte == null) return "";
        
        StringBuilder texteNormalise = new StringBuilder();
        for (char c : texte.toCharArray()) {
            char charMinuscule = Character.toLowerCase(c);

            // on Garde seulement les lettres (a-z) et les espaces (mitovy fona maj na min a=A iray ihany)
            if ((charMinuscule >= 'a' && charMinuscule <= 'z') || charMinuscule == ' ') {
                texteNormalise.append(charMinuscule);
            }
        }
        
        return texteNormalise.toString();
    }
    
    /**
     * Construit l'arbre de Huffman à partir d'un texte.
     * 
     * @param texte Le texte à encoder (sera normalisé automatiquement)
     * @return La racine de l'arbre de Huffman
     */
    private static HuffmanNode construireArbreHuffman(String texte) {
        // Normalisation du texte 
        String texteNormalise = normaliserTexte(texte);
        
        // Calcul les fréquences des caractères
        Map<Character, Integer> frequences = new HashMap<>();
        for (char c : texteNormalise.toCharArray()) {
            frequences.put(c, frequences.getOrDefault(c, 0) + 1);
        }
        
        // Si le texte normalisé est vide, on  return null
        if (frequences.isEmpty()) {
            return null;
        }
        
        // Créer une file de priorité pour les nœuds
        PriorityQueue<HuffmanNode> fileNoeuds = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : frequences.entrySet()) {
            fileNoeuds.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }
        
        // Construction de l'arbre de Huffman
        while (fileNoeuds.size() > 1) {
            HuffmanNode gauche = fileNoeuds.poll();
            HuffmanNode droite = fileNoeuds.poll();
            
            int sommeFrequences = gauche.frequence + droite.frequence;
            fileNoeuds.add(new HuffmanNode(sommeFrequences, gauche, droite));
        }
        
        return fileNoeuds.poll();
    }
    
    /**
     * Génère la table de codage Huffman à partir de l'arbre.
     * 
     * @param racine La racine de l'arbre Huffman
     * @return Une map associant chaque caractère à son code Huffman
     */
    private static Map<Character, String> genererTableCodage(HuffmanNode racine) {
        Map<Character, String> tablesCodage = new HashMap<>();
        if (racine != null) {
            genererCodeRecursif(racine, "", tablesCodage);
        }
        return tablesCodage;
    }
    
    /**
     * Fonction auxiliaire récursive pour générer les codes Huffman.
     */
    private static void genererCodeRecursif(HuffmanNode noeud, String code, Map<Character, String> tablesCodage) {
        if (noeud == null) return;
        
        if (noeud.estFeuille()) {
            tablesCodage.put(noeud.caractere, code.isEmpty() ? "0" : code);
            return;
        }
        
        genererCodeRecursif(noeud.gauche, code + "0", tablesCodage);
        genererCodeRecursif(noeud.droite, code + "1", tablesCodage);
    }
    
    /**
     * Encode un texte en utilisant le codage de Huffman.
     * Le texte sera automatiquement normalisé (minuscules, lettres et espaces seulement).
     * 
     * @param texte Le texte à encoder
     * @return Un objet avec le texte encodé en binaire et la table de codage
     */
    public static Map.Entry<String, Map<Character, String>> encoder(String texte) {
        if (texte == null || texte.isEmpty()) {
            return new AbstractMap.SimpleEntry<>("", new HashMap<>());
        }
        
        // Normalise le texte
        String texteNormalise = normaliserTexte(texte);
        
        if (texteNormalise.isEmpty()) {
            return new AbstractMap.SimpleEntry<>("", new HashMap<>());
        }
        
        // Construction de l'arbre de Huffman
        HuffmanNode racine = construireArbreHuffman(texteNormalise);
        
        if (racine == null) {
            return new AbstractMap.SimpleEntry<>("", new HashMap<>());
        }
        
        // Génere la table de codage
        Map<Character, String> tableCodage = genererTableCodage(racine);
        
        // Encodage du texte normalisé
        StringBuilder texteCodé = new StringBuilder();
        for (char c : texteNormalise.toCharArray()) {
            texteCodé.append(tableCodage.get(c));
        }
        
        return new AbstractMap.SimpleEntry<>(texteCodé.toString(), tableCodage);
    }
    
    /**
     * Décode un texte encodé avec Huffman en utilisant la table de codage.
     * 
     * @param texteCodé Le texte binaire encodé
     * @param tableCodage La table de correspondance entre caractères et codes
     * @return Le texte décodé (normalisé : minuscules, lettres et espaces)
     */
    public static String decoder(String texteCodé, Map<Character, String> tableCodage) {
        // Inverser la table de codage
        Map<String, Character> tableDecodage = new HashMap<>();
        for (Map.Entry<Character, String> entry : tableCodage.entrySet()) {
            tableDecodage.put(entry.getValue(), entry.getKey());
        }
        
        StringBuilder texteDecodé = new StringBuilder();
        StringBuilder codeActuel = new StringBuilder();
        
        for (char bit : texteCodé.toCharArray()) {
            codeActuel.append(bit);
            if (tableDecodage.containsKey(codeActuel.toString())) {
                texteDecodé.append(tableDecodage.get(codeActuel.toString()));
                codeActuel.setLength(0);
            }
        }
        
        return texteDecodé.toString();
    }
    
    /**
     * Affiche la table de codage Huffman.
     */
    public static void afficherTableCodage(Map<Character, String> tableCodage) {
        System.out.println("Table de codage Huffman (alphabet + espace uniquement):");
        for (Map.Entry<Character, String> entry : tableCodage.entrySet()) {
            char c = entry.getKey();
            String representation = (c == ' ') ? "ESPACE" : "'" + c + "'";
            System.out.println(representation + " -> " + entry.getValue());
        }
    }
    
    /**
     * Encode un texte à partir d'une table de codage existante.
     * Le texte sera automatiquement normalisé.
     */
    public static String encoderFromTable(String texte, Map<Character, String> tableCodage) {
        String texteNormalise = normaliserTexte(texte);
        StringBuilder binaire = new StringBuilder();
        for (char c : texteNormalise.toCharArray()) {
            String code = tableCodage.get(c);
            if (code != null) {
                binaire.append(code);
            }
        }
        return binaire.toString();
    }
    
    /**
     * Méthode utilitaire pour afficher le texte normalisé
     */
    public static String obtenirTexteNormalise(String texte) {
        return normaliserTexte(texte);
    }
    
    /**
     * Calcule les fréquences relatives des caractères
     */
    public static Map<Character, Double> calculerFrequencesRelatives(String texte) {
        String texteNormalise = normaliserTexte(texte);
        Map<Character, Integer> frequencesAbsolues = new HashMap<>();
        
        // Compter les occurrences
        for (char c : texteNormalise.toCharArray()) {
            frequencesAbsolues.put(c, frequencesAbsolues.getOrDefault(c, 0) + 1);
        }
        
        // Convertir en fréquences relatives
        Map<Character, Double> frequencesRelatives = new HashMap<>();
        int total = texteNormalise.length();
        for (Map.Entry<Character, Integer> entry : frequencesAbsolues.entrySet()) {
            frequencesRelatives.put(entry.getKey(), (double) entry.getValue() / total);
        }
        
        return frequencesRelatives;
    }
    
    /**
     * Calcule l'entropie de Shannon du texte 
     * H(X) = -Σ p(x) * log2(p(x)) 
     */
    public static double calculerEntropie(String texte) {
        Map<Character, Double> frequences = calculerFrequencesRelatives(texte);
        double entropie = 0.0;
        
        for (double p : frequences.values()) {
            if (p > 0) {
                entropie -= p * (Math.log(p) / Math.log(2));
            }
        }
        
        return entropie;
    }
    
    /**
     * Calcule la longueur moyenne des codes de Huffman
     * L = Σ p(x) * longueur(code(x))
     */
    public static double calculerLongueurMoyenne(String texte, Map<Character, String> tableCodage) {
        Map<Character, Double> frequences = calculerFrequencesRelatives(texte);
        double longueurMoyenne = 0.0;
        
        for (Map.Entry<Character, Double> entry : frequences.entrySet()) {
            char caractere = entry.getKey();
            double probabilite = entry.getValue();
            
            if (tableCodage.containsKey(caractere)) {
                int longueurCode = tableCodage.get(caractere).length();
                longueurMoyenne += probabilite * longueurCode;
            }
        }
        
        return longueurMoyenne;
    }
    
    /**
     * Calcule l'efficacité du codage de Huffman
     * Efficacité = H(X) / L * 100%
     */
    public static double calculerEfficacite(String texte, Map<Character, String> tableCodage) {
        double entropie = calculerEntropie(texte);
        double longueurMoyenne = calculerLongueurMoyenne(texte, tableCodage);
        
        if (longueurMoyenne == 0) return 0;
        return (entropie / longueurMoyenne) * 100;
    }
    
    /**
     * Affiche la distribution des longueurs de codes
     */
    public static void afficherDistributionLongueurs(Map<Character, String> tableCodage) {
        Map<Integer, Integer> distributionLongueurs = new HashMap<>();
        
        // on compte les codes par longueur
        for (String code : tableCodage.values()) {
            int longueur = code.length();
            distributionLongueurs.put(longueur, distributionLongueurs.getOrDefault(longueur, 0) + 1);
        }
        
        System.out.println("\n=== DISTRIBUTION DES LONGUEURS DE CODES ===");
        for (Map.Entry<Integer, Integer> entry : distributionLongueurs.entrySet()) {
            System.out.println("Longueur " + entry.getKey() + " bits : " + entry.getValue() + " caractères");
        }
    }
    
    /**
     * Vérifie l'inégalité de Kraft pour s'assurer que le code est valide
     * Σ 2^(-longueur) ≤ 1
     */
    public static boolean verifierInegaliteKraft(Map<Character, String> tableCodage) {
        double sommeKraft = 0.0;
        
        for (String code : tableCodage.values()) {
            sommeKraft += Math.pow(2, -code.length());
        }
        
        return sommeKraft <= 1.0;
    }
    
    /**
     * Analyse complète du codage de Huffman
     */
    public static void analyserCodeHuffman(String texte, Map<Character, String> tableCodage) {
        System.out.println("\n=== ANALYSE THÉORIQUE DU CODAGE DE HUFFMAN ===");
        
        // Statistiques de base
        String texteNormalise = normaliserTexte(texte);
        int nombreCaracteres = calculerFrequencesRelatives(texte).size();
        
        System.out.println("Nombre de caractères distincts : " + nombreCaracteres);
        System.out.println("Longueur du texte normalisé : " + texteNormalise.length());
        
        // Calculs théoriques
        double entropie = calculerEntropie(texte);
        double longueurMoyenne = calculerLongueurMoyenne(texte, tableCodage);
        double efficacite = calculerEfficacite(texte, tableCodage);
        
        System.out.println(String.format("Entropie de Shannon H(X) : %.4f bits/symbole", entropie));
        System.out.println(String.format("Longueur moyenne L : %.4f bits/symbole", longueurMoyenne));
        System.out.println(String.format("Efficacité du codage : %.2f%%", efficacite));
        
        // Borne théorique
        System.out.println(String.format("Borne inférieure théorique : %.4f <= L <= %.4f", 
            entropie, entropie + 1));
        
        // Vérifications
        boolean kraftValide = verifierInegaliteKraft(tableCodage);
        System.out.println("Inégalité de Kraft respectée : " + (kraftValide ? "OUI" : "NON"));
        
        boolean huffmanOptimal = longueurMoyenne <= entropie + 1;
        System.out.println("Codage de Huffman optimal : " + (huffmanOptimal ? "OUI" : "NON"));
        
        // Distribution des longueurs
        afficherDistributionLongueurs(tableCodage);

        System.out.println("\n=== ANALYSE PAR CARACTÈRE ===");
        Map<Character, Double> frequences = calculerFrequencesRelatives(texte);
        System.out.println("Caractère | Fréquence | Code Huffman | Longueur | Contribution");
        System.out.println("----------|-----------|--------------|----------|-------------");
        
        for (Map.Entry<Character, String> entry : tableCodage.entrySet()) {
            char c = entry.getKey();
            String code = entry.getValue();
            double freq = frequences.get(c);
            double contribution = freq * code.length();
            
            String charDisplay = (c == ' ') ? "ESPACE" : "'" + c + "'";
            System.out.println(String.format("%-9s | %8.4f | %-12s | %8d | %11.4f", 
                charDisplay, freq, code, code.length(), contribution));
        }
    }

    
    public static void main(String[] args) {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        
        System.out.println("=== CODAGE DE HUFFMAN ===");
        System.out.print("Entrez le texte à encoder : ");
        String texte = scanner.nextLine();

        Map.Entry<String, Map<Character, String>> resultat = HuffmanCodingCharacter.encoder(texte);
        Map<Character, String> tableCodage = resultat.getValue();
        //analyserCodeHuffman(texte,tableCodage);
        
        afficherTableCodage(tableCodage);
        
        scanner.close();
    }
}