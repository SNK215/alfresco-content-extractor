package fr.amexio.extractor.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import fr.amexio.extractor.utils.IHM;

import java.io.*;
import java.util.Properties;

/**
 * Class used to retrieve and store some parameters that are needed in order for the extraction to take place. Singleton design pattern is applied
 */
@Getter
@Setter
@Log4j2
public class Credentials {

    /**
     * Alfresco admin username
     */
    private String user;

    /**
     * Alfresco admin password
     */
    private String password;

    /**
     * service URL of an Alfresco repository
     */
    private String serviceUrl;

    /**
     * Directory where the files and folders will be imported
     */
    private String destinationDirectory;

    /**
     * Path of the directory that is going to be extracted
     */
    private String selectiveExtractorPath;

    private static Credentials instance;

    public Credentials() {

    }

    /**
     *  Data is recovered from extractor_application.properties and stored in an instance of the class
     */
    public void init() throws IOException {

        File file  = new File("extractor_application.properties");

        try (InputStream input = new FileInputStream(file.getAbsolutePath())) {

            Properties prop = new Properties();
            prop.load(input);

            serviceUrl = prop.getProperty("serviceUrl");
            destinationDirectory = prop.getProperty("destinationDirectory");
            selectiveExtractorPath = prop.getProperty("selectiveExtractorPath");

            log.info("Params retrieved from extractor_application.properties successfully");

        } catch (FileNotFoundException e) {
            log.error("File " + file.getName() + " not found");
            new IHM().endProgram();
        } catch (IOException e) {
            log.error("Error retrieving data, please verify and modify extractor_application.properties");
            new IHM().endProgram();

        }
    }

    /**
     * @return instance of the Credentials class
     */
    public static Credentials getInstance() {
        if (instance == null) {
            instance = new Credentials();
        }
        return instance;
    }
}
