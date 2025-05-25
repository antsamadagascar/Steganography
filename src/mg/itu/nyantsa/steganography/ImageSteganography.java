package steganography;

import huffman.HuffmanCodingCharacter;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
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

        if (image.getType() != BufferedImage.TYPE_BYTE_GRAY) {
            throw new IllegalArgumentException("L'image doit être en niveaux de gris (TYPE_BYTE_GRAY)");
        }

        int largeur = image.getWidth();
        int hauteur = image.getHeight();
        WritableRaster raster = image.getRaster();

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

            int gray = raster.getSample(x, y, 0);
            int bit = message.charAt(i) == '1' ? 1 : 0;
            int newGray = (gray & 0xFE) | bit;

            raster.setSample(x, y, 0, newGray);
            pixelsModifies.add(index);
        }

        ImageIO.write(image, "png", new File(outputPath));
        return pixelsModifies;
    }


    public static String extraireMessage(String imagePath, List<Integer> pixelsIndices) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));
    
        if (image.getType() != BufferedImage.TYPE_BYTE_GRAY) {
            throw new IllegalArgumentException("L'image doit être en niveaux de gris (TYPE_BYTE_GRAY)");
        }
    
        Raster raster = image.getRaster();
        int largeur = image.getWidth();
        int hauteur = image.getHeight();
        int bitsPerPixel = raster.getSampleModel().getSampleSize(0);
    
        List<Integer> graySamples = new ArrayList<>();
        List<Integer> bits = new ArrayList<>();
        StringBuilder messageBinaire = new StringBuilder();
    
       // System.out.println("Indices : " + pixelsIndices);
    

        for (int index : pixelsIndices) {
      //     if (index == 0) continue;  // saute l'indice 0
            int x = index % largeur;
            int y = index / largeur;
    
            if (x >= largeur || y >= hauteur) {
                throw new IllegalArgumentException("Indice de pixel invalide: " + index);
            }
    
            int gray = raster.getSample(x, y, 0);
            int bit = gray & 1;
    
            graySamples.add(gray);
            bits.add(bit);
            messageBinaire.append(bit);
        }
    
        // System.out.println("Sample: " + graySamples);
        // System.out.println("Bits: " + bits);
        // System.out.println("bitsPerPixel:" + bitsPerPixel);
    
        return messageBinaire.toString();
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

    public static long getImageCapacityInBits(String imagePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));
        Raster raster = image.getRaster();
        int largeur = image.getWidth();
        int hauteur = image.getHeight();
    
        int bitsPerSample = raster.getSampleModel().getSampleSize(0);
        int numBands = raster.getNumBands();
    
        long totalBits = (long) largeur * hauteur * bitsPerSample * numBands;
    
        // System.out.println("Largeur : " + largeur);
        // System.out.println("Hauteur : " + hauteur);
        // System.out.println("Bits par échantillon : " + bitsPerSample);
        // System.out.println("Nombre de bandes : " + numBands);
        // System.out.println("Capacité totale (bits) : " + totalBits);
    
        return totalBits;
    }
    
    public static void main(String[] args) {
        try {
            String imagePath = "C:\\Users\\Ny Antsa\\Documents\\CODAGE\\HUFFMAN\\data\\data-test-tendry\\la_lune_est_belle_67_1011000100110111101000010100101010110100100111101110101101110110101.png";
            //getImageCapacityInBits(imagePath);
            String fichierIndices = "C:\\Users\\Ny Antsa\\Documents\\Fianarana\\semestre6\\Mr Tsinjo\\final-exam-codage\\tete2.txt";
            List<Integer> pixelsIndices = lireIndicesDepuisFichier(fichierIndices);
    
            String binaryMessage = extraireMessage(imagePath, pixelsIndices);
                System.out.println("Message binaire extrait de l'image : " + binaryMessage);

            /// String texteReference = new String(Files.readAllBytes(Paths.get("C:\\Users\\Ny Antsa\\Downloads\\data\\text")), "UTF-8");
            // Map.Entry<String, Map<Character, String>> refResult = HuffmanCodingCharacter.encoder(texteReference);
            // Map<Character, String> table = refResult.getValue();
            //String messageTexte = HuffmanCodingCharacter.decoder(binaryMessage, table);
            //  System.out.println("Message texte extrait de l'image : " + messageTexte);
    
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture de l'image : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
    
}
    

