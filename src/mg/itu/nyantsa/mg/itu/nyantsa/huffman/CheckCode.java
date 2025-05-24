package mg.itu.nyantsa.huffman;

import java.util.*;

public class CheckCode {
    
    /**
     * Vérifie si un langage est un code
     * @param language ensemble de mots du langage
     * @return true si c'est un code, false sinon
     */
    public static boolean isCode(Set<String> language) {
        // Cas particuliers
        if (language.isEmpty()) {
            System.out.println("Le langage vide ∅ est un code (par convention).");
            return true;
        }
        
        if (language.contains("")) {
            System.out.println("Le langage contient le mot vide ε, donc ce n'est pas un code.");
            return false;
        }
        
        // Vérification de la propriété préfixe
        if (isPrefixCode(language)) {
            System.out.println("C'est un code préfixe, donc c'est un code.");
            return true;
        }
        
        // Vérification générale : recherche de mots avec double factorisation
        return !hasMultipleFactorizations(language);
    }
    
    /**
     * Vérifie si le langage est un code préfixe
     */
    private static boolean isPrefixCode(Set<String> language) {
        List<String> words = new ArrayList<>(language);
        
        for (int i = 0; i < words.size(); i++) {
            for (int j = i + 1; j < words.size(); j++) {
                String word1 = words.get(i);
                String word2 = words.get(j);
                
                if (word1.startsWith(word2) || word2.startsWith(word1)) {
                    System.out.println("\"" + word1 + "\" et \"" + word2 + "\" - l'un est préfixe de l'autre.");
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Recherche des mots ayant plusieurs factorisations possibles
     */
    private static boolean hasMultipleFactorizations(Set<String> language) {
        // Génére tous les mots possibles jusqu'à une certaine longueur
        int maxLength = getMaxWordLength(language) * 3; // Limite raisonnable
        
        for (int len = 1; len <= maxLength; len++) {
            Set<String> possibleWords = generatePossibleWords(language, len);
            
            for (String word : possibleWords) {
                List<List<String>> factorizations = findAllFactorizations(word, language);
                
                if (factorizations.size() > 1) {
                    System.out.println("Le mot \"" + word + "\" a plusieurs factorisations :");
                    for (int i = 0; i < factorizations.size(); i++) {
                        System.out.println("  Factorisation " + (i+1) + ": " + factorizations.get(i));
                    }
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Trouve toutes les factorisations possibles d'un mot dans le langage
     */
    private static List<List<String>> findAllFactorizations(String word, Set<String> language) {
        List<List<String>> result = new ArrayList<>();
        findFactorizationsRecursive(word, language, new ArrayList<>(), result);
        return result;
    }
    
    /**
     * Méthode récursive pour trouver les factorisations
     */
    private static void findFactorizationsRecursive(String remaining, Set<String> language, 
                                                  List<String> currentFactorization, 
                                                  List<List<String>> allFactorizations) {
        if (remaining.isEmpty()) {
            allFactorizations.add(new ArrayList<>(currentFactorization));
            return;
        }
        
        for (String word : language) {
            if (remaining.startsWith(word)) {
                currentFactorization.add(word);
                findFactorizationsRecursive(remaining.substring(word.length()), 
                                           language, currentFactorization, allFactorizations);
                currentFactorization.remove(currentFactorization.size() - 1);
            }
        }
    }
    
    /**
     * Génère tous les mots possibles de longueur donnée à partir du langage
     */
    private static Set<String> generatePossibleWords(Set<String> language, int maxLength) {
        Set<String> result = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        
        queue.add("");
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            
            if (current.length() > maxLength) continue;
            
            if (!current.isEmpty()) {
                result.add(current);
            }
            
            for (String word : language) {
                String newWord = current + word;
                if (newWord.length() <= maxLength) {
                    queue.offer(newWord);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Obtient la longueur du mot le plus long dans le langage
     */
    private static int getMaxWordLength(Set<String> language) {
        return language.stream().mapToInt(String::length).max().orElse(0);
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Test de code préfixe/suffixe/etc. ===");
        System.out.println("Entrez les mots du langage séparés par des espaces (ou 'quit' pour quitter) :");
    
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
    
            if (input.equalsIgnoreCase("quit")) {
                break;
            }
    
            Set<String> customL = input.isEmpty() ? new HashSet<>() : new HashSet<>(Arrays.asList(input.split("\\s+")));
    
            System.out.print("L = { ");
            System.out.print(String.join(", ", customL));
            System.out.println(" }");
    
            boolean result = isCode(customL);
            System.out.println("Résultat : " + (result ? "Code" : "Pas un code"));
            System.out.println();
        }
    
        scanner.close();
        System.out.println("Programme terminé.");
    }
    
}
