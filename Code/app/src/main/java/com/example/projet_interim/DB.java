package com.example.projet_interim;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import java.sql.SQLException;

public class DB {

    // users : id, username, role : { candidat, employeur, agence, admin }, mail
    // candidates : id, user_id, nom, prenom, date de naissance, nationalité, CV
    // employersAgency : id, user_id, nom entreprise, nom service/département, nom sous service/département, SIREN, mail2
    // annonces : id, creator_user_id, titre, description, coordonnées
    // annonces_prisent : id, creator_user_id, candidatId, titre, description, coordonnées
    // notifications : id, user_id, receiverID, senderName, titre, contenu
    // candidatures : id, candidat_id, annonce_id, CV(txt), LDM(txt)

    public ArrayList<ArrayList<String>> candidatures = new ArrayList<>();
    public ArrayList<ArrayList<String>> notifications = new ArrayList<>();
    public ArrayList<ArrayList<String>> annonces = new ArrayList<>();
    public ArrayList<ArrayList<String>> annonces_prisent = new ArrayList<>();
    public ArrayList<ArrayList<String>> users = new ArrayList<>();
    public ArrayList<ArrayList<String>> candidates = new ArrayList<>();
    public ArrayList<ArrayList<String>> employersAgency = new ArrayList<>();

    // ID auto incrémentiel
    int autoIncr_user = 0;
    int autoIncr_annonce = 0;
    int autoIncr_notification = 0;
    int autoIncr_candidature = 0;
    int autoIncr_candidatesId = 0;
    int autoIncr_employersAgencyId = 0;

    private transient Context context;

    public DB(Context context, Activity activity){
        this.context = context;
        readFromFile(activity);
    }

    // Lis le fichier DB.json dans /data/data/com.example.projet_interim/files et convertis le fichier json en objet DB, l'initialise
    public void readFromFile(Activity activity){

        // Si le fichier n'existe pas, le créé
        String file_name = activity.getFilesDir() + "/DB.json";
        File t = new File(file_name);
        if(t.exists()){
            try {

                InputStream inputStream =  context.openFileInput("DB.json");

                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                inputStream.close();
                String text = new String(buffer);

                Gson gson = new Gson();
                DB newDB = gson.fromJson(text, DB.class);

                if(newDB != null){
                    this.autoIncr_annonce = newDB.autoIncr_annonce;
                    this.autoIncr_candidature = newDB.autoIncr_candidature;
                    this.autoIncr_notification = newDB.autoIncr_notification;
                    this.autoIncr_user = newDB.autoIncr_user;
                    this.autoIncr_candidatesId = newDB.autoIncr_candidatesId;
                    this.autoIncr_employersAgencyId = newDB.autoIncr_employersAgencyId;

                    this.users = newDB.users;
                    this.annonces = newDB.annonces;
                    this.annonces_prisent = newDB.annonces_prisent;
                    this.notifications = newDB.notifications;
                    this.candidatures = newDB.candidatures;
                    this.candidates = newDB.candidates;
                    this.employersAgency = newDB.employersAgency;
                }

            } catch (IOException e) {
                System.out.println("Probleme lecture");
                e.printStackTrace();
            }
        }
    }

    // Convertis l'objet DB en fichier json et l'écris dans /data/data/com.example.projet_interim/files/DB.json
    public void writeToFile(){
        try {
            FileOutputStream fOut = context.openFileOutput("DB.json",0);
            Gson gson = new Gson();
            String json = gson.toJson(this);
            fOut.write(json.getBytes());
            fOut.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------- USER ---------------------------------------- //

    public boolean addCandidate(String username, String mail, String nom, String prenom, String dateNaissance, String nationalite, String cv){
        ArrayList<String> user = new ArrayList<>();
        user.add(String.valueOf(autoIncr_user));
        user.add(username);
        user.add("candidat");
        user.add(mail);

        users.add(user);

        ArrayList<String> candidat = new ArrayList<>();
        candidat.add(String.valueOf(autoIncr_candidatesId));
        candidat.add(String.valueOf(autoIncr_user));
        candidat.add(nom);
        candidat.add(prenom);
        candidat.add(dateNaissance);
        candidat.add(nationalite);
        candidat.add(cv);

        candidates.add(candidat);

        autoIncr_user = autoIncr_user + 1;
        autoIncr_candidatesId = autoIncr_candidatesId + 1;

        writeToFile();
        return true;
    }

    public boolean addEmployerAgency(String username, String role, String mail, String nomEntrep, String nomServiceDepartement, String nomSousServiceDepartement, String siren, String mail2){
        ArrayList<String> user = new ArrayList<>();
        user.add(String.valueOf(autoIncr_user));
        user.add(username);
        user.add(role);
        user.add(mail);

        users.add(user);

        ArrayList<String> employerAgency = new ArrayList<>();
        employerAgency.add(String.valueOf(autoIncr_candidatesId));
        employerAgency.add(String.valueOf(autoIncr_user));
        employerAgency.add(nomEntrep);
        employerAgency.add(nomServiceDepartement);
        employerAgency.add(nomSousServiceDepartement);
        employerAgency.add(siren);
        employerAgency.add(mail2);

        employersAgency.add(employerAgency);

        autoIncr_user = autoIncr_user + 1;
        autoIncr_employersAgencyId = autoIncr_employersAgencyId + 1;

        writeToFile();
        return true;
    }

    public boolean addAdmin(String username, String mail){
        ArrayList<String> user = new ArrayList<>();
        user.add(String.valueOf(autoIncr_user));
        user.add(username);
        user.add("admin");
        user.add(mail);

        users.add(user);
        autoIncr_user = autoIncr_user + 1;

        writeToFile();
        return true;
    }

    public ArrayList<String> getUserById(String id){
        for(ArrayList<String> user : users){
            if(user.get(0).equals(id)){
                return user;
            }
        }
        return null;
    }

    public ArrayList<String> getUserByName(String username){
        for(ArrayList<String> user : users){
            if(user.get(1).equals(username)){
                return user;
            }
        }
        return null;
    }

    public String getUserId(String username){
        for(ArrayList<String> user : users){
            if(user.get(1).equals(username)){
                return user.get(0);
            }
        }
        return null;
    }

    public String getUserNameFromID(String id){
        for(ArrayList<String> user : users){
            if(user.get(0).equals(id)){
                return user.get(1);
            }
        }
        return null;
    }

    // info = {user_ID, username, role, mail, nom, prenom, date de naissance, nationalité, CV}
    public ArrayList<String> getCandidatesInfo(String id){
        ArrayList<String> info = new ArrayList<>();

        for(ArrayList<String> user : users){
            if(user.get(0).equals(id)){
                info.add(id);
                info.add(user.get(1));
                info.add(user.get(2));
                info.add(user.get(3));

                for(ArrayList<String> candidate : candidates) {
                    if (candidate.get(1).equals(id)) {
                        info.add(candidate.get(2));
                        info.add(candidate.get(3));
                        info.add(candidate.get(4));
                        info.add(candidate.get(5));
                        info.add(candidate.get(6));
                    }
                }

                return info;
            }
        }
        return null;
    }

    // info = {role, mail, nom, prenom, date de naissance, nationalité, CV}
    // or
    // info = {role, mail, nom entreprise, nom service/département, nom sous service/département, SIREN, mail2}
    public boolean modifyUserInfo(String userID, ArrayList<String> info){

        for(int i = 0; i < users.size(); i++){
            if(users.get(i).get(0).equals(userID)){
                ArrayList<String> user = users.get(i);
                user.set(3, info.get(1));
                users.set(i, user);
            }
        }

        switch (info.get(0)){
            case "candidat":

                for(int i = 0; i < candidates.size(); i++){
                    if(candidates.get(i).get(1).equals(userID)){

                        if(info.get(0).equals("") || info.get(1).equals("") ||info.get(2).equals("") ||info.get(3).equals("")){
                            return false;
                        }

                        ArrayList<String> candid = candidates.get(i);
                        candid.set(2, info.get(2));
                        candid.set(3, info.get(3));
                        candid.set(4, info.get(4));
                        candid.set(5, info.get(5));
                        candid.set(6, info.get(6));

                        candidates.set(i, candid);
                    }
                }

                break;
            case "employeur":
            case "agence":
                //TODO
                for(int i = 0; i < employersAgency.size(); i++){
                    if(employersAgency.get(i).get(1).equals(userID)){

                        if(info.get(0).equals("") || info.get(1).equals("") ||info.get(2).equals("") ||info.get(3).equals("") || info.get(5).equals("")){
                            return false;
                        }

                        ArrayList<String> empAg = employersAgency.get(i);
                        empAg.set(2, info.get(2));
                        empAg.set(3, info.get(3));
                        empAg.set(4, info.get(4));
                        empAg.set(5, info.get(5));
                        empAg.set(6, info.get(6));
                        empAg.set(7, info.get(7));

                        employersAgency.set(i, empAg);
                    }
                }

                break;
            case "admin":
                break;
        }

        writeToFile();
        return true;
    }

    // -------------------------------------------------------------------------------------- //

    // -------------------------------------- ANNONCES -------------------------------------- //

    public ArrayList<ArrayList<String>> getAnnoncesFromCreatorId(String id){
        ArrayList<ArrayList<String>> c = new ArrayList<>();

        for(ArrayList<String> annonce : annonces){
            if(annonce.get(1).equals(id)){
                c.add(annonce);
            }
        }
        return c;
    }
    public ArrayList<String> getAnnonceFromId(String id){
        for(ArrayList<String> annonce : annonces){
            if(annonce.get(0).equals(id)){
                return annonce;
            }
        }
        return null;
    }

    public String getAnnonceTitleFromId(String id){
        for(ArrayList<String> annonce : annonces){
            if(annonce.get(0).equals(id)){
                return annonce.get(2);
            }
        }
        return null;
    }

    // récupère les annonces pour lesquelles a candidaté un utilisateur
    public ArrayList<ArrayList<String>> getCandidatureAnnonceForUserId(String id){
        ArrayList<ArrayList<String>> c = new ArrayList<>();

        for(ArrayList<String> candidature : candidatures){
            if(candidature.get(1).equals(id)){
                c.add(getAnnonceFromId(candidature.get(2)));
            }
        }
        return c;
    }

    public ArrayList<ArrayList<String>> getAnnonces_prisentForUserId(String id){
        ArrayList<ArrayList<String>> c = new ArrayList<>();

        for(ArrayList<String> annonce : annonces_prisent){
            if(annonce.get(2).equals(id)){
                annonce.remove(1);
                c.add(annonce);
            }
        }
        return c;
    }

    public void addAnnonce(String authorId, String title, String description, String coord){
        ArrayList<String> annonce = new ArrayList<>();
        annonce.add(String.valueOf(autoIncr_annonce));
        annonce.add(authorId);
        annonce.add(title);
        annonce.add(description);
        annonce.add(coord);
        annonces.add(annonce);

        autoIncr_annonce = autoIncr_annonce + 1;
        writeToFile();
    }

    public void removeAnnonceFromId(String id){

        ArrayList<ArrayList<String>> newAnnonces = new ArrayList<>();
        for(int i = 0; i < annonces.size(); i++){
            if(! annonces.get(i).get(0).equals(id)){
                newAnnonces.add(annonces.get(i));
            }
        }
        annonces = newAnnonces;

        ArrayList<ArrayList<String>> newCandidatures = new ArrayList<>();

        for(int i = 0; i < candidatures.size(); i++){
            if(! candidatures.get(i).get(2).equals(id)){
                newCandidatures.add(candidatures.get(i));
            }
        }

        candidatures = newCandidatures;
        writeToFile();
    }

    public ArrayList<ArrayList<String>> getAnnoncesFromUsername(String username){
        ArrayList<ArrayList<String>> ann = new ArrayList<>();

        for(ArrayList<String> annonce : annonces){
            String name = getUserNameFromID(annonce.get(1));
            if(name.equals(username)){
                ann.add(annonce);
            }
        }

        return ann;
    }

    // -------------------------------------------------------------------------------------- //

    // ------------------------------------ CANDIDATURES ------------------------------------ //

    public void applyTo(String authorId, String annonceId, String lm){
        ArrayList<String> candidature = new ArrayList<>();
        candidature.add(String.valueOf(autoIncr_candidature));
        candidature.add(authorId);
        candidature.add(annonceId);
        candidature.add(lm);
        candidatures.add(candidature);

        autoIncr_candidature = autoIncr_candidature + 1;
        writeToFile();
    }

    public boolean acceptCandidature(String candidatureId){

        String annonceId = null;
        String userId = null;
        for(ArrayList<String> candidature : candidatures){
            if(candidature.get(0).equals(candidatureId)){
                annonceId = candidature.get(2);
                userId = candidature.get(1);
                break;
            }
        }

        ArrayList<String> acceptedAnnonce = getAnnonceFromId(annonceId);
        removeAnnonceFromId(annonceId);

        acceptedAnnonce.add(2, userId);

        annonces_prisent.add(acceptedAnnonce);
        writeToFile();

        return true;
    }

    public ArrayList<ArrayList<String>> getCandidaturesForAnnonceID(String id){
        ArrayList<ArrayList<String>> c = new ArrayList<>();

        for(ArrayList<String> candidature : candidatures){
            if(candidature.get(2).equals(id)){
                c.add(candidature);
            }
        }

        return c;
    }

    public ArrayList<ArrayList<String>> getCandidaturesIntendedForUserID(String id){
        ArrayList<ArrayList<String>> c = new ArrayList<>();
        ArrayList<String> annonceIdsForUser = new ArrayList<>();

        for(ArrayList<String> annonce : annonces){
            if(annonce.get(1).equals(id)){
                annonceIdsForUser.add(annonce.get(0));
            }
        }

        for(ArrayList<String> candidature : candidatures){
            if(annonceIdsForUser.contains(candidature.get(2))){
                c.add(candidature);
            }
        }

        return c;
    }

    // Info = {user_ID, username, role, mail, nom, prenom, date de naissance, nationalité, CV, LM}
    public ArrayList<String> getInfoForCandidatureID(String id){
        ArrayList<String> info = new ArrayList<>();

        for(ArrayList<String> candidature : candidatures){
            if(candidature.get(0).equals(id))
            {
                ArrayList<String> user = getUserById(candidature.get(1));
                info = getCandidatesInfo(user.get(0));
                info.add(candidature.get(3));
                return info;
            }
        }

        return null;
    }

    public void removeCandidatureFromId(String id)
    {
        ArrayList<ArrayList<String>> newCandidatures = new ArrayList<>();
        for(int i = 0; i < candidatures.size(); i++){
            if(! candidatures.get(i).get(0).equals(id)){
                newCandidatures.add(candidatures.get(i));
            }
        }
        candidatures = newCandidatures;
        writeToFile();
    }

    // -------------------------------------------------------------------------------------- //

    // --------------------------------------- NOTIF ---------------------------------------- //

    public void addNotif(String authorId, String receiverID, String title, String content){
        ArrayList<String> msg = new ArrayList<>();
        msg.add(String.valueOf(autoIncr_notification));
        msg.add(authorId);
        msg.add(receiverID);
        msg.add(getUserNameFromID(authorId));
        msg.add(title);
        msg.add(content);

        notifications.add(msg);

        autoIncr_notification = autoIncr_notification + 1;
        writeToFile();
    }

    public ArrayList<ArrayList<String>> getNotifForUserID(String id){
        ArrayList<ArrayList<String>> notifs = new ArrayList<>();

        for(ArrayList<String> msg : notifications){
            if(msg.get(2).equals(id)){
                notifs.add(msg);
            }
        }
        return notifs;
    }

    public void removeNotifFromId(String id){

        ArrayList<ArrayList<String>> newNotif = new ArrayList<>();
        for(int i = 0; i < notifications.size(); i++){
            if(! notifications.get(i).get(0).equals(id)){
                newNotif.add(notifications.get(i));
            }
        }
        notifications = newNotif;
        writeToFile();
    }

    // -------------------------------------------------------------------------------------- //

    // Reset la bdd et init avec quelques exemples
    public static void exempleFillIfEmpty(Context c, Activity a){

        DB db = new DB(c, a);

        if(db.users.size() == 0){
            db.autoIncr_user = 0;
            db.autoIncr_candidature = 0;
            db.autoIncr_notification = 0;
            db.autoIncr_annonce = 0;
            db.users.clear();
            db.annonces.clear();
            db.candidatures.clear();
            db.annonces_prisent.clear();
            db.notifications.clear();

            db.addCandidate("JohnDoe", "john.doe@example.com", "John", "Doe", "01-01-1990", "France", "");
            db.addCandidate("JaneSmith", "jane.smith@example.com", "Jane", "Smith", "01-01-1995", "USA", "");
            db.addEmployerAgency("BigCorp", "employeur", "hr@bigcorp.com", "Big Corporation", "yes", "", "123456789", "contact@bigcorp.com");
            db.addEmployerAgency("RecruitersInc", "agence", "info@recruitersinc.com", "Recruiters Inc", "yes", "", "987654321", "info@recruitersinc.com");
            db.addAdmin("admin2", "admin@example.com");
            db.addCandidate("testuser", "test@example.com", "Test", "User", "01-01-2000", "France", "");

            db.addAnnonce("2", "Assistant Administratif", "Recherche un assistant administratif polyvalent pour une mission intérimaire de 3 mois. Les tâches incluent la gestion des appels téléphoniques, la saisie de données et le support administratif général.", "Agence Tempo\n789 Rue de l'Intérim, Tempoville\nTél : +123456789\nEmail : recrutement@agencetempo.com");
            db.addAnnonce("2", "Assistant Administratif", "Recherche un assistant administratif polyvalent pour une mission intérimaire de 3 mois. Les tâches incluent la gestion des appels téléphoniques, la saisie de données et le support administratif général.", "Agence Tempo\n789 Rue de l'Intérim, Tempoville\nTél : +123456789\nEmail : recrutement@agencetempo.com");

            db.addAnnonce("2", "Manutentionnaire", "Main Forte Intérim recrute des manutentionnaires pour une mission d'intérim dans un entrepôt logistique. Chargement et déchargement de marchandises, utilisation de chariots élévateurs et respect des consignes de sécurité.", "Main Forte Intérim\n456 Avenue de la Manutention, Logistiville\nTél : +987654321\nEmail : contact@mainforteinterim.com");

            db.addAnnonce("2", "Secrétaire Médical", "MédicInterim recherche un secrétaire médical pour une mission intérimaire dans un cabinet médical. Accueil des patients, gestion des dossiers médicaux et prise de rendez-vous.", "MédicInterim\n123 Boulevard de la Santé, Médiville\nTél : +246813579\nEmail : info@medicinterim.com");

            db.addAnnonce("2", "Développeur Web", "CodeTech Recrutement recherche un développeur web expérimenté pour rejoindre une équipe dynamique. Le candidat retenu participera au développement et à la maintenance de sites web et d'applications.", "CodeTech Recrutement\n567 Rue du Code, Techville\nTél : +987654321\nEmail : contact@codetechrecrutement.com");

            db.addAnnonce("2", "Ingénieur en Génie Civil", "BuildPro Solutions recrute un ingénieur en génie civil pour superviser des projets de construction. Le candidat idéal aura une solide expérience dans la conception et la gestion de projets de construction.", "BuildPro Solutions\n890 Avenue de la Construction, Bâtiville\nTél : +123456789\nEmail : recrutement@buildprosolutions.com");

            db.addAnnonce("2", "Consultant en Stratégie d'Entreprise", "Stratégie Plus Consulting cherche un consultant en stratégie d'entreprise pour conseiller ses clients sur les questions de croissance, d'efficacité opérationnelle et de gestion des risques.", "Stratégie Plus Consulting\n123 Rue de la Stratégie, Straticity\nTél : +246813579\nEmail : info@strategieplusconsulting.com");

            db.addAnnonce("2", "Chef de Projet Marketing", "MarketVision Group recherche un chef de projet marketing pour développer et mettre en œuvre des stratégies marketing efficaces. Le candidat retenu dirigera des équipes et supervisera des campagnes marketing.", "MarketVision Group\n456 Boulevard du Marketing, Marketville\nTél : +135792468\nEmail : recrutement@marketvisiongroup.com");

            db.applyTo("0", "0", "Voici ma Lettre de motivation");
            db.applyTo("0", "1", "Voici ma Lettre de motivation2");
            db.applyTo("0", "2", "Voici ma Lettre de motivation3");
            db.applyTo("0", "3", "");
            db.applyTo("0", "4", "");
            db.applyTo("1", "0", "");
            db.applyTo("1", "1", "");

            db.acceptCandidature("0");

            db.addNotif("2", "0", "Confirmation de l'envoi du CV", "Bonjour,\nVotre CV a bien été reçu. Nous vous remercions pour votre candidature.\n\nCordialement,\n.");
            db.addNotif("1", "0", "Nouvelle offre d'emploi", "Bonjour,\nNous avons une nouvelle offre d'emploi qui pourrait vous intéresser. Consultez-la dès maintenant sur notre plateforme.\n\nCordialement,\n");
            db.addNotif("2", "0", "Re: Pouvez vous m'envoyer votre CV ?", "Bonjour,\nJe vous ai envoyé mon CV en pièce jointe. Merci de l'avoir pris en compte.\n\nCordialement,\n");

        }

    }

    public void enregistrerLieuPreference(String username, String lieu) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Connexion à la base de données (à adapter selon votre configuration)
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/votre_base_de_donnees", "utilisateur", "mot_de_passe");

            // Requête SQL pour insérer les préférences de l'utilisateur
            String sql = "INSERT INTO preferences_utilisateur (username, lieu_preference) VALUES (?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, lieu);

            // Exécution de la requête
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Fermeture des ressources
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
