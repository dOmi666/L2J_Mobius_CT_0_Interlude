package org.l2jmobius.commons.database;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.l2jmobius.Config;
import org.l2jmobius.commons.enums.ServerMode;

/**
 * @author Mobius
 */
public class DatabaseBackup
{
    public static void performBackup()
    {
        // Delete old files.
        if (Config.BACKUP_DAYS > 0)
        {
            final long cut = LocalDateTime.now().minusDays(Config.BACKUP_DAYS).toEpochSecond(ZoneOffset.UTC);
            final Path path = Paths.get(Config.BACKUP_PATH);
            try
            {
                Files.list(path).filter(n ->
                {
                    try
                    {
                        return Files.getLastModifiedTime(n).to(TimeUnit.SECONDS) < cut;
                    }
                    catch (Exception ex)
                    {
                        return false;
                    }
                }).forEach(n ->
                {
                    try
                    {
                        Files.delete(n);
                    }
                    catch (Exception ex)
                    {
                        // Ignore.
                    }
                });
            }
            catch (Exception e)
            {
                // Ignore.
            }
        }
        
        // Dump to file.
        final String mysqldumpPath = System.getProperty("os.name").toLowerCase().contains("win") ? Config.MYSQL_BIN_PATH : "";
        
        // DATABASE_URL içerisinden Host, Port ve DB adını ayıkla
        String dbHost = "127.0.0.1";
        String dbPort = "3306";
        String dbName = "";

        try
        {
            String cleanUrl = Config.DATABASE_URL.replace("jdbc:mariadb://", "").replace("jdbc:mysql://", "");
            if (cleanUrl.contains("/"))
            {
                String[] hostPortAndDb = cleanUrl.split("/");
                String[] hostAndPort = hostPortAndDb[0].split(":");
                dbHost = hostAndPort[0];
                if (hostAndPort.length > 1)
                {
                    dbPort = hostAndPort[1];
                }
                dbName = hostPortAndDb[1].replaceAll("\\?.*", "");
            }
        }
        catch (Exception e)
        {
            dbName = Config.DATABASE_URL.replace("jdbc:mariadb://", "").replaceAll(".*\\/|\\?.*", "");
        }

        try
        {
            // --skip-ssl, -h ve -P parametreleri eklendi
            final String command = mysqldumpPath + "mysqldump --skip-ssl -h " + dbHost + " -P " + dbPort + " -u " + Config.DATABASE_LOGIN + 
                (Config.DATABASE_PASSWORD.trim().isEmpty() ? "" : " -p" + Config.DATABASE_PASSWORD) + 
                " " + dbName + " -r " + Config.BACKUP_PATH + (Config.SERVER_MODE == ServerMode.GAME ? "game" : "login") + 
                new SimpleDateFormat("_yyyy_MM_dd_HH_mm'.sql'").format(new Date());

            final Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}