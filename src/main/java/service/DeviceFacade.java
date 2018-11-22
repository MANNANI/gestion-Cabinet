/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Device;
import bean.UserConnexion;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author houda
 */
@Stateless
public class DeviceFacade extends AbstractFacade<Device> {

    @PersistenceContext(unitName = "com.mycompany_ProjetAgenda_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DeviceFacade() {
        super(Device.class);
    }

    public Device findMac(Device device) {
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
//            System.out.println("Current IP address : " + ip.getHostAddress());
            device.setAdresseIP(ip.getHostAddress());
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            if (mac != null) {
//                System.out.print("Current MAC address : ");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mac.length; i++) {
                    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                }
                System.out.println(sb.toString());
                device.setAdresseMac(sb.toString());
                return device;

            }
        } catch (UnknownHostException e) {

            e.printStackTrace();

        } catch (SocketException e) {

            e.printStackTrace();

        }
        return device;
    }

    public List<Device> findByUser(UserConnexion userConnexion) {
        return em.createQuery("select d FROM Device d WHERE  d.userConnexion.login='" + userConnexion.getLogin() + "'").getResultList();
    }

    public Date findDateInscription(UserConnexion user) {
        if (user != null) {
            List<Date> dates = em.createQuery("SELECT MIN(d.dateEntree) FROM Device d WHERE d.userConnexion.login='" + user.getLogin() + "'").getResultList();
            return dates.get(0);
        }
        return null;
    }

    public void creerDevice(UserConnexion user, String type) {
        Device device = new Device();
        device.setDateEntree(new Date());
        device.setUserConnexion(user);
        user.getDevices().add(device);
        if (type.equals("Employe")) {
            device.setNom(user.getEmploye().getNom());
        } else {
            device.setNom(user.getPatient().getNom());
        }
        create(device);
        util.Session.setAttribut("Device", device);
    }
}
