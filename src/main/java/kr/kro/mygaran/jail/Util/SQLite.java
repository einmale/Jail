package kr.kro.mygaran.jail.Util;

import kr.kro.mygaran.jail.Classes.Jail;
import kr.kro.mygaran.jail.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;


public class SQLite extends Database{
    String dbname;
    public SQLite(Main instance){
        super(instance);
        dbname = plugin.getConfig().getString("SQLite.Filename", "jail");
    }

    public String SQLiteCreateJailPlayerTable = "CREATE TABLE IF NOT EXISTS jail_players (" +
            "`UUID` varchar(32) NOT NULL," +
            "`TIME` integer(11) NOT NULL," +
            "`REASON` varchar(100) NOT NULL," +
            "`MANAGER` varchar(32) NOT NULL," +
            "`JAIL` varchar(32) NOT NULL," +
            "`SPAWN_POSITION` varchar(100) NOT NULL," +
            "PRIMARY KEY (`UUID`)" +
            ");";

    public String SQLiteCreateJailTable = "CREATE TABLE IF NOT EXISTS jails (" +
            "`ID` varchar(32) NOT NULL," +
            "`WORLD` varchar(32) NOT NULL," +
            "`POSITION` varchar(100) NOT NULL," +
            "PRIMARY KEY (`ID`)" +
            ");";

    public String SQLiteCreateWaitTable = "CREATE TABLE IF NOT EXISTS wait_locations (" +
            "`UUID` varchar(32) NOT NULL," +
            "`POSITION` varchar(100) NOT NULL," +
            "PRIMARY KEY (`UUID`)" +
            ");";


    public String SQLiteCreateSendLogTable = "CREATE TABLE IF NOT EXISTS send_logs (" +
            "`ID` INTEGER PRIMARY KEY AUTOINCREMENT," +
            "`UUID` varchar(32) NOT NULL," +
            "`TIME` integer(20) NOT NULL," +
            "`REASON` varchar(100) NOT NULL," +
            "`MANAGER` varchar(32) NOT NULL," +
            "`JAIL` varchar(100) NOT NULL," +
            "`TIMESTAMP` text(30) NOT NULL" +
            ");";

    public String SQLiteCreateReleaseLogTable = "CREATE TABLE IF NOT EXISTS release_logs (" +
            "`ID` INTEGER PRIMARY KEY AUTOINCREMENT," +
            "`UUID` varchar(32) NOT NULL," +
            "`MANAGER` varchar(32) NOT NULL," +
            "`TIMESTAMP` text(30) NOT NULL" +
            ");";

    public String SQLiteCreateJailLogTable = "CREATE TABLE IF NOT EXISTS jail_logs (" +
            "`ID` INTEGER PRIMARY KEY AUTOINCREMENT," +
            "`UUID` varchar(32) NOT NULL," +
            "`JAIL` varchar(100) NOT NULL," +
            "`TIMESTAMP` text(30) NOT NULL" +
            ");";
    public String SQLiteCreateRemoveLogTable = "CREATE TABLE IF NOT EXISTS remove_logs (" +
            "`ID` INTEGER PRIMARY KEY AUTOINCREMENT," +
            "`UUID` varchar(32) NOT NULL," +
            "`JAIL` varchar(100) NOT NULL," +
            "`TIMESTAMP` text(30) NOT NULL" +
            ");";



    public Connection getSQLConnection(boolean reload) {
        File dataFolder = new File(plugin.getDataFolder() + "/");

        if (!(dataFolder.exists())) {
            dataFolder.mkdirs();
        }

        File dataFile = new File(dataFolder, dbname+".db");
        if (!dataFile.exists()){
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+dbname+".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()&&(!reload)){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFile);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JDBC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    public int createJail(Player player, String id) {
        String[] jails_keys = {"ID", "WORLD", "POSITION"};
        String[] jails_saves = {id, player.getWorld().getName(), Location3.convert(player.getLocation())};

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp ts = Timestamp.from(ZonedDateTime.now().toInstant());

        String[] logs_keys = {"UUID", "JAIL", "TIMESTAMP"};
        String[] logs_saves = {player.getUniqueId().toString(), id, sdf.format(ts)};

        Object find = this.getObject("jails", "ID", "ID", id);

        if (find != null) {
            return -1;
        }

        boolean success1 = this.setObject("jails", jails_keys, jails_saves);
        boolean success2 = this.insertObject("jail_logs", logs_keys, logs_saves);

        if (!success1 && !success2) {
            return 0;
        }

        this.plugin.reloadJails();
        return 1;
    }

    public int removeJail(String name, String managerUUID) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp ts = Timestamp.from(ZonedDateTime.now().toInstant());

        String[] logs_keys = {"UUID", "TIMESTAMP", "JAIL"};
        Object[] logs_saves = {managerUUID, sdf.format(ts), managerUUID};

        Object find = this.getObject("jails", "ID", "ID", name);

        if (find == null) {
            return -1;
        }

        boolean success1 = this.removeObject("jails", "ID", name);
        boolean success2 = this.insertObject("remove_logs", logs_keys, logs_saves);

        if (!success1 && !success2) {
            return 0;
        }

        this.plugin.reloadJails();
        return 1;
    }

    public int sendPrisoner(String prisonerUUID, String managerUUID, Jail jail, long time, String reason) {
        Player prisoner = Bukkit.getPlayer(UUID.fromString(prisonerUUID));

        if (prisoner == null) {
            return 0;
        }

        String[] send_keys = {"UUID", "TIME", "REASON", "MANAGER", "JAIL", "SPAWN_POSITION"};
        Object[] send_saves = {
                prisonerUUID,
                time, reason, managerUUID,
                jail.getId(),
                Location3.convert(prisoner.getBedSpawnLocation() == null ? Bukkit.getWorld("world").getSpawnLocation() : prisoner.getBedSpawnLocation())
        };

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp ts = Timestamp.from(ZonedDateTime.now().toInstant());

        String[] logs_keys = {"UUID", "TIME", "TIMESTAMP", "REASON", "MANAGER", "JAIL"};
        Object[] logs_saves = {prisonerUUID, time, sdf.format(ts), reason, managerUUID, jail.getId()};

        Object find = this.getObject("jail_players", "UUID", "UUID", prisonerUUID);

        if (find != null) {
            return -1;
        }

        boolean success1 = this.setObject("jail_players", send_keys, send_saves);
        boolean success2 = this.insertObject("send_logs", logs_keys, logs_saves);

        if (!success1 && !success2) {
            return 0;
        }

        return 1;
    }

    public int releasePrisoner(Player prisoner, Player manager) {
        return this.releasePrisoner(prisoner.getUniqueId().toString(), manager.getUniqueId().toString());
    }
    public int releasePrisoner(String prisoner, String manager) {
        String prisonerUUID = prisoner;
        String managerUUID = manager;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp ts = Timestamp.from(ZonedDateTime.now().toInstant());

        String[] logs_keys = {"UUID", "TIMESTAMP", "MANAGER"};
        Object[] logs_saves = {prisonerUUID, sdf.format(ts), managerUUID};

        Object find = this.getObject("jail_players", "UUID", "UUID", prisonerUUID);

        if (find == null) {
            return -1;
        }

        boolean success1 = this.removeObject("jail_players", "UUID", prisonerUUID);
        boolean success2 = this.insertObject("release_logs", logs_keys, logs_saves);

        if (!success1 && !success2) {
            return 0;
        }

        return 1;
    }

    public HashMap<String, Long> getAllPrisoners(String table) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + ";");

            rs = ps.executeQuery();

            HashMap<String, Long> prisoners = new HashMap<>();

            while (rs.next()) {
                String uuid = rs.getString("UUID");
                long time = rs.getLong("TIME");

                prisoners.put(uuid, time);
            }

            return prisoners;
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

    public List<Jail> getAllJails(String table) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + ";");

            rs = ps.executeQuery();

            List<Jail> jails = new ArrayList<>();

            while (rs.next()) {
                String id = rs.getString("ID");
                String worldName = rs.getString("WORLD");
                String location = rs.getString("POSITION");
                String[] splitLocation = location.split(",");

                World jailWorld = Bukkit.getWorld(worldName);
                if (jailWorld == null) {
                    jailWorld = Bukkit.getWorld("world");
                }

                Location newLocation = Location3.convert(jailWorld, splitLocation);

                jails.add(new Jail(
                        id,
                        jailWorld,
                        newLocation
                ));
            }

            return jails;
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

    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(SQLiteCreateJailPlayerTable);
            s.executeUpdate(SQLiteCreateJailTable);
            s.executeUpdate(SQLiteCreateWaitTable);
            s.executeUpdate(SQLiteCreateSendLogTable);
            s.executeUpdate(SQLiteCreateReleaseLogTable);
            s.executeUpdate(SQLiteCreateJailLogTable);
            s.executeUpdate(SQLiteCreateRemoveLogTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize("jail_players");
    }
}