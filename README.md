# 🧪 Projet d'Examen Final – Codage Huffman et Décodage de Message Caché

## 🎯 Objectif

Processus du projet  :
- Générer un texte à partir du numéro d’étudiant
- Le compresser avec Huffman
- Identifier le langage du code
- Utiliser ce codage pour décoder un message caché dans une **image** et un **son**, en générant des indices à l’aide d’une suite pseudo-aléatoire à **saut**.

---

## ✅ Étapes du projet

---

### 🧩 Partie 1 : Codage Huffman

1. **Préparation**
   - Vérifie le **numéro d’étudiant**:2754
   - Génère :
     - un **texte de base**
     - les **valeurs `a` et `b`**
     - une **valeur de modulo `m`**

2. **Codage**
   - Appliquer le **codage de Huffman** au texte
   - Vérifier que le codage est valide (**code préfixe**, **sans ambiguïté**)

3. **Langage**
   - Identifier le **langage de codage** parmi 3 propositions
   - Utiliser ce langage pour le décodage final

4. **Préparation des indices**
   - Utiliser la suite à saut :
     ```
     U₀ = 1
     Uₙ = (a × Uₙ₋₁ + b) mod m
     ```
   - Interprétation :
     - Chaque `Uₙ` est un **saut à effectuer** à partir de la **position précédente**
     - Exemple : `U₁ = 3` → on saute 3 positions depuis le point précédent pour lire le bit
   - Répéter jusqu’à obtenir un nombre d’indices égal à la **longueur (en bits) du message caché**

---

### 🖼️ Partie 2 : Décodage du message dans l’image

1. Utiliser les **indices générés** (par saut)
2. Lire les **bits cachés dans l’image** (souvent LSB des pixels ou canaux)
3. Reconstituer le **message binaire**
4. **Décoder** le message en utilisant la **table de Huffman**

---

### 🎧 Partie 3 : Décodage du message dans le son

1. Même procédé que pour l’image :
   - Utiliser les **indices à saut**
   - Extraire les bits cachés dans les **échantillons audio**
   - Reconstituer et décoder le message avec Huffman

---

## 📌 Récapitulatif technique

- **Suite utilisée** :
  ```math
  U₀ = 1  
  Uₙ = (a × Uₙ₋₁ + b) mod m
