package kr.kro.mygaran.jail.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import kr.kro.mygaran.jail.Main;


public abstract class Database {
    Main plugin;
    Connection connection;

    public Database(Main instance){
        plugin = instance;
    }

    public Connection getSQLConnection() {
        return this.getSQLConnection(false);
    }
    public abstract Connection getSQLConnection(boolean reload);

    public abstract void load();

    public void initialize(String table){
        connection = getSQLConnection();
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE UUID = ?");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);

        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retrieve connection", ex);
        }
    }

    public Object getObject(String table, String name, String where, String query) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE " + where + " = ?");

            ps.setString(1, query);

            rs = ps.executeQuery();
            while(rs.next()){
                return rs.getObject(name);
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return null;
    }

    public boolean setObject(String table, String[] keyList, Object[] saveList) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            List<String> strings = new ArrayList<>();
            for (Object o : saveList) {
                strings.add("?");
            }
            ps = conn.prepareStatement("REPLACE INTO " + table + " (" + String.join(",", keyList) + ") VALUES(" + String.join(",", strings) + ")");

            for (int i = 1; i <= saveList.length; i++) {
                ps.setObject(i, saveList[i-1]);
            }

            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return false;
    }

    public boolean insertObject(String table, String[] keyList, Object[] saveList) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            List<String> strings = new ArrayList<>();
            for (Object o : saveList) {
                strings.add("?");
            }
            ps = conn.prepareStatement("INSERT INTO " + table + " (" + String.join(",", keyList) + ") VALUES(" + String.join(",", strings) + ")");

            for (int i = 1; i <= saveList.length; i++) {
                ps.setObject(i, saveList[i-1]);
            }

            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return false;
    }

    public boolean removeObject(String table, String key, String value) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("DELETE FROM " + table + " WHERE " + key + "=?");

            ps.setObject(1, value);

            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return false;
    }

    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Error.close(plugin, ex);
        }
    }
}