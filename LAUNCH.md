# TEST

---

## Partie huffman

### ➤ Migenerer table huffman a partir texte  
**(Dans la classes : `HuffmanCodingCaracter`)**

#### ▶ RUN java (compile le classe)

**.Input :**  
- Entrer le chemin du fichier (copier le chemin misy anle texte avadika huffman) : ...

**.Output :**  
- Information verification code huffman si correct ou pas :  
  - moyenne  
  - entropie  
  - ditribution longeur  
  - etc...

- Representation dictionnaire de huffamn du texte :

Exemple SORTIE:
Table de codage Huffman (alphabet + espace uniquement):
ESPACE -> 101
'a' -> 1001
'b' -> 0000111
'c' -> 11001
'd' -> 10000
'e' -> 1101

→ Mahazo langage a partir du table codage :  
`L = {101, 1001, 0000111, 11001, 10000, 1101}`

---

### ➤ Miverifier oe code sa tsy code  
**(Dans la classe `CheckCode.java`)**

#### ▶ RUN java (compile le classe)

**.Input :**  
- Choisissez une option :
1.    Exécuter les tests prédéfinis

2.  Tester un langage personnalisé

3.  Voir les exemples rapides

4.  Quitter

- Pour tester choisissez entrer : `2`  
- Entrer le langage a verifier :

Exemple:
    101 1001 0000111 11001 10000 1101


**.Output :**  
→ code na tsia code

---

## Decodage images

### ➤ Decodage message images apartir images

- Modifier le fichier de configuration dans :  
  `config/config.properties`

- Changer par le table huffman correct de Mr  
  (changer les valeurs OU value (KEY,VALUE)) :

  - Ex : `a = 0001`, etc...  
    (changer les key ou valeurs en fonction du table huffman donnees)

#### ▶ RUN JAVA (class `Main.java`)

**.Input :**  
- Entrer les parametres demander :  
  `(a, b, m, U0, longeur bite message)`  
- Entrer chemin ou se trouve l'image (format png)

**.Output :**  
- Message binaire cacher

**Pour trouver le message textuelles :**  
- Cliquer sur `o` :  
  → Resulat : message texte

---

## Decodage son

### ➤ Decodage message son apartir son
Zavatra atao voalany aloany decodage images na son :
- Modifier le fichier de configuration dans :  
  `config/config.properties`

- Changer par le table huffman correct de Mr  
  (changer les valeurs OU value (KEY,VALUE)) :

  - Ex : `a = 0001`, etc...  
    (changer les key ou valeurs en fonction du table huffman donnees)

#### ▶ RUN JAVA (class `Main.java`)

**.Input :**  
- Entrer les parametres demander :  
  `(a, b, m, U0, longeur bite message)`  
- Entrer chemin ou se trouve l'image (format png)

**.Output :**  
- Message binaire cacher

**Pour trouver le message textuelles :**  
- Cliquer sur `o` :  
  → Resulat : message texte
