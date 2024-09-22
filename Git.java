import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Git
{
    
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException{
        init();
        createBlob(new File("balls.txt"));
    }
    
    //initializes \\git, \\git\\objects, \\git\\index directories and files in the working directory
    public static void init() throws IOException{
        
        //initialize directory and file objects with the File class
        File gitDir = new File("git");
        File objectsDir = new File("git\\objects");
        File indexFile = new File("git\\index");

        //check if objects exist at specified path
        if(repoExists(gitDir, objectsDir, indexFile))
        {
            System.out.println("Git Repository already exists");
            return;
        }

        //if paths are empty, create directories and file
        if(!gitDir.exists()) {
            gitDir.mkdir();
        }
        if(!objectsDir.exists()) {
            objectsDir.mkdir();
        }
        if(!indexFile.exists()) {
            indexFile.createNewFile();
        }

        System.out.println("Created \\git, \\git\\objects,\\git\\index in working directory");
    }

    private static boolean repoExists(File git, File objects, File index) {
        return git.exists() && objects.exists() && index.exists();
    }


    public static String createBlob(File readFile) throws FileNotFoundException, NoSuchAlgorithmException {
        String filename = "";
        String filedata = fileString(readFile);
        MessageDigest md = MessageDigest.getInstance("SHA1");

        //read data off readFile into filedata using FileReader
        //hash filedata into filename as SHA-1

        return filename;
    }
    public static String fileString(File readFile) {
        String filedata = "";
        try{
            FileReader reader = new FileReader(readFile);
            int data;
            while((data = reader.read()) != -1) {
                filedata += (char) data;
            }
            System.out.println("Filedata: " + filedata);
            reader.close();

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return filedata;
    }
}