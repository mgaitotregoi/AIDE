package com.company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class Util {

     public static void print(List<String> res){
        for (String r: res) {
            System.out.println(r);
        }
    }

    public static void acidTrans(Connection con, PreparedStatement p1, PreparedStatement p2) throws SQLException {
        con.setAutoCommit(false);

        p1.executeUpdate();
        p2.executeUpdate();

        try{
            con.commit();
        }catch (SQLException e){
            System.out.println("Commit fails");
            if(con != null){
                try {
                    con.rollback();
                }catch (SQLException ex){
                    System.out.println("Rollback fails");
                }
            }
        }finally {
            try {
                con.setAutoCommit(false);
            }catch (SQLException c){
                System.out.println("Autocommit fails");
            }
        }

        p1.close();
        p2.close();
    }
}