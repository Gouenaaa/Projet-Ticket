/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;

import Entity.Etat;
import Entity.Ticket;
import Entity.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jbuffeteau
 */
public class FonctionsMetier 
{
    private PreparedStatement ps;
    private ResultSet rs;
    private Connection cnx;
    
    public  FonctionsMetier(){
        cnx = ConnexionBDD.getCnx();
    }
    
    public boolean loginExiste(String unLogin){
        boolean existe = false;
        try{
            ps = cnx.prepareStatement("SELECT idUser FROM users WHERE loginUser = '"+unLogin+"'");
            rs = ps.executeQuery();
            if(rs.next()){
                existe = true;
            }
           
        } catch (SQLException ex){
            Logger.getLogger(FonctionsMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return existe;
    }

    public boolean mdpValide(String unLogin, String unMdp){
        boolean valide = false;
        try{
            ps = cnx.prepareStatement("SELECT idUser FROM users WHERE pwdUser IN (\n" +
                                        "SELECT pwdUser FROM users WHERE loginUser = '"+unLogin+"')\n" +
                                        "AND pwdUser = '"+unMdp+"'");
            rs = ps.executeQuery();
            if(rs.next()){
                valide = true;
            }
           
        } catch (SQLException ex){
            Logger.getLogger(FonctionsMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return valide;
    }
    
    public boolean isAdmin(String unLogin){
        boolean admin = false;
        try{
            ps = cnx.prepareStatement("SELECT statutUser FROM users WHERE loginUser = '"+unLogin+"'");
            rs = ps.executeQuery();
            if(rs.next() && rs.getString(1).compareTo("admin") == 0){
                admin = true;
            }
           
        } catch (SQLException ex){
            Logger.getLogger(FonctionsMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return admin;
    }
    
    public ArrayList<Ticket> getTicketsByLogin(String unLogin){
        ArrayList<Ticket> mesTickets = new ArrayList<>();
        try{
            ps = cnx.prepareStatement("SELECT idTicket, nomTicket, dateTicket, etats.nomEtat FROM tickets, users, etats\n" +
                                        "WHERE etats.idEtat = tickets.numEtat\n" +
                                        "AND tickets.numUser = users.idUser\n" +
                                        "AND users.loginUser = '"+unLogin+"'");
            rs = ps.executeQuery();
            while(rs.next()){
                Ticket monTicket = new Ticket(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
                mesTickets.add(monTicket);
            }
            return mesTickets;
        } catch (SQLException ex){
            Logger.getLogger(FonctionsMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mesTickets;
    }
    
    public ArrayList<Ticket> getTicketsById(int unId){
        ArrayList<Ticket> mesTickets = new ArrayList<>();
        try{
            ps = cnx.prepareStatement("SELECT idTicket, nomTicket, dateTicket, etats.nomEtat FROM tickets, etats\n" +
                                        "WHERE etats.idEtat = tickets.numEtat\n" +
                                        "AND tickets.numUser = "+unId);
            rs = ps.executeQuery();
            while(rs.next()){
                Ticket monTicket = new Ticket(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
                mesTickets.add(monTicket);
            }
            return mesTickets;
        } catch (SQLException ex){
            Logger.getLogger(FonctionsMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mesTickets;
    }
    
    public ArrayList<Etat> getNomEtat(){
        ArrayList<Etat> mesEtats = new ArrayList<>();
        try{
            ps = cnx.prepareStatement("SELECT idEtat, nomEtat FROM etats");
            rs = ps.executeQuery();
            while(rs.next()){
                Etat monEtat = new Etat(rs.getInt(1), rs.getString(2));
                mesEtats.add(monEtat);
            }
            return mesEtats;
        } catch (SQLException ex){
            Logger.getLogger(FonctionsMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mesEtats;
    }
    
    public void updateEtat(int unId, String unEtat){
        try{
            ps = cnx.prepareStatement("UPDATE tickets SET numEtat = (SELECT idEtat FROM etats WHERE nomEtat = '"+unEtat+"') WHERE idTicket = "+unId);
            ps.executeUpdate();
        } catch (SQLException ex){
            Logger.getLogger(FonctionsMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ArrayList<User> getLesDevs(){
        ArrayList<User> mesUsers = new ArrayList<>();
        try{
            ps = cnx.prepareStatement("SELECT idUser, nomUser, prenomUser, statutUser FROM users");
            rs = ps.executeQuery();
            while(rs.next()){
                User monUser = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
                mesUsers.add(monUser);
            }
            return mesUsers;
        } catch (SQLException ex){
            Logger.getLogger(FonctionsMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mesUsers;
    }
    
    public int getNumTicket(){
        int num = 0;
        try{
            ps = cnx.prepareStatement("SELECT MAX(idTicket) FROM tickets");
            rs = ps.executeQuery();
            if(rs.next()){
                num = rs.getInt(1)+1;
            }
            return num;
        } catch (SQLException ex){
            Logger.getLogger(FonctionsMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return num;
    }
    
    public int getNumEtatByName(String unNom){
        int num = 0;
        try{
            ps = cnx.prepareStatement("SELECT idEtat FROM etats WHERE nomEtat = '"+unNom+"'");
            rs = ps.executeQuery();
            if(rs.next()){
                num = rs.getInt(1);
            }
            return num;
        } catch (SQLException ex){
            Logger.getLogger(FonctionsMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return num;
    }
    
    public void addTicket(String unNom, String uneDate, int unIdUser, int unIdEtat){
        try{
            ps = cnx.prepareStatement("INSERT INTO tickets (nomTicket, dateTicket, numUser, numEtat)\n" +
                                        "VALUES ('"+unNom+"', '"+uneDate+"', "+unIdUser+", "+unIdEtat+")");
            ps.executeUpdate();
        } catch (SQLException ex){
            Logger.getLogger(FonctionsMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
