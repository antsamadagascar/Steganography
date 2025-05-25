package steganography;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import huffman.HuffmanCodingCharacter;

public class AudioSteganography {

    public static void cacherMessage(String inputWav, String outputWav, String bits, List<Integer> indices) throws IOException {
        if (!new File(inputWav).exists()) 
        {    throw new IllegalArgumentException("Le fichier WAV d'entrée n'existe pas : " + inputWav); }

        if (indices == null || indices.isEmpty()) 
        {    throw new IllegalArgumentException("La liste des indices est vide ou null"); }

        if (bits == null || bits.isEmpty())
        {    throw new IllegalArgumentException("Le message à cacher est vide ou null"); }

        if (indices.size() < bits.length()) 
        {    throw new IllegalArgumentException("Pas assez d'indices pour cacher tous les bits du message"); }
    
        try (FileInputStream in = new FileInputStream(inputWav)) {
            byte[] header = new byte[44];
            in.read(header);
    
            int bitsPerSample = ((header[34] & 0xFF) | ((header[35] & 0xFF) << 8));
            if (bitsPerSample != 8 && bitsPerSample != 16 && bitsPerSample != 24 && bitsPerSample != 32) 
            {   throw new UnsupportedOperationException("Résolution non supportée : " + bitsPerSample + " bits"); }
    
            byte[] data = in.readAllBytes();
            int bytesPerSample = bitsPerSample / 8;
            int maxIndex = data.length / bytesPerSample;
            if (data.length % bytesPerSample != 0) {
                throw new IllegalArgumentException("Données audio corrompues : taille non divisible par bytesPerSample");
            }
    
            for (int index : indices) {
                if (index < 0 || index >= maxIndex) {
                    throw new IllegalArgumentException("Index invalide : " + index);
                }
            }
    
            for (int i = 0; i < bits.length(); i++) {
                int index = indices.get(i);
                int sampleIndex = index * bytesPerSample;
                int sampleValue;
    
                if (bitsPerSample == 8) {
                    sampleValue = data[sampleIndex] & 0xFF;
                    sampleValue = (sampleValue & 0xFE) | (bits.charAt(i) - '0');
                    data[sampleIndex] = (byte) sampleValue;
                } else if (bitsPerSample == 16) {
                    sampleValue = (short) (((data[sampleIndex + 1] & 0xFF) << 8) | (data[sampleIndex] & 0xFF));
                    sampleValue = (sampleValue & 0xFFFE) | (bits.charAt(i) - '0');
                    data[sampleIndex] = (byte) (sampleValue & 0xFF);
                    data[sampleIndex + 1] = (byte) ((sampleValue >> 8) & 0xFF);
                } else if (bitsPerSample == 24) {
                    sampleValue = ((data[sampleIndex + 2] & 0xFF) << 16) | ((data[sampleIndex + 1] & 0xFF) << 8) | (data[sampleIndex] & 0xFF);
                    sampleValue = (sampleValue & 0xFFFFFE) | (bits.charAt(i) - '0');
                    data[sampleIndex] = (byte) (sampleValue & 0xFF);
                    data[sampleIndex + 1] = (byte) ((sampleValue >> 8) & 0xFF);
                    data[sampleIndex + 2] = (byte) ((sampleValue >> 16) & 0xFF);
                } else { // bitsPerSample == 32
                    sampleValue = ((data[sampleIndex + 3] & 0xFF) << 24) | ((data[sampleIndex + 2] & 0xFF) << 16) |
                                  ((data[sampleIndex + 1] & 0xFF) << 8) | (data[sampleIndex] & 0xFF);
                    sampleValue = (sampleValue & 0xFFFFFFFE) | (bits.charAt(i) - '0');
                    data[sampleIndex] = (byte) (sampleValue & 0xFF);
                    data[sampleIndex + 1] = (byte) ((sampleValue >> 8) & 0xFF);
                    data[sampleIndex + 2] = (byte) ((sampleValue >> 16) & 0xFF);
                    data[sampleIndex + 3] = (byte) ((sampleValue >> 24) & 0xFF);
                }
            }
    
            try (FileOutputStream out = new FileOutputStream(outputWav)) {
                out.write(header);
                out.write(data);
            }
        }
    }

    public static String extraireMessage(String stegoWav, List<Integer> indices) throws IOException {
        if (!new File(stegoWav).exists()) {
            throw new IllegalArgumentException("Le fichier WAV n'existe pas : " + stegoWav);
        }
        if (indices == null || indices.isEmpty()) {
            throw new IllegalArgumentException("La liste des indices est vide ou null");
        }
    
        try (FileInputStream in = new FileInputStream(stegoWav)) {
            byte[] header = new byte[44];
            in.read(header);
    
            int bitsPerSample = ((header[34] & 0xFF) | ((header[35] & 0xFF) << 8));
            if (bitsPerSample != 8 && bitsPerSample != 16 && bitsPerSample != 24 && bitsPerSample != 32) {
                throw new UnsupportedOperationException("Résolution non supportée : " + bitsPerSample + " bits");
            }
    
            byte[] data = in.readAllBytes();
            int bytesPerSample = bitsPerSample / 8; // Nombre d'octets par échantillon
            int maxIndex = data.length / bytesPerSample; // Nombre total d'échantillons
            if (data.length % bytesPerSample != 0) {
                throw new IllegalArgumentException("Données audio corrompues : taille non divisible par bytesPerSample");
            }
    
            for (int index : indices) {
                if (index < 0 || index >= maxIndex) {
                    throw new IllegalArgumentException("Index invalide : " + index);
                }
            }
    
            StringBuilder bits = new StringBuilder();
            List<Integer> samples = new ArrayList<>();
    
            for (int index : indices) {
                // if (index == 0) continue; // sauter l'indice 0

                int sampleIndex = index * bytesPerSample;
                int sampleValue;
                if (bitsPerSample == 8) {
                    sampleValue = data[index] & 0xFF;
                } else if (bitsPerSample == 16) {
                    sampleValue = (short) (((data[sampleIndex + 1] & 0xFF) << 8) | (data[sampleIndex] & 0xFF));
                } else if (bitsPerSample == 24) {
                    sampleValue = ((data[sampleIndex + 2] & 0xFF) << 16) | ((data[sampleIndex + 1] & 0xFF) << 8) | (data[sampleIndex] & 0xFF);
                } else { // bitsPerSample == 32
                    sampleValue = ((data[sampleIndex + 3] & 0xFF) << 24) | ((data[sampleIndex + 2] & 0xFF) << 16) |
                                  ((data[sampleIndex + 1] & 0xFF) << 8) | (data[sampleIndex] & 0xFF);
                }
                samples.add(sampleValue);
                bits.append(sampleValue & 1);
            }
    
            // Débogage
            System.out.println("Indices: " + indices.toString());
            System.out.println("Samples: " + samples.toString());
    
            StringBuilder bitsAvecCrochets = new StringBuilder("[");
            for (int i = 0; i < bits.length(); i++) {
                bitsAvecCrochets.append(bits.charAt(i));
                if (i != bits.length() - 1) {
                    bitsAvecCrochets.append(", ");
                }
            }
            bitsAvecCrochets.append("]");
    
            System.out.println("Bits: " + bitsAvecCrochets.toString());
    
            return bits.toString();
        }
    }
    
    public static void ecrireIndicesDansFichier(List<Integer> indices, String cheminFichier) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cheminFichier))) {
            for (Integer index : indices) {
                writer.write(index.toString());
                writer.newLine();
            }
            writer.close();
        }
    }

    public static long getSampleCount(String audioPath) throws IOException {
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(audioPath))) {
            AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();
            int channels = format.getChannels();
            float sampleRate = format.getSampleRate();
            int sampleSizeInBits = format.getSampleSizeInBits();
            int bytesPerFrame = format.getFrameSize();
            boolean isBigEndian = format.isBigEndian();
            AudioFormat.Encoding encoding = format.getEncoding();
    
            System.out.println("Format audio : " + encoding);
            System.out.println("Sample rate : " + sampleRate + " Hz");
            System.out.println("Sample size : " + sampleSizeInBits + " bits");
            System.out.println("Channels : " + channels);
            System.out.println("Frames : " + frames);
            System.out.println("Bytes per frame : " + bytesPerFrame);
            System.out.println("Endian : " + (isBigEndian ? "Big-endian" : "Little-endian"));
    
            if (bytesPerFrame <= 0) {
                throw new IOException("Format audio non supporté");
            }
    
            long totalSamples = frames * channels;
            System.out.println("Total samples : " + totalSamples);
            return totalSamples;
        } catch (UnsupportedAudioFileException e) {
            throw new IOException("Fichier audio non supporté", e);
        }
    }
    

    private static List<Integer> chargerIndices(String chemin) throws IOException {
        List<String> lignes = Files.readAllLines(Paths.get(chemin));
        List<Integer> indices = new ArrayList<>();
        for (String ligne : lignes) {
            indices.add(Integer.parseInt(ligne.trim()));
        }
        return indices;
    }

    public static void main(String[] args) throws IOException {
        String cheminFichierWav = "C:\\Users\\Ny Antsa\\Downloads\\data\\a_4_0001.wav";
        String cheminFichierIndices = "C:\\Users\\Ny Antsa\\Documents\\Fianarana\\semestre6\\Mr Tsinjo\\final-exam-codage\\indices_audio.txt";
       // getSampleCount(cheminFichierWav);
        // String texteReference = new String(Files.readAllBytes(Paths.get("C:\\Users\\Ny Antsa\\Documents\\CODAGE\\HUFFMAN\\data\\Séance 3 - code\\text.txt")), "UTF-8");
    
        List<Integer> indices = chargerIndices(cheminFichierIndices);
        // Map.Entry<String, Map<Character, String>> refResult = HuffmanCodingCharacter.encoder(texteReference);
        // Map<Character, String> table = refResult.getValue();

         String bits = AudioSteganography.extraireMessage(cheminFichierWav, indices);
            System.out.println("Message binaire extrait audio: " +bits);
        //String messageAudio = HuffmanCodingCharacter.decoder(bits, table);
        //   System.out.println("Message extrait de l'Audio : " + messageAudio);
    }

}
