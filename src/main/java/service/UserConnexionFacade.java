/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Employe;
import bean.Patient;
import bean.UserConnexion;
import controller.util.JsfUtil;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author houda
 */
@Stateless
public class UserConnexionFacade extends AbstractFacade<UserConnexion> {

    @PersistenceContext(unitName = "com.mycompany_ProjetAgenda_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    private static int i = 0;

    public UserConnexionFacade() {
        super(UserConnexion.class);
    }

    public String generatePassword() {
        i++;
        return "Password" + i;
    }

    //somaya
    public UserConnexion creer(String login) {
        if (!login.equals("")) {
            UserConnexion user = find(login);
            if (user != null) {
                return null;
            } else {
                user.setLogin(login);
                user.setBlocked(0);
                user.setMdpChanged(false);
                user.setNbrCnx(0);
                user.setPassword(generatePassword());
                create(user);
                return user;
            }
        } else {
            return null;
        }
    }

    //somaya
    public int ModifierMp2(UserConnexion userConnexion, String nvPasswors) {
        UserConnexion user = find(userConnexion.getLogin());
        if (user == null) {
            return -1;
        } else {
            user.setMdpChanged(false);
            edit(user);
            ModifierPassworde(nvPasswors, user);
            return 1;
        }
    }

    public UserConnexion connecter(UserConnexion user) {
        if (user == null || user.getLogin().equals("")) {
            return null;
        } else {
            UserConnexion laoder = find(user.getLogin());
            if (laoder == null || laoder.getBlocked() == 1) {
                return null;
            }
            if (!user.getPassword().equals(laoder.getPassword()) && laoder.getNbrCnx() < 3) {
                laoder.setNbrCnx(laoder.getNbrCnx() + 1);
                edit(laoder);
            }
            if (laoder.getNbrCnx() >= 3) {
                laoder.setBlocked(1);
                edit(laoder);
                return null;
            }
            if (laoder.getPassword().equals(user.getPassword())) {
                laoder.setNbrCnx(0);
                edit(laoder);
                user.setPassword(null);
                return laoder;
            }
        }
        return null;
    }

    //somaya
    public int ModifierPassworde(String nvPasswors, UserConnexion userConnexion) {
        if (nvPasswors.equals("") || userConnexion == null || userConnexion.isMdpChanged() == true) {
            return -1;
        } else {
            userConnexion.setPassword(util.HashageUtil.sha256(nvPasswors));
            userConnexion.setMdpChanged(true);
            edit(userConnexion);
            return 1;
        }
    }
    
    //somaya
    public int bloquerDebloquer(UserConnexion user) {
        if (user.getBlocked() == 0) {
            user.setBlocked(0);
            edit(user);
            return 0;
        } else {
            user.setBlocked(1);
            edit(user);
            return 1;
        }
    }

    //    public String findUserFonction(UserConnexion userConnexion) {
//        String requete;
//        String type = "";
//        if (userConnexion.getEmploye() != null) {
//            requete = "SELECT e.dtype FROM Employe e WHERE e.id=" + userConnexion.getEmploye().getId();
//            type = (String) getEntityManager().createNativeQuery(requete).getSingleResult();
//        } else if (userConnexion.getPatient() != null) {
//            return "Patient";
//        }
//        return type;
//    }
//    
    //    public List<Patient> findPatient(UserConnexion user) {
//        System.out.println("haa le user+" + user + "pat=" + user.getPatient());
//        if (user.getPatient() != null) {
//            return em.createQuery("select e FROM Patient e WHERE e.id='" + user.getPatient().getId() + "'").getResultList();
//        }
//        return null;
//    }
    //    public List<Employe> findEmploye(UserConnexion user) {
//        System.out.println("haa le user+" + user + "emp=" + user.getEmploye());
//        if (user.getEmploye() != null) {
//            return em.createQuery("select e FROM Employe e WHERE e.id='" + user.getEmploye().getId() + "'").getResultList();
//        }
//        return null;
//    }
    //    public int ModifierPassworde(String nvPasswors, UserConnexion userConnexion) {
//        if (nvPasswors.equals("") || userConnexion == null || userConnexion.isMdpChanged() == true) {
//            return -1;
//        } else {
//            userConnexion.setPassword(HashageUtil.sha256(nvPasswors));
//            userConnexion.setMdpChanged(true);
//            edit(userConnexion);
//            return 1;
//        }
//    }
//
    //    public int changePassword(String login, String oldPassword, String newPassword, String newPasswordConfirmation) {
//        System.out.println("voila hana dkhalt le service verifierPassword");
//        UserConnexion loadedeUser = find(login);
//
//        if (!newPasswordConfirmation.equals(newPassword)) {
//            return -1;
//        } else if (!loadedeUser.getPassword().equals(HashageUtil.sha256(oldPassword))) {
//            return -2;
//        } else if (oldPassword.equals(newPassword)) {
//            return -3;
//        }
//        loadedeUser.setPassword(HashageUtil.sha256(newPassword));
//        edit(loadedeUser);
//        return 1;
//    }
//    
}
