import java.io.*;
import java.security.NoSuchAlgorithmException;

public class GitTester {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        //create test files and store them in a file array
        File firstFile = new File("firstFile.txt");
        FileWriter firstWriter = new FileWriter(firstFile);
        firstWriter.write("This is the data!");
        firstWriter.close();
        firstFile.createNewFile();

        File secondFile = new File("secondFile.txt");
        FileWriter secondWriter = new FileWriter(secondFile);
        secondWriter.write("This is the second data!");
        secondWriter.close();
        secondFile.createNewFile();
        File[] files = {firstFile, secondFile};

        //space for testing methods
        resetTestFiles(files);
    }

    //checks if directorties and files are created during init()
    public static void initTest() throws IOException {
        Git.init();
        File gitDir = new File("git");
        File objectsDir = new File("git\\objects");
        File indexFile = new File("git\\index");
        if(objectsDir.exists()) {
            System.out.println("Created \\git\\objects in working directory");
        }
        if(indexFile.exists()) {
            System.out.println("Created \\git\\index in working directory");
        }
        if(gitDir.exists()) {
            System.out.println("Created \\git in working directory");
        }

    //deletes all directories and files from init()
    }
    public static void deleteInit() {
        File gitDir = new File("git");
        File objectsDir = new File("git\\objects");
        File indexFile = new File("git\\index");

        if(objectsDir.exists()) {
            objectsDir.delete();
            System.out.println("Deleted \\git\\objects in working directory");
        }
        if(indexFile.exists()) {
            indexFile.delete();
            System.out.println("Deleted \\git\\index in working directory");
        }
        if(gitDir.exists()) {
            gitDir.delete();
            System.out.println("Deleted \\git in working directory");
        }

    }

    //executes createBlob() on a given file and checks if the blob is saved correctly inside git\\objects and git\\index
    public static void blobTester(File file, boolean doCompress) throws IOException, NoSuchAlgorithmException {
        File objectsDir = new File("git\\objects");
        File indexFile = new File("git\\index");
        File[] objectChildren = objectsDir.listFiles();
        String indexString = Git.fileString(indexFile);

        //run createBlob on given file
        Git.createBlob(file, doCompress);

        //check if file exists in object directory
        for(File child : objectChildren) {
            if (child.getName().equals(Git.hashedString(Git.fileString(file)))) {
                System.out.println(file.getName() + " exists int the git\\objects directory.");
            }

        //check if file is logged in index file
        }
        if(indexString.contains(Git.hashedString(Git.fileString(file)))) {
            System.out.println(file.getName() + " exists in the git\\index file.");
        } else {
            System.out.println(file.getName() + " doesn't exist in the git\\index file.");
        }

    //empties git\\objects, git\\index
    }
    public static void resetDirectories() throws IOException {
        File objectsDir = new File("git\\objects");
        File indexFile = new File("git\\index");
        FileWriter indexWriter = new FileWriter(indexFile);

        for(File child: objectsDir.listFiles()) {
            child.delete();
        }

        indexWriter.write("");
        indexWriter.close();
    }

    //deletes all test files
    public static void resetTestFiles(File[] files) {
        for(File f: files) {
            if(f.exists()) {
                f.delete();
            }
        }
    }

}
