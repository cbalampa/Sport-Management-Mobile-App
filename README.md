# Sport Management Mobile App

Πίνακας Περιεχομένων
=================
* [Περιγραφή Εφαρμογής](#περιγραφή-εφαρμογής)
* [Τεχνολογίες](#τεχνολογίες)
* [Setup](#setup)
* [Λειτουργίες](#λειτουργίες)

## Περιγραφή Εφαρμογής
Σκοπός τοu project είναι η υλοποίηση μιας εφαρμογής για κινητές συσκευές σχετική με αθλητικούς αγώνες μέσω του προγράμματος Android Studio. Για το συγκεκριμένο Project είναι ανάγκη να χρησιμοποιηθούν δύο ειδών βάσεις: μία τοπική (Room API) στην οποία θα αποθηκεύονται οι πληροφορίες σχετικά με τους Αθλητές, τις Ομάδες και τα Αθλήματα και μία απομακρυσμένη (Firebase Firestore), όπου εκεί θα καταχωρούνται οι Αγώνες και οι Λογαριασμού των χρηστών που χρησιμοποιούν την εφαρμογή.

## Τεχνολογίες
Για την δημιουργία του project χρησιμοποιήθηκαν οι παρακάτω τεχνολογίες:
- Java
- Android Studio
- Room API
- Firebase Firestore
- SQL

## Setup
Μπορείτε να δοκιμάσετε το project κατεβάζοντας απλά τα αρχεία που θα βρείτε στον παρακάτω σύνδεσμο:
[https://drive.google.com/drive/folders/1-XG4xadguRQwnNyhNfvz2mIKwyeE4LWS?usp=sharing](https://drive.google.com/drive/folders/1-XG4xadguRQwnNyhNfvz2mIKwyeE4LWS?usp=sharing)

## Βάση Δεδομένων
Η βάση αποτελείται από ένα σύνολο οκτώ πινάκων οι οποίοι σχετίζονται μεταξύ τους με τη χρήση ξένων κλειδιών όπως φαίνεται στην παρακάτω εικόνα.


![Database Diagram](https://user-images.githubusercontent.com/73292440/134400105-bb5b040d-0687-437e-9ff1-42801c3a875d.png)

Πιο αναλυτικά, έχουμε ένα σύνολο πέντε πινάκων εκ των οποίων οι τρεις θα αποθηκεύονται εσωτερικά της κινητής συσκευής, ενώ οι άλλοι δύο θα αποθηκεύονται σε κάποιο Cloud server της Google. Να σημειωθεί εδώ ότι ο πίνακας Games παίρνει δεδομένα από όλους τους πίνακες της τοπικής βάσης.

## Λειτουργίες
Διαχείριση Αθλητή
- Εισαγωγή αθλητή στην τοπική βάση δεδομένων
- Επεξεργασία/ενημέρωση στοιχείων αθλητή
- Διαγραφή αθλητή από την τοπική βάση δεδομένων

 
Διαχείριση Ομάδας
- Εισαγωγή ομάδας στην τοπική βάση δεδομένων 
- Επεξεργασία/ενημέρωση στοιχείων ομάδας
- Διαγραφή ομάδας από την τοπική βάση δεδομένων


Διαχείριση Αθλήματος
- Εισαγωγή αθλήματος στην τοπική βάση δεδομένων 
- Επεξεργασία/ενημέρωση στοιχείων αθλήματος 
- Διαγραφή αθλήματος από την τοπική βάση δεδομένων

Διαχείριση Αγώνων
- Εισαγωγή αγώνα στην απομακρυσμένη βάση δεδομένων 
- Επεξεργασία/ενημέρωση στοιχείων αγώνα
- Διαγραφή αγώνα από την απομακρυσμένη βάση δεδομένων


Εμφάνιση Στατιστικών Στοιχείων
- Μέτρηση αθλητών στην τοπική βάση
- Αριθμός αθλητών ανά άθλημα
- Μέσος όρος ηλικίας αθλητών της τοπικής βάσης

<img src="https://user-images.githubusercontent.com/73292440/134453091-824c9763-c1ae-4b2b-bf99-f37472b81426.gif" alt="drawing" width="300"/>

