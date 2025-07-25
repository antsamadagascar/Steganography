package recurrence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import steganography.AudioSteganography;
import steganography.ImageSteganography;

public class Recurrence {
    
    private final long a;
    private final long b;
    private final long m;
    private final long U0;
    
    public Recurrence(long a, long b, long m, long U0) {
        this.a = a;
        this.b = b;
        this.m = m;
        this.U0 = U0;
    }

    public long calculateUn(int n) {
        if (n == 0) return U0;
        
        long U = U0;
        for (int i = 1; i <= n; i++)
         {
            U = (a * U + b) % m;
        }
        return U;
    }

    public List<Long> generateSequence(int length) {
        List<Long> sequence = new ArrayList<>();
        long U = U0;
        for (int i = 0; i < length; i++) {
            U = (a * U + b) % m;
            sequence.add(U);
        }
        return sequence;
    }

    /* saut  */
    public List<Long> generateIndices(int messageLength) {
        List<Long> indices = new ArrayList<>();
        long currentIndex = U0;
        indices.add(currentIndex);
        
        long U = U0;
        for (int i = 0; i < messageLength; i++) {
            U = (a * U + b) % m;
            currentIndex += U;
            indices.add(currentIndex);
        }
        
        return indices;
    }

    public List<Long> generateIndicesWithWrap(int messageLength, long maxSize) {
        List<Long> indices = new ArrayList<>();
        
        long currentIndex = U0 % maxSize;
        indices.add(currentIndex);
        
        long U = U0;
        for (int i = 0; i < messageLength; i++) {
            U = (a * U + b) % m;
            currentIndex = (currentIndex + U) % maxSize;
            indices.add(currentIndex);
        }
        
        return indices;
    }

    public boolean checkIndicesOrder(List<Long> indices) {
        for (int i = 1; i < indices.size(); i++) {
            if (indices.get(i) <= indices.get(i-1)) {
                System.out.println("ATTENTION: Indice " + indices.get(i) + 
                                 " à la position " + i + " <= " + indices.get(i-1));
                return false;
            }
        }
    //    System.out.println("Tous les indices sont en ordre croissant");
        return true;
    }

    public void displayGeneration(int messageLength) {
        List<Long> sequence = generateSequence(messageLength);
        List<Long> indices = generateIndices(messageLength);
        
        System.out.println("Suite générée:");
        for (int i = 0; i < sequence.size(); i++) {
            if (i == 0) {
                System.out.printf("U0 = %d (position initiale)\n", sequence.get(i));
            } else {
                System.out.printf("U%d = %d (saut de %d)\n", i, sequence.get(i), sequence.get(i));
            }
        }
        
        System.out.println("\nIndices d'extraction:");
        for (int i = 0; i < indices.size(); i++) {
            System.out.printf("Bit[%d] -> Indice %d\n", i, indices.get(i));
        }
    }

    public void saveIndicesToFile(List<Long> indices, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Long index : indices) {
                writer.write(index.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier : " + e.getMessage());
        }
    }

}