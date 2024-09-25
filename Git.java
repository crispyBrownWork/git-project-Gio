import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;
import java.math.BigInteger;

public class Git
{
    
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException{
        
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
    }

    private static boolean repoExists(File git, File objects, File index) {
        return git.exists() && objects.exists() && index.exists();
    }

    public static void createBlob(File readFile, boolean doCompress) throws NoSuchAlgorithmException {
        //read data off readFile into filedata using FileReader
        //hash filedata into filename as SHA-1
        String filedata = fileString(readFile);
        String filename = hashedString(filedata);

        //make a copy of the file into the objects folder with filename as the name
        File blob = new File("git\\objects\\" + filename);
        if(!blob.exists()) {
            try {
                blob.createNewFile();
                FileWriter objectsWriter = new FileWriter("git\\objects\\" + filename);
                if(doCompress) {
                    objectsWriter.write(compressedHash(filedata));
                } else {
                    objectsWriter.write(filedata);
                }
                objectsWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //insert a new line entry into the index file using FileWriter
        try {
            FileWriter indexWriter = new FileWriter("git\\index", true);
            indexWriter.append("\n" + filename + " " + readFile.getName());
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //read data off readFile into filedata using FileReader
    public static String fileString(File readFile) {
        String filedata = "";
        try {
            FileReader reader = new FileReader(readFile);
            int data;
            while((data = reader.read()) != -1) {
                filedata += (char) data;
            }
            reader.close();

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return filedata;
    }

    //hash filedata into filename as SHA-1
    public static String hashedString(String filedata) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        byte[] digest = md.digest(filedata.getBytes());
        BigInteger bigInt = new BigInteger(1, digest);
        return bigInt.toString(16);
    }

    //compress inputString using GZIPOutputStream and return as a String
    private static String compressedHash(String inputString) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream zip = new GZIPOutputStream(out);
        zip.write(inputString.getBytes());
        zip.close();
        return out.toString();
    }
}