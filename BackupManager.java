/**
 * <p> Title: Backup Manager </p>
 * 
 * <p> Description: Manages the backup and restoration of help system articles </p>
 * 
 * <p> Copyright: Copyright (c) 2024 </p>
 * 
 * @author Department of Defense Software Division
 * 
 * @version 1.0    2024-10-19    Initial implementation
 */
package core;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Base64;
import core.databaseInterface;
import core.EncryptionHelper;
import core.EncryptionUtils;

/**
 * This class manages the backup and restoration of articles in the help system.
 * It provides functionality to securely backup articles to a file and restore them from a backup file.
 */
public class BackupManager {
    private databaseInterface dbMan;
    private EncryptionHelper encryptionHelper;

    /**
     * Constructs a new BackupManager with the given DatabaseManager.
     * 
     * @param dbMan The DatabaseManager instance to use for article operations
     * @throws Exception If there's an error initializing the EncryptionHelper
     */
    public BackupManager(databaseInterface dbMan) throws Exception {
        this.dbMan = dbMan;
        this.encryptionHelper = new EncryptionHelper();
    }

    /**
     * Backs up all articles to a specified file.
     * 
     * @param fileName The name of the file to save the backup to
     * @throws Exception If there's an error during the backup process
     */
    public void backupArticles(String fileName) throws Exception {
    	System.out.println("Initiating backup of articles...");
    	System.out.println("Retrieving articles from database");

        List<Article> articles = dbMan.getAllCompleteArticles();
    	System.out.println("Articles retrieved successfully");
    	System.out.println("Writing articles to backup file...");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Article article : articles) {
            	System.out.println("Encrypting article...");
                String encryptedArticle = encryptArticle(article);
            	System.out.println("Writing encrypted article to file...");
                writer.write(encryptedArticle);
            	System.out.println("Article written successfully");
                writer.newLine();
            }
        }
        System.out.println("Backup completed successfully.");
    }

    /**
     * Restores articles from a specified backup file.
     * 
     * @param fileName The name of the file to restore the backup from
     * @throws Exception If there's an error during the restoration process
     */
    public void restoreArticles(String fileName) throws Exception {
        List<Article> restoredArticles = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Article article = decryptArticle(line);
                restoredArticles.add(article);
            }
        }
        
        // Clear existing articles and add restored ones
        dbMan.clearAllArticles();
        for (Article article : restoredArticles) {
            dbMan.addArticle(article);
        }
        System.out.println("Restore completed successfully.");
    }

    /**
     * Encrypts an article for secure storage.
     * 
     * @param article The Article to encrypt
     * @return A String representing the encrypted article
     * @throws Exception If there's an error during encryption
     */
    private String encryptArticle(Article article) throws Exception {
        StringBuilder sb = new StringBuilder();
        System.out.println("ENCRYPTING ARTICLE");
        sb.append(encryptField(new String(article.getTitle()))).append("|");
        sb.append(encryptField(new String(article.getAuthors()))).append("|");
        sb.append(encryptField(new String(article.getAbstract()))).append("|");
        sb.append(encryptField(new String(article.getKeywords()))).append("|");
        sb.append(encryptField(new String(article.getBody()))).append("|");
        sb.append(encryptField(new String(article.getReferences())));
        return sb.toString();
    }

    /**
     * Decrypts an encrypted article string.
     * 
     * @param encryptedArticle The encrypted article string
     * @return The decrypted Article object
     * @throws Exception If there's an error during decryption
     */
    private Article decryptArticle(String encryptedArticle) throws Exception {
        String[] fields = encryptedArticle.split("\\|");
        return new Article(
            decryptField(fields[0]).toCharArray(),
            decryptField(fields[1]).toCharArray(),
            decryptField(fields[2]).toCharArray(),
            decryptField(fields[3]).toCharArray(),
            decryptField(fields[4]).toCharArray(),
            decryptField(fields[5]).toCharArray()
        );
    }

    /**
     * Encrypts a single field of an article.
     * 
     * @param field The field to encrypt
     * @return The encrypted field as a Base64 encoded string
     * @throws Exception If there's an error during encryption
     */
    private String encryptField(String field) throws Exception {
        byte[] iv = EncryptionUtils.getInitializationVector(field.toCharArray());
        byte[] encrypted = encryptionHelper.encrypt(field.getBytes(), iv);
        return Base64.getEncoder().encodeToString(encrypted) + ":" + Base64.getEncoder().encodeToString(iv);
    }
    
    private int getID(String encrypt) {
    	int ret = 0;
    	
    	return ret;
    }

    /**
     * Decrypts a single encrypted field.
     * 
     * @param encryptedField The encrypted field as a Base64 encoded string
     * @return The decrypted field as a String
     * @throws Exception If there's an error during decryption
     */
    private String decryptField(String encryptedField) throws Exception {
        String[] parts = encryptedField.split(":");
        byte[] encrypted = Base64.getDecoder().decode(parts[0]);
        byte[] iv = Base64.getDecoder().decode(parts[1]);
        byte[] decrypted = encryptionHelper.decrypt(encrypted, iv);
        return new String(decrypted);
    }
}