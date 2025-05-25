package steganography;

import huffman.HuffmanCodingCharacter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

public class ImageSteganography {
    
    public static List<Integer> cacherMessage(String imagePath, String outputPath, 
                                          String message, List<Integer> pixelsIndices) throws IOException {
        if (message.length() > pixelsIndices.size()) {
            throw new IllegalArgumentException("Le message est trop long pour être caché dans les pixels spécifiés");
        }

        for (char c : message.toCharArray()) {
            if (c != '0' && c != '1') {
                throw new IllegalArgumentException("Le message doit être composé uniquement de '0' et '1'");
            }
        }

        BufferedImage image = ImageIO.read(new File(imagePath));
        int largeur = image.getWidth();
        int hauteur = image.getHeight();

        for (int index : pixelsIndices) {
            int x = index % largeur;
            int y = index / largeur;
            if (x >= largeur || y >= hauteur) {
                throw new IllegalArgumentException("Indice de pixel invalide: " + index);
            }
        }

        List<Integer> pixelsModifies = new ArrayList<>();
        for (int i = 0; i < message.length(); i++) {
            int index = pixelsIndices.get(i);
            int x = index % largeur;
            int y = index / largeur;

            int rgb = image.getRGB(x, y);

            int alpha = (rgb >> 24) & 0xFF;
            int rgbSansAlpha = rgb & 0x00FFFFFF;

            int bit = message.charAt(i) == '1' ? 1 : 0;

            int alphaModifie = (alpha & 0xFE) | bit;

            int rgbModifie = (alphaModifie << 24) | rgbSansAlpha;

            image.setRGB(x, y, rgbModifie);
            pixelsModifies.add(index);
        }

        ImageIO.write(image, "png", new File(outputPath));

        return pixelsModifies;
    }

    public static String extraireMessage(String imagePath, List<Integer> pixelsIndices) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));
        int largeur = image.getWidth();
        int hauteur = image.getHeight();
    
        for (int index : pixelsIndices) {
            int x = index % largeur;
            int y = index / largeur;
            if (x >= largeur || y >= hauteur) {
                throw new IllegalArgumentException("Indice de pixel invalide: " + index);
            }
        }
    
        StringBuilder message = new StringBuilder();
        List<Integer> alphaValues = new ArrayList<>();
    
        System.out.println("Indices : " + pixelsIndices);
    
        for (int index : pixelsIndices) {
            int x = index % largeur;
            int y = index / largeur;
    
            int rgb = image.getRGB(x, y);
            int alpha = (rgb >> 24) & 0xFF;
            alphaValues.add(alpha);
    
            int bit = alpha & 1;
            message.append(bit);
        }
        System.out.println("Sample: " + alphaValues);
    
        String binaryMessage = message.toString();
    
        StringBuilder bitsAvecCrochets = new StringBuilder("[");
        for (int i = 0; i < binaryMessage.length(); i++) {
            bitsAvecCrochets.append(binaryMessage.charAt(i));
            if (i != binaryMessage.length() - 1) {
                bitsAvecCrochets.append(", ");
            }
        }
        bitsAvecCrochets.append("]");
    
        System.out.println("Bits: " + bitsAvecCrochets.toString());
    
        return binaryMessage;
    }
    
    public static List<Integer> lireIndicesDepuisFichier(String fichierPath) throws IOException {
        List<Integer> indices = new ArrayList<>();
        List<String> lignes = Files.readAllLines(Paths.get(fichierPath));

        for (String ligne : lignes) {
            try {
                indices.add(Integer.valueOf(ligne.trim()));
            } catch (NumberFormatException e) {
                System.err.println("Erreur de format dans le fichier, ligne ignorée: " + ligne);
            }
        }
        return indices;
    }

    public static void main(String[] args) {
        try {
            String imagePath = "C:\\Users\\Ny Antsa\\Documents\\CODAGE\\HUFFMAN\\data\\Séance 3 - code\\image_coded.png";
    
            String fichierIndices = "C:\\Users\\Ny Antsa\\Documents\\CODAGE\\HUFFMAN\\data\\Séance 3 - code\\indices.txt";
            List<Integer> pixelsIndices = lireIndicesDepuisFichier(fichierIndices);
    
            String binaryMessage = extraireMessage(imagePath, pixelsIndices);
    
            String texteReference = new String(Files.readAllBytes(Paths.get("C:\\Users\\Ny Antsa\\Documents\\CODAGE\\HUFFMAN\\data\\Séance 3 - code\\text.txt")), "UTF-8");
    
            Map.Entry<String, Map<Character, String>> refResult = HuffmanCodingCharacter.encoder(texteReference);
            Map<Character, String> table = refResult.getValue();
            String messageImage = HuffmanCodingCharacter.decoder(binaryMessage, table);
            System.out.println("Message extrait de l'image : " + messageImage);
    
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture de l'image : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
    
}
    

