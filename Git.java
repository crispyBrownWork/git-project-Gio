import java.io.File;
import java.io.IOException;

public class Git
{
    
    public static void main(String[] args) throws IOException{
        init();
    }
    
    public static void init() throws IOException{
        boolean exists = false;
        //check if /git, /git/obects, /git/index exists -- this can be done using a helper method
        //if so print already exists

        //make both directories and file
        //first check that they dont already exist
        File gitDir = new File("\\git");
        File objectsDir = new File("\\git\\objects");
        File indexFile = new File("\\git\\index");

        if(!repoExists(gitDir, objectsDir, indexFile))
        {
            System.out.println("Git Repository already exists");
            return;
        }

        
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
}