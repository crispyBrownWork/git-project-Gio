import java.io.File;
import java.io.IOException;

public class Git
{
    
    public static void main(String[] args) throws IOException{
        init();
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


    public static String blobString(String filePath){
        String filename = "";
        return filename;
    }
}