# 🔐 Système de Stéganographie avec Codage Huffman

## 🎯 Vue d'ensemble

Ce projet implémente un système complet de stéganographie utilisant le codage Huffman pour cacher et extraire des messages dans des médias numériques (images et audio). Le système combine compression de données, génération pseudo-aléatoire et techniques de dissimulation d'information.

---

## 🔧 Fonctionnalités principales

### 📊 Compression et Codage
- **Génération de texte personnalisé** basée sur l'identifiant utilisateur
- **Codage Huffman optimal** avec construction d'arbre binaire
- **Validation automatique** du code préfixe (sans ambiguïté)
- **Détection de langage** parmi plusieurs propositions pour optimiser le décodage

### 🎲 Génération pseudo-aléatoire
- **Suite à saut configurable** : `Uₙ = (a × Uₙ₋₁ + b) mod m`
- **Paramètres dynamiques** (a, b, m) générés selon l'identifiant
- **Indices de position** calculés automatiquement pour l'extraction

### 🖼️ Stéganographie dans les images
- **Extraction LSB** (Least Significant Bit) des pixels
- **Support multi-canaux** (RGB, RGBA)
- **Navigation par indices** pour éviter la détection séquentielle
- **Reconstruction binaire** complète du message caché

### 🎧 Stéganographie dans l'audio
- **Traitement des échantillons** audio numériques
- **Extraction de bits** dans les données sonores
- **Synchronisation temporelle** avec les indices générés
- **Décodage temps réel** des messages cachés

### 🔍 Système de décodage
- **Table de Huffman inversée** pour la décompression
- **Validation de cohérence** des messages extraits
- **Support multi-formats** (texte, binaire)
- **Gestion d'erreurs** intégrée

---

## ⚙️ Architecture technique

### Composants principaux
1. **Générateur de texte** personnalisé
2. **Encodeur/Décodeur Huffman** optimisé
3. **Générateur d'indices** pseudo-aléatoires
4. **Extracteur de stéganographie** multi-média
5. **Système de validation** et vérification

### Algorithme de base
```
Initialisation → Génération texte → Codage Huffman → 
Détection langage → Calcul indices → Extraction bits → 
Décodage message → Validation résultat
```

### Paramètres configurables
- **Identifiant utilisateur** : Base de génération
- **Coefficients (a, b, m)** : Contrôle de la suite pseudo-aléatoire
- **Format de sortie** : Texte, binaire, hexadécimal
- **Niveau de validation** : Basique, avancé, complet

---

## 🚀 Cas d'usage

- **Communication sécurisée** via médias publics
- **Authentification de contenu** numérique
- **Recherche en cryptographie** et stéganographie
- **Éducation** en théorie de l'information
- **Analyse forensique** de médias numériques

---

## 📈 Performances

- **Compression efficace** grâce à Huffman
- **Extraction rapide** par navigation ciblée
- **Faible empreinte mémoire** avec traitement par flux
- **Scalabilité** pour gros volumes de données
- **Robustesse** face aux modifications mineures du média

---

## 🛡️ Sécurité

- **Dispersion pseudo-aléatoire** des bits cachés
- **Validation d'intégrité** des messages extraits
- **Résistance aux analyses** séquentielles
- **Flexibilité des paramètres** de génération