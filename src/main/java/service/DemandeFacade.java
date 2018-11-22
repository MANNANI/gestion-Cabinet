/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Demande;
import bean.Employe;
import bean.Medecin;
import bean.MedecinSecretaire;
import bean.Patient;
import bean.UserConnexion;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author PC
 */
@Stateless
public class DemandeFacade extends AbstractFacade<Demande> {

    @PersistenceContext(unitName = "com.mycompany_ProjetAgenda_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    @EJB
    private PatientFacade patientFacade;
    @EJB
    private MedecinFacade medecinFacade;
    @EJB
    private MedecinSecretaireFacade medecinSecretaireFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DemandeFacade() {
        super(Demande.class);
    }

    public Long nbrDemande() {

        List l = em.createQuery("SELECT count(d.id) FROM Demande d ").getResultList();
        System.out.println("haa l=> " + l.get(0));
        return (Long) l.get(0);
//            System.out.println("haa apres le cast => "+res);
//                 return  3;
    }

    public UserConnexion creerUserHachage(Employe employe, Patient patient, Demande demande) throws MessagingException {
        UserConnexion user = new UserConnexion();
        String str = util.HashageUtil.genererMdp();
        user.setPassword(str);

        if (employe != null) {
            user.setLogin(employe.getNom() + util.HashageUtil.genererMdp());
            user.setEmploye(employe);
        } else {
            user.setLogin(patient.getNom() + util.HashageUtil.genererMdp());
            user.setPatient(patient);
        }
        util.EmailUtil.sendMail("projetcabinet28@gmail.com", "test123456/", "voici votre le mdp" + user.getLogin() + "  ainsi que votre le login " + user.getPassword(), demande.getEmail(), "validation du compte");

        return user;
        //        employe.setUserConnexion(user);    
    }

    public void creerCompteHachage(Demande demande) throws IOException, MessagingException {
        File file = new File("C:\\Users\\PC\\Desktop\\Cabinet\\src\\main\\webapp\\resources\\img\\default-user-image.png");
        UserConnexion user = new UserConnexion();
        if (demande.getFonction().equals("Patient")) {
            Patient patient = new Patient();
            patient.setNom(demande.getNom());
            patient.setImg(Files.readAllBytes(file.toPath()));
            patient.setPrenom(demande.getPrenom());
            patient.setEmail(demande.getEmail());
            user = creerUserHachage(null, patient, demande);
            patient.setUserConnexion(user);
            patientFacade.create(patient);
        } else if (demande.getFonction().equals("Medecin")) {
            Medecin medecin = new Medecin();
            medecin.setEmail(demande.getEmail());
            medecin.setImg(Files.readAllBytes(file.toPath()));
            medecin.setNom(demande.getNom());
            medecin.setPrenom(demande.getPrenom());
            user = creerUserHachage(medecin, null, demande);
            medecin.setUserConnexion(user);
            medecinFacade.create(medecin);
        }else if(demande.getFonction().equals("MedecinSecretaire")){
             MedecinSecretaire medecin = new MedecinSecretaire();
             Medecin med=(Medecin) util.Session.getAttribut("UserEmploye");
           medecin.setMedecin(med);
            medecin.setEmail(demande.getEmail());
            medecin.setImg(Files.readAllBytes(file.toPath()));
            medecin.setNom(demande.getNom());
            medecin.setPrenom(demande.getPrenom());
            user = creerUserHachage(medecin, null, demande);
            medecin.setUserConnexion(user);
         medecinSecretaireFacade.create(medecin);
            remove(demande);
        }
    }

}
