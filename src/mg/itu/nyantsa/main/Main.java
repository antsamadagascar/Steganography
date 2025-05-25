package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import recurrence.Recurrence;
import steganography.AudioSteganography;
import steganography.ImageSteganography;
import huffman.HuffmanCodingCharacter;

public class Main {
    private static final String RESET = "\033[0m";
    private static final String BOLD = "\033[1m";
    private static final String CYAN = "\033[36m";
    private static final String GREEN = "\033[32m";
    private static final String YELLOW = "\033[33m";
    private static final String RED = "\033[31m";
    private static final String BLUE = "\033[34m";
    private static final String PURPLE = "\033[35m";
    
    private static Scanner scanner = new Scanner(System.in);
    private static final String basePath = "C:\\Users\\Ny Antsa\\Documents\\Fianarana\\semestre6\\Mr Tsinjo\\final-exam-codage\\src\\mg\\itu\\nyantsa\\data";

    public static void main(String[] args) {
        try {
            showWelcomeBanner();
            runApplication();
        } catch (Exception e) {
            showError("Erreur inattendue", e.getMessage());
        } finally {
            scanner.close();
            showGoodbye();
        }
    }

    private static void showWelcomeBanner() {
        clearScreen();
        System.out.println(CYAN + BOLD + 
            "╔══════════════════════════════════════════════════════════════════════╗\n" +
            "║                BIENVENUE NY ANTSA                                    ║\n" +
            "║                     EXTRACTEUR DE MESSAGES CACHÉS                    ║\n" +
            "║                                                                      ║\n" +
            "║              Stéganographie Audio & Image avec Huffman               ║\n" +
            "║                                                                      ║\n" +
            "╚══════════════════════════════════════════════════════════════════════╝" + RESET);
        System.out.println();
        pause(1000);
    }

    private static void runApplication() {
        while (true) {
            try {
                // Sélection du type de média
                int mediaChoice = selectMediaType();
                if (mediaChoice == -1) break;

                // Configuration du fichier média
                String mediaPath = getMediaPath(mediaChoice);
                if (mediaPath == null) continue;

                // Calcul de la capacité
                MediaInfo mediaInfo = getMediaCapacity(mediaChoice, mediaPath);
                if (mediaInfo == null) continue;

                // Configuration des paramètres de récurrence
                RecurrenceParams params = getRecurrenceParams();
                if (params == null) continue;

                // Génération et validation des indices
                processIndices(mediaChoice, mediaPath, mediaInfo, params);

                // Demander si l'utilisateur veut continuer
                if (!askContinue()) break;

            } catch (Exception e) {
                showError("Erreur lors du traitement", e.getMessage());
                if (!askContinue()) break;
            }
        }
    }

    private static int selectMediaType() {
        showSection("SÉLECTION DU TYPE DE MÉDIA");
        System.out.println(YELLOW + "┌─────────────────────────────────────┐");
        System.out.println("│  1  Image (PNG)                            │");
        System.out.println("│  2  Audio (WAV)                            │");
        System.out.println("│  3  Quitter                                │");
        System.out.println("└─────────────────────────────────────┘" + RESET);
        System.out.print(CYAN + " Votre choix : " + RESET);
        
        int choice = getIntInput();
        if (choice == 0) return -1;
        if (choice == 1 || choice == 2) return choice == 1 ? 1 : 0;
        
        showError("Choix invalide", "Veuillez sélectionner 1, 2 ou 0");
        return selectMediaType();
    }

    private static String getMediaPath(int mediaChoice) {
        String mediaType = (mediaChoice == 1) ? "image" : "audio";
        showSection("FICHIER " + mediaType.toUpperCase());
        
        System.out.println(BLUE + "Conseils :");
        System.out.println("   Utilisez un chemin complet (ex: C:\\dossier\\fichier.png)");
        System.out.println("     Vérifiez que le fichier existe et est accessible" + RESET);
        System.out.println();
        
        System.out.print(CYAN + "Chemin du fichier " + mediaType + " : " + RESET);
        String path = scanner.nextLine().trim();
        
        if (path.isEmpty()) {
            showError("Chemin vide", "Veuillez entrer un chemin valide");
            return null;
        }
        
        File file = new File(path);
        if (!file.exists()) {
            showError("Fichier introuvable", "Le fichier spécifié n'existe pas : " + path);
            return null;
        }
        
        showSuccess("Fichier trouvé : " + file.getName());
        return path;
    }

    private static MediaInfo getMediaCapacity(int mediaChoice, String mediaPath) {
        showSection("ANALYSE DU MÉDIA");
        showProgress("Analyse en cours...");
        
        try {
            long maxSize;
            String fileName;
            
            if (mediaChoice == 1) {
                maxSize = ImageSteganography.getImageCapacityInBits(mediaPath);
                fileName = "indices_image.txt";
                System.out.println(GREEN + "Image analysée avec succès");
                System.out.println("   Capacité : " + maxSize + " bits" + RESET);
            } else {
                maxSize = AudioSteganography.getSampleCount(mediaPath);
                fileName = "indices_son.txt";
                System.out.println(GREEN + "Audio analysé avec succès");
                System.out.println("   Échantillons : " + maxSize + RESET);
            }
            
            return new MediaInfo(maxSize, fileName);
            
        } catch (IOException e) {
            showError("Erreur d'analyse", "Impossible d'analyser le fichier : " + e.getMessage());
            return null;
        }
    }

    private static RecurrenceParams getRecurrenceParams() {
        showSection("PARAMÈTRES DE RÉCURRENCE");
        System.out.println(BLUE + "Configuration de la suite Un+1 = (a×Un + b) mod m" + RESET);
        System.out.println();

        try {
            System.out.print(CYAN + "Coefficient multiplicateur (a) : " + RESET);
            long a = getLongInput();
            
            System.out.print(CYAN + "Terme additif (b) : " + RESET);
            long b = getLongInput();
            
            System.out.print(CYAN + "Modulo (m) : " + RESET);
            long m = getLongInput();
            
            System.out.print(CYAN + "Valeur initiale (U0) : " + RESET);
            long U0 = getLongInput();
            
            System.out.print(CYAN + "Longueur du message à extraire : " + RESET);
            int messageLength = getIntInput();

            if (m <= 0) {
                showError("Paramètre invalide", "Le modulo doit être positif");
                return null;
            }
            
            if (messageLength <= 0) {
                showError("Paramètre invalide", "La longueur du message doit être positive");
                return null;
            }

            showParametersSummary(a, b, m, U0, messageLength);
            return new RecurrenceParams(a, b, m, U0, messageLength);
            
        } catch (Exception e) {
            showError("Erreur de saisie", "Veuillez entrer des nombres valides");
            return null;
        }
    }

    private static void showParametersSummary(long a, long b, long m, long U0, int messageLength) {
        System.out.println();
        System.out.println(YELLOW + "RÉCAPITULATIF DES PARAMÈTRES :");
        System.out.println("┌─────────────────────────────────────┐");
        System.out.printf("│ a (multiplicateur) : %-14d │%n", a);
        System.out.printf("│ b (additif)        : %-14d │%n", b);
        System.out.printf("│ m (modulo)         : %-14d │%n", m);
        System.out.printf("│ U0 (initial)       : %-14d │%n", U0);
        System.out.printf("│ Longueur message   : %-14d │%n", messageLength);
        System.out.println("└─────────────────────────────────────┘" + RESET);
    }

    private static void processIndices(int mediaChoice, String mediaPath, MediaInfo mediaInfo, RecurrenceParams params) {
        showSection("  GÉNÉRATION DES INDICES");
        showProgress("Génération des indices...");

        Recurrence rec = new Recurrence(params.a, params.b, params.m, params.U0);
        List<Long> indices = rec.generateIndicesWithWrap(params.messageLength, mediaInfo.maxSize);

        System.out.println(GREEN + "" + indices.size() + " indices générés" + RESET);

        // Vérification de l'ordre
        boolean isValid = rec.checkIndicesOrder(indices);
        if (isValid) {
            System.out.println(GREEN + "Les indices sont en ordre croissant" + RESET);
        } else {
            System.out.println(YELLOW + "ATTENTION : Les indices ne sont pas en ordre croissant !" + RESET);
        }

        // Sauvegarde des indices
        String outputPath = basePath + File.separator + mediaInfo.fileName;
        rec.saveIndicesToFile(indices, outputPath);
        System.out.println(GREEN + "Indices sauvegardés dans : " + mediaInfo.fileName + RESET);

        // Extraction du message
        extractMessage(mediaChoice, mediaPath, indices);
    }

    private static void extractMessage(int mediaChoice, String mediaPath, List<Long> indices) {
        showSection("  EXTRACTION DU MESSAGE");
        showProgress("Extraction en cours...");

        try {
            List<Integer> intIndices = indices.stream().map(Long::intValue).toList();
            String binaryMessage;

            if (mediaChoice == 1) {
                binaryMessage = ImageSteganography.extraireMessage(mediaPath, intIndices);
                System.out.println(GREEN + "Message extrait de l'image" + RESET);
            } else {
                binaryMessage = AudioSteganography.extraireMessage(mediaPath, intIndices);
                System.out.println(GREEN + "Message extrait de l'audio" + RESET);
            }

            System.out.println();
            System.out.println(PURPLE + "  MESSAGE BINAIRE :");
            System.out.println("┌" + "─".repeat(Math.min(binaryMessage.length() + 2, 78)) + "┐");
            
            if (binaryMessage.length() > 76) {
                for (int i = 0; i < binaryMessage.length(); i += 76) {
                    int end = Math.min(i + 76, binaryMessage.length());
                    System.out.printf("│ %-76s │%n", binaryMessage.substring(i, end));
                }
            } else {
                System.out.printf("│ %-76s │%n", binaryMessage);
            }
            
            System.out.println("└" + "─".repeat(Math.min(binaryMessage.length() + 2, 78)) + "┘" + RESET);

            proposeHuffmanDecoding(binaryMessage);

        } catch (IOException | IllegalArgumentException e) {
            showError("Erreur d'extraction", e.getMessage());
        }
    }

    private static void proposeHuffmanDecoding(String binaryMessage) {
        System.out.println();
        System.out.print(CYAN + " Voulez-vous décoder avec Huffman ? (o/n) : " + RESET);
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("o") || response.equals("oui")) {
            performHuffmanDecoding(binaryMessage);
        } else {
            System.out.println(YELLOW + "  Décodage Huffman ignoré" + RESET);
        }
    }

    private static void performHuffmanDecoding(String binaryMessage) {
        showSection(" DÉCODAGE HUFFMAN");
        
        System.out.print(CYAN + " Chemin du fichier texte de référence : " + RESET);
        String referencePath = scanner.nextLine().trim();

        if (referencePath.isEmpty()) {
            showError("Chemin vide", "Chemin de fichier requis pour le décodage Huffman");
            return;
        }

        File refFile = new File(referencePath);
        if (!refFile.exists()) {
            showError("Fichier introuvable", "Le fichier de référence n'existe pas");
            return;
        }

        showProgress("Décodage Huffman en cours...");

        try {
            String referenceText = new String(Files.readAllBytes(Paths.get(referencePath)), "UTF-8");
            Map.Entry<String, Map<Character, String>> refResult = HuffmanCodingCharacter.encoder(referenceText);
            Map<Character, String> table = refResult.getValue();

            String decodedMessage = HuffmanCodingCharacter.decoder(binaryMessage, table);
            
            System.out.println(GREEN + " Décodage Huffman réussi !" + RESET);
            System.out.println();
            System.out.println(PURPLE + BOLD + " MESSAGE FINAL DÉCODÉ :");
            System.out.println("╔" + "═".repeat(Math.min(decodedMessage.length() + 2, 78)) + "╗");
            
            if (decodedMessage.length() > 76) {
                for (int i = 0; i < decodedMessage.length(); i += 76) {
                    int end = Math.min(i + 76, decodedMessage.length());
                    System.out.printf("║ %-76s ║%n", decodedMessage.substring(i, end));
                }
            } else {
                System.out.printf("║ %-76s ║%n", decodedMessage);
            }
            
            System.out.println("╚" + "═".repeat(Math.min(decodedMessage.length() + 2, 78)) + "╝" + RESET);

        } catch (IOException e) {
            showError("Erreur de lecture", "Impossible de lire le fichier de référence : " + e.getMessage());
        } catch (Exception e) {
            showError("Erreur de décodage", "Échec du décodage Huffman : " + e.getMessage());
        }
    }

    private static void showSection(String title) {
        System.out.println();
        System.out.println(CYAN + BOLD + "═".repeat(20) + " " + title + " " + "═".repeat(20) + RESET);
        System.out.println();
    }

    private static void showProgress(String message) {
        System.out.print(YELLOW + "" + message + RESET);
        pause(500);
        System.out.println();
    }

    private static void showSuccess(String message) {
        System.out.println(GREEN + "✓ " + message + RESET);
    }

    private static void showError(String title, String message) {
        System.out.println();
        System.out.println(RED + BOLD + "" + title.toUpperCase() + RESET);
        System.out.println(RED + "   " + message + RESET);
        System.out.println();
    }

    private static boolean askContinue() {
        System.out.println();
        System.out.print(CYAN + " Voulez-vous traiter un autre fichier ? (o/n) : " + RESET);
        String response = scanner.nextLine().trim().toLowerCase();
        return response.equals("o") || response.equals("oui");
    }

    private static void showGoodbye() {
        System.out.println();
        System.out.println(CYAN + BOLD + 
            "╔══════════════════════════════════════════════════════════════════════╗\n" +
            "║                                                                      ║\n" +
            "║                            MERCI ET À BIENTÔT Ny Antsa!              ║\n" +
            "║                                                                      ║\n" +
            "╚══════════════════════════════════════════════════════════════════════╝" + RESET);
    }

    private static int getIntInput() {
        try {
            int value = scanner.nextInt();
            scanner.nextLine();
            return value;
        } catch (Exception e) {
            scanner.nextLine(); 
            throw new IllegalArgumentException("Nombre entier attendu");
        }
    }

    private static long getLongInput() {
        try {
            long value = scanner.nextLong();
            scanner.nextLine(); 
            return value;
        } catch (Exception e) {
            scanner.nextLine(); 
            throw new IllegalArgumentException("Nombre entier long attendu");
        }
    }

    private static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[2J\033[H");
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    private static void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static class MediaInfo {
        final long maxSize;
        final String fileName;

        MediaInfo(long maxSize, String fileName) {
            this.maxSize = maxSize;
            this.fileName = fileName;
        }
    }

    private static class RecurrenceParams {
        final long a, b, m, U0;
        final int messageLength;

        RecurrenceParams(long a, long b, long m, long U0, int messageLength) {
            this.a = a;
            this.b = b;
            this.m = m;
            this.U0 = U0;
            this.messageLength = messageLength;
        }
    }
}