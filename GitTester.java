import java.io.*;
import java.security.NoSuchAlgorithmException;

public class GitTester {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        // create test files and store them in a file array
        File firstFile = new File("workingDirectory/firstFile.txt");
        try (FileWriter firstWriter = new FileWriter(firstFile)) {
            firstWriter.write("This is the data!");
        }
        firstFile.createNewFile();

        File secondFile = new File("workingDirectory/secondFile.txt");
        try (FileWriter secondWriter = new FileWriter(secondFile)) {
            secondWriter.write("This is the second data!");
        }
        secondFile.createNewFile();
        File[] files = { firstFile, secondFile };

        // test init() method

        initTest();

        // blobTester(firstFile, false);
        // treeTester();

        commitTest();

        // reset test files and directories

        // resetTestFiles(files);
        // resetDirectories();
    }


    public static void commitTest() throws IOException {
        Git testGit = new Git();
        File addedFile = new File("workingDirectory/addedFile.txt");
        
        addedFile.delete();

        testGit.stage("workingDirectory");
        testGit.commit("Gio", "This is the first commit");

        addedFile.createNewFile();
        try (FileWriter addedWriter = new FileWriter(addedFile)) {
            addedWriter.write("This is the added data!");
        }

        testGit.stage("workingDirectory/addedFile.txt");
        testGit.commit("Gio", "This is the second commit");
    }
    public static void initTest() throws IOException {
        Git.init();
        File gitDir = new File("git");
        File objectsDir = new File("git" + File.separator + "objects");
        File indexFile = new File("git" + File.separator + "index");
        File headFile = new File("git" + File.separator + "HEAD");
        if (gitDir.exists()) {
            System.out.println("Created \\git in working directory");
        }
        if (objectsDir.exists()) {
            System.out.println("Created \\git\\objects in working directory");
        }
        if (indexFile.exists()) {
            System.out.println("Created \\git\\index in working directory");
        }
        if (headFile.exists()) {
            System.out.println("Created \\git\\HEAD in working directory");
        }
    }

    public static void blobTester(File file, boolean doCompress) throws IOException, NoSuchAlgorithmException {
        Git.createBlob(file, doCompress);

        File objectsDir = new File("git" + File.separator + "objects");
        File indexFile = new File("git" + File.separator + "index");

        File[] objectChildren = objectsDir.listFiles();
        String indexString = Git.fileString(indexFile);

        String expectedHash = Git.hashedString(Git.fileString(file));

        boolean foundInObjects = false;
        for (File child : objectChildren) {
            if (child.getName().equals(expectedHash)) {
                System.out.println(file.getName() + " exists in the git\\objects directory.");
                foundInObjects = true;
                break;
            }
        }
        if (!foundInObjects) {
            System.out.println(file.getName() + " does not exist in the git\\objects directory.");
        }

        if (indexString.contains(expectedHash)) {
            System.out.println(file.getName() + " exists in the git\\index file.");
        } else {
            System.out.println(file.getName() + " does not exist in the git\\index file.");
        }
    }

    public static void treeTester() throws NoSuchAlgorithmException {
        Git.createTree("workingDirectory/testDirectory");
        System.out.println("Tree created successfully.");
    }

    public static void resetDirectories() throws IOException {
        File objectsDir = new File("git" + File.separator + "objects");
        File indexFile = new File("git" + File.separator + "index");

        for (File child : objectsDir.listFiles()) {
            if (child != null) {
                child.delete();
            }
        }

        try (FileWriter indexWriter = new FileWriter(indexFile)) {
            indexWriter.write("");
        }

        System.out.println("Reset directories successfully");
    }

    public static void resetTestFiles(File[] files) {
        for (File f : files) {
            if (f.exists()) {
                f.delete();
            }
        }

        System.out.println("Reset test files successfully");
    }
}
